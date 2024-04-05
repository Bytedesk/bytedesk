//
//  KFDSDBApis.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/18.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BDThreadModel;
@class BDQueueModel;
@class BDMessageModel;
@class BDGroupModel;
@class BDWorkGroupModel;
@class BDContactModel;

//typedef void (^SuccessCallbackBlock)(NSDictionary *dict);
//typedef void (^FailedCallbackBlock)(NSError *error);

@interface BDDBApis : NSObject

+ (BDDBApis *)sharedInstance;

#pragma mark - 访客端接口


#pragma mark - 客服端接口

/**
 <#Description#>

 @param thread <#thread description#>
 @return <#return value description#>
 */
- (BOOL) insertThread:(BDThreadModel *)thread;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) deleteThread:(NSString *)tId;

/**
 为保证会话列表中同一个访客、联系人、群组，只保留一条记录

 @param uId <#uId description#>
 @return <#return value description#>
 */
- (BOOL) deleteThreadUser:(NSString *)uId;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSMutableArray *) getThreads;

- (NSMutableArray *) getTopThreads;

- (NSMutableArray *) getRecentThreads;

- (NSMutableArray *) getIMThreads;

- (NSMutableArray *) getOnGoingThreads;

- (NSMutableArray *) getHistoryThreads;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearThreads;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) clearThreadUnreadCount:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) markTopThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) unmarkTopThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) markDisturbThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) unmarkDisturbThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) markUnreadThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) unmarkUnreadThread:(NSString *)tId;

/**
 <#Description#>

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) markDeletedThread:(NSString *)tId;

/**
 <#Description#>

 @param mId <#mId description#>
 @return <#return value description#>
 */
- (BOOL) markDeletedMessage:(NSString *)mId;
/**
 TODO: 根据tid标记所有消息已删除

 @param tId <#tId description#>
 @return <#return value description#>
 */
- (BOOL) markClearMessage:(NSString *)tId;

/**
 <#Description#>

 @param queue <#queue description#>
 @return <#return value description#>
 */
- (BOOL) insertQueue:(BDQueueModel *)queue;

/**
 <#Description#>

 @param qId <#qId description#>
 @return <#return value description#>
 */
- (BOOL) deleteQueue:(NSString *)qId;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSMutableArray *) getQueues;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSNumber *) getQueueCount;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearQueues;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertTextMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                   withSend:(BOOL)isSend;

- (BDMessageModel *) insertRobotMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertImageMessageLocal:(NSString *)tid
                            withWorkGroupWid:(NSString *)wid
                                 withContent:(NSString *)content
                                 withLocalId:(NSString *)localId
                             withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @param voiceLength <#voiceLength description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertVoiceMessageLocal:(NSString *)tid
                            withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                 withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                             withVoiceLength:(int)voiceLength
                                  withFormat:format
                                    withSend:(BOOL)isSend;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @param fileName <#fileName description#>
 @param fileSize <#fileSize description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertFileMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                 withFormat:format
                               withFileName:(NSString *)fileName
                               withFileSize:(NSString *)fileSize
                                   withSend:(BOOL)isSend;


/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertVideoMessageLocal:(NSString *)tid
                            withWorkGroupWid:(NSString *)wid
                                 withContent:(NSString *)content
                                 withLocalId:(NSString *)localId
                             withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertCommodityMessageLocal:(NSString *)tid
                                withWorkGroupWid:(NSString *)wid
                                     withContent:(NSString *)content
                                     withLocalId:(NSString *)localId
                                 withSessionType:(NSString *)sessionType
                                        withSend:(BOOL)isSend;

/**
 <#Description#>

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertRedPacketMessageLocal:(NSString *)tid
                                withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                     withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                        withSend:(BOOL)isSend;


/**
 插入消息到本地
  TODO: 加频率限制，每秒1条

 @param tid <#tid description#>
 @param wid <#wid description#>
 @param content <#content description#>
 @param localId <#localId description#>
 @param type <#type description#>
 @param sessionType <#sessionType description#>
 @param voiceLength <#voiceLength description#>
 @param format <#format description#>
 @param fileName <#fileName description#>
 @param fileSize <#fileSize description#>
 @return <#return value description#>
 */
- (BDMessageModel *) insertMessageLocal:(NSString *)tid
                       withWorkGroupWid:(NSString *)wid
                            withContent:(NSString *)content
                            withLocalId:(NSString *)localId
                               withType:(NSString *)type
                        withSessionType:(NSString *)sessionType
                        withVoiceLength:(int)voiceLength
                             withFormat:(NSString *)format
                           withFileName:(NSString *)fileName
                           withFileSize:(NSString *)fileSize
                               withSend:(BOOL)isSend;

/**
  TODO: 加频率限制，每秒1条

 @param message <#message description#>
 @return <#return value description#>
 */
