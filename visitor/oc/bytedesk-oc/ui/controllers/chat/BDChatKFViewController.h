//
//  KFDSChatViewController.h
//  bdui
//
//  Created by 萝卜丝 on 2018/11/29.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BDThreadModel.h"
#import "BDContactModel.h"
#import "BDGroupModel.h"

// 客服对话窗口

@interface BDChatKFViewController : UIViewController

#pragma mark - 访客端接口

/**
 工作组会话

 @param wId <#wId description#>
 @param title <#title description#>
 @param isPush <#isPush description#>
 */
- (void) initWithWorkGroupWid:(NSString *)wId withTitle:(NSString *)title withPush:(BOOL)isPush;

- (void) initWithWorkGroupWid:(NSString *)wId withTitle:(NSString *)title withPush:(BOOL)isPush withCustom:(NSDictionary *)custom;

/**
 指定坐席会话

 @param uId <#uId description#>
 @param title <#title description#>
 @param isPush <#isPush description#>
 */
- (void) initWithAgentUid:(NSString *)uId withTitle:(NSString *)title withPush:(BOOL)isPush;

- (void) initWithAgentUid:(NSString *)uId withTitle:(NSString *)title withPush:(BOOL)isPush withCustom:(NSDictionary *)custom;

#pragma mark - 公共接口



@end





