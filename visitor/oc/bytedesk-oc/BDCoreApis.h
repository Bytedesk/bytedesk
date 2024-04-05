//
//  BDCoreApis.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/7/15.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^SuccessCallbackBlock)(NSDictionary *dict);
typedef void (^FailedCallbackBlock)(NSError *error);

@class BDProfileModel;

@interface BDCoreApis : NSObject

+ (BDCoreApis *)sharedInstance;

#pragma mark - 访客端接口

/**
 访客登录: 包含自动注册默认用户

 @param appkey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) init:(NSString *)appkey
    withSubdomain:(NSString *)subdomain
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**

 @param appKey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withAppkey:(NSString *)appKey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**

 @param appKey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withNickname:(NSString *)nickname
        withAppkey:(NSString *)appKey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**

 @param appKey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withNickname:(NSString *)nickname
        withAvatar:(NSString *)avatar
        withAppkey:(NSString *)appKey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;


/**
 普通用户注册

 @param username 用户名
 @param nickname 昵称
 @param password 密码
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
        withPassword:(NSString *)password
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 普通用户注册接口，自定义subDomain

 @param username 用户名
 @param nickname 昵称
 @param password 密码
 @param subDomain 企业号，主要用于多租户平台，如SaaS；其他情况可写死为 ‘vip’
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 普通用户注册接口，自定义subDomain

 @param username 用户名
 @param nickname 昵称
 @param avatar 头像
 @param password 密码
 @param subDomain 企业号，主要用于多租户平台，如SaaS；其他情况可写死为 ‘vip’
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)registerUser:(NSString *)username
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
+ (void)registerEmail:(NSString *)email
         withNickname:(NSString *)nickname
         withPassword:(NSString *)password
        withSubDomain:(NSString *)subDomain
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 自定义注册用户uid
 可用于跟业务用户系统对接

 @param username 用户名
 @param nickname 昵称
 @param uid 自定义uid
 @param password 密码
 @param subDomain 企业号，主要用于多租户平台，如SaaS；其他情况可写死为 ‘vip’
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
             withUid:(NSString *)uid
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param email <#email description#>
 @param password <#password description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)registerAdmin:(NSString *)email
         withPassword:(NSString *)password
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 手机号注册
/// @param mobile <#mobile description#>
/// @param email <#email description#>
/// @param nickname <#nickname description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)registerMobile:(NSString *)mobile
             withEmail:(NSString *)email
          withNickname:(NSString *)nickname
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 绑定手机号
/// @param mobile <#mobile description#>
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)bindMobile:(NSString *)mobile
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/// 判断微信号是否曾经登录 或者 被绑定过
/// @param unionId <#unionId description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)isWeChatRegistered:(NSString *)unionId
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param unionid <#unionid description#>
 @param openid <#openid description#>
 @param nickname <#nickname description#>
 @param avatar <#avatar description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)registerWeChat:(NSString *)unionid
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
+ (void)bindWeChat:(NSString *)unionid
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/// 上传通讯录
/// @param mobile <#mobile description#>
/// @param nickname <#nickname description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
//+ (void)uploadMobile:(NSString *)mobile
//        withNickname:(NSString *)nickname
//       resultSuccess:(SuccessCallbackBlock)success
//        resultFailed:(FailedCallbackBlock)failed;

/**
 访客登录: 包含自动注册默认用户

 @param appkey appkey
 @param subdomain 二级域名
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) loginWithAppkey:(NSString *)appkey
           withSubdomain:(NSString *)subdomain
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 设置当前用户身份为访客

 @return true/false
 */
+ (BOOL)loginAsVisitor;

+ (void) loginWithUsername:(NSString *)username
              withPassword:(NSString *)password
                withAppkey:(NSString *)appkey
              withSubdomain:(NSString *)subdomain
                   withRole:(NSString *)role
                resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 客服首次登录，需要提供相关信息

 @param username 用户名
 @param password 密码
 @param appkey appkey
 @param subDomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) loginWithUsername:(NSString *)username
              withPassword:(NSString *)password
                withAppkey:(NSString *)appkey
             withSubdomain:(NSString *)subDomain
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/// 手机号登录
/// @param mobile <#mobile description#>
/// @param code <#code description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void) loginWithMobile:(NSString *)mobile
                withCode:(NSString *)code
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/// 邮箱验证码登录
/// @param email <#email description#>
/// @param code <#code description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void) loginWithEmail:(NSString *)email
                withCode:(NSString *)code
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/// 微信登录
/// @param unionId <#unionId description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void) loginWithUnionId:(NSString *)unionId
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/// 发送手机h验证码
/// @param mobile <#mobile description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)requestMobileCode:(NSString *)mobile
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/// 发送邮件验证码
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)requestEmailCode:(NSString *)email
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;


