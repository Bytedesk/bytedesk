//
//  BDWorkGroupModel.h
//  bytedesk-core
//
//  Created by bytedesk.com on 2018/11/27.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDWorkGroupModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *uu_id;
// 服务器端主键
@property(nonatomic, strong) NSNumber *server_id;
// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *wid;
// 工作组名称
@property(nonatomic, strong) NSString *nickname;
// 默认头像
@property(nonatomic, strong) NSString *avatar;
// 是否默认机器人接待
@property(nonatomic, strong) NSNumber *is_default_robot;
// 是否无客服在线时，启用机器人接待
@property(nonatomic, strong) NSNumber *is_offline_robot;
// 宣传语，对话框顶部，
@property(nonatomic, strong) NSString *slogan;
// 进入页面欢迎语
@property(nonatomic, strong) NSString *welcome_tip;
// 接入客服欢迎语
@property(nonatomic, strong) NSString *accept_tip;
// 非工作时间提示
@property(nonatomic, strong) NSString *non_working_time_tip;
// 离线提示
@property(nonatomic, strong) NSString *offline_tip;
// 客服关闭会话提示语
@property(nonatomic, strong) NSString *close_tip;
// 会话自动关闭会话提示语
@property(nonatomic, strong) NSString *auto_close_tip;
// 是否强制评价
@property(nonatomic, strong) NSNumber *is_force_rate;
// 路由类型
@property(nonatomic, strong) NSString *route_type;
// 是否是系统分配的默认工作组，不允许删除
@property(nonatomic, strong) NSNumber *is_default;
// 描述、介绍, 对话框 右侧 "关于我们"
@property(nonatomic, strong) NSString *about;
// 左上角，昵称下面描述语
@property(nonatomic, strong) NSString *mdescription;
// 当前登录用户Uid
@property(nonatomic, strong) NSString *current_uid;

@end
