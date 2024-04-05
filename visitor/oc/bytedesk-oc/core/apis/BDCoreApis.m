//
//  BDCoreApis.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/7/15.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDCoreApis.h"
#import "BDMQTTApis.h"
#import "BDDBApis.h"
#import "BDHttpApis.h"

#import "BDSettings.h"
#import "BDNotify.h"
#import "BDConstants.h"

static BDCoreApis *sharedInstance = nil;

@interface BDCoreApis()

@end

@implementation BDCoreApis


+ (BDCoreApis *)sharedInstance {
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDCoreApis alloc] init];
    });
    return sharedInstance;
}


#pragma mark - 访客接口

/**
 访客登录: 包含自动注册默认用户

 @param appkey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) init:(NSString *)appkey
    withSubdomain:(NSString *)subdomain
    resultSuccess:(SuccessCallbackBlock)success
 resultFailed:(FailedCallbackBlock)failed {
    
    [BDCoreApis loginWithAppkey:appkey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**

 @param appKey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withAppkey:(NSString *)appKey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    
    [BDCoreApis initWithUsername:username withNickname:@"" withAppkey:appKey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**

 @param appKey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withNickname:(NSString *)nickname
        withAppkey:(NSString *)appKey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [BDCoreApis initWithUsername:username withNickname:nickname withAvatar:@"" withAppkey:appKey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**

 @param appkey appkey
 @param subdomain 企业号
 @param success 成功回调
 @param failed 失败回调
 */
+ (void) initWithUsername:(NSString *)username
        withNickname:(NSString *)nickname
        withAvatar:(NSString *)avatar
        withAppkey:(NSString *)appkey
        withSubdomain:(NSString *)subdomain
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    NSString *usernameLocal = [BDSettings getUsername];
    NSString *passwordLocal = [BDSettings getPassword];
    if (usernameLocal != nil && [usernameLocal length] > 0) {
        NSLog(@"login username %@", usernameLocal);
        // 非空，直接登录
        [BDCoreApis loginWithUsername:usernameLocal withPassword:passwordLocal withAppkey:appkey withSubdomain:subdomain withRole:BD_ROLE_VISITOR resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }
    else {
        NSLog(@"register %@", username);
        // 注册
        NSString *password = username;
        [[BDHttpApis sharedInstance] registerUser:username withNickname:nickname withAvatar:avatar withPassword:password withSubDomain:subdomain resultSuccess:^(NSDictionary *dict) {
            
            NSNumber *status_code = [dict objectForKey:@"status_code"];
            if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                // 注册成功
    //            NSLog(@"response dict:  %@", dict);
                NSString *uid = [[dict objectForKey:@"data"] objectForKey:@"uid"];
                NSString *usernameServer = [[dict objectForKey:@"data"] objectForKey:@"username"];
                NSString *nicknameServer = [[dict objectForKey:@"data"] objectForKey:@"nickname"];
                NSString *avatarServer = [[dict objectForKey:@"data"] objectForKey:@"avatar"];
                //
                [BDSettings setUid:uid];
                [BDSettings setUsername:usernameServer];
                [BDSettings setPassword:password];
                [BDSettings setNickname:nicknameServer];
                [BDSettings setAvatar:avatarServer];
                [BDSettings setSubdomain:subdomain];
                [BDSettings setRole:BD_ROLE_VISITOR];
                
                // 登录
                [BDCoreApis loginWithUsername:usernameServer withPassword:password withAppkey:appkey withSubdomain:subdomain withRole:BD_ROLE_VISITOR resultSuccess:^(NSDictionary *dict) {
                    success(dict);
                } resultFailed:^(NSError *error) {
                    failed(error);
                }];
                
            } else {
                // 账号已经存在
                NSString *uid = [dict objectForKey:@"data"];
                NSString *usernameCompose = [NSString stringWithFormat:@"%@@%@", username, subdomain];
                //
                [BDSettings setUid:uid];
                [BDSettings setUsername:usernameCompose];
                [BDSettings setPassword:password];
                [BDSettings setNickname:nickname];
                [BDSettings setAvatar:avatar];
                [BDSettings setSubdomain:subdomain];
                [BDSettings setRole:BD_ROLE_VISITOR];
                
                // 登录
                [BDCoreApis loginWithUsername:usernameCompose withPassword:password withAppkey:appkey withSubdomain:subdomain withRole:BD_ROLE_VISITOR resultSuccess:^(NSDictionary *dict) {
                    success(dict);
                } resultFailed:^(NSError *error) {
                    failed(error);
                }];
            }
            
        } resultFailed:^(NSError *error) {
            
        }];
        
    }
}


