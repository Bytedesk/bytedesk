//
//  BDNoticeModel.h
//  bytedesk-core
//
//  Created by 萝卜丝 on 2019/3/1.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDNoticeModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *nid;
// 标题
@property(nonatomic, strong) NSString *title;
// 内容
@property(nonatomic, strong) NSString *content;
// 类型：针对个人通知、群组通知
@property(nonatomic, strong) NSString *type;
// 是否已经被处理
@property(nonatomic, strong) NSNumber *processed;
// 如果是群组通知，则此为群组gid
@property(nonatomic, strong) NSString *gid;

@end

NS_ASSUME_NONNULL_END
