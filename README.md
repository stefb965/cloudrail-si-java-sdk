<p align="center">
  <img width="200px" src="http://cloudrail.github.io/img/cloudrail_logo_github.png"/>
</p>
# CloudRail - Integrate Mulitple Services With Just One API

<p align="center">
  <img width="500px" src="http://cloudrail.github.io/img/cloudrail_si_github.png"/>
</p>

CloudRail is a free software library which abstracts multiple APIs from different providers into a single and universal interface.

Full documentation can be found at https://docs.cloudrail.com/

With CloudRail, you can easily integrate external APIs into your application. CloudRail is an abstracted interface that takes several services and then gives a developer-friendly API that uses common functions between all providers. This means that, for example, upload() works in exactly the same way for Dropbox as it does for Google Drive, OneDrive, and other Cloud Storage Services, and getEmail() works similarly the same way across all social networks.

## Current Interfaces
<p align="center">
  <img width="600px" src="http://cloudrail.github.io/img/available_interfaces.png"/>
</p>

### Cloud Storage:

* Dropbox
* Box
* Google Drive
* Microsoft OneDrive

#### Features:

* Download files from Cloud Storage.
* Upload files to Cloud Storage.
* Get Meta Data of files, folders and perform all standard operations (copy, move, etc) with them.
* Retrieve user information.

#### Code Sample
```` java
// CloudStorage cs = new Box(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
// CloudStorage cs = new OneDrive(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
// CloudStorage cs = new GoogleDrive(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
CloudStorage cs = new Dropbox(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
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

### Points of Interest: 

* Google Places
* Foursquare
* Yelp

#### Features

* Get a list of POIs nearby
* Filter by categories or search term

#### Code Example

```` java
final PointsOfInteres poi = new GooglePlaces(null, "[apiKey]");
new Thread() {
    @Override
    public void run() {
        List<POI> res = poi.getNearbyPOIs(49.4557091, 8.5279138, 1000L, "restaurant", null);
        System.out.println("POIs: " + res.toString());    
    }
}.start();
````

### Social Media Profiles:

* Facebook
* Github
* Google Plus
* LinkedIn
* Slack
* Twitter
* Windows Live
* Yahoo

#### Features

* Get profile information, including full names, emails, genders, date of birth, and locales.
* Retrieve profile pictures.
* Login using the Social Network.

#### Code Sample

```` java
// final Profile profile = new GooglePlus(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
// final Profile profile = new GitHub(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
// final Profile profile = new Slack(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
// ...
final Profile profile = new Facebook(redirectReceiver, "[clientIdentifier]", "[clientSecret]");
new Thread() {
    @Override
    public void run() {
        String fullName = profile.getFullName();
        String email = profile.getEmail();
        // ...
    }
}.start();
````

### Payment 

* PayPal
* Stripe

#### Features

* Perform charges
* Refund previously made charges
* Manage subscriptions

#### Code Sample

```` java
final Payment payment = new PayPal(null, true, "[clientIdentifier]", "[clientSecret]");
new Thread() {
    @Override
    public void run() {
        CreditCard source = new CreditCard(null, 6L, 2021L, "xxxxxxxxxxxxxxxx", "visa", "<FirstName>", "<LastName>", null);
        Charge charge = payment.createCharge(500L, "USD", source);
    }
}.start();
````

### SMS

* Twilio
* Nexmo

#### Features

* Send SMS

#### Code Sample

````java
final SMS sms = new Twilio(null, "[clientIdentifier]", "[clientSecret]");
new Thread() {
    @Override
    public void run() {
        sms.sendSMS("CloudRail", "+4912345678", "Hello from CloudRail");
    }
}.start();
````

### Email 

* Mailjet
* Sendgrid

#### Features

* Send Email

#### Code Sample

````java
final Email email = new Sendgrid(null, "[username]", "[password]");
new Thread() {
    @Override
    public void run() {
        email.sendEmail("info@cloudrail.com", "CloudRail", Arrays.asList("foo@bar.com", "bar@foo.com"), "Welcome", "Hello from CloudRail", null, null, null);
    }
}.start();
````


More interfaces are coming soon.

## Advantages of Using CloudRail

* Consistent Interfaces: As functions work the same across all services, you can perform tasks between services simply.

* Easy Authentication: CloudRail includes easy ways to authenticate, to remove one of the biggest hassles of coding for external APIs.

* Switch services instantly: One line of code is needed to set up the service you are using. Changing which service is as simple as changing the name to the one you wish to use.

* Simple Documentation: There is no searching around Stack Overflow for the answer. The CloudRail documentation at https://docs.cloudrail.com/ is regularly updated, clean, and simple to use. 

* No Maintenance Times: The CloudRail Libraries are updated when a provider changes their API.

## Maven
pom.xml
```
<repositories>
	<repository>
		<id>cloudrail-maven</id>
		<name>CloudRail Maven Repo.</name>
		<url>http://maven.cloudrail.com</url>
	</repository>
</repositories>

<dependencies>
	<dependency>
		<groupId>com.cloudrail</groupId>
		<artifactId>cloudrail-si-java</artifactId>
		<version>2.2.0</version>
	</dependency>
</dependencies>
```

## Get Updates

To keep updated with CloudRail, including any new providers that are added, just add your email address to https://cloudrail.com/updates/.

## Pricing

CloudRail is free to use. For all projects; commercial and non-commercial. We want APIs to be accessible to all developers. We want APIs to be easier to manage. This is only possible if there are free, powerful tools out there to do this.

## Other Platforms

CloudRail is also available for other platforms like iOS and Android. You can find all libraries on https://cloudrail.github.io

## Questions?

Get in touch at any time by emailing us: support@cloudrail.com
