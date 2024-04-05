//
//  BDChatKFViewController+Http.m
//  bytedesk-oc
//
//  Created by 宁金鹏 on 2023/9/12.
//

#import "BDChatKFViewController+Http.h"
#import "BDChatKFViewController.h"
#import "BDCoreApis.h"

@implementation BDChatKFViewController (Http)

//- (void)queryRobotAnswer:(NSString *)aid {
    
    // TODO: 插入本地消息，显示发送状态
    //
//    [BDCoreApis queryAnswer:self.mTid withQuestinQid:aid resultSuccess:^(NSDictionary *dict) {
//        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
//        //
//        NSNumber *status_code = [dict objectForKey:@"status_code"];
//        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
//            
//            NSDictionary *queryMessageDict = dict[@"data"][@"query"];
//            NSDictionary *replyMessageDict = dict[@"data"][@"reply"];
//            
//            //
//            BDMessageModel *queryMessageModel = [[BDMessageModel alloc] initWithDictionary:queryMessageDict];
//            [[BDDBApis sharedInstance] insertMessage:queryMessageModel];
//            
//            //
//            BDMessageModel *replyMessageModel = [[BDMessageModel alloc] initWithRobotRightAnswerDictionary:replyMessageDict];
//            [[BDDBApis sharedInstance] insertMessage:replyMessageModel];
//            
//        } else {
//            //
//            NSString *message = dict[@"message"];
//            NSLog(@"%s %@", __PRETTY_FUNCTION__, message);
//            [BDUIApis showErrorWithVC:self withMessage:message];
//        }
//        //
//        [self reloadTableData];
//        
//    } resultFailed:^(NSError *error) {
//        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
//        if (error) {
//            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
//        }
//    }];
//}

@end
