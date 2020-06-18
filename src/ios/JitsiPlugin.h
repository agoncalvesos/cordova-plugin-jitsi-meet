#import <Cordova/CDVPlugin.h>
#import <JitsiMeet/JitsiMeetView.h>
#import <JitsiMeet/JitsiMeetViewDelegate.h>
#import <JitsiMeet/JitsiMeetConferenceOptions.h>

@interface JitsiPlugin : CDVPlugin<JitsiMeetViewDelegate> {
	JitsiMeetView* jitsiMeetView;
	CDVInvokedUrlCommand* commandBack;
}

- (void)loadURL:(CDVInvokedUrlCommand *)command;
- (void)destroy:(CDVInvokedUrlCommand *)command;
- (void)backButtonPressed:(CDVInvokedUrlCommand *)command; 
- (void)conferenceFailed:(NSDictionary *)data;
- (void)conferenceJoined:(NSDictionary *)data;
- (void)conferenceLeft:(NSDictionary *)data;
- (void)conferenceWillJoin:(NSDictionary *)data;
- (void)conferenceWillLeave:(NSDictionary *)data;
- (void)loadConfigError:(NSDictionary *)data;

@end
