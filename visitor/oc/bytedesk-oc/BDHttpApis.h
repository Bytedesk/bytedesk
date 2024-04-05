//
//  KFDSHttpApis.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/18.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^SuccessCallbackBlock)(NSDictionary *dict);
typedef void (^FailedCallbackBlock)(NSError *error);

@interface BDHttpApis : NSObject

+ (BDHttpApis *)sharedInstance;

#pragma mark - Bytedesk.com接口

#pragma mark - 访客端接口

/**
 从服务器请求用户名
 */
- (void)registerAnonymousUserWithAppkey:(NSString *)appkey
                            withSubdomain:(NSString *)subdomain
                            resultSuccess:(SuccessCallbackBlock)success
                             resultFailed:(FailedCallbackBlock)failed;

/**
注册自定义普通用户：用于IM

 @param username <#username description#>
 @param nickname <#nickname description#>
 @param password <#password description#>
 @param subDomain <#subDomain description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)registerUser:(NSString *)username
           withNickname:(NSString *)nickname
           withPassword:(NSString *)password
          withSubDomain:(NSString *)subDomain
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
注册自定义普通用户：用于IM

 @param username <#username description#>
 @param nickname <#nickname description#>
 @param avatar 头像
 @param password <#password description#>
 @param subDomain <#subDomain description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)registerUser:(NSString *)username
           withNickname:(NSString *)nickname
             withAvatar:(NSString *)avatar
           withPassword:(NSString *)password
          withSubDomain:(NSString *)subDomain
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 普通用户注册接口，自定义subDomain
 
 @param email 邮箱
 @param nickname 昵称
 @param password 密码
 @param subDomain 企业号，主要用于多租户平台，如SaaS；其他情况可写死为 ‘vip’
 @param success 成功回调
 @param failed 失败回调
 */
- (void)registerEmail:(NSString *)email
         withNickname:(NSString *)nickname
         withPassword:(NSString *)password
        withSubDomain:(NSString *)subDomain
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;


/**
 注册自定义普通用户, 自定义uid

 @param username <#username description#>
 @param nickname <#nickname description#>
 @param uid <#uid description#>
 @param password <#password description#>
 @param subDomain <#subDomain description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
             withUid:(NSString *)uid
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 注册管理员账号，客服管理员

 @param email <#email description#>
 @param password <#password description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)registerAdmin:(NSString *)email
         withPassword:(NSString *)password
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 手机号注册
/// @param mobile <#mobile description#>
/// @param email <#email description#>
/// @param nickname <#nickname description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)registerMobile:(NSString *)mobile
             withEmail:(NSString *)email
          withNickname:(NSString *)nickname
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 绑定手机号
/// @param mobile <#mobile description#>
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)bindMobile:(NSString *)mobile
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/// 判断微信号是否曾经登录 或者 被绑定过
/// @param unionId <#unionId description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)isWeChatRegistered:(NSString *)unionId
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 微信登录之后注册微信用户信息到自有用户系统

 @param unionid <#unionid description#>
 @param openid <#openid description#>
 @param nickname <#nickname description#>
 @param avatar <#avatar description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)registerWeChat:(NSString *)unionid
             withEmail:(NSString *)email
            withOpenId:(NSString *)openid
          withNickname:(NSString *)nickname
            withAvatar:(NSString *)avatar
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/// 绑定微信
/// @param unionid  <#mobile description#>
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)bindWeChat:(NSString *)unionid
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/// @param mobile <#mobile description#>
/// @param nickname <#nickname description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
//- (void)uploadMobile:(NSString *)mobile
//        withNickname:(NSString *)nickname
//       resultSuccess:(SuccessCallbackBlock)success
//        resultFailed:(FailedCallbackBlock)failed;

/// 用户名密码登录
/// @param role <#role description#>
/// @param username <#username description#>
/// @param password <#password description#>
/// @param appkey <#appkey description#>
/// @param subdomain <#subdomain description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)authWithRole:(NSString *)role
        withUsername:(NSString *)username
        withPassword:(NSString *)password
          withAppkey:(NSString *)appkey
       withSubdomain:(NSString *)subdomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/// 手机验证码登录
/// @param mobile <#mobile description#>
/// @param code <#code description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)authWithMobile:(NSString *)mobile
            withCode:(NSString *)code
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;


/// 邮箱验证码登录
/// @param email <#email description#>
/// @param code <#code description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)authWithEmail:(NSString *)email
             withCode:(NSString *)code
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 微信登录
/// @param unionId <#unionId description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)authWithUnionId:(NSString *)unionId
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/// 发送手机h验证码
/// @param mobile <#mobile description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)requestMobileCode:(NSString *)mobile
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/// 发送邮件验证码
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)requestEmailCode:(NSString *)email
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param wId <#wId description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)requestThreadWithWorkGroupWid:(NSString *)wId
                        resultSuccess:(SuccessCallbackBlock)success
                         resultFailed:(FailedCallbackBlock)failed;

- (void)requestThreadWebRTCWithWorkGroupWid:(NSString *)wId
                                     webrtc:(int)webrtc
                        resultSuccess:(SuccessCallbackBlock)success
                         resultFailed:(FailedCallbackBlock)failed;

/**
 指定坐席

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)requestThreadWithAgentUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed;

/**
 请求会话

 @param workGroupWid 工作组wid
 @param type 类型：workGroup or appointed
 @param agentUid 指定坐席uid
 @param success 成功回调
 @param failed 失败回调
 */