+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
        withPassword:(NSString *)password
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    // 默认企业号为vip
    [[BDHttpApis sharedInstance] registerUser:username withNickname:nickname withPassword:password withSubDomain:@"vip" resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerUser:(NSString *)username
           withNickname:(NSString *)nickname
           withPassword:(NSString *)password
          withSubDomain:(NSString *)subDomain
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerUser:username withNickname:nickname withPassword:password withSubDomain:subDomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
          withAvatar:(NSString *)avatar
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerUser:username withNickname:nickname withAvatar:avatar withPassword:password withSubDomain:subDomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerEmail:(NSString *)email
         withNickname:(NSString *)nickname
         withPassword:(NSString *)password
        withSubDomain:(NSString *)subDomain
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerEmail:email withNickname:nickname withPassword:password withSubDomain:subDomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerUser:(NSString *)username
        withNickname:(NSString *)nickname
             withUid:(NSString *)uid
        withPassword:(NSString *)password
       withSubDomain:(NSString *)subDomain
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerUser:username withNickname:nickname withUid:uid withPassword:password withSubDomain:subDomain resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)registerAdmin:(NSString *)email
         withPassword:(NSString *)password
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerAdmin:email withPassword:password resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerMobile:(NSString *)mobile
             withEmail:(NSString *)email
          withNickname:(NSString *)nickname
        resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerMobile:mobile withEmail:email withNickname:nickname resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)bindMobile:(NSString *)mobile
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] bindMobile:mobile withEmail:email resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)isWeChatRegistered:(NSString *)unionId
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isWeChatRegistered:unionId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)registerWeChat:(NSString *)unionid
             withEmail:(NSString *)email
            withOpenId:(NSString *)openid
          withNickname:(NSString *)nickname
            withAvatar:(NSString *)avatar
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] registerWeChat:unionid withEmail:email withOpenId:openid withNickname:nickname withAvatar:avatar resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)bindWeChat:(NSString *)unionid
         withEmail:(NSString *)email
    resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] bindWeChat:unionid withEmail:email resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

//+ (void)uploadMobile:(NSString *)mobile
//        withNickname:(NSString *)nickname
//       resultSuccess:(SuccessCallbackBlock)success
//        resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] uploadMobile:mobile withNickname:nickname resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}

+ (void) loginWithAppkey:(NSString *)appkey
           withSubdomain:(NSString *)subdomain
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    // TODO: 检测网络是否可用，如果不可用，则直接返回
    //
    NSString *username = [BDSettings getUsername];
    if (username == nil || username == NULL || [username isKindOfClass:[NSNull class]] || [username length] == 0) {
        NSLog(@"username is null");
        //
        [[BDHttpApis sharedInstance] registerAnonymousUserWithAppkey:appkey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
           NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
           //
           [BDCoreApis oauthWithAppkey:appkey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
               //
               [BDCoreApis connect];
               // 上传设备信息
               [[BDHttpApis sharedInstance] uploadDeviceInfo];
               //
               success(dict);
           } resultFailed:^(NSError *error) {
               failed(error);
           }];
            
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }
    else {
        NSLog(@"username is %@",  username);
        
        [BDCoreApis oauthWithAppkey:appkey withSubdomain:subdomain resultSuccess:^(NSDictionary *dict) {
            //
            [BDCoreApis connect];
            // // 上传设备信息
            [[BDHttpApis sharedInstance] uploadDeviceInfo];
            //
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
    }
}

+ (void) oauthWithAppkey:(NSString *)appkey
           withSubdomain:(NSString *)subdomain
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    NSString *password = @"";
    if ([BDSettings getPassword].length > 1) {
        password = [BDSettings getPassword];
    } else {
        password = [BDSettings getUsername];
    }
    [[BDHttpApis sharedInstance] authWithRole:BD_ROLE_VISITOR
                                 withUsername:[BDSettings getUsername]
                                 withPassword:password
                                   withAppkey:appkey
                                withSubdomain:subdomain
                                resultSuccess:^(NSDictionary *dict) {
                                    // 返回结果
                                    NSDictionary *dict2 = @{@"message":@"login success", @"status_code": @200,  @"data":  @"success"};
                                    success(dict2);
                                }
                                resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void) requestThreadWithWorkGroupWid:(NSString *)wId
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestThreadWithWorkGroupWid:wId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestThreadWebRTCWithWorkGroupWid:(NSString *)wId
                                     webrtc:(int)webrtc
                              resultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestThreadWebRTCWithWorkGroupWid:wId webrtc:webrtc resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)requestThreadWithAgentUid:(NSString *)uid
                        resultSuccess:(SuccessCallbackBlock)success
                         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestThreadWithAgentUid:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestThread:(NSString *)workGroupWid
             withType:(NSString *)type
         withAgentUid:(NSString *)agentUid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestThread:workGroupWid withType:type withAgentUid:agentUid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestAgent:(NSString *)workGroupWid
