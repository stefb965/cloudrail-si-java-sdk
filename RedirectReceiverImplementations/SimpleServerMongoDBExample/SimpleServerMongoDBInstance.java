import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.interfaces.Profile;
import com.cloudrail.si.servicecode.commands.awaitCodeRedirect.RedirectReceiver;
import com.cloudrail.si.services.Facebook;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class SimpleServerMongoDBInstance {
	private static final String CLIENT_ID = "[clientID]";
	private static final String CLIENT_SECRET = "[clientSecret]";
	
	private int port;
	private String dbHost;
	private int dbPort;
	private String dbName;
	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Document> usersCollection;
	private MongoCollection<Document> loginSessionsCollection;

	private HttpServer server;
	
	public SimpleServerMongoDBInstance(int port, String dbHost, int dbPort, String dbName) {
		this.port = port;
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.dbName = dbName;
	}
	
	public void start() {
		try {
			connectDB();
			startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up a connection to the MongoDB
	 */
	private void connectDB() {
		mongoClient = new MongoClient(dbHost, dbPort);
		db = mongoClient.getDatabase(dbName);
		usersCollection = db.getCollection("users");
		loginSessionsCollection = db.getCollection("logins");
	}

	/**
	 * Launches the server instance and registers two handlers, one to display user information and one to digest authorization redirects.
	 */
	private void startServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress(port), 0);
		
		server.createContext("/start", new HttpHandler() {
			
			public void handle(HttpExchange exchange) throws IOException {
				
				// Generate a unique identifier to identify this login session
				final ObjectId loginId = new ObjectId();
				
				// Set up the profile object, provide a custom RedirectReceiver, the credentials, the other server as a redirect and the generated ID as state
				Profile profile = new Facebook(new RedirectReceiver() {
					
					public String openAndAwait(String url, String currentState) {
						
						// Save state to the database, identified by the generated ID
						loginSessionsCollection.insertOne(new Document("authState", currentState).append("loginId", loginId));
						
						// Throw an Exception to interrupt execution, pass the URL to be redirected to on to the Exception
						throw new LoginRequiredException(url);
					}
					
				}, CLIENT_ID, CLIENT_SECRET, "http://localhost:12346/auth", loginId.toHexString());
				
				// Check if the incoming request's query contains a userId
				String query = exchange.getRequestURI().getQuery();
				String userId = query != null ? parseQueryString(query).get("userId") : null;
				
				// If so, we've already logged in the user and can restore information from the DB
				if (userId != null) {
					Document user = usersCollection.find(new Document("userId", userId)).first();
					String persistentState = user.getString("persistentState");
					try {
						profile.loadAsString(persistentState);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				// Commence login, an exception is expected if the persistent state was not loaded or is deprecated
				try {
					profile.login(); // This is where an exception might be thrown
					
					// This only happens when the user is authenticated
					// Respond with code 200 and a string
					String answer = "Your name is " + profile.getFullName();
					exchange.sendResponseHeaders(200, answer.length());
					OutputStream os = exchange.getResponseBody();
					os.write(answer.getBytes());
					os.close();
				} catch (RuntimeException e) { // This is where we catch the LoginRequiredException from the RedirectReceiver's openAndAwait method
					if (e.getCause() instanceof LoginRequiredException) {
						LoginRequiredException lre = (LoginRequiredException) e.getCause();
						
						// Extract the URL from the exception and redirect to it to start the login process
						exchange.getResponseHeaders().add("Location", lre.getUrl());
						exchange.sendResponseHeaders(302, 0);
					} else {
						throw e;
					}
				}
			}
			
		});
		
		server.createContext("/auth", new HttpHandler() {
			
			public void handle(final HttpExchange exchange) throws IOException {
				
				// Extract the state parameter which is the generated ID
				String idFromState = parseQueryString(exchange.getRequestURI().getQuery()).get("state");
				
				// Set up the profile here as well, only this time the RedirectReceiver's openAndAwait method returns the incoming request URL directly which allows authentication to finish
				Profile profile = new Facebook(new RedirectReceiver() {
					
					public String openAndAwait(String url, String currentState) {
						return "http://localhost:12346" + exchange.getRequestURI().toString();
					}
					
				}, CLIENT_ID, CLIENT_SECRET, "http://localhost:12346/auth", "unimportant");
				
				// Use the ID from the query to find the loginSession and thus the state we have saved before
				Document session = loginSessionsCollection.find(new Document("loginId", new ObjectId(idFromState))).first();
				if (session == null) throw new RuntimeException("ID in redirect state not recognized or used twice");
				String authState = session.getString("authState");
				loginSessionsCollection.deleteOne(session); // Clean up behind ourselves
				
				// Load the state and resume login
				try {
					profile.resumeLogin(authState);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				// Since the user is now authenticated we can get their unique identifier
				String userId = profile.getIdentifier();
				
				// Check if we have seen this user before
				Document user = usersCollection.find(new Document("userId", userId)).first();
				
				if (user == null) { // If not, create a new entry for them
					user = new Document("userId", userId);
					user.append("persistentState", profile.saveAsString());
					usersCollection.insertOne(user);
				} else { // If yes, update their authentication information
					usersCollection.updateOne(eq("userId", userId), set("persistentState", profile.saveAsString()));
				}
				
				// Redirect back to the start page, now with a user ID in the query
				exchange.getResponseHeaders().add("Location", "http://localhost:12345/start?userId=" + user.get("userId"));
				exchange.sendResponseHeaders(302, 0);
			}
			
		});
		
		server.setExecutor(null);
		server.start();
	}
	
	/**
	 * Takes a simple query string and parses it into a Map
	 * @param queryString
	 * @return
	 */
	private Map<String, String> parseQueryString(String queryString) {
		TreeMap<String, String> queryMap = new TreeMap<String, String>();
		
		String[] queryPairs = queryString.split("&");
	    for (String pair : queryPairs) {
	        int idx = pair.indexOf("=");
	        try {
				queryMap.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    }
	    
	    return queryMap;
	}
	
	/**
	 * Custom exception to interrupt a service instance when the user must login
	 */
	private class LoginRequiredException extends RuntimeException {
		private static final long serialVersionUID = 1322249324859123469L;
		private String url;
		
		public LoginRequiredException(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return this.url;
		}
	}
}