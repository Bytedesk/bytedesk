//
//  BDTodoModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/1/6.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import "BDTodoModel.h"

@implementation BDTodoModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _tid = [dictionary objectForKey:@"tid"];
        _title = [dictionary objectForKey:@"title"];
        _content = [dictionary objectForKey:@"content"];
        _timestamp = [dictionary objectForKey:@"timestamp"];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDTodoModel *todo = (BDTodoModel *)object;
    if ([_tid isEqualToString:todo.tid]) {
        return TRUE;
    }
    return FALSE;
}

@end
