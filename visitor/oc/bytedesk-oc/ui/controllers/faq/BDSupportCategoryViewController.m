//
//  KFSupportCategoryViewController.m
//  demo
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDSupportCategoryViewController.h"
#import "BDSupportArticleViewController.h"

#import "BDCategoryModel.h"
#import "BDArticleModel.h"
#import "BDCoreApis.h"

@interface BDSupportCategoryViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
@property(nonatomic, strong) BDCategoryModel *mCategoryModel;

@property(nonatomic, strong) NSMutableArray *mArticleArray;

@end

@implementation BDSupportCategoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //
    self.mArticleArray = [[NSMutableArray alloc] init];
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
    //
    [self.mRefreshControl beginRefreshing];
    [self getCategoryDetail];
}

- (void) initWithCategoryModel:(BDCategoryModel *)categoryModel {
    self.mCategoryModel = categoryModel;
    //
    self.title = categoryModel.name;
}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.mArticleArray count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return @"常见问题";
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
    BDArticleModel *articleModel = [self.mArticleArray objectAtIndex:indexPath.row];
    cell.textLabel.text = articleModel.title;
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    BDArticleModel *articleModel = [self.mArticleArray objectAtIndex:indexPath.row];
    //
    BDSupportArticleViewController *articleVC = [[BDSupportArticleViewController alloc] initWithStyle:UITableViewStyleGrouped];
    [articleVC initWithArticleModel:articleModel];
    [self.navigationController pushViewController:articleVC animated:YES];
}

#pragma mark -

- (void)refreshControlSelector {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self getCategoryDetail];
}

#pragma mark -

- (void)getCategoryDetail {
    //
    [BDCoreApis getCategoryDetail:self.mCategoryModel.cid resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            //
            NSMutableArray *articleArray = dict[@"data"][@"articles"];
            for (NSDictionary *articleDict in articleArray) {
                BDArticleModel *articleModel = [[BDArticleModel alloc] initWithDictionary:articleDict];
                //
                if (![self.mArticleArray containsObject:articleModel]) {
                    [self.mArticleArray addObject:articleModel];
                }
            }
            //
            [self.tableView reloadData];
            [self.mRefreshControl endRefreshing];
            
        } else {
            //
            NSString *message = dict[@"message"];
//            // [QMUITips showError:message inView:self.view hideAfterDelay:2];
        }
    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
        //
        if (error) {
//            // [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:2];
        }
    }];
}



@end
