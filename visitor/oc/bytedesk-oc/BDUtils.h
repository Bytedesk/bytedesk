//
//  KFDSCUtils.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

//如果要禁止发送消息时播放声音，请设置此值为整数2，
//如：[[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
#define APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND      @"appkefu_should_play_send_message_sound"
//如果要禁止收到消息时播放声音，请设置此值为整数2，
//如：[[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
#define APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND   @"appkefu_should_play_receive_message_sound"
//如果要禁止收到消息时震动，请设置此值为整数2，
//如：[[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
#define APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE   @"appkefu_should_vibrate_on_receiving_message"

//@class BDMessageModel;

@interface BDUtils : NSObject

+(NSString *)getGuid;

+(NSString *)getCurrentDate;

/**
 <#Description#>

 @param string <#string description#>
 @return <#return value description#>
 */
+ (NSDate *)stringToDate:(NSString *)string;

/**
 <#Description#>

 @param date <#date description#>
 @return <#return value description#>
 */
+ (NSString *) getOptimizedTimestamp:(NSString *)date;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getCurrentTimeString;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)deviceVersion;

/**
 <#Description#>

 @param dict <#dict description#>
 @return <#return value description#>
 */
+ (NSString *)dictToJson:(NSDictionary *)dict;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getQRCodeLogin;

/**
 <#Description#>

 @param uid <#uid description#>
 @return <#return value description#>
 */
+ (NSString *)getQRCodeUser:(NSString *)uid;

/**
 <#Description#>

 @param gid <#gid description#>
 @return <#return value description#>
 */
+ (NSString *)getQRCodeGroup:(NSString *)gid;

/**
 设置发送消息时是否播放提示音

 @return <#return value description#>
 */
+(BOOL) shouldRingWhenSendMessage;
+(void) setRingWhenSendMessage:(BOOL)flag;

/**
 设置接收到消息时是否播放提示音

 @return <#return value description#>
 */
+(BOOL) shouldRingWhenReceiveMessage;
+(void) setRingWhenReceiveMessage:(BOOL)flag;

/**
 设置接收到消息时是否震动

 @return <#return value description#>
 */
+(BOOL) shouldVibrateWhenReceiveMessage;
+(void) setVibrateWhenReceiveMessage:(BOOL)flag;

#pragma 正则匹配手机号

/**
 <#Description#>

 @param telNumber <#telNumber description#>
 @return <#return value description#>
 */
+ (BOOL)checkTelNumber:(NSString *) telNumber;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (BOOL)canRecordVoice;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (BOOL)checkMicrophonePermission;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (BOOL)isSimulator;

/**
 断开连接，清空缓存
 */
+ (void)clearOut;

#pragma mark -

+ (NSString*)encodeString:(NSString*)string;

+ (NSString*)decodeString:(NSString*)string;

+ (NSAttributedString*)transformContentToContentAttr:(NSString *)content;

+ (void)setButtonTitleColor:(UIButton *)button;

@end
