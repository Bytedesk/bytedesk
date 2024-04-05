//
//  BDLeaveMsgModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/5/26.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDLeaveMsgModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@property(nonatomic, strong) NSString *lid; // 唯一lid

@property(nonatomic, strong) NSString *avatar; // 留言人头像

@property(nonatomic, strong) NSString *nickname; // 留言人昵称

@property(nonatomic, strong) NSString *client; // 留言人来源

@property(nonatomic, strong) NSString *mobile; // 手机

@property(nonatomic, strong) NSString *email; // 邮箱

@property(nonatomic, strong) NSString *content; // 留言内容

@property(nonatomic, strong) NSString *createdAt; // 留言时间

@property(nonatomic, strong) NSString *reply; // 回复内容

@property(nonatomic, assign) bool replied; // 是否已经回复

@end

NS_ASSUME_NONNULL_END
