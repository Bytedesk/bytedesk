//
//  KFSocialApiViewController.m
//  demo
//
//  Created by 萝卜丝客服1.5.9 on 2018/4/12.
//  Copyright © 2018年 bytedesk.com. All rights reserved.
//

#import "KFApiTableViewController.h"
#import <SafariServices/SafariServices.h>

// 客服接口演示
#import "KFChatViewController.h"
#import "KFUserinfoViewController.h"
#import "KFStatusViewController.h"
#import "KFSupportViewController.h"
#import "KFSwitchViewController.h"

#import <bytedesk_oc/bytedesk_oc.h>

//开发文档：https://github.com/pengjinning/bytedesk-ios
//获取appkey：登录后台->渠道->APP->appkey列
//获取subDomain，也即企业号：登录后台->客服->账号->企业号列
// 需要替换为真实的
#define DEFAULT_TEST_APPKEY @"a3f79509-5cb6-4185-8df9-b1ce13d3c655"
#define DEFAULT_TEST_SUBDOMAIN @"vip"

// 获取adminUid, 登录后台->客服->账号->管理员uid列
#define DEFAULT_TEST_ADMIN_UID @"201808221551193"

@interface KFApiTableViewController ()<SFSafariViewControllerDelegate>

@property(nonatomic, strong) NSArray *kefuApisArray;
@property(nonatomic, strong) NSString *mLoginItemDetailText;
@property(nonatomic, strong) NSString *demoVersion;
@property(nonatomic, assign) int count; // 方便演示接口定义的变量，无实际意义

@end

@implementation KFApiTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.count = 0;
    self.demoVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    self.title = [NSString stringWithFormat:@"萝卜丝客服OCDemo%@(未连接)", self.demoVersion];
    //
    self.mLoginItemDetailText = @"当前未连接，点我建立连接";
    // 客服接口
    self.kefuApisArray = @[
                           @"联系客服",
                           @"用户信息",
                           @"在线状态",
                           @"意见反馈",
                           @"帮助中心",
                           @"网页会话",
                           @"切换用户",
                           @"监听截图"
                           ];

    // 监听消息通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyConnectionStatus:) name:BD_NOTIFICATION_CONNECTION_STATUS object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessageAdd:) name:BD_NOTIFICATION_MESSAGE_ADD object:nil];
    
    // 摇一摇：意见反馈
    [[UIApplication sharedApplication] setApplicationSupportsShakeToEdit:YES];
    [self becomeFirstResponder];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self resignFirstResponder];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 2;
}

- (nullable NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return @"客服接口";
    }
    return @"技术支持";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    if (section == 1) {
        return 1;
    }
    return [self.kefuApisArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    //
    if (indexPath.section == 1) {
        [cell.textLabel setText:@"官网: kefux.com, QQ-3群: 825257535"];
    } else {
        [cell.textLabel setText:[NSString stringWithFormat:@"%ld. %@", (long)(indexPath.row+1), [self.kefuApisArray objectAtIndex:indexPath.row]]];
    }
    [cell.detailTextLabel setText:@""];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (indexPath.section == 1) {
        return;
    }
    // 客服接口
    UIViewController *viewController = nil;
    if (indexPath.row == 0) {
        // 客服会话接口
        viewController = [[KFChatViewController alloc] initWithStyle:UITableViewStyleGrouped];
    } else if (indexPath.row == 1) {
        // 自定义用户信息接口
        viewController = [[KFUserinfoViewController alloc] initWithStyle:UITableViewStyleGrouped];
    } else if (indexPath.row == 2) {
        // 在线状态接口
        viewController = [[KFStatusViewController alloc] initWithStyle:UITableViewStyleGrouped];
    } else if (indexPath.row == 3) {
        // 意见反馈
        if (self.count++ % 2 == 0) {
            [BDUIApis pushFeedback:self.navigationController withAdminUid:DEFAULT_TEST_ADMIN_UID];
        } else {
            [BDUIApis presentFeedback:self.navigationController withAdminUid:DEFAULT_TEST_ADMIN_UID];
        }
        return;
    } else if (indexPath.row == 4) {
        // TODO: 帮助中心
//         viewController = [[KFSupportViewController alloc] initWithStyle:UITableViewStyleGrouped];
        [BDUIApis pushFaqApi:self.navigationController withAdminUid:DEFAULT_TEST_ADMIN_UID];
        return;
    } else if (indexPath.row == 5) {
        // 网页形式接入
        // 注意: 登录后台->客服->技能组/账号->获取代码 获取相应URL
        NSURL *url = [NSURL URLWithString:@"https://chat.kefux.com/chat/h5/index.html?sub=vip&uid=201808221551193&wid=201807171659201&type=workGroup&aid=&ph=ph"];
        SFSafariViewController *safariVC = [[SFSafariViewController alloc] initWithURL:url];
        safariVC.delegate = self;
        // 建议
        [self presentViewController:safariVC animated:YES completion:nil];
        return;
    } else if (indexPath.row == 6) {
        viewController = [[KFSwitchViewController alloc] initWithStyle:UITableViewStyleGrouped];
    } else if (indexPath.row == 7) {
        [self userDidTakeScreenshot];
        return;
    }
    viewController.title = [self.kefuApisArray objectAtIndex:indexPath.row];
    viewController.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:viewController animated:YES];
}

