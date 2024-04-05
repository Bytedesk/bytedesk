//
//  WXMQTTApis.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/5/20.
//  Copyright © 2018年 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BDThreadModel;
@class BDExtraParam;

@interface BDMQTTApis : NSObject

+ (BDMQTTApis *)sharedInstance;

/**
 建立连接
 */
- (void)connect;

/**
 建立连接

 @param clientId <#clientId description#>
 */
- (void)connect:(NSString *)clientId;

/**
 订阅主题

 @param topic <#topic description#>
 */
- (void)subscribeTopic:(NSString *)topic;

/**
 取消订阅

 @param topic <#topic description#>
 */
- (void)unsubscribeTopic:(NSString *)topic;

/// 发送消息
/// @param mid message唯一mid
/// @param type 消息类型
/// @param content 消息内容
/// @param tId 会话thread唯一tid
/// @param topic 会话topic
/// @param threadType 会话类型
/// @param threadNickname 会话昵称
/// @param threadAvatar 会话头像
- (void)sendMessageProtobuf:(NSString *)mid type:(NSString *)type content:(NSString *)content tid:(NSString *)tId topic:(NSString *)topic threadType:(NSString *)threadType threadNickname:(NSString *)threadNickname threadAvatar:(NSString *)threadAvatar threadClient:(NSString *)threadClient extraParam:(BDExtraParam *)extraParam;

- (void)sendMessageProtobuf:(NSString *)mid type:(NSString *)type content:(NSString *)content thread:(BDThreadModel *)thread extraParam:(BDExtraParam *)extraParam;

- (void)sendTextMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread;

- (void)sendImageMessageProtobuf:(NSString *)mid content:(NSString *)imageUrl thread:(BDThreadModel *)thread;

- (void)sendVoiceMessageProtobuf:(NSString *)mid content:(NSString *)voiceUrl thread:(BDThreadModel *)thread;

- (void)sendFileMessageProtobuf:(NSString *)mid content:(NSString *)fileUrl thread:(BDThreadModel *)thread;

- (void)sendVideoMessageProtobuf:(NSString *)mid content:(NSString *)videoUrl thread:(BDThreadModel *)thread;

- (void)sendCommodityMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread;

- (void)sendInviteRateMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread;

- (void)sendFormRequestMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread;

- (void)sendPreviewMessageProtobufThread:(BDThreadModel *)thread previewContent:(NSString *)previewContent;

- (void)sendRecallMessageProtobufThread:(BDThreadModel *)thread recallMid:(NSString *)recallMid;

- (void)sendReceiptReceivedMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid;

- (void)sendReceiptReadMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid;

- (void)sendReceiptMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid receiptStatus:(NSString *)receiptStatus;

- (void)sendTransferMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic transferContent:(NSString *)transferContent;

- (void)sendTransferAcceptMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic;

- (void)sendTransferRejectMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic;

//
- (void)sendWebRTCOfferVideoSDP:(NSString *)content thread:(BDThreadModel *)threadModel;
- (void)sendWebRTCAnswerSDP:(NSString *)content thread:(BDThreadModel *)threadModel;
- (void)sendWebRTCICECandidate:(NSString *)content thread:(BDThreadModel *)threadModel;
- (void)sendWebRTCCancel:(BDThreadModel *)threadModel;
- (void)sendWebRTCClose:(BDThreadModel *)threadModel;

/**
 设置在线状态

 @param status <#status description#>
 */
//- (void)setStatus:(NSString *)status;

/**
 断开连接
 */
- (void)disconnect;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL)isConnected;


@end

///**
// 发送文本消息
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendTextMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype;
//
///**
// 发送图片消息
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendImageMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype;
//
///**
// 发送语音消息
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendVoiceMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype;
//
///**
// 发送消息预知通知
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#sessiontype description#>
// */
//- (void)sendPreviewMessage:(NSString *)content toTid:(NSString *)tId sessionType:(NSString *)sessiontype;
//
///**
//  发送消息送达通知
//
// @param mId 消息唯一mid
// */
//- (void)sendReceiptReceivedMessage:(NSString *)mId threadTid:(NSString *)tId;
//
///**
//  发送消息已读通知
//
// @param mId 消息唯一mid
// */
//- (void)sendReceiptReadMessage:(NSString *)mId threadTid:(NSString *)tId;
//
///**
// 发送消息销毁通知
//
// @param mId 消息唯一mid
// */
//- (void)sendReceiptDestroyedMessage:(NSString *)mId threadTid:(NSString *)tId;
//
///**
// 发送消息回执通知
//
// @param mId mid
// @param status status
// */
//- (void)sendReceiptMessage:(NSString *)mId threadTid:(NSString *)tId status:(NSString *)status;


//- (void)sendWebRTCInviteVideoMessage:(NSString *)uid sessionType:(NSString *)sessionType;
//
//
//- (void)sendWebRTCInviteAudioMessage:(NSString *)uid sessionType:(NSString *)sessionType;
//
//
//- (void)sendWebRTCOfferVideoSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content;
//
//
//- (void)sendWebRTCOfferAudioSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content;
//
//
//- (void)sendWebRTCAnswerSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content;
//
//
//- (void)sendWebRTCICECandidate:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content;
//
//
//- (void)sendWebRTCCancelMessage:(NSString *)uid sessionType:(NSString *)sessionType;
//
//
//- (void)sendWebRTCAcceptMessage:(NSString *)uid sessionType:(NSString *)sessionType;
//
//
//- (void)sendWebRTCRejectMessage:(NSString *)uid sessionType:(NSString *)sessionType;
//
//
//- (void)sendWebRTCCloseMessage:(NSString *)uid sessionType:(NSString *)sessionType;

/**
 发送webrtc消息

 @param uuid 会话tid、用户uid 或者 群组gid
 @param type 消息类型：invite、sdp、ice等
 @param sessionType 会话类型：客服会话、单聊、群组
 */
//- (void)sendWebRTCMessage:(NSString *)uuid type:(NSString *)type sessionType:(NSString *)sessionType content:(NSString *)content;

/**
 发送消息

 @param content <#content description#>
 @param type <#type description#>
 @param tId <#tId description#>
 @param sessiontype <#stype description#>
 */
//- (void)sendMessage:(NSString *)content type:(NSString *)type toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype;