- (void)requestThread:(NSString *)workGroupWid
            withType:(NSString *)type
        withAgentUid:(NSString *)agentUid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工

 @param workGroupWid 工作组wid
 @param success 成功回调
 @param failed 失败回调
 */
- (void)requestAgent:(NSString *)workGroupWid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/// 请求联系人会话
/// @param cid <#cid description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)getContactThread:(NSString *)cid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/// 请求群组会话
/// @param gid <#gid description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)getGroupThread:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;


/**
 <#Description#>

 @param tid <#tid description#>
 @param qid <#qid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)requestQuestionnairWithTid:(NSString *)tid
                           itemQid:(NSString *)qid
                     resultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param wid <#wid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)requestChooseWorkGroup:(NSString *)wid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

- (void)requestChooseWorkGroupLiuXue:(NSString *)wid
               withWorkGroupNickname:(NSString *)workGroupNickname
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed;

- (void)requestChooseWorkGroupLiuXueLBS:(NSString *)wid
                  withWorkGroupNickname:(NSString *)workGroupNickname
                           withProvince:(NSString *)province
                               withCity:(NSString *)city
                          resultSuccess:(SuccessCallbackBlock)success
                           resultFailed:(FailedCallbackBlock)failed;

/**
 设置昵称

 @param nickname <#nickname description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setNickname:(NSString *)nickname
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 设置头像

 @param avatar <#avatar description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setAvatar:(NSString *)avatar
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 设置描述

 @param description description
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFingerPrintWithUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed;

- (void)getDeviceInfoByUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param name <#name description#>
 @param key <#key description#>
 @param value <#value description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setFingerPrint:(NSString *)name
                   withKey:(NSString *)key
                 withValue:(NSString *)value
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param wId <#wId description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getWorkGroupStatus:(NSString *)wId
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param agentUid <#agentUid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getAgentStatus:(NSString *)agentUid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 查询当前用户-某技能组wid或指定客服未读消息数目
 注意：技能组wid或指定客服唯一id
 适用于 访客 和 客服
 */
- (void)getUreadCount:(NSString *)wid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 访客端-查询访客所有未读消息数目
 */
- (void)getUreadCountVisitorWithResultSuccess:(SuccessCallbackBlock)success
                                 resultFailed:(FailedCallbackBlock)failed;

/**
 客服端-查询客服所有未读消息数目
 */
- (void)getUreadCountAgentWithResultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed;


/**
 访客端-查询访客所有未读消息
 */
- (void)getUreadMessagesVisitor:(NSInteger)page
                       withSize:(NSInteger)size
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 客服端-查询客服所有未读消息
 */
- (void)getUreadMessagesAgent:(NSInteger)page
                     withSize:(NSInteger)size
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)visitorGetThreadsPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tId <#tId description#>
 @param score <#score description#>
 @param note <#note description#>
 @param invite <#invite description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)visitorRate:(NSString *)tId
          withScore:(NSInteger)score
           withNote:(NSString *)note
         withInvite:(BOOL)invite
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 客服端接口

/**
 初始化从服务器获取：
 1. 客服个人信息
 2. 队列信息
 3. 会话信息

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)initDataResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 加载用户个人资料

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getAgentProfileResultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

- (void)getVisitorProfileResultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 根据uid加载访客个人信息
 */
