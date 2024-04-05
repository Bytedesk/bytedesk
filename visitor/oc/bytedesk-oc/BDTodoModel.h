//
//  BDTodoModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/1/6.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDTodoModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@property(nonatomic, strong) NSString *tid;

@property(nonatomic, strong) NSString *title;

@property(nonatomic, strong) NSString *content;

@property(nonatomic, strong) NSString *timestamp;

@end

NS_ASSUME_NONNULL_END
