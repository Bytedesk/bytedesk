//
//  KFDSCSettings.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDSettings.h"
#import "BDHttpApis.h"
#import "BDConstants.h"

//https://github.com/Tencent/MMKV/blob/master/readme_cn.md
//#import <MMKV/MMKV.h>

@implementation BDSettings

+ (BOOL)loginAsVisitor {
    //
//    return [[[MMKV defaultMMKV] getStringForKey:BD_ROLE] isEqualToString:BD_ROLE_VISITOR];
    return [[[NSUserDefaults standardUserDefaults] stringForKey:BD_ROLE] isEqualToString:BD_ROLE_VISITOR];
}

//+ (BOOL)getIsAlreadyLogin {
//    NSString *access_token = [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_ACCESS_TOKEN];;
//    if ([access_token isKindOfClass:[NSNull class]] || [access_token length] == 0) {
//        return false;
//    } else {
//        return true;
//    }
//}

+ (BOOL)isAlreadyLogin {
//    [MMKV setLogLevel:MMKVLogNone];
//    [MMKV initializeMMKV:nil logLevel:MMKVLogNone];
//    NSString *access_token = [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_ACCESS_TOKEN];
    NSString *access_token = [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSPORT_ACCESS_TOKEN];
    if ([access_token isKindOfClass:[NSNull class]] || [access_token length] == 0) {
        return false;
    } else {
        return true;
    }
}

+ (NSString *)getClient {
    return BD_CLIENT_IOS;
//    return [self loginAsVisitor] ? BD_CLIENT_IOS : BD_CLIENT_IOS_ADMIN;
}

+ (NSString *)getUsername {
    //
//    NSString *username = [[MMKV defaultMMKV] getStringForKey:BD_USERNAME];
    NSString *username = [[NSUserDefaults standardUserDefaults] stringForKey:BD_USERNAME];
    if (username == nil || username == NULL || [username isKindOfClass:[NSNull class]] || [username length] == 0) {
        return @"";
    }
    return username;
}

+ (void)setUsername:(NSString *)username {
    if ([username isKindOfClass:[NSNull class]]) {
        username = @"";
    }
//    [[MMKV defaultMMKV] setString:username forKey:BD_USERNAME];
    [[NSUserDefaults standardUserDefaults] setValue:username forKey:BD_USERNAME];
}

+ (NSString *)getUid {
//    return [[MMKV defaultMMKV] getStringForKey:BD_UID];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_UID];
}

+ (void)setUid:(NSString *)uid {
    if ([uid isKindOfClass:[NSNull class]]) {
        uid = @"";
    }
    [[NSUserDefaults standardUserDefaults] setValue:uid forKey:BD_UID];
}

+ (NSString *)getNickname {
//    NSString *nickname = [[MMKV defaultMMKV] getStringForKey:BD_NICKNAME];
    NSString *nickname = [[NSUserDefaults standardUserDefaults] stringForKey:BD_NICKNAME];
    if (nickname == nil || nickname == NULL || [nickname isKindOfClass:[NSNull class]] || [nickname length] == 0) {
        return [BDSettings getUsername];
    }
    return nickname;
}

+ (void)setNickname:(NSString *)nickname {
    if ([nickname isKindOfClass:[NSNull class]]) {
        nickname = @"";
    }
//    [[MMKV defaultMMKV] setString:nickname forKey:BD_NICKNAME];
    [[NSUserDefaults standardUserDefaults] setValue:nickname forKey:BD_NICKNAME];
}

+ (NSString *)getRealname {
//    NSString *realname = [[MMKV defaultMMKV] getStringForKey:BD_REALNAME];
    NSString *realname = [[NSUserDefaults standardUserDefaults] stringForKey:BD_REALNAME];
    if (realname == nil || realname == NULL || [realname isKindOfClass:[NSNull class]] || [realname length] == 0) {
        return @"";
    }
    return realname;
}

