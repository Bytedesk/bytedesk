//
//  KFUIUtils.h
//  bytedesk-ui
//
//  Created by 宁金鹏 on 2019/4/11.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDUIUtils : NSObject

+ (NSArray *)documentTypes;

+ (CGSize)sizeOfRobotContent:(NSString *)msgContent;

+ (CGSize)sizeOfRobotContentAttr:(NSAttributedString *)msgContent;

@end

NS_ASSUME_NONNULL_END