- (BOOL) insertMessage:(BDMessageModel *)message;

/**
  TODO: 加频率限制，每秒1条

 @param message <#message description#>
 @return <#return value description#>
 */
- (BOOL) insertMessageLocal:(BDMessageModel *)message;

/**
 <#Description#>

 @param type <#type description#>
 @param uid <#uid description#>
 @return <#return value description#>
 */
- (NSMutableArray *)getMessagesWithType:(NSString *)type withUid:(NSString *)uid;

- (void)clearMessagesWithType:(NSString *)type withUid:(NSString *)uid;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param type <#type description#>
 @param uid <#uid description#>
 @return <#return value description#>
 */
- (NSMutableArray *)getMessagesPage:(NSUInteger)page withSize:(NSUInteger)size withType:(NSString *)type withUid:(NSString *)uid;

/**
 <#Description#>

 @param localId <#localId description#>
 @param serverId <#serverId description#>
 @param mid <#mid description#>
 @param status <#status description#>
 @return <#return value description#>
 */
- (BOOL) updateMessage:(NSString *)localId withServerId:(NSNumber *)serverId withMid:(NSString *)mid withStatus:(NSString *)status;

/**
 <#Description#>

 @param localId <#localId description#>
 @return <#return value description#>
 */
- (BOOL) updateMessageSuccess:(NSString *)localId;

/**
 <#Description#>

 @param localId <#localId description#>
 @return <#return value description#>
 */
- (BOOL) updateMessageError:(NSString *)localId;

/**
 <#Description#>

 @param localId <#localId description#>
 @param status <#status description#>
 @return <#return value description#>
 */
- (BOOL) updateMessage:(NSString *)localId withStatus:(NSString *)status;

/**
 <#Description#>

 @param mId <#mId description#>
 @param localFilePath <#localFilePath description#>
 @return <#return value description#>
 */
- (BOOL) updateMessage:(NSString *)mId withLocalFilePath:(NSString *)localFilePath;

/**
 <#Description#>

 @param localId <#localId description#>
 @param localFilePath <#localFilePath description#>
 @return <#return value description#>
 */
- (BOOL) updateMessageLocal:(NSString *)localId withLocalFilePath:(NSString *)localFilePath;

/**
 <#Description#>

 @param mid <#mid description#>
 @return <#return value description#>
 */
- (BOOL) deleteMessage:(NSString *)mid;

/**
 <#Description#>

 @param tid <#tid description#>
 @return <#return value description#>
 */
- (BOOL) deleteThreadMessages:(NSString *)tid;

/**
 <#Description#>

 @param uid <#uid description#>
 @return <#return value description#>
 */
- (BOOL) deleteContactMessages:(NSString *)uid;

/**
 <#Description#>

 @param gid <#gid description#>
 @return <#return value description#>
 */
- (BOOL) deleteGroupMessages:(NSString *)gid;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearMessages;
/**
 <#Description#>

 @param contact <#contact description#>
 @return <#return value description#>
 */
- (BOOL) insertContact:(BDContactModel *)contact;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSMutableArray *) getContacts;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearContacts;

/**
 <#Description#>

 @param group <#group description#>
 @return <#return value description#>
 */
- (BOOL) insertGroup:(BDGroupModel *)group;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSMutableArray *) getGroups;

/**
 更新会话列表群组昵称

 @param gid <#gid description#>
 @param nickname <#nickname description#>
 @return <#return value description#>
 */
- (BOOL) updateThreadGroupNickname:(NSString *)gid withNickname:(NSString *)nickname;

/**
 更新会话列表用户昵称

 @param uid <#uid description#>
 @param nickname <#nickname description#>
 @return <#return value description#>
 */
- (BOOL) updateThreadUserNickname:(NSString *)uid withNickname:(NSString *)nickname;

/**
 更新群组本地昵称

 @param gid <#gid description#>
 @param nickname <#nickname description#>
 @return <#return value description#>
 */
- (BOOL) updateGroupNickname:(NSString *)gid withNickname:(NSString *)nickname;

/**
 更新联系人本地昵称

 @param gid <#gid description#>
 @param nickname <#nickname description#>
 @return <#return value description#>
 */
- (BOOL) updateContactNickname:(NSString *)gid withNickname:(NSString *)nickname;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearGroups;



/**
 <#Description#>

 @param workGroup <#workGroup description#>
 @return <#return value description#>
 */
- (BOOL) insertWorkGroup:(BDWorkGroupModel *)workGroup;

/**
 <#Description#>

 @return <#return value description#>
 */
- (NSMutableArray *) getWorkGroups;

/**
 <#Description#>

 @return <#return value description#>
 */
- (BOOL) clearWorkGroups;

#pragma mark - 公共接口

/**
 <#Description#>
 */
- (void) clearAll;



@end
