//
//  KFDSContactModel.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDContactModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *uu_id;
// 服务器端主键
@property(nonatomic, strong) NSNumber *server_id;
// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *uid;
// 全平台唯一，公司子账号通过添加@subDomain来区分唯一性
@property(nonatomic, strong) NSString *username;
// 邮箱
@property(nonatomic, strong) NSString *email;
// 手机号
@property(nonatomic, strong) NSString *mobile;
// 对话页面显示
@property(nonatomic, strong) NSString *nickname;
// 后台统计显示
@property(nonatomic, strong) NSString *real_name;
// 头像
@property(nonatomic, strong) NSString *avatar;
// 企业号
@property(nonatomic, strong) NSString *sub_domain;
// 长连接状态
@property(nonatomic, strong) NSString *connection_status;
// 客服设置接待状态
@property(nonatomic, strong) NSString *accept_status;
// false 为离职禁止登录，true 为在职
@property(nonatomic, assign) bool is_enabled;
// 是否是机器人, 默认非机器人
@property(nonatomic, assign) bool is_robot;
//// 进入页面欢迎语
//@property(nonatomic, strong) NSString *welcome_tip;
//// 是否自动回复
//@property(nonatomic, strong) NSNumber *is_auto_reply;
//// 自动回复内容
//@property(nonatomic, strong) NSString *auto_reply_content;
//// 个性签名
//@property(nonatomic, strong) NSString *mdescription;
//// 同时最大接待会话数目, 默认10个
//@property(nonatomic, strong) NSNumber *max_thread_count;
// 当前登录用户Uid
@property(nonatomic, strong) NSString *current_uid;

@end
