//
//  KFDSUIApis.m
//  bdui
//
//  Created by 萝卜丝 on 2018/7/15.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDUIApis.h"
#import "BDChatKFViewController.h"
#import "BDFeedbackViewController.h"
#import "BDFaqViewController.h"

#import <SafariServices/SafariServices.h>
#import <objc/runtime.h>

static BDUIApis *sharedInstance = nil;

@implementation BDUIApis

+ (BDUIApis *)sharedInstance {
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDUIApis alloc] init];
    });
    return sharedInstance;
}

#pragma mark - 访客端接口

+ (void)pushWorkGroupChat:(UINavigationController *)navigationController
                withWorkGroupWid:(NSString *)wId
                       withTitle:(NSString *)title {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    chatViewController.title = title;
    chatViewController.navigationItem.backBarButtonItem.title = @"";
    chatViewController.hidesBottomBarWhenPushed = YES;
    //
    [chatViewController initWithWorkGroupWid:wId withTitle:title withPush:YES];
    [navigationController pushViewController:chatViewController animated:YES];
}

+ (void)pushWorkGroupChat:(UINavigationController *)navigationController
                withWorkGroupWid:(NSString *)wId
                       withTitle:(NSString *)title
                      withCustom:(NSDictionary *)custom {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    chatViewController.navigationItem.backBarButtonItem.title = @"";
    chatViewController.hidesBottomBarWhenPushed = YES;
    //
    [chatViewController initWithWorkGroupWid:wId withTitle:title withPush:YES withCustom:custom];
    [navigationController pushViewController:chatViewController animated:YES];
}

+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
                   withWorkGroupWid:(NSString *)wId
                          withTitle:(NSString *)title{
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithWorkGroupWid:wId withTitle:title withPush:NO];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}

+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
  withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle
                   withWorkGroupWid:(NSString *)wId
                          withTitle:(NSString *)title{
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithWorkGroupWid:wId withTitle:title withPush:NO];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
    chatNavigationController.modalPresentationStyle = modalPresentationStyle;
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}


+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
                   withWorkGroupWid:(NSString *)wId
                          withTitle:(NSString *)title
                         withCustom:(NSDictionary *)custom {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithWorkGroupWid:wId withTitle:title withPush:NO withCustom:custom];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
    chatNavigationController.modalPresentationStyle = UIModalPresentationFullScreen;
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}


+ (void)pushAppointChat:(UINavigationController *)navigationController
                  withAgentUid:(NSString *)uId
                     withTitle:(NSString *)title {
    //
//    [BDConfig switchToKF];
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    chatViewController.navigationItem.backBarButtonItem.title = @"";
    chatViewController.hidesBottomBarWhenPushed = YES;
    //
    [chatViewController initWithAgentUid:uId withTitle:title withPush:YES];
    [navigationController pushViewController:chatViewController animated:YES];
}

+ (void)pushAppointChat:(UINavigationController *)navigationController
                  withAgentUid:(NSString *)uId
                     withTitle:(NSString *)title
                    withCustom:(NSDictionary *)custom {
    //
//    [BDConfig switchToKF];
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    chatViewController.navigationItem.backBarButtonItem.title = @"";
    chatViewController.hidesBottomBarWhenPushed = YES;
    //
    [chatViewController initWithAgentUid:uId withTitle:title withPush:YES withCustom:custom];
    [navigationController pushViewController:chatViewController animated:YES];
}

+ (void)presentAppointChat:(UINavigationController *)navigationController
                     withAgentUid:(NSString *)uId
                        withTitle:(NSString *)title {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithAgentUid:uId withTitle:title withPush:NO];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
//    chatNavigationController.modalPresentationStyle = UIModalPresentationFullScreen;
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}

+ (void)presentAppointChat:(UINavigationController *)navigationController
withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle
                     withAgentUid:(NSString *)uId
                 withTitle:(NSString *)title {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithAgentUid:uId withTitle:title withPush:NO];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
    chatNavigationController.modalPresentationStyle = modalPresentationStyle;
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}

