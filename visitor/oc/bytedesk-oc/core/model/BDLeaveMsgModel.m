//
//  BDLeaveMsgModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/5/26.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import "BDLeaveMsgModel.h"

@implementation BDLeaveMsgModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _lid = [dictionary objectForKey:@"lid"];
        _avatar = [[dictionary objectForKey:@"visitor"] objectForKey:@"avatar"];
        _nickname = [[dictionary objectForKey:@"visitor"] objectForKey:@"nickname"];
        _client = [[dictionary objectForKey:@"visitor"] objectForKey:@"client"];
        _mobile = [dictionary objectForKey:@"mobile"];
        _email = [dictionary objectForKey:@"email"];
        _content = [dictionary objectForKey:@"content"];
        _createdAt = [dictionary objectForKey:@"createdAt"];
        _reply = [dictionary objectForKey:@"reply"];
        _replied = [[dictionary objectForKey:@"replied"] boolValue];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDLeaveMsgModel *msg = (BDLeaveMsgModel *)object;
    if ([_lid isEqualToString:msg.lid]) {
        return TRUE;
    }
    return FALSE;
}

@end
