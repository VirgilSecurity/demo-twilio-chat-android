# Demo Virgil & Twilio E2EE Chat - Client

![VirgilSDK](https://cloud.githubusercontent.com/assets/6513916/19643783/bfbf78be-99f4-11e6-8d5a-a43394f2b9b2.png)

## Getting Started

Start Android Studio and choose *File -> New -> Project from Version Control -> Git* and fill in the *Git repository URL* with: 
```
https://github.com/VirgilSecurity/demo-twilio-chat-android.git
```
Press *Done* - you're good to go!

### Set Up Backend
Follow instructions [here](https://github.com/VirgilSecurity/demo-twilio-chat-js/tree/master) for setting up your own backend.

## Build and Run
At this point you are ready to build and run the application on Android Emulator. To test the application on real device you have to provide your device with access to the local server you've recently setted up.

## Credentials

To build this sample were used next third-party frameworks

* [Twilio Programmable Chat](https://www.twilio.com/chat) - transmitting messages and handling channel events.
* [Virgil SDK](https://github.com/VirgilSecurity/virgil-sdk-java-android) - encrypting, decrypting messages and passwordless authentication.
* And some popular libraries: Fuel, RxKotlin/RxAndroid, Koin, Gson.

## Documentation

Virgil Security has a powerful set of APIs, and the documentation is there to get you started today.

* [Configure the SDK][_getstarted_root] documentation
* [Usage examples][_guides]
  * [Create & publish a Card][_create_card] on Virgil Cards Service
  * [Search user's Card by user's identity][_search_card]
  * [Get user's Card by its ID][_get_card]
  * [Use Card for crypto operations][_use_card]
* [Reference API][_reference_api]

## Support
Our developer support team is here to help you. Find out more information on our [Help Center](https://help.virgilsecurity.com/).

You can find us on [Twitter](https://twitter.com/VirgilSecurity) or send us email support@VirgilSecurity.com.

Also, get extra help from our support team on [Slack](https://virgilsecurity.com/join-community).

[_getstarted_root]: https://developer.virgilsecurity.com/docs/how-to#sdk-configuration
[_guides]: https://developer.virgilsecurity.com/docs/how-to#public-key-management
[_use_card]: https://developer.virgilsecurity.com/docs/java/how-to/public-key-management/v5/use-card-for-crypto-operation
[_get_card]: https://developer.virgilsecurity.com/docs/java/how-to/public-key-management/v5/get-card
[_search_card]: https://developer.virgilsecurity.com/docs/java/how-to/public-key-management/v5/search-card
[_create_card]: https://developer.virgilsecurity.com/docs/java/how-to/public-key-management/v5/create-card
[_reference_api]: https://developer.virgilsecurity.com/docs/api-reference
