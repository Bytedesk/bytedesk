//
//  WXMQTTApis.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/5/20.
//  Copyright © 2018年 bytedesk.com. All rights reserved.
//
//@import MQTTClient;
#import "MQTTClient.h"
//#import <MQTTClient/MQTTClient.h>

#import "BDMQTTApis.h"
#import "BDSettings.h"
#import "BDDBApis.h"
#import "BDNotify.h"
#import "BDConstants.h"
#import "BDUtils.h"
#import "BDConfig.h"
//#import "AESCipher.h"

#import "BDExtraParam.h"
#import "BDThreadModel.h"
#import "BDQueueModel.h"
#import "BDMessageModel.h"
#import "BDContactModel.h"
#import "BDGroupModel.h"
#import "BDWorkGroupModel.h"

#import "User.pbobjc.h"
#import "Thread.pbobjc.h"
#import "Message.pbobjc.h"

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#endif

//#import <bytedesk-mars/bdmars.h>
#define AES_KEY @"16BytesLengthKey"

static BDMQTTApis *sharedInstance = nil;

@interface BDMQTTApis()<MQTTSessionDelegate>

@property(nonatomic, strong) NSString *mClientId;
@property(nonatomic, strong) MQTTSession *mSession;
@property(nonatomic, assign) BOOL mConnected;

@end

@implementation BDMQTTApis

+ (BDMQTTApis *)sharedInstance {
    //
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDMQTTApis alloc] init];
    });
    return sharedInstance;
}

/**
 官方文档:
    https://github.com/novastone-media/MQTT-Client-Framework
 博客：
    https://www.hivemq.com/blog/mqtt-client-library-encyclopedia-mqtt-client-framework
    https://www.jianshu.com/p/80ea4507ca74
 iOS MQTT使用案例 (断线重连):
    https://blog.csdn.net/imhwn/article/details/77508821
 RabbitMQ:
    http://www.rabbitmq.com/mqtt.html
 */
- (void)connect {
    // TODO: 增加网络检测，如果无网络则提示
    //
    if ([[BDSettings getUid] length] == 0) {
        NSLog(@"用户名不能为空-请首先调用授权接口");
    } else {
        // FIXME: 暂未找到处理同一账号异地登录的问题，故先添加uuid后缀
        // TODO: 首次生成，之后直接使用缓存clientId
//        NSString *clientId = [NSString stringWithFormat:@"%@/%@/%@", [BDSettings getUid], [BDSettings getClient], [BDSettings getClientUUID]];
        NSString *clientId = [NSString stringWithFormat:@"%@/%@", [BDSettings getUid], [BDSettings getClient]];
        [self connect:clientId];
    }
}

// TODO: 增加异地登录踢掉线管理
- (void)connect:(NSString *)clientId {
    //
    [BDNotify notifyConnnectionStatus:BD_USER_STATUS_CONNECTING];
    //
    self.mClientId = clientId;
#ifdef DEBUG
    // 设置loglevel,否则log会太多
    [MQTTLog setLogLevel:DDLogLevelWarning];
#else
    [MQTTLog setLogLevel:DDLogLevelWarning]; // DDLogLevelWarning
#endif
    [MQTTStrict setStrict:NO];
    //
//    MQTTCFSocketTransport *transport = [[MQTTCFSocketTransport alloc] init];
//    transport.host = [BDConfig getMqttHost];
//    transport.port = (UInt32)[BDConfig getMqttPort];
    //
//    MQTTWebsocketTransport *transport = [[MQTTWebsocketTransport alloc] init];
//    transport.url = [NSURL URLWithString:[BDConfig getMqttWebSocketWssURL]];
//    // TODO: 正式上线之前，全部转换为tls，启用安全模式
////    transport.tls = NO;
//    transport.tls = [BDConfig isTlsConnection];
    
    self.mSession = [[MQTTSession alloc] init];
//    self.mSession.transport = transport;
    self.mSession.transport = [self socketTransport];
    self.mSession.delegate = self;
    // 是否保留之前的订阅, 接收之前的消息，
    self.mSession.cleanSessionFlag = false;
    self.mSession.keepAliveInterval = 10;
    
//    // 如果客户端异常地断开连接，服务器端(the broker)将会广播 LWT 消息到所有订阅者的客户端中。
//    self.mSession.willFlag = YES;
//    self.mSession.willTopic = BD_MQTT_TOPIC_LASTWILL;
//    //
//    NSMutableDictionary *offlineDict = [NSMutableDictionary new];
//    [offlineDict setObject:BD_MESSAGE_TYPE_NOTIFICATION_ONLINE_STATUS forKey:@"type"];
//    [offlineDict setObject:BD_CLIENT_IOS forKey:@"client"];
//    [offlineDict setObject:BD_USER_STATUS_DISCONNECTED forKey:@"status"];
//    [offlineDict setObject:self.mClientId forKey:@"clientId"];
//    [offlineDict setObject:@"2" forKey:@"version"];
//    // Object To JSON string
//    NSString *offlineJsonString = [BDUtils dictToJson:offlineDict];
//    //
//    self.mSession.willMsg = [offlineJsonString dataUsingEncoding:NSUTF8StringEncoding];
//    // 注意：不能使用MQTTQosLevelExactlyOnce，否则异常离线的情况，服务器会收不到通知
//    // 务必使用 MQTTQosLevelAtLeastOnce 或者 MQTTQosLevelAtMostOnce
//    self.mSession.willQoS = MQTTQosLevelAtMostOnce;
//    self.mSession.willRetainFlag = YES;
//    self.mSession.willDelayInterval = [NSNumber numberWithInt:10];
    
    // cliendId每个客户端唯一
    [self.mSession setClientId:self.mClientId];
//    [self.mSession setUserName:[BDConfig getMqttAuthUsername]];
//    [self.mSession setPassword:[BDConfig getMqttAuthPassword]];
    //
    [self.mSession addObserver:self forKeyPath:@"status" options:NSKeyValueObservingOptionOld context:nil];
    // 连接
//    [self.mSession connectAndWaitTimeout:1]; //会话连接并设置超时时间
    [self.mSession connect];
}