+ (void)setRealname:(NSString *)realname {
    if ([realname isKindOfClass:[NSNull class]]) {
        realname = @"";
    }
//    [[MMKV defaultMMKV] setString:realname forKey:BD_REALNAME];
    [[NSUserDefaults standardUserDefaults] setValue:realname forKey:BD_REALNAME];
}

+ (NSString *)getPassword {
//    NSString *password = [[MMKV defaultMMKV] getStringForKey:BD_PASSWORD];
    NSString *password = [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSWORD];
    if (password == nil || password == NULL || [password isKindOfClass:[NSNull class]] || [password length] == 0) {
        return @"";
    }
    return password;
}

+ (void)setPassword:(NSString *)password {
    if ([password isKindOfClass:[NSNull class]]) {
        password = @"";
    }
//    [[MMKV defaultMMKV] setString:password forKey:BD_PASSWORD];
    [[NSUserDefaults standardUserDefaults] setValue:password forKey:BD_PASSWORD];
}

+ (NSString *)getAvatar {
    //
//    NSString *avatar = [[MMKV defaultMMKV] getStringForKey:BD_AVATAR];
    NSString *avatar = [[NSUserDefaults standardUserDefaults] stringForKey:BD_AVATAR];
    if (avatar == nil || avatar == NULL || [avatar isKindOfClass:[NSNull class]] || [avatar length] == 0) {
        avatar = @"https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png";
    }
//    NSLog(@"avatar: %@", avatar);
    return avatar;
}

+ (void)setAvatar:(NSString *)avatar {
    if ([avatar isKindOfClass:[NSNull class]]) {
        avatar = @"";
    }
//    [[MMKV defaultMMKV] setString:avatar forKey:BD_AVATAR];
    [[NSUserDefaults standardUserDefaults] setValue:avatar forKey:BD_AVATAR];
}


+ (NSString *)getRole {
//    return [[MMKV defaultMMKV] getStringForKey:BD_ROLE];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_ROLE];
}

+ (void)setRole:(NSString *)role {
    if ([role isKindOfClass:[NSNull class]]) {
        role = @"";
    }
//    [[MMKV defaultMMKV] setString:role forKey:BD_ROLE];
    [[NSUserDefaults standardUserDefaults] setValue:role forKey:BD_ROLE];
}

+  (NSString *)getAppkey {
//    return [[MMKV defaultMMKV] getStringForKey:BD_APPKEY];
    NSString *appkey = [[NSUserDefaults standardUserDefaults] stringForKey:BD_APPKEY];
    if ([appkey isKindOfClass:[NSNull class]]) {
        return @"";
    }
    return appkey;
}

+ (void)setAppkey:(NSString *)appkey {
    if ([appkey isKindOfClass:[NSNull class]]) {
        appkey = @"";
    }
//    [[MMKV defaultMMKV] setString:appkey forKey:BD_APPKEY];
    [[NSUserDefaults standardUserDefaults] setValue:appkey forKey:BD_APPKEY];
}

+ (NSString *)getSubdomain {
//   return [[MMKV defaultMMKV] getStringForKey:BD_SUBDOMAIN];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_SUBDOMAIN];
}

+ (void)setSubdomain:(NSString *)subdomain {
    if ([subdomain isKindOfClass:[NSNull class]]) {
        subdomain = @"";
    }
//    [[MMKV defaultMMKV] setString:subdomain forKey:BD_SUBDOMAIN];
    [[NSUserDefaults standardUserDefaults] setValue:subdomain forKey:BD_SUBDOMAIN];
}

+ (NSString *)getDescription {
//    return [[MMKV defaultMMKV] getStringForKey:BD_DESCRIPTION];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_DESCRIPTION];
}

