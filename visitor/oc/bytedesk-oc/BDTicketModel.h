//
//  BDTicketModel.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2019/8/12.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDTicketModel : NSObject

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@property(nonatomic, strong) NSString *tid;

@property(nonatomic, strong) NSString *content;

@end

NS_ASSUME_NONNULL_END