- (MQTTTransport *) socketTransport {
    
    if ([BDConfig isWebSocketWssConnection]) {
        NSString *url = [BDConfig getMqttWebSocketWssURL];
//        NSLog(@"wss url %@", url);
        MQTTWebsocketTransport *transport = [[MQTTWebsocketTransport alloc] init];
        transport.url = [NSURL URLWithString:url];
        transport.tls = [BDConfig isTlsConnection];
        return transport;
    } else {
        NSString *host = [BDConfig getMqttHost];
        NSLog(@"tcp host %@", host);
        MQTTCFSocketTransport *transport = [[MQTTCFSocketTransport alloc] init];
        transport.host = host;
        transport.port = (UInt32)[BDConfig getMqttPort];
        transport.tls = [BDConfig isTlsConnection];
        return transport;
    }
}

#pragma mark - MQTTSessionDelegate


- (void)connected:(MQTTSession *)mSession {
    NSLog(@"%s connected, %@", __PRETTY_FUNCTION__, mSession);
    //
    self.mConnected = TRUE;
    // 通知界面连接状态
    [BDNotify notifyConnnectionStatus:BD_USER_STATUS_CONNECTED];
}

- (void)connected:(MQTTSession *)mSession mSessionPresent:(BOOL)mSessionPresent {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)connectionRefused:(MQTTSession *)mSession error:(NSError *)error {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)connectionClosed:(MQTTSession *)mSession {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.mConnected = FALSE;
    //
    [BDNotify notifyConnnectionStatus:BD_USER_STATUS_DISCONNECTED];
}

- (void)connectionError:(MQTTSession *)mSession error:(NSError *)error {
    NSLog(@"%s, error: %@", __PRETTY_FUNCTION__, error.localizedDescription);
    self.mConnected = FALSE;
    //
    [BDNotify notifyConnnectionStatus:BD_USER_STATUS_CONNECT_ERROR];
}

- (void)handleEvent:(MQTTSession *)mSession event:(MQTTSessionEvent)eventCode error:(NSError *)error {
    NSLog(@"%s, eventCode:%ld, error:%@ ", __PRETTY_FUNCTION__, (long)eventCode, error);
    // TODO: eventCode !== 0 的时候，通知正在发送的消息，发送失败
}

// TODO: 发送成功，更新本地消息状态
- (void)messageDelivered:(MQTTSession *)mSession
                   msgID:(UInt16)msgID
                   topic:(NSString *)topic
                    data:(NSData *)data
                     qos:(MQTTQosLevel)qos
              retainFlag:(BOOL)retainFlag {
    //
    // [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding],
    NSLog(@"%s, msgID:%d, topic:%@, qos:%d, retrainFlag:%@", __PRETTY_FUNCTION__, msgID, topic, qos, retainFlag?@"true":@"false");
    //
//    if ([topic hasPrefix:BD_MQTT_TOPIC_PROTOBUF_PREFIX]) {
        // TODO: protobuf
        NSLog(@"messageDelivered 收到protobuf消息");
        
        NSError* error;
        Message *message = [Message parseFromData:data extensionRegistry:nil error:&error];
        if (error) {
            NSLog(@"protobuf parseFromData error");
            return;
        }
        NSLog(@"protobuf content %@", message.text.content);
        //
        // 服务器返回自定义消息本地id
        NSString *localId = message.mid;
        // 修改本地消息发送状态为成功
        [[BDDBApis sharedInstance] updateMessageSuccess:localId];
        [BDNotify notifyReloadCellSuccess:localId];
        
//        return;
//    }
    
    //
//    NSError* error;
//    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
//    if (error) {
//        NSLog(@"parse send message error: %s %@", __PRETTY_FUNCTION__, error.localizedDescription);
//        return;
//    }
//    //
//    if ([dict objectForKey:@"localId"] != nil) {
//        // 服务器返回自定义消息本地id
//        NSString *localId = dict[@"localId"];
//        // 修改本地消息发送状态为成功
//        [[BDDBApis sharedInstance] updateMessageSuccess:localId];
//        [BDNotify notifyReloadCellSuccess:localId];
//    }
    
//    TODO: 下面为protobuf相关解析代码，测试成功
//    NSError* error;
//    ProtoMessage *messageProto = [ProtoMessage parseFromData:data error:&error];
//    if (error) {
//        NSLog(@"parse message proto error: %s %@", __PRETTY_FUNCTION__, error.localizedDescription);
//        return;
//    }
//    NSLog(@"%s topic: %@, type: %d, content: %@", __PRETTY_FUNCTION__, topic, messageProto.type, messageProto.content);
//    //
//    NSString *decryptedText = aesDecryptString(messageProto.content, AES_KEY);
//    NSLog(@"decrypt content: %@", decryptedText);
}