+ (void)setDescription:(NSString *)description {
    if ([description isKindOfClass:[NSNull class]]) {
        description = @"";
    }
//    [[MMKV defaultMMKV] setString:description forKey:BD_DESCRIPTION];
    [[NSUserDefaults standardUserDefaults] setValue:description forKey:BD_DESCRIPTION];
}

/**
 ValidateUntilDate
 */
+ (NSString *)getValidateUntilDate {
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_VALIDATE_UNTIL_DATE];
}

/**
 ValidateUntilDate
 */
+ (void)setValidateUntilDate:(NSString *)validateUntilDate {
    if ([validateUntilDate isKindOfClass:[NSNull class]]) {
        validateUntilDate = @"";
    }
    [[NSUserDefaults standardUserDefaults] setValue:validateUntilDate forKey:BD_VALIDATE_UNTIL_DATE];
}


+ (NSString *)getAcceptStatus {
//    NSString *acceptStatus = [[MMKV defaultMMKV] getStringForKey:BD_ACCEPTSTATUS];
    NSString *acceptStatus = [[NSUserDefaults standardUserDefaults] stringForKey:BD_ACCEPTSTATUS];
    if ([acceptStatus isEqualToString:BD_USER_STATUS_ONLINE]) {
        return @"在线";
    } else if ([acceptStatus isEqualToString:BD_USER_STATUS_BUSY]) {
        return @"忙线";
    } else {
        return @"小休";
    }
}

+ (void)setAcceptStatus:(NSString *)acceptStatus {
    if ([acceptStatus isKindOfClass:[NSNull class]]) {
        acceptStatus = @"";
    }
//    [[MMKV defaultMMKV] setString:acceptStatus forKey:BD_ACCEPTSTATUS];
    [[NSUserDefaults standardUserDefaults] setValue:acceptStatus forKey:BD_ACCEPTSTATUS];
}

+ (NSString *)getAutoReplyContent {
//    return [[MMKV defaultMMKV] getStringForKey:BD_AUTOREPLYCONTENT];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_AUTOREPLYCONTENT];
}

+ (void)setAutoReplyContent:(NSString *)autoreply {
    if ([autoreply isKindOfClass:[NSNull class]]) {
        autoreply = @"无自动回复";
    }
//    [[MMKV defaultMMKV] setString:autoreply forKey:BD_AUTOREPLYCONTENT];
    [[NSUserDefaults standardUserDefaults] setValue:autoreply forKey:BD_AUTOREPLYCONTENT];
}

+ (NSString *)getWelcomeTip {
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_WELCOMETIP];
}

+ (void)setWelcomeTip:(NSString *)welcomeTip {
    if ([welcomeTip isKindOfClass:[NSNull class]]) {
        welcomeTip = @"您好，有什么可以帮您的？";
    }
    [[NSUserDefaults standardUserDefaults] setValue:welcomeTip forKey:BD_WELCOMETIP];
}

+ (NSString *)getStatus {
//    return [[MMKV defaultMMKV] getStringForKey:BD_STATUS];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_STATUS];
}

+ (void)setStatus:(NSString *)status {
    if ([status isKindOfClass:[NSNull class]]) {
        status = @"在线";
    }
//    [[MMKV defaultMMKV] setString:status forKey:BD_STATUS];
    [[NSUserDefaults standardUserDefaults] setValue:status forKey:BD_STATUS];
}

+ (NSString *)getCurrentTid {
//    return [[MMKV defaultMMKV] getStringForKey:BD_CURRENT_TID];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_CURRENT_TID];
}

+ (void)setCurrentTid:(NSString *)tid {
    if ([tid isKindOfClass:[NSNull class]]) {
        tid = @"";
    }
//    [[MMKV defaultMMKV] setString:tid forKey:BD_CURRENT_TID];
    [[NSUserDefaults standardUserDefaults] setValue:tid forKey:BD_CURRENT_TID];
}


+ (NSString *)getPassportAccessToken {
//    return [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_ACCESS_TOKEN];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSPORT_ACCESS_TOKEN];
}

