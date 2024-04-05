//
//  KFDSMsgBaseContentView.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDMsgBaseContentView.h"

#define DeviceHeight [[UIScreen mainScreen] bounds].size.height
#define DeviceWidth [[UIScreen mainScreen] bounds].size.width

@interface BDMsgBaseContentView ()

@end

@implementation BDMsgBaseContentView

//@synthesize sendingStatusIndicatorView, sendErrorStatusButton;

- (instancetype) initMessageContentView {
    if (self = [self initWithFrame:CGRectZero]) {
        //
//        _bubbleImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
//        _bubbleImageView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
//        _bubbleImageView.userInteractionEnabled = YES;
//        [self addSubview:_bubbleImageView];
        
        _xMargin = 10;
        _yTop = 20;
        
        _bubbleView = [[UIView alloc] initWithFrame:CGRectZero];
        _bubbleView.userInteractionEnabled = YES;
        _bubbleView.layer.cornerRadius = 10;
        _bubbleView.clipsToBounds = YES;
        [self addSubview:_bubbleView];
        
    }
    return self;
}

- (void)initWithMessageModel:(BDMessageModel*)data {
    _model = data;
//    _isAgent = agent;
    //
//    [_bubbleImageView setImage:[self chatBubbleImageForState:UIControlStateNormal isSend:[_model isSend]]];
//    [_bubbleImageView setHighlightedImage:[self chatBubbleImageForState:UIControlStateHighlighted isSend:[_model isSend]]];
//    _bubbleImageView.frame = self.bounds;

    [self setNeedsLayout];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    if (_model.isSend) {
        [_bubbleView setBackgroundColor:[UIColor colorNamed:@"chat_me_background" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
    } else {
        [_bubbleView setBackgroundColor:[UIColor colorNamed:@"chat_friend_background" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
    }
}

- (void)onTouchDown:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)onTouchUpInside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)onTouchUpOutside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

#pragma mark - Private

- (UIImage *)chatBubbleImageForState:(UIControlState)state isSend:(BOOL)send {

    NSString *imageName = @"";
    if (send) {
        if (state == UIControlStateNormal) {
            imageName = @"SenderTextNodeBkg";
        }
        else {
            imageName = @"SenderTextNodeBkgHL";
        }
    }
    else {
        if (state == UIControlStateNormal) {
            imageName = @"ReceiverTextNodeBkg";
        }
        else {
            imageName = @"ReceiverTextNodeBkgHL";
        }
    }
    
    return [[UIImage imageNamed:imageName inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] stretchableImageWithLeftCapWidth:50 topCapHeight:30];
}

- (void)setHighlighted:(BOOL)highlighted{
//    [super setHighlighted:highlighted];
//    _bubbleView.highlighted = highlighted;
}


@end