//            withType:(NSString *)type
//        withAgentUid:(NSString *)agentUid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestAgent:workGroupWid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getContactThread:(NSString *)cid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getContactThread:cid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getGroupThread:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getGroupThread:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestQuestionnairWithTid:(NSString *)tid
                           itemQid:(NSString *)qid
                     resultSuccess:(SuccessCallbackBlock)success
                      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestQuestionnairWithTid:tid itemQid:qid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestChooseWorkGroup:(NSString *)wid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestChooseWorkGroup:wid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestChooseWorkGroupLiuXue:(NSString *)wid
               withWorkGroupNickname:(NSString *)workGroupNickname
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestChooseWorkGroupLiuXue:wid withWorkGroupNickname:workGroupNickname resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestChooseWorkGroupLiuXueLBS:(NSString *)wid
                  withWorkGroupNickname:(NSString *)workGroupNickname
                           withProvince:(NSString *)province
                               withCity:(NSString *)city
                          resultSuccess:(SuccessCallbackBlock)success
                           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestChooseWorkGroupLiuXueLBS:wid withWorkGroupNickname:workGroupNickname withProvince:province withCity:city resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)setNickname:(NSString *)nickname
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] setNickname:nickname resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)setAvatar:(NSString *)avatar
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] setAvatar:avatar resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)setDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] setDescription:description resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getFingerPrintWithUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] getFingerPrintWithUid:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getDeviceInfoByUid:(NSString *)uid
                    resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getDeviceInfoByUid:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)setFingerPrint:(NSString *)name
                   withKey:(NSString *)key
                 withValue:(NSString *)value
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {

    [[BDHttpApis sharedInstance] setFingerPrint:name withKey:key withValue:value resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)getWorkGroupStatus:(NSString *)wId
                    resultSuccess:(SuccessCallbackBlock)success
                     resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] getWorkGroupStatus:wId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)getAgentStatus:(NSString *)agentUid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getAgentStatus:agentUid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getUreadCount:(NSString *)wid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    [[BDHttpApis sharedInstance] getUreadCount:wid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getUreadCountVisitorWithResultSuccess:(SuccessCallbackBlock)success
                                 resultFailed:(FailedCallbackBlock)failed {
    [[BDHttpApis sharedInstance] getUreadCountVisitorWithResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getUreadCountAgentWithResultSuccess:(SuccessCallbackBlock)success
                               resultFailed:(FailedCallbackBlock)failed {
    [[BDHttpApis sharedInstance] getUreadCountAgentWithResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**
 访客端-查询访客所有未读消息
 */
+ (void)getUreadMessagesVisitor:(NSInteger)page
                       withSize:(NSInteger)size
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    [[BDHttpApis sharedInstance] getUreadMessagesVisitor:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**
 客服端-查询客服所有未读消息
 */
+ (void)getUreadMessagesAgent:(NSInteger)page
                     withSize:(NSInteger)size
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    [[BDHttpApis sharedInstance] getUreadMessagesAgent:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)visitorGetThreadsPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed{
    //
    [[BDHttpApis sharedInstance] visitorGetThreadsPage:page resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)visitorRate:(NSString *)tId
          withScore:(NSInteger)score
           withNote:(NSString *)note
         withInvite:(BOOL)invite
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed  {
    
    [[BDHttpApis sharedInstance] visitorRate:tId withScore:score withNote:note withInvite:invite resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void) loginWithUsername:(NSString *)username
                withPassword:(NSString *)password
                withAppkey:(NSString *)appkey
                withSubdomain:(NSString *)subdomain
                withRole:(NSString *)role
                resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] authWithRole:role
                                   withUsername:username
                                   withPassword:password
                                     withAppkey:appkey
                                  withSubdomain:subdomain
                                  resultSuccess:^(NSDictionary *dict) {
                                      
                                      // TODO: 处理超时
//                                      NSLog(@"login %@", dict);
                                      // 授权成功：用户名+密码+subdomain 验证通过
                                      success(dict);
                                      
                                      // 加载初始化数据
                                      [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                          // TODO: 建立长连接
                                          [[BDMQTTApis sharedInstance] connect];
                                          // 上传设备信息
                                          [[BDHttpApis sharedInstance] uploadDeviceInfo];
                                          
                                      } resultFailed:^(NSError *error) {
                                          failed(error);
                                      }];
                                      
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
    
}


#pragma mark - 客服接口

//登录流程：
//1. 发送http请求进行passport授权，获取token
//2. 发送gate请求
//3. 发送connector请求
//4. 发送send消息

+ (void) loginWithUsername:(NSString *)username
                   withPassword:(NSString *)password
                     withAppkey:(NSString *)appkey
                  withSubdomain:(NSString *)subDomain
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    //
    if (![username containsString:@"@"]) {
        username = [NSString stringWithFormat:@"%@@%@", username, subDomain];
    }
    NSLog(@"username %@", username);
    //
    [[BDHttpApis sharedInstance] authWithRole:BD_ROLE_ADMIN
                                   withUsername:username
                                   withPassword:password
                                     withAppkey:appkey
                                  withSubdomain:subDomain
                                  resultSuccess:^(NSDictionary *dict) {
                                      
                                      // TODO: 处理超时
//                                      NSLog(@"login %@", dict);
                                      // 授权成功：用户名+密码+subdomain 验证通过
                                      success(dict);
                                      
                                      // 加载初始化数据
                                      [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                          // TODO: 建立长连接
                                          [[BDMQTTApis sharedInstance] connect];
                                          // 上传设备信息
                                          [[BDHttpApis sharedInstance] uploadDeviceInfo];
                                          
                                      } resultFailed:^(NSError *error) {
                                          failed(error);
                                      }];
                                      
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}


+ (void) loginWithMobile:(NSString *)mobile
                withCode:(NSString *)code
            resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] authWithMobile:mobile
                                     withCode:code
                                resultSuccess:^(NSDictionary *dict) {
        
                                    // TODO: 处理超时
                                    // NSLog(@"login %@", dict);
                                    success(dict);
                                      
                                    // 加载初始化数据
                                    [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                        // TODO: 建立长连接
                                        [[BDMQTTApis sharedInstance] connect];
                                        // 上传设备信息
                                        [[BDHttpApis sharedInstance] uploadDeviceInfo];
                                          
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
        
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void) loginWithEmail:(NSString *)email
                withCode:(NSString *)code
            resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] authWithEmail:email
                                     withCode:code
                                resultSuccess:^(NSDictionary *dict) {
        
                                    // TODO: 处理超时
                                    // NSLog(@"login %@", dict);
                                    success(dict);
                                      
                                    // 加载初始化数据
                                    [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                        // TODO: 建立长连接
                                        [[BDMQTTApis sharedInstance] connect];
                                        // 上传设备信息
                                        [[BDHttpApis sharedInstance] uploadDeviceInfo];
                                          
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
        
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void) loginWithUnionId:(NSString *)unionId
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] authWithUnionId:unionId
                                resultSuccess:^(NSDictionary *dict) {
        
                                    // TODO: 处理超时
                                    // NSLog(@"login %@", dict);
                                    success(dict);
                                      
                                    // 加载初始化数据
                                    [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                        // TODO: 建立长连接
                                        [[BDMQTTApis sharedInstance] connect];
                                        // 上传设备信息
                                        [[BDHttpApis sharedInstance] uploadDeviceInfo];
                                          
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
        
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
    
}


+ (void)requestMobileCode:(NSString *)mobile
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestMobileCode:mobile resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)requestEmailCode:(NSString *)email
            resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] requestEmailCode:email resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)loginResultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    //
    NSString *username = [BDSettings getUsername];
    NSString *password = [BDSettings getPassword];
    NSString *appkey = [BDSettings getAppkey];
    NSString *subDomain = [BDSettings getSubdomain];
    NSLog(@"username: %@, password: %@, appkey: %@, subDomain: %@", username, password, appkey, subDomain);
    //
    [[BDHttpApis sharedInstance] authWithRole:BD_ROLE_ADMIN
                                 withUsername:username
                                 withPassword:password
                                   withAppkey:appkey
                                withSubdomain:subDomain
                                resultSuccess:^(NSDictionary *dict) {
                                    
                                    // 加载初始化数据
                                    [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
                                        // TODO: 建立长连接
                                        [[BDMQTTApis sharedInstance] connect];
                                        // 授权成功：用户名+密码+subdomain 验证通过
                                        success(dict);
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
                                    //
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
    
}

+ (void)initDataResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] initDataResultSuccess:^(NSDictionary *dict) {
        // TODO: 建立长连接
        [[BDMQTTApis sharedInstance] connect];
        // 成功回调
        success(dict);
    } resultFailed:^(NSError *error) {
        // 失败回调
        failed(error);
    }];
}

/**
 加载用户个人资料
 
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)getAgentProfileResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getAgentProfileResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getVisitorProfileResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getVisitorProfileResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**
 根据uid加载访客个人信息
 */
+ (void)getUserProfileByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getUserProfileByUid:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**
 根据uid加载crm客户信息
 */
+ (void)getCustomerByUid:(NSString *)uid resultSuccess:(SuccessCallbackBlock)success resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getCustomerByUid:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getOnlineAgents:(int)page
               withSize:(int)size
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] userOnline:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

/**
 他人加载用户详情
 
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)userDetail:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] userDetail:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateNickname:(NSString *)nickname
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateNickname:nickname resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateAutoReply:(BOOL)isAutoReply
            withContent:(NSString *)content
           withImageUrl:(NSString *)imageUrl
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateAutoReply:isAutoReply withContent:content withImageUrl:imageUrl resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)setAcceptStatus:(NSString *)acceptStatus
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] setAcceptStatus:acceptStatus resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getChatCodeResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getChatCodeResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateWelcomeTip:(NSString *)welcomeTip
             resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateWelcomeTip:welcomeTip resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updatePassword:(NSString *)password
             resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updatePassword:password resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateDescription:(NSString *)description
             resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateDescription:description resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)agentCloseThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] agentCloseThread:tid
        resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
}

+ (void)visitorCloseThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] visitorCloseThread:tid
        resultSuccess:^(NSDictionary *dict) {
            success(dict);
        } resultFailed:^(NSError *error) {
            failed(error);
        }];
}


#pragma mark - 群组接口

/**
 获取群组
 
 @param success <#success description#>
 @param failed <#failed description#>
 */
+ (void)getGroupsResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getGroupsResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getGroupDetail:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getGroupDetail:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getGroupMembers:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getGroupMembers:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)createGroup:(NSString *)nickname
        selectedContacts:(NSArray *)selectedContacts
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] createGroup:nickname type:BD_GROUP_TYPE_GROUP selectedContacts:selectedContacts resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)createGroup:(NSString *)nickname
               type:(NSString *)type
   selectedContacts:(NSArray *)selectedContacts
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] createGroup:nickname type:type selectedContacts:selectedContacts resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
    
}