#pragma mark - 扩展面板

// 截屏-联系客服，意见反馈，分享截图
- (void)userDidTakeScreenshot {
    NSLog(@"检测到截屏显示 联系客服、意见反馈、分享截图，注：模拟器不支持，仅支持真机");
    [[BDUIApis sharedInstance] showScreenshot:[UIApplication sharedApplication].windows.lastObject
                          withBackgroundColor: [UIColor blackColor]
                             withWorkGroupWid:@""
                                 withShowKeFu:TRUE
                             withShowFeedback:TRUE
                                withShowShare:TRUE
                             withKefuCallback:^(UIImage *image) {
        
    } withFeedbackCallback:^(UIImage *image) {
        
    } withShareCallback:^(UIImage *image) {
        
    }];
}
/**
 监听连接状态通知

 @param notification <#notification description#>
 */
- (void)notifyConnectionStatus:(NSNotification *)notification {
    NSString *status = [notification object];
    NSLog(@"%s status:%@", __PRETTY_FUNCTION__, status);
    //
    if ([status isEqualToString:BD_USER_STATUS_CONNECTING]) {
        self.title = [NSString stringWithFormat:@"萝卜丝客服OCDemo%@(连接中...)", self.demoVersion];
    } else if ([status isEqualToString:BD_USER_STATUS_CONNECTED]){
        self.title = [NSString stringWithFormat:@"萝卜丝客服OCDemo%@(已连接)", self.demoVersion];
        self.mLoginItemDetailText = [NSString stringWithFormat:@"当前已连接: %@", [BDSettings getUsername]];
    } else {
        self.title = [NSString stringWithFormat:@"萝卜丝客服OCDemo%@(连接断开)", self.demoVersion];
        self.mLoginItemDetailText = @"当前未连接";
    }
    [self.tableView reloadData];
}

/**
 监听到新消息

 @param notification <#notification description#>
 */
- (void)notifyMessageAdd:(NSNotification *)notification {
    BDMessageModel *messageModel = [notification object];
    NSLog(@"%s type:%@", __PRETTY_FUNCTION__, messageModel.type);
    // TODO: 监听到新消息之后，开发者可自行处理
    if ([messageModel.type isEqual:@"text"]) {
        NSLog(@"%s content:%@", __PRETTY_FUNCTION__, messageModel.content);
    } else if ([messageModel.type isEqual:@"image"]) {
        NSLog(@"%s imageurl:%@", __PRETTY_FUNCTION__, messageModel.image_url);
    }
}


#pragma mark - ShakeToEdit 摇动手机之后的回调方法, 摇一摇意见反馈(类似知乎)

- (void) motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (motion == UIEventSubtypeMotionShake) {
        NSLog(@"检测到摇动开始");
    }
}

- (void) motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    NSLog(@"摇动取消");
}

- (void) motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        NSLog(@"摇动结束");
        //振动效果 需要#import <AudioToolbox/AudioToolbox.h>
//        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
    }
}

@end

