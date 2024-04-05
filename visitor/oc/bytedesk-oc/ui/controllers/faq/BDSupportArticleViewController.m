//
//  KFSupportArticleViewController.m
//  demo
//
//  Created by 宁金鹏 on 2019/5/30.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDSupportArticleViewController.h"

//#import <bytedesk-core/bdcore.h>
#import "BDArticleModel.h"
#import "BDCoreApis.h"

@interface BDSupportArticleViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
@property(nonatomic, strong) BDArticleModel *mArticleModel;

@end

@implementation BDSupportArticleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
    //
//    [self.mRefreshControl beginRefreshing];
//    [self getArticleDetail];
}

- (void)initWithArticleModel:(BDArticleModel *)articleModel {
    self.mArticleModel = articleModel;
    //
    self.title = articleModel.title;
}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 2;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return @"";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
//        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    // TODO: 1. 处理显示html富文本； 2. 显示评价有用/无用按钮
    if (indexPath.row == 0) {
        cell.textLabel.text = self.mArticleModel.content;
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    //
    CGFloat height = 50.0;
    
    if (indexPath.row == 0) {
        // TODO: 跟进内容调整高度
        return height = 100;
    }
    
    return height;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

#pragma mark -

- (void)refreshControlSelector {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self getArticleDetail];
}

#pragma mark -

- (void)getArticleDetail {
    //
    [BDCoreApis getArticleDetail:self.mArticleModel.aid resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            //
            
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
