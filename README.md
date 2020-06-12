# cordova-plugin-jitsi-meet
Cordova plugin for Jitsi Meet React Native SDK. Works with both iOS and Android, and fixes the 64-bit binary dependency issue with Android found in previous versions of this plugin.

# Summary 
I created this repo as there were multiple versions of this plugin, one which worked for iOS and Android, but neither worked with both. This verison satisfies the 64bit requirement for Android and also works in iOS and is currently being used in production.

# Installation
`cordova plugin add https://github.com/findmate/cordova-plugin-jitsi-meet`

## iOS Installation
On iOS/Xcode you will need to manually specify the WebRTC and JitsiMeet frameworks manually to be embedded.

Example of how to select them here: https://github.com/findmate/cordova-plugin-jitsi-meet/blob/master/xcode-ios-framework-embed-example.png


# Usage
```
const callOptions = {
    url: "https:meet.jitsi.si/roomId",
    subject: "Room Subject",
    chatEnabled: false,
    inviteEnabled: false,
    calendarEnabled: false,
    welcomePageEnabled: false,
    pipEnabled: false,
    audioOnly: false,
    audioMuted: false,
    videoMuted: false,
    userInfo: {
        displayName: "John Doe",
        email: "john.doe@email.com",
        avatarURL: "https:sample.avatar.com/randomImage.png"
    }
}

jitsiplugin.loadURL(callOptions, function () {
    console.log("Call initialized with successs");
}, function (err) {
    console.log("Error initializing call");
    console.log(err);
});
```
