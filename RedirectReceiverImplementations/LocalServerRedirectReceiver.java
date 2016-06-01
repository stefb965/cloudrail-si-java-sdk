import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import com.cloudrail.si.servicecode.commands.awaitCodeRedirect.RedirectReceiver;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * RedirectReceiver implementation that uses a local server and the local browser for authentication.
 * Use like CloudStorage cs = new Dropbox(new LocalServerRedirectReceiver(), "[clientID]", "[clientSecret]", "http://localhost:12345/auth", "someState");
 * If you are using Eclipse it might complain about an access restriction, this can be turned off: http://stackoverflow.com/questions/9266632/access-restriction-is-not-accessible-due-to-restriction-on-required-library
 */
public class LocalServerRedirectReceiver implements RedirectReceiver {
	public String openAndAwait(String url, String currentState) {
		final AtomicReference<String> ar = new AtomicReference<String>();
		try {
			
			// Create local server on port 12345 listening for path /auth
			HttpServer server = HttpServer.create(new InetSocketAddress(12345), 0);
			server.createContext("/auth", new HttpHandler() {
				public void handle(HttpExchange exchange) throws IOException {
					String uri = "http://localhost:12345" + exchange.getRequestURI().toString();
					
					String answer = "<h1>Can be closed now</h1>";
					exchange.sendResponseHeaders(200, answer.length());
					OutputStream os = exchange.getResponseBody();
					os.write(answer.getBytes());
					os.close();
					synchronized(ar)  {
						ar.set(uri);
						ar.notify();
					}
				}
			});
			server.setExecutor(null);
			server.start();
			
			// Open URI
		    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        try {
		            desktop.browse(new URI(url));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		    
			// Block (unblocked by server callback)
			synchronized(ar) {
				while(ar.get() == null)
					ar.wait();
			}
			
			server.stop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ar.get();
	}
}
