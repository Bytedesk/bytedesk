//
//  BDCategoryModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDCategoryModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *cid;
// 类别名称
@property(nonatomic, strong) NSString *name;


@end

NS_ASSUME_NONNULL_END
