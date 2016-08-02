<p align="center">
  <img width="200px" src="http://cloudrail.github.io/img/cloudrail_logo_github.png"/>
</p>
# CloudRail SI for Java
Integrate Multiple Services With Just One API

<p align="center">
  <img width="300px" src="http://cloudrail.github.io/img/cloudrail_si_github.png"/>
</p>

CloudRail is a free software library which abstracts multiple APIs from different providers into a single and universal interface.

**Current Interfaces:**
<p align="center">
  <img width="800px" src="http://cloudrail.github.io/img/available_interfaces_v2.png"/>
</p>
---
---

Full documentation can be found at our [wiki](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki)

Learn more about CloudRail on https://cloudrail.com

---
---

With CloudRail, you can easily integrate external APIs into your application. CloudRail is an abstracted interface that takes several services and then gives a developer-friendly API that uses common functions between all providers. This means that, for example, upload() works in exactly the same way for Dropbox as it does for Google Drive, OneDrive, and other Cloud Storage Services, and getEmail() works similarly the same way across all social networks.

## Current Interfaces:
Interface | Included Services 
--- | --- 
Cloud Storage | Dropbox, Google Drive, OneDrive, Box
Social Profiles | Facebook, GitHub, Google+, LinkedIn, Slack, Twitter, Windows Live, Yahoo, Instagram
Payment | PayPal, Stripe
Email | Maljet, Sendgrid
SMS | Twilio, Nexmo
Point of Interest | Google Places, Foursquare, Yelp
Social Interaction | Facebook, Twitter
---
### Cloud Storage Interface:

* Dropbox
* Box
* Google Drive
* Microsoft OneDrive

#### Features:

* Download files from Cloud Storage.
* Upload files to Cloud Storage.
* Get Meta Data of files, folders and perform all standard operations (copy, move, etc) with them.
* Retrieve user and quota information.
* Generate share links for files and folders.

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-cloudstorage)
#### Code Sample
```` java
// CloudStorage cs = new Box(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// CloudStorage cs = new OneDrive(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// CloudStorage cs = new GoogleDrive(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
CloudStorage cs = new Dropbox(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
new Thread() {
    @Override
    public void run() {
        cs.createFolder("/TestFolder");
        InputStream stream = null;
        try {
            stream = getClass().getResourceAsStream("Data.csv");
            long size = new File(getClass().getResource("Data.csv").toURI()).length();
            cs.upload("/TestFolder/Data.csv", stream, size, false);
        } catch (Exception e) {
            // TODO: handle error
        } finally {
            // TODO: close stream
        }
    }
}.start();
````

---

### Social Media Profiles Interface:

* Facebook
* Github
* Google Plus
* LinkedIn
* Slack
* Twitter
* Windows Live
* Yahoo
* Instagram

#### Features

* Get profile information, including full names, emails, genders, date of birth, and locales.
* Retrieve profile pictures.
* Login using the Social Network.

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-profile)
#### Code Sample

```` java
// final Profile profile = new GooglePlus(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// final Profile profile = new GitHub(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// final Profile profile = new Slack(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// final Profile profile = new Instagram(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
// ...
final Profile profile = new Facebook(redirectReceiver, "[clientIdentifier]", "[clientSecret]", "[redirectUri]", "[state]");
new Thread() {
    @Override
    public void run() {
        String fullName = profile.getFullName();
        String email = profile.getEmail();
        // ...
    }
}.start();
````

---

### Payment Interface:

* PayPal
* Stripe

#### Features Interface

* Perform charges
* Refund previously made charges
* Manage subscriptions

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-payment)
#### Code Sample

```` java
// final Payment payment = new Stripe(null, "[secretKey]");
final Payment payment = new PayPal(null, true, "[clientIdentifier]", "[clientSecret]");
new Thread() {
    @Override
    public void run() {
        CreditCard source = new CreditCard(null, 6L, 2021L, "xxxxxxxxxxxxxxxx", "visa", "<FirstName>", "<LastName>", null);
        Charge charge = payment.createCharge(500L, "USD", source);
    }
}.start();
````
---

### Email Interface:

* Mailjet
* Sendgrid

#### Features

* Send Email

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-email)
#### Code Sample

````java
// final Email email = new Mailjet(null, "[clientID]", "[clientSecret]");
final Email email = new Sendgrid(null, "[username]", "[password]");
new Thread() {
    @Override
    public void run() {
        email.sendEmail("info@cloudrail.com", "CloudRail", Arrays.asList("foo@bar.com", "bar@foo.com"), "Welcome", "Hello from CloudRail", null, null, null);
    }
}.start();
````

