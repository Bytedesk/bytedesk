//
//  ExtraParam.h
//  bytedesk-core
//
//  Created by 宁金鹏 on 2020/3/22.
//  Copyright © 2020 KeFuDaShi. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDExtraParam : NSObject

@property(nonatomic, strong) NSString *previewContent;

@property(nonatomic, strong) NSString *receiptMid;
@property(nonatomic, strong) NSString *receiptStatus;

@property(nonatomic, strong) NSString *recallMid;

@property(nonatomic, strong) NSString *transferTopic;
@property(nonatomic, strong) NSString *transferContent;

@property(nonatomic, strong) NSString *inviteTopic;
@property(nonatomic, strong) NSString *inviteContent;

@end

NS_ASSUME_NONNULL_END
