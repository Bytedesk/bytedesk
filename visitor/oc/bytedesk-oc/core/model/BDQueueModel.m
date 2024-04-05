//
//  KFDSQueueModel.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDQueueModel.h"
#import "BDSettings.h"

@implementation BDQueueModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _qid = [dictionary objectForKey:@"qid"];
        
        if (![[dictionary objectForKey:@"visitor"] isKindOfClass:[NSNull class]]) {
            _visitor_uid = [[dictionary objectForKey:@"visitor"] objectForKey:@"uid"];
            _nickname = [[dictionary objectForKey:@"visitor"] objectForKey:@"nickname"];
            _avatar = [[dictionary objectForKey:@"visitor"] objectForKey:@"avatar"];
            _visitor_client = [[dictionary objectForKey:@"visitor"] objectForKey:@"client"];
        }
        
//        _agent_uid = [[dictionary objectForKey:@"agent"] objectForKey:@"uid"];
//        _status = [dictionary objectForKey:@"status"];
        
        _current_uid = [BDSettings getUid];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDQueueModel *queue = (BDQueueModel *)object;
    if ([_qid isEqualToString:queue.qid]) {
        return TRUE;
    }
    return FALSE;
}


@end