/**
 非首次客服登录，利用首次登录保存在本地的token请求数据

 @param success 成功回调
 @param failed 失败回调
 */
+ (void) loginResultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 利用本地缓存信息登录

 @param appkey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) oauthWithAppkey:(NSString *)appkey
           withSubdomain:(NSString *)subdomain
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 工作组会话

 @param wId 工作组id
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)requestThreadWithWorkGroupWid:(NSString *)wId
                        resultSuccess:(SuccessCallbackBlock)success
                         resultFailed:(FailedCallbackBlock)failed;

/**
 音视频工作组会话

 @param wId <#wId description#>
 @param webrtc <#webrtc description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)requestThreadWebRTCWithWorkGroupWid:(NSString *)wId
                                     webrtc:(int)webrtc
                              resultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed;

/**
 指定坐席会话

 @param uid 指定坐席uid
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)requestThreadWithAgentUid:(NSString *)uid
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
+ (void)requestThread:(NSString *)workGroupWid
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
+ (void)requestAgent:(NSString *)workGroupWid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/// 请求联系人会话
/// @param cid <#cid description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)getContactThread:(NSString *)cid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/// 请求群组会话
/// @param gid <#gid description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
+ (void)getGroupThread:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;


/**
 选择问卷答案

 @param tid 会话tid
 @param qid <#qid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)requestQuestionnairWithTid:(NSString *)tid
                           itemQid:(NSString *)qid
                     resultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed;

/**
 选择工作组

 @param wid 工作组wid
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)requestChooseWorkGroup:(NSString *)wid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

+ (void)requestChooseWorkGroupLiuXue:(NSString *)wid
               withWorkGroupNickname:(NSString *)workGroupNickname
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed;

+ (void)requestChooseWorkGroupLiuXueLBS:(NSString *)wid
                  withWorkGroupNickname:(NSString *)workGroupNickname
                           withProvince:(NSString *)province
                               withCity:(NSString *)city
                          resultSuccess:(SuccessCallbackBlock)success
                           resultFailed:(FailedCallbackBlock)failed;

/**
 设置昵称

 @param nickname 昵称
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)setNickname:(NSString *)nickname
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 设置头像
 注意：新头像需要与旧头像url保持一致，如此可以保证用户更新头像之后，其所有好友实时更新头像

 @param avatar <#avatar description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)setAvatar:(NSString *)avatar
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;


/**
 设置描述

 @param description 描述
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)setDescription:(NSString *)description
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;


/**
 获取用户信息

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getFingerPrintWithUid:(NSString *)uid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 获取设备信息

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getDeviceInfoByUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 自定义设置用户属性
 */
+ (void)setFingerPrint:(NSString *)name
               withKey:(NSString *)key
             withValue:(NSString *)value
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 获取工作组在线状态
 */
+ (void)getWorkGroupStatus:(NSString *)wId
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 获取某个客服账号的在线状态

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getAgentStatus:(NSString *)agentUid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 查询当前用户-某技能组wid或指定客服未读消息数目
 注意：技能组wid或指定客服唯一id
 适用于 访客 和 客服
 */
+ (void)getUreadCount:(NSString *)wid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 访客端-查询访客所有未读消息数目
 */
+ (void)getUreadCountVisitorWithResultSuccess:(SuccessCallbackBlock)success
                                 resultFailed:(FailedCallbackBlock)failed;

/**
 客服端-查询客服所有未读消息数目
 */
+ (void)getUreadCountAgentWithResultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed;

/**
 访客端-查询访客所有未读消息
 */
+ (void)getUreadMessagesVisitor:(NSInteger)page
                       withSize:(NSInteger)size
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 客服端-查询客服所有未读消息
 */
