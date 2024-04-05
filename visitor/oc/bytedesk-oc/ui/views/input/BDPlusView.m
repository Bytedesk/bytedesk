//
//  KFPlusView.m
//  ChatViewController
//
//  Created by jack on 14-5-4.
//  Copyright (c) 2014年 appkefu.com. All rights reserved.
//

#import "BDPlusView.h"

#define SHAREMORE_ITEMS_TOP_MARGIN       15.0f

#define SHAREMORE_ITEMS_WIDTH            53.0f
#define SHAREMORE_ITEMS_HEIGHT           55.0f

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface BDPlusView ()

@property (nonatomic, strong) UIView    *topLineView;

@property (nonatomic, strong) UIButton  *sharePickPhotoButton;
@property (nonatomic, strong) UIButton  *shareTakePhotoButton;
@property (nonatomic, strong) UIButton  *shareFileButton;
@property (nonatomic, strong) UIButton  *shareLeaveMsgButton;
@property (nonatomic, strong) UIButton  *shareRateButton;
//@property (nonatomic, strong) UIButton  *shareShowFAQButton;

@property (nonatomic, assign) NSInteger mButtonMargin;

@end

@implementation BDPlusView

@synthesize delegate,
            topLineView,
            sharePickPhotoButton,
            shareTakePhotoButton,
//            shareShowFAQButton,
            shareRateButton,
            shareFileButton,
            shareLeaveMsgButton,

            mButtonMargin;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        mButtonMargin = ([UIScreen mainScreen].bounds.size.width - SHAREMORE_ITEMS_WIDTH*4)/5;
        self.backgroundColor = [UIColor clearColor]; //UIColorFromRGB(0XEBEBEB);
        self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;

        [self setUpSubViews];
    }
    return self;
}

-(void)dealloc
{
    topLineView = nil;
    sharePickPhotoButton = nil;
    shareTakePhotoButton = nil;
    shareFileButton = nil;
    shareLeaveMsgButton = nil;
    shareRateButton = nil;
//    shareShowFAQButton = nil;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

-(void)setUpSubViews
{
    [self addSubview:[self topLineView]];
    [self addSubview:[self sharePickPhotoButton]];
    [self addSubview:[self shareTakePhotoButton]];
    [self addSubview:[self shareFileButton]];
    [self addSubview:[self shareLeaveMsgButton]];
    [self addSubview:[self shareRateButton]];
//    [self addSubview:[self shareShowFAQButton]];
}

#pragma mark Init SubViews
-(UIView *)topLineView
{
    if (!topLineView) {
        topLineView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, self.bounds.size.width, 0.5f)];
        topLineView.backgroundColor = UIColorFromRGB(0X939698);
        topLineView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    }
    
    return topLineView;
}

#pragma mark - 相册

