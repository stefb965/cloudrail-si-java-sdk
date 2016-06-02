This project demonstrates how the CloudRail SI SDK can be made compatible to work with a server application that runs on mutliple stateless instances.

To try it out, put your Facebook client ID and secret, include the dependency to the MongoDB Java driver

	<dependency>
		<groupId>org.mongodb</groupId>
		<artifactId>mongodb-driver</artifactId>
		<version>3.2.2</version>
	</dependency>

and launch the application. It will create two servers listening on different local ports. Using e.g. your browser, you can access the first one and see how authentication works, even if your user is redirected to the second instance (not the one that started and is naturally aware of the authentication process). The code is well commented.