+ (void)updateGroupNickname:(NSString *)nickname
                    withGroupGid:(NSString *)gid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateGroupNickname:nickname withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateGroupAnnouncement:(NSString *)announcement
                        withGroupGid:(NSString *)gid
                       resultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateGroupAnnouncement:announcement withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateGroupDescription:(NSString *)description
                       withGroupGid:(NSString *)gid
                      resultSuccess:(SuccessCallbackBlock)success
                       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateGroupDescription:description withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)inviteToGroup:(NSString *)uid
            withGroupGid:(NSString *)gid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] inviteToGroup:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)inviteListToGroup:(NSArray *)uidList
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] inviteListToGroup:uidList withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)joinGroup:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] joinGroup:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)applyGroup:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] applyGroup:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)approveGroupApply:(NSString *)nid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] approveGroupApply:nid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)denyGroupApply:(NSString *)nid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] denyGroupApply:nid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)kickGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] kickGroupMember:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)muteGroupMember:(NSString *)uid
          withGroupGid:(NSString *)gid
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] muteGroupMember:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unmuteGroupMember:(NSString *)uid
             withGroupGid:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unmuteGroupMember:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)setGroupAdmin:(NSString *)uid
         withGroupGid:(NSString *)gid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] setGroupAdmin:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)unsetGroupAdmin:(NSString *)uid
           withGroupGid:(NSString *)gid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unsetGroupAdmin:uid withGroupGid:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)transferGroup:(NSString *)uid
         withGroupGid:(NSString *)gid
      withNeedApprove:(BOOL)needApprove
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] transferGroup:uid
                                  withGroupGid:gid
                               withNeedApprove:needApprove
                                 resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)acceptGroupTransfer:(NSString *)nid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] acceptGroupTransfer:nid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)rejectGroupTransfer:(NSString *)nid
                   resultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] rejectGroupTransfer:nid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)withdrawGroup:(NSString *)gid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] withdrawGroup:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)dismissGroup:(NSString *)gid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] dismissGroup:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)filterGroup:(NSString *)keyword
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] filterGroup:keyword resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)filterGroupMembers:(NSString *)gid
        withKeyword:(NSString *)keyword
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] filterGroupMembers:gid withKeyword:keyword resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getNoticesPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getNoticesPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


