//
//  AppDelegate.m
//  demo_kefu_oc
//
//  Created by 宁金鹏 on 2023/8/15.
//

#import "AppDelegate.h"
//#import "ViewController.h"
#import "KFApiTableViewController.h"

#pragma mark - 第一步：引入头文件
#import <bytedesk_oc/bytedesk_oc.h>

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    //
    self.window  = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    KFApiTableViewController *apiViewController = [[KFApiTableViewController alloc] init];
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:apiViewController];
    self.window.rootViewController = navigationController;
    [self.window makeKeyAndVisible];

#pragma mark - 第二步：萝卜丝客服初始化
    [self initBytedesk];
    //
    return YES;
}

// 开发文档：https://github.com/pengjinning/bytedesk-ios
// 获取appkey：登录后台->渠道->APP->appkey列
// 获取subDomain，也即企业号：登录后台->客服->账号->企业号列
#pragma 开发者需要到客服管理后台获取真实数据, 并将appkey 和 subdomain两个参数替换为真实值
//注：管理后台网址 https://www.weikefu.net/admin/
// 需要替换为真实的
#define DEFAULT_TEST_APPKEY @"a3f79509-5cb6-4185-8df9-b1ce13d3c655"
#define DEFAULT_TEST_SUBDOMAIN @"vip"
- (void)initBytedesk {
    // 访客登录
    [BDCoreApis loginWithAppkey:DEFAULT_TEST_APPKEY withSubdomain:DEFAULT_TEST_SUBDOMAIN resultSuccess:^(NSDictionary *dict) {
        // 登录成功
         NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
    } resultFailed:^(NSError *error) {
        // 登录失败
         NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
    }];
    //注册截屏通知
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(userDidTakeScreenshot)
                                                 name:UIApplicationUserDidTakeScreenshotNotification object:nil];
    /// 方便调试
    if(@available(iOS 13.0,*)) {
//        self.window.overrideUserInterfaceStyle = UIUserInterfaceStyleDark; // 深色主题
//        self.window.overrideUserInterfaceStyle = UIUserInterfaceStyleLight; // 浅色主题
//        self.window.overrideUserInterfaceStyle = UITraitCollection.currentTraitCollection.userInterfaceStyle; // 跟随系统的dark Mode
    }
}

// 默认设置工作组wid
#define kDefaultWorkGroupWid @"201807171659201"
// 截屏-联系客服，意见反馈，分享截图
- (void)userDidTakeScreenshot {
    NSLog(@"检测到截屏显示 联系客服、意见反馈、分享截图，注：模拟器不支持，仅支持真机");
//    [[BDUIApis sharedInstance] showScreenshot:self.window];
    [[BDUIApis sharedInstance] showScreenshot:self.window
                          withBackgroundColor: [UIColor blackColor]
                             withWorkGroupWid:kDefaultWorkGroupWid
                                 withShowKeFu:TRUE
                             withShowFeedback:TRUE
                                withShowShare:TRUE
                             withKefuCallback:^(UIImage *image) {
        
    } withFeedbackCallback:^(UIImage *image) {
        
    } withShareCallback:^(UIImage *image) {
        
    }];
}

#pragma mark - 第三步：从后台切换到前台时，建立长链接

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    // 从后台切换到前台时，重新建立长链接
    [BDCoreApis reconnect];
}


@end
