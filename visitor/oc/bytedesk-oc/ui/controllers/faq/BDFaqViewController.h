//
//  KFSupportApiViewController.h
//  demo
//
//  Created by 宁金鹏 on 2019/5/29.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDFaqViewController : UITableViewController

//- (void)initWithUid:(NSString *)uid withPush:(BOOL)isPush;
- (void)initWithType:(NSString *)type withUid:(NSString *)uid withPush:(BOOL)isPush;

@end

NS_ASSUME_NONNULL_END
