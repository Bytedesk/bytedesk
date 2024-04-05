//
//  KFSupportApiViewController.m
//  demo
//
//  Created by 宁金鹏 on 2019/5/29.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDFaqViewController.h"

#import "BDSupportCategoryViewController.h"
#import "BDSupportArticleViewController.h"

//#import <bytedesk-core/bdcore.h>
#import "BDCategoryModel.h"
#import "BDCoreApis.h"
#import "BDArticleModel.h"
#import "UITextView+Placeholder.h"

#define DeviceHeight [[UIScreen mainScreen] bounds].size.height
#define DeviceWidth [[UIScreen mainScreen] bounds].size.width

@interface KFRecentSupportSearchView : UIView

@property(nonatomic, strong) UILabel *titleLabel;
//@property(nonatomic, strong) QMUIFloatLayoutView *floatLayoutView;

@end

@implementation KFRecentSupportSearchView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
//        self.backgroundColor = UIColorWhite;
        //
//        self.titleLabel = [[QMUILabel alloc] init];
//        self.titleLabel.font = [UIFont systemFontOfSize:14.0f];
////        self.titleLabel.textColor = UIColorGray2;
//        self.titleLabel.text = @"最近搜索";
//        self.titleLabel.contentEdgeInsets = UIEdgeInsetsMake(0, 0, 8, 0);
//        [self.titleLabel sizeToFit];
//        //        self.titleLabel.style = QMUINavigationTitleViewStyleSubTitleVertical;
//        [self addSubview:self.titleLabel];
        //
//        self.floatLayoutView = [[QMUIFloatLayoutView alloc] init];
//        self.floatLayoutView.padding = UIEdgeInsetsZero;
//        self.floatLayoutView.itemMargins = UIEdgeInsetsMake(0, 0, 10, 10);
//        self.floatLayoutView.minimumItemSize = CGSizeMake(69, 29);
//        [self addSubview:self.floatLayoutView];
//
//        NSArray<NSString *> *suggestions = @[@"Helps", @"Maintain", @"Liver", @"Health", @"Function", @"Supports", @"Healthy", @"Fat"];
//        for (NSInteger i = 0; i < suggestions.count; i++) {
//            QMUIGhostButton *button = [[QMUIGhostButton alloc] initWithGhostType:QMUIGhostButtonColorGray];
//            [button setTitle:suggestions[i] forState:UIControlStateNormal];
//            button.titleLabel.font = UIFontMake(14);
//            button.contentEdgeInsets = UIEdgeInsetsMake(6, 20, 6, 20);
//            [self.floatLayoutView addSubview:button];
//        }
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
//    UIEdgeInsets padding = UIEdgeInsetsMake(26, 26, 26, 26);
//    CGFloat titleLabelMarginTop = 20;
//    self.titleLabel.frame = CGRectMake(padding.left, padding.top, CGRectGetWidth(self.bounds) - UIEdgeInsetsGetHorizontalValue(padding), CGRectGetHeight(self.titleLabel.frame));
//
//    CGFloat minY = CGRectGetMaxY(self.titleLabel.frame) + titleLabelMarginTop;
//    self.floatLayoutView.frame = CGRectMake(padding.left, minY, CGRectGetWidth(self.bounds) - UIEdgeInsetsGetHorizontalValue(padding), CGRectGetHeight(self.bounds) - minY);
}

@end

@interface BDFaqViewController ()
// <QMUISearchControllerDelegate>

//@property(nonatomic, strong) NSMutableArray *searchResultsKeywords;
//@property(nonatomic, strong) QMUISearchController *mySearchController;

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;

@property(nonatomic, strong) NSMutableArray *mCategoryArray;
@property(nonatomic, strong) NSMutableArray *mArticleArray;
//
//@property(nonatomic, strong) NSString *mAdminUid;
//
@property(nonatomic, assign) BOOL mIsPush;
@property(nonatomic, assign) BOOL forceEnableBackGesture;

@end

@implementation BDFaqViewController

//@synthesize contentTextView, imageView, contactTextField;

- (void)initWithType:(NSString *)type withUid:(NSString *)uid withPush:(BOOL)isPush {
    NSLog(@"1.%s", __PRETTY_FUNCTION__);
//    self.mAdminUid = uid;
    self.mIsPush = isPush;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"2.%s", __PRETTY_FUNCTION__);
}

