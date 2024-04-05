//
//  KFHelpCenterViewController.m
//  demo
//
//  Created by 萝卜丝 on 2019/1/21.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "KFSupportViewController.h"
//#import "KFSupportApiViewController.h"

//#import <SafariServices/SafariServices.h>
//#import <bytedesk-ui/bdui.h>
#import <bytedesk_oc/bytedesk_oc.h>

//开发文档：https://github.com/pengjinning/bytedesk-ios
//获取appkey：登录后台->渠道->APP->appkey列
//获取subDomain，也即企业号：登录后台->客服->账号->企业号列
// 需要替换为真实的
#define DEFAULT_TEST_APPKEY @"a3f79509-5cb6-4185-8df9-b1ce13d3c655"
#define DEFAULT_TEST_SUBDOMAIN @"vip"

// 获取adminUid, 登录后台->客服->账号->管理员uid列
#define DEFAULT_TEST_ADMIN_UID @"201808221551193"


@interface KFSupportViewController ()//<SFSafariViewControllerDelegate>

@end

@implementation KFSupportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return section == 0 ? @"方式一" : @"方式二";
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
        cell.textLabel.text = @"嵌入网页";
    }
    else {
        // TODO
        cell.textLabel.text = @"调用API接口";
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 0) {
        // 替换：URL参数uid
        // 注意: 登录后台->所有设置->所有客服->管理员唯一uid
        [BDUIApis presentSupportURL:self.navigationController withAdminUid:DEFAULT_TEST_ADMIN_UID];
        
    } else {
        
//        BDSupportApiViewController *apiVC = [[BDSupportApiViewController alloc] initWithStyle:UITableViewStyleGrouped];
//        [self.navigationController pushViewController:apiVC animated:YES];
        
        [BDUIApis pushFaqApi:self.navigationController withAdminUid:DEFAULT_TEST_ADMIN_UID];
    }
}



@end
