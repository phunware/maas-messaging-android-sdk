#MaaS Messaging SDK for Android

[Android MaaS Messaging Documentation](http://phunware.github.io/maas-messaging-android-sdk/)
=======
**Version 3.0.0**
________________


## Overview
This is Phunware's Android SDK for Messaging. Visit http://maas.phunware.com/ for more details and to sign up.

### Build requirements
* Android SDK 4.0.3+ (API level 15) or above

### Documentation

Developer documentation can be found at
[developer.phunware.com](https://developer.phunware.com/pages/viewpage.action?pageId=3409274).

Attribution
-----------
MaaS Messaging uses the following third party components.

| Component     | Description   | License  |
| ------------- |:-------------:| -----:|
| [okhttp](https://github.com/square/okhttp)        | An HTTP+HTTP/2 client for Android and Java applications by Square, Inc. | [Apache 2.0](https://github.com/square/okhttp/blob/master/LICENSE.txt) |
| [autovalue](https://github.com/google/auto/tree/master/value)        | AutoValue provides an easier way to create immutable value classes | [Apache 2.0](https://github.com/google/auto/blob/master/LICENSE.txt) |
| [autovalue GSON](https://github.com/rharter/auto-value-gson)        | An extension for Google's AutoValue that creates a simple Gson TypeAdapterFactory for each AutoValue annotated object. | [Apache 2.0](https://github.com/rharter/auto-value-gson/blob/master/LICENSE.txt) |

## Setup
-------
1. Bring up the sample app with Android studio
2. Create a new Android application in MAAS-portal
3. In strings.xml (under Sample/src/main/res/values) of the downloaded sample, replace the appId, accessKey and signatureKey with values for this application in MAAS portal.

  e.g.Replace the following strings with values from MaaS Portal

  `<string name="appId">1421</string>`

  `<string name="accesKey">b91b116ceafb413bed252a7b274e95a622aee20b</string>`

  `<string name="sigKey">3191eacc0cd0a5f66b445d11fbdf08fad7c596b8</string>`

  Because we also show a google map in the sample app, in order to use the locations feature please also add the following meta data tag with a google maps key

  `<meta-data android:name="com.google.android.geo.API_KEY" android:value="XXXX"/>`

4. Set up GCM at https://developers.google.com/cloud-messaging/android/client
5. Create a project on Firebase console

    -> Choose 'Add Firebase to Android app'

    -> In the Firebase console, the package name should be the same as application id in build.grade (under Sample directory) in the sample app. The default application id in the messaging sample app is com.phunware.messaging.sample
8. In the MAAS portal, for the newly created Android app, replace the API Key and sender id with the values for ServerKey and SenderId on the Firebase console (under CloudMessaging section)
9. In build.gradle of the messaging sample app, replace GCMSenderId with the SenderId in Firebase console (under CloudMessaging section).

  e.g.

        defaultConfig {

          applicationId "com.phunware.messaging.sample" minSdkVersion 16 targetSdkVersion 22
          //insert your GCM sender id below
          resValue 'string', 'GCMSenderId', '\"478224698966\"'

        }

10. The Firebase console creates a google-services.json file and downloads it to your default Downloads folder.
11. Replace the default google-service.json file (under Sample directory) the sample app with the dowloaded google-service.json from Firebase console
12. Compile the project under Android Studio and run it on the device
