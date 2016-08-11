# JavaFX RedirectReceiver Implementation

If you are developing an application based on JavaFX you can use the provided RedirectReceiver implementation. The JavaFXReceiver basically opens a new window containing a WebView where the user needs to login to the requested service. If the user closes the window before finishing the authentication process an AuthenticationException is thrown, indicating that the authentication was cancelled. Here is a sample that illustrates this behavior:

```java
String id = "[clientID]";
String secret = "[clientSecret]";

// This can in theory be any url as long as it's set in your App settings
// for the service you want to use.
String red = "[redirectUrl]";

// Can be an arbitrary string but is not used in the local case.
// It is not allowed to be null though.
String state = "[state]";

CloudStorage storage = new Dropbox(new JavaFXReceiver(), id, secret, red, state);

try {
    // Try to login the user.
    storage.login();
} catch (AuthenticationException e) {
    // Do whatever you want to do in this case.
    System.out.println("Authentication was cancelled.");
}
```