#pragma mark - 社交关系

+ (void)getStrangersPage:(NSUInteger)page
                withSize:(NSUInteger)size
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getStrangersPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getFollowsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getFollowsPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getFansPage:(NSUInteger)page
           withSize:(NSUInteger)size
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getFansPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getFriendsPage:(NSUInteger)page
              withSize:(NSUInteger)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getFriendsPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getBlocksPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getBlocksPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)addFollow:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] addFollow:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unFollow:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unFollow:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)addFriend:(NSString *)uid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] addFriend:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)removeFriend:(NSString *)uid
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] removeFriend:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)isFollowed:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isFollowed:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getRelation:(NSString *)uid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getRelation:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)isShield:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isShield:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)isShielded:(NSString *)uid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isShielded:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)shield:(NSString *)uid
 resultSuccess:(SuccessCallbackBlock)success
  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] shield:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unshield:(NSString *)uid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unshield:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)addBlock:(NSString *)uid
        withNote:(NSString *)note
        withType:(NSString *)type
        withUuid:(NSString *)uuid
   resultSuccess:(SuccessCallbackBlock)success
    resultFailed:(FailedCallbackBlock)failed {
    //
//    NSString *type = @"默认类型";
    [[BDHttpApis sharedInstance] addBlock:uid withNote:note withType:type withUuid:uuid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unBlock:(NSString *)bid
  resultSuccess:(SuccessCallbackBlock)success
   resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unBlock:bid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 公共

+ (BOOL)loginAsVisitor {
    return [BDSettings loginAsVisitor];
}

+ (NSMutableArray *)getThreads {
    return [[BDDBApis sharedInstance] getThreads];
}

+ (NSMutableArray *)getIMThreads {
    return [[BDDBApis sharedInstance] getIMThreads];
}

+ (NSMutableArray *)getOnGoingThreads {
    return [[BDDBApis sharedInstance] getOnGoingThreads];
}

+ (NSMutableArray *)getHistoryThreads {
    return [[BDDBApis sharedInstance] getHistoryThreads];
}

+ (void)getThreadResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] agentThreadsResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getThreadHistoryRecords:(int)page
                         withSize:(int)size
                    resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] agentThreadHistoryRecords:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)queryTodos:(int)page
              withSize:(int)size
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] queryTodos:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)queryTickets:(int)page
              withSize:(int)size
         resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] queryTickets:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (NSMutableArray *)getQueues {
    return [[BDDBApis sharedInstance] getQueues];
}

+ (NSNumber *)getQueueCount {
    return [[BDDBApis sharedInstance] getQueueCount];
}

+ (void)getQueueResultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getQueuesPage:0 resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (NSMutableArray *)getContacts {
    return [[BDDBApis sharedInstance] getContacts];
}

+ (NSMutableArray *)getGroups {
    return [[BDDBApis sharedInstance] getGroups];
}

+ (void)getContactsResultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getContactsResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateCurrentThread:(NSString *)preTid
                 currentTid:(NSString *)tid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateCurrentThread:preTid currentTid:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markTopThread:(NSString *)tid
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markTopThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unmarkTopThread:(NSString *)tid
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unmarkTopThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markNoDisturbThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markNoDisturbThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unmarkNoDisturbThread:(NSString *)tid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unmarkNoDisturbThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markUnreadThread:(NSString *)tid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markUnreadThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)unmarkUnreadThread:(NSString *)tid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] unmarkUnreadThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markDeletedThread:(NSString *)tid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markDeletedThread:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markDeletedMessage:(NSString *)mid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markDeletedMessage:mid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markClearThreadMessage:(NSString *)tid
                 resultSuccess:(SuccessCallbackBlock)success
                  resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markClearThreadMessage:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markClearContactMessage:(NSString *)uid
                  resultSuccess:(SuccessCallbackBlock)success
                   resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markClearContactMessage:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)markClearGroupMessage:(NSString *)gid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] markClearGroupMessage:gid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

