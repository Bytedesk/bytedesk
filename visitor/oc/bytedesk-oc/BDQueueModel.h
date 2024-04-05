//
//  KFDSQueueModel.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDQueueModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *uu_id;
// 服务器端主键
@property(nonatomic, strong) NSNumber *server_id;
// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *qid;
// 访客昵称
@property(nonatomic, strong) NSString *nickname;
// 访客头像
@property(nonatomic, strong) NSString *avatar;
// 访客uid
@property(nonatomic, strong) NSString *visitor_uid;
// 访客来源客户端
@property(nonatomic, strong) NSString *visitor_client;
// 客服uid
@property(nonatomic, strong) NSString *agent_uid;
// 客服接入端
@property(nonatomic, strong) NSString *agent_client;
// 所属thread tid
@property(nonatomic, strong) NSString *thread_tid;
// 所属工作组 wid
@property(nonatomic, strong) NSString *workgroup_wid;
// 接入客服时间
@property(nonatomic, strong) NSString *actioned_at;
// queuing/accepted/leaved
@property(nonatomic, strong) NSString *status;
// 当前登录用户Uid
@property(nonatomic, strong) NSString *current_uid;

@end
