//
//  KFDSProfileModel.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BDProfileModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 本地表主键id
@property(nonatomic, strong) NSNumber *local_id;

@property(nonatomic, strong) NSNumber *server_id;
@property(nonatomic, strong) NSString *uid;

@property(nonatomic, strong) NSString *username;
@property(nonatomic, strong) NSString *nickname;
@property(nonatomic, strong) NSString *realname;
@property(nonatomic, strong) NSString *avatar;
@property(nonatomic, strong) NSString *email;
@property(nonatomic, strong) NSString *mobile;
@property(nonatomic, strong) NSString *sub_domain;
@property(nonatomic, strong) NSString *client;
@property(nonatomic, strong) NSString *mdescription;

@property(nonatomic, strong) NSNumber *max_thread_count;

@property(nonatomic, strong) NSString *roles; // NSMutableArray
@property(nonatomic, strong) NSString *workgroups; // NSMutableArray

@property(nonatomic, strong) NSString *myusername;


@end
