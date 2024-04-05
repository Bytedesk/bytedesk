//
//  BDMsgVideoContentView.m
//  bytedesk-ui
//
//  Created by 宁金鹏 on 2021/2/7.
//  Copyright © 2021 KeFuDaShi. All rights reserved.
//

#import "BDMsgVideoContentView.h"
#import "BDLoadProgressView.h"
//#import "KFDSMessageModel.h"
#import "BDUIConstants.h"

//@import AFNetworking;
#import <AFNetworking/UIImageView+AFNetworking.h>
//#import "UIImageView+AFNetworking.h"

@interface BDMsgVideoContentView ()

@property (nonatomic,strong) UIImageView * imageView;
@property (nonatomic, strong) UIView                    *kfImageViewSendingPercentageMaskView;
@property (nonatomic, strong) UIActivityIndicatorView   *kfImageSendPercentageIndicatorView;
@property (nonatomic, strong) UILabel                   *kfImageSendPercentageLabel;

@property (nonatomic,strong) BDLoadProgressView * progressView;

@end

@implementation BDMsgVideoContentView

- (instancetype)initMessageContentView
{
    if (self = [super initMessageContentView]) {
        self.opaque = YES;
        //
        _imageView  = [[UIImageView alloc] initWithFrame:CGRectZero];
        _imageView.backgroundColor = [UIColor blackColor];
        _imageView.userInteractionEnabled = YES;
        [self addSubview:_imageView];
        //
        UITapGestureRecognizer *singleTap =  [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleVideoClicked:)];
        [singleTap setNumberOfTapsRequired:1];
        [_imageView addGestureRecognizer:singleTap];
        //
        _progressView = [[BDLoadProgressView alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
        _progressView.maxProgress = 1.0f;
        [self addSubview:_progressView];
    }
    return self;
}

//- (BOOL)canBecomeFirstResponder {
//    return YES;
//}

- (void)initWithMessageModel:(BDMessageModel *)data{
    [super initWithMessageModel:data];
//    NSLog(@"%s %@", __PRETTY_FUNCTION__, self.model.video_or_short_url);
    
    // TODO: 图片大小按照图片长宽比例显示
    [_imageView setImageWithURL:[NSURL URLWithString:@"https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/videoplay.png"] placeholderImage:[UIImage imageNamed:@"Fav_Cell_File_Img"]];
//    [_imageView setImage:[UIImage imageNamed:@"Fav_Cell_File_Img"]];
    
    [self setNeedsLayout];
}

- (void)layoutSubviews{
    [super layoutSubviews];
    
    UIEdgeInsets contentInsets = self.model.contentViewInsets;
    
    CGSize size = CGSizeMake(80, 80);
    self.model.contentSize = size;
    
    CGRect imageFrame = CGRectZero;
    CGRect bubbleFrame = CGRectZero;
    CGRect boundsFrame = CGRectZero;
    
    if ([self.model isSend]) {
        imageFrame = CGRectMake(contentInsets.left+2, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width - 55, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
    }
    else {
        imageFrame = CGRectMake(contentInsets.left+3, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
        boundsFrame = CGRectMake(50, 40, bubbleFrame.size.width, bubbleFrame.size.height);
    }
    self.frame = boundsFrame;
    
    self.imageView.frame = imageFrame;
    self.bubbleView.frame = bubbleFrame;
    self.model.contentSize = boundsFrame.size;
}


- (void)handleVideoClicked:(UIGestureRecognizer *)recognizer {
    NSLog(@"%s, %@", __PRETTY_FUNCTION__, self.model.video_or_short_url);
    if ([self.delegate respondsToSelector:@selector(videoViewClicked:)]) {
        [self.delegate videoViewClicked:self.model.video_or_short_url];
    }
}

//- (void)updateProgress:(float)progress {
//    if (progress > 1.0) {
//        progress = 1.0;
//    }
//    self.progressView.progress = progress;
//}

@end