// 收到新消息
- (void)newMessage:(MQTTSession *)mSession data:(NSData *)data onTopic:(NSString *)topic
               qos:(MQTTQosLevel)qos retained:(BOOL)retained mid:(unsigned int)mid {
    // New message received in topic
    NSLog(@"%s new message, topic:%@, retained:%@, mid:%d", __PRETTY_FUNCTION__, topic, retained?@"true":@"false", mid);
    NSError* error;
    Message *message = [Message parseFromData:data extensionRegistry:nil error:&error];
    if (error) {
        NSLog(@"protobuf parseFromData error");
        return;
    }
    NSLog(@"protobuf type %@, content %@", message.type, message.text.content);
    // 会话
    BDThreadModel *threadModel = [[BDThreadModel alloc] init];
    [threadModel setTid:message.thread.tid];
    [threadModel setType:message.thread.type];
    [threadModel setNickname:message.thread.nickname];
    [threadModel setAvatar:message.thread.avatar];
    [threadModel setContent:message.thread.content];
    [threadModel setTimestamp:message.thread.timestamp];
    [threadModel setTopic:message.thread.topic];
    [threadModel setClient:message.client];
    [threadModel setCurrent_uid:[BDSettings getUid]];
    //
    [threadModel setIs_closed:[NSNumber numberWithBool:FALSE]];
//    [threadModel setUnread_count:[NSNumber numberWithInt:message.thread.unreadCount]];
    [threadModel setIs_mark_unread:[NSNumber numberWithBool:FALSE]];
    [threadModel setIs_mark_top:[NSNumber numberWithBool:FALSE]];
    [threadModel setIs_mark_disturb:[NSNumber numberWithBool:FALSE]];
    [threadModel setIs_mark_deleted:[NSNumber numberWithBool:FALSE]];
    
    // 消息
    BDMessageModel *messageModel = [[BDMessageModel alloc] init];
    [messageModel setMid:message.mid];
    [messageModel setLocal_id:message.mid];
    // topic格式为: 工作组wid/访客uid
    NSArray *array = [threadModel.topic componentsSeparatedByString:@"/"];
    if ([array count] > 0) {
        [messageModel setWid:array[0]];
        [messageModel setVisitor_uid:array[1]];
    }
    //
    [messageModel setCreated_at:message.timestamp];
    [messageModel setType:message.type];
    //
    [messageModel setCid:message.thread.tid];
    [messageModel setGid:message.thread.tid];
    [messageModel setThread_tid:message.thread.tid];
    //
    [messageModel setUid:message.user.uid];
    [messageModel setNickname:message.user.nickname];
    [messageModel setAvatar:message.user.avatar];
    [messageModel setCurrent_uid:[BDSettings getUid]];
    [messageModel setIs_mark_deleted:false];
    [messageModel setClient:message.client];
    [messageModel setStatus:BD_MESSAGE_STATUS_STORED];
    //
    NSString *type = message.type;
    bool sendReceipt = false;
    if ([type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        sendReceipt = true;
        [messageModel setContent:message.text.content];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        sendReceipt = true;
        [messageModel setImage_url:message.image.imageURL];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
       sendReceipt = true;
       [messageModel setVoice_url:message.voice.voiceURL];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
       sendReceipt = true;
       [messageModel setFile_url:message.file.fileURL];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VIDEO] ||
               [type isEqualToString:BD_MESSAGE_TYPE_SHORTVIDEO]) {
        sendReceipt = true;
        [messageModel setVideo_or_short_url:message.video.videoOrShortURL];
     } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW]) {
        // TODO: 消息预知
        [BDNotify notifyMessagePreview:messageModel];
        return;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT]) {
        // 消息回执：送达/已读
        NSString *receiptMid = message.receipt.mid;
        NSString *receiptStatus = message.receipt.status;
        [[BDDBApis sharedInstance] updateMessage:receiptMid withStatus:receiptStatus];
//        [BDNotify notifyReloadCellSuccess:receiptMid];
        [BDNotify notifyReloadCell:receiptMid status:receiptStatus];
        return;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECALL]) {
        // TODO: 消息撤回
        NSString *recallMid = message.recall.mid;
        [[BDDBApis sharedInstance] deleteMessage:recallMid];
        //
        [BDNotify notifyMessageRecall:recallMid];
        return;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER]) {
        // 会话转接
        // 过滤掉自己发送的消息
        if ([[BDSettings getUid] isEqualToString:message.user.uid]) {
           return;
        }
        [BDNotify notifyTransferMessage:messageModel];
    
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT]) {
       // 接受会话转接
        // 过滤掉自己发送的消息
        if ([[BDSettings getUid] isEqualToString:message.user.uid]) {
           return;
        }
        [BDNotify notifyTransferAcceptMessage:messageModel];
        
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT]) {
       // 拒绝会话转接
        // 过滤掉自己发送的消息
        if ([[BDSettings getUid] isEqualToString:message.user.uid]) {
           return;
        }
        [BDNotify notifyTransferRejectMessage:messageModel];
        
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE]) {
       // TODO: 会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT]) {
       // TODO: 接受会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT]) {
       // TODO: 拒绝会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE]) {
        // TODO: 邀请评价
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RATE_RESULT]) {
        // TODO: 评价结果
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO]) {
        
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO]) {
        //
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE] ||
               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL]) {
        // 过滤掉自己发送的消息
        if ([messageModel isSend]) {
            return;
        }
        //
        [messageModel setContent:message.extra.content];
        [BDNotify notifyWebRTCMessage:messageModel];
    } else {
        // TODO: 其他消息类型
        [messageModel setContent:message.text.content];
    }
    
    // 非自己发送 && 非系统通知消息 && 非当前会话
    NSLog(@"%s, getCurrentTid %@, tid %@", __PRETTY_FUNCTION__, [BDSettings getCurrentTid], threadModel.tid);
    if ([messageModel isSend] || [messageModel.type hasPrefix:@"notification_"] || [[BDSettings getCurrentTid] isEqualToString:threadModel.tid]) {
        [threadModel setUnread_count:[NSNumber numberWithInt:0]];
    } else {
        [threadModel setUnread_count:[NSNumber numberWithInt:1]];
    }
    
    // 发送消息回执：送达、已读
    if (sendReceipt && ![messageModel isSend]) {
        [self sendReceiptReceivedMessageProtobufThread:threadModel receiptMid:message.mid];
    }
    
    if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_THREAD] ||
        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY] ||
        //
        [type isEqualToString:BD_MESSAGE_TYPE_TEXT] ||
        [type isEqualToString:BD_MESSAGE_TYPE_IMAGE] ||
        [type isEqualToString:BD_MESSAGE_TYPE_VOICE] ||
        [type isEqualToString:BD_MESSAGE_TYPE_FILE] ||
        [type isEqualToString:BD_MESSAGE_TYPE_VIDEO] ||
        [type isEqualToString:BD_MESSAGE_TYPE_SHORTVIDEO] ||
        
        [type isEqualToString:BD_MESSAGE_TYPE_LOCATION] ||
        [type isEqualToString:BD_MESSAGE_TYPE_LINK] ||
        [type isEqualToString:BD_MESSAGE_TYPE_EVENT] ||
        [type isEqualToString:BD_MESSAGE_TYPE_CUSTOM] ||
        [type isEqualToString:BD_MESSAGE_TYPE_RED_PACKET] ||
        [type isEqualToString:BD_MESSAGE_TYPE_COMMODITY] ||
        
        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE] ||
        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE] ||
        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE]) {
        //
//        NSLog(@"0");
        [[BDDBApis sharedInstance] insertThread:threadModel];
//        if (![messageModel isSend]) {
            [[BDDBApis sharedInstance] insertMessage:messageModel];
//        }
//        NSLog(@"5");
        // 通知界面
        [BDNotify notifyThreadAdd:threadModel];
        // 通知界面
        [BDNotify notifyMessageAdd:messageModel];
    }
    
    // 非自己发送的消息
    if (![messageModel isSend]) {
        // 收到消息震动
        if ([BDSettings shouldVibrateWhenReceiveMessage]) {
            [[BDNotify sharedInstance] backgroundMessageReceivedVibrate];
        }
        
        // 收到消息播放提示音
        if ([BDSettings shouldRingWhenReceiveMessage]) {
            [[BDNotify sharedInstance] playMessageReceivedSound];
        }
    }

}