- (void)getUserProfileByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed;

/**
 根据uid加载crm客户信息
 */
- (void)getCustomerByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed;

/**
 当前在线的客服

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)userOnline:(int)page
        withSize:(int)size
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 他人加载用户详情

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)userDetail:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)agentThreadsResultSuccess:(SuccessCallbackBlock)success
                       resultFailed:(FailedCallbackBlock)failed;

/**
 客服端分页加载-客服自己的历史会话：客服会话
 */
- (void)agentThreadHistoryRecords:(int)page
                         withSize:(int)size
                    resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

/**
 加载待办任务列表
 */
- (void)queryTodos:(int)page
         withSize:(int)size
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 加载我的工单
 */
- (void)queryTickets:(int)page
         withSize:(int)size
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nickname <#nickname description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateNickname:(NSString *)nickname
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param isAutoReply <#isAutoReply description#>
 @param content <#content description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateAutoReply:(BOOL)isAutoReply
            withContent:(NSString *)content
           withImageUrl:(NSString *)imageUrl
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;



/**
 <#Description#>

 @param acceptStatus <#acceptStatus description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setAcceptStatus:(NSString *)acceptStatus
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 获取客服代码
 */
- (void)getChatCodeResultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新欢迎语
 */
- (void)updateWelcomeTip:(NSString *)welcomeTip
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新密码
 */
- (void)updatePassword:(NSString *)password
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新个性签名
 */
- (void)updateDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)agentCloseThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)visitorCloseThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 获取好友列表
 
 @param success success description
 @param failed 失败回调函数
 */
- (void)getContactsResultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed;

/**
 获取当前进行中会话
 
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getQueuesPage:(NSUInteger)page
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param preTid <#preTid description#>
 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateCurrentThread:(NSString *)preTid
                      currentTid:(NSString *)tid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markTopThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unmarkTopThread:(NSString *)tid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markNoDisturbThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unmarkNoDisturbThread:(NSString *)tid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markUnreadThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unmarkUnreadThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markDeletedThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param mid <#mid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markDeletedMessage:(NSString *)mid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markClearThreadMessage:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markClearContactMessage:(NSString *)uid
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)markClearGroupMessage:(NSString *)gid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 群组接口

/**
 获取群组

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getGroupsResultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getGroupDetail:(NSString *)gid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getGroupMembers:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nickname <#nickname description#>
 @param type <#type description#>
 @param selectedContacts <#selectedContacts description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)createGroup:(NSString *)nickname
               type:(NSString *)type
        selectedContacts:(NSArray *)selectedContacts
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nickname <#nickname description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateGroupNickname:(NSString *)nickname
               withGroupGid:(NSString *)gid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param announcement <#announcement description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateGroupAnnouncement:(NSString *)announcement
                        withGroupGid:(NSString *)gid
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param description <#description description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateGroupDescription:(NSString *)description
                       withGroupGid:(NSString *)gid
                      resultSuccess:(SuccessCallbackBlock)success
                       resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)inviteToGroup:(NSString *)uid
            withGroupGid:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uidList <#uidList description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)inviteListToGroup:(NSArray *)uidList
         withGroupGid:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)joinGroup:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)applyGroup:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nid <#nid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)approveGroupApply:(NSString *)nid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nid <#nid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)denyGroupApply:(NSString *)nid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)kickGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)muteGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unmuteGroupMember:(NSString *)uid
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)setGroupAdmin:(NSString *)uid
         withGroupGid:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unsetGroupAdmin:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param needApprove <#needApprove description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)transferGroup:(NSString *)uid
         withGroupGid:(NSString *)gid
      withNeedApprove:(BOOL)needApprove
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nid <#nid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)acceptGroupTransfer:(NSString *)nid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param nid <#nid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)rejectGroupTransfer:(NSString *)nid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)withdrawGroup:(NSString *)gid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)dismissGroup:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param keyword <#keyword description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)filterGroup:(NSString *)keyword
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param keyword <#keyword description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)filterGroupMembers:(NSString *)gid
        withKeyword:(NSString *)keyword
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getNoticesPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 社交关系

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getStrangersPage:(NSUInteger)page
                withSize:(NSUInteger)size
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFollowsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFansPage:(NSUInteger)page
           withSize:(NSUInteger)size
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFriendsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)addFollow:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unFollow:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)addFriend:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)removeFriend:(NSString *)uid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)isFollowed:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getRelation:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 判断自己是否已经屏蔽对方

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)isShield:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 判断自己是否已经被对方屏蔽

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)isShielded:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 屏蔽对方，则对方无法给自己发送消息。但自己仍然可以给对方发送消息

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)shield:(NSString *)uid
 resultSuccess:(SuccessCallbackBlock)success
  resultFailed:(FailedCallbackBlock)failed;

/**
 取消屏蔽

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unshield:(NSString *)uid
 resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getBlocksPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param type <#type description#>
 @param note <#note description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)addBlock:(NSString *)uid
        withNote:(NSString *)note
        withType:(NSString *)type
        withUuid:(NSString *)uuid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param bid <#bid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)unBlock:(NSString *)bid
  resultSuccess:(SuccessCallbackBlock)success
   resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 公共接口

/**
 检测网络是否可用
 */
