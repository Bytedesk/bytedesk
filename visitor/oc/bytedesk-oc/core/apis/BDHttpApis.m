//
//  KFDSHttpApis.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/18.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDHttpApis.h"
#import "BDCoreApis.h"
#import "BDMQTTApis.h"

#import "BDSettings.h"
#import "BDNotify.h"
#import "BDDBApis.h"
#import "BDUtils.h"
#import "BDConfig.h"

#import "BDThreadModel.h"
#import "BDQueueModel.h"
#import "BDMessageModel.h"
#import "BDContactModel.h"
#import "BDGroupModel.h"
#import "BDWorkGroupModel.h"
#import "BDConstants.h"

#import <AFNetworking/AFNetworking.h>
//#import "AFNetworking.h"

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
////static const DDLogLevel ddLogLevel = DDLogLevelWarning;
//#endif

#pragma mark - 客服接口

// 从服务器请求生成username
#define httpGetAnonymousUser               [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/username"]
// 注册自定义普通用户：用于IM
#define httpVisitorRegister                [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register/user"]
// 注册自定义普通用户：用于IM
#define httpVisitorRegisterEmail           [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register/email"]
// 注册自定义普通用户, 自定义uid
#define httpVisitorRegisterUid             [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register/user/uid"]
// 注册管理员账号
#define httpAdminRegister                 [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register"]
// 手机号注册
#define httpAdminRegisterMobile                 [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register/mobile"]
// 绑定手机号
#define httpAdminBindMobile                 [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/bind/mobile"]
// 绑定微信
#define httpAdminBindWeChat                 [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/bind/wechat"]
// 微信登录之后，注册微信账号
#define httpAdminRegisterWeChat           [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/register/wechat"]
// 判断微信号是否曾经登录 或者 被绑定过
#define httpIsWechatRegistered           [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/is/wechat/registered"]
// 请求会话
#define httpVisitorRequestThread            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/request"]
//
#define httpGetContactThread            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/contact"]
//
#define httpGetGroupThread            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/group"]
// 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
#define httpVisitorRequestAgent            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/request/agent"]
// 选择问卷答案
#define httpVisitorQuestionnaire    [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/questionnaire"]
// 选择工作组
#define httpVisitorChooseWorkGroup         [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/choose/workGroup"]
// 留学，针对大学长定制
#define httpVisitorChooseWorkGroupLiuXue         [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/choose/workGroup/liuxue"]

#define httpVisitorChooseWorkGroupLiuXueLBS         [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/choose/workGroup/liuxue/lbs"]
// 一一上传通讯录
#define httpUploadMobile              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/mobile/upload"]
// 设置昵称
#define httpSetNickname              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/nickname"]
// 设置头像
#define httpSetAvatar              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/avatar"]
// 设置描述
#define httpSetDescription              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/description"]
// 获取用户信息
#define httpVisitorGetUserinfo              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/fingerprint2/userInfo"]
// 获取设备信息
#define httpVisitorDeviceInfo              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/fingerprint2/get"]
// 设置用户信息
#define httpVisitorSetUserinfo              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/fingerprint2/userInfo"]
// 设置设备信息
#define httpVisitorSetDeviceinfo              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/fingerprint2/ios/deviceInfo"]
// 获取工作组在线状态
#define httpVisitorGetWorkGroupStatus       [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/status/workGroup"]
// 获取客服在线状态
#define httpVisitorGetAgentStatus           [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/status/agent"]
// 获取访客的所有历史会话记录
#define httpVisitorGetHistory               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/visitor/history"]
// http rest api同步发送消息
#define httpSendMessages               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/send"]
// 查询当前用户-某技能组wid或指定客服未读消息数目
#define httpMessagesUnreadCount               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/unreadCount"]
// 访客端-查询访客所有未读消息数目
#define httpMessagesUnreadCountVisitor        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/unreadCount/visitor"]
// 客服端-查询客服所有未读消息数目
#define httpMessagesUnreadCountAgent          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/unreadCount/agent"]
// 访客端-查询访客所有未读消息
#define httpMessagesUnreadVisitor        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/unread/message/visitor"]
// 客服端-查询客服所有未读消息
#define httpMessagesUnreadAgent          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/unread/message/agent"]
// 获取某访客的所有聊天记录
#define httpGetUserMessages               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/user"]
#define httpGetUserMessagesFrom               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/user/from"]
// 获取某联系人的聊天记录
#define httpGetContactMessages               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/contact"]
#define httpGetContactMessagesFrom           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/contact/from"]
// 获取某群组的聊天记录
#define httpGetGroupMessages               [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/group"]
// 获取某群组的聊天记录，某条消息id之后的
#define httpGetGroupMessagesFrom           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/group/from"]
// 客户端标记删除，之后不再出现在其消息列表，非真正删除
#define httpAdminMessageMarkDeleted        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/mark/deleted"]
// 客户端标记，清空聊天记录
#define httpAdminMarkClearThreadMessage        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/mark/clear/thread"]
#define httpAdminMarkClearContactMessage        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/mark/clear/contact"]
#define httpAdminMarkClearGroupMessage        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/messages/mark/clear/group"]

// 评价
#define httpVisitorRate                     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/rate/do"]
// 上传图片
#define httpVisitorUploadImage                [NSString stringWithFormat:@"%@%@", [BDConfig getUploadApiVisitorBaseUrl], @"/upload/image"]
// 上传头像
#define httpVisitorUploadAvatar                [NSString stringWithFormat:@"%@%@", [BDConfig getUploadApiVisitorBaseUrl], @"/upload/avatar"]
// 上传录音
#define httpVisitorUploadVoice               [NSString stringWithFormat:@"%@%@", [BDConfig getUploadApiVisitorBaseUrl], @"/upload/voice"]
// 上传文件
#define httpVisitorUploadFile               [NSString stringWithFormat:@"%@%@", [BDConfig getUploadApiVisitorBaseUrl], @"/upload/file"]
// 上传视频
#define httpVisitorUploadVideo               [NSString stringWithFormat:@"%@%@", [BDConfig getUploadApiVisitorBaseUrl], @"/upload/video"]

// 初始化：获取个人资料、队列信息、会话信息、contact信息
#define httpAdminInit                       [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/init"]
// 用户个人资料
#define httpUserProfile                      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/profile"]
// 根据uid获取用户信息
#define httpUserProfileUid                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/profile/uid"]
// 访客个人资料
#define httpVisitorProfile                      [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/profile"]
// 根据uid获取crm信息
#define httpCrmUid                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/crm/get/uid"]
// 在线的客服
#define httpUserOnline                      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/online"]
// 他人加载用户详情
#define httpUserDetail                      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/detail"]
// 更新个人资料
#define httpAdminUpdateProfile              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/update/profile"]
// 修改昵称
#define httpUpdateNickname                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/nickname"]
// 修改欢迎语
#define httpUpdateWelcome                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/change/welcome"]
// 修改密码
#define httpUpdatePassword                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/change/password"]
// 修改个性签名
#define httpUpdateDescription                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/description"]
//
#define httpGetChatCodeUrl                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/wechatUrl"]
// 更新自动回复
//#define httpAdminUpdateAutoReplyContent     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v2/user/update/autoReply"]
#define httpAdminUpdateAutoReplyContent     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v1/autoreply/update"]
// 修改密码
#define httpAdminChangePassword            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/change/password"]
// 当前客服所有同事
#define httpAdminContacts                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/contacts"]
// 设置当前在线状态
#define httpAdminSetAcceptStatus           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/status/set"]
// 获取客服本人所有当前进行中会话
#define httpAdminThreads                    [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/get"]
//
#define httpAdminThreadHistoryRecords      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/history/records"]
// 客服关闭会话
#define httpAdminAgentCloseThread                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/agent/close"]
// 访客关闭会话
#define httpAdminVisitorCloseThread                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/visitor/close"]
// 设置当前会话
#define httpAdminThreadUpdateCurrent        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/update/current"]
// 置顶会话
#define httpAdminThreadMarkTop        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/mark/top"]
// 取消置顶会话
#define httpAdminThreadUnmarkTop        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/unmark/top"]
// 设置会话免打扰
#define httpAdminThreadMarkDisturb        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/mark/nodisturb"]
// 取消会话免打扰
#define httpAdminThreadUnmarkDisturb        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/unmark/nodisturb"]
// 标记未读会话
#define httpAdminThreadMarkUnread        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/mark/unread"]
// 取消标记未读会话
#define httpAdminThreadUnmarkUnread        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/unmark/unread"]
// 标记会话已删除
#define httpAdminThreadMarkDeleted        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/thread/mark/deleted"]

// 获取当前排队
#define httpAdminQueues                     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/queue/get"]
// 从队列中接入会话
#define httpAdminQueueAccept                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/queue/accept"]

// 加载待办任务
#define httpTodoQuery                    [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/todo/query"]

// 获取群组
#define httpAdminGroups                 [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/get"]
// 获取群组成员
#define httpAdminGroupDetail            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v2/group/detail"]
// 获取群组成员
#define httpAdminGroupMembers           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/members"]
// 创建群组
#define httpAdminGroupCreate                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/create"]
// 更新群组
#define httpAdminGroupUpdateNickname        [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/update/nickname"]
// 更新群组
#define httpAdminGroupUpdateAnnouncement    [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/update/announcement"]
// 更新群组
#define httpAdminGroupUpdateDescription     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/update/description"]
// 邀请一人加入群组
#define httpAdminGroupInvite                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/invite"]
// 邀请多人加入群组
#define httpAdminGroupInviteList                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/invite/list"]
// 申请加入群组
#define httpAdminGroupJoin                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/join"]
// 申请加入群组
#define httpAdminGroupApply                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/apply"]
// 接受申请加入
#define httpAdminGroupApplyApprove         [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/apply/approve"]
// 拒绝申请加入
#define httpAdminGroupApplyDeny            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/apply/deny"]
// 踢人
#define httpAdminGroupKick                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/kick"]
// 禁言
#define httpAdminGroupMute                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/mute"]
//
#define httpAdminGroupUnMute                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/unmute"]
//
#define httpAdminGroupSetAdmin                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/set/admin"]
//
#define httpAdminGroupUnSetAdmin                [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/unset/admin"]
// 移交群组
#define httpAdminGroupTransfer            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/transfer"]
// 接受移交群组
#define httpAdminGroupTransferAccept      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/transfer/accept"]
// 拒绝移交群组
#define httpAdminGroupTransferReject      [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/transfer/reject"]
// 退群
#define httpAdminGroupWithdraw            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/withdraw"]
// 解散群组
#define httpAdminGroupDismiss            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/dismiss"]
// 搜索群组
#define httpAdminGroupFilter            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/filter"]
// 搜索群组成员
#define httpAdminGroupFilterMembers     [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/group/filter/members"]

