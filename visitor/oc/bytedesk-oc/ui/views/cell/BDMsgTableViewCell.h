//
//  KFDSMsgViewCell.h
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BDMessageModel.h"

@protocol BDMsgTableViewCellDelegate <NSObject>

@optional

- (void)saveImageCellWith:(NSInteger)tag;

- (void)removeCellWith:(NSInteger)tag;

- (void)recallCellWith:(NSInteger)tag;

//- (void)avatarClicked:(BDMessageModel *)messageModel;

- (void) linkUrlClicked:(NSString *)url;

// 打开放大图片
- (void) imageViewClicked:(UIImageView *)imageView;

- (void) fileViewClicked:(NSString *)fileUrl;

- (void) videoViewClicked:(NSString *)videoUrl;

- (void) sendErrorStatusButtonClicked:(BDMessageModel *)model;

- (void) robotLinkClicked:(NSString *)label withKey:(NSString *)key;

- (void) robotQuestionClicked:(NSString *)aid withQuestion:(NSString *)question withAnswer:(NSString *)answer;

// 答案有帮助
- (void) robotRateUpBtnClicked:(BDMessageModel *)messageModel;

// 答案无帮助
- (void) robotRateDownBtnClicked:(BDMessageModel *)messageModel;
//
//- (void) shouldReloadTable;

@end


@class BDMsgBaseContentView;
@class BDBadgeView;

@interface BDMsgTableViewCell : UITableViewCell

@property (nonatomic, strong) UILabel                   *timestampLabel;
@property (nonatomic, strong) UILabel                   *nicknameLabel;          //姓名（群显示 个人不显示）
@property (nonatomic, strong) UIImageView                 *avatarImageView;
@property (nonatomic, strong) BDMsgBaseContentView        *baseContentView;             //内容区域
@property (nonatomic, strong) UIActivityIndicatorView     *sendingStatusActivityIndicator;  //发送loading
@property (nonatomic, strong) UIButton                  *resendButton;             //重试
@property (nonatomic, strong) BDBadgeView               *audioUnplayedIcon;         //语音未读红点
@property (nonatomic, strong) UILabel                   *statusLabel;               //已读状态
@property (nonatomic, strong) BDMessageModel              *messageModel;
//@property (nonatomic, assign) BOOL                        isAgent;

@property(nonatomic, assign) id<BDMsgTableViewCellDelegate>  delegate;

- (void)initWithMessageModel:(BDMessageModel *)messageModel;

@end