- (BOOL)isNetworkReachable;

///**
// 同步发送文本消息
// 
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendTextMessage:(NSString *)content
//                  toTid:(NSString *)tId
//                localId:(NSString *)localId
//            sessionType:(NSString *)sessiontype
//          resultSuccess:(SuccessCallbackBlock)success
//           resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送图片消息
// 
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendImageMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送文件消息
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调函数
// @param failed 失败回调函数
// */
//- (void)sendFileMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//                 format:(NSString *)format
//               fileName:(NSString *)fileName
//               fileSize:(NSString *)fileSize
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送语音消息
// 
// @param content <#content description#>
// @param tId <#tId description#>
// @param sessiontype <#stype description#>
// */
//- (void)sendVoiceMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//             voiceLength:(int)voiceLength
//                  format:(NSString *)format
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// <#Description#>
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调函数
// @param failed 失败回调函数
// */
//- (void)sendCommodityMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// <#Description#>
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调函数
// @param failed 失败回调函数
// */
//- (void)sendRedPacketMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送预知消息
//
// @param content <#content description#>
// @param tId <#tId description#>
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调函数
// @param failed 失败回调函数
// */
//- (void)sendPreviewMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送消息回执
//
// @param mid mid
// @param tId <#tId description#>
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调函数
// @param failed 失败回调函数
// */
//- (void)sendReceiptMessage:(NSString *)mid
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;

/**
 同步发送消息
 TODO: 加频率限制，每秒1条
 */
- (void)sendMessage:(NSString *)json
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 获取某个访客的聊天记录
 */
- (void)getMessageWithUser:(NSString *)uid
                  withPage:(NSInteger)page
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param messageid <#messageid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getMessageWithUser:(NSString *)uid
                    withId:(NSInteger)messageid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param cid <#cid description#>
 @param page <#page description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getMessageWithContact:(NSString *)cid
                  withPage:(NSInteger)page
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param cid <#cid description#>
 @param messageid <#messageid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getMessageWithContact:(NSString *)cid
                       withId:(NSInteger)messageid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param page <#page description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getMessageWithGroup:(NSString *)gid
                  withPage:(NSInteger)page
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param messageid <#messageid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getMessageWithGroup:(NSString *)gid
                     withId:(NSInteger)messageid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param imageData <#imageData description#>
 @param imageName <#imageName description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)uploadImageData:(NSData *)imageData
          withImageName:(NSString *)imageName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param imageData <#imageData description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)uploadAvatarData:(NSData *)imageData
             withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param voiceData <#voiceData description#>
 @param voiceName <#voiceName description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)uploadVoiceData:(NSData *)voiceData
          withVoiceName:(NSString *)voiceName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param fileData <#fileData description#>
 @param fileName <#fileName description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)uploadFileData:(NSData *)fileData
          withFileName:(NSString *)fileName
           withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;


