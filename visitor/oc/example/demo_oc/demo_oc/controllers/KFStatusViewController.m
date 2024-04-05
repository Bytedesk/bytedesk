//
//  KFVisitorStatusViewController.m
//  demo
//
//  Created by 萝卜丝 on 2018/11/22.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "KFStatusViewController.h"
//#import <bytedesk-core/bdcore.h>
#import <bytedesk_oc/bytedesk_oc.h>


@interface KFStatusViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
//@property(nonatomic, weak) QMUIDialogTextFieldViewController *currentTextFieldDialogViewController;

@property(nonatomic, strong) NSString *mDefaultWorkgroupWid;
@property(nonatomic, strong) NSString *mWorkgroupStatus;

@property(nonatomic, strong) NSString *mDefaultAgentUid;
@property(nonatomic, strong) NSString *mAgentStatus;

@end

@implementation KFStatusViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.mDefaultWorkgroupWid = @"201807171659201";
    self.mDefaultAgentUid = @"201808221551193";
    
    // Do any additional setup after loading the view.
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    //
    [self refreshControlSelector];
}


#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return section == 1 ? @"客服账号在线状态接口" : @"工作组在线状态接口";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
    }
    
    //
    if (indexPath.section == 0) {
        cell.textLabel.text = [NSString stringWithFormat:@"工作组:%@", self.mDefaultWorkgroupWid];
        cell.detailTextLabel.text = self.mWorkgroupStatus;
    }
    else {
        cell.textLabel.text = [NSString stringWithFormat:@"客服账号:%@", self.mDefaultAgentUid];
        cell.detailTextLabel.text = self.mAgentStatus;
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark - Selectors

- (void)refreshControlSelector {
//    // DDLogInfo(@"%s", __PRETTY_FUNCTION__);
    
    // 查询工作组在线状态
    [BDCoreApis getWorkGroupStatus:@"201807171659201" resultSuccess:^(NSDictionary *dict) {
        
//        NSString *wId = dict[@"data"][@"wid"];
        NSString *status = dict[@"data"][@"status"];
//        // DDLogInfo(@"wid: %@, status:%@", wId, status);
        self.mWorkgroupStatus = status;
        //
        [self.mRefreshControl endRefreshing];
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
//        // DDLogInfo(@"%@", error);
        [self.mRefreshControl endRefreshing];
        if (error) {
//            [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:3];
        }
    }];
    
    // 查询客服账号在线状态
    [BDCoreApis getAgentStatus:self.mDefaultAgentUid resultSuccess:^(NSDictionary *dict) {
        //
//        NSString *uId = dict[@"data"][@"uid"];
        NSString *status = dict[@"data"][@"status"];
//        // DDLogInfo(@"uid: %@, status:%@", uId, status);
        self.mAgentStatus = status;
        //
        [self.mRefreshControl endRefreshing];
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
//        // DDLogInfo(@"%@", error);
        [self.mRefreshControl endRefreshing];
        if (error) {
//            [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:3];
        }
    }];
}


@end