// 拉取通知
#define httpGetNotices              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/notice/get"]

// 退出登录
#define httpAdminLogout                  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/logout"]

// 社交接口：
// 获取陌生人，用于测试
#define httpGetStrangers              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/strangers"]
// 获取关注
#define httpGetFollows              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/follows"]
// 获取粉丝
#define httpGetFans              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/fans"]
// 获取好友
#define httpGetFriends              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/friends"]
// 添加关注
#define httpAddFollow              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/follow"]
// 取消关注
#define httpUnFollow              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/unfollow"]
// 添加好友
#define httpAddFriend              [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/friend/add"]
// 删除好友
#define httpRemoveFriend            [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/friend/remove"]
// 判断是否关注
#define httpIsFollowed           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/isfollowed"]
// 判断好友关系
#define httpGetRelation           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/relation"]
// 判断是否已经屏蔽对方
#define httpIsShield           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/shield"]
// 判断是否被对方屏蔽
#define httpIsShielded           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/shielded"]
// 屏蔽对方
#define httpShield           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/shield"]
// 取消屏蔽
#define httpUnShield           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/user/unshield"]

// 获取黑名单
#define httpGetBlocks           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/block/get"]
// 拉黑
#define httpAddBlock           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/block/add2"]
// 取消拉黑
#define httpUnBlock           [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/block/remove"]

// 初始化机器人
#define httpInitAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v2/answer/init"]
// 请求最热问答
#define httpTopAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/answer/top"]
// 通过aid，请求智能答案
#define httpQueryAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/answer/query"]
// 输入内容，请求智能答案
//#define httpMessageAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v2/answer/message"]
#define httpMessageAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/elastic/robot/message"]
// 评价机器人问答结果
#define httpRateAnswer          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/answer/rate"]

// 留言
#define httpLeaveMessage          [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/v2/leavemsg/save"]
// 拉取留言
#define httpGetLeaveMessages       [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/leavemsg/query"]
// 访客拉取留言
#define httpGetLeaveMessagesVisitor       [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/leavemsg/query/visitor"]
// 回复留言
#define httpReplyLeaveMessages       [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/leavemsg/reply"]

// 访客获取工单类别
#define httpGetTicketCategories  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/category/ticket"]
// 我的工单
#define httpMineTicket  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/ticket/mine"]
// 访客提交工单
#define httpCreateTicket  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/ticket/create"]

// 访客获取意见反馈类别
#define httpGetFeedbackCategories  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/category/feedback"]
// 我的反馈
#define httpMineFeedback  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/feedback/mine"]
// 访客提交意见反馈
#define httpCreateFeedback  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/feedback/create"]

// 访客意见反馈/历史反馈
#define httpFeedbackHistory  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/feedback/my"]

// 获取帮助中心分类
#define httpGetSupportCategories  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/category/support"]

// 获取帮助分类详情
#define httpGetCategoryDetail  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/category/detail"]

// 获取文档详情
#define httpGetArticleDetail  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/article/detail"]

// 搜索帮助文档
#define httpSearchArticle  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/article/search"]

// 评价文档
#define httpRateArticle  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/article/rate"]

// 获取文章详情
#define httpGetArticles  [NSString stringWithFormat:@"%@%@", [BDConfig getApiVisitorBaseUrl], @"/articles"]

// 常用语
#define httpGetCuws  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/cuw/get"]
#define httpCreateCuw  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/cuw/create"]
#define httpUpdateCuw  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/cuw/update"]
#define httpDeleteCuw  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/cuw/delete"]

// 设备 deviceToken
// 判断token 是否已经上传
#define httpIsDeviceTokenUploaded  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/push/token/uploaded"]
// 更新deviceToken
#define httpUpdateDeviceToken  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/push/update/token"]
// 删除deviceToken
#define httpDeleteDeviceToken  [NSString stringWithFormat:@"%@%@", [BDConfig getApiBaseUrl], @"/push/delete/token"]

//
@interface BDHttpApis()

@property(nonatomic, strong) AFHTTPSessionManager *mHttpSessionManager;
@property(nonatomic, strong) AFNetworkReachabilityManager *mReachabilityManager;

@end

static BDHttpApis *sharedInstance = nil;

// TODO: 处理401 access_token无效的错误

@implementation BDHttpApis


+ (BDHttpApis *)sharedInstance {
    //
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDHttpApis alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        //
        _mHttpSessionManager = [AFHTTPSessionManager manager];
        //数据上行为json格式
        _mHttpSessionManager.requestSerializer = [AFJSONRequestSerializer serializer];
        //超时
        _mHttpSessionManager.requestSerializer.timeoutInterval = kTimeOutInterval;
        //数据下行为json格式
        _mHttpSessionManager.responseSerializer = [AFHTTPResponseSerializer serializer];
        //
//        if ([BDSettings getPassportAccessToken] != NULL) {
//            [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
//        }
        // 监测网络状态
        _mReachabilityManager = [AFNetworkReachabilityManager sharedManager];
        [_mReachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
            switch (status) {
                case AFNetworkReachabilityStatusUnknown:
                    NSLog(@"AFNetworkReachabilityStatusUnknown");
                    break;
                case AFNetworkReachabilityStatusNotReachable:
                    NSLog(@"AFNetworkReachabilityStatusNotReachable");
                    break;
                case AFNetworkReachabilityStatusReachableViaWWAN:
                    NSLog(@"AFNetworkReachabilityStatusReachableViaWWAN");
                    break;
                case AFNetworkReachabilityStatusReachableViaWiFi:
                    NSLog(@"AFNetworkReachabilityStatusReachableViaWiFi");
                    break;
                default:
                    NSLog(@"AFNetworkReachabilityStatusOther");
                    break;
            }
        }];
        [_mReachabilityManager startMonitoring];
    }
    return self;
}


- (AFHTTPSessionManager *) getPostHttpSessionManager:(NSDictionary *)parameterDict {
    AFHTTPSessionManager *httpSessionManager = [AFHTTPSessionManager manager];
    httpSessionManager.requestSerializer = [AFJSONRequestSerializer serializer];
    httpSessionManager.requestSerializer.timeoutInterval = kTimeOutInterval;
    httpSessionManager.responseSerializer = [AFHTTPResponseSerializer serializer];
    [httpSessionManager.requestSerializer setValue:@"application/json; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    if ([BDSettings getPassportAccessToken] != NULL && [[BDSettings getPassportAccessToken] length] > 10) {
        [httpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    }
    [httpSessionManager.requestSerializer setQueryStringSerializationWithBlock:^NSString *(NSURLRequest *request, id parameters, NSError * __autoreleasing * error) {
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:parameterDict options:NSJSONWritingPrettyPrinted error:error];
        NSString *argString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return argString;
    }];
    return httpSessionManager;
}


#pragma mark - 访客端接口

- (void)registerAnonymousUserWithAppkey:(NSString *)appkey
                            withSubdomain:(NSString *)subDomain
                            resultSuccess:(SuccessCallbackBlock)success
                             resultFailed:(FailedCallbackBlock)failed {
    
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:subDomain, @"subDomain", appkey, @"appkey", [BDSettings getClient], @"client", nil];
    //
    [_mHttpSessionManager GET:httpGetAnonymousUser parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
        
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"response dict:  %@", dict);
            
            NSString *username = [[dict objectForKey:@"data"] objectForKey:@"username"];
            NSString *uid = [[dict objectForKey:@"data"] objectForKey:@"uid"];
            NSString *nickname = [[dict objectForKey:@"data"] objectForKey:@"nickname"];
            NSString *avatar = [[dict objectForKey:@"data"] objectForKey:@"avatar"];
            
            if ([[username stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] > 0) {
                //
                [BDSettings setUsername:username];
                [BDSettings setUid:uid];
                [BDSettings setNickname:nickname];
                [BDSettings setAvatar:avatar];
                
                // 返回结果
                NSDictionary *dict = @{@"message":@"login success", @"status_code": @200,  @"data":  @"success"};
                success(dict);
            }
            else {
                NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
                NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
                failed(error);
            }
        
        } else {
            
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}


- (void)registerUser:(NSString *)username
           withNickname:(NSString *)nickname
           withPassword:(NSString *)password
          withSubDomain:(NSString *)subDomain
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            username, @"username",
                            nickname, @"nickname",
                            password, @"password",
                            subDomain, @"subDomain",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorRegister parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerUser:(NSString *)username
           withNickname:(NSString *)nickname
             withAvatar:(NSString *)avatar
           withPassword:(NSString *)password
          withSubDomain:(NSString *)subDomain
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            username, @"username",
                            nickname, @"nickname",
                            avatar,   @"avatar",
                            password, @"password",
                            subDomain, @"subDomain",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorRegister parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerEmail:(NSString *)email
        withNickname:(NSString *)nickname
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            email, @"email",
                            nickname, @"nickname",
                            password, @"password",
                            subDomain, @"subDomain",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorRegisterEmail parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
             withUid:(NSString *)uid
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            username, @"username",
                            nickname, @"nickname",
                            uid, @"uid",
                            password, @"password",
                            subDomain, @"subDomain",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorRegisterUid parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerAdmin:(NSString *)email
        withPassword:(NSString *)password
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            email, @"email",
                            password, @"password",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminRegister parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerMobile:(NSString *)mobile
             withEmail:(NSString *)email
          withNickname:(NSString *)nickname
        resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            mobile, @"mobile",
                            email, @"email",
                            nickname, @"nickname",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminRegisterMobile parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)bindMobile:(NSString *)mobile
         withEmail:(NSString *)email
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            mobile, @"mobile",
                            email, @"email",
                            @"nickname", @"nickname", // TODO: 替换真实参数
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminBindMobile parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)isWeChatRegistered:(NSString *)unionId
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            unionId, @"unionid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpIsWechatRegistered parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)registerWeChat:(NSString *)unionid
             withEmail:(NSString *)email
            withOpenId:(NSString *)openid
          withNickname:(NSString *)nickname
            withAvatar:(NSString *)avatar
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            unionid, @"unionid",
                            email, @"email",
                            openid, @"openid",
                            nickname, @"nickname",
                            avatar, @"avatar",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminRegisterWeChat parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

