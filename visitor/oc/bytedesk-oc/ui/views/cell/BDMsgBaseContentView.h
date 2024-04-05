//
//  KFDSMsgBaseContentView.h
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BDMessageModel.h"

@protocol BDMsgBaseContentViewDelegate <NSObject>

// TODO: text点击超链接
- (void) linkUrlClicked:(NSString *)url;
// TODO: 打开放大图片
- (void) imageViewClicked:(UIImageView *)imageView;
// TODO: 点击文件消息
- (void) fileViewClicked:(NSString *)fileUrl;
// TODO: 点击视频消息
- (void) videoViewClicked:(NSString *)videoUrl;
// 机器人问答
- (void) robotLinkClicked:(NSString *)label withKey:(NSString *)key;
- (void) robotQuestionClicked:(NSString *)aid withQuestion:(NSString *)question withAnswer:(NSString *)answer;
// 转人工
//- (void) toAgentBtnClicked;
// 答案有帮助
- (void) robotRateUpBtnClicked:(BDMessageModel *)messageModel;
// 答案无帮助
- (void) robotRateDownBtnClicked:(BDMessageModel *)messageModel;

@end


@interface BDMsgBaseContentView : UIView //UIControl

@property (nonatomic, strong, readonly)  BDMessageModel   *model;
//@property (nonatomic, assign) BOOL isAgent;
@property (nonatomic, assign) BOOL highlighted; //UIControl
@property (nonatomic, assign) CGFloat xMargin;
@property (nonatomic, assign) CGFloat yTop;

//@property (nonatomic, strong) UIImageView * bubbleImageView;
@property (nonatomic, strong) UIView* bubbleView;

//@property (nonatomic, strong) UIActivityIndicatorView   *sendingStatusIndicatorView;
//@property (nonatomic, strong) UIButton                  *sendErrorStatusButton;

@property (nonatomic, weak) id<BDMsgBaseContentViewDelegate> delegate;

/**
 *  contentView初始化方法
 *
 *  @return content实例
 */
- (instancetype)initMessageContentView;
/**
 *  刷新方法
 *
 *  @param data 刷新数据
 */
- (void)initWithMessageModel:(BDMessageModel*)data;


/**
 *  手指从contentView内部抬起
 */
- (void)onTouchUpInside:(id)sender;

/**
 *  手指从contentView外部抬起
 */
- (void)onTouchUpOutside:(id)sender;

/**
 *  手指按下contentView
 */
- (void)onTouchDown:(id)sender;

/**
 *  聊天气泡图
 *
 *  @param state    目前的按压状态
 */
- (UIImage *)chatBubbleImageForState:(UIControlState)state isSend:(BOOL)send;// isClientSystem:(BOOL)system;


@end






