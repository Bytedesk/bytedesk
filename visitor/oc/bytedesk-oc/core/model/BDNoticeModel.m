//
//  BDNoticeModel.m
//  bytedesk-core
//
//  Created by 萝卜丝 on 2019/3/1.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDNoticeModel.h"

@implementation BDNoticeModel

- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        _nid = [dictionary objectForKey:@"nid"];
        //
        _title = [dictionary objectForKey:@"title"];
        //
        _content = [dictionary objectForKey:@"content"];
        //
        _type = [dictionary objectForKey:@"type"];
        //
        _processed = [dictionary objectForKey:@"processed"];
        //
        if (![[dictionary objectForKey:@"group"] isKindOfClass:[NSNull class]]) {
            _gid = [[dictionary objectForKey:@"group"] objectForKey:@"gid"];
        }
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDNoticeModel *notice = (BDNoticeModel *)object;
    if ([_nid isEqualToString:notice.nid]) {
        return TRUE;
    }
    return FALSE;
}

@end
