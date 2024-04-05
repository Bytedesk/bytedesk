//
//  KFDSMsgViewCell.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDMsgTableViewCell.h"
#import "BDBadgeView.h"
#import "BDConstants.h"

#import "BDMsgBaseContentView.h"
#import "BDMsgTextContentView.h"
#import "BDMsgImageContentView.h"
#import "BDMsgVoiceContentView.h"
#import "BDMsgFileContentView.h"
#import "BDMsgVideoContentView.h"
#import "BDMsgRobotContentView.h"

#import "BDMsgQuestionnairViewCell.h"
#import "BDMsgCommodityViewCell.h"

//@import AFNetworking;
#import <AFNetworking/UIImageView+AFNetworking.h>
//#import "UIImageView+AFNetworking.h"
#import "BDUIConstants.h"
#import "BDUtils.h"
#import "BDUIApis.h"

#define AVATAR_WIDTH_HEIGHT       40.0f
#define TIMESTAMp_HEIGHT          20.0f

@interface BDMsgTableViewCell ()<BDMsgBaseContentViewDelegate> {
    UILongPressGestureRecognizer *_longPressGesture;
}

@end

@implementation BDMsgTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
//        [self setQmui_shouldShowDebugColor:YES];
        self.backgroundColor = [UIColor systemGroupedBackgroundColor];
        
        [self addGestureRecognizer:[[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longGesturePress:)]];
    }
    return self;
}

- (void)initWithMessageModel:(BDMessageModel *)messageModel {
    _messageModel = messageModel;
//    _isAgent = FALSE;
    
    [self addSubviews];
    [self setNeedsLayout];
}
//
//- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
//    [super setSelected:selected animated:animated];
//    // Configure the view for the selected state
//}
//
//- (void)dealloc {
////    [self removeGestureRecognizer:_longPressGesture];
//}

- (BOOL)canBecomeFirstResponder  {
    return YES;
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    if (action == @selector(mycopy:)) {
        return YES;
    }
    else if (action == @selector(mydelete:)) {
        return YES;
    }
    else if (action == @selector(myrecall:)) {
        return YES;
    }
    
    return NO;
}

- (void)mycopy:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_TEXT] ||
        [_messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOT]||
        [_messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOTV2]) {
        //
        NSString *content = [_messageModel.content stringByReplacingOccurrencesOfString:@"amp;" withString:@""];
        [[UIPasteboard generalPasteboard] setString:content];
//        [[UIPasteboard generalPasteboard] setString:_messageModel.content];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        if (_messageModel.image_url) {
            [[UIPasteboard generalPasteboard] setString:_messageModel.image_url];
        }
        else if (_messageModel.pic_url){
            [[UIPasteboard generalPasteboard] setString:_messageModel.pic_url];
        }
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
        [[UIPasteboard generalPasteboard] setString:_messageModel.voice_url];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
        [[UIPasteboard generalPasteboard] setString:_messageModel.file_url];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_VIDEO]) {
        [[UIPasteboard generalPasteboard] setString:_messageModel.video_or_short_url];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_QUESTIONNAIRE]) {
        [[UIPasteboard generalPasteboard] setString:_messageModel.content];
    } else {
        [[UIPasteboard generalPasteboard] setString:_messageModel.content];
    }
    // TODO：其他类型消息记录
    [self resignFirstResponder];
    _baseContentView.highlighted = NO;
}

- (void)mySaveToAlbum:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(saveImageCellWith:)]) {
        [_delegate saveImageCellWith:self.tag];
    }
}



-(void)mydelete:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(removeCellWith:)]) {
        [_delegate removeCellWith:self.tag];
    }
}

-(void)myrecall:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(recallCellWith:)]) {
        [_delegate recallCellWith:self.tag];
    }
}