/**
 <#Description#>

 @param videoData <#fileData description#>
 @param videoName <#fileName description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)uploadVideoData:(NSData *)videoData
          withVideoName:(NSString *)videoName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 机器人

/**
 <#Description#>

 @param type <#type description#>
 @param wid <#wid description#>
 @param aid <#aid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)initAnswer:(NSString *)type
  withWorkGroupWid:(NSString *)wid
      withAgentUid:(NSString *)aid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)topAnswer:(NSString *)uid
       withThreadTid:(NSString *)tid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param aid <#aid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)queryAnswer:(NSString *)tid
     withQuestinQid:(NSString *)aid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param type <#type description#>
 @param wid <#wid description#>
 @param aid <#aid description#>
 @param content <#content description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)messageAnswer:(NSString *)wid
          withMessage:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;


/**
 对机器人返回答案进行评价反馈
 
 @param aid <#aid description#>
 @param mid <#mid description#>
 @param rate <#rate description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)rateAnswer:(NSString *)aid
    withMessageMid:(NSString *)mid
          withRate:(BOOL)rate
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param type <#type description#>
 @param content <#content description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)leaveMessage:(NSString *)type
             withUid:(NSString *)uid
        withMobile:(NSString *)contact
         withContent:(NSString *)content
        withImageUrl:(NSString *)imageUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

- (void)getLeaveMessagesPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

- (void)getLeaveMessagesVisitorPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

- (void)replyLeaveMessage:(NSString *)lid
             withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 工单系统

/**
 获取工单类别

 @param uid 管理员uid
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getTicketCategories:(NSString *)uid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 我的工单

 @param page <#page description#>
 @param size <#size description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)getTickets:(NSUInteger)page
          withSize:(NSUInteger)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 
 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)createTicket:(NSString *)uid
          withUrgent:(NSString *)urgent
             withCid:(NSString *)cid
         withContent:(NSString *)content
          withMobile:(NSString *)mobile
           withEmail:(NSString *)email
         withFileUrl:(NSString *)fileUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 意见反馈

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFeedbackCategories:(NSString *)uid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 我的反馈

 @param page <#page description#>
 @param size <#size description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)getFeedbacks:(NSUInteger)page
            withSize:(NSUInteger)size
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)createFeedback:(NSString *)uid
               withCid:(NSString *)cid
           withContent:(NSString *)content
            withMobile:(NSString *)mobile
//             withEmail:(NSString *)email
           withFileUrl:(NSString *)fileUrl
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getFeedbackHistoriesWithResultSuccess:(SuccessCallbackBlock)success
                                 resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 帮助中心

/**
 <#Description#>
 
 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getSupportCategories:(NSString *)uid
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param cid <#cid description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)getCategoryDetail:(NSString *)cid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 
 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getArticles:(NSString *)uid
           withType:(NSString *)type
           withPage:(NSUInteger)page
           withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 获取文档详情

 @param aid <#aid description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)getArticleDetail:(NSString *)aid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 搜索文档

 @param uid <#uid description#>
 @param content <#content description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)searchArticle:(NSString *)uid
          withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 评价文档是否有帮助

 @param aid <#aid description#>
 @param rate <#rate description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
- (void)rateArticle:(NSString *)aid
           withRate:(BOOL)rate
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 常用语

- (void)getCuwsWithResultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

- (void)createCuw:(int)categoryId
         withName:(NSString *)name
      withContent:(NSString *)content
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

- (void)updateCuw:(int)cuwid
         withName:(NSString *)name
      withContent:(NSString *)content
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

- (void)deleteCuw:(int)cuwid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;


#pragma mark - WebRTC

/**
 <#Description#>
 */
- (void)uploadDeviceInfo;

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)logoutResultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;
/**
 错误处理

 @param error error
 @param success 成功回调
 @param failed 失败回调
 */
- (void)failError:(NSError *)error
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 微信

/**
 获取token
 https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN

 @param code <#code description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getWXAccessToken:(NSString *)code
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;
/**
 刷新token
 https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN

 @param refreshToken <#refreshToken description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)refreshWXAccessToken:(NSString *)refreshToken
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param accessToken <#accessToken description#>
 @param openId <#openId description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)isWxAccessTokenValid:(NSString *)accessToken
                  withOpenId:(NSString *)openId
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param accessToken <#accessToken description#>
 @param openId <#openId description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)getWxUserinfo:(NSString *)accessToken
           withOpenId:(NSString *)openId
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

#pragma mark - device token

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)isTokenUploadedResultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param deviceToken <#deviceToken description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)updateDeviceToken:(NSString *)deviceToken
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
- (void)deleteDeviceTokenResultSuccess:(SuccessCallbackBlock)success
                          resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 取消网络请求

/**
 <#Description#>
 */
- (void)cancelAllHttpRequest;

@end







