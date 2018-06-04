# Virgil Demo Messenger

![VirgilSDK](https://cloud.githubusercontent.com/assets/6513916/19643783/bfbf78be-99f4-11e6-8d5a-a43394f2b9b2.png)

## Getting Started

Start with cloning repository to your PC. Open *terminal*, navigate to the folder where you want to store the application and execute
```bash
$ git clone https://github.com/VirgilSecurity/chat-twilio-ios.git

$ cd chat-twilio-ios
```

## Prerequisites
**Virgil Messenger** uses several modules, including **Virgil SDK**. These packages are distributed via Carthage and CocoaPods. Since Carthage is a RECOMMENDED way to integrate those packages into the project, these application's dependencies are managed by it. Carthage integration is easy, convenient and you can simultaneously use CocoaPods to manage all other dependencies.

### Carthage

[Carthage](https://github.com/Carthage/Carthage) is a decentralized dependency manager that builds your dependencies and provides you with binary frameworks.

You can install Carthage with [Homebrew](http://brew.sh/) using the following command:

```bash
$ brew update
$ brew install carthage
```

#### Updating dependencies
This example already has Carthage file with all required dependencies. All you need to do is to go to the project folder and update these dependencies.

```bash 
$ cd PathToProjectFolder/chat-twilio-ios
$ carthage bootstrap --platform iOS --no-use-binaries
```

### Set Up Backend
Follow instructions [here](https://github.com/VirgilSecurity/demo-twilio-chat-js/tree/v5) for setting up your own backend.

## Build and Run
At this point you are ready to build and run the application on iPhone and/or Simulator.

## Credentials

To build this sample were used next third-party frameworks

* [Twilio Programmable Chat](https://www.twilio.com/chat) - transmitting messages and handling channel events.
* [Chatto](https://github.com/badoo/Chatto) - representing UI of chatting. 
* [Virgil SDK](https://github.com/VirgilSecurity/virgil-sdk-x) - encrypting, decrypting messages and passwordless authentication.
* [PKHUD](https://github.com/pkluz/PKHUD) - reimplementing Apple's HUD.

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