- (void)viewDidLoad {
    [super viewDidLoad];
//    self.shouldShowSearchBar = YES;
    // Do any additional setup after loading the view.
    self.title = @"帮助中心";
    NSLog(@"3.%s", __PRETTY_FUNCTION__);
    self.view.backgroundColor = [UIColor whiteColor];
    if(@available(iOS 13.0,*)) {
        UIUserInterfaceStyle mode = UITraitCollection.currentTraitCollection.userInterfaceStyle;
        if (mode == UIUserInterfaceStyleDark) {
            self.view.backgroundColor = [UIColor blackColor];
        } else {
            self.view.backgroundColor = [UIColor whiteColor];
        }
    }
    //
    self.mCategoryArray = [[NSMutableArray alloc] init];
//    self.mArticleArray = [[NSMutableArray alloc] init];
    // QMUISearchController 有两种使用方式，一种是独立使用，一种是集成到 QMUICommonTableViewController 里使用。
    // 为了展示它的使用方式，这里使用第一种，不理会 QMUICommonTableViewController 内部自带的 QMUISearchController
//    self.mySearchController = [[QMUISearchController alloc] initWithContentsViewController:self];
//    self.mySearchController.searchResultsDelegate = self;
//    self.mySearchController.launchView = [[KFRecentSupportSearchView alloc] init];// launchView 会自动布局，无需处理 frame
//    self.tableView.tableHeaderView = self.mySearchController.searchBar;
    //
    if (self.mIsPush) {
        //
    }
    else {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemClose target:self action:@selector(handleCloseButtonEvent:)];
//        self.navigationItem.leftBarButtonItem = [UIBarButtonItem qmui_closeItemWithTarget:self action:@selector(handleCloseButtonEvent:)];
    }
    self.forceEnableBackGesture = YES;// 当系统的返回按钮被屏蔽的时候，系统的手势返回也会跟着失效，所以这里要手动强制打开手势返回
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
    //
    [self.mRefreshControl beginRefreshing];
    // 加载数据
    [self getCategories];
    [self getArticles];
}

- (void)viewWillLayoutSubviews {
    NSLog(@"4.%s", __PRETTY_FUNCTION__);
    [super viewWillLayoutSubviews];
}

#pragma mark -

- (void)submitButtonClicked:(id)sender {
    
//    [BDCoreApis leaveMessage:self.type withWorkGroupWid:self.wid withAgentUid:self.uid
//                  withMobile:@"手机号"
//                   withEmail:@"邮箱"
//                 withContent:@"留言内容"
//               resultSuccess:^(NSDictionary *dict) {
//
//    } resultFailed:^(NSError *error) {
//        if (error) {
//            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
//        }
//    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)forceEnableInteractivePopGestureRecognizer {
    return self.forceEnableBackGesture;
}

// 针对Present打开模式，左上角返回按钮处理action
- (void)handleCloseButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    self.mIsViewControllerClosed = YES;
    [self.navigationController dismissViewControllerAnimated:YES completion:^{
    }];
}

// 针对Push打开模式，左上角返回按钮处理action
- (void)handleBackButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    self.mIsViewControllerClosed = YES;
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - 下拉刷新

- (void)refreshControlSelector {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    UIView *parentView = self.navigationController.view;
    
    [self getCategories];
    [self getArticles];
}

#pragma mark -

/**
 加载问题分类
 */
- (void)getCategories {
    //
//    [BDCoreApis getSupportCategories:self.mAdminUid resultSuccess:^(NSDictionary *dict) {
////        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//        //
//        NSNumber *status_code = [dict objectForKey:@"status_code"];
//        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
//            //
//            NSMutableArray *categoryArray = dict[@"data"];
//            for (NSDictionary *categoryDict in categoryArray) {
//                BDCategoryModel *categoryModel = [[BDCategoryModel alloc] initWithDictionary:categoryDict];
//                //
//                if (![self.mCategoryArray containsObject:categoryModel]) {
//                    [self.mCategoryArray addObject:categoryModel];
//                }
//            }
//            //
//            [self.tableView reloadData];
//            [self.mRefreshControl endRefreshing];
//
//        } else {
//            //
//            NSString *message = dict[@"message"];
////            // [QMUITips showError:message inView:self.view hideAfterDelay:2];
//        }
//    } resultFailed:^(NSError *error) {
//        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
//        //
//        if (error) {
////            // [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:2];
//        }
//    }];
}