/// 绑定微信
/// @param unionid  <#mobile description#>
/// @param email <#email description#>
/// @param success <#success description#>
/// @param failed <#failed description#>
- (void)bindWeChat:(NSString *)unionid
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            unionid, @"unionid",
                            email, @"email",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminBindWeChat parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

/// 上传通讯录
//- (void)uploadMobile:(NSString *)mobile
//        withNickname:(NSString *)nickname
//       resultSuccess:(SuccessCallbackBlock)success
//        resultFailed:(FailedCallbackBlock)failed {
//    //
//    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
//                            mobile, @"mobile",
//                            nickname, @"nickname",
//                            [BDSettings getClient], @"client",
//                            nil];
//    //
//    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
//    //
//    [httpSessionManager POST:httpUploadMobile parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
//        //
//    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
//        //
//        if(responseObject) {
//            //
//            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict[@"message"]);
//            //
//            success(dict);
//        } else {
//            NSLog(@"%s", __PRETTY_FUNCTION__);
//        }
//    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
//        //
//        [self failError:error resultSuccess:^(NSDictionary *dict) {
//            success(dict);
//        } resultFailed:^(NSError *error) {
//            failed(error);
//        }];
//    }];
//}


/**
 http://blog.csdn.net/ios_wq/article/details/51285444
 */
- (void)authWithRole:(NSString *)role
        withUsername:(NSString *)username
        withPassword:(NSString *)password
          withAppkey:(NSString *)appkey
       withSubdomain:(NSString *)subdomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    _mHttpSessionManager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [_mHttpSessionManager.requestSerializer setAuthorizationHeaderFieldWithUsername:@"client" password:@"secret"];
//    NSLog(@"%s, Authorization:%@", __PRETTY_FUNCTION__, [_mHttpSessionManager.requestSerializer valueForHTTPHeaderField:@"Authorization"]);
    
    // 将请求参数放在请求的字典里
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           @"password", @"grant_type",
                           username, @"username",
                           password, @"password",
                           @"all", @"scope", // TODO: 根据角色区分scope值
                           nil];
    // 创建请求类
    [_mHttpSessionManager POST:[BDConfig getPasswordOAuthTokenUrl] parameters:param headers:nil progress:^(NSProgress * _Nonnull uploadProgress) {
        // 这里可以获取到目前数据请求的进度
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 发送登录成功通知
        [BDNotify notifyOAuthResult:YES];
        // 请求成功
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            //
            [BDSettings setRole:role];
            [BDSettings setUsername:username];
            [BDSettings setPassword:password];
            [BDSettings setSubdomain:subdomain];
            [BDSettings setAppkey:appkey];
            //
            [BDSettings setPassportAccessToken:[dict objectForKey:@"access_token"]];
            [BDSettings setPassportExpiresIn:[dict objectForKey:@"expires_in"]];
            [BDSettings setPassportRefreshToken:[dict objectForKey:@"refresh_token"]];
            [BDSettings setPassportTokenType:[dict objectForKey:@"token_type"]];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        // 发送登录失败通知
        [BDNotify notifyOAuthResult:NO];
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)authWithMobile:(NSString *)mobile
             withCode:(NSString *)code
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    _mHttpSessionManager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [_mHttpSessionManager.requestSerializer setAuthorizationHeaderFieldWithUsername:@"client" password:@"secret"];
//    NSLog(@"%s, Authorization:%@", __PRETTY_FUNCTION__, [_mHttpSessionManager.requestSerializer valueForHTTPHeaderField:@"Authorization"]);
    
    // 将请求参数放在请求的字典里
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           @"mobile", @"grant_type",
                           mobile, @"mobile",
                           code, @"code",
                           @"all", @"scope", // TODO: 根据角色区分scope值
                           nil];
    // 创建请求类
    [_mHttpSessionManager POST:[BDConfig getMobileOAuthTokenUrl] parameters:param headers:nil progress:^(NSProgress * _Nonnull uploadProgress) {
        // 这里可以获取到目前数据请求的进度
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 发送登录成功通知
        [BDNotify notifyOAuthResult:YES];
        // 请求成功
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            //
//            [BDSettings setRole:role];
//            [BDSettings setUsername:username];
//            [BDSettings setPassword:password];
//            [BDSettings setSubdomain:subdomain];
//            [BDSettings setAppkey:appkey];
//            //
            [BDSettings setPassportAccessToken:[dict objectForKey:@"access_token"]];
            [BDSettings setPassportExpiresIn:[dict objectForKey:@"expires_in"]];
            [BDSettings setPassportRefreshToken:[dict objectForKey:@"refresh_token"]];
            [BDSettings setPassportTokenType:[dict objectForKey:@"token_type"]];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        // 发送登录失败通知
        [BDNotify notifyOAuthResult:NO];
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)authWithEmail:(NSString *)email
             withCode:(NSString *)code
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
        _mHttpSessionManager.requestSerializer = [AFHTTPRequestSerializer serializer];
        [_mHttpSessionManager.requestSerializer setAuthorizationHeaderFieldWithUsername:@"client" password:@"secret"];
    //    NSLog(@"%s, Authorization:%@", __PRETTY_FUNCTION__, [_mHttpSessionManager.requestSerializer valueForHTTPHeaderField:@"Authorization"]);
        
        // 将请求参数放在请求的字典里
        NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                               @"email", @"grant_type",
                               email, @"email",
                               code, @"code",
                               @"all", @"scope", // TODO: 根据角色区分scope值
                               nil];
        // 创建请求类
        [_mHttpSessionManager POST:[BDConfig getEmailOAuthTokenUrl] parameters:param headers:nil progress:^(NSProgress * _Nonnull uploadProgress) {
            // 这里可以获取到目前数据请求的进度
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            // 发送登录成功通知
            [BDNotify notifyOAuthResult:YES];
            // 请求成功
            if(responseObject) {
                //
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
                NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
                //
    //            [BDSettings setRole:role];
    //            [BDSettings setUsername:username];
    //            [BDSettings setPassword:password];
    //            [BDSettings setSubdomain:subdomain];
    //            [BDSettings setAppkey:appkey];
                //
                [BDSettings setPassportAccessToken:[dict objectForKey:@"access_token"]];
                [BDSettings setPassportExpiresIn:[dict objectForKey:@"expires_in"]];
                [BDSettings setPassportRefreshToken:[dict objectForKey:@"refresh_token"]];
                [BDSettings setPassportTokenType:[dict objectForKey:@"token_type"]];
                //
                success(dict);
            } else {
                //
                [self successNoData:^(NSDictionary *dict) {
                    success(dict);
                }];
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            // 发送登录失败通知
            [BDNotify notifyOAuthResult:NO];
            //
            [self failError:error resultSuccess:^(NSDictionary *dict) {
                success(dict);
            } resultFailed:^(NSError *error) {
                failed(error);
            }];
        }];
    
}

- (void)authWithUnionId:(NSString *)unionId
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
        _mHttpSessionManager.requestSerializer = [AFHTTPRequestSerializer serializer];
        [_mHttpSessionManager.requestSerializer setAuthorizationHeaderFieldWithUsername:@"client" password:@"secret"];
    //    NSLog(@"%s, Authorization:%@", __PRETTY_FUNCTION__, [_mHttpSessionManager.requestSerializer valueForHTTPHeaderField:@"Authorization"]);
        
        // 将请求参数放在请求的字典里
        NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                               @"wechat", @"grant_type",
                               unionId, @"unionid",
                               @"all", @"scope", // TODO: 根据角色区分scope值
                               nil];
        // 创建请求类
        [_mHttpSessionManager POST:[BDConfig getWeChatOAuthTokenUrl] parameters:param headers:nil progress:^(NSProgress * _Nonnull uploadProgress) {
            // 这里可以获取到目前数据请求的进度
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            // 发送登录成功通知
            [BDNotify notifyOAuthResult:YES];
            // 请求成功
            if(responseObject) {
                //
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
                NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
                //
                [BDSettings setPassportAccessToken:[dict objectForKey:@"access_token"]];
                [BDSettings setPassportExpiresIn:[dict objectForKey:@"expires_in"]];
                [BDSettings setPassportRefreshToken:[dict objectForKey:@"refresh_token"]];
                [BDSettings setPassportTokenType:[dict objectForKey:@"token_type"]];
                //
                success(dict);
            } else {
                //
                [self successNoData:^(NSDictionary *dict) {
                    success(dict);
                }];
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            // 发送登录失败通知
            [BDNotify notifyOAuthResult:NO];
            //
            [self failError:error resultSuccess:^(NSDictionary *dict) {
                success(dict);
            } resultFailed:^(NSError *error) {
                failed(error);
            }];
        }];
}