- (void)subAckReceived:(MQTTSession *)mSession msgID:(UInt16)msgID grantedQoss:(NSArray<NSNumber *> *)qoss {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)unsubAckReceived:(MQTTSession *)mSession msgID:(UInt16)msgID {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}


#pragma mark -

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, keyPath, change);
    
    if (self.mSession.status == 4) {
        [self.mSession connect];
    }
}

#pragma mark - 自定义方法

- (void)subscribeTopic:(NSString *)topic {
    NSLog(@"%s topic:%@", __PRETTY_FUNCTION__, topic);
    
    if ([topic isKindOfClass:[NSNull class]]) {
        NSLog(@"订阅主题不能为NULL");
        return;
    }
    
    if ([topic hasSuffix:@"/"]) {
        NSLog(@"订阅主题不能以/为结尾");
        return;
    }
    
    // TODO: 添加topic到容器，防止重复订阅
    [self.mSession subscribeToTopic:topic
                            atLevel:MQTTQosLevelExactlyOnce
                   subscribeHandler:^(NSError *error, NSArray<NSNumber *> *gQoss) {
        if (error) {
            NSLog(@"Subscription %@ failed %@", topic, error.localizedDescription);
        } else {
            NSLog(@"Subscription %@ sucessfull! Granted Qos: %@", topic, gQoss);
        }
    }];
}

- (void)unsubscribeTopic:(NSString *)topic {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self.mSession unsubscribeTopic:topic
                 unsubscribeHandler:^(NSError *error) {
        if (error) {
            NSLog(@"Unsubscribe topic fail");
        } else {
            NSLog(@"Unsubscribe topic success");
        }
    }];
}


- (void)sendMessageProtobuf:(NSString *)mid
                       type:(NSString *)type
                    content:(NSString *)content
                        tid:(NSString *)tId
                      topic:(NSString *)topic
                 threadType:(NSString *)threadType
             threadNickname:(NSString *)threadNickname
               threadAvatar:(NSString *)threadAvatar
               threadClient:(NSString *)threadClient
                 extraParam:(BDExtraParam *)extraParam {
    //
    NSLog(@"%s, type:%@, content:%@, topic:%@, nickname:%@", __PRETTY_FUNCTION__, type, content, topic, threadNickname);

    // TODO: 增加判断content长度，限制<512
    // 使用AES加密内容
//    NSString *cipherText = [AESCipher aesEncryptString2:content];

    //
    Thread *threadProto = [Thread new];
    [threadProto setTid:tId];
    [threadProto setType:threadType];
    [threadProto setTopic:topic];
    [threadProto setNickname:threadNickname];
    [threadProto setAvatar:threadAvatar];
    [threadProto setClient:threadClient];
    [threadProto setTimestamp:[BDUtils getCurrentDate]];
    [threadProto setUnreadCount:0];
    
    //
    User *userProto = [User new];
    [userProto setUid:[BDSettings getUid]];
    [userProto setUsername:[BDSettings getUsername]];
    [userProto setNickname:[BDSettings getNickname]];
    [userProto setAvatar:[BDSettings getAvatar]];
    //
    NSMutableDictionary *userExtraDict = [NSMutableDictionary new];
    [userExtraDict setObject:[NSNumber numberWithBool:![BDSettings loginAsVisitor]] forKey:@"agent"];
    // Object To JSON string
    NSString *userExtraJsonString = [BDUtils dictToJson:userExtraDict];
    [userProto setExtra:userExtraJsonString];
    
    //
    Message *messageProto = [Message new];
    [messageProto setMid:mid];
    [messageProto setType:type];
    [messageProto setTimestamp:[BDUtils getCurrentDate]];
    [messageProto setClient:CLIENT_MQTT];
    [messageProto setVersion:@"1"];
    [messageProto setEncrypted:false];
    
    if ([type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        //
        [threadProto setContent:content];
        //
        Text *textProto = [Text new];
     // [textProto setContent:cipherText];
        [textProto setContent:content];
        [messageProto setText:textProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        //
        [threadProto setContent:@"[图片]"];
        //
        Image *imageProto = [Image new];
        [imageProto setImageURL:content];
        [messageProto setImage:imageProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
        //
        [threadProto setContent:@"[语音]"];
        //
        Voice *voiceProto = [Voice new];
        [voiceProto setVoiceURL:content];
        [messageProto setVoice:voiceProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
        //
        [threadProto setContent:@"[文件]"];
        //
        File *fileProto = [File new];
        [fileProto setFileURL:content];
        [messageProto setFile:fileProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VIDEO] ||
               [type isEqualToString:BD_MESSAGE_TYPE_SHORTVIDEO]) {
        //
        [threadProto setContent:@"[视频]"];
        //
        Video *videoProto = [Video new];
        [videoProto setVideoOrShortURL:content];
        [messageProto setVideo:videoProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_COMMODITY]) {
       //
       [threadProto setContent:@"[商品]"];
       //
       Text *textProto = [Text new];
       [textProto setContent:content];
       [messageProto setText:textProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW]) {
        //
        Preview *previewProto = [Preview new];
        [previewProto setContent:extraParam.previewContent];
        [messageProto setPreview:previewProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT]) {
        //
        Receipt *receiptProto = [Receipt new];
        [receiptProto setMid:extraParam.receiptMid];
        [receiptProto setStatus:extraParam.receiptStatus];
        [messageProto setReceipt:receiptProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECALL]) {
        //
        Recall *recallProto = [Recall new];
        [recallProto setMid:extraParam.recallMid];
        [messageProto setRecall:recallProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST]) {
        // 表单请求
        [threadProto setContent:@"[表单]"];
        // 发送表单
        Extra *extraProto = [Extra new];
        [extraProto setContent:content];
        [messageProto setExtra:extraProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER]) {
        // 会话转接
        Transfer *transferProto = [Transfer new];
        [transferProto setTopic:[extraParam transferTopic]];
        [transferProto setContent:[extraParam transferContent]];
        [messageProto setTransfer:transferProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT]) {
        // 接受会话转接
        Transfer *transferProto = [Transfer new];
        [transferProto setTopic:[extraParam transferTopic]];
        [transferProto setAccept:true];
        [messageProto setTransfer:transferProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT]) {
        // 拒绝会话转接
        Transfer *transferProto = [Transfer new];
        [transferProto setTopic:[extraParam transferTopic]];
        [transferProto setAccept:false];
        [messageProto setTransfer:transferProto];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE]) {
        // TODO: 会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT]) {
        // TODO: 接受会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT]) {
        // TODO: 拒绝会话邀请
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE]) {
        // 邀请评价
        Extra *extraProto = [Extra new];
        [extraProto setContent:content];
        [messageProto setExtra:extraProto];
    }
    else if ([type hasPrefix:@"notification_webrtc"]) {
        //
        if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO]) {
            //
            [threadProto setContent:@"[邀请视频]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO]) {
            //
            [threadProto setContent:@"[邀请音频]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER] ||
                   [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE]) {
            // 不设置
        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT]) {
            //
            [threadProto setContent:@"[接受邀请]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT]) {
            //
            [threadProto setContent:@"[拒绝邀请]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY]) {
            //
            [threadProto setContent:@"[音视频就绪]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY]) {
            //
            [threadProto setContent:@"[忙线中]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE]) {
            //
            [threadProto setContent:@"[结束音视频]"];

        } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL]) {
            //
            [threadProto setContent:@"[取消音视频]"];
        }
        //
        Extra *extraProto = [Extra new];
        [extraProto setContent:content];
        [messageProto setExtra:extraProto];
        //
    }
    else {
        // TODO: 其他消息类型
        Text *textProto = [Text new];
        [textProto setContent:content];
        [messageProto setText:textProto];
    }
    //
    [messageProto setUser:userProto];
    [messageProto setThread:threadProto];
    //
    [self.mSession publishData:[messageProto data]
                       onTopic:topic
                        retain:YES
                           qos:MQTTQosLevelAtLeastOnce
                publishHandler:^(NSError *error) {
                    NSLog(@"%s error: %@", __PRETTY_FUNCTION__, error);
                }];
}

- (void)sendMessageProtobuf:(NSString *)mid type:(NSString *)type content:(NSString *)content thread:(BDThreadModel *)thread extraParam:(BDExtraParam *)extraParam {
    [self sendMessageProtobuf:mid type:type content:content tid:thread.tid topic:thread.topic threadType:thread.type threadNickname:thread.nickname threadAvatar:thread.avatar threadClient:thread.client extraParam:extraParam];
}

- (void)sendTextMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_TEXT content:content thread:thread extraParam:NULL];
}

- (void)sendImageMessageProtobuf:(NSString *)mid content:(NSString *)imageUrl thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_IMAGE content:imageUrl thread:thread extraParam:NULL];
}