+ (void)getUreadMessagesAgent:(NSInteger)page
                     withSize:(NSInteger)size
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 获取用户的所有会话历史

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)visitorGetThreadsPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;


/**
 满意度评价

 @param tId 会话tid
 @param score <#score description#>
 @param note <#note description#>
 @param invite <#invite description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)visitorRate:(NSString *)tId
          withScore:(NSInteger)score
           withNote:(NSString *)note
         withInvite:(BOOL)invite
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 客服端接口



/**
 数据初始化：
 {
     message: "success"
     status_code: 200,
     data: {
         agent: '', // 个人资料
         queues: '', // 排队列表
         rosters: '', // 好友信息
         threads: '' // 当前会话列表
     }
 }
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)initDataResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 加载用户个人资料
 
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getAgentProfileResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

+ (void)getVisitorProfileResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed;

/**
 根据uid加载访客个人信息
 */
+ (void)getUserProfileByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed;

/**
 根据uid加载crm客户信息
 */
+ (void)getCustomerByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed;

/**
 当前在线的客服

 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getOnlineAgents:(int)page
        withSize:(int)size
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 他人加载用户详情
 
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)userDetail:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 更新个人资料，目前仅开放nickname

 @param nickname <#nickname description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)updateNickname:(NSString *)nickname
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 更新自动回复内容，和是否启用自动回复
 客服专用

 @param isAutoReply <#isAutoReply description#>
 @param content <#content description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)updateAutoReply:(BOOL)isAutoReply
            withContent:(NSString *)content
           withImageUrl:(NSString *)imageUrl
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 设置接待状态

 @param acceptStatus <#acceptStatus description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)setAcceptStatus:(NSString *)acceptStatus
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;


/**
 获取客服代码
 */
+ (void)getChatCodeResultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新欢迎语
 */
+ (void)updateWelcomeTip:(NSString *)welcomeTip
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新密码
 */
+ (void)updatePassword:(NSString *)password
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 更新个性签名
 */
+ (void)updateDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 */
+ (void)agentCloseThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

+ (void)visitorCloseThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 群组接口

