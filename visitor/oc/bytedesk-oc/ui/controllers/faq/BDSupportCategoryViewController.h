//
//  KFSupportCategoryViewController.h
//  demo
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class BDCategoryModel;

@interface BDSupportCategoryViewController : UITableViewController

- (void) initWithCategoryModel:(BDCategoryModel *)categoryModel;

@end

NS_ASSUME_NONNULL_END
