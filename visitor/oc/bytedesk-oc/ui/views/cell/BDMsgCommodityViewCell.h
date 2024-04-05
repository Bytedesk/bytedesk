//
//  BDCommodityTableViewCell.h
//  bytedesk-ui
//
//  Created by 萝卜丝 on 2019/3/15.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import <UIKit/UIKit.h>
//#import <bytedesk-core/bdcore.h>
#import "BDMessageModel.h"

NS_ASSUME_NONNULL_BEGIN

@protocol BDMsgCommodityViewCellDelegate <NSObject>

- (void)sendCommodityButtonClicked:(BDMessageModel *)messageModel;
//- (void)commodityBackgroundClicked:(BDMessageModel *)messageModel;

@end

@interface BDMsgCommodityViewCell : UITableViewCell

@property (nonatomic, weak) id<BDMsgCommodityViewCellDelegate> delegate;

@property(nonatomic, strong) UILabel                  *timestampLabel;

@property(nonatomic, strong) UIImageView                *commodityImageView;
@property(nonatomic, strong) UILabel                  *titleLabel;
@property(nonatomic, strong) UILabel                  *priceLabel;

@property(nonatomic, strong) UILabel                  *contentLabel;
@property(nonatomic, strong) BDMessageModel             *messageModel;

@property(nonatomic, strong) UIButton                 *sendButton;

- (void)initWithMessageModel:(BDMessageModel *)messageModel;

@end

NS_ASSUME_NONNULL_END