+ (void)setPassportAccessToken:(NSString *)accessToken {
    if ([accessToken isKindOfClass:[NSNull class]]) {
        accessToken = @"";
    }
    //
//    [[MMKV defaultMMKV] setString:accessToken forKey:BD_PASSPORT_ACCESS_TOKEN];
    [[NSUserDefaults standardUserDefaults] setValue:accessToken forKey:BD_PASSPORT_ACCESS_TOKEN];
}

+ (NSString *)getPassportExpiresIn {
//    return [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_EXPIRES_IN];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSPORT_EXPIRES_IN];
}

+ (void)setPassportExpiresIn:(NSString *)expiresIn {
    if ([expiresIn isKindOfClass:[NSNull class]]) {
        expiresIn = @"";
    }
    //
//    [[MMKV defaultMMKV] setString:expiresIn forKey:BD_PASSPORT_EXPIRES_IN];
    [[NSUserDefaults standardUserDefaults] setValue:expiresIn forKey:BD_PASSPORT_EXPIRES_IN];
}

+ (NSString *)getPassportRefreshToken {
//    return [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_REFRESH_TOKEN];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSPORT_REFRESH_TOKEN];
}

+ (void)setPassportRefreshToken:(NSString *)refreshToken {
    if ([refreshToken isKindOfClass:[NSNull class]]) {
        refreshToken = @"";
    }
    //
//    [[MMKV defaultMMKV] setString:refreshToken forKey:BD_PASSPORT_REFRESH_TOKEN];
    [[NSUserDefaults standardUserDefaults] setValue:refreshToken forKey:BD_PASSPORT_REFRESH_TOKEN];
}

+ (NSString *)getPassportTokenType {
//    return [[MMKV defaultMMKV] getStringForKey:BD_PASSPORT_TOKEN_TYPE];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_PASSPORT_TOKEN_TYPE];
}

+ (void)setPassportTokenType:(NSString *)tokenType {
    if ([tokenType isKindOfClass:[NSNull class]]) {
        tokenType = @"";
    }
    //
//    [[MMKV defaultMMKV] setString:tokenType forKey:BD_PASSPORT_TOKEN_TYPE];
    [[NSUserDefaults standardUserDefaults] setValue:tokenType forKey:BD_PASSPORT_TOKEN_TYPE];
}

+ (NSString *)getDeviceToken {
//    return [[MMKV defaultMMKV] getStringForKey:BD_DEVICE_TOKEN];
    return [[NSUserDefaults standardUserDefaults] stringForKey:BD_DEVICE_TOKEN];
}

+ (void)setDeviceToken:(NSString *)deviceToken {
    if ([deviceToken isKindOfClass:[NSNull class]]) {
        deviceToken = @"";
    }
    //
//    [[MMKV defaultMMKV] setString:deviceToken forKey:BD_DEVICE_TOKEN];
    [[NSUserDefaults standardUserDefaults] setValue:deviceToken forKey:BD_DEVICE_TOKEN];
}

+ (NSString *)getClientUUID {
    //
//    NSString *clientId = [[MMKV defaultMMKV] getStringForKey:BD_CLIENT_UUID];
//    if (clientId == nil || clientId == NULL || [clientId isKindOfClass:[NSNull class]] || [clientId length] == 0) {
//        NSString *newClientId = [[NSUUID UUID] UUIDString];
//        [[MMKV defaultMMKV] setString:newClientId forKey:BD_CLIENT_UUID];
//        return newClientId;
//    }
//    return clientId;
    
    return [UIDevice currentDevice].identifierForVendor.UUIDString;
}

+ (void)clear {
    //
    [self setUid:@""];
    [self setUsername:@""];
    [self setPassword:@""];
    [self setNickname:@""];
    [self setAvatar:@""];
    [self setSubdomain:@""];
    //
    [self setPassportAccessToken:@""];
    [self setPassportRefreshToken:@""];
    [self setPassportTokenType:@""];
    //
}