- (void)addSubviews {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if (self.timestampLabel) {
        [self.timestampLabel removeFromSuperview];
        self.timestampLabel = nil;
    }
    if (self.avatarImageView) {
        [self.avatarImageView removeFromSuperview];
        self.avatarImageView = nil;
    }
    if (self.nicknameLabel) {
        [self.nicknameLabel removeFromSuperview];
        self.nicknameLabel = nil;
    }
    if (self.baseContentView) {
        [self.baseContentView removeFromSuperview];
        self.baseContentView = nil;
    }
    if (self.sendingStatusActivityIndicator) {
        [self.sendingStatusActivityIndicator removeFromSuperview];
        self.sendingStatusActivityIndicator = nil;
    }
    if (self.resendButton) {
        [self.resendButton removeFromSuperview];
        self.resendButton = nil;
    }
    if (self.audioUnplayedIcon) {
        [self.audioUnplayedIcon removeFromSuperview];
        self.audioUnplayedIcon = nil;
    }
    if (self.statusLabel) {
        [self.statusLabel removeFromSuperview];
        self.statusLabel = nil;
    }
    
    if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        //
        _baseContentView = [[BDMsgTextContentView alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        //
        _baseContentView = [[BDMsgImageContentView alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
        //
        _baseContentView = [[BDMsgVoiceContentView alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
        //
        _baseContentView = [[BDMsgFileContentView alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_VIDEO]) {
        //
        _baseContentView = [[BDMsgVideoContentView alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_QUESTIONNAIRE]) {
        //
        _baseContentView = [[BDMsgQuestionnairViewCell alloc] initMessageContentView];
    } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOT] || [_messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOTV2]) {
        //
        _baseContentView = [[BDMsgRobotContentView alloc] initMessageContentView];
    } else {
        // TODO: 当前版本暂不支持查看此消息, 请升级
        // 暂未处理的类型，全部当做text类型处理
        _baseContentView = [[BDMsgTextContentView alloc] initMessageContentView];
    }
    //
    _baseContentView.delegate = self;
    //
    [self.contentView addSubview:self.timestampLabel];
//    [self.contentView addSubview:self.avatarImageView];
//    [self.contentView addSubview:self.nicknameLabel];
    [self.contentView addSubview:self.baseContentView];
    
    [self.contentView addSubview:self.sendingStatusActivityIndicator];
    [self.contentView addSubview:self.resendButton];
    
    [self.contentView addSubview:self.audioUnplayedIcon];
    [self.contentView addSubview:self.statusLabel];
}

- (UILabel *)timestampLabel {
    if (!_timestampLabel) {
        _timestampLabel = [UILabel new];
        _timestampLabel.textColor = [UIColor grayColor];
        _timestampLabel.font = [UIFont systemFontOfSize:11.0f];
    }
    return _timestampLabel;
}

- (UIImageView *)avatarImageView {
    if (!_avatarImageView) {
        _avatarImageView = [[UIImageView alloc] init];
        _avatarImageView.layer.cornerRadius = 5;
        _avatarImageView.layer.masksToBounds = YES;
        _avatarImageView.userInteractionEnabled = YES;
        //
        UITapGestureRecognizer *singleTap =  [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleAvatarClicked:)];
        [singleTap setNumberOfTapsRequired:1];
        [_avatarImageView addGestureRecognizer:singleTap];
    }
    return _avatarImageView;
}

- (UILabel *)nicknameLabel {
    if (!_nicknameLabel) {
        _nicknameLabel = [UILabel new];
        _nicknameLabel.textColor = [UIColor grayColor];
        _nicknameLabel.font = [UIFont systemFontOfSize:11.0f];
        _nicknameLabel.backgroundColor = [UIColor clearColor];
    }
    return _nicknameLabel;
}

//- (KFDSMsgBaseContentView *)bubbleView {
//    if (!_bubbleView) {
//        _bubbleView = [[KFDSMsgBaseContentView alloc] init];
//    }
//    return _bubbleView;
//}

- (UIActivityIndicatorView *)sendingStatusActivityIndicator {
    if (!_sendingStatusActivityIndicator) {
        _sendingStatusActivityIndicator = [UIActivityIndicatorView new];
        _sendingStatusActivityIndicator.color = [UIColor grayColor];
    }
    [_sendingStatusActivityIndicator startAnimating];
    return _sendingStatusActivityIndicator;
}

- (UIButton *)resendButton {
    if (!_resendButton) {
        _resendButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 15, 15)];
        [_resendButton setImage:[UIImage imageNamed:@"appkefu_error.png" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        [_resendButton addTarget:self action:@selector(sendErrorStatusButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _resendButton;
}

- (BDBadgeView *)audioUnplayedIcon {
    if (!_audioUnplayedIcon) {
        _audioUnplayedIcon = [BDBadgeView new];
    }
    return _audioUnplayedIcon;
}

- (UILabel *)statusLabel {
    if (!_statusLabel) {
        _statusLabel = [UILabel new];
        _statusLabel.textColor = [UIColor grayColor];
        _statusLabel.font = [UIFont systemFontOfSize:11.0f];
        _statusLabel.backgroundColor = [UIColor clearColor];
    }
    return _statusLabel;
}

- (void)layoutSubviews {
    [super layoutSubviews];
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self layoutTimestampLabel];
    [self layoutAvatarImageView];
    [self layoutNicknameLabel];
    [self layoutContentView];
    [self layoutSendingStatusActivityIndicator];
    [self layoutResendButton];
    [self layoutAudioUnplayedIcon];
    [self layoutStatusLabel];
}


- (void)layoutTimestampLabel {
    //
    NSString *timestampString = [BDUtils getOptimizedTimestamp:_messageModel.created_at];
    CGSize timestampSize = [timestampString sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0f]}];
    _timestampLabel.frame = CGRectMake((self.bounds.size.width - timestampSize.width - 10)/2,
                                       0.5f, timestampSize.width + 10.0f, timestampSize.height+1);
    [_timestampLabel setText:timestampString];
}

- (void)layoutAvatarImageView {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    
//    if (_isAgent && [_messageModel isClientSystem]) {
//        _avatarImageView.frame = CGRectMake(BDScreen.width - 50, TIMESTAMp_HEIGHT, AVATAR_WIDTH_HEIGHT, AVATAR_WIDTH_HEIGHT);
//    } else
        
    if ([_messageModel isSend]) {
        _avatarImageView.frame = CGRectMake(BDScreen.width - 50, TIMESTAMp_HEIGHT, AVATAR_WIDTH_HEIGHT, AVATAR_WIDTH_HEIGHT);
    }
    else {
        _avatarImageView.frame = CGRectMake(5, TIMESTAMp_HEIGHT, AVATAR_WIDTH_HEIGHT, AVATAR_WIDTH_HEIGHT);
    }
    [_avatarImageView setImageWithURL:[NSURL URLWithString:_messageModel.avatar] placeholderImage:[UIImage imageNamed:@"avatar"]];
}

- (void)layoutNicknameLabel {
    
//    if (_isAgent && [_messageModel isClientSystem]) {
//        _nicknameLabel.frame = CGRectZero;
//    } else
    if ([_messageModel isSend]) {
        _nicknameLabel.frame = CGRectZero;
    }
    else {
        _nicknameLabel.frame = CGRectMake(50, TIMESTAMp_HEIGHT, 200, 20);
        _nicknameLabel.text = _messageModel.nickname;
    }
}

- (void)layoutContentView {
    //
    [_baseContentView initWithMessageModel:_messageModel];
    [_baseContentView setNeedsLayout];
}

- (void)layoutSendingStatusActivityIndicator {
//    NSLog(@"%s, status: %@, content: %@", __PRETTY_FUNCTION__, self.messageModel.status, self.messageModel.content);
    
//    if ([self.messageModel isSend]) {
        if ([self.messageModel.status isKindOfClass:[NSString class]] &&
            [self.messageModel.status isEqualToString:BD_MESSAGE_STATUS_SENDING]) {
            //
            _sendingStatusActivityIndicator.frame = CGRectMake(_avatarImageView.frame.origin.x - self.messageModel.contentSize.width - self.messageModel.contentViewInsets.left - self.messageModel.contentViewInsets.right - 30,
                                                               _avatarImageView.frame.origin.y,
                                                               15, 15);
            
        } else {
            _sendingStatusActivityIndicator.frame = CGRectMake(-50, -50, 0, 0);
        }
        [_sendingStatusActivityIndicator setNeedsLayout];
//    }
}

- (void)layoutResendButton {
//    NSLog(@"%s, status: %@, content: %@", __PRETTY_FUNCTION__, self.messageModel.status, self.messageModel.content);
    
//    if ([self.messageModel isSend]) {
        if ([self.messageModel.status isKindOfClass:[NSString class]] &&
            [self.messageModel.status isEqualToString:BD_MESSAGE_STATUS_ERROR]) {
            
            _resendButton.frame = CGRectMake(_avatarImageView.frame.origin.x - self.messageModel.contentSize.width - self.messageModel.contentViewInsets.left - self.messageModel.contentViewInsets.right - 30,
                                             _avatarImageView.frame.origin.y,
                                             15, 15);
//            NSLog(@"x: %f, y:%f, w: %f, h: %f",
//                      _resendButton.frame.origin.x, _resendButton.frame.origin.y,
//                      _resendButton.frame.size.width, _resendButton.frame.size.height);
        } else {
            _resendButton.frame = CGRectZero;
        }
        [_resendButton setNeedsLayout];
//    }
}

- (void)layoutAudioUnplayedIcon {
    
}

- (void)layoutStatusLabel {
//    NSLog(@"%s, status: %@, content: %@", __PRETTY_FUNCTION__, self.messageModel.status, self.messageModel.content);
    // 如果已经为read的，则直接返回
    if ([_statusLabel.text isEqualToString:BD_MESSAGE_STATUS_READ]) {
        return;
    }
    //
    if ([self.messageModel.status isKindOfClass:[NSString class]] &&
        ([self.messageModel.status isEqualToString:BD_MESSAGE_STATUS_RECEIVED] ||
         [self.messageModel.status isEqualToString:BD_MESSAGE_STATUS_READ] )) {
        //
            _statusLabel.frame = CGRectMake(_avatarImageView.frame.origin.x - self.messageModel.contentSize.width - self.messageModel.contentViewInsets.left - self.messageModel.contentViewInsets.right - 30,
                                            _avatarImageView.frame.origin.y,
                                            25, 25);
        // TODO: 不直接使用汉语，修改为国际化
            _statusLabel.text = [self.messageModel.status isEqualToString:BD_MESSAGE_STATUS_READ] ?  @"已读" : @"送达";
        //
//        NSLog(@"%s x: %f, y:%f, w: %f, h: %f", __PRETTY_FUNCTION__,
//                  _statusLabel.frame.origin.x, _statusLabel.frame.origin.y,
//                  _statusLabel.frame.size.width, _statusLabel.frame.size.height);
    } else {
        _statusLabel.frame = CGRectMake(-50, -50, 0, 0);
    }
    [_statusLabel setNeedsLayout];
}

#pragma mark - UILongPressGestureRecognizer

- (void)longGesturePress:(UIGestureRecognizer*)gestureRecognizer {
//    if (gestureRecognizer.state != UIGestureRecognizerStateBegan || ![self becomeFirstResponder]) {
//        return;
//    }

    if ([gestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]] &&
        gestureRecognizer.state == UIGestureRecognizerStateBegan) {
    
        NSLog(@"%s", __PRETTY_FUNCTION__);
        
        [self becomeFirstResponder];//报错
        _baseContentView.highlighted = YES;
//        
////        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleMenuWillShowNotification:) name:UIMenuControllerWillShowMenuNotification object:nil];
//
        UIMenuController *menu = [UIMenuController sharedMenuController];
//        [menu setTargetRect:_bubbleView.frame inView:_bubbleView.superview];

        //添加你要自定义的MenuItem
        UIMenuItem *item = [[UIMenuItem alloc] initWithTitle:@"复制" action:@selector(mycopy:)];
        UIMenuItem *item2 = [[UIMenuItem alloc] initWithTitle:@"删除" action:@selector(mydelete:)];
        if(_messageModel.isSend) {
            UIMenuItem *item3 = [[UIMenuItem alloc] initWithTitle:@"撤回" action:@selector(myrecall:)];
            menu.menuItems = [NSArray arrayWithObjects:item,item2,item3,nil];
        } else if ([_messageModel.type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
            UIMenuItem *item4 = [[UIMenuItem alloc] initWithTitle:@"保存到相册" action:@selector(mySaveToAlbum:)];
            menu.menuItems = [NSArray arrayWithObjects:item,item2,item4,nil];
        } else {
            menu.menuItems = [NSArray arrayWithObjects:item,item2,nil];
        }
//        [menu setMenuVisible:YES animated:YES];
        [menu showMenuFromView:_baseContentView.superview rect:_baseContentView.frame];
        
        if ([menu isMenuVisible]) {
            NSLog(@"menu visible");
        }
        else {
            NSLog(@"menu invisible");
        }
    }
}

#pragma mark - Notifications

- (void)handleMenuWillHideNotification:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    _baseContentView.highlighted = NO;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIMenuControllerWillHideMenuNotification object:nil];
}

- (void)handleMenuWillShowNotification:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);

    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIMenuControllerWillShowMenuNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleMenuWillHideNotification:) name:UIMenuControllerWillHideMenuNotification object:nil];
}