//+ (void)sendTextMessage:(NSString *)content
//                  toTid:(NSString *)tId
//                localId:(NSString *)localId
//            sessionType:(NSString *)sessiontype
//          resultSuccess:(SuccessCallbackBlock)success
//           resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendTextMessage:content toTid:tId localId:localId sessionType:sessiontype resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//+ (void)sendImageMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendImageMessage:content toTid:tId localId:localId sessionType:sessiontype resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//+ (void)sendFileMessage:(NSString *)content
//                  toTid:(NSString *)tId
//                localId:(NSString *)localId
//            sessionType:(NSString *)sessiontype
//                 format:(NSString *)format
//               fileName:(NSString *)fileName
//               fileSize:(NSString *)fileSize
//          resultSuccess:(SuccessCallbackBlock)success
//           resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendFileMessage:content
//                                           toTid:tId
//                                         localId:localId
//                                     sessionType:sessiontype
//                                          format:format
//                                        fileName:fileName
//                                        fileSize:fileSize
//                                   resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//+ (void)sendVoiceMessage:(NSString *)content
//                   toTid:(NSString *)tId
//                 localId:(NSString *)localId
//             sessionType:(NSString *)sessiontype
//             voiceLength:(int)voiceLength
//                  format:(NSString *)format
//           resultSuccess:(SuccessCallbackBlock)success
//            resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendVoiceMessage:content toTid:tId localId:localId
//                                      sessionType:sessiontype
//                                      voiceLength:voiceLength
//                                           format:format
//                                    resultSuccess:^(NSDictionary *dict) {
//                                        success(dict);
//                                    } resultFailed:^(NSError *error) {
//                                        failed(error);
//                                    }];
//}
//
//+ (void)sendCommodityMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendCommodityMessage:content toTid:tId localId:localId sessionType:sessiontype resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}
//
//+ (void)sendRedPacketMessage:(NSString *)content
//                       toTid:(NSString *)tId
//                     localId:(NSString *)localId
//                 sessionType:(NSString *)sessiontype
//               resultSuccess:(SuccessCallbackBlock)success
//                resultFailed:(FailedCallbackBlock)failed {
//    //
//    [[BDHttpApis sharedInstance] sendRedPacketMessage:content toTid:tId localId:localId sessionType:sessiontype resultSuccess:^(NSDictionary *dict) {
//        success(dict);
//    } resultFailed:^(NSError *error) {
//        failed(error);
//    }];
//}

+ (void)sendMessage:(NSString *)json
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] sendMessage:json
                               resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (NSMutableArray *)getMessagesWithUser {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_USER withUid:[BDSettings getUid]];
}

+ (NSMutableArray *)getMessagesWithUserPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_USER withUid:[BDSettings getUid]];
}

+ (NSMutableArray *)getMessagesWithThread:(NSString *)tid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_THREAD withUid:tid];
}

+ (void)clearMessagesWithThread:(NSString *)tid {
    [[BDDBApis sharedInstance] clearMessagesWithType:BD_GET_MESSAGE_TYPE_THREAD withUid:tid];
}

+ (NSMutableArray *)getMessagesWithAppointed:(NSString *)tid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_APPOINTED withUid:tid];
}

+ (NSMutableArray *)getMessagesWithThread:(NSString *)tid withPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_THREAD withUid:[BDSettings getUid]];
}

+ (NSMutableArray *)getMessagesWithWorkGroup:(NSString *)wid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_WORKGROUP withUid:wid];
}

+ (void)clearMessagesWithWorkGroup:(NSString *)wid {
    [[BDDBApis sharedInstance] clearMessagesWithType:BD_GET_MESSAGE_TYPE_WORKGROUP withUid:wid];
}

+ (NSMutableArray *)getMessagesWithWorkGroup:(NSString *)wid withPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_WORKGROUP withUid:[BDSettings getUid]];
}

+ (NSMutableArray *)getMessagesWithUser:(NSString *)uid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_USER withUid:uid];
}

+ (void)clearMessagesWithUser:(NSString *)uid {
    [[BDDBApis sharedInstance] clearMessagesWithType:BD_GET_MESSAGE_TYPE_USER withUid:uid];
}

+ (NSMutableArray *)getMessagesWithUser:(NSString *)uid withPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_USER withUid:uid];
}

+ (NSMutableArray *)getMessagesWithContact:(NSString *)cid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_CONTACT withUid:cid];
}

+ (NSMutableArray *)getMessagesWithContact:(NSString *)cid withPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_CONTACT withUid:cid];
}

+ (NSMutableArray *)getMessagesWithGroup:(NSString *)gid {
    return [[BDDBApis sharedInstance] getMessagesWithType:BD_GET_MESSAGE_TYPE_GROUP withUid:gid];
}

+ (NSMutableArray *)getMessagesWithGroup:(NSString *)gid withPage:(NSUInteger)page {
    return [[BDDBApis sharedInstance] getMessagesPage:page withSize:20 withType:BD_GET_MESSAGE_TYPE_GROUP withUid:gid];
}