#pragma mark - 阅后即焚

+ (BOOL)getDestroyAfterReading:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType {
    NSString *key = [NSString stringWithFormat:@"%@_%@", tidOrUidOrGid, sessionType];
//    return [[MMKV defaultMMKV] getBoolForKey:key];
    return [[NSUserDefaults standardUserDefaults] boolForKey:key];
}

+ (void)setDestroyAfterReading:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType flag:(BOOL)flag {
     NSString *key = [NSString stringWithFormat:@"%@_%@", tidOrUidOrGid, sessionType];
//    [[MMKV defaultMMKV] setBool:flag forKey:key];
    [[NSUserDefaults standardUserDefaults] setBool:flag forKey:key];
}

+ (uint32_t)getDestroyAfterLength:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType {
     NSString *key = [NSString stringWithFormat:@"%@_%@_length", tidOrUidOrGid, sessionType];
//    return [[MMKV defaultMMKV] getUInt32ForKey:key defaultValue:0];
    return [[NSUserDefaults standardUserDefaults] integerForKey:key];
}

+ (void)setDestroyAfterLength:(NSString *)tidOrUidOrGid sessionType:(NSString *)sessionType length:(uint32_t)length {
    NSString *key = [NSString stringWithFormat:@"%@_%@_length", tidOrUidOrGid, sessionType];
//    [[MMKV defaultMMKV] setUInt32:length forKey:key];
    [[NSUserDefaults standardUserDefaults] setInteger:length forKey:key];
}


#pragma mark - 新消息提示

// 设置发送消息时是否播放提示音
+(BOOL) shouldRingWhenSendMessage {
//    return [[MMKV defaultMMKV] getBoolForKey:BD_SHOULD_PLAY_SEND_MESSAGE_SOUND defaultValue:TRUE];
    return [[NSUserDefaults standardUserDefaults] boolForKey:BD_SHOULD_PLAY_SEND_MESSAGE_SOUND];
}

+(void) setRingWhenSendMessage:(BOOL)flag {
//    [[MMKV defaultMMKV] setBool:flag forKey:BD_SHOULD_PLAY_SEND_MESSAGE_SOUND];
    [[NSUserDefaults standardUserDefaults] setBool:flag forKey:BD_SHOULD_PLAY_SEND_MESSAGE_SOUND];
}

// 设置接收到消息时是否播放提示音
+(BOOL) shouldRingWhenReceiveMessage {
//    return [[MMKV defaultMMKV] getBoolForKey:BD_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND defaultValue:TRUE];
    return [[NSUserDefaults standardUserDefaults] boolForKey:BD_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
}

+(void) setRingWhenReceiveMessage:(BOOL)flag {
//    [[MMKV defaultMMKV] setBool:flag forKey:BD_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
    [[NSUserDefaults standardUserDefaults] setBool:flag forKey:BD_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
}

// 设置接收到消息时是否震动
+(BOOL) shouldVibrateWhenReceiveMessage {
//    return [[MMKV defaultMMKV] getBoolForKey:BD_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE defaultValue:TRUE];
    return [[NSUserDefaults standardUserDefaults] boolForKey:BD_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
}

+(void) setVibrateWhenReceiveMessage:(BOOL)flag {
//    [[MMKV defaultMMKV] setBool:flag forKey:BD_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
    [[NSUserDefaults standardUserDefaults] setBool:flag forKey:BD_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
}

+(BOOL) hasSetDeviceInfo {
    return [[NSUserDefaults standardUserDefaults] boolForKey:BD_SHOULD_SET_DEVICE_INFO];
}

+(void) setDeviceInfo:(BOOL)flag {
    [[NSUserDefaults standardUserDefaults] setBool:flag forKey:BD_SHOULD_SET_DEVICE_INFO];
}


@end