- (void)handleAvatarClicked:(UIGestureRecognizer *)recognizer {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
//    if ([_delegate respondsToSelector:@selector(avatarClicked:)]) {
////        [_delegate avatarClicked:_messageModel];
//    }
}

#pragma mark - KFDSMsgBaseContentViewDelegate

// TODO: 点击客服/访客头像，显示其相关信息

// TODO: text点击超链接
- (void) linkUrlClicked:(NSString *)url {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(linkUrlClicked:)]) {
        [_delegate linkUrlClicked:url];
    }
}

// TODO: 打开放大图片
- (void) imageViewClicked:(UIImageView *)imageView {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(imageViewClicked:)]) {
        [_delegate imageViewClicked:imageView];
    }
}

//
- (void) fileViewClicked:(NSString *)fileUrl {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(fileViewClicked:)]) {
        [_delegate fileViewClicked:fileUrl];
    }
}

//
- (void) videoViewClicked:(NSString *)videoUrl {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(videoViewClicked:)]) {
        [_delegate videoViewClicked:videoUrl];
    }
}

- (void)sendErrorStatusButtonClicked:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([self.delegate respondsToSelector:@selector(sendErrorStatusButtonClicked:)]) {
        [self.delegate sendErrorStatusButtonClicked:_messageModel];
    }
}

- (void) robotLinkClicked:(NSString *)label withKey:(NSString *)key; {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(robotLinkClicked:withKey:)]) {
        [self.delegate robotLinkClicked:label withKey:key];
    }
}

- (void) robotQuestionClicked:(NSString *)aid withQuestion:(NSString *)question withAnswer:(NSString *)answer {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([self.delegate respondsToSelector:@selector(robotQuestionClicked:withQuestion:withAnswer:)]) {
        [self.delegate robotQuestionClicked:aid withQuestion:question withAnswer:answer];
    }
}

// 答案有帮助
- (void) robotRateUpBtnClicked:(BDMessageModel *)messageModel {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(robotRateUpBtnClicked:)]) {
        [self.delegate robotRateUpBtnClicked:messageModel];
    }
}

// 答案无帮助
- (void) robotRateDownBtnClicked:(BDMessageModel *)messageModel {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if ([_delegate respondsToSelector:@selector(robotRateDownBtnClicked:)]) {
        [self.delegate robotRateDownBtnClicked:messageModel];
    }
}

- (void) shouldReloadTable {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
//    if ([_delegate respondsToSelector:@selector(shouldReloadTable)]) {
//        [self.delegate shouldReloadTable];
//    }
}

@end