/**
 加载常见问题
 */
- (void)getArticles {
    //
//    [BDCoreApis getSupportArticles:self.mAdminUid resultSuccess:^(NSDictionary *dict) {
//        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//        //
//        NSNumber *status_code = [dict objectForKey:@"status_code"];
//        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
//            //
//            NSMutableArray *articleArray = dict[@"data"][@"content"];
//            for (NSDictionary *articleDict in articleArray) {
//                BDArticleModel *articleModel = [[BDArticleModel alloc] initWithDictionary:articleDict];
//                //
//                if (![self.mArticleArray containsObject:articleModel]) {
//                    [self.mArticleArray addObject:articleModel];
//                }
//            }
//            //
//            [self.tableView reloadData];
//            [self.mRefreshControl endRefreshing];
//            
//        } else {
//            //
//            NSString *message = dict[@"message"];
////            // [QMUITips showError:message inView:self.view hideAfterDelay:2];
//        }
//        
//    } resultFailed:^(NSError *error) {
//        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
//        //
//        if (error) {
////            // [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:2];
//        }
//    }];
}
#pragma mark - <QMUISearchControllerDelegate>

//- (void)searchController:(QMUISearchController *)searchController updateResultsForSearchString:(NSString *)searchString {
//    //
//    [self.searchResultsKeywords removeAllObjects];
//    //
//    [BDCoreApis searchArticle:self.mAdminUid withContent:searchString resultSuccess:^(NSDictionary *dict) {
//        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
//        //
//        NSNumber *status_code = [dict objectForKey:@"status_code"];
//        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
//
//            //
//        //    for (BDThreadModel *threadModel in self.mThreadArray) {
//        //        if ([threadModel.nickname containsString:searchString]) {
//        //            [self.searchResultsKeywords addObject:threadModel.nickname];
//        //        }
//        //    }
//
//            //
//            [searchController.tableView reloadData];
//            //
//            if (self.searchResultsKeywords.count == 0) {
//                [searchController showEmptyViewWithText:@"没有匹配结果" detailText:nil buttonTitle:nil buttonAction:NULL];
//            } else {
//                [searchController hideEmptyView];
//            }
//
//        } else {
//            //
//            NSString *message = dict[@"message"];
//            // [QMUITips showError:message inView:self.view hideAfterDelay:2];
//        }
//
//    } resultFailed:^(NSError *error) {
//        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
//        //
//        if (error) {
//            // [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:2];
//        }
//    }];
//
//}
//
//- (void)willPresentSearchController:(QMUISearchController *)searchController {
//    [self setNeedsStatusBarAppearanceUpdate];
//}
//
//- (void)willDismissSearchController:(QMUISearchController *)searchController {
//    [self setNeedsStatusBarAppearanceUpdate];
//}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return [self.mCategoryArray count];
    } else {
        return [self.mArticleArray count];
    }
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return section == 0 ? @"问题分类" : @"常见问题";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];

    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }

    //
    if (indexPath.section == 0) {
        BDCategoryModel *categoryModel = [self.mCategoryArray objectAtIndex:indexPath.row];
        cell.textLabel.text = categoryModel.name;
    }
    else {
        BDArticleModel *articleModel = [self.mArticleArray objectAtIndex:indexPath.row];
        cell.textLabel.text = articleModel.title;
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];

    if (indexPath.section == 0) {
        BDCategoryModel *categoryModel = [self.mCategoryArray objectAtIndex:indexPath.row];
        //
        BDSupportCategoryViewController *categoryVC = [[BDSupportCategoryViewController alloc] initWithStyle:UITableViewStyleGrouped];
        [categoryVC initWithCategoryModel:categoryModel];
        [self.navigationController pushViewController:categoryVC animated:YES];
    } else {
        BDArticleModel *articleModel = [self.mArticleArray objectAtIndex:indexPath.row];
        //
        BDSupportArticleViewController *articleVC = [[BDSupportArticleViewController alloc] initWithStyle:UITableViewStyleGrouped];
        [articleVC initWithArticleModel:articleModel];
        [self.navigationController pushViewController:articleVC animated:YES];
    }

}

@end
