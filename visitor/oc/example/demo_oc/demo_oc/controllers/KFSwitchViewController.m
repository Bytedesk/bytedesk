//
//  KFSwitchViewController.m
//  demo_kefu
//
//  Created by 宁金鹏 on 2022/6/27.
//  Copyright © 2022 bytedesk.com. All rights reserved.
//

#import "KFSwitchViewController.h"
#import "KFUserinfoViewController.h"
//#import <bytedesk-core/bdcore.h>
#import <bytedesk_oc/bytedesk_oc.h>

//开发文档：https://github.com/pengjinning/bytedesk-ios
//获取appkey：登录后台->渠道->APP->appkey列
//获取subDomain，也即企业号：登录后台->客服->账号->企业号列
// 需要替换为真实的
#define DEFAULT_TEST_APPKEY @"a3f79509-5cb6-4185-8df9-b1ce13d3c655"
#define DEFAULT_TEST_SUBDOMAIN @"vip"

// 获取adminUid, 登录后台->客服->账号->管理员uid列
#define DEFAULT_TEST_ADMIN_UID @"201808221551193"


@interface KFSwitchViewController ()

@end

@implementation KFSwitchViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 4;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return @"切换用户需要先退出之前的登录用户，然后再执行login";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
    }
    
    if (indexPath.row == 0) {
        cell.textLabel.text = @"用户信息";
    } else if (indexPath.row == 1) {
        cell.textLabel.text = @"用户1男";
    } else if (indexPath.row == 2) {
        cell.textLabel.text = @"用户2女";
    } else if (indexPath.row == 3) {
        cell.textLabel.text = @"退出登录";
    }

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.row == 0) {
        [self userInfo];
    } else if (indexPath.row == 1) {
        [self userBoyLogin];
    } else if (indexPath.row == 2) {
        [self userGirlLogin];
    } else if (indexPath.row == 3) {
        [self logout];
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)userInfo {
    //
    KFUserinfoViewController *userInfoVC = [[KFUserinfoViewController alloc] initWithStyle:UITableViewStyleGrouped];
    [self.navigationController pushViewController:userInfoVC animated:YES];
}

- (void)userBoyLogin {
    if ([BDSettings isAlreadyLogin]) {
//        [QMUITips showWithText:@"请先退出登录" inView:self.view hideAfterDelay:.8];
        return;;
    }
    [self initWithUsernameAndNicknameAndAvatar:@"myiosuserboy" withNickname:@"我是帅哥ios" withAvatar:@"https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/boy.png" withAppkey:DEFAULT_TEST_APPKEY withSubdomain:DEFAULT_TEST_SUBDOMAIN];
}

- (void)userGirlLogin {
    if ([BDSettings isAlreadyLogin]) {
//        [QMUITips showWithText:@"请先退出登录" inView:self.view hideAfterDelay:.8];
        return;;
    }
    [self initWithUsernameAndNicknameAndAvatar:@"myiosusergirl" withNickname:@"我是美女ios" withAvatar:@"https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png" withAppkey:DEFAULT_TEST_APPKEY withSubdomain:DEFAULT_TEST_SUBDOMAIN];
}

- (void)initWithUsernameAndNicknameAndAvatar:(NSString *)username withNickname:(NSString *)nickname withAvatar:(NSString *)avatar withAppkey:(NSString *)appkey withSubdomain:(NSString *)subDomain {
//    [QMUITips showLoading:@"登录中..." inView:self.view];
    
    [BDCoreApis initWithUsername:username withNickname:nickname withAvatar:avatar withAppkey:appkey withSubdomain:subDomain resultSuccess:^(NSDictionary *dict) {
        
//        [QMUITips hideAllToastInView:self.view animated:YES];
//        [QMUITips showWithText:@"登录成功" inView:self.view hideAfterDelay:.8];
    } resultFailed:^(NSError *error) {
        
//        [QMUITips hideAllToastInView:self.view animated:YES];
//        [QMUITips showError:@"登录失败" inView:self.view hideAfterDelay:.8];
    }];
}

- (void)logout {
//    [QMUITips showLoading:@"退出登录中..." inView:self.view];
    
    [BDCoreApis logoutResultSuccess:^(NSDictionary *dict) {
        
//        [QMUITips hideAllToastInView:self.view animated:YES];
//        [QMUITips showWithText:@"退出登录成功" inView:self.view hideAfterDelay:.8];
    } resultFailed:^(NSError *error) {
        
//        [QMUITips hideAllToastInView:self.view animated:YES];
//        [QMUITips showError:@"请先退出登录" inView:self.view hideAfterDelay:.8];
    }];
}

@end
