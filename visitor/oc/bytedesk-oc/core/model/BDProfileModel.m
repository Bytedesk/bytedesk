//
//  KFDSProfileModel.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDProfileModel.h"
#import "BDSettings.h"

@implementation BDProfileModel


-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        
        _server_id = [dictionary objectForKey:@"id"];
        _uid = [dictionary objectForKey:@"uid"];
        
        _username = [dictionary objectForKey:@"username"];
        _nickname = [dictionary objectForKey:@"nickname"];
        _realname = [dictionary objectForKey:@"realname"];
        _avatar = [dictionary objectForKey:@"avatar"];
        _email = [dictionary objectForKey:@"email"];
        _mobile = [dictionary objectForKey:@"mobile"];
        _sub_domain = [dictionary objectForKey:@"subDomain"];
        
        _client = [dictionary objectForKey:@"client"];
        _mdescription = [dictionary objectForKey:@"description"];

//        _roles = [dictionary objectForKey:@"roles"];
//        _workgroups = [dictionary objectForKey:@"workgroups"];
        
        _myusername = [BDSettings getUsername];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDProfileModel *profile = (BDProfileModel *)object;
    if ([_uid isEqualToString:profile.uid]) {
        return TRUE;
    }
    return FALSE;
}


@end
