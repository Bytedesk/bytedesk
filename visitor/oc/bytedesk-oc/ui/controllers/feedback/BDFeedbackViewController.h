//
//  KFFeedbackViewController.h
//  demo
//
//  Created by 萝卜丝 on 2018/12/30.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDFeedbackViewController : UIViewController

- (void)initWithUid:(NSString *)uid withPush:(BOOL)isPush;
//- (void)initWithType:(NSString *)type withUid:(NSString *)uid withPush:(BOOL)isPush;

@end

NS_ASSUME_NONNULL_END