- (void)sendVoiceMessageProtobuf:(NSString *)mid content:(NSString *)voiceUrl thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_VOICE content:voiceUrl thread:thread extraParam:NULL];
}

- (void)sendFileMessageProtobuf:(NSString *)mid content:(NSString *)fileUrl thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_FILE content:fileUrl thread:thread extraParam:NULL];
}

- (void)sendVideoMessageProtobuf:(NSString *)mid content:(NSString *)videoUrl thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_VIDEO content:videoUrl thread:thread extraParam:NULL];
}

- (void)sendCommodityMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_COMMODITY content:content thread:thread extraParam:NULL];
}

- (void)sendInviteRateMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE content:content thread:thread extraParam:NULL];
}

- (void)sendFormRequestMessageProtobuf:(NSString *)mid content:(NSString *)content thread:(BDThreadModel *)thread {
    [self sendMessageProtobuf:mid type:BD_MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST content:content thread:thread extraParam:NULL];
}

- (void)sendPreviewMessageProtobufThread:(BDThreadModel *)thread previewContent:(NSString *)previewContent {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setPreviewContent:previewContent];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW content:@"content" thread:thread extraParam:extraParam];
}

- (void)sendRecallMessageProtobufThread:(BDThreadModel *)thread recallMid:(NSString *)recallMid {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setRecallMid:recallMid];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_RECALL content:@"content" thread:thread extraParam:extraParam];
}

- (void)sendReceiptReceivedMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid {
    [self sendReceiptMessageProtobufThread:thread receiptMid:receiptMid receiptStatus:BD_MESSAGE_STATUS_RECEIVED];
}

- (void)sendReceiptReadMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid {
    [self sendReceiptMessageProtobufThread:thread receiptMid:receiptMid receiptStatus:BD_MESSAGE_STATUS_READ];
}

- (void)sendReceiptMessageProtobufThread:(BDThreadModel *)thread receiptMid:(NSString *)receiptMid receiptStatus:(NSString *)receiptStatus {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setReceiptMid:receiptMid];
    [extraParam setReceiptStatus:receiptStatus];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT content:@"content" thread:thread extraParam:extraParam];
}

- (void)sendTransferMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic transferContent:(NSString *)transferContent {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setTransferTopic:transferTopic];
    [extraParam setTransferContent:transferContent];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER content:@"content" thread:thread extraParam:extraParam];
}

- (void)sendTransferAcceptMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setTransferTopic:transferTopic];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT content:@"content" thread:thread extraParam:extraParam];
}

- (void)sendTransferRejectMessageProtobufThread:(BDThreadModel *)thread transferTopic:(NSString *)transferTopic {
    BDExtraParam *extraParam = [[BDExtraParam alloc] init];
    [extraParam setTransferTopic:transferTopic];
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT content:@"content" thread:thread extraParam:extraParam];
}

// webrtc
- (void)sendWebRTCOfferVideoSDP:(NSString *)content thread:(BDThreadModel *)threadModel {
    //
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO content:content thread:threadModel extraParam:NULL];
}

- (void)sendWebRTCAnswerSDP:(NSString *)content thread:(BDThreadModel *)threadModel {
    //
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER content:content thread:threadModel extraParam:NULL];
}

- (void)sendWebRTCICECandidate:(NSString *)content thread:(BDThreadModel *)threadModel {
    //
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE content:content thread:threadModel extraParam:NULL];
}

- (void)sendWebRTCCancel:(BDThreadModel *)threadModel {
    //
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL content:@"取消音视频会话" thread:threadModel extraParam:NULL];
}

- (void)sendWebRTCClose:(BDThreadModel *)threadModel {
    //
    [self sendMessageProtobuf:[[NSUUID UUID] UUIDString] type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE content:@"结束音视频会话" thread:threadModel extraParam:NULL];
}


- (void)disconnect {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    // 通知服务器在线状态：要用disconnect，不要用logout
//    [self setStatus:BD_USER_STATUS_DISCONNECTED];

    #pragma mark - todo 设置状态
    
//    [self.mSession unsubscribeTopics:self.topicUrls unsubscribeHandler:^(NSError *error) {
//        [self.topicUrls removeAllObjects];
//    }]; // 取消订阅所有topic
    
    self.mConnected = FALSE;
    //
    [self.mSession disconnect];
    self.mSession = nil;
}


