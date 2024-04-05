//
//  KFDSCNotify.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BDQueueModel;
@class BDThreadModel;
@class BDMessageModel;


@interface BDNotify : NSObject

+(BDNotify *)sharedInstance;

/**
 <#Description#>

 @param observer <#observer description#>
 */
+ (void)registerOAuthResult:(id)observer;

/**
 <#Description#>

 @param observer <#observer description#>
 */
+ (void)unregisterOAuthResult:(id)observer;


/**
 <#Description#>

 @param isSuccess <#isSuccess description#>
 */
+ (void)notifyOAuthResult:(BOOL)isSuccess;

/**
 <#Description#>

 @param status <#status description#>
 */
+ (void)notifyInitStatus:(NSString *)status;

/**
 <#Description#>

 @param status <#status description#>
 */
+ (void)notifyConnnectionStatus:(NSString *)status;

/**
 <#Description#>

 @param threadModel <#threadModel description#>
 */
+ (void)notifyThreadAdd:(BDThreadModel *)threadModel;

/**
 <#Description#>

 @param tid <#tid description#>
 */
+ (void)notifyThreadDelete:(NSString *)tid;

/**
 <#Description#>

 @param tid <#tid description#>
 */
+ (void)notifyThreadClose:(NSString *)tid;

/**
 <#Description#>
 */
+ (void)notifyThreadUpdate;

/**
 <#Description#>

 @param queueModel <#queueModel description#>
 */
+ (void)notifyQueueAdd:(BDQueueModel *)queueModel;

/**
 <#Description#>

 @param qid <#qid description#>
 */
+ (void)notifyQueueDelete:(NSString *)qid;

/**
 <#Description#>

 @param qid <#qid description#>
 */
+ (void)notifyQueueAccept:(NSString *)qid;

/**
 <#Description#>
 */
+ (void)notifyQueueUpdate;

/**
 <#Description#>

 @param localId <#localId description#>
 */
+ (void)notifyReloadCellSuccess:(NSString *)localId;

+ (void)notifyReloadCell:(NSString *)localId status:(NSString *)status;

/**
 <#Description#>

 @param messageModel <#messageModel description#>
 */
+ (void)notifyMessageAdd:(BDMessageModel *)messageModel;

/**
 <#Description#>

 @param tid <#tid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 */
+ (void)notifyMessageTextSend:(NSString *)tid withContent:(NSString *)content withLocalId:(NSNumber *)localId;

/**
 <#Description#>

 @param tid <#tid description#>
 @param imageUrl <#imageUrl description#>
 @param localId <#localId description#>
 */
+ (void)notifyMessageImageSend:(NSString *)tid withImageUrl:(NSString *)imageUrl withLocalId:(NSNumber *)localId;

/**
 <#Description#>

 @param messageId <#messageId description#>
 */
+ (void)notifyMessageDelete:(NSNumber *)messageId;

/**
 <#Description#>
 */
+ (void)notifyMessagePreview:(BDMessageModel *)message;

/**
 <#Description#>

 @param mid mid
 */
+ (void)notifyMessageRecall:(NSString *)mid;

/**
 <#Description#>

 @param localId <#localId description#>
 @param status <#status description#>
 */
+ (void)notifyMessage:(NSNumber *)localId withStatus:(NSString *)status;

/**
 <#Description#>
 */
+ (void)notifyContactUpdate;

/**
 <#Description#>
 */
+ (void)notifyGroupUpdate;

/**
 <#Description#>
 */
+ (void)notifyProfileUpdate;

/**
 <#Description#>

 @param content <#content description#>
 */
+ (void)notifyKickoff:(NSString *)content;


+ (void)notifyWebRTCMessage:(BDMessageModel *)message;

// 账号过期，待续费
+ (void)notifyOutOfDate;

// 收到转接会话
+ (void)notifyTransferMessage:(BDMessageModel *)message;

// 收到接受转接
+ (void)notifyTransferAcceptMessage:(BDMessageModel *)message;

// 收到拒绝转接
+ (void)notifyTransferRejectMessage:(BDMessageModel *)message;

//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCInvite:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCCancel:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCOfferVideo:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCOfferAudio:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCAnswer:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCCandidate:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCAccept:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCReject:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCReady:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCBusy:(NSDictionary *)dict;
//
///**
// <#Description#>
//
// @param dict <#dict description#>
// */
//+ (void)notifyWebRTCClose:(NSDictionary *)dict;

#pragma mark - 微信

/**
 <#Description#>

 @param dict <#dict description#>
 */
+ (void)notifyWeChatStart:(NSDictionary *)dict;

/**
 <#Description#>

 @param dict <#dict description#>
 */
+ (void)notifyWeChatSuccess:(NSDictionary *)dict;

/**
 <#Description#>

 @param dict <#dict description#>
 */
+ (void)notifyWeChatError:(NSDictionary *)dict;

#pragma mark -

/**
 <#Description#>
 */
-(void)initNofifySound;

/**
 <#Description#>
 */
-(void)playMessageSendSound;

/**
 <#Description#>
 */
-(void)playMessageReceivedSound;

/**
 <#Description#>
 */
-(void)backgroundMessageReceivedVibrate;

/**
 <#Description#>

 @param percentage <#percentage description#>
 @param localId <#localId description#>
 */
+ (void)notifyUploadPercentage:(NSString *)percentage withLocalId:(NSString *)localId;

/**
 <#Description#>

 @param localId <#localId description#>
 */
+ (void)notifyUploadError:(NSString *)localId;

@end