/**
 获取群组
 
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getGroupsResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;


/**
 查询群组详情

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getGroupDetail:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 获取群组成员

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getGroupMembers:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 创建群组

 @param nickname <#nickname description#>
 @param selectedContacts <#selectedContacts description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)createGroup:(NSString *)nickname
   selectedContacts:(NSArray *)selectedContacts
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

+ (void)createGroup:(NSString *)nickname
               type:(NSString *)type
   selectedContacts:(NSArray *)selectedContacts
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 更新群组昵称

 @param nickname <#nickname description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)updateGroupNickname:(NSString *)nickname
               withGroupGid:(NSString *)gid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 更新群组公告

 @param announcement <#announcement description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)updateGroupAnnouncement:(NSString *)announcement
                   withGroupGid:(NSString *)gid
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 更新群组描述

 @param description <#description description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)updateGroupDescription:(NSString *)description
                  withGroupGid:(NSString *)gid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 邀请某个用户到群组

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)inviteToGroup:(NSString *)uid
         withGroupGid:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

+ (void)inviteListToGroup:(NSArray *)uidList
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 主动申请加群，无需要群主审核

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)joinGroup:(NSString *)gid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 主动申请加群，需要群主审核

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)applyGroup:(NSString *)gid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 同意加群请求

 @param nid <#nid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)approveGroupApply:(NSString *)nid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 拒绝加群请求

 @param nid <#nid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)denyGroupApply:(NSString *)nid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 将某用户踢出群组

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)kickGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 禁言群内某用户

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)muteGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;


/**
 将某人取消禁言

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)unmuteGroupMember:(NSString *)uid
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;


/**
 将某人设置为群组管理员

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)setGroupAdmin:(NSString *)uid
         withGroupGid:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;


/**
 取消某人群组管理员身份

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)unsetGroupAdmin:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 移交群组

 @param uid <#uid description#>
 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)transferGroup:(NSString *)uid
         withGroupGid:(NSString *)gid
      withNeedApprove:(BOOL)needApprove
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 同意移交群组

 @param nid <#nid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)acceptGroupTransfer:(NSString *)nid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 拒绝移交群组

 @param nid <#nid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)rejectGroupTransfer:(NSString *)nid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 退出群组

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)withdrawGroup:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 解散群组

 @param gid <#gid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)dismissGroup:(NSString *)gid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 搜索群组

 @param keyword <#keyword description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)filterGroup:(NSString *)keyword
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 搜索群组成员

 @param gid <#gid description#>
 @param keyword <#keyword description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)filterGroupMembers:(NSString *)gid
               withKeyword:(NSString *)keyword
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;


/**
 分页获取通知列表

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getNoticesPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 社交关系

/**
 获取陌生人列表（暂未上线）

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getStrangersPage:(NSUInteger)page
                withSize:(NSUInteger)size
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 获取关注列表
 分页

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getFollowsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 获取粉丝列表
 分页

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getFansPage:(NSUInteger)page
           withSize:(NSUInteger)size
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 获取好友列表
 分页

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getFriendsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 获取黑名单
 分页

 @param page <#page description#>
 @param size <#size description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getBlocksPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 添加关注

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)addFollow:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;

/**
 取消关注

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)unFollow:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;


/**
 添加好友

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)addFriend:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed;


/**
 删除好友

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)removeFriend:(NSString *)uid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 判断是否已经关注

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)isFollowed:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 获取关系（暂未上线）

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getRelation:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 判断自己是否已经屏蔽对方
 
 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)isShield:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 判断自己是否已经被对方屏蔽
 
 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)isShielded:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 屏蔽对方，则对方无法给自己发送消息。但自己仍然可以给对方发送消息
 
 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)shield:(NSString *)uid
 resultSuccess:(SuccessCallbackBlock)success
  resultFailed:(FailedCallbackBlock)failed;

/**
 取消屏蔽
 
 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)unshield:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 拉黑用户

 @param uid <#uid description#>
 @param note <#note description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)addBlock:(NSString *)uid
        withNote:(NSString *)note
        withType:(NSString *)type
        withUuid:(NSString *)uuid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

/**
 取消拉黑

 @param uid <#uid description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)unBlock:(NSString *)bid
  resultSuccess:(SuccessCallbackBlock)success
   resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 公共接口

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSMutableArray *)getThreads;

+ (NSMutableArray *)getIMThreads;

+ (NSMutableArray *)getOnGoingThreads;

+ (NSMutableArray *)getHistoryThreads;

/**
 加载会话列表: 当前进行中

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getThreadResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 客服端分页加载-客服自己的历史会话：客服会话
 */
+ (void)getThreadHistoryRecords:(int)page
                       withSize:(int)size
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 加载待办任务列表
 */
+ (void)queryTodos:(int)page
              withSize:(int)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 加载我的工单
 */
+ (void)queryTickets:(int)page
              withSize:(int)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSMutableArray *)getQueues;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSNumber *)getQueueCount;

/**
 <#Description#>

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getQueueResultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSMutableArray *)getContacts;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSMutableArray *)getGroups;

/**
 <#Description#>

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)getContactsResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param preTid <#preTid description#>
 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)updateCurrentThread:(NSString *)preTid
                  currentTid:(NSString *)tid
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markTopThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)unmarkTopThread:(NSString *)tid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markNoDisturbThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)unmarkNoDisturbThread:(NSString *)tid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markUnreadThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)unmarkUnreadThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markDeletedThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param mid <#mid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markDeletedMessage:(NSString *)mid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param tid <#tid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markClearThreadMessage:(NSString *)tid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markClearContactMessage:(NSString *)uid
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param gid <#gid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)markClearGroupMessage:(NSString *)gid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;

///**
// 同步发送文本消息
// 
// @param content content description
// @param tId 会话tid
// @param sessiontype <#stype description#>
// */
//+ (void)sendTextMessage:(NSString *)content
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
// @param tId 会话tid
// @param sessiontype <#stype description#>
// */
//+ (void)sendImageMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// 发送文件消息
//
// @param content <#content description#>
// @param tId 会话tid
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调
// @param failed 失败回调
// */
//+ (void)sendFileMessage:(NSString *)content
//                  toTid:(NSString *)tId
//                localId:(NSString *)localId
//            sessionType:(NSString *)sessiontype
//                 format:(NSString *)format
//               fileName:(NSString *)fileName
//               fileSize:(NSString *)fileSize
//          resultSuccess:(SuccessCallbackBlock)success
//           resultFailed:(FailedCallbackBlock)failed;
//
///**
// 同步发送语音消息
// 
// @param content <#content description#>
// @param tId 会话tid
// @param sessiontype <#stype description#>
// */
//+ (void)sendVoiceMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//             voiceLength:(int)voiceLength
//                  format:(NSString *)format
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed;
//
///**
// 发送商品消息
//
// @param content <#content description#>
// @param tId 会话tid
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调
// @param failed 失败回调
// */
//+ (void)sendCommodityMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed;
//
//
///**
// 发送红包消息
//
// @param content <#content description#>
// @param tId 会话tid
// @param localId <#localId description#>
// @param sessiontype <#sessiontype description#>
// @param success 成功回调
// @param failed 失败回调
// */
//+ (void)sendRedPacketMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed;