---

### SMS Interface:

* Twilio
* Nexmo

#### Features

* Send SMS

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-sms)
#### Code Sample

````java
// final SMS sms = new Nexmo(null, "[clientIdentifier]", "[clientSecret]");
final SMS sms = new Twilio(null, "[clientIdentifier]", "[clientSecret]");
new Thread() {
    @Override
    public void run() {
        sms.sendSMS("CloudRail", "+4912345678", "Hello from CloudRail");
    }
}.start();
````

---

### Points of Interest Interface:

* Google Places
* Foursquare
* Yelp

#### Features

* Get a list of POIs nearby
* Filter by categories or search term

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-pointsofinterest)
#### Code Example

```` java
// final PointsOfInteres poi = new Foursquare(null, "[clientID]", "[clientSecret]");
// final PointsOfInteres poi = new Yelp(null, "[consumerKey]", "[consumerSecret]", "[token]", "[tokenSecret]");
final PointsOfInteres poi = new GooglePlaces(null, "[apiKey]");
new Thread() {
    @Override
    public void run() {
        List<POI> res = poi.getNearbyPOIs(49.4557091, 8.5279138, 1000L, "restaurant", null);
        System.out.println("POIs: " + res.toString());    
    }
}.start();
````

---

### Social Media Interaction Interface:

* Facebook
* Twitter

#### Features

* Get a list of connections.
* Make a post for the user.

[Full Documentation](https://github.com/CloudRail/cloudrail-si-java-sdk/wiki/Usage#interfaces-social)
#### Code Example:

```` java
// final Social social = new Twitter(this, "[clientID]", "[clientSecret]", "[redirectUri]");
final Social social = new Facebook(this, "[clientID]", "[clientSecret]", "[redirectUri]", "[state]");
new Thread() {
    @Override
    public void run() {
        social.postUpdate("Hey there! I'm using CloudRail.");
        List<String> connections = social.getConnections();
        // ...
    }
}.start();
````
---


More interfaces are coming soon.

## Advantages of Using CloudRail

* Consistent Interfaces: As functions work the same across all services, you can perform tasks between services simply.

* Easy Authentication: CloudRail includes easy ways to authenticate, to remove one of the biggest hassles of coding for external APIs.

* Switch services instantly: One line of code is needed to set up the service you are using. Changing which service is as simple as changing the name to the one you wish to use.

* Simple Documentation: There is no searching around Stack Overflow for the answer. The CloudRail documentation at https://docs.cloudrail.com/ is regularly updated, clean, and simple to use.

* No Maintenance Times: The CloudRail Libraries are updated when a provider changes their API.

* Direct Data: Everything happens directly in the Library. No data ever passes a CloudRail server.

## Maven
pom.xml
````
<dependencies>
	<dependency>
		<groupId>com.cloudrail</groupId>
		<artifactId>cloudrail-si-java</artifactId>
		<version>2.6.1</version>
	</dependency>
</dependencies>
````
## Sample Applications

If you don't know how to start or just want to have a look at how to use our SDK in a real use case, we created a few sample application which you can try out:

* Sample using the CloudStorage interface: [File Viewer](https://github.com/CloudRail/cloudrail-si-java-sdk/tree/master/ExampleProjects/simple-cloud-file-viewer)
* Sample using the Payment and the SMS interface: [Payment SMS](https://github.com/CloudRail/cloudrail-si-java-sdk/tree/master/ExampleProjects/payment-sms)


## Get Updates

To keep updated with CloudRail, including any new providers that are added, just add your email address to https://cloudrail.com/updates/.

## Pricing

CloudRail is **free to use**. For all projects; **commercial and non-commercial**. We want APIs to be accessible to all developers. We want APIs to be easier to manage. This is only possible if there are free, powerful tools out there to do this. The only favor we are asking for, is to spread the word about this library. Thank you!

CloudRail also has enterprise licensing options. Please contact us for more information: support@cloudrail.com

## Other Platforms

CloudRail is also available for other platforms like iOS and Android. You can find all libraries on https://cloudrail.github.io

## Questions?

Get in touch at any time by emailing us: support@cloudrail.com

or

Tag a question with cloudrail on [StackOverflow](http://stackoverflow.com/questions/tagged/cloudrail)
