//
//  KFSupportArticleViewController.h
//  demo
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class BDArticleModel;

@interface BDSupportArticleViewController : UITableViewController

- (void)initWithArticleModel:(BDArticleModel *)articleModel;

@end

NS_ASSUME_NONNULL_END
