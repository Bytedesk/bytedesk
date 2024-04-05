//
//  BDArticleModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDArticleModel.h"

@implementation BDArticleModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        _aid = [dictionary objectForKey:@"aid"];
        //
        _title = [dictionary objectForKey:@"title"];
        // 内容
        _content = [dictionary objectForKey:@"content"];
        // 评价有帮助数量
        _rateHelpful = [dictionary objectForKey:@"rateHelpful"];
        // 评价无帮助数量
        _rateUseless = [dictionary objectForKey:@"rateUseless"];
        // 阅读次数
        _readCount = [dictionary objectForKey:@"readCount"];
        // 是否推荐
        _is_recommend = [dictionary objectForKey:@"recommend"];
        // 是否置顶
        _is_top = [dictionary objectForKey:@"top"];
        // 类型
        _type = [dictionary objectForKey:@"type"];
        // 创建时间
        _createdAt = [dictionary objectForKey:@"createdAt"];
        // 更新时间
        _updatedAt = [dictionary objectForKey:@"updatedAt"];
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDArticleModel *article = (BDArticleModel *)object;
    if ([_aid isEqualToString:article.aid]) {
        return TRUE;
    }
    return FALSE;
}

@end