+ (void)getMessageWithUser:(NSString *)uid
                  withPage:(NSInteger)page
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithUser:uid withPage:page resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getMessageWithUser:(NSString *)uid
                    withId:(NSInteger)messageid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithUser:uid withId:messageid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getMessageWithContact:(NSString *)cid
                     withPage:(NSInteger)page
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithContact:cid withPage:page resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getMessageWithContact:(NSString *)cid
                       withId:(NSInteger)messageid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithContact:cid withId:messageid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getMessageWithGroup:(NSString *)gid
                   withPage:(NSInteger)page
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithGroup:gid withPage:page resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getMessageWithGroup:(NSString *)gid
                     withId:(NSInteger)messageid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getMessageWithGroup:gid withId:messageid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)uploadImageData:(NSData *)imageData
          withImageName:(NSString *)imageName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] uploadImageData:imageData
                                   withImageName:imageName
                                     withLocalId:localId
                                   resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void)uploadAvatarData:(NSData *)imageData
             withLocalId:(NSString *)localId
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] uploadAvatarData:imageData
                                      withLocalId:localId
                                    resultSuccess:^(NSDictionary *dict) {
                                        success(dict);
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
}

+ (void)uploadVoiceData:(NSData *)voiceData
          withVoiceName:(NSString *)voiceName
            withLocalId:(NSString *)localId
          resultSuccess:(SuccessCallbackBlock)success
           resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] uploadVoiceData:voiceData
                                   withVoiceName:voiceName
                                     withLocalId:localId
                                   resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void)uploadFileData:(NSData *)fileData
         withFileName:(NSString *)fileName
           withLocalId:(NSString *)localId
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] uploadFileData:fileData
                                  withFileName:fileName
                                    withLocalId:localId
                                  resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void)uploadVideoData:(NSData *)videoData
          withVideoName:(NSString *)videoName
            withLocalId:(NSString *)localId
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] uploadVideoData:videoData withVideoName:videoName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 机器人

+ (void)initAnswer:(NSString *)type
  withWorkGroupWid:(NSString *)wid
      withAgentUid:(NSString *)aid
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] initAnswer:type withWorkGroupWid:wid withAgentUid:aid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)topAnswer:(NSString *)uid
    withThreadTid:(NSString *)tid
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] topAnswer:uid withThreadTid:tid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)queryAnswer:(NSString *)tid
     withQuestinQid:(NSString *)aid
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] queryAnswer:tid withQuestinQid:aid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)messageAnswer:(NSString *)wid
          withMessage:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] messageAnswer:wid withMessage:content resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)rateAnswer:(NSString *)aid
    withMessageMid:(NSString *)mid
          withRate:(BOOL)rate
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] rateAnswer:aid withMessageMid:mid withRate:rate resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)leaveMessage:(NSString *)type
             withUid:(NSString *)uid
        withMobile:(NSString *)mobile
         withContent:(NSString *)content
        withImageUrl:(NSString *)imageUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] leaveMessage:type
                                      withUid:uid
                                  withMobile:mobile
                                  withContent:content
                                 withImageUrl:imageUrl
                                resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getLeaveMessagesPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] getLeaveMessagesPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getLeaveMessagesVisitorPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] getLeaveMessagesVisitorPage:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)replyLeaveMessage:(NSString *)lid
             withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    
    [[BDHttpApis sharedInstance] replyLeaveMessage:lid withContent:content resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 常用语

+ (void)getCuwsWithResultSuccess:(SuccessCallbackBlock)success
                    resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getCuwsWithResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)createCuw:(int)categoryId
         withName:(NSString *)name
      withContent:(NSString *)content
      resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] createCuw:categoryId withName:name withContent:content resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateCuw:(int)cuwid
         withName:(NSString *)name
      withContent:(NSString *)content
    resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateCuw:cuwid withName:name withContent:content resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)deleteCuw:(int)cuwid
      resultSuccess:(SuccessCallbackBlock)success
     resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] deleteCuw:cuwid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 工单系统

+ (void)getTicketCategories:(NSString *)uid
              resultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getTicketCategories:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getTickets:(NSUInteger)page
          withSize:(NSUInteger)size
     resultSuccess:(SuccessCallbackBlock)success
      resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getTickets:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)createTicket:(NSString *)uid
          withUrgent:(NSString *)urgent
             withCid:(NSString *)cid
         withContent:(NSString *)content
          withMobile:(NSString *)mobile
           withEmail:(NSString *)email
         withFileUrl:(NSString *)fileUrl
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] createTicket:uid withUrgent:urgent withCid:cid withContent:content withMobile:mobile withEmail:email withFileUrl:fileUrl resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 意见反馈

+ (void)getFeedbackCategories:(NSString *)uid
                resultSuccess:(SuccessCallbackBlock)success
                 resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getFeedbackCategories:uid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getFeedbacks:(NSUInteger)page
            withSize:(NSUInteger)size
       resultSuccess:(SuccessCallbackBlock)success
        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getFeedbacks:page withSize:size resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)createFeedback:(NSString *)uid
               withCid:(NSString *)cid
           withContent:(NSString *)content
            withMobile:(NSString *)mobile
           withFileUrl:(NSString *)fileUrl
         resultSuccess:(SuccessCallbackBlock)success
          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] createFeedback:uid withCid:cid withContent:content withMobile:mobile withFileUrl:fileUrl resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - 帮助中心

+ (void)getSupportCategories:(NSString *)uid
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getSupportCategories:uid
                                    resultSuccess:^(NSDictionary *dict) {
                                        success(dict);
                                    } resultFailed:^(NSError *error) {
                                        failed(error);
                                    }];
}

