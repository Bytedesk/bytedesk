//
//  KFVisitorProfileViewController.m
//  demo
//
//  Created by 萝卜丝 on 2018/11/22.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "KFUserinfoViewController.h"
#import <bytedesk_oc/bytedesk_oc.h>

@interface KFUserinfoViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;

@property(nonatomic, strong) NSString *mTitle;

@property(nonatomic, strong) NSString *mUid;
@property(nonatomic, strong) NSString *mNickname;
@property(nonatomic, strong) NSString *mDescription;
@property(nonatomic, strong) NSString *mAvatar;

@property(nonatomic, strong) NSString *mTagkey;
@property(nonatomic, strong) NSString *mTagvalue;

@end

@implementation KFUserinfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.mTitle = @"设置昵称";
    self.mTagkey = @"自定义标签key";
    
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
    if (section == 0) {
        return 3;
    }
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return section == 1 ? @"自定义用户信息接口" : @"默认用户信息接口，支持自定义昵称和头像";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
    }
    
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            cell.textLabel.text = @"昵称";
            cell.detailTextLabel.text = self.mNickname;
        } else if (indexPath.row == 1) {
            cell.textLabel.text = @"描述";
            cell.detailTextLabel.text = self.mDescription;
        } else if (indexPath.row == 2) {
            cell.textLabel.text = @"头像";
            cell.detailTextLabel.text = self.mAvatar;
        }
    } else if (indexPath.section == 1) {
        cell.textLabel.text = self.mTagkey;
        cell.detailTextLabel.text = self.mTagvalue;
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            // 调用接口设置昵称
            [self setNickname];
        } else if (indexPath.row == 1) {
            [self setDescription];
        } else if (indexPath.row == 2) {
            [self setAvatar];
        }
    } else {
        // 自定义标签设置
        [self setTag];
    }
    //
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void) setNickname {
    self.mNickname = @"自定义昵称";
    [BDCoreApis setNickname:self.mNickname resultSuccess:^(NSDictionary *dict) {
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
        // DDLogInfo(@"%s %@", __PRETTY_FUNCTION__, error);
    }];
}

- (void) setAvatar {
    self.mAvatar = @"https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png";
    [BDCoreApis setAvatar:self.mAvatar resultSuccess:^(NSDictionary *dict) {
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
        
    }];
}

- (void) setDescription {
    self.mDescription = @"自定义APP用户备注信息ios";
    [BDCoreApis setDescription:self.mDescription resultSuccess:^(NSDictionary *dict) {
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
        
    }];
}

- (void) setTag {
    //
    self.mTagvalue = @"自定义标签value";
    [BDCoreApis setFingerPrint:@"自定义标签" withKey:self.mTagkey withValue:self.mTagvalue resultSuccess:^(NSDictionary *dict) {
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
        // DDLogInfo(@"%s %@", __PRETTY_FUNCTION__, error);
    }];
}

#pragma mark - Selectors

- (void)refreshControlSelector {
//    // DDLogInfo(@"%s", __PRETTY_FUNCTION__);
    //
    [BDCoreApis getVisitorProfileResultSuccess:^(NSDictionary *dict) {
//        // DDLogInfo(@"%s, %@", __PRETTY_FUNCTION__, dict);
        self.mUid = dict[@"data"][@"uid"];
        self.mNickname = dict[@"data"][@"nickname"];
        self.mDescription = dict[@"data"][@"description"];
        self.mAvatar = dict[@"data"][@"avatar"];
        [self.tableView reloadData];
        
    } resultFailed:^(NSError *error) {
        
    }];
    //
    [BDCoreApis getFingerPrintWithUid:[BDSettings getUid] resultSuccess:^(NSDictionary *dict) {
//        // DDLogInfo(@"%s, %@, %@", __PRETTY_FUNCTION__, dict, dict[@"data"][@"nickname"]);
        self.mNickname = dict[@"data"][@"nickname"];
        NSMutableArray *fingerPrints = dict[@"data"][@"fingerPrints"];
        for (NSDictionary *fingerPrint in fingerPrints) {
            // // DDLogInfo(@"%@ %@", fingerPrint[@"key"], fingerPrint[@"value"]);
            if ([fingerPrint[@"key"] isEqualToString:self.mTagkey]) {
                self.mTagvalue = fingerPrint[@"value"];
            }
        }
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



