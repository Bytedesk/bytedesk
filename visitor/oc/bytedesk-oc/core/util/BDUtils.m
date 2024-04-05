//
//  KFDSCUtils.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/23.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDUtils.h"
#import "sys/utsname.h"
#import "BDConfig.h"
#import "BDSettings.h"
#import "BDMQTTApis.h"
#import "BDDBApis.h"
//#import "BDMessageModel.h"

#import <AVFoundation/AVFoundation.h>

//https://github.com/Tencent/MMKV/blob/master/readme_cn.md
//#import <MMKV/MMKV.h>

@implementation BDUtils

+ (NSString*)getGuid
{
    NSDateFormatter *dateformat=[[NSDateFormatter alloc] init];
    [dateformat setDateFormat:@"yyyyMMddHHmmss"];
    NSString *timestamp = [dateformat stringFromDate:[NSDate date]];
    NSString *uuid = [[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""].lowercaseString;
    return [NSString stringWithFormat:@"%@%@", timestamp, uuid];
}

+ (NSString*)getCurrentDate
{
    NSDateFormatter *dateformat=[[NSDateFormatter alloc] init];
    [dateformat setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    return [dateformat stringFromDate:[NSDate date]];
}

+ (NSDate *)stringToDate:(NSString *)string {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    return [dateFormatter dateFromString:string];
}

+ (NSString *)getOptimizedTimestamp:(NSString *)dateString
{
    NSDate *date = [BDUtils stringToDate:dateString];
    // 1、设置时间 获取当前日期
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *dateComponents = [calendar components:(NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay) fromDate:[NSDate date]];
    
    [dateComponents setDay:dateComponents.day];
    NSDate *today = [calendar dateFromComponents:dateComponents];//今天
    
    [dateComponents setDay:dateComponents.day-1];
    NSDate *yesterday = [calendar dateFromComponents:dateComponents];//昨天
    
    [dateComponents setDay:dateComponents.day-1];
    NSDate *twoDaysAgo = [calendar dateFromComponents:dateComponents];//前天
    
    [dateComponents setDay:dateComponents.day-5];
    NSDate *lastWeek = [calendar dateFromComponents:dateComponents];//上星期
    
    //日期优化
    NSDate *lastMessageSentDate = date;
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setTimeZone:[NSTimeZone localTimeZone]];
    NSString *stringFromDate;
    
    if([lastMessageSentDate compare:today] == NSOrderedDescending)
    {
        [formatter setDateFormat:@"HH:mm:ss"];
        stringFromDate = [formatter stringFromDate:lastMessageSentDate];
    }
    else if ([lastMessageSentDate compare:yesterday] == NSOrderedDescending)
    {
        [formatter setDateFormat:@"HH:mm:ss"];
        stringFromDate = [formatter stringFromDate:lastMessageSentDate];
        
        stringFromDate = [NSString stringWithFormat:@"昨天 %@", stringFromDate];//NSLocalizedString(@"昨天", nil);
    }
    else if([lastMessageSentDate compare:twoDaysAgo] == NSOrderedDescending)
    {
        [formatter setDateFormat:@"HH:mm:ss"];
        stringFromDate = [formatter stringFromDate:lastMessageSentDate];
        
        stringFromDate = [NSString stringWithFormat:@"前天 %@", stringFromDate];//NSLocalizedString(@"前天", nil);
    }
    else if ([lastMessageSentDate compare:lastWeek] == NSOrderedDescending)
    {
        //[formatter setDateFormat:@"cccc HH:mm:ss"];//显示星期
        [formatter setDateFormat:@"MM-dd HH:mm"]; // yy-MM-dd HH:mm:ss
        stringFromDate = [formatter stringFromDate:lastMessageSentDate];
    }
    else
    {
        [formatter setDateFormat:@"MM-dd HH:mm"]; // yy-MM-dd HH:mm:ss
        stringFromDate = [formatter stringFromDate:lastMessageSentDate];
    }
    
    return stringFromDate;
}

+ (NSString*)getCurrentTimeString
{
    NSDateFormatter *dateformat=[[NSDateFormatter alloc] init];
    [dateformat setDateFormat:@"yyyyMMddHHmmss"];
    return [dateformat stringFromDate:[NSDate date]];
}

/**
 *  设备版本
 *
 *  @return e.g. iPhone 5S
 */
+ (NSString*)deviceVersion {
    // 需要
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString *deviceString = [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
//    NSLog(@"%s, %@", __PRETTY_FUNCTION__, deviceString);
    
    //iPhone
    if ([deviceString isEqualToString:@"iPhone1,1"])    return @"iPhone 1G";
    if ([deviceString isEqualToString:@"iPhone1,2"])    return @"iPhone 3G";
    if ([deviceString isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([deviceString isEqualToString:@"iPhone3,1"])    return @"iPhone 4";
    if ([deviceString isEqualToString:@"iPhone3,2"])    return @"Verizon iPhone 4";
    if ([deviceString isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([deviceString isEqualToString:@"iPhone5,1"])    return @"iPhone 5";
    if ([deviceString isEqualToString:@"iPhone5,2"])    return @"iPhone 5";
    if ([deviceString isEqualToString:@"iPhone5,3"])    return @"iPhone 5C";
    if ([deviceString isEqualToString:@"iPhone5,4"])    return @"iPhone 5C";
    if ([deviceString isEqualToString:@"iPhone6,1"])    return @"iPhone 5S";
    if ([deviceString isEqualToString:@"iPhone6,2"])    return @"iPhone 5S";
    if ([deviceString isEqualToString:@"iPhone7,1"])    return @"iPhone 6 Plus";
    if ([deviceString isEqualToString:@"iPhone7,2"])    return @"iPhone 6";
    if ([deviceString isEqualToString:@"iPhone8,1"])    return @"iPhone 6s";
    if ([deviceString isEqualToString:@"iPhone8,2"])    return @"iPhone 6s Plus";
    
    //iPod
    if ([deviceString isEqualToString:@"iPod1,1"])      return @"iPod Touch 1G";
    if ([deviceString isEqualToString:@"iPod2,1"])      return @"iPod Touch 2G";
    if ([deviceString isEqualToString:@"iPod3,1"])      return @"iPod Touch 3G";
    if ([deviceString isEqualToString:@"iPod4,1"])      return @"iPod Touch 4G";
    if ([deviceString isEqualToString:@"iPod5,1"])      return @"iPod Touch 5G";
    
    //iPad
    if ([deviceString isEqualToString:@"iPad1,1"])      return @"iPad";
    if ([deviceString isEqualToString:@"iPad2,1"])      return @"iPad 2 (WiFi)";
    if ([deviceString isEqualToString:@"iPad2,2"])      return @"iPad 2 (GSM)";
    if ([deviceString isEqualToString:@"iPad2,3"])      return @"iPad 2 (CDMA)";
    if ([deviceString isEqualToString:@"iPad2,4"])      return @"iPad 2 (32nm)";
    if ([deviceString isEqualToString:@"iPad2,5"])      return @"iPad mini (WiFi)";
    if ([deviceString isEqualToString:@"iPad2,6"])      return @"iPad mini (GSM)";
    if ([deviceString isEqualToString:@"iPad2,7"])      return @"iPad mini (CDMA)";
    
    if ([deviceString isEqualToString:@"iPad3,1"])      return @"iPad 3(WiFi)";
    if ([deviceString isEqualToString:@"iPad3,2"])      return @"iPad 3(CDMA)";
    if ([deviceString isEqualToString:@"iPad3,3"])      return @"iPad 3(4G)";
    if ([deviceString isEqualToString:@"iPad3,4"])      return @"iPad 4 (WiFi)";
    if ([deviceString isEqualToString:@"iPad3,5"])      return @"iPad 4 (4G)";
    if ([deviceString isEqualToString:@"iPad3,6"])      return @"iPad 4 (CDMA)";
    
    if ([deviceString isEqualToString:@"iPad4,1"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad4,2"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad4,3"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad5,3"])      return @"iPad Air 2";
    if ([deviceString isEqualToString:@"iPad5,4"])      return @"iPad Air 2";
    if ([deviceString isEqualToString:@"i386"])         return @"Simulator";
    if ([deviceString isEqualToString:@"x86_64"])       return @"Simulator";
    
    if ([deviceString isEqualToString:@"iPad4,4"]
        ||[deviceString isEqualToString:@"iPad4,5"]
        ||[deviceString isEqualToString:@"iPad4,6"])      return @"iPad mini 2";
    
    if ([deviceString isEqualToString:@"iPad4,7"]
        ||[deviceString isEqualToString:@"iPad4,8"]
        ||[deviceString isEqualToString:@"iPad4,9"])      return @"iPad mini 3";
    
    return deviceString;
}

+ (NSString *)dictToJson:(NSDictionary *)dict {
    NSError *writeError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&writeError];
    if (writeError) {
        NSLog(@"dictToJson error %@", writeError.description);
    }
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return jsonString;
}

+ (NSString *)getQRCodeLogin {
    return [NSString stringWithFormat:@"%@%@%@", [BDConfig getQRCodeBaseUrl], @"/qrcode/login?code=", [[NSUUID UUID] UUIDString]];
}

+ (NSString *)getQRCodeUser:(NSString *)uid {
    return [NSString stringWithFormat:@"%@%@%@", [BDConfig getQRCodeBaseUrl], @"/qrcode/user?uid=", uid];
}

+ (NSString *)getQRCodeGroup:(NSString *)gid {
    return [NSString stringWithFormat:@"%@%@%@", [BDConfig getQRCodeBaseUrl], @"/qrcode/group?gid=", gid];
}


+(BOOL) shouldRingWhenSendMessage
{
//    NSInteger value = [[MMKV defaultMMKV] getUInt32ForKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND defaultValue:1];
    NSInteger value = [[NSUserDefaults standardUserDefaults] integerForKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
    
    if (value == 2) {
        return FALSE;
    } else {
        return TRUE;
    }
}

+(void) setRingWhenSendMessage:(BOOL)flag {
    if (flag) {
//        [[MMKV defaultMMKV] setUInt32:1 forKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
        [[NSUserDefaults standardUserDefaults] setInteger:1 forKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
    } else {
//        [[MMKV defaultMMKV] setUInt32:2 forKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
        [[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_PLAY_SEND_MESSAGE_SOUND];
    }
}

+(BOOL) shouldRingWhenReceiveMessage {
//    NSInteger value = [[MMKV defaultMMKV] getUInt32ForKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
    NSInteger value = [[NSUserDefaults standardUserDefaults] integerForKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
    
    if (value == 2) {
        return FALSE;
    } else {
        return TRUE;
    }
}

+(void) setRingWhenReceiveMessage:(BOOL)flag {
    if (flag) {
//        [[MMKV defaultMMKV] setUInt32:1 forKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
        [[NSUserDefaults standardUserDefaults] setInteger:1 forKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
    } else {
//        [[MMKV defaultMMKV] setUInt32:2 forKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
        [[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_PLAY_RECEIVE_MESSAGE_SOUND];
    }
}

+(BOOL) shouldVibrateWhenReceiveMessage {
//    NSInteger value = [[MMKV defaultMMKV] getUInt32ForKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
    NSInteger value = [[NSUserDefaults standardUserDefaults] integerForKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
    
    if (value == 2) {
        return FALSE;
    } else {
        return TRUE;
    }
}

+(void) setVibrateWhenReceiveMessage:(BOOL)flag
{
    if (flag) {
//        [[MMKV defaultMMKV] setUInt32:1 forKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
        [[NSUserDefaults standardUserDefaults] setInteger:1 forKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
    } else {
//        [[MMKV defaultMMKV] setUInt32:2 forKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
        [[NSUserDefaults standardUserDefaults] setInteger:2 forKey:APPKEFU_SHOULD_VIBRATE_ON_RECEIVE_MESSAGE];
    }
}


#pragma 正则匹配手机号
+ (BOOL)checkTelNumber:(NSString *) telNumber {
    NSString *pattern = @"^1+[3578]+\\d{9}";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    BOOL isMatch = [pred evaluateWithObject:telNumber];
    return isMatch;
}


+(BOOL)canRecordVoice {
    return ![BDUtils isSimulator] && [BDUtils checkMicrophonePermission];
}

+(BOOL)checkMicrophonePermission {
    
    BOOL __block flag = FALSE;
    //如果录音设备不存在
    if (![[AVAudioSession sharedInstance] isInputAvailable] ) {
        
        flag = FALSE;
        //
//        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:AppKeFuLocalizedString(@"Promotion", nil) message:AppKeFuLocalizedString(@"NoRecordDevice", nil) delegate: nil cancelButtonTitle:AppKeFuLocalizedString(@"OK", nil) otherButtonTitles:nil];
//        [alertView show];
    }
    //录音设备存在，检测麦克风权限是否开启，否则提示用户开启
    else
    {
        if ([[AVAudioSession sharedInstance] respondsToSelector:@selector(requestRecordPermission:)]) {
            //
            [[AVAudioSession sharedInstance] requestRecordPermission:^(BOOL granted)
             {
                 flag = granted;
                 
                 if (granted) {
                     //NSLog(@"允许使用麦克风！");
                 } else {
                     //NSLog(@"不允许使用麦克风！");
//                     UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:AppKeFuLocalizedString(@"Promotion", nil)
//                                                                         message:AppKeFuLocalizedString(@"AllowUsingMicrophone", nil)
//                                                                        delegate:self
//                                                               cancelButtonTitle:AppKeFuLocalizedString(@"OK", nil)
//                                                               otherButtonTitles: nil];
//                     [alertView show];
                     
                 }
             }];
        }
    }
    
    return flag;
    
}

+ (BOOL) isSimulator {
#if TARGET_OS_SIMULATOR
    NSLog(@"模拟器不支持语音");
    //    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:AppKeFuLocalizedString(@"Tip", nil)
    //                                                                             message:AppKeFuLocalizedString(@"SimulatorNoVoice", nil)preferredStyle:UIAlertControllerStyleAlert];
    //    UIAlertAction *defaultAction = [UIAlertAction actionWithTitle:AppKeFuLocalizedString(@"OK", nil) style:UIAlertActionStyleDefault handler:nil];
    //    [alertController addAction:defaultAction];
    //    [self presentViewController:alertController animated:TRUE completion:nil];
    return TRUE;
#else
    return FALSE;
#endif
}

+ (void)clearOut {
    // 断开长连接
    [[BDMQTTApis sharedInstance] disconnect];
    // 清空本身缓存数据
    [BDSettings clear];
    // 清空本地数据
    [[BDDBApis sharedInstance] clearAll];
}

#pragma mark -

+ (NSString*)encodeString:(NSString*)originalUrl {
    NSCharacterSet *encodeUrlSet = [NSCharacterSet URLQueryAllowedCharacterSet];
    return [originalUrl stringByAddingPercentEncodingWithAllowedCharacters:encodeUrlSet];
}

+ (NSString *)decodeString:(NSString *)string {
    return [string stringByRemovingPercentEncoding];
}

+ (NSAttributedString*)transformContentToContentAttr:(NSString *)content {
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithData:[content dataUsingEncoding:NSUnicodeStringEncoding] options:@{ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType } documentAttributes:nil error:nil];
    [attributedString addAttributes:@{NSFontAttributeName: [UIFont systemFontOfSize:16]} range:NSMakeRange(0, attributedString.length)];
    return attributedString;
}

+ (void)setButtonTitleColor:(UIButton *)button {
    UIUserInterfaceStyle mode = UITraitCollection.currentTraitCollection.userInterfaceStyle;
    if (mode == UIUserInterfaceStyleDark) {
        [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    } else if (mode == UIUserInterfaceStyleLight) {
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    }
}

@end



