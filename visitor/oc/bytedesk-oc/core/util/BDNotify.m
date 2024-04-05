//
//  KFDSCNotify.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDNotify.h"
#import "BDSettings.h"
#import "BDUtils.h"
#import "BDQueueModel.h"
#import "BDThreadModel.h"
#import "BDMessageModel.h"
#import "BDConstants.h"

#import <AudioToolbox/AudioToolbox.h>

@interface BDNotify ()

@property(nonatomic, assign) SystemSoundID  messageSendSoundID;
@property(nonatomic, assign) SystemSoundID  messageReceivedSoundID;

@end

static BDNotify *sharedInstance = nil;

@implementation BDNotify

@synthesize messageSendSoundID,
messageReceivedSoundID;

+(BDNotify *)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDNotify alloc] init];
    });
    return sharedInstance;
}

-(id)init {
    self = [super init];
    if (self) {
        [self initNofifySound];
    }
    return self;
}


+ (void)registerOAuthResult:(id)observer {
   
}

+ (void)unregisterOAuthResult:(id)observer {
    
}

+ (void)notifyOAuthResult:(BOOL)isSuccess {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_OAUTH_RESULT object:[NSNumber numberWithBool:isSuccess]];
}

+ (void)notifyInitStatus:(NSString *)status {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_INIT_STATUS object:status];
}

+ (void)notifyConnnectionStatus:(NSString *)status {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_CONNECTION_STATUS object:status];
}

+ (void)notifyThreadAdd:(BDThreadModel *)threadModel {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_THREAD_ADD object:threadModel];
}

+ (void)notifyThreadDelete:(NSString *)tid {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_THREAD_DELETE object:tid];
}

+ (void)notifyThreadClose:(NSString *)tid {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_THREAD_CLOSE object:tid];
}


+ (void)notifyQueueAdd:(BDQueueModel *)queueModel {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_QUEUE_ADD object:queueModel];
}

+ (void)notifyQueueDelete:(NSString *)qid {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_QUEUE_DELETE object:qid];
}

+ (void)notifyQueueAccept:(NSString *)qid {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_QUEUE_ACCEPT object:qid];
}

+ (void)notifyReloadCellSuccess:(NSString *)localId {
    [BDNotify notifyReloadCell:localId status:BD_MESSAGE_STATUS_STORED];
}

+ (void)notifyReloadCell:(NSString *)localId status:(NSString *)status {
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    dict[@"localId"] = localId;
    dict[@"status"] = status;
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_LOCALID object:dict];
}

+ (void)notifyMessageAdd:(BDMessageModel *)messageModel {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_ADD object:messageModel];
}

+ (void)notifyMessageTextSend:(NSString *)tid withContent:(NSString *)content withLocalId:(NSNumber *)localId {
    //
    BDMessageModel *messageModel = [[BDMessageModel alloc] init];
    messageModel.current_uid = [BDSettings getUid];
    messageModel.username = [BDSettings getUsername];
    messageModel.nickname = [BDSettings getNickname];
    messageModel.avatar = [BDSettings getAvatar];
    messageModel.type = BD_MESSAGE_TYPE_TEXT;
    messageModel.content = content;
    messageModel.client = [BDSettings getClient];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_ADD object:messageModel];
}

+ (void)notifyMessageImageSend:(NSString *)tid withImageUrl:(NSString *)imageUrl withLocalId:(NSNumber *)localId {
    //
    BDMessageModel *messageModel = [[BDMessageModel alloc] init];
    
    messageModel.current_uid = [BDSettings getUid];
    messageModel.username = [BDSettings getUsername];
    messageModel.nickname = [BDSettings getNickname];
    messageModel.avatar = [BDSettings getAvatar];
    messageModel.type = BD_MESSAGE_TYPE_IMAGE;
    messageModel.pic_url = imageUrl;
    messageModel.image_url = imageUrl;
    messageModel.client = [BDSettings getClient];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_ADD object:messageModel];
}

+ (void)notifyMessageDelete:(NSNumber *)messageId {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_DELETE object:messageId];
}

+ (void)notifyMessagePreview:(BDMessageModel *)message {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_PREVIEW object:message];
}

+ (void)notifyMessageRecall:(NSString *)mid {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_RECALL object:mid];
}