- (BOOL)isConnected {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    return self.mConnected;
}


@end


//    //
//    NSError* error;
//    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
//    if (error) {
//        NSLog(@"parse json message error: %s %@", __PRETTY_FUNCTION__, error.localizedDescription);
//        return;
//    }
//    // 判断是否为空
//    if ([dict objectForKey:@"user"] == nil) {
//        // TODO: 回执：{"mid":"201907181533292","thread":{"tid":"201907181518231"},"type":"notification_receipt","status":"received"}
//        NSLog(@"new message user should not be null");
//        return;
//    }
//    //
//    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:dict];
//    NSString *type = dict[@"type"];
//
//    // 消息回执：送达
////    {"mid":"201903302031511","type":"notification_receipt","cid":"201808221551198","status":"received"}
//
//    // TODO: 消息回执和消息预知 没有id字段, 暂不处理此类型，后续处理
////    if (
////        // TODO: 对于访客端暂时忽略连接状态信息
//////        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_CONNECT] ||
//////        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_DISCONNECT] ||
////        // 浏览相关类型暂不处理
////        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_START] ||
////        [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_END]) {
////        return;
////    }
//
//    //
//    if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_QUEUE]) {
//
//        // TODO: queue持久化
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_QUEUE_ACCEPT]) {
//
//        // 消息持久化
//        [[BDDBApis sharedInstance] insertMessage:messageModel];
//        // 通知界面
//        [BDNotify notifyMessageAdd:messageModel];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_THREAD]) {
//
//        NSDictionary *threadDict = [dict objectForKey:@"thread"];
//        BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
//        [[BDDBApis sharedInstance] insertThread:threadModel];
//
//        //
//        NSString *threadTopic = [NSString stringWithFormat:@"thread/%@", threadModel.tid];
//        [self subscribeTopic:threadTopic];
//
//        // 通知界面
//        [BDNotify notifyThreadAdd:threadModel];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_TEXT] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_IMAGE] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_VOICE] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_FILE] ||
//
//               [type isEqualToString:BD_MESSAGE_TYPE_VIDEO] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_SHORTVIDEO] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_LOCATION] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_LINK] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_EVENT] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_CUSTOM] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_RED_PACKET] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_COMMODITY]) {
//
//        //
//        NSDictionary *threadDict = [dict objectForKey:@"thread"];
//        BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
//        [[BDDBApis sharedInstance] insertThread:threadModel];
//
//        if (![messageModel.uid isEqualToString:[BDSettings getUid]]) {
//            // 消息持久化
//            [[BDDBApis sharedInstance] insertMessage:messageModel];
//            // 消息回执
//            [self sendReceiptReceivedMessage:messageModel.mid threadTid:threadModel.tid];
//        }
//
//        //
////        if ([threadModel.type isEqualToString:BD_THREAD_TYPE_THREAD]) {
////
////            // TODO: 通知服务器对方已经收到消息，针对客服会话和单聊，暂对群聊无效
////            // 过滤掉自己发送的消息
////            if (![messageModel.uid isEqualToString:[BDSettings getUid]]) {
////                [self sendReceiptReceivedMessage:messageModel.mid];
////            }
////
////        } else if ([threadModel.type isEqualToString:BD_THREAD_TYPE_CONTACT]) {
////
////            // TODO: 通知服务器对方已经收到消息，针对客服会话和单聊，暂对群聊无效
////            // 过滤掉自己发送的消息
////            if (![messageModel.uid isEqualToString:[BDSettings getUid]]) {
////                [self sendReceiptReceivedMessage:messageModel.mid];
////            }
////
////        } else if ([threadModel.type isEqualToString:BD_THREAD_TYPE_GROUP]) {
////            //
////        }
//
//        // 通知界面
//        [BDNotify notifyThreadAdd:threadModel];
//
//        // 通知界面
//        [BDNotify notifyMessageAdd:messageModel];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_CONNECT] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_DISCONNECT]) {
//
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE]) {
//
//        // 消息持久化
//        [[BDDBApis sharedInstance] insertMessage:messageModel];
//        // 通知界面
//        [BDNotify notifyMessageAdd:messageModel];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_ACCEPT_STATUS]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_START]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_END]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_ACCEPT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_BROWSE_INVITE_REJECT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW]) {
//        // {"mid":"201908241850174","client":"web_admin","thread":{"tid":"201908241849491"},"type":"notification_preview",
//        // "user":{"visitor":false,"uid":"201808221551193","username":"admin@test.com"},"content":"22222"}
//        NSString *uid = dict[@"user"][@"uid"];
//        if (![uid isEqualToString:[BDSettings getUid]]) {
//            [BDNotify notifyMessagePreview:dict];
//        }
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT]) {
//
//        // message:{"mid":"d6d90205-3c87-4842-ac47-cfb163068bbc","type":"notification_receipt",
//        //          "user":{"uid":"201908241551572"},"status":"read"}
//        NSString *mid = dict[@"mid"];
//        NSString *status = dict[@"status"];
//        // 修改本地消息发送状态为成功
//        [[BDDBApis sharedInstance] updateMessage:mid withStatus:status];
//        [BDNotify notifyReloadCell:mid status:status];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECALL]) {
//        //
//        NSString *mid = dict[@"mid"];
//        [[BDDBApis sharedInstance] deleteMessage:mid];
//        // 通知界面更新
//        [BDNotify notifyMessageRecall:mid];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_KICKOFF]) {
//        //
//        NSString *clientId = messageModel.client;
//        NSArray *list = [clientId componentsSeparatedByString:@"/"];
//        //
//        if ([list count] < 3) {
//            NSLog(@"clientId %@ 格式错误", clientId);
//            return;
//        }
//        NSString *username = list[0];
//        NSString *client = list[1];
//        NSString *uuid = list[2];
//        //
//        NSLog(@"username: %@, client: %@, uuid:%@", username, client, uuid);
//        if (![clientId isEqualToString:self.mClientId]) {
//            NSString *content;
//            if ([client hasPrefix:@"ios"]) {
//                content = @"账号在其他iOS客户端登录";
//                // 广播通知
//                [BDNotify notifyKickoff:content];
//            } else if ([client hasPrefix:@"android"]) {
//                content = @"账号在其他安卓客户端登录";
//                // 广播通知
//                [BDNotify notifyKickoff:content];
//            } else {
//                content = @"账号在其他客户端登录";
//            }
//
//        } else {
//            NSLog(@"当前登录客户端");
//        }
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_CREATE] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_INVITE]) {
//
//        // 1. 持久化会话
//        NSDictionary *threadDict = [dict objectForKey:@"thread"];
//        BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
//        [[BDDBApis sharedInstance] insertThread:threadModel];
//
//        // 2. 持久化群组
//        NSDictionary *groupDict = [threadDict objectForKey:@"group"];
//        BDGroupModel *groupModel = [[BDGroupModel alloc] initWithDictionary:groupDict];
//        [[BDDBApis sharedInstance] insertGroup:groupModel];
//
//        // 3. 订阅群组
//        NSString *groupTopic = [NSString stringWithFormat:@"group/%@", groupModel.gid];
//        [self subscribeTopic:groupTopic];
//
//        // 4. 通知界面
//        [BDNotify notifyThreadAdd:threadModel];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_UPDATE]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_APPROVE]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_APPLY_DENY]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_KICK] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_DISMISS]) {
//
//        // TODO: 判断是否自己被踢
//
//        // TODO: 删除群组
//
//        // TODO: 取消订阅群组消息
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_MUTE]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_ACCEPT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_TRANSFER_REJECT]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_GROUP_WITHDRAW]) {
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO] ||
//               [type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO]) {
//        NSLog(@"视频邀请");
//
//        [BDNotify notifyWebRTCInvite:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL]) {
//
//        [BDNotify notifyWebRTCCancel:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO]) {
//
//        [BDNotify notifyWebRTCOfferVideo:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO]) {
//
//        [BDNotify notifyWebRTCOfferAudio:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER]) {
//
//        [BDNotify notifyWebRTCAnswer:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE]) {
//
//        [BDNotify notifyWebRTCCandidate:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT]) {
//
//        [BDNotify notifyWebRTCAccept:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT]) {
//
//        [BDNotify notifyWebRTCReject:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_READY]) {
//
//        [BDNotify notifyWebRTCReady:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_BUSY]) {
//
//        [BDNotify notifyWebRTCBusy:dict];
//
//    } else if ([type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE]) {
//
//        [BDNotify notifyWebRTCClose:dict];
//
//    } else {
//        NSLog(@"%s other message type", __PRETTY_FUNCTION__);
//    }
    
