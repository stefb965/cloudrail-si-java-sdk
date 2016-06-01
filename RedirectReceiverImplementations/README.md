This folder contains a number of example implementations of the RedirectReceiver interface, the most involved part of integrating CloudRail SI for Java.
An implementation of this interface must be provided to every service that uses OAuth1 or OAuth2 for authentication.
For others it can be left null.
The implementations are just for demonstration and inspiration purposes and should not be used in production environments.

* Those who just want to try it out should have a look at ConsoleRedirectReceiver
* Those who want to build a Desktop application should have a look at LocalServerRedirectReceiver for inspiration
* Those who want to build a Server application should have a look at SimpleServerMongoDBExample which demonstrates how the CloudRail SI SDK can be made compatible to work with a server application that runs on mutliple stateless instances