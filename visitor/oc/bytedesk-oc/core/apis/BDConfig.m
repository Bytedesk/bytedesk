//
//  BDConfig.m
//  bytedesk-core
//
//  Created by 萝卜丝 on 2019/3/26.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDConfig.h"

//https://github.com/Tencent/MMKV/blob/master/readme_cn.md
//#import <MMKV/MMKV.h>

// TODO: 所有配置后期均修改为动态从服务器获取

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
////static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//static const DDLogLevel ddLogLevel = DDLogLevelWarning;
//#endif

//static BDConfig *sharedInstance = nil;

@interface BDConfig()

@end

@implementation BDConfig

// key
#define BD_MQTT_HOST_KEY                  @"BD_MQTT_HOST_KEY"
#define BD_MQTT_PORT_KEY                  @"BD_MQTT_PORT_KEY"
#define BD_MQTT_WEBSOCKET_WSS_URL_KEY     @"BD_MQTT_WEBSOCKET_WSS_URL_KEY"
//#define BD_MQTT_AUTH_USERNAME_KEY         @"BD_MQTT_AUTH_USERNAME_KEY"
//#define BD_MQTT_AUTH_PASSWORD_KEY         @"BD_MQTT_AUTH_PASSWORD_KEY"

#define BD_WEBRTC_STUN_SERVER_KEY         @"BD_WEBRTC_STUN_SERVER_KEY"
#define BD_WEBRTC_TURN_SERVER_KEY         @"BD_WEBRTC_TURN_SERVER_KEY"
#define BD_WEBRTC_TURN_USERNAME_KEY       @"BD_WEBRTC_TURN_USERNAME_KEY"
#define BD_WEBRTC_TURN_PASSWORD_KEY       @"BD_WEBRTC_TURN_PASSWORD_KEY"

#define BD_REST_API_HOST_KEY              @"BD_REST_API_HOST_KEY"
#define BD_UPLOAD_API_HOST_KEY            @"BD_UPLOAD_API_HOST_KEY"
//#define BD_REST_API_HOST_IM_KEY           @"BD_REST_API_HOST_IM_KEY"
//#define BD_REST_API_HOST_KF_KEY           @"BD_REST_API_HOST_KF_KEY"

// 切换服务器 im or kf
//#define BD_REST_API_HOST_MODE_KEY         @"BD_REST_API_HOST_MODE_KEY"
//#define BD_REST_API_HOST_MODE_IM          @"BD_REST_API_HOST_MODE_IM"
//#define BD_REST_API_HOST_MODE_KF          @"BD_REST_API_HOST_MODE_KF"

// value
#define BD_WEBRTC_STUN_SERVER               @"turn:turn.bytedesk.com:3478"
#define BD_WEBRTC_TURN_SERVER               @"turn:turn.bytedesk.com:3478"
#define BD_WEBRTC_TURN_USERNAME             @"jackning"
#define BD_WEBRTC_TURN_PASSWORD             @"kX1JiyPGVTtO3y0o"

// 真机调试连接localhost：1. 真机与电脑连接同一个wifi；2. ifconfig查看电脑ip；3. 修改为上述ip
//#define BD_IS_DEBUG                         YES
//#define BD_TLS_CONNECTION                   NO
//#define BD_IS_WEBSOCKET_WSS_CONNECTION      NO
//#define BD_MQTT_PORT                        3883
//#define BD_MQTT_HOST                        @"192.168.110.124"
//#define BD_UPLOAD_HOST                      @"192.168.110.124"
//#define BD_MQTT_WEBSOCKET_WSS_URL           [NSString stringWithFormat:@"wss://%@/websocket", BD_MQTT_HOST]
//#define BD_REST_API_HOST                    [NSString stringWithFormat:@"http://%@:8000/", BD_MQTT_HOST]
//#define BD_UPLOAD_API_HOST                  [NSString stringWithFormat:@"http://%@:8000/", BD_UPLOAD_HOST]