// 通知服务器在线状态
//    [self setStatus:BD_USER_STATUS_CONNECTED];
//    // 添加订阅
//    // 1. 订阅subDomain
//    NSString *subDomainTopic = [NSString stringWithFormat:@"subDomain/%@", [BDSettings getSubdomain]];
//    [self subscribeTopic:subDomainTopic];
//    // 2. 订阅user
//    NSString *userTopic = [NSString stringWithFormat:@"user/%@", [BDSettings getUid]];
//    [self subscribeTopic:userTopic];
//    // 3. 订阅contact
//    NSString *contactTopic = [NSString stringWithFormat:@"contact/%@", [BDSettings getUid]];
//    [self subscribeTopic:contactTopic];
//    // 4. 订阅workGroup
//    NSArray *workGroupArray = [[BDDBApis sharedInstance] getWorkGroups];
//    for (BDWorkGroupModel *workGroup in workGroupArray) {
//        NSString *workGroupTopic = [NSString stringWithFormat:@"workGroup/%@",  workGroup.wid];
//        [self subscribeTopic:workGroupTopic];
//    }
//    // 5. 订阅thread
//    NSArray *threadArray = [[BDDBApis sharedInstance] getThreads];
//    for (BDThreadModel *thread in threadArray) {
//        NSString *threadTopic = [NSString stringWithFormat:@"thread/%@", thread.tid];
//        [self subscribeTopic:threadTopic];
//    }
//    // 6. 订阅group
//    NSArray *groupArray = [[BDDBApis sharedInstance] getGroups];
//    for (BDGroupModel *group in groupArray) {
//        NSString *groupTopic = [NSString stringWithFormat:@"group/%@", group.gid];
//        [self subscribeTopic:groupTopic];
//    }

//- (void)sendTextMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype {
//    [self sendMessage:content type:BD_MESSAGE_TYPE_TEXT toTid:tId localId:localId sessionType:sessiontype];
//}
//
//- (void)sendImageMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype{
//    [self sendMessage:content type:BD_MESSAGE_TYPE_IMAGE toTid:tId localId:localId sessionType:sessiontype];
//}
//
//- (void)sendVoiceMessage:(NSString *)content toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype{
//    [self sendMessage:content type:BD_MESSAGE_TYPE_VOICE toTid:tId localId:localId sessionType:sessiontype];
//}
//
//- (void)sendPreviewMessage:(NSString *)content toTid:(NSString *)tId sessionType:(NSString *)sessiontype {
//    [self sendMessage:content type:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW toTid:tId localId:[[NSUUID UUID] UUIDString] sessionType:sessiontype];
//}

//- (void)sendWebRTCInviteVideoMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO sessionType:sessionType content:@"邀请视频"];
//}
//
//
//- (void)sendWebRTCInviteAudioMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_AUDIO sessionType:sessionType content:@"邀请音频"];
//}
//
//
//- (void)sendWebRTCOfferVideoSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_VIDEO sessionType:sessionType content:content];
//}
//
//
//- (void)sendWebRTCOfferAudioSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_OFFER_AUDIO sessionType:sessionType content:content];
//}
//
//
//- (void)sendWebRTCAnswerSDP:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ANSWER sessionType:sessionType content:content];
//}
//
//
//- (void)sendWebRTCICECandidate:(NSString *)uid sessionType:(NSString *)sessionType content:(NSString *)content {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANDIDATE sessionType:sessionType content:content];
//}
//
//
//- (void)sendWebRTCCancelMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CANCEL sessionType:sessionType content:@"取消音视频"];
//}
//
//
//- (void)sendWebRTCAcceptMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_ACCEPT sessionType:sessionType content:@"接受音视频"];
//}
//
//
//- (void)sendWebRTCRejectMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_REJECT sessionType:sessionType content:@"拒绝音视频"];
//}
//
//
//- (void)sendWebRTCCloseMessage:(NSString *)uid sessionType:(NSString *)sessionType {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uid type:BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_CLOSE sessionType:sessionType content:@"结束音视频"];
//}
//
//- (void)sendWebRTCGroupMessage:(NSString *)uuid type:(NSString *)type {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self sendWebRTCMessage:uuid type:type sessionType:BD_MESSAGE_SESSION_TYPE_GROUP content:@""];
//}