-(UIButton *)sharePickPhotoButton
{
    if (!sharePickPhotoButton) {
        
        CGRect frame = CGRectMake(mButtonMargin,
                                  SHAREMORE_ITEMS_TOP_MARGIN,
                                  SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_HEIGHT);
        sharePickPhotoButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [sharePickPhotoButton setFrame:frame];
        [sharePickPhotoButton setImage:[UIImage systemImageNamed:@"photo.circle"] forState:UIControlStateNormal];
        [sharePickPhotoButton setImage:[UIImage systemImageNamed:@"photo.circle.fill"] forState:UIControlStateHighlighted];
//        sharePickPhotoButton.imageView.frame = CGRectMake(5, 5, 40, 40);
        [sharePickPhotoButton setTitle:@"相册" forState:UIControlStateNormal];
        [sharePickPhotoButton setTitle:@"相册" forState:UIControlStateHighlighted];
        [[sharePickPhotoButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
        [sharePickPhotoButton setImageEdgeInsets:UIEdgeInsetsMake(0, 10, 15, -12)];
        [sharePickPhotoButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -5, 0, 15)];
        //
        sharePickPhotoButton.layer.cornerRadius = 5.0;
        sharePickPhotoButton.layer.masksToBounds = YES;
//        sharePickPhotoButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        sharePickPhotoButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        sharePickPhotoButton.layer.borderWidth = 0.5;
        [sharePickPhotoButton addTarget:self action:@selector(sharePickPhotoButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [self setButtonTitleColor:sharePickPhotoButton];
    }
    
    return sharePickPhotoButton;
}

- (void)setButtonTitleColor:(UIButton *)button {
    UIUserInterfaceStyle mode = UITraitCollection.currentTraitCollection.userInterfaceStyle;
    if (mode == UIUserInterfaceStyleDark) {
//            NSLog(@"深色模式");
    } else if (mode == UIUserInterfaceStyleLight) {
//        浅色
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    }
}

//图片缩放到指定大小尺寸
- (UIImage *)scaleToSize:(UIImage *)img size:(CGSize)size{
    // 创建一个bitmap的context
    // 并把它设置成为当前正在使用的context
    UIGraphicsBeginImageContext(size);
    // 绘制改变大小的图片
    [img drawInRect:CGRectMake(0, 0, size.width, size.height)];
    // 从当前context中创建一个改变大小后的图片
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();
    // 返回新的改变大小后的图片
    return scaledImage;
}

#pragma mark - 拍照

-(UIButton *)shareTakePhotoButton
{
    if (!shareTakePhotoButton) {
        
        CGRect frame = CGRectMake(mButtonMargin*2 + SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_TOP_MARGIN,
                                  SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_HEIGHT);
        
        shareTakePhotoButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [shareTakePhotoButton setFrame:frame];
//        [shareTakePhotoButton setBackgroundImage:[UIImage imageNamed:@"sharemore_video_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        [shareTakePhotoButton setImage:[UIImage systemImageNamed:@"camera.circle"] forState:UIControlStateNormal];
        [shareTakePhotoButton setImage:[UIImage systemImageNamed:@"camera.circle.fill"] forState:UIControlStateHighlighted];
        [shareTakePhotoButton setTitle:@"拍照" forState:UIControlStateNormal];
        [shareTakePhotoButton setTitle:@"拍照" forState:UIControlStateHighlighted];
        [[shareTakePhotoButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
        [shareTakePhotoButton setImageEdgeInsets:UIEdgeInsetsMake(0, 10, 15, -12)];
        [shareTakePhotoButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -5, 0, 15)];
        //
        shareTakePhotoButton.layer.cornerRadius = 5.0;
        shareTakePhotoButton.layer.masksToBounds = YES;
//        shareTakePhotoButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        shareTakePhotoButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        shareTakePhotoButton.layer.borderWidth = 0.5;
        
        [shareTakePhotoButton addTarget:self action:@selector(shareTakePhotoButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [self setButtonTitleColor:shareTakePhotoButton];
    }
    
    return shareTakePhotoButton;
}

#pragma mark - 文件

-(UIButton *)shareFileButton
{
    if (!shareFileButton) {
        
        CGRect frame = CGRectMake(mButtonMargin * 3 + SHAREMORE_ITEMS_WIDTH * 2,
                                  SHAREMORE_ITEMS_TOP_MARGIN,
                                  SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_HEIGHT);
        
        shareFileButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [shareFileButton setFrame:frame];
//        [shareFilebButton setBackgroundImage:[UIImage imageNamed:@"sharemore_pic_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        [shareFileButton setImage:[UIImage systemImageNamed:@"folder.circle"] forState:UIControlStateNormal];
        [shareFileButton setImage:[UIImage systemImageNamed:@"folder.circle.fill"] forState:UIControlStateHighlighted];
        [shareFileButton setTitle:@"文件" forState:UIControlStateNormal];
        [shareFileButton setTitle:@"文件" forState:UIControlStateHighlighted];
        [[shareFileButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
        [shareFileButton setImageEdgeInsets:UIEdgeInsetsMake(0, 10, 15, -12)];
        [shareFileButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -5, 0, 15)];
        //
        shareFileButton.layer.cornerRadius = 5.0;
        shareFileButton.layer.masksToBounds = YES;
//        shareFileButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        shareFileButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        shareFileButton.layer.borderWidth = 0.5;
        
        [shareFileButton addTarget:self action:@selector(shareFileButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [self setButtonTitleColor:shareFileButton];
    }
    
    return shareFileButton;
}

#pragma mark - 留言
//
-(UIButton *)shareLeaveMsgButton
{
    if (!shareLeaveMsgButton) {
        
        CGRect frame = CGRectMake(mButtonMargin*4 + SHAREMORE_ITEMS_WIDTH * 3,
                                  SHAREMORE_ITEMS_TOP_MARGIN,
                                  SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_HEIGHT);
        
        shareLeaveMsgButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [shareLeaveMsgButton setFrame:frame];
//        [shareLeaveMsgButton setBackgroundImage:[UIImage imageNamed:@"sharemore_pic_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        [shareLeaveMsgButton setImage:[UIImage systemImageNamed:@"message.badge.circle"] forState:UIControlStateNormal];
        [shareLeaveMsgButton setImage:[UIImage systemImageNamed:@"message.badge.circle.fill"] forState:UIControlStateHighlighted];
        [shareLeaveMsgButton setTitle:@"留言" forState:UIControlStateNormal];
        [shareLeaveMsgButton setTitle:@"留言" forState:UIControlStateHighlighted];
        [[shareLeaveMsgButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
        [shareLeaveMsgButton setImageEdgeInsets:UIEdgeInsetsMake(0, 10, 15, -12)];
        [shareLeaveMsgButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -5, 0, 15)];
        //
        shareLeaveMsgButton.layer.cornerRadius = 5.0;
        shareLeaveMsgButton.layer.masksToBounds = YES;
//        shareLeaveMsgButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        shareLeaveMsgButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        shareLeaveMsgButton.layer.borderWidth = 0.5;
        
        [shareLeaveMsgButton addTarget:self action:@selector(shareLeaveMsgButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [self setButtonTitleColor:shareLeaveMsgButton];
    }
    
    return shareLeaveMsgButton;
}

#pragma mark - 换一行 - 服务评价

-(UIButton *)shareRateButton
{
    if (!shareRateButton) {
        
        CGRect frame = CGRectMake(mButtonMargin,
                                  SHAREMORE_ITEMS_TOP_MARGIN * 2 + SHAREMORE_ITEMS_HEIGHT,
                                  SHAREMORE_ITEMS_WIDTH,
                                  SHAREMORE_ITEMS_HEIGHT);
        
        shareRateButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [shareRateButton setFrame:frame];
        [shareRateButton setImage:[UIImage systemImageNamed:@"star.circle"] forState:UIControlStateNormal];
        [shareRateButton setImage:[UIImage systemImageNamed:@"star.circle.fill"] forState:UIControlStateHighlighted];
        [shareRateButton setTitle:@"评价" forState:UIControlStateNormal];
        [shareRateButton setTitle:@"评价" forState:UIControlStateHighlighted];
        [[shareRateButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
        [shareRateButton setImageEdgeInsets:UIEdgeInsetsMake(0, 10, 15, -12)];
        [shareRateButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -5, 0, 15)];
        
        //
        shareRateButton.layer.cornerRadius = 5.0;
        shareRateButton.layer.masksToBounds = YES;
//        shareRateButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        shareRateButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        shareRateButton.layer.borderWidth = 0.5;
        
        [shareRateButton addTarget:self action:@selector(shareRateButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [self setButtonTitleColor:shareRateButton];
    }
    
    return shareRateButton;
}

#pragma mark - 常见问题

//-(UIButton *)shareShowFAQButton
//{
//    if (!shareShowFAQButton) {
//
//        CGRect frame = CGRectMake(m_buttonMargin*2 + SHAREMORE_ITEMS_WIDTH,
//                                  SHAREMORE_ITEMS_TOP_MARGIN * 2 + SHAREMORE_ITEMS_HEIGHT,
//                                  SHAREMORE_ITEMS_WIDTH,
//                                  SHAREMORE_ITEMS_HEIGHT);
//
//        shareShowFAQButton = [UIButton buttonWithType:UIButtonTypeCustom];
//        [shareShowFAQButton setFrame:frame];
//        [shareShowFAQButton setImage:[UIImage systemImageNamed:@"questionmark.circle"] forState:UIControlStateNormal];
//        [shareShowFAQButton setImage:[UIImage systemImageNamed:@"questionmark.circle.fill"] forState:UIControlStateHighlighted];
//        [shareShowFAQButton setTitle:@"常见问题" forState:UIControlStateNormal];
//        [shareShowFAQButton setTitle:@"常见问题" forState:UIControlStateHighlighted];
//        [[shareShowFAQButton titleLabel] setAdjustsFontSizeToFitWidth:TRUE];
//        [shareShowFAQButton setImageEdgeInsets:UIEdgeInsetsMake(0, 15, 15, -12)];
//        [shareShowFAQButton setTitleEdgeInsets:UIEdgeInsetsMake(25, -15, 0, 5)];
//        //
//        shareShowFAQButton.layer.cornerRadius = 5.0;
//        shareShowFAQButton.layer.masksToBounds = YES;
//        shareShowFAQButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
//        shareShowFAQButton.layer.borderWidth = 0.5;
//
//        [shareShowFAQButton addTarget:self action:@selector(shareShowFAQButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
//        //
//        [self setButtonTitleColor:shareShowFAQButton];
//    }
//
//    return shareShowFAQButton;
//}


#pragma mark - Delegate

-(void)sharePickPhotoButtonPressed:(id)sender
{
    if (delegate && [delegate respondsToSelector:@selector(sharePickPhotoButtonPressed:)]) {
        [delegate performSelector:@selector(sharePickPhotoButtonPressed:) withObject:nil];
    }
}

-(void)shareTakePhotoButtonPressed:(id)sender
{
    if (delegate && [delegate respondsToSelector:@selector(shareTakePhotoButtonPressed:)]) {
        [delegate performSelector:@selector(shareTakePhotoButtonPressed:) withObject:nil];
    }
}

-(void)shareShowFAQButtonPressed:(id)sender
{
    if (delegate && [delegate respondsToSelector:@selector(shareShowFAQButtonPressed:)]) {
        [delegate performSelector:@selector(shareShowFAQButtonPressed:) withObject:nil];
    }
}

-(void)shareRateButtonPressed:(id)sender
{
    if (delegate && [delegate respondsToSelector:@selector(shareRateButtonPressed:)]) {
        [delegate performSelector:@selector(shareRateButtonPressed:) withObject:nil];
    }
}

-(void)shareFileButtonPressed:(id)sender {
    if (delegate && [delegate respondsToSelector:@selector(shareFileButtonPressed:)]) {
        [delegate performSelector:@selector(shareFileButtonPressed:) withObject:nil];
    }
}

-(void)shareLeaveMsgButtonPressed:(id)sender {
    if (delegate && [delegate respondsToSelector:@selector(shareLeaveMsgButtonPressed:)]) {
        [delegate performSelector:@selector(shareLeaveMsgButtonPressed:) withObject:nil];
    }
}


#pragma mark - HIDE

- (void)hideRateButton {
    [shareRateButton setHidden:true];
}

- (void)hideFAQButton {
//    [shareShowFAQButton setHidden:true];
}

#pragma mark - 切换机器人&人工

- (void)switchToRobot {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    self.shareLeaveMsgButton.frame = self.sharePickPhotoButton.frame;
    self.shareRateButton.frame = self.shareTakePhotoButton.frame;
//    self.shareShowFAQButton.frame = self.shareFileButton.frame;
    //
    self.sharePickPhotoButton.hidden = TRUE;
    self.shareTakePhotoButton.hidden = TRUE;
    self.shareFileButton.hidden = TRUE;
}

- (void)switchToAgent {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.sharePickPhotoButton.hidden = FALSE;
    self.shareTakePhotoButton.hidden = FALSE;
    self.shareFileButton.hidden = FALSE;
    
}

@end
