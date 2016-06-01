

/**
 * Starts a local server with connection to a local MongoDB.
 * Leverages interpreter state saving and restoring such that multiple instances could be run.
 * Launch and visit http://localhost:12345/start in your local browser to test.
 */
public class SimpleServerMongoDBSample {
	public static void main(String[] args) {
		SimpleServerMongoDBInstance inst0 = new SimpleServerMongoDBInstance(12345, "localhost", 27017, "CloudRailTest");
		SimpleServerMongoDBInstance inst1 = new SimpleServerMongoDBInstance(12346, "localhost", 27017, "CloudRailTest");
		inst0.start();
		inst1.start();
	}
}