- (void)requestMobileCode:(NSString *)mobile
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
//    NSLog(@"requestMobileCode url %@", [BDConfig getMobileCodeUrl]);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            mobile, @"mobile",
                            [BDSettings getClient], @"client",
                            nil];
    //
    _mHttpSessionManager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [_mHttpSessionManager GET:[BDConfig getMobileCodeUrl] parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestEmailCode:(NSString *)email
            resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            email, @"email",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:[BDConfig getEmailCodeUrl] parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestThreadWithWorkGroupWid:(NSString *)wId
                      resultSuccess:(SuccessCallbackBlock)success
                       resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            wId, @"wId",
                            BD_THREAD_REQUEST_TYPE_WORK_GROUP, @"type",
                            @"", @"aId",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorRequestThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestThreadWebRTCWithWorkGroupWid:(NSString *)wId
                                     webrtc:(int)webrtc
                              resultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSNumber *webrtcNumber = [NSNumber numberWithInt:webrtc];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            wId, @"wId",
                            BD_THREAD_REQUEST_TYPE_WORK_GROUP, @"type",
                            @"", @"aId",
                            webrtcNumber, @"webrtc",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorRequestThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestThreadWithAgentUid:(NSString *)uid
                        resultSuccess:(SuccessCallbackBlock)success
                         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"", @"wId",
                            BD_THREAD_REQUEST_TYPE_APPOINTED, @"type",
                            uid, @"aId",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorRequestThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestThread:(NSString *)workGroupWid
             withType:(NSString *)type
         withAgentUid:(NSString *)agentUid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            workGroupWid, @"wId",
                            type, @"type",
                            agentUid, @"aId",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorRequestThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestAgent:(NSString *)workGroupWid
//            withType:(NSString *)type
//        withAgentUid:(NSString *)agentUid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            workGroupWid, @"wId",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorRequestAgent parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}



- (void)getContactThread:(NSString *)cid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cid, @"cid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetContactThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getGroupThread:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetGroupThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)requestQuestionnairWithTid:(NSString *)tid
                           itemQid:(NSString *)qid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tId",
                            qid, @"itemQid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorQuestionnaire parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestChooseWorkGroup:(NSString *)wid
                     resultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            wid, @"wId",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorChooseWorkGroup parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestChooseWorkGroupLiuXue:(NSString *)wid
               withWorkGroupNickname:(NSString *)workGroupNickname
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            wid, @"wId",
                            workGroupNickname, @"nickname",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorChooseWorkGroupLiuXue parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)requestChooseWorkGroupLiuXueLBS:(NSString *)wid
                  withWorkGroupNickname:(NSString *)workGroupNickname
                           withProvince:(NSString *)province
                               withCity:(NSString *)city
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            wid, @"wId",
                            workGroupNickname, @"nickname",
                            province, @"province",
                            city, @"city",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorChooseWorkGroupLiuXueLBS parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)setNickname:(NSString *)nickname
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nickname, @"nickname",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpSetNickname parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            [BDSettings setNickname:nickname];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)setAvatar:(NSString *)avatar
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            avatar, @"avatar",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpSetAvatar parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            [BDSettings setAvatar:avatar];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)setDescription:(NSString *)description
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            description, @"description",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpSetDescription parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            [BDSettings setNickname:nickname];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFingerPrintWithUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorGetUserinfo parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}

- (void)getDeviceInfoByUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorDeviceInfo parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}


- (void)setFingerPrint:(NSString *)name
                   withKey:(NSString *)key
                 withValue:(NSString *)value
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            name, @"name",
                            key, @"key",
                            value, @"value",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorSetUserinfo parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@, %@", __PRETTY_FUNCTION__, dict, dict[@"message"]);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getWorkGroupStatus:(NSString *)wId
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            wId, @"wid",
                            nil];
    [_mHttpSessionManager GET:httpVisitorGetWorkGroupStatus parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
            
        } else {
            //
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            
            success(dict);
            
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getAgentStatus:(NSString *)agentUid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                             [BDSettings getClient], @"client",
                            agentUid, @"uid",
                            nil];
    [_mHttpSessionManager GET:httpVisitorGetAgentStatus parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        failed(error);
    }];
}

- (void)getUreadCount:(NSString *)wid
        resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                             [BDSettings getClient], @"client",
                             wid, @"wid",
                            nil];
    [_mHttpSessionManager GET:httpMessagesUnreadCount parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        failed(error);
    }];
}

- (void)getUreadCountVisitorWithResultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                             [BDSettings getClient], @"client",
                            nil];
    [_mHttpSessionManager GET:httpMessagesUnreadCountVisitor parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        failed(error);
    }];
}

- (void)getUreadCountAgentWithResultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                             [BDSettings getClient], @"client",
                            nil];
    [_mHttpSessionManager GET:httpMessagesUnreadCountAgent parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        failed(error);
    }];
}


/**
 访客端-查询访客所有未读消息
 */
- (void)getUreadMessagesVisitor:(NSInteger)page
                       withSize:(NSInteger)size
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            pageString, @"page",
                            sizeString, @"size",
                            nil];
    [_mHttpSessionManager GET:httpMessagesUnreadVisitor parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        failed(error);
    }];
}

/**
 客服端-查询客服所有未读消息
 */
- (void)getUreadMessagesAgent:(NSInteger)page
                     withSize:(NSInteger)size
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            pageString, @"page",
                            sizeString, @"size",
                            nil];
    [_mHttpSessionManager GET:httpMessagesUnreadAgent parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}



- (void)visitorGetThreadsPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                             [BDSettings getClient], @"client",
                            pageString, @"page",
                            nil];
    [_mHttpSessionManager GET:httpVisitorGetHistory parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        
            //
            NSMutableArray *threadArray = dict[@"data"][@"content"];
            for (NSDictionary *threadDict in threadArray) {
                BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                [[BDDBApis sharedInstance] insertThread:threadModel];
            }
            
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data", @"success", @"message", number201, @"status_code", nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)visitorRate:(NSString *)tId
          withScore:(NSInteger)score
           withNote:(NSString *)note
         withInvite:(BOOL)invite
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    
    //数据上行为json格式
    _mHttpSessionManager.requestSerializer = [AFJSONRequestSerializer serializer];
    //
    NSNumber *scoreNumber = [NSNumber numberWithInteger:score];
    NSNumber *inviteNumber = [NSNumber numberWithBool:invite];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            tId, @"tid",
                            scoreNumber, @"score",
                            note, @"note",
                            inviteNumber, @"invite",
                            nil];
    NSLog(@"%s, tid:%@, score:%@, invite:%@, param:%@", __PRETTY_FUNCTION__, tId, scoreNumber, inviteNumber, params);
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorRate parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}

#pragma mark - 客服端接口

- (void)initDataResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminInit parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            
            // 数据持久化 info/workGroups/queues/agentThreads/contactThreads/groupThreads
            NSDictionary *infoDict = dict[@"data"][@"info"];
            [BDSettings setUid:[infoDict objectForKey:@"uid"]];
            [BDSettings setUsername:[infoDict objectForKey:@"username"]];
            [BDSettings setNickname:[infoDict objectForKey:@"nickname"]];
            [BDSettings setAvatar:[infoDict objectForKey:@"avatar"]];
            [BDSettings setRealname:[infoDict objectForKey:@"realName"]];
            [BDSettings setSubdomain:[infoDict objectForKey:@"subDomain"]];
            [BDSettings setDescription:[infoDict objectForKey:@"description"]];
            [BDSettings setValidateUntilDate:[infoDict objectForKey:@"validateUntilDate"]];
            [BDSettings setAcceptStatus:[infoDict objectForKey:@"acceptStatus"]];
            [BDSettings setAutoReplyContent:[infoDict objectForKey:@"autoReplyContent"]];
            [BDNotify notifyProfileUpdate];
            // 有效日期
            NSString *validateUntilDateString = [infoDict objectForKey:@"validateUntilDate"];
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            NSDate *validateUntilDate = [dateFormatter dateFromString:validateUntilDateString];
//            NSDate *elierDate = [validateUntilDate earlierDate:[NSDate date]];
            NSComparisonResult result = [validateUntilDate compare:[NSDate date]];
            if (result == NSOrderedAscending) {
                // 断开长连接
                [[BDMQTTApis sharedInstance] disconnect];
                // TODO: 账号过期，需要续费
                NSLog(@"NSOrderedAscending");
                [BDNotify notifyOutOfDate];
                
            } else {
                // 没过期
                NSLog(@"not NSOrderedAscending");
            }
            //
            NSMutableArray *workGroupsArray = dict[@"data"][@"workGroups"];
            for (NSDictionary *workGroupDict in workGroupsArray) {
                BDWorkGroupModel *workGroupModel = [[BDWorkGroupModel alloc] initWithDictionary:workGroupDict];
                [[BDDBApis sharedInstance]  insertWorkGroup:workGroupModel];
            }
            
            NSMutableArray *queueArray = dict[@"data"][@"queues"][@"content"];
            for (NSDictionary *queueDict in queueArray) {
                BDQueueModel *queueModel = [[BDQueueModel alloc] initWithDictionary:queueDict];
                [[BDDBApis sharedInstance] insertQueue:queueModel];
            }
            
            NSMutableArray *groupsArray = dict[@"data"][@"groups"];
            for (NSDictionary *groupDict in groupsArray) {
                BDGroupModel *groupModel = [[BDGroupModel alloc] initWithDictionary:groupDict];
                [[BDDBApis sharedInstance] insertGroup:groupModel];
            }
            
            NSMutableArray *contactsArray = dict[@"data"][@"contacts"];
            for (NSDictionary *contactDict in contactsArray) {
                BDContactModel *contactModel = [[BDContactModel alloc] initWithDictionary:contactDict];
                [[BDDBApis sharedInstance] insertContact:contactModel];
            }

            NSMutableArray *agentThreadsArray = dict[@"data"][@"agentThreads"];
            for (NSDictionary *threadDict in agentThreadsArray) {
                BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                [[BDDBApis sharedInstance] insertThread:threadModel];
            }
            
            NSMutableArray *contactThreadArray = dict[@"data"][@"contactThreads"];
            for (NSDictionary *threadDict in contactThreadArray) {
                BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                [[BDDBApis sharedInstance] insertThread:threadModel];
            }
            
            NSMutableArray *groupThreadArray = dict[@"data"][@"groupThreads"];
            for (NSDictionary *threadDict in groupThreadArray) {
                BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                [[BDDBApis sharedInstance] insertThread:threadModel];
            }
            [BDNotify notifyThreadUpdate];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getAgentProfileResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpUserProfile parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            // 数据持久化 info/workGroups/queues/agentThreads/contactThreads/groupThreads
            NSDictionary *infoDict = dict[@"data"];
            [BDSettings setUid:[infoDict objectForKey:@"uid"]];
            [BDSettings setUsername:[infoDict objectForKey:@"username"]];
            [BDSettings setNickname:[infoDict objectForKey:@"nickname"]];
            [BDSettings setAvatar:[infoDict objectForKey:@"avatar"]];
            [BDSettings setRealname:[infoDict objectForKey:@"realName"]];
            [BDSettings setSubdomain:[infoDict objectForKey:@"subDomain"]];
            [BDSettings setDescription:[infoDict objectForKey:@"description"]];
            [BDSettings setValidateUntilDate:[infoDict objectForKey:@"validateUntilDate"]];
            [BDSettings setAcceptStatus:[infoDict objectForKey:@"acceptStatus"]];
            [BDSettings setAutoReplyContent:[infoDict objectForKey:@"autoReplyContent"]];
            [BDSettings setWelcomeTip:[infoDict objectForKey:@"welcomeTip"]];
            [BDNotify notifyProfileUpdate];
            // 有效日期
            NSString *validateUntilDateString = [infoDict objectForKey:@"validateUntilDate"];
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            NSDate *validateUntilDate = [dateFormatter dateFromString:validateUntilDateString];
//            NSDate *elierDate = [validateUntilDate earlierDate:[NSDate date]];
            NSComparisonResult result = [validateUntilDate compare:[NSDate date]];
            if (result == NSOrderedAscending) {
                // 断开长连接
                [[BDMQTTApis sharedInstance] disconnect];
                // TODO: 账号过期，需要续费
                NSLog(@"NSOrderedAscending");
                [BDNotify notifyOutOfDate];
                
            } else {
                // 没过期
                NSLog(@"not NSOrderedAscending");
            }
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getVisitorProfileResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpVisitorProfile parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            NSDictionary *infoDict = dict[@"data"];
            [BDSettings setUid:[infoDict objectForKey:@"uid"]];
            [BDSettings setUsername:[infoDict objectForKey:@"username"]];
            [BDSettings setNickname:[infoDict objectForKey:@"nickname"]];
            [BDSettings setAvatar:[infoDict objectForKey:@"avatar"]];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getUserProfileByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpUserProfileUid parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//            NSDictionary *infoDict = dict[@"data"];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getCustomerByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpCrmUid parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//            NSDictionary *infoDict = dict[@"data"];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)userOnline:(int)page withSize:(int)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSNumber *pageNum = [NSNumber numberWithInt:page];
    NSNumber *sizeNum = [NSNumber numberWithInt:size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageNum, @"page",
                            sizeNum, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpUserOnline parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)userDetail:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpUserDetail parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)agentThreadsResultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminThreads parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            [[BDDBApis sharedInstance] clearThreads];
            //
            id poi = dict[@"data"];
            if ([poi isKindOfClass:[NSDictionary class]]) {
//                NSLog(@"its probably a dictionary");
                // FIXME: [__NSCFBoolean objectForKeyedSubscript:]: unrecognized selector sent to instance 0x1fa119b00
                NSMutableArray *agentThreadsArray = dict[@"data"][@"agentThreads"];
                for (NSDictionary *threadDict in agentThreadsArray) {
                    BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                    [[BDDBApis sharedInstance] insertThread:threadModel];
                }

                NSMutableArray *contactThreadArray = dict[@"data"][@"contactThreads"];
                for (NSDictionary *threadDict in contactThreadArray) {
                    BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                    [[BDDBApis sharedInstance] insertThread:threadModel];
                }

                NSMutableArray *groupThreadArray = dict[@"data"][@"groupThreads"];
                for (NSDictionary *threadDict in groupThreadArray) {
                    BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                    [[BDDBApis sharedInstance] insertThread:threadModel];
                }
                [BDNotify notifyThreadUpdate];
                
            }else {
                NSLog(@"its a other class");
            }
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)agentThreadHistoryRecords:(int)page
                        withSize:(int)size
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
        [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
        //
        NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
        NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
        NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                                [BDSettings getClient], @"client",
                                pageString, @"page",
                                sizeString, @"size",
                                nil];
        //
        [_mHttpSessionManager GET:httpAdminThreadHistoryRecords parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
            //
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            if(responseObject){
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
    //            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//                [[BDDBApis sharedInstance] clearThreads];
                //
                NSMutableArray *agentThreadsArray = dict[@"data"][@"content"];
                for (NSDictionary *threadDict in agentThreadsArray) {
                    BDThreadModel *threadModel = [[BDThreadModel alloc] initWithDictionary:threadDict];
                    [[BDDBApis sharedInstance] insertThread:threadModel];
                }
                [BDNotify notifyThreadUpdate];
                //
                success(dict);
            } else {
                //
                [self successNoData:^(NSDictionary *dict) {
                    success(dict);
                }];
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            //
            [self failError:error resultSuccess:^(NSDictionary *dict) {
                success(dict);
            } resultFailed:^(NSError *error) {
                failed(error);
            }];
        }];
}

