//
//  KFDSThreadModel.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDThreadModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

- (instancetype)initWithKeFuRequestDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *uu_id;
// 服务器端主键
//@property(nonatomic, strong) NSNumber *server_id;
// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *tid;
//
@property(nonatomic, strong) NSString *topic;
// 公众号、小程序 token
@property(nonatomic, strong) NSString *token;
// 来自stomp的session id
@property(nonatomic, strong) NSString *session_id;
// 最新消息内容
@property(nonatomic, strong) NSString *content;
// 最新消息时间戳
@property(nonatomic, strong) NSString *timestamp;
//@property(nonatomic, strong) NSDate *timestamp;
// 未读消息数
@property(nonatomic, strong) NSNumber *unread_count;
// 是否是当前会话，客户端点击会话之后，通知服务器并保存，同时通知其他所有端
@property(nonatomic, strong) NSNumber *is_current;
// 会话类型: 工作组会话、同事一对一、群组会话
@property(nonatomic, strong) NSString *type;
// 访客、联系人、群组 昵称
@property(nonatomic, strong) NSString *nickname;
// 访客、联系人、群组 头像
@property(nonatomic, strong) NSString *avatar;
// 对应队列
@property(nonatomic, strong) NSString *queue_qid;
// 访客，只能有一个访客
@property(nonatomic, strong) NSString *visitor_uid;
// 访客来源客户端
//@property(nonatomic, strong) NSString *visitor_client;
@property(nonatomic, strong) NSString *client;
// 一对一会话：Contact
@property(nonatomic, strong) NSString *contact_uid;
// 群组会话：Group
@property(nonatomic, strong) NSString *group_gid;
// 主客服，accept会话的客服账号，或被转接客服
@property(nonatomic, strong) NSString *agent_uid;
// 所属工作组
@property(nonatomic, strong) NSString *workgroup_wid;
// 会话开始时间
@property(nonatomic, strong) NSString *started_at;
// 会话是否结束
@property(nonatomic, strong) NSNumber *is_closed;
// 是否为系统自动关闭
@property(nonatomic, strong) NSNumber *is_auto_close;
// 会话结束时间
@property(nonatomic, strong) NSString *closed_at;
// 当前登录用户Uid
@property(nonatomic, strong) NSString *current_uid;
// 输入草稿
@property(nonatomic, strong) NSString *craft; // 草稿, TODO: 暂定保存在content字段中
// 是否置顶
@property(nonatomic, strong) NSNumber *is_mark_top;
// 是否设置消息免打扰
@property(nonatomic, strong) NSNumber *is_mark_disturb;
// 是否标记未读
@property(nonatomic, strong) NSNumber *is_mark_unread;
// 是否标记删除
@property(nonatomic, strong) NSNumber *is_mark_deleted;
// 是否临时会话
@property(nonatomic, strong) NSNumber *is_temp;


@end




