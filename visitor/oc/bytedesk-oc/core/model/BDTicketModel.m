//
//  BDTicketModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/8/12.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDTicketModel.h"

@implementation BDTicketModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _tid = [dictionary objectForKey:@"tid"];
        _content = [dictionary objectForKey:@"content"];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDTicketModel *ticket = (BDTicketModel *)object;
    if ([_tid isEqualToString:ticket.tid]) {
        return TRUE;
    }
    return FALSE;
}

@end
