//
//  BDBlockModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2022/5/26.
//  Copyright © 2022 KeFuDaShi. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDBlockModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@property(nonatomic, strong) NSString *bid;

@property(nonatomic, strong) NSString *avatar;

@property(nonatomic, strong) NSString *nickname;

@property(nonatomic, strong) NSString *note;

@end

NS_ASSUME_NONNULL_END