- (void)queryTodos:(int)page
          withSize:(int)size
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
        [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
        //
        NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
        NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
        NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                                [BDSettings getClient], @"client",
                                pageString, @"page",
                                sizeString, @"size",
                                nil];
        //
        [_mHttpSessionManager GET:httpTodoQuery parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
            //
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            if(responseObject){
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
                NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
                //
                success(dict);
            } else {
                //
                [self successNoData:^(NSDictionary *dict) {
                    success(dict);
                }];
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            //
            [self failError:error resultSuccess:^(NSDictionary *dict) {
                success(dict);
            } resultFailed:^(NSError *error) {
                failed(error);
            }];
        }];
}

- (void)queryTickets:(int)page
            withSize:(int)size
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
        [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
        //
        NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
        NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
        NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                                [BDSettings getClient], @"client",
                                pageString, @"page",
                                sizeString, @"size",
                                nil];
        //
        [_mHttpSessionManager GET:httpTodoQuery parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
            //
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            if(responseObject){
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
                NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
                //
                success(dict);
            } else {
                //
                [self successNoData:^(NSDictionary *dict) {
                    success(dict);
                }];
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            //
            [self failError:error resultSuccess:^(NSDictionary *dict) {
                success(dict);
            } resultFailed:^(NSError *error) {
                failed(error);
            }];
        }];
}

- (void)updateNickname:(NSString *)nickname
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nickname, @"nickname",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdateNickname parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateAutoReply:(BOOL)isAutoReply
            withContent:(NSString *)content
           withImageUrl:(NSString *)imageUrl
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
//    NSString *isAutoReplyString = [NSString stringWithFormat:@"%ld", (long)isAutoReply];
    NSNumber *isAutoReplyNum = [NSNumber numberWithBool:isAutoReply];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
//                            isAutoReplyString, @"isAutoReply",
                            isAutoReplyNum, @"autoReply",
//                            content, @"autoReplyContent",
                            content, @"content",
                            imageUrl, @"imageUrl",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminUpdateAutoReplyContent parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)setAcceptStatus:(NSString *)acceptStatus
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            acceptStatus, @"status",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminSetAcceptStatus parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getChatCodeResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetChatCodeUrl parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        // progress
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateWelcomeTip:(NSString *)welcomeTip
             resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            welcomeTip, @"welcomeTip",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdateWelcome parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updatePassword:(NSString *)password
             resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            password, @"password",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdatePassword parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            description, @"description",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdateDescription parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateCurrentThread:(NSString *)preTid
                      currentTid:(NSString *)tid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"preTid: %@, tid: %@", preTid, tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            preTid, @"preTid",
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadUpdateCurrent parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

#pragma mark - 标记会话

