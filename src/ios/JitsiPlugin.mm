#import "JitsiPlugin.h"

@implementation JitsiPlugin

CDVPluginResult *pluginResult = nil;

- (void)loadURL:(CDVInvokedUrlCommand *)command {

    NSDictionary * inputObject = [command.arguments objectAtIndex:0];
    NSString * server = [inputObject objectForKey: @"server"];
    NSString * room = [inputObject objectForKey: @"room"];
    NSString * subject = [inputObject objectForKey: @"subject"];
    NSString * jwt = [inputObject objectForKey: @"jwt"];

    Boolean chatEnabled = [[inputObject objectForKey: @"chatEnabled"] boolValue];
    Boolean inviteEnabled = [[inputObject objectForKey: @"inviteEnabled"] boolValue];
    Boolean calendarEnabled = [[inputObject objectForKey: @"calendarEnabled"] boolValue];
    Boolean welcomePageEnabled = [[inputObject objectForKey: @"welcomePageEnabled"] boolValue];
    Boolean audioOnly = [[inputObject objectForKey: @"audioOnly"] boolValue];
    Boolean audioMuted = [[inputObject objectForKey: @"audioMuted" ] boolValue];
    Boolean videoMuted = [[inputObject objectForKey: @"videoMuted"] boolValue];
    
    commandBack = command;
    jitsiMeetView = [[JitsiMeetView alloc] initWithFrame:self.viewController.view.frame];
    jitsiMeetView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    jitsiMeetView.delegate = self;
    
    JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
        if(audioOnly) builder.audioOnly = audioOnly;
        if(audioMuted) builder.audioMuted = audioMuted;
        if(videoMuted) builder.videoMuted = videoMuted;
        if(welcomePageEnabled) builder.welcomePageEnabled = welcomePageEnabled;
        if(chatEnabled) [builder setFeatureFlag:@"chat.enabled" withBoolean:chatEnabled];
        if(calendarEnabled) [builder setFeatureFlag:@"calendar.enabled" withBoolean:calendarEnabled];
        if(inviteEnabled) [builder setFeatureFlag:@"invite.enabled" withBoolean:inviteEnabled];
        if(jwt) builder.token = jwt;
        if(subject) builder.subject = subject;
        
        builder.room = room;
        builder.serverURL = [NSURL URLWithString:server];
     }];

    [jitsiMeetView join:options];
    [self.viewController.view addSubview:jitsiMeetView];
}


- (void)destroy:(CDVInvokedUrlCommand *)command {
    [self destroyView];
    if(command){
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"DESTROYED"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) destroyView {
    if(jitsiMeetView){
        [jitsiMeetView removeFromSuperview];
        jitsiMeetView = nil;
    }
}

void _onJitsiMeetViewDelegateEvent(NSString *name, NSDictionary *data) {
    NSLog(
        @"[%s:%d] JitsiMeetViewDelegate %@ %@",
        __FILE__, __LINE__, name, data);

}

- (void)conferenceJoined:(NSDictionary *)data{
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_JOINED", data);
}

- (void)conferenceTerminated:(NSDictionary *)data{
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_TERMINATED", data);
    [self destroyView];
}

- (void)conferenceWillJoin:(NSDictionary *)data;{
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_ENTERED_PIP", data);
}


- (void)enterPictureInPicture:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_ENTERED_PIP", data);
}


@end
