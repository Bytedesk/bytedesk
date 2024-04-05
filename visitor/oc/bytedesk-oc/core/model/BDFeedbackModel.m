//
//  BDFeedbackModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/8/12.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDFeedbackModel.h"

@implementation BDFeedbackModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _fid = [dictionary objectForKey:@"fid"];
        _content = [dictionary objectForKey:@"content"];
        _replyContent = [dictionary objectForKey:@"replyContent"];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDFeedbackModel *feedback = (BDFeedbackModel *)object;
    if (![_fid isKindOfClass:[NSNull class]] && [_fid isEqualToString:feedback.fid]) {
        return TRUE;
    }
    return FALSE;
}

@end
