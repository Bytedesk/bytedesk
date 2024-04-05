//
//  KFDSThreadModel.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDThreadModel.h"
#import "BDSettings.h"
#import "BDConstants.h"

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static const DDLogLevel ddLogLevel = DDLogLevelWarning;
//#endif

@implementation BDThreadModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        
        // TODO: 不再引用服务器端id
//        _server_id = [dictionary objectForKey:@"id"];
        _tid = [dictionary objectForKey:@"tid"];
        _topic = [dictionary objectForKey:@"topic"];
        _unread_count = [dictionary objectForKey:@"unreadCount"];
        _content = [dictionary objectForKey:@"content"];
        
        _type = [dictionary objectForKey:@"type"];
        _nickname = [dictionary objectForKey:@"nickname"];
        _avatar = [dictionary objectForKey:@"avatar"];
        _client = [dictionary objectForKey:@"client"];
        //
        _is_closed = [dictionary objectForKey:@"closed"];
        
//        if ([_type isEqualToString:BD_THREAD_TYPE_THREAD]) {
//
//            if (![[dictionary objectForKey:@"visitor"] isKindOfClass:[NSNull class]]) {
//                _visitor_uid = [[dictionary objectForKey:@"visitor"] objectForKey:@"uid"];
//                _nickname = [[dictionary objectForKey:@"visitor"] objectForKey:@"nickname"];
//                _avatar = [[dictionary objectForKey:@"visitor"] objectForKey:@"avatar"];
//                _visitor_client = [[dictionary objectForKey:@"visitor"] objectForKey:@"client"];
//            }
//
//        } else if ([_type isEqualToString:BD_THREAD_TYPE_CONTACT]) {
//
//            if (![[dictionary objectForKey:@"contact"] isKindOfClass:[NSNull class]]) {
//                _contact_uid = [[dictionary objectForKey:@"contact"] objectForKey:@"uid"];
//                 _nickname = [[dictionary objectForKey:@"contact"] objectForKey:@"realName"];
////                _nickname = [[dictionary objectForKey:@"contact"] objectForKey:@"nickname"];
//                _avatar = [[dictionary objectForKey:@"contact"] objectForKey:@"avatar"];
//            }
//
//        } else if ([_type isEqualToString:BD_THREAD_TYPE_GROUP]) {
//
//            if (![[dictionary objectForKey:@"group"] isKindOfClass:[NSNull class]]) {
//                _group_gid = [[dictionary objectForKey:@"group"] objectForKey:@"gid"];
//                _nickname = [[dictionary objectForKey:@"group"] objectForKey:@"nickname"];
//                _avatar = [[dictionary objectForKey:@"group"] objectForKey:@"avatar"];
//            }
//        }
        
        _timestamp = [dictionary objectForKey:@"timestamp"];
        _current_uid = [BDSettings getUid];
//        _is_temp = [dictionary objectForKey:@"temp"];
        
        // 是否置顶
        _is_mark_top = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *topArray = [dictionary objectForKey:@"topSet"];
//        for (NSDictionary *userDict in topArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark top uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_top = [NSNumber numberWithBool:TRUE];
//            }
//        }
        
        // 是否设置消息免打扰
        _is_mark_disturb = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *disturbArray = [dictionary objectForKey:@"noDisturbSet"];
//        for (NSDictionary *userDict in disturbArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark disturb uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_disturb = [NSNumber numberWithBool:TRUE];
//            }
//        }
        
        // 是否标记未读
        _is_mark_unread = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *unreadArray = [dictionary objectForKey:@"unreadSet"];
//        for (NSDictionary *userDict in unreadArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark unread uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_unread = [NSNumber numberWithBool:TRUE];
//            }
//        }

        // 是否标记删除
        _is_mark_deleted = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *deletedArray = [dictionary objectForKey:@"deletedSet"];
//        for (NSDictionary *userDict in deletedArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark deleted uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_deleted = [NSNumber numberWithBool:TRUE];
//            }
//        }
        
    }
    return self;
}

- (instancetype)initWithKeFuRequestDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
        if (self) {
            
            // TODO: 不再引用服务器端id
//            _server_id = [dictionary objectForKey:@"id"];
            _tid = [dictionary objectForKey:@"tid"];
            _topic = [dictionary objectForKey:@"topic"];
            _unread_count = [dictionary objectForKey:@"unreadCount"];
            _content = [dictionary objectForKey:@"content"];
            
            _type = [dictionary objectForKey:@"type"];
//            _nickname = [dictionary objectForKey:@"nickname"];
//            _avatar = [dictionary objectForKey:@"avatar"];
//            _client = [dictionary objectForKey:@"client"];
            
            if ([_type isEqualToString:BD_THREAD_TYPE_CONTACT]) {
                // 一对一
    
                if (![[dictionary objectForKey:@"contact"] isKindOfClass:[NSNull class]]) {
                    _contact_uid = [[dictionary objectForKey:@"contact"] objectForKey:@"uid"];
                     _nickname = [[dictionary objectForKey:@"contact"] objectForKey:@"realName"];
    //                _nickname = [[dictionary objectForKey:@"contact"] objectForKey:@"nickname"];
                    _avatar = [[dictionary objectForKey:@"contact"] objectForKey:@"avatar"];
                }
    
            } else if ([_type isEqualToString:BD_THREAD_TYPE_GROUP]) {
                // 群组
    
                if (![[dictionary objectForKey:@"group"] isKindOfClass:[NSNull class]]) {
                    _group_gid = [[dictionary objectForKey:@"group"] objectForKey:@"gid"];
                    _nickname = [[dictionary objectForKey:@"group"] objectForKey:@"nickname"];
                    _avatar = [[dictionary objectForKey:@"group"] objectForKey:@"avatar"];
                }
                
            } else {
                // 客服
            
                if (![[dictionary objectForKey:@"visitor"] isKindOfClass:[NSNull class]]) {
                    _visitor_uid = [[dictionary objectForKey:@"visitor"] objectForKey:@"uid"];
                    _nickname = [[dictionary objectForKey:@"visitor"] objectForKey:@"nickname"];
                    _avatar = [[dictionary objectForKey:@"visitor"] objectForKey:@"avatar"];
                    _client = [[dictionary objectForKey:@"visitor"] objectForKey:@"client"];
                }
            }
            
            _timestamp = [dictionary objectForKey:@"timestamp"];
            _current_uid = [BDSettings getUid];
            _is_temp = [dictionary objectForKey:@"temp"];
            
            // 是否置顶
            _is_mark_top = [NSNumber numberWithBool:FALSE];
            // 是否设置消息免打扰
            _is_mark_disturb = [NSNumber numberWithBool:FALSE];
            // 是否标记未读
            _is_mark_unread = [NSNumber numberWithBool:FALSE];
            // 是否标记删除
            _is_mark_deleted = [NSNumber numberWithBool:FALSE];
            
        }
        return self;
}

- (BOOL)isEqual:(id)object {
    BDThreadModel *thread = (BDThreadModel *)object;
    if ([_tid isEqualToString:thread.tid]) {
        return TRUE;
    }
    return FALSE;
}

@end