/**
 同步发送消息
 */
+ (void)sendMessage:(NSString *)json
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 访客端调用，查询当前访客自己的所有聊天记录

 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithUser;

/**
 <#Description#>

 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithUserPage:(NSUInteger)page;

/**
 <#Description#>

 @param tid <#tid description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithThread:(NSString *)tid;

+ (void)clearMessagesWithThread:(NSString *)tid;


+ (NSMutableArray *)getMessagesWithAppointed:(NSString *)tid;

/**
 <#Description#>

 @param tid <#tid description#>
 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithThread:(NSString *)tid withPage:(NSUInteger)page;

/**
 <#Description#>

 @param wid <#wid description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithWorkGroup:(NSString *)wid;

+ (void)clearMessagesWithWorkGroup:(NSString *)wid;

/**
 <#Description#>

 @param wid <#wid description#>
 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithWorkGroup:(NSString *)wid withPage:(NSUInteger)page;

/**
 客服端调用

 @param uid <#uid description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithUser:(NSString *)uid;

+ (void)clearMessagesWithUser:(NSString *)uid;

/**
 <#Description#>

 @param uid <#uid description#>
 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithUser:(NSString *)uid withPage:(NSUInteger)page;

/**
 <#Description#>

 @param cid <#cid description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithContact:(NSString *)cid;

/**
 <#Description#>

 @param cid <#cid description#>
 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithContact:(NSString *)cid withPage:(NSUInteger)page;

/**
 <#Description#>

 @param gid <#gid description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithGroup:(NSString *)gid;

/**
 <#Description#>

 @param gid <#gid description#>
 @param page <#page description#>
 @return <#return value description#>
 */
+ (NSMutableArray *)getMessagesWithGroup:(NSString *)gid withPage:(NSUInteger)page;

/**
 <#Description#>

 @param uid <#uid description#>
 @param page <#page description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getMessageWithUser:(NSString *)uid
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
+ (void)getMessageWithUser:(NSString *)uid
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
+ (void)getMessageWithContact:(NSString *)cid
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
+ (void)getMessageWithContact:(NSString *)cid
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
+ (void)getMessageWithGroup:(NSString *)gid
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
+ (void)getMessageWithGroup:(NSString *)gid
                     withId:(NSInteger)messageid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param imageData <#imageData description#>
 @param imageName <#imageName description#>
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)uploadImageData:(NSData *)imageData
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
+ (void)uploadAvatarData:(NSData *)imageData
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
+ (void)uploadVoiceData:(NSData *)voiceData
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
+ (void)uploadFileData:(NSData *)fileData
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
+ (void)uploadVideoData:(NSData *)videoData
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
+ (void)initAnswer:(NSString *)type
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
+ (void)topAnswer:(NSString *)uid
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
+ (void)queryAnswer:(NSString *)tid
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
+ (void)messageAnswer:(NSString *)wid
          withMessage:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 对机器人返回答案进行评价反馈
 
 @param aid <#aid description#>
 @param mid <#mid description#>
 @param rate <#rate description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)rateAnswer:(NSString *)aid
    withMessageMid:(NSString *)mid
          withRate:(BOOL)rate
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 留言

 @param type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
 @param content 留言内容
 @param success 成功回调
 @param failed 失败回调
 */
