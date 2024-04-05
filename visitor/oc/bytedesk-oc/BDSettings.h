//
//  KFDSCSettings.h
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

#define BD_USERNAME               @"bd_username"
#define BD_UID                    @"bd_uid"
#define BD_NICKNAME               @"bd_nickname"
#define BD_REALNAME               @"bd_realname"
#define BD_PASSWORD               @"bd_password"
#define BD_AVATAR                 @"bd_avatar"
#define BD_ROLE                   @"bd_role"
#define BD_APPKEY                 @"bd_appkey"
#define BD_SUBDOMAIN              @"bd_subdomain"
#define BD_DESCRIPTION            @"bd_description"
#define BD_VALIDATE_UNTIL_DATE    @"bd_validate_until_date"
#define BD_ACCEPTSTATUS           @"bd_acceptstatus"
#define BD_AUTOREPLYCONTENT       @"bd_autoreplycontent"
#define BD_WELCOMETIP             @"bd_welcometip"
#define BD_STATUS                 @"bd_status"
#define BD_CURRENT_TID            @"bd_current_tid"
#define BD_DEVICE_TOKEN           @"bd_device_token"
#define BD_CLIENT_UUID            @"bd_client_uuid"

#define BD_IS_ALREADY_LOGIN       @"bd_is_already_login"

#define BD_PASSPORT_ACCESS_TOKEN  @"bd_access_token"
#define BD_PASSPORT_EXPIRES_IN    @"bd_expires_in"
#define BD_PASSPORT_REFRESH_TOKEN @"bd_refresh_token"
#define BD_PASSPORT_TOKEN_TYPE    @"bd_token_type"


@interface BDSettings : NSObject

+ (BOOL)loginAsVisitor;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (BOOL)isAlreadyLogin;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getClient;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getSubdomain;

/**
 <#Description#>

 @param subdomain <#subdomain description#>
 */
+ (void)setSubdomain:(NSString *)subdomain;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getUsername;

/**
 <#Description#>

 @param username <#username description#>
 */
+ (void)setUsername:(NSString *)username;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getUid;

/**
 <#Description#>

 @param uid <#uid description#>
 */
+ (void)setUid:(NSString *)uid;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getNickname;

/**
 <#Description#>

 @param nickname <#nickname description#>
 */
+ (void)setNickname:(NSString *)nickname;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getRealname;

/**
 <#Description#>

 @param realname <#realname description#>
 */
+ (void)setRealname:(NSString *)realname;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getPassword;

/**
 <#Description#>

 @param password <#password description#>
 */
+ (void)setPassword:(NSString *)password;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getAvatar;

/**
 <#Description#>

 @param avatar <#avatar description#>
 */
+ (void)setAvatar:(NSString *)avatar;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getRole;

/**
 <#Description#>

 @param role <#role description#>
 */
+ (void)setRole:(NSString *)role;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getAppkey;

/**
 <#Description#>

 @param appkey <#appkey description#>
 */
+ (void)setAppkey:(NSString *)appkey;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getDescription;

/**
 <#Description#>

 @param description <#description description#>
 */
+ (void)setDescription:(NSString *)description;

/**
 ValidateUntilDate
 */
+ (NSString *)getValidateUntilDate;

/**
 ValidateUntilDate
 */
+ (void)setValidateUntilDate:(NSString *)validateUntilDate;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getAcceptStatus;

/**
 <#Description#>

 @param acceptStatus <#acceptStatus description#>
 */
+ (void)setAcceptStatus:(NSString *)acceptStatus;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getAutoReplyContent;

/**
 <#Description#>

 @param autoreply <#autoreply description#>
 */
+ (void)setAutoReplyContent:(NSString *)autoreply;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getWelcomeTip;

/**
 <#Description#>
 */
+ (void)setWelcomeTip:(NSString *)welcomeTip;


/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getStatus;

/**
 <#Description#>

 @param status <#status description#>
 */
+ (void)setStatus:(NSString *)status;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getCurrentTid;

/**
 <#Description#>

 @param tid <#tid description#>
 */
+ (void)setCurrentTid:(NSString *)tid;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getPassportAccessToken;

/**
 <#Description#>

 @param accessToken <#accessToken description#>
 */
+ (void)setPassportAccessToken:(NSString *)accessToken;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getPassportExpiresIn;

/**
 <#Description#>

 @param expiresIn <#expiresIn description#>
 */
+ (void)setPassportExpiresIn:(NSString *)expiresIn;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getPassportRefreshToken;

/**
 <#Description#>

 @param refreshToken <#refreshToken description#>
 */
+ (void)setPassportRefreshToken:(NSString *)refreshToken;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getPassportTokenType;

/**
 <#Description#>

 @param tokenType <#tokenType description#>
 */
+ (void)setPassportTokenType:(NSString *)tokenType;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getDeviceToken;

/**
 <#Description#>

 @param deviceToken <#deviceToken description#>
 */
+ (void)setDeviceToken:(NSString *)deviceToken;

/**
 <#Description#>

 @return <#return value description#>
 */
+ (NSString *)getClientUUID;

/**
 <#Description#>
 */
+ (void)clear;

#pragma mark - 阅后即焚

/**
 <#Description#>

 @param tidOrUidOrGid <#tidOrUidOrGid description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
+ (BOOL)getDestroyAfterReading:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType;

/**
 <#Description#>

 @param tidOrUidOrGid <#tidOrUidOrGid description#>
 @param sessionType <#sessionType description#>
 @param flag <#flag description#>
 */
+ (void)setDestroyAfterReading:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType flag:(BOOL)flag;

/**
 <#Description#>

 @param tidOrUidOrGid <#tidOrUidOrGid description#>
 @param sessionType <#sessionType description#>
 @return <#return value description#>
 */
+ (uint32_t)getDestroyAfterLength:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType;

/**
 <#Description#>

 @param tidOrUidOrGid <#tidOrUidOrGid description#>
 @param sessionType <#sessionType description#>
 @param length <#length description#>
 */
+ (void)setDestroyAfterLength:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType length:(uint32_t)length;


#pragma mark - 新消息提示


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


+(BOOL) hasSetDeviceInfo;
+(void) setDeviceInfo:(BOOL)flag;



@end





