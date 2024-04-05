//
//  BDLeaveMessageViewController.h
//  bytedesk-ui
//
//  Created by 萝卜丝 on 2019/4/8.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDLeaveMessageViewController : UIViewController

- (void)initWithType:(NSString *)type withUid:(NSString *)uid withPush:(BOOL)isPush;

@end

NS_ASSUME_NONNULL_END
