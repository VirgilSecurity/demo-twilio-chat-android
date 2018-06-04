# Virgil Demo Messenger

![VirgilSDK](https://cloud.githubusercontent.com/assets/6513916/19643783/bfbf78be-99f4-11e6-8d5a-a43394f2b9b2.png)

## Getting Started

Start Android Studio and choose File -> New -> Project from Version Control -> Git and fill in the *Git repository URL* with: 
```
https://github.com/VirgilSecurity/demo-twilio-chat-android.git
```
Press Done - you're good to go!

### Set Up Backend
Follow instructions [here](https://github.com/VirgilSecurity/demo-twilio-chat-js/tree/v5) for setting up your own backend.

## Build and Run
At this point you are ready to build and run the application on Android Emulator. To test the application on real device you have to provide your device with access to the local server you've recently setted up.

## Credentials

To build this sample were used next third-party frameworks

* [Twilio Programmable Chat](https://www.twilio.com/chat) - transmitting messages and handling channel events.
* [Virgil SDK](https://github.com/VirgilSecurity/virgil-sdk-x) - encrypting, decrypting messages and passwordless authentication.
* As well with well known libraries as: Fuel, RxKotlin/RxAndroid, Koin, Gson.

## Documentation

Virgil Security has a powerful set of APIs, and the documentation is there to get you started today.

* [Get Started][_getstarted_root] documentation
  * [Initialize the SDK][_guide_initialization]
  * [Encrypted storage][_getstarted_storage]
  * [Encrypted communication][_getstarted_encryption]
  * [Data integrity][_getstarted_data_integrity]
* [Guides][_guides]
  * [Virgil Cards][_guide_virgil_cards]
* [Reference API][_reference_api]

## Support

Our developer support team is here to help you. You can find us on [Twitter](https://twitter.com/virgilsecurity) or send us email support@virgilsecurity.com

[_getstarted_root]: https://developer.virgilsecurity.com/docs/swift/get-started
[_getstarted_encryption]: https://developer.virgilsecurity.com/docs/swift/get-started/encrypted-communication
[_getstarted_storage]: https://developer.virgilsecurity.com/docs/swift/get-started/encrypted-storage
[_getstarted_data_integrity]: https://developer.virgilsecurity.com/docs/swift/get-started/data-integrity
[_guides]: https://developer.virgilsecurity.com/docs/swift/guides
[_guide_initialization]: https://developer.virgilsecurity.com/docs/swift/how-to/setup/v5/install-sdk
[_guide_virgil_cards]: https://developer.virgilsecurity.com/docs/swift/how-to/public-key-management/v5/create-card
[_guide_virgil_keys]: https://developer.virgilsecurity.com/docs/swift/how-to/public-key-management/v5/create-card
[_guide_encryption]: https://developer.virgilsecurity.com/docs/swift/how-to/public-key-management/v5/use-card-for-crypto-operation
[_reference_api]: https://developer.virgilsecurity.com/docs/api-reference