+ (void)presentAppointChat:(UINavigationController *)navigationController
                     withAgentUid:(NSString *)uId
                        withTitle:(NSString *)title
                       withCustom:(NSDictionary *)custom {
    //
    BDChatKFViewController *chatViewController = [[BDChatKFViewController alloc] init];
    [chatViewController initWithAgentUid:uId withTitle:title withPush:NO withCustom:custom];
    //
    UINavigationController *chatNavigationController = [[UINavigationController alloc] initWithRootViewController:chatViewController];
//    chatNavigationController.modalPresentationStyle = UIModalPresentationFullScreen;
    [navigationController presentViewController:chatNavigationController animated:YES completion:^{
        
    }];
}

+ (void)pushFeedback:(UINavigationController *)navigationController withAdminUid:(NSString *)uid {
    //
    BDFeedbackViewController *feedbackViewController = [[BDFeedbackViewController alloc] init];
    feedbackViewController.hidesBottomBarWhenPushed = YES;
//   TODO: 待处理
    [feedbackViewController initWithUid:uid withPush:YES];
    //
    [navigationController pushViewController:feedbackViewController animated:YES];
}

+ (void)presentFeedback:(UINavigationController *)navigationController withAdminUid:(NSString *)uid {
    //
    BDFeedbackViewController *feedbackViewController = [[BDFeedbackViewController alloc] init];
//    [feedbackViewController initWithUid:uid withPush:NO];
    //   TODO: 待处理
    [feedbackViewController initWithUid:uid withPush:NO];
    //
    UINavigationController *fbnavigationController = [[UINavigationController alloc] initWithRootViewController:feedbackViewController];
    [navigationController presentViewController:fbnavigationController animated:YES completion:^{
        
    }];
}

+ (void)presentFeedback:(UINavigationController *)navigationController withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle withAdminUid:(NSString *)uid {
    //
    BDFeedbackViewController *feedbackViewController = [[BDFeedbackViewController alloc] init];
    feedbackViewController.modalPresentationStyle = modalPresentationStyle;
    [feedbackViewController initWithUid:uid withPush:NO];
    //   TODO: 待处理
//    [feedbackViewController initWithType:@"" withUid:uid withPush:NO];
    //
    UINavigationController *fbnavigationController = [[UINavigationController alloc] initWithRootViewController:feedbackViewController];
    [navigationController presentViewController:fbnavigationController animated:YES completion:^{
        
    }];
}

+ (void)pushFaqApi:(UINavigationController *)navigationController withAdminUid:(NSString *)uid {
    //
    BDFaqViewController *supportViewController = [[BDFaqViewController alloc] init];
    supportViewController.hidesBottomBarWhenPushed = YES;
//    [supportViewController initWithUid:uid withPush:YES];
    //   TODO: 待处理
    [supportViewController initWithType:@"" withUid:uid withPush:YES];
    //
    [navigationController pushViewController:supportViewController animated:YES];
}

+ (void)presentSupportURL:(UINavigationController *)navigationController withAdminUid:(NSString *)uid {
    //
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"https://vip.docs.bytedesk.com/support?uid=%@&ph=ph", uid]];
    SFSafariViewController *safariVC = [[SFSafariViewController alloc] initWithURL:url];
//    safariVC.delegate = self;
//    [navigationController pushViewController:safariVC animated:YES];
    // 建议
    [navigationController presentViewController:safariVC animated:YES completion:^{
    }];
}

#pragma mark - 截屏