- (void)markTopThread:(NSString *)tid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadMarkTop parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] markTopThread:tid];
            }
            
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unmarkTopThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadUnmarkTop parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] unmarkTopThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markNoDisturbThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadMarkDisturb parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] markDisturbThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unmarkNoDisturbThread:(NSString *)tid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadUnmarkDisturb parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] unmarkDisturbThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markUnreadThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadMarkUnread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] markUnreadThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)unmarkUnreadThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadUnmarkUnread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] unmarkUnreadThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markDeletedThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminThreadMarkDeleted parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] markDeletedThread:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markDeletedMessage:(NSString *)mid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"mid: %@", mid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            mid, @"mid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminMessageMarkDeleted parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // 更新本地数据库
                [[BDDBApis sharedInstance] deleteMessage:mid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markClearThreadMessage:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSLog(@"tid: %@", tid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminMarkClearThreadMessage parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // TODO: 更新本地数据库
                [[BDDBApis sharedInstance] markClearMessage:tid];
            }
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markClearContactMessage:(NSString *)uid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminMarkClearContactMessage parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                
            }
            // 失败也可以更新本地数据库
            [[BDDBApis sharedInstance] deleteContactMessages:uid];
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        // 失败也可以更新本地数据库
        [[BDDBApis sharedInstance] deleteContactMessages:uid];
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)markClearGroupMessage:(NSString *)gid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminMarkClearGroupMessage parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                
            }
            // 失败也可以更新本地数据库
            [[BDDBApis sharedInstance] deleteGroupMessages:gid];
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        // 失败也可以更新本地数据库
        [[BDDBApis sharedInstance] deleteGroupMessages:gid];
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)agentCloseThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminAgentCloseThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            // TODO: 数据库删除，
            BOOL isThreadDeleted = [[BDDBApis sharedInstance] deleteThread:tid];
            // TODO: 通知UI中删除
            if (isThreadDeleted) {
                [BDNotify notifyThreadDelete:tid];
            }
            //
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)visitorCloseThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminVisitorCloseThread parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            // TODO: 数据库删除，
            BOOL isThreadDeleted = [[BDDBApis sharedInstance] deleteThread:tid];
            // TODO: 通知UI中删除
            if (isThreadDeleted) {
                [BDNotify notifyThreadDelete:tid];
            }
            //
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getQueuesPage:(NSUInteger)page
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            @"20", @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminQueues parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            [[BDDBApis sharedInstance] clearQueues];
            //
            NSMutableArray *queueArray = dict[@"data"][@"queues"][@"content"];
            for (NSDictionary *queueDict in queueArray) {
                BDQueueModel *queueModel = [[BDQueueModel alloc] initWithDictionary:queueDict];
                [[BDDBApis sharedInstance] insertQueue:queueModel];
            }
            [BDNotify notifyQueueUpdate];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getContactsResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminContacts parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            [[BDDBApis sharedInstance] clearContacts];
            //
            NSMutableArray *contactArray = dict[@"data"];
            for (NSDictionary *contactDict in contactArray) {
                BDContactModel *contactModel = [[BDContactModel alloc] initWithDictionary:contactDict];
                // 过滤掉当前登录用户
                if (![contactModel.uid isEqualToString:[BDSettings getUid]] && !contactModel.is_robot) {
                    [[BDDBApis sharedInstance] insertContact:contactModel];
                }
            }
            [BDNotify notifyContactUpdate];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getGroupsResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminGroups parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            // [[BDDBApis sharedInstance] clearContacts];
            //
            NSMutableArray *groupArray = dict[@"data"];
            for (NSDictionary *groupDict in groupArray) {
                //
                BDGroupModel *groupModel = [[BDGroupModel alloc] initWithDictionary:groupDict];
                [[BDDBApis sharedInstance] insertGroup:groupModel];
            }
            [BDNotify notifyGroupUpdate];
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            //            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getGroupDetail:(NSString *)gid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminGroupDetail parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"error", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getGroupMembers:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminGroupMembers parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            //            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)createGroup:(NSString *)nickname
               type:(NSString *)type
        selectedContacts:(NSArray *)selectedContacts
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nickname, @"nickname",
                            type, @"type",
                            selectedContacts, @"selectedContacts",
                            nil];
    
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupCreate parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            BDGroupModel *groupModel = [[BDGroupModel alloc] initWithDictionary:dict];
            [[BDDBApis sharedInstance] insertGroup:groupModel];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateGroupNickname:(NSString *)nickname
               withGroupGid:(NSString *)gid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nickname, @"nickname",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupUpdateNickname parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            // TODO: 更新本地会话列表中群组昵称
            [[BDDBApis sharedInstance] updateThreadGroupNickname:gid withNickname:nickname];
            [[BDDBApis sharedInstance] updateGroupNickname:gid withNickname:nickname];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateGroupAnnouncement:(NSString *)announcement
                    withGroupGid:(NSString *)gid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            announcement, @"announcement",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupUpdateAnnouncement parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateGroupDescription:(NSString *)description
                        withGroupGid:(NSString *)gid
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            description, @"description",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupUpdateDescription parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)inviteToGroup:(NSString *)uid
            withGroupGid:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupInvite parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)inviteListToGroup:(NSArray *)uidList
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uidList, @"uidList",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupInviteList parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)joinGroup:(NSString *)gid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupJoin parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)applyGroup:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupApply parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)approveGroupApply:(NSString *)nid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nid, @"nid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupApplyApprove parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)denyGroupApply:(NSString *)nid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nid, @"nid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupApplyDeny parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)kickGroupMember:(NSString *)uid
            withGroupGid:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupKick parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)muteGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupMute parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unmuteGroupMember:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupUnMute parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)setGroupAdmin:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupSetAdmin parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unsetGroupAdmin:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupUnSetAdmin parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)transferGroup:(NSString *)uid
          withGroupGid:(NSString *)gid
      withNeedApprove:(BOOL)approve
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    // 默认不需要审核
    NSNumber *needApprove = [NSNumber numberWithBool:approve];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            gid, @"gid",
                            needApprove, @"need_approve",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupTransfer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)acceptGroupTransfer:(NSString *)nid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nid, @"nid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupTransferAccept parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)rejectGroupTransfer:(NSString *)nid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            nid, @"nid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupTransferReject parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)withdrawGroup:(NSString *)gid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupWithdraw parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)dismissGroup:(NSString *)gid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminGroupDismiss parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)filterGroup:(NSString *)keyword resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminGroupFilter parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            //            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)filterGroupMembers:(NSString *)gid withKeyword:(NSString *)keyword resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpAdminGroupFilterMembers parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSNumber *number201 = [NSNumber numberWithInt:201];
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                                  @"success", @"message",
                                  number201, @"status_code",
                                  nil];
            //            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getNoticesPage:(NSUInteger)page withSize:(NSUInteger)size resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetNotices parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
            
        } else {
            //
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}




#pragma mark - 社交关系接口

- (void)getStrangersPage:(NSUInteger)page withSize:(NSUInteger)size resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetStrangers parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFollowsPage:(NSUInteger)page withSize:(NSUInteger)size resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetFollows parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFansPage:(NSUInteger)page withSize:(NSUInteger)size resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetFans parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFriendsPage:(NSUInteger)page withSize:(NSUInteger)size resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetFriends parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)addFollow:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAddFollow parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unFollow:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUnFollow parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)addFriend:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *notifyNum = [NSNumber numberWithBool:TRUE];
    NSNumber *approveNum = [NSNumber numberWithBool:FALSE];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            notifyNum, @"notify",
                            approveNum, @"approve",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAddFriend parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)removeFriend:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpRemoveFriend parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)isFollowed:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpIsFollowed parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getRelation:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetRelation parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)isShield:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpIsShield parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)isShielded:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpIsShielded parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)shield:(NSString *)uid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpShield parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unshield:(NSString *)uid
 resultSuccess:(SuccessCallbackBlock)success
  resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUnShield parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject){
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getBlocksPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetBlocks parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)addBlock:(NSString *)uid
        withNote:(NSString *)note
        withType:(NSString *)type
        withUuid:(NSString *)uuid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            type, @"type",
                            note, @"note",
                            uuid, @"uuid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAddBlock parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)unBlock:(NSString *)bid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            bid, @"bid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUnBlock parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

#pragma mark - 机器人

- (void)initAnswer:(NSString *)type
  withWorkGroupWid:(NSString *)wid
      withAgentUid:(NSString *)aid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    if ([wid isKindOfClass:[NSNull class]] || wid == nil || wid == NULL) {
        wid = @"";
    }
    if ([aid isKindOfClass:[NSNull class]] || aid == nil || aid == NULL) {
        aid = @"";
    }
    NSLog(@"type: %@, wid: %@, aid: %@", type, wid, aid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            type, @"type",
                            wid, @"wid",
                            aid, @"aid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpInitAnswer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)topAnswer:(NSString *)uid
        withThreadTid:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpTopAnswer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)queryAnswer:(NSString *)tid
     withQuestinQid:(NSString *)aid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            tid, @"tid",
                            aid, @"aid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpQueryAnswer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)messageAnswer:(NSString *)wid
          withMessage:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    if ([wid isKindOfClass:[NSNull class]] || wid == nil || wid == NULL) {
        wid = @"";
    }
//    if ([aid isKindOfClass:[NSNull class]] || aid == nil || aid == NULL) {
//        aid = @"";
//    }
//    NSLog(@"type: %@, wid: %@, aid: %@", type, wid, aid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
//                            type, @"type",
                            wid, @"wid",
//                            aid, @"aid",
                            content, @"content",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpMessageAnswer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)rateAnswer:(NSString *)aid
    withMessageMid:(NSString *)mid
          withRate:(BOOL)rate
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *rateNumber = [NSNumber numberWithBool:rate];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            aid, @"aid",
                            mid, @"mid",
                            rateNumber, @"rate",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpRateAnswer parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)leaveMessage:(NSString *)type
             withUid:(NSString *)uid
        withMobile:(NSString *)mobile
         withContent:(NSString *)content
        withImageUrl:(NSString *)imageUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            type, @"type",
                            uid, @"wid",
                            uid, @"aid",
                            mobile, @"mobile",
                            content, @"content",
                            imageUrl, @"imageUrl",
                            [BDSettings getClient], @"client",
                            nil];
//    NSLog(@"%s, %@, uid:%@, mobile:%@, content:%@", __PRETTY_FUNCTION__, params, uid, mobile, content);
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpLeaveMessage parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getLeaveMessagesPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetLeaveMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getLeaveMessagesVisitorPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetLeaveMessagesVisitor parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)replyLeaveMessage:(NSString *)lid
             withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *replied = [NSNumber numberWithBool:TRUE];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            lid, @"lid",
                            replied, @"replied",
                            content, @"reply",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpReplyLeaveMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

#pragma mark - 工单系统

- (void)getTicketCategories:(NSString *)uid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSLog(@"getTicketCategories %@", uid);
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetTicketCategories parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%@", dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getTickets:(NSUInteger)page
          withSize:(NSUInteger)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
//    NSLog(@"ticket %@", httpMineTicket);
    //
    [_mHttpSessionManager GET:httpMineTicket parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //            NSLog(@"%@", dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)createTicket:(NSString *)uid
          withUrgent:(NSString *)urgent
             withCid:(NSString *)cid
         withContent:(NSString *)content
          withMobile:(NSString *)mobile
           withEmail:(NSString *)email
         withFileUrl:(NSString *)fileUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            urgent, @"urgent",
                            cid, @"cid",
                            content, @"content",
                            mobile, @"mobile",
                            email, @"email",
                            fileUrl, @"fileUrl",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpCreateTicket parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            NSLog(@"%s", __PRETTY_FUNCTION__);
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

#pragma mark - 意见反馈

- (void)getFeedbackCategories:(NSString *)uid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetFeedbackCategories parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFeedbacks:(NSUInteger)page
          withSize:(NSUInteger)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    NSLog(@"feedback %@", httpMineFeedback);
    //
    [_mHttpSessionManager GET:httpMineFeedback parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //            NSLog(@"%@", dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)createFeedback:(NSString *)uid
               withCid:(NSString *)cid
           withContent:(NSString *)content
            withMobile:(NSString *)mobile
//             withEmail:(NSString *)email
           withFileUrl:(NSString *)fileUrl
  resultSuccess:(SuccessCallbackBlock)success
   resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            cid, @"cid",
                            content, @"content",
                            mobile, @"mobile",
//                            email, @"email",
                            fileUrl, @"fileUrl",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpCreateFeedback parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            NSLog(@"%s", __PRETTY_FUNCTION__);
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getFeedbackHistoriesWithResultSuccess:(SuccessCallbackBlock)success
                                  resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpFeedbackHistory parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


#pragma mark - 帮助中心

- (void)getSupportCategories:(NSString *)uid
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetSupportCategories parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getCategoryDetail:(NSString *)cid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cid, @"cid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetCategoryDetail parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getArticles:(NSString *)uid
           withType:(NSString *)type
           withPage:(NSUInteger)page
           withSize:(NSUInteger)size
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSString *sizeString = [NSString stringWithFormat:@"%ld", (long)size];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            type, @"type",
                            pageString, @"page",
                            sizeString, @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetArticles parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getArticleDetail:(NSString *)aid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            aid, @"aid",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetArticleDetail parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)searchArticle:(NSString *)uid
          withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            content, @"content",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpSearchArticle parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)rateArticle:(NSString *)aid
           withRate:(BOOL)rate
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *rateNumber = [NSNumber numberWithBool:rate];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            aid, @"aid",
                            rateNumber, @"rate",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpRateArticle parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}