//#define BD_IS_DEBUG                         YES
//#define BD_TLS_CONNECTION                   NO
//#define BD_IS_WEBSOCKET_WSS_CONNECTION      NO
//#define BD_MQTT_PORT                        3883
//#define BD_MQTT_HOST                        @"127.0.0.1"
//#define BD_UPLOAD_HOST                      @"127.0.0.1"
//#define BD_MQTT_WEBSOCKET_WSS_URL           [NSString stringWithFormat:@"wss://%@/websocket", BD_MQTT_HOST]
//#define BD_REST_API_HOST                    [NSString stringWithFormat:@"http://%@:8000/", BD_MQTT_HOST]
//#define BD_UPLOAD_API_HOST                  [NSString stringWithFormat:@"http://%@:8000/", BD_UPLOAD_HOST]

// 线上服务器
#define BD_IS_DEBUG                         NO
#define BD_TLS_CONNECTION                   YES
#define BD_IS_WEBSOCKET_WSS_CONNECTION      YES
#define BD_MQTT_PORT                        13883
#define BD_MQTT_HOST                        @"ios.bytedesk.com"
#define BD_UPLOAD_HOST                      @"upload.bytedesk.com"
#define BD_MQTT_WEBSOCKET_WSS_URL           [NSString stringWithFormat:@"wss://%@/websocket", BD_MQTT_HOST]
#define BD_REST_API_HOST                    [NSString stringWithFormat:@"https://%@/", BD_MQTT_HOST]
#define BD_UPLOAD_API_HOST                  [NSString stringWithFormat:@"https://%@/", BD_UPLOAD_HOST]

+ (BOOL)isDebug {
    return BD_IS_DEBUG;
}

+ (BOOL)isTlsConnection {
    return BD_TLS_CONNECTION;
}

+ (BOOL)isWebSocketWssConnection {
    return BD_IS_WEBSOCKET_WSS_CONNECTION;
}

+ (void)restoreDefault {
    [[NSUserDefaults standardUserDefaults] setValue:BD_MQTT_HOST forKey:BD_MQTT_HOST_KEY];
    [[NSUserDefaults standardUserDefaults] setInteger:BD_MQTT_PORT forKey:BD_MQTT_PORT_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_WEBRTC_STUN_SERVER forKey:BD_WEBRTC_STUN_SERVER_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_WEBRTC_TURN_SERVER forKey:BD_WEBRTC_TURN_SERVER_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_WEBRTC_TURN_USERNAME forKey:BD_WEBRTC_TURN_USERNAME_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_WEBRTC_TURN_PASSWORD forKey:BD_WEBRTC_TURN_PASSWORD_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_REST_API_HOST forKey:BD_REST_API_HOST_KEY];
}

+ (void)enableLocalHost {
    [[NSUserDefaults standardUserDefaults] setValue:@"127.0.0.1" forKey:BD_MQTT_HOST_KEY];
    [[NSUserDefaults standardUserDefaults] setInteger:BD_MQTT_PORT forKey:BD_MQTT_PORT_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:BD_WEBRTC_STUN_SERVER forKey:BD_WEBRTC_STUN_SERVER_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:@"http://127.0.0.1:8000/" forKey:BD_REST_API_HOST_KEY];
}

+ (NSString *)getMqttHost {
//    NSString *mqttHost = [[MMKV defaultMMKV] getStringForKey:BD_MQTT_HOST_KEY];
    NSString *mqttHost = [[NSUserDefaults standardUserDefaults] valueForKey:BD_MQTT_HOST_KEY];
    if ([mqttHost isKindOfClass:[NSNull class]] || mqttHost == nil) {
        return BD_MQTT_HOST;
    }
    return mqttHost;
}

+ (void)setMqttHost:(NSString *)host {
    if ([host isKindOfClass:[NSNull class]]) {
        host = BD_MQTT_HOST;
    }
//    [[MMKV defaultMMKV] setString:host forKey:BD_MQTT_HOST_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:host forKey:BD_MQTT_HOST_KEY];
}

+ (NSInteger)getMqttPort {
//    NSInteger mqttPort = [[MMKV defaultMMKV] getUInt32ForKey:BD_MQTT_PORT_KEY defaultValue:3883];
    NSInteger mqttPort = [[NSUserDefaults standardUserDefaults] integerForKey:BD_MQTT_PORT_KEY];
    if (mqttPort == 0) {
        return BD_MQTT_PORT;
    }
    return mqttPort;
}