+ (void)leaveMessage:(NSString *)type
             withUid:(NSString *)uid
        withMobile:(NSString *)mobile
         withContent:(NSString *)content
        withImageUrl:(NSString *)imageUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

+ (void)getLeaveMessagesPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

+ (void)getLeaveMessagesVisitorPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

+ (void)replyLeaveMessage:(NSString *)lid
             withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 常用语

+ (void)getCuwsWithResultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

+ (void)createCuw:(int)categoryId
         withName:(NSString *)name
      withContent:(NSString *)content
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

+ (void)updateCuw:(int)cuwid
         withName:(NSString *)name
      withContent:(NSString *)content
    resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed;

+ (void)deleteCuw:(int)cuwid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 工单系统

/**
 获取工单类别
 
 @param uid 管理员uid
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getTicketCategories:(NSString *)uid
          resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;


+ (void)getTickets:(NSUInteger)page
          withSize:(NSUInteger)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 
 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)createTicket:(NSString *)uid
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
+ (void)getFeedbackCategories:(NSString *)uid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed;


+ (void)getFeedbacks:(NSUInteger)page
            withSize:(NSUInteger)size
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>
 
 @param uid <#uid description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)createFeedback:(NSString *)uid
               withCid:(NSString *)cid
           withContent:(NSString *)content
            withMobile:(NSString *)mobile
           withFileUrl:(NSString *)fileUrl
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed;


#pragma mark - 帮助中心

/**
 获取帮助中心分类
 
 @param uid 管理员uid
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getSupportCategories:(NSString *)uid
           resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 获取帮助文档分类详情

 @param cid 分类cid
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)getCategoryDetail:(NSString *)cid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 加载常见问题

 @param uid 管理员uid
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)getSupportArticles:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 分页获取帮助文档
 
 @param uid 管理员uid
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getArticles:(NSString *)uid
           withType:(NSString *)type
           withPage:(NSUInteger)page
           withSize:(NSUInteger)size
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;

/**
 获取文档详情
 
 @param aid 文档aid
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)getArticleDetail:(NSString *)aid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 搜索文档
 
 @param uid 管理员uid
 @param content <#content description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)searchArticle:(NSString *)uid
          withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

/**
 评价文档是否有帮助

 @param aid 文档aid
 @param rate <#rate description#>
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)rateArticle:(NSString *)aid
           withRate:(BOOL)rate
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed;


#pragma mark - Section

/**
 <#Description#>

 @param success 成功回调
 @param failed 失败回调
 */
+ (void)logoutResultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed;

/**
 建立长连接
 */
+ (void)connect;
+ (void)reconnect;

/**
 断开连接
 */
+ (void)disconnect;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (BOOL)isConnected;

#pragma mark - Application

/**
 <#Description#>
 */
+ (void)applicationWillResignActive;

/**
 <#Description#>
 */
+ (void)applicationDidEnterBackground;

/**
 <#Description#>
 */
+ (void)applicationWillEnterForeground;

/**
 <#Description#>
 */
+ (void)applicationDidBecomeActive;

/**
 <#Description#>
 */
+ (void)applicationWillTerminate;

#pragma mark - 微信

/**
 <#Description#>

 @param code <#code description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)getWXAccessToken:(NSString *)code
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param refreshToken <#refreshToken description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)refreshWXAccessToken:(NSString *)refreshToken
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param accessToken <#accessToken description#>
 @param openId <#openId description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)isWxAccessTokenValid:(NSString *)accessToken
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
+ (void)getWxUserinfo:(NSString *)accessToken
           withOpenId:(NSString *)openId
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed;

#pragma mark - device token

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)isTokenUploadedResultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param deviceToken <#deviceToken description#>
 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)updateDeviceToken:(NSString *)deviceToken
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed;

/**
 <#Description#>

 @param success 成功回调函数
 @param failed 失败回调函数
 */
+ (void)deleteDeviceTokenResultSuccess:(SuccessCallbackBlock)success
                          resultFailed:(FailedCallbackBlock)failed;

#pragma mark - 取消网络请求

/**
 <#Description#>
 */
+ (void)cancelAllHttpRequest;


#pragma mark - Mars

//+ (void)disconnect;

+ (void)didEnterBackground;

+ (void)willEnterForground;

+ (void)willTerminate;

@end






