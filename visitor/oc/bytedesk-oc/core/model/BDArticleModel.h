//
//  BDArticleModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDArticleModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

// 唯一数字id，保证唯一性
@property(nonatomic, strong) NSString *aid;
// 标题
@property(nonatomic, strong) NSString *title;
// 内容
@property(nonatomic, strong) NSString *content;
// 评价有帮助数量
@property(nonatomic, strong) NSNumber *rateHelpful;
// 评价无帮助数量
@property(nonatomic, strong) NSNumber *rateUseless;
// 阅读次数
@property(nonatomic, strong) NSNumber *readCount;
// 是否推荐
@property(nonatomic, strong) NSNumber *is_recommend;
// 是否置顶
@property(nonatomic, strong) NSNumber *is_top;
// 类型
@property(nonatomic, strong) NSString *type;
// 创建时间
@property(nonatomic, strong) NSString *createdAt;
// 更新时间
@property(nonatomic, strong) NSString *updatedAt;


@end

NS_ASSUME_NONNULL_END