// TODO: 1.可拖动
// TODO: 2.可X关闭
// TODO: 3.用户无操作，5秒后自动消失
// TODO: 4.联系客服
// TODO: 5.反馈问题
// TODO: 6.分享截图
- (void)showScreenshot:(UIWindow *)window
   withBackgroundColor:(UIColor *)backgroundColor
      withWorkGroupWid:(NSString *)workGroupWid
          withShowKeFu:(BOOL)showKefu
      withShowFeedback:(BOOL)showFeedback
         withShowShare:(BOOL)showShare
      withKefuCallback:(ScreenshotCallbackBlock)kefuCallback
  withFeedbackCallback:(ScreenshotCallbackBlock)feedbackCallback
     withShareCallback:(ScreenshotCallbackBlock)shareCallback {
    
    //人为截屏, 模拟用户截屏行为, 获取所截图片
    UIImage *imageScreenShoot = [self imageWithScreenshot];
    
    int width = 150;
    int height = 250;
    
    //添加显示
    CGRect screenshotFrame = CGRectMake(window.frame.size.width - width - 5, window.frame.size.height/2 - height/2, width, height);
    UIView *screenshotView = [[UIView alloc] initWithFrame:screenshotFrame];
    [screenshotView setBackgroundColor:backgroundColor];
    screenshotView.layer.cornerRadius = 10;
    
    // 截图
    UIImageView *screenShootImageView = [[UIImageView alloc] initWithImage:imageScreenShoot];
    [screenShootImageView setFrame:CGRectMake(10, 10, 100, 105)];
    [screenshotView addSubview:screenShootImageView];
    
    UIButton *closeButton = [[UIButton alloc] initWithFrame:CGRectMake(95, 5, 20, 20)];
    [closeButton setTitle:@"X" forState:UIControlStateNormal];
    [closeButton addTarget:self action:@selector(closeScreenShoot:) forControlEvents:UIControlEventTouchUpInside];
    [screenshotView addSubview:closeButton];
    objc_setAssociatedObject(closeButton, @"closeButton", screenshotView, OBJC_ASSOCIATION_RETAIN_NONATOMIC);

    if (showKefu) {
        UIButton *kefuButton = [[UIButton alloc] initWithFrame:CGRectMake(10, 120, 100, 20)];
        kefuButton.titleLabel.font = [UIFont systemFontOfSize:15];
        [kefuButton setTitle:@"联系客服" forState:UIControlStateNormal];
        [kefuButton setBackgroundColor:[UIColor grayColor]];
        [kefuButton addTarget:self action:@selector(kefuScreenShoot) forControlEvents:UIControlEventTouchUpInside];
        [screenshotView addSubview:kefuButton];
    }

    if (showFeedback) {
        UIButton *feedbackButton = [[UIButton alloc] initWithFrame:CGRectMake(10, 145, 100, 20)];
        feedbackButton.titleLabel.font = [UIFont systemFontOfSize:15];
        [feedbackButton setTitle:@"意见反馈" forState:UIControlStateNormal];
        [feedbackButton setBackgroundColor:[UIColor grayColor]];
        [feedbackButton addTarget:self action:@selector(feedbackScreenShoot) forControlEvents:UIControlEventTouchUpInside];
        [screenshotView addSubview:feedbackButton];
    }
    
    if (showShare) {
        UIButton *shareButton = [[UIButton alloc] initWithFrame:CGRectMake(10, 170, 100, 20)];
        shareButton.titleLabel.font = [UIFont systemFontOfSize:15];
        [shareButton setTitle:@"分享截图" forState:UIControlStateNormal];
        [shareButton setBackgroundColor:[UIColor grayColor]];
        [shareButton addTarget:self action:@selector(shareScreenShoot) forControlEvents:UIControlEventTouchUpInside];
        [screenshotView addSubview:shareButton];
    }
    //
    [window addSubview:screenshotView];
}

- (void)closeScreenShoot:(UIButton *)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    UIView *feedbackView = objc_getAssociatedObject(sender, @"closeButton");
    [feedbackView removeFromSuperview];
}

