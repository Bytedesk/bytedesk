//
//  BDCategoryModel.m
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDCategoryModel.h"

@implementation BDCategoryModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        _cid = [dictionary objectForKey:@"cid"];
        _name = [dictionary objectForKey:@"name"];
        
    }
    return self;
}

- (BOOL)isEqual:(id)object {
    BDCategoryModel *category = (BDCategoryModel *)object;
    if ([_cid isEqualToString:category.cid]) {
        return TRUE;
    }
    return FALSE;
}


@end
