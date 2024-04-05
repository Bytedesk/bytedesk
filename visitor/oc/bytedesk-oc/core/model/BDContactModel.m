//
//  KFDSContactModel.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDContactModel.h"
#import "BDSettings.h"

@implementation BDContactModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {

        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _uid = [dictionary objectForKey:@"uid"];
        
        _username = [dictionary objectForKey:@"username"];
        _email = [dictionary objectForKey:@"email"];
        _mobile = [dictionary objectForKey:@"mobile"];
        _nickname = [dictionary objectForKey:@"nickname"];
        _real_name = [dictionary objectForKey:@"realName"];
        _avatar = [dictionary objectForKey:@"avatar"];
    
        _sub_domain = [dictionary objectForKey:@"subDomain"];
        _connection_status = [dictionary objectForKey:@"connectionStatus"];
        _accept_status = [dictionary objectForKey:@"acceptStatus"];
        _is_enabled = [[dictionary objectForKey:@"enabled"] boolValue];
        _is_robot = [[dictionary objectForKey:@"robot"] boolValue];
//        _welcome_tip = [dictionary objectForKey:@"welcomeTip"];
//        _is_auto_reply = [dictionary objectForKey:@"autoReply"];
//        _auto_reply_content = [dictionary objectForKey:@"autoReplyContent"];
//        _mdescription = [dictionary objectForKey:@"description"];
//        _max_thread_count = [dictionary objectForKey:@"maxThreadCount"];
        
        _current_uid = [BDSettings getUid];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDContactModel *contact = (BDContactModel *)object;
    if ([_uid isEqualToString:contact.uid]) {
        return TRUE;
    }
    return FALSE;
}


@end