- (void)kefuScreenShoot {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)feedbackScreenShoot {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)shareScreenShoot {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

// 截取当前屏幕
- (NSData *)dataWithScreenshotInPNGFormat {
    CGSize imageSize = CGSizeZero;
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].windows.firstObject.windowScene.interfaceOrientation;
//    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    if (UIInterfaceOrientationIsPortrait(orientation))
        imageSize = [UIScreen mainScreen].bounds.size; // 竖屏
    else
        imageSize = CGSizeMake([UIScreen mainScreen].bounds.size.height, [UIScreen mainScreen].bounds.size.width); // 横屏
    
    UIGraphicsBeginImageContextWithOptions(imageSize, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    for (UIWindow *window in [[UIApplication sharedApplication] windows]) {
        //
        CGContextSaveGState(context);
        CGContextTranslateCTM(context, window.center.x, window.center.y);
        CGContextConcatCTM(context, window.transform);
        CGContextTranslateCTM(context, -window.bounds.size.width * window.layer.anchorPoint.x, -window.bounds.size.height * window.layer.anchorPoint.y);
        if (orientation == UIInterfaceOrientationLandscapeLeft) {
            CGContextRotateCTM(context, M_PI_2);
            CGContextTranslateCTM(context, 0, -imageSize.width);
        }
        else if (orientation == UIInterfaceOrientationLandscapeRight) {
            CGContextRotateCTM(context, -M_PI_2);
            CGContextTranslateCTM(context, -imageSize.height, 0);
        } else if (orientation == UIInterfaceOrientationPortraitUpsideDown) {
            CGContextRotateCTM(context, M_PI);
            CGContextTranslateCTM(context, -imageSize.width, -imageSize.height);
        }
        if ([window respondsToSelector:@selector(drawViewHierarchyInRect:afterScreenUpdates:)]) {
            [window drawViewHierarchyInRect:window.bounds afterScreenUpdates:YES];
        }
        else {
            [window.layer renderInContext:context];
        }
        CGContextRestoreGState(context);
    }
    //
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return UIImagePNGRepresentation(image);
}

/**
 *  返回截取到的图片
 *
 *  @return UIImage *
 */
- (UIImage *)imageWithScreenshot {
    NSData *imageData = [self dataWithScreenshotInPNGFormat];
    return [UIImage imageWithData:imageData];
}


#pragma mark - 公共接口

+ (void) showTipWithVC:(UIViewController *)viewController  withMessage:(NSString *)message {
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:nil
                                                                  message:@""
                                                           preferredStyle:UIAlertControllerStyleAlert];
    UIView *firstSubview = alertController.view.subviews.firstObject;
    UIView *alertContentView = firstSubview.subviews.firstObject;
    for (UIView *subSubView in alertContentView.subviews) {
        subSubView.backgroundColor = [UIColor colorWithRed:141/255.0f green:0/255.0f blue:254/255.0f alpha:1.0f];
    }
    //
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:message];
    [attributedString addAttribute:NSForegroundColorAttributeName value:[UIColor whiteColor] range:NSMakeRange(0,attributedString.length)];
    [alertController setValue:attributedString forKey:@"attributedTitle"];
    //
    [viewController presentViewController:alertController animated:YES completion:nil];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [alertController dismissViewControllerAnimated:YES completion:^{
        }];
    });
}

+ (void) showErrorWithVC:(UIViewController *)viewController  withMessage:(NSString *)message {
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:nil
                                                                  message:@""
                                                           preferredStyle:UIAlertControllerStyleAlert];
    UIView *firstSubview = alertController.view.subviews.firstObject;
    UIView *alertContentView = firstSubview.subviews.firstObject;
    for (UIView *subSubView in alertContentView.subviews) {
        subSubView.backgroundColor = [UIColor colorWithRed:141/255.0f green:0/255.0f blue:254/255.0f alpha:1.0f];
    }
    //
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:message];
    [attributedString addAttribute:NSForegroundColorAttributeName value:[UIColor whiteColor] range:NSMakeRange(0,attributedString.length)];
    [alertController setValue:attributedString forKey:@"attributedTitle"];
    //
    [viewController presentViewController:alertController animated:YES completion:nil];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [alertController dismissViewControllerAnimated:YES completion:^{
        }];
    });
}

+ (void)screenshot {
    
    UIWindow *keyWindow = [UIApplication sharedApplication].windows.firstObject; //[[UIApplication sharedApplication] keyWindow];
    
    CGRect rect = [keyWindow bounds];
    UIGraphicsBeginImageContextWithOptions(rect.size, YES, 0);
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    [keyWindow.layer renderInContext:context];
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    [[UIPasteboard generalPasteboard] setImage:image];
}


@end







