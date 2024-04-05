//
//  KFDSUIApis.h
//  bdui
//
//  Created by 萝卜丝 on 2018/7/15.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
//#import <bytedesk_oc/BDThreadModel.h>
//#import <bytedesk_oc/BDContactModel.h>
//#import <bytedesk_oc/BDGroupModel.h>

@class BDThreadModel;
@class BDContactModel;
@class BDGroupModel;

@interface BDUIApis : NSObject

+ (BDUIApis *)sharedInstance;

//- (void) connect;
//
//- (void) connectWithUsername:(NSString *)username withPassword:(NSString *)password;


#pragma mark - 访客端接口

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param wId <#wId description#>
 @param title <#title description#>
 */
+ (void)pushWorkGroupChat:(UINavigationController *)navigationController
                    withWorkGroupWid:(NSString *)wId
                       withTitle:(NSString *)title;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param wId <#wId description#>
 @param title <#title description#>
 @param custom <#custom description#>
 */
+ (void)pushWorkGroupChat:(UINavigationController *)navigationController
                withWorkGroupWid:(NSString *)wId
                       withTitle:(NSString *)title
                      withCustom:(NSDictionary *)custom;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param wId <#wId description#>
 @param title <#title description#>
 */
+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
                       withWorkGroupWid:(NSString *)wId
                            withTitle:(NSString *)title;

+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
  withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle
                       withWorkGroupWid:(NSString *)wId
                            withTitle:(NSString *)title;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param wId <#wId description#>
 @param title <#title description#>
 @param custom <#custom description#>
 */
+ (void)presentWorkGroupChat:(UINavigationController *)navigationController
                   withWorkGroupWid:(NSString *)wId
                          withTitle:(NSString *)title
                         withCustom:(NSDictionary *)custom;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uId <#uId description#>
 @param title <#title description#>
 */
+ (void)pushAppointChat:(UINavigationController *)navigationController
                  withAgentUid:(NSString *)uId
                    withTitle:(NSString *)title;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uId <#uId description#>
 @param title <#title description#>
 @param custom <#custom description#>
 */
+ (void)pushAppointChat:(UINavigationController *)navigationController
                  withAgentUid:(NSString *)uId
                     withTitle:(NSString *)title
                    withCustom:(NSDictionary *)custom;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uId <#uId description#>
 @param title <#title description#>
 */
+ (void)presentAppointChat:(UINavigationController *)navigationController
                     withAgentUid:(NSString *)uId
                        withTitle:(NSString *)title;

+ (void)presentAppointChat:(UINavigationController *)navigationController
withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle
                     withAgentUid:(NSString *)uId
                        withTitle:(NSString *)title;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uId <#uId description#>
 @param title <#title description#>
 @param custom <#custom description#>
 */
+ (void)presentAppointChat:(UINavigationController *)navigationController
                     withAgentUid:(NSString *)uId
                        withTitle:(NSString *)title
                       withCustom:(NSDictionary *)custom;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 */
+ (void)pushFeedback:(UINavigationController *)navigationController withAdminUid:(NSString *)uid;

+ (void)presentFeedback:(UINavigationController *)navigationController withAdminUid:(NSString *)uid;

+ (void)presentFeedback:(UINavigationController *)navigationController withModalPresentationStyle:(UIModalPresentationStyle)modalPresentationStyle withAdminUid:(NSString *)uid;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uid <#uid description#>
 */
+ (void)pushFaqApi:(UINavigationController *)navigationController withAdminUid:(NSString *)uid;

/**
 <#Description#>

 @param navigationController <#navigationController description#>
 @param uid <#uid description#>
 */
+ (void)presentSupportURL:(UINavigationController *)navigationController withAdminUid:(NSString *)uid;


#pragma mark - 截屏

typedef void (^ScreenshotCallbackBlock)(UIImage *image);

- (void)showScreenshot:(UIWindow *)window
   withBackgroundColor:(UIColor *)backgroundColor
      withWorkGroupWid:(NSString *)workGroupWid
          withShowKeFu:(BOOL)showKefu
      withShowFeedback:(BOOL)showFeedback
         withShowShare:(BOOL)showShare
      withKefuCallback:(ScreenshotCallbackBlock)kefuCallback
  withFeedbackCallback:(ScreenshotCallbackBlock)feedbackCallback
     withShareCallback:(ScreenshotCallbackBlock)shareCallback;

+ (void)screenshot;

#pragma mark - 公共接口


+ (void)showTipWithVC:(UIViewController *)viewController withMessage:(NSString *)message;

+ (void)showErrorWithVC:(UIViewController *)viewController withMessage:(NSString *)message;


@end






