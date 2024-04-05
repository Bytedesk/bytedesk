//
//  BDGroupModel.m
//  bytedesk-core
//
//  Created by bytedesk.com on 2018/11/28.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import "BDGroupModel.h"
#import "BDSettings.h"

@implementation BDGroupModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _gid = [dictionary objectForKey:@"gid"];
        
        _nickname = [dictionary objectForKey:@"nickname"];
        _avatar = [dictionary objectForKey:@"avatar"];
        _type = [dictionary objectForKey:@"type"];
        
        if ([dictionary objectForKey:@"memberCount"]) {
            _member_count = [dictionary objectForKey:@"memberCount"];
        } else {
            NSArray *members = [dictionary objectForKey:@"members"];
            _member_count = [NSNumber numberWithUnsignedInteger:[members count]];
        }
        
        _mdescription = [dictionary objectForKey:@"description"];
        _announcement = [dictionary objectForKey:@"announcement"];
        _is_dismissed = [dictionary objectForKey:@"dismissed"];
        
        _current_uid = [BDSettings getUid];
        
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDGroupModel *group = (BDGroupModel *)object;
    if ([_gid isEqualToString:group.gid]) {
        return TRUE;
    }
    return FALSE;
}

@end
