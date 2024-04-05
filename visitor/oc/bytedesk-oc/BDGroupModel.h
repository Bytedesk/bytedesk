//
//  BDGroupModel.h
//  bytedesk-core
//
//  Created by bytedesk.com on 2018/11/28.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDGroupModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *uu_id;
// 服务器端主键
@property(nonatomic, strong) NSNumber *server_id;
// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *gid;
// 群昵称
@property(nonatomic, strong) NSString *nickname;
// 头像
@property(nonatomic, strong) NSString *avatar;
// 类型：群、讨论组
@property(nonatomic, strong) NSString *type;
// 当前成员数
@property(nonatomic, strong) NSNumber *member_count;
// 群简介
@property(nonatomic, strong) NSString *mdescription;
// 群公告
@property(nonatomic, strong) NSString *announcement;
// 群组是否已经解散
@property(nonatomic, strong) NSNumber *is_dismissed;
// 当前登录用户Uid
@property(nonatomic, strong) NSString *current_uid;

@end

