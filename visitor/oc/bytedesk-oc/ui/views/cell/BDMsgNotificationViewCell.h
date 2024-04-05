//
//  KFDSNotificationViewCell.h
//  feedback
//
//  Created by 萝卜丝 on 2018/2/21.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BDMessageModel.h"

@interface BDMsgNotificationViewCell : UITableViewCell

@property(nonatomic, strong) UILabel                   *timestampLabel;
@property(nonatomic, strong) UILabel                   *contentLabel;
@property(nonatomic, strong) BDMessageModel            *messageModel;

- (void)initWithMessageModel:(BDMessageModel *)messageModel;

@end