+ (void)setMqttPort:(uint32_t)port {
//    [[MMKV defaultMMKV] setUInt32:port forKey:BD_MQTT_PORT_KEY];
    [[NSUserDefaults standardUserDefaults] setInteger:port forKey:BD_MQTT_PORT_KEY];
}

+ (NSString *)getMqttWebSocketWssURL {
    NSString *mqttWebSocketWssURL = [[NSUserDefaults standardUserDefaults] stringForKey:BD_MQTT_WEBSOCKET_WSS_URL_KEY];
//    NSLog(@"mqttWebSocketWssURL %@", mqttWebSocketWssURL);
    if ([mqttWebSocketWssURL isKindOfClass:[NSNull class]] || mqttWebSocketWssURL == nil) {
        return BD_MQTT_WEBSOCKET_WSS_URL;
    }
    return mqttWebSocketWssURL;
}

+ (void)setMqttWebSocketWssURL:(NSString *)mqttWebSocketWssURL {
    if ([mqttWebSocketWssURL isKindOfClass:[NSNull class]]) {
        mqttWebSocketWssURL = BD_MQTT_WEBSOCKET_WSS_URL;
    }
    [[NSUserDefaults standardUserDefaults] setValue:mqttWebSocketWssURL forKey:BD_MQTT_WEBSOCKET_WSS_URL_KEY];
}


//+ (NSString *)getMqttAuthUsername {
//    NSString *mqttAuthUsername = [[MMKV defaultMMKV] getStringForKey:BD_MQTT_AUTH_USERNAME_KEY];
//    if (mqttAuthUsername) {
//        return mqttAuthUsername;
//    }
//    return BD_MQTT_AUTH_USERNAME;
//}
//
//+ (void)setMqttAuthUsername:(NSString *)username {
//    if ([username isKindOfClass:[NSNull class]]) {
//        username = BD_MQTT_AUTH_USERNAME;
//    }
//    [[MMKV defaultMMKV] setString:username forKey:BD_MQTT_AUTH_USERNAME_KEY];
//}
//
//+ (NSString *)getMqttAuthPassword {
//    NSString *mqttAuthPassword = [[MMKV defaultMMKV] getStringForKey:BD_MQTT_AUTH_PASSWORD_KEY];
//    if (mqttAuthPassword) {
//        return mqttAuthPassword;
//    }
//    return BD_MQTT_AUTH_PASSWORD;
//}
//
//+ (void)setMqttAuthPassword:(NSString *)password {
//    if ([password isKindOfClass:[NSNull class]]) {
//        password = BD_MQTT_AUTH_PASSWORD;
//    }
//    [[MMKV defaultMMKV] setString:password forKey:BD_MQTT_AUTH_PASSWORD_KEY];
//}

+ (NSString *)getWebRTCStunServer {
//    NSString *webRTCStunServer = [[MMKV defaultMMKV] getStringForKey:BD_WEBRTC_STUN_SERVER_KEY];
    NSString *webRTCStunServer = [[NSUserDefaults standardUserDefaults] stringForKey:BD_WEBRTC_STUN_SERVER_KEY];
    if ([webRTCStunServer isKindOfClass:[NSNull class]] || webRTCStunServer == nil) {
        return BD_WEBRTC_STUN_SERVER;
    }
    return webRTCStunServer;
}

+ (void)setWebRTCStunServer:(NSString *)stunServer {
    if ([stunServer isKindOfClass:[NSNull class]]) {
        stunServer = BD_WEBRTC_STUN_SERVER;
    }
//    [[MMKV defaultMMKV] setString:stunServer forKey:BD_WEBRTC_STUN_SERVER_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:stunServer forKey:BD_WEBRTC_STUN_SERVER_KEY];
}

+ (NSString *)getWebRTCTurnServer {
//    NSString *webRTCStunServer = [[MMKV defaultMMKV] getStringForKey:BD_WEBRTC_TURN_SERVER_KEY];
    NSString *webRTCStunServer = [[NSUserDefaults standardUserDefaults] stringForKey:BD_WEBRTC_TURN_SERVER_KEY];
    if ([webRTCStunServer isKindOfClass:[NSNull class]] || webRTCStunServer == nil) {
        return BD_WEBRTC_TURN_SERVER;
    }
    return webRTCStunServer;
}