+ (void)notifyMessage:(NSNumber *)localId withStatus:(NSString *)status {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:status, @"status", [localId stringValue], @"localId", nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_MESSAGE_STATUS object:dict];
}

//
+ (void)notifyThreadUpdate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_THREAD_UPDATE object:nil];
}

+ (void)notifyContactUpdate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_CONTACT_UPDATE object:nil];
}

+ (void)notifyGroupUpdate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_GROUP_UPDATE object:nil];
}

+ (void)notifyQueueUpdate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_QUEUE_UPDATE object:nil];
}

+ (void)notifyProfileUpdate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_PROFILE_UPDATE object:nil];
}

+ (void)notifyKickoff:(NSString *)content {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_KICKOFF object:content];
}

// webrtc
+ (void)notifyWebRTCMessage:(BDMessageModel *)message {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_MESSAGE object:message];
}

// 账号过期，待续费
+ (void)notifyOutOfDate {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_OUTOFDATE object:nil];
}

// 收到转接会话
+ (void)notifyTransferMessage:(BDMessageModel *)message {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_TRANSFER object:message];
}

// 收到接受转接
+ (void)notifyTransferAcceptMessage:(BDMessageModel *)message {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_TRANSFER_ACCEPT object:message];
}

// 收到拒绝转接
+ (void)notifyTransferRejectMessage:(BDMessageModel *)message {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_TRANSFER_REJECT object:message];
}



//+ (void)notifyWebRTCInvite:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_INVITE object:dict];
//}
//
//+ (void)notifyWebRTCCancel:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_CANCEL object:dict];
//}
//
//+ (void)notifyWebRTCOfferVideo:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_OFFER_VIDEO object:dict];
//}
//
//+ (void)notifyWebRTCOfferAudio:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_OFFER_AUDIO object:dict];
//}
//
//+ (void)notifyWebRTCAnswer:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_ANSWER object:dict];
//}
//
//+ (void)notifyWebRTCCandidate:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_CANDIDATE object:dict];
//}
//
//+ (void)notifyWebRTCAccept:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_ACCEPT object:dict];
//}
//
//+ (void)notifyWebRTCReject:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_REJECT object:dict];
//}
//
//+ (void)notifyWebRTCReady:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_READY object:dict];
//}
//
//+ (void)notifyWebRTCBusy:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_BUSY object:dict];
//}
//
//+ (void)notifyWebRTCClose:(NSDictionary *)dict {
//    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WEBRTC_CLOSE object:dict];
//}

#pragma mark - 微信

+ (void)notifyWeChatStart:(NSDictionary *)dict {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WECHAT_START object:dict];
}

+ (void)notifyWeChatSuccess:(NSDictionary *)dict {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WECHAT_SUCCESS object:dict];
}

+ (void)notifyWeChatError:(NSDictionary *)dict {
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_NOTIFICATION_WECHAT_ERROR object:dict];
}

#pragma mark -

-(void)initNofifySound {
    NSString *inpath = [[NSBundle bundleForClass:[self class]] pathForResource:@"in" ofType:@"caf"];
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL fileURLWithPath:inpath], &messageReceivedSoundID);
    
    NSString *receivepath = [[NSBundle bundleForClass:[self class]] pathForResource:@"messageReceived" ofType:@"aiff"];
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)[NSURL fileURLWithPath:receivepath], &messageSendSoundID);
}

-(void)playMessageSendSound {
    AudioServicesPlaySystemSound (messageSendSoundID);
}

-(void)playMessageReceivedSound {
    AudioServicesPlaySystemSound (messageReceivedSoundID);
}

-(void)backgroundMessageReceivedVibrate {
    AudioServicesPlaySystemSound (kSystemSoundID_Vibrate);
}

+ (void)notifyUploadPercentage:(NSString *)percentage withLocalId:(NSString *)localId {
    
    NSDictionary *dictionary = [[NSDictionary alloc] initWithObjectsAndKeys:percentage, @"percentage", localId, @"localId", nil];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_UPLOAD_NOTIFICATION_PERCENTAGE object:dictionary];
}

+ (void)notifyUploadError:(NSString *)localId {
    
    NSDictionary *dictionary = [[NSDictionary alloc] initWithObjectsAndKeys:localId, @"localId", nil];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:BD_UPLOAD_NOTIFICATION_ERROR object:dictionary];
}

#pragma mark -



@end