//- (void)sendWebRTCMessage:(NSString *)uuid type:(NSString *)type sessionType:(NSString *)sessionType content:(NSString *)content{
//    NSLog(@"%s uuid:%@, type:%@, sessionType:%@", __PRETTY_FUNCTION__, uuid, type, sessionType);
//    if (uuid == nil) {
//        NSLog(@"uuid is nil");
//        return;
//    }
////    //
////    NSMutableDictionary *messageDict = [NSMutableDictionary new];
////    [messageDict setObject:uuid forKey:@"tid"];
////    [messageDict setObject:self.mClientId forKey:@"clientId"];
////    [messageDict setObject:[[NSUUID UUID] UUIDString] forKey:@"localId"];
////    [messageDict setObject:uuid forKey:@"uuid"];
////    [messageDict setObject:type forKey:@"type"];
////    [messageDict setObject:sessionType forKey:@"sessionType"];
////    [messageDict setObject:content forKey:@"content"];
////    [messageDict setObject:@"1" forKey:@"version"];
////    //
////    NSString *jsonMessageString = [BDUtils dictToJson:messageDict];
////    //
////    [self.mSession publishData:[jsonMessageString dataUsingEncoding:NSUTF8StringEncoding]
////                       onTopic:BD_MQTT_TOPIC_WEBRTC
////                        retain:YES
////                           qos:MQTTQosLevelAtLeastOnce
////                publishHandler:^(NSError *error) {
////                    NSLog(@"%s error: %@", __PRETTY_FUNCTION__, error);
////                }];
//}

//- (void)sendMessage:(NSString *)content type:(NSString *)type toTid:(NSString *)tId localId:(NSString *)localId sessionType:(NSString *)sessiontype {
//    NSLog(@"%s, content:%@, topic:%@", __PRETTY_FUNCTION__, content, tId);
////    //
////    NSMutableDictionary *messageDict = [NSMutableDictionary new];
////    [messageDict setObject:localId forKey:@"mid"];
////    [messageDict setObject:tId forKey:@"tid"];
////    [messageDict setObject:type forKey:@"type"];
////    [messageDict setObject:BD_CLIENT_IOS forKey:@"client"];
////    [messageDict setObject:content forKey:@"content"];
////    [messageDict setObject:self.mClientId forKey:@"clientId"];
////    [messageDict setObject:BD_MESSAGE_STATUS_SENDING forKey:@"status"];
////    [messageDict setObject:localId forKey:@"localId"];
////    [messageDict setObject:sessiontype forKey:@"sessionType"];
////    [messageDict setObject:@"2" forKey:@"version"];
////
////    // Object To JSON string
////    NSString *jsonMessageString = [BDUtils dictToJson:messageDict];
////    //
////    [self.mSession publishData:[jsonMessageString dataUsingEncoding:NSUTF8StringEncoding]
////                       onTopic:BD_MQTT_TOPIC_MESSAGE
////                        retain:YES
////                           qos:MQTTQosLevelAtLeastOnce
////                publishHandler:^(NSError *error) {
////                    NSLog(@"%s error: %@", __PRETTY_FUNCTION__, error);
////                }];
//}
//
//- (void)sendReceiptReceivedMessage:(NSString *)mId threadTid:(NSString *)tId {
//    [self sendReceiptMessage:mId threadTid:tId status:BD_MESSAGE_STATUS_RECEIVED];
//}
//
//- (void)sendReceiptReadMessage:(NSString *)mId threadTid:(NSString *)tId {
//    [self sendReceiptMessage:mId threadTid:tId status:BD_MESSAGE_STATUS_READ];
//}
//
//- (void)sendReceiptDestroyedMessage:(NSString *)mId threadTid:(NSString *)tId {
//    [self sendReceiptMessage:mId threadTid:tId status:BD_MESSAGE_STATUS_DESTROYED];
//}
//
//- (void)sendReceiptMessage:(NSString *)mId threadTid:(NSString *)tId status:(NSString *)status {
//    //
////    NSMutableDictionary *messageDict = [NSMutableDictionary new];
////    [messageDict setObject:mId forKey:@"mid"];
////    [messageDict setObject:tId forKey:@"tid"];
////    [messageDict setObject:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT forKey:@"type"];
////    [messageDict setObject:BD_CLIENT_IOS forKey:@"client"];
////    // 注意此处mid放在content中
////    [messageDict setObject:mId forKey:@"content"];
////    [messageDict setObject:status forKey:@"status"];
////    [messageDict setObject:self.mClientId forKey:@"clientId"];
////    [messageDict setObject:@"2" forKey:@"version"];
////    //
////    NSString *jsonMessageString = [BDUtils dictToJson:messageDict];
////    //
////    [self.mSession publishData:[jsonMessageString dataUsingEncoding:NSUTF8StringEncoding]
////                       onTopic:BD_MQTT_TOPIC_RECEIPT
////                        retain:YES
////                           qos:MQTTQosLevelAtLeastOnce
////                publishHandler:^(NSError *error) {
////                    NSLog(@"%s error: %@", __PRETTY_FUNCTION__, error);
////                }];
//}


//- (void)setStatus:(NSString *)status {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//    if (self.mClientId == nil) {
//        return;
//    }
////    //
////    NSMutableDictionary *statusDict = [NSMutableDictionary new];
////    [statusDict setObject:BD_MESSAGE_TYPE_NOTIFICATION_ONLINE_STATUS forKey:@"type"];
////    [statusDict setObject:BD_CLIENT_IOS forKey:@"client"];
////    [statusDict setObject:status forKey:@"status"];
////    [statusDict setObject:self.mClientId forKey:@"clientId"];
////    [statusDict setObject:@"2" forKey:@"version"];
////
////    // Object To JSON string
////    NSString *jsonStatusString = [BDUtils dictToJson:statusDict];
////    //
////    [self.mSession publishData:[jsonStatusString dataUsingEncoding:NSUTF8StringEncoding]
////                      onTopic:BD_MQTT_TOPIC_STATUS
////                       retain:YES
////                          qos:MQTTQosLevelAtLeastOnce
////                publishHandler:^(NSError *error) {
////                    NSLog(@"%s", __PRETTY_FUNCTION__);
////                }];
//}













