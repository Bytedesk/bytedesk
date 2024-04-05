//
//  BDBlockModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/5/26.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import "BDBlockModel.h"

@implementation BDBlockModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _bid = [dictionary objectForKey:@"bid"];
        _avatar = [[dictionary objectForKey:@"blockedUser"] objectForKey:@"avatar"];
        _nickname = [[dictionary objectForKey:@"blockedUser"] objectForKey:@"nickname"];
        _note = [dictionary objectForKey:@"note"];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDBlockModel *block = (BDBlockModel *)object;
    if ([_bid isEqualToString:block.bid]) {
        return TRUE;
    }
    return FALSE;
}


@end