+ (void)getCategoryDetail:(NSString *)cid
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getCategoryDetail:cid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)getSupportArticles:(NSString *)uid
             resultSuccess:(SuccessCallbackBlock)success
              resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getArticles:uid
                                    withType:@"support"
                                    withPage:0
                                    withSize:20
                               resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                              } resultFailed:^(NSError *error) {
                                  failed(error);
                              }];
}

+ (void)getArticles:(NSString *)uid
             withType:(NSString *)type
             withPage:(NSUInteger)page
             withSize:(NSUInteger)size
        resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getArticles:uid
                                    withType:type
                                    withPage:page
                                    withSize:size
                               resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void)getArticleDetail:(NSString *)aid
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getArticleDetail:aid resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)searchArticle:(NSString *)uid
          withContent:(NSString *)content
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] searchArticle:uid
                                   withContent:content
                                 resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                                } resultFailed:^(NSError *error) {
                                    failed(error);
                                }];
}

+ (void)rateArticle:(NSString *)aid
           withRate:(BOOL)rate
      resultSuccess:(SuccessCallbackBlock)success
       resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] rateArticle:aid
                                    withRate:rate
                               resultSuccess:^(NSDictionary *dict) {
                                    success(dict);
                              } resultFailed:^(NSError *error) {
                                    failed(error);
                              }];
}

#pragma mark - Section

+ (void)logoutResultSuccess:(SuccessCallbackBlock)success
               resultFailed:(FailedCallbackBlock)failed {
    
    // 首先判断是否已经登录，如果没有，则直接返回
    NSString *accessToken = [BDSettings getPassportAccessToken];
    if (accessToken == NULL || [accessToken isKindOfClass:[NSNull class]]) {
        NSNumber *number202 = [NSNumber numberWithInt:202];
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"false", @"data",
                              @"未登录,无需退出", @"message",
                              number202, @"status_code",
                              nil];
        success(dict);
        return;
    }
    NSLog(@"token %@", [BDSettings getPassportAccessToken]);
    
    // 调用rest接口
    [[BDHttpApis sharedInstance] logoutResultSuccess:^(NSDictionary *dict) {
        // 成功回调
        success(dict);
    } resultFailed:^(NSError *error) {
        // 失败回调
        failed(error);
    }];
    // 缓存清理
}

+ (void)connect {
    if ([BDSettings isAlreadyLogin]) {
        [[BDMQTTApis sharedInstance] connect];
    }
}

+ (void)reconnect {
    if ([BDSettings isAlreadyLogin]) {
        [[BDMQTTApis sharedInstance] connect];
    }
}

/**
 断开连接
 */
+ (void)disconnect {
    [[BDMQTTApis sharedInstance] disconnect];
//    [[BDMarsApis sharedInstance] disconnect];
}


+ (BOOL)isConnected {
    return [[BDMQTTApis sharedInstance] isConnected];
//    return [[BDMarsApis sharedInstance] is]
}


#pragma mark - Application

+ (void)applicationWillResignActive {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

+ (void)applicationDidEnterBackground {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

+ (void)applicationWillEnterForeground {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

+ (void)applicationDidBecomeActive {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

+ (void)applicationWillTerminate {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}


#pragma mark - 微信
    
+ (void)getWXAccessToken:(NSString *)code
           resultSuccess:(SuccessCallbackBlock)success
            resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getWXAccessToken:code resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)refreshWXAccessToken:(NSString *)refreshToken
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] refreshWXAccessToken:refreshToken resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)isWxAccessTokenValid:(NSString *)accessToken
                  withOpenId:(NSString *)openId
               resultSuccess:(SuccessCallbackBlock)success
                resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isWxAccessTokenValid:accessToken withOpenId:openId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

    
+ (void)getWxUserinfo:(NSString *)accessToken
           withOpenId:(NSString *)openId
        resultSuccess:(SuccessCallbackBlock)success
         resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] getWxUserinfo:accessToken withOpenId:openId resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

#pragma mark - device token

+ (void)isTokenUploadedResultSuccess:(SuccessCallbackBlock)success
                        resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] isTokenUploadedResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)updateDeviceToken:(NSString *)deviceToken
            resultSuccess:(SuccessCallbackBlock)success
             resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] updateDeviceToken:deviceToken resultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}

+ (void)deleteDeviceTokenResultSuccess:(SuccessCallbackBlock)success
                          resultFailed:(FailedCallbackBlock)failed {
    //
    [[BDHttpApis sharedInstance] deleteDeviceTokenResultSuccess:^(NSDictionary *dict) {
        success(dict);
    } resultFailed:^(NSError *error) {
        failed(error);
    }];
}


+ (void)cancelAllHttpRequest {
    //
    [[BDHttpApis sharedInstance] cancelAllHttpRequest];
}

#pragma mark - Mars

//+ (void)disconnect {
//    [[BDMarsApis sharedInstance] disconnect];
//}

+ (void)didEnterBackground {
//    [[BDMarsApis sharedInstance] didEnterBackground];
}

+ (void)willEnterForground {
//    [[BDMarsApis sharedInstance] willEnterForground];
}

+ (void)willTerminate {
//    [[BDMarsApis sharedInstance] willTerminate];
}
    
@end