#pragma mark - 常用语

- (void)getCuwsWithResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetCuws parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)createCuw:(int)categoryId
         withName:(NSString *)name
      withContent:(NSString *)content
      resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *categoryIdNum = [NSNumber numberWithInt:categoryId];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            categoryIdNum, @"categoryId",
                            name, @"name",
                            content, @"content",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpCreateCuw parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateCuw:(int)cuwid
         withName:(NSString *)name
      withContent:(NSString *)content
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *cuwIdNum = [NSNumber numberWithInt:cuwid];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cuwIdNum, @"id",
                            name, @"name",
                            content, @"content",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdateCuw parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}

- (void)deleteCuw:(int)cuwid
      resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSNumber *cuwIdNum = [NSNumber numberWithInt:cuwid];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cuwIdNum, @"id",
                            [BDSettings getClient], @"client",
                            nil];
    //添加多的请求格式
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpDeleteCuw parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
        }
        else {
            //
            NSDictionary *userinfoDict = [NSDictionary dictionaryWithObject:@"ERROR" forKey:NSLocalizedDescriptionKey];
            NSError *error = [NSError errorWithDomain:BD_ERROR_WITH_DOMAIN code:0 userInfo:userinfoDict];
            failed(error);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
    
}

#pragma mark - WebRTC



#pragma mark - 公共接口

- (BOOL)isNetworkReachable {
    return [_mReachabilityManager isReachable];
}

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
//           resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_TEXT toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
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
//            resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_IMAGE toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//- (void)sendFileMessage:(NSString *)content
//                  toTid:(NSString *)tId
//                localId:(NSString *)localId
//            sessionType:(NSString *)sessiontype
//                 format:(NSString *)format
//               fileName:(NSString *)fileName
//               fileSize:(NSString *)fileSize
//          resultSuccess:(SuccessCallbackBlock)success
//           resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_FILE toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:format fileName:fileName fileSize:fileSize destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//              success(dict);
//          } resultFailed:^(NSError *error) {
//              failed(error);
//          }];
//}
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
//            resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_VOICE toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:voiceLength format:format fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//- (void)sendCommodityMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_COMMODITY toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//- (void)sendRedPacketMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_RED_PACKET toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//- (void)sendPreviewMessage:(NSString *)content
//                     toTid:(NSString *)tId
//                   localId:(NSString *)localId
//               sessionType:(NSString *)sessiontype
//             resultSuccess:(SuccessCallbackBlock)success
//              resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:content type:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//- (void)sendReceiptMessage:(NSString *)mid
//                     toTid:(NSString *)tId
//                   localId:(NSString *)localId
//               sessionType:(NSString *)sessiontype
//             resultSuccess:(SuccessCallbackBlock)success
//              resultFailed:(FailedCallbackBlock)failed {
//    //
//    [self sendMessage:mid type:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT toTid:tId localId:localId
//          sessionType:sessiontype voiceLength:0 format:@"" fileName:@"" fileSize:@"" destroyAfterReading:FALSE destroyAfterLength:0 resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}


/**
 同步发送消息
 */
- (void)sendMessage:(NSString *)json
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            json, @"json",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpSendMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getMessageWithUser:(NSString *)uid
                  withPage:(NSInteger)page
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            pageString, @"page",
                            @"20", @"size",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetUserMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getMessageWithUser:(NSString *)uid
                  withId:(NSInteger)messageid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *messageIdString = [NSString stringWithFormat:@"%ld", (long)messageid];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            uid, @"uid",
                            messageIdString, @"id",
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpGetUserMessagesFrom parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
            
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getMessageWithContact:(NSString *)cid
                     withPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cid, @"cid",
                            pageString, @"page",
                            @"20", @"size",
                            [BDSettings getClient], @"client",
                            nil];
    
    [_mHttpSessionManager GET:httpGetContactMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
            
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getMessageWithContact:(NSString *)cid
                     withId:(NSInteger)messageid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *messageIdString = [NSString stringWithFormat:@"%ld", (long)messageid];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            cid, @"cid",
                            messageIdString, @"id",
                            [BDSettings getClient], @"client",
                            nil];
    
    [_mHttpSessionManager GET:httpGetContactMessagesFrom parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
            
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getMessageWithGroup:(NSString *)gid
                   withPage:(NSInteger)page
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *pageString = [NSString stringWithFormat:@"%ld", (long)page];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            pageString, @"page",
                            @"20", @"size",
                            [BDSettings getClient], @"client",
                            nil];
    
    [_mHttpSessionManager GET:httpGetGroupMessages parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)getMessageWithGroup:(NSString *)gid
                   withId:(NSInteger)messageid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSString *messageIdString = [NSString stringWithFormat:@"%ld", (long)messageid];
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            gid, @"gid",
                            messageIdString, @"id",
                            [BDSettings getClient], @"client",
                            nil];
    
    [_mHttpSessionManager GET:httpGetGroupMessagesFrom parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            //
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // data
                NSMutableArray *messageArray = dict[@"data"][@"content"];
                for (NSDictionary *messageDict in messageArray) {
                    BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
                    [[BDDBApis sharedInstance] insertMessage:messageModel];
                }
                success(dict);
            } else {
                // TODO: 错误
                NSString *message = [dict objectForKey:@"message"];
                NSLog(@"error: %@", message);
                NSString *domain = message;
                NSString *desc = NSLocalizedString(@"error", @"error");//NSLocalizedString国际化
                NSDictionary *userInfo = @{NSLocalizedDescriptionKey : desc };
                NSError *error = [NSError errorWithDomain:domain code:-101 userInfo:userInfo];
                failed(error);
            }
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)uploadImageData:(NSData *)imageData
          withImageName:(NSString *)imageName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    
//    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:httpVisitorUploadImage parameters:nil headers:nil constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        
        [formData appendPartWithFileData:imageData name:@"file" fileName:imageName mimeType:@"image/png"];
        [formData appendPartWithFormData:[imageName dataUsingEncoding:NSUTF8StringEncoding] name:@"file_name"];
        [formData appendPartWithFormData:[[BDSettings getUsername] dataUsingEncoding:NSUTF8StringEncoding] name:@"username"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
        NSString *percentage = [NSString stringWithFormat:@"%lld", uploadProgress.completedUnitCount*100/uploadProgress.totalUnitCount];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, percentage);

        [BDNotify notifyUploadPercentage:percentage withLocalId:localId];
//        [[CSDBApis sharedInstance] updateMessageImageId:msgid percentage:percentage];
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        // NSLog(@"%s %@", __PRETTY_FUNCTION__, responseObject);
        if (responseObject) {
            success(responseObject);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)uploadAvatarData:(NSData *)imageData
             withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    
    //    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
//    使用用户uid作为头像名称，以保证用户更换头像之后，客户端总是显示最新头像
    NSString *imageName = [BDSettings getUid];
    NSString *username = [BDSettings getUsername];
    
    [manager POST:httpVisitorUploadAvatar parameters:nil headers:nil constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        
        [formData appendPartWithFileData:imageData name:@"file" fileName:imageName mimeType:@"image/png"];
        [formData appendPartWithFormData:[imageName dataUsingEncoding:NSUTF8StringEncoding] name:@"file_name"];
        [formData appendPartWithFormData:[username dataUsingEncoding:NSUTF8StringEncoding] name:@"username"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
        NSString *percentage = [NSString stringWithFormat:@"%lld", uploadProgress.completedUnitCount*100/uploadProgress.totalUnitCount];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, percentage);
        
        [BDNotify notifyUploadPercentage:percentage withLocalId:localId];
        //        [[CSDBApis sharedInstance] updateMessageImageId:msgid percentage:percentage];
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // NSLog(@"%s %@", __PRETTY_FUNCTION__, responseObject);
        if (responseObject) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)uploadVoiceData:(NSData *)voiceData
          withVoiceName:(NSString *)voiceName
            withLocalId:(NSString *)localId
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:httpVisitorUploadVoice parameters:nil headers:nil constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        
        [formData appendPartWithFileData:voiceData name:@"file" fileName:voiceName mimeType:@"audio/amr"];
        [formData appendPartWithFormData:[voiceName dataUsingEncoding:NSUTF8StringEncoding] name:@"file_name"];
        [formData appendPartWithFormData:[[BDSettings getUsername] dataUsingEncoding:NSUTF8StringEncoding] name:@"username"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
        NSString *percentage = [NSString stringWithFormat:@"%lld", uploadProgress.completedUnitCount*100/uploadProgress.totalUnitCount];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, percentage);
        
        [BDNotify notifyUploadPercentage:percentage withLocalId:localId];
        //        [[CSDBApis sharedInstance] updateMessageImageId:msgid percentage:percentage];
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // NSLog(@"%s %@", __PRETTY_FUNCTION__, responseObject);
        if (responseObject) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)uploadFileData:(NSData *)fileData
         withFileName:(NSString *)fileName
           withLocalId:(NSString *)localId
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    NSLog(@"%s, fileName: %@", __PRETTY_FUNCTION__, fileName);
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:httpVisitorUploadFile parameters:nil headers:nil constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        
        [formData appendPartWithFileData:fileData name:@"file" fileName:fileName mimeType:@"audio/amr"];
        [formData appendPartWithFormData:[fileName dataUsingEncoding:NSUTF8StringEncoding] name:@"file_name"];
        [formData appendPartWithFormData:[[BDSettings getUsername] dataUsingEncoding:NSUTF8StringEncoding] name:@"username"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
        NSString *percentage = [NSString stringWithFormat:@"%lld", uploadProgress.completedUnitCount*100/uploadProgress.totalUnitCount];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, percentage);
        
        [BDNotify notifyUploadPercentage:percentage withLocalId:localId];
        //        [[CSDBApis sharedInstance] updateMessageImageId:msgid percentage:percentage];
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // NSLog(@"%s %@", __PRETTY_FUNCTION__, responseObject);
        if (responseObject) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)uploadVideoData:(NSData *)videoData
          withVideoName:(NSString *)videoName
            withLocalId:(NSString *)localId
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    NSLog(@"%s, videoName: %@", __PRETTY_FUNCTION__, videoName);
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:httpVisitorUploadVideo parameters:nil headers:nil constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        
        [formData appendPartWithFileData:videoData name:@"file" fileName:videoName mimeType:@"video/mp4"];
        [formData appendPartWithFormData:[videoName dataUsingEncoding:NSUTF8StringEncoding] name:@"file_name"];
        [formData appendPartWithFormData:[[BDSettings getUsername] dataUsingEncoding:NSUTF8StringEncoding] name:@"username"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
        NSString *percentage = [NSString stringWithFormat:@"%lld", uploadProgress.completedUnitCount*100/uploadProgress.totalUnitCount];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, percentage);
        
        [BDNotify notifyUploadPercentage:percentage withLocalId:localId];