+ (void)setWebRTCTurnServer:(NSString *)turnServer {
    if ([turnServer isKindOfClass:[NSNull class]]) {
        turnServer = BD_WEBRTC_TURN_SERVER;
    }
//    [[MMKV defaultMMKV] setString:turnServer forKey:BD_WEBRTC_TURN_SERVER_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:turnServer forKey:BD_WEBRTC_TURN_SERVER_KEY];
}

+ (NSString *)getWebRTCTurnUsername {
//    NSString *webRTCTurnUsername = [[MMKV defaultMMKV] getStringForKey:BD_WEBRTC_TURN_USERNAME_KEY];
    NSString *webRTCTurnUsername = [[NSUserDefaults standardUserDefaults] stringForKey:BD_WEBRTC_TURN_USERNAME_KEY];
    if ([webRTCTurnUsername isKindOfClass:[NSNull class]] || webRTCTurnUsername == nil) {
        return BD_WEBRTC_TURN_USERNAME;
    }
    return webRTCTurnUsername;
}

+ (void)setWebRTCTurnUsername:(NSString *)username {
    if ([username isKindOfClass:[NSNull class]]) {
        username = BD_WEBRTC_TURN_USERNAME;
    }
//    [[MMKV defaultMMKV] setString:username forKey:BD_WEBRTC_TURN_USERNAME_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:username forKey:BD_WEBRTC_TURN_USERNAME_KEY];
}

+ (NSString *)getWebRTCTurnPassword {
//    NSString *webRTCTurnPassword = [[MMKV defaultMMKV] getStringForKey:BD_WEBRTC_TURN_PASSWORD_KEY];
    NSString *webRTCTurnPassword = [[NSUserDefaults standardUserDefaults] stringForKey:BD_WEBRTC_TURN_PASSWORD_KEY];
    if ([webRTCTurnPassword isKindOfClass:[NSNull class]] || webRTCTurnPassword == nil) {
        return BD_WEBRTC_TURN_PASSWORD;
    }
    return webRTCTurnPassword;
}

+ (void)setWebRTCTurnPassword:(NSString *)password {
    if ([password isKindOfClass:[NSNull class]]) {
        password = BD_WEBRTC_TURN_PASSWORD;
    }
//    [[MMKV defaultMMKV] setString:password forKey:BD_WEBRTC_TURN_PASSWORD_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:password forKey:BD_WEBRTC_TURN_PASSWORD_KEY];
}

+ (NSString *)getRestApiHost {
//    NSString *restApiHost = [[MMKV defaultMMKV] getStringForKey:BD_REST_API_HOST_KEY];
    NSString *restApiHost = [[NSUserDefaults standardUserDefaults] stringForKey:BD_REST_API_HOST_KEY];
    if ([restApiHost isKindOfClass:[NSNull class]] || restApiHost == nil) {
        return BD_REST_API_HOST;
    }
    return restApiHost;
}

+ (void)setRestApiHost:(NSString *)host {
    if ([host isKindOfClass:[NSNull class]]) {
        host = BD_REST_API_HOST;
    }
//    [[MMKV defaultMMKV] setString:host forKey:BD_REST_API_HOST_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:host forKey:BD_REST_API_HOST_KEY];
}


+ (NSString *)getUploadApiHost {
//    NSString *restApiHost = [[MMKV defaultMMKV] getStringForKey:BD_REST_API_HOST_KEY];
    NSString *restApiHost = [[NSUserDefaults standardUserDefaults] stringForKey:BD_UPLOAD_API_HOST_KEY];
    if ([restApiHost isKindOfClass:[NSNull class]] || restApiHost == nil) {
        return BD_UPLOAD_API_HOST;
    }
    return restApiHost;
}

+ (void)setUploadApiHost:(NSString *)host {
    if ([host isKindOfClass:[NSNull class]]) {
        host = BD_UPLOAD_API_HOST;
    }
//    [[MMKV defaultMMKV] setString:host forKey:BD_REST_API_HOST_KEY];
    [[NSUserDefaults standardUserDefaults] setValue:host forKey:BD_REST_API_HOST_KEY];
}

