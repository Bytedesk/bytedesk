//
//  BDWorkGroupModel.m
//  bytedesk-core
//
//  Created by bytedesk.com on 2018/11/27.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import "BDWorkGroupModel.h"
#import "BDSettings.h"

@implementation BDWorkGroupModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _wid = [dictionary objectForKey:@"wid"];
        
        _nickname = [dictionary objectForKey:@"nickname"];
        _avatar = [dictionary objectForKey:@"avatar"];
        _mdescription = [dictionary objectForKey:@"description"];
        
        _is_default_robot = [dictionary objectForKey:@"defaultRobot"];
        _is_offline_robot = [dictionary objectForKey:@"offlineRobot"];

        _current_uid = [BDSettings getUid];
        
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDWorkGroupModel *workGroup = (BDWorkGroupModel *)object;
    if ([_wid isEqualToString:workGroup.wid]) {
        return TRUE;
    }
    return FALSE;
}

@end