//        [[CSDBApis sharedInstance] updateMessageImageId:msgid percentage:percentage];

    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // NSLog(@"%s %@", __PRETTY_FUNCTION__, responseObject);
        if (responseObject) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void) uploadDeviceInfo {
    
//    防止重复发送设备信息
    if ([BDSettings hasSetDeviceInfo]) {
        return;
    }
    
    //设备相关信息的获取
    NSString *os_name = [[UIDevice currentDevice] systemName];
    NSString *os_version = [[UIDevice currentDevice] systemVersion];
    
    NSString *device_name = [[UIDevice currentDevice] name];
    NSString *device_model = [BDUtils deviceVersion];
//    NSString *device_localmodel = [[UIDevice currentDevice] localizedModel];
    
    //app应用相关信息的获取
    NSDictionary *dicInfo = [[NSBundle mainBundle] infoDictionary];
    NSString *app_name = [dicInfo objectForKey:@"CFBundleDisplayName"];
    NSString *app_version = [dicInfo objectForKey:@"CFBundleShortVersionString"];
    
    //Getting the User’s Language
    NSArray *languageArray = [NSLocale preferredLanguages];
    NSString *app_language = [languageArray objectAtIndex:0];
    
    NSLocale *locale = [NSLocale currentLocale];
    NSString *app_country = [locale localeIdentifier];
    
//    NSLog(@"device_name:%@, system_name:%@, system_version:%@, device_model:%@, device_localmodel:%@, app_name:%@, app_version:%@, app_laguage:%@, app_country:%@", device_name, os_name, os_version, device_model,  device_localmodel, app_name, app_version, app_language, app_country);
    
    //数据上行为json格式
    _mHttpSessionManager.requestSerializer = [AFJSONRequestSerializer serializer];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            
                            os_name, @"os",
                            os_version, @"osVersion",
                            
                            device_name, @"deviceName",
                            device_model, @"deviceModel",
                            
                            app_name, @"appName",
                            app_version, @"appVersion",
                            app_language, @"language",
                            app_country, @"appCountry",
                            
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpVisitorSetDeviceinfo parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if(responseObject) {
//            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
//            NSLog(@"%s, %@, %@", __PRETTY_FUNCTION__, dict, dict[@"message"]);
//            success(dict);
            
            [BDSettings setDeviceInfo:true];
            
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
//        failed(error);
    }];
}

- (void)logoutResultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpAdminLogout parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        [BDUtils clearOut];
        
        if(responseObject) {
            
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
            success(dict);
            
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [BDUtils clearOut];
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getWXAccessToken:(NSString *)code
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    
    NSString *accessTokenUrl = @"https://api.weixin.qq.com/sns/oauth2/access_token";
    //[NSString stringWithFormat:@"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=%@&grant_type=authorization_code", code];
    //
    NSString *kAppID = @"wx397e52c3b24b613d";
    NSString *kAppSecret = @"86f9a078a958d3583b022e507092e678";
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            kAppID, @"appid",
                            kAppSecret, @"secret",
                            code, @"code",
                            @"authorization_code", @"grant_type",
                            nil];
    //
    [_mHttpSessionManager GET:accessTokenUrl parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

    
- (void)refreshWXAccessToken:(NSString *)refreshToken
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    NSLog(@"refreshWXAccessToken");
    //
    NSString *accessTokenUrl = @"https://api.weixin.qq.com/sns/oauth2/refresh_token";
    //[NSString stringWithFormat:@"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN"];
    //
    NSString *kAppID = @"wx397e52c3b24b613d";
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            kAppID, @"appid",
                            refreshToken, @"refresh_token",
                            @"refresh_token", @"grant_type",
                            nil];
    
    [_mHttpSessionManager GET:accessTokenUrl parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}
    
    
- (void)isWxAccessTokenValid:(NSString *)accessToken
                  withOpenId:(NSString *)openId
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    NSString *accessTokenUrl = @"https://api.weixin.qq.com/sns/auth";
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            openId, @"openid",
                            accessToken, @"access_token",
                            nil];
    
    [_mHttpSessionManager GET:accessTokenUrl parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)getWxUserinfo:(NSString *)accessToken
           withOpenId:(NSString *)openId
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    NSString *accessTokenUrl = @"https://api.weixin.qq.com/sns/userinfo";
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            openId, @"openid",
                            accessToken, @"access_token",
                            nil];
    //
    [_mHttpSessionManager GET:accessTokenUrl parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        // 请求成功
        if(responseObject){
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        }
        else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


#pragma mark - device token

- (void)isTokenUploadedResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [_mHttpSessionManager.requestSerializer setValue:[NSString stringWithFormat:@"Bearer %@", [BDSettings getPassportAccessToken]] forHTTPHeaderField:@"Authorization"];
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getClient], @"client",
                            nil];
    //
    [_mHttpSessionManager GET:httpIsDeviceTokenUploaded parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            success(dict);
        } else {
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}

- (void)updateDeviceToken:(NSString *)deviceToken
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    
    NSString *build = @"";
#ifdef DEBUG
    //do sth.
    build = BD_IOS_BUILD_DEBUG;
#else
    //do sth.
    build = BD_IOS_BUILD_RELEASE;
#endif
    //
//    NSLog(@"deviceToken: %@, build: %@", deviceToken, build);
    // TODO: 首先将deviceToken跟本地存储token作对比，如果相等，则没有必要重复上传
//    if ([deviceToken isEqualToString:[BDSettings getDeviceToken]]) {
//        //
//        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:deviceToken, @"data",
//                              @"success", @"已经存在，不需要重复上传",
//                              @200, @"status_code",
//                              nil];
//        success(dict);
//        return;
//    }
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            [BDSettings getAppkey], @"appkey",
                            build, @"build",
                            deviceToken, @"token",
                            [BDSettings getClient], @"client",
                            nil];
    NSLog(@"url: %@, params: %@, appkey: %@, build: %@, devicetoken: %@, client: %@",
          httpUpdateDeviceToken, params, [BDSettings getAppkey], build, deviceToken, [BDSettings getClient]);
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpUpdateDeviceToken parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            // 本地持久化
            [BDSettings setDeviceToken:deviceToken];
            //
            NSLog(@"update device:  %@", dict);
            //
            success(dict);
        } else {
            NSLog(@"%s", __PRETTY_FUNCTION__);
            //
            [self successNoData:^(NSDictionary *dict) {
                success(dict);
            }];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)deleteDeviceTokenResultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    
    NSString *build = @"";
#ifdef DEBUG
    //do sth.
    build = BD_IOS_BUILD_DEBUG;
#else
    //do sth.
    build = BD_IOS_BUILD_RELEASE;
#endif
    //
    NSDictionary *params = [NSDictionary dictionaryWithObjectsAndKeys:
                            build, @"build",
                            [BDSettings getClient], @"client",
                            nil];
    //
    AFHTTPSessionManager *httpSessionManager = [self getPostHttpSessionManager:params];
    //
    [httpSessionManager POST:httpDeleteDeviceToken parameters:params headers:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        //
    } success:^(NSURLSessionDataTask * _Nonnull task, id _Nullable responseObject) {
        //
        if(responseObject) {
            //
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableContainers error:nil];
            // 清空本地deviceToken
            [BDSettings setDeviceToken:@""];
            //
            success(dict);
        } else {
            //
            NSLog(@"%s", __PRETTY_FUNCTION__);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        //
        [self failError:error resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }];
}


- (void)cancelAllHttpRequest {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    for (NSURLSessionTask *task in _mHttpSessionManager.tasks) {
        [task cancel];
    }
    // Download task cancellation:
//    for (NSURLSessionTask *task in httpSessionManager.downloadTasks) {
//        [task cancel];
//    }
    // Upload task cancellation:
//    for (NSURLSessionTask *task in httpSessionManager.uploadTasks) {
//        [task cancel];
//    }
}


- (void)successNoData:(SuccessCallbackBlock)success {
    NSNumber *number201 = [NSNumber numberWithInt:201];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"no data", @"data",
                          @"success", @"message",
                          number201, @"status_code",
                          nil];
    success(dict);
}

- (void)failError:(NSError *)error
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    NSInteger errorCode = [error.userInfo[AFNetworkingOperationFailingURLResponseErrorKey] statusCode];
    NSLog(@"%s errorCode1: %ld, errorCode2: %ld, description: %@", __PRETTY_FUNCTION__, (long)errorCode, (long)error.code, error.localizedDescription);
    //
    if (errorCode == 401) {
        //
        [BDUtils clearOut];
        //
        NSNumber *number401 = [NSNumber numberWithInt:401];
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"false", @"data",
                              @"token已经过期，请重新登录", @"message",
                              number401, @"status_code", nil];
        success(dict);
        
    } else {
        failed(error);
    }
//    else if (error.code == -1001) {
//        //
//        NSNumber *number1001 = [NSNumber numberWithInt:-1001];
//        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"false", @"data",
//                              @"网络请求超时", @"message",
//                              number1001, @"status_code", nil];
//        success(dict);
//    } else if (error.code == -1005) {
//        //
//        NSNumber *number1005 = [NSNumber numberWithInt:-1005];
//        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"false", @"data",
//                              @"无网络连接", @"message",
//                              number1005, @"status_code", nil];
//        success(dict);
//    } else {
//        //
//        failed(error);
//    }
}


@end