//+ (NSString *)getRestApiHostIM {
////    NSString *restApiHost = [[MMKV defaultMMKV] getStringForKey:BD_REST_API_HOST_IM_KEY];
//    NSString *restApiHost = [[NSUserDefaults standardUserDefaults] stringForKey:BD_REST_API_HOST_IM_KEY];
//    if ([restApiHost isKindOfClass:[NSNull class]] || restApiHost == nil) {
//        return BD_REST_API_HOST_IM;
//    }
//    return restApiHost;
//}
//
//+ (void)setRestApiHostIM:(NSString *)host {
//    if ([host isKindOfClass:[NSNull class]]) {
//        host = BD_REST_API_HOST_IM;
//    }
////    [[MMKV defaultMMKV] setString:host forKey:BD_REST_API_HOST_IM_KEY];
//    [[NSUserDefaults standardUserDefaults] setValue:host forKey:BD_REST_API_HOST_IM_KEY];
//}
//
//+ (NSString *)getRestApiHostKF {
////    NSString *restApiHost = [[MMKV defaultMMKV] getStringForKey:BD_REST_API_HOST_KF_KEY];
//    NSString *restApiHost = [[NSUserDefaults standardUserDefaults] stringForKey:BD_REST_API_HOST_KF_KEY];
//    if ([restApiHost isKindOfClass:[NSNull class]] || restApiHost == nil) {
//        return BD_REST_API_HOST_KF;
//    }
//    return restApiHost;
//}
//
//+ (void)setRestApiHostKF:(NSString *)host {
//    if ([host isKindOfClass:[NSNull class]]) {
//        host = BD_REST_API_HOST_KF;
//    }
////    [[MMKV defaultMMKV] setString:host forKey:BD_REST_API_HOST_KF_KEY];
//    [[NSUserDefaults standardUserDefaults] setValue:host forKey:BD_REST_API_HOST_KF_KEY];
//}

//+ (void)switchToONE {
//    [BDConfig setRestApiHost:[BDConfig getRestApiHost]];
//}
//
//+ (void)switchToIM {
//    [BDConfig setRestApiHost:[BDConfig getRestApiHostIM]];
//}
//
//+ (void)switchToKF {
//    [BDConfig setRestApiHost:[BDConfig getRestApiHostKF]];
//}

///////////////////////////////////////

+ (NSString *)getQRCodeBaseUrl {
    return [self getRestApiHost];
}

+ (NSString *)getApiBaseUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"api"];
}

+ (NSString *)getPasswordOAuthTokenUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"oauth/token"];
}

+ (NSString *)getMobileOAuthTokenUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"mobile/token"];
}

+ (NSString *)getEmailOAuthTokenUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"email/token"];
}

+ (NSString *)getWeChatOAuthTokenUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"wechat/token"];
}

+ (NSString *)getMobileCodeUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"sms/api/send"];
}

+ (NSString *)getEmailCodeUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"email/api/send"];
}

+ (NSString *)getApiVisitorBaseUrl {
    return [NSString stringWithFormat:@"%@%@", [self getRestApiHost], @"visitor/api"];
}

+ (NSString *)getUploadApiVisitorBaseUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiHost], @"visitor/api"];
}

+ (NSString *)getVisitorGenerateUsernameUrl {
    return [NSString stringWithFormat:@"%@%@", [self getApiVisitorBaseUrl], @"username"];
}

+ (NSString *)getVisitorRegisterUserUrl {
    return [NSString stringWithFormat:@"%@%@", [self getApiVisitorBaseUrl], @"register/user"];
}

+ (NSString *)getVisitorRegisterUserUidUrl {
    return [NSString stringWithFormat:@"%@%@", [self getApiVisitorBaseUrl], @"register/user/uid"];
}

+ (NSString *)getVisitorRegisterAdminUrl {
    return [NSString stringWithFormat:@"%@%@", [self getApiVisitorBaseUrl], @"register"];
}

+ (NSString *)getUploadImageUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiVisitorBaseUrl], @"upload/image"];
}

+ (NSString *)getUploadAvatarUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiVisitorBaseUrl], @"upload/avatar"];
}

+ (NSString *)getUploadVoiceUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiVisitorBaseUrl], @"upload/voice"];
}

+ (NSString *)getUploadFileUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiVisitorBaseUrl], @"upload/file"];
}

+ (NSString *)getUploadVideoUrl {
    return [NSString stringWithFormat:@"%@%@", [self getUploadApiVisitorBaseUrl], @"upload/video"];
}



@end
