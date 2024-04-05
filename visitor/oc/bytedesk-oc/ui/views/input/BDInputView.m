//
//  KFInputView.m
//  ChatViewController
//
//  Created by jack on 14-4-29.
//  Copyright (c) 2014年 appkefu.com. All rights reserved.
//

#import "BDInputView.h"

#define INPUTBAR_HEIGHT 60.0f
#define INPUTBAR_MAX_HEIGHT 200.0f
#define INPUTBAR_TOP_MARGIN 10.0f

#define INPUTBAR_SHOWMENU_BUTTON_WIDTH_HEIGHT 45.0f

#define INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT 36.0f

#define INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN INPUTBAR_TOP_MARGIN
#define INPUTBAR_SWITCH_EMOTION_LEFT_MARGIN 5.0f
#define INPUTBAR_SWITCH_EMOTION_RIGHT_MARGIN 3.0f
#define INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN 6.0f

#define INPUTBAR_SWITCH_VOICE_LEFT_MARGIN 8.0f
#define INPUTBAR_SWITCH_VOICE_TOP_MARGIN  INPUTBAR_TOP_MARGIN
#define INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT 36.0f

#define INPUTBAR_SWITCH_AGENT_LEFT_MARGIN 8.0f
#define INPUTBAR_SWITCH_AGENT_TOP_MARGIN  INPUTBAR_TOP_MARGIN
#define INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT 36.0f

#define INPUTBAR_INPUT_TEXTVIEW_TOP_MARGIN INPUTBAR_TOP_MARGIN
#define INPUTBAR_INPUT_TEXTVIEW_LEFT_MARGIN 5.0f

#define INPUTBAR_INPUT_TEXTVIEW_HEIGHT 34.5f
#define INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT 188.0f

#define INPUTBAR_RECORD_VOICE_BUTTON_TOP_MARGIN INPUTBAR_TOP_MARGIN
#define INPUTBAR_RECORD_VOICE_BUTTON_LEFT_MARGIN 5.0f
#define INPUTBAR_RECORD_VOICE_HEIGHT   34.5f

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

CGFloat const kFontSize = 17.0f;

@interface BDInputView ()

@property (nonatomic, assign) CGFloat   inputTextViewPreviousTextHeight;

@end


@implementation BDInputView

@synthesize inputToolbar,
            switchVoiceButton,
            switchAgentButton,
            recordVoiceButton,
            inputTextView,
            switchEmotionButton,
            switchPlusButton,

            inputTextViewMaxHeight,
            inputTextViewMaxLinesCount,
            inputTextViewPreviousTextHeight,

            delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self setUpViews];
//        self.backgroundColor = [UIColor yellowColor];
        
    }
    return self;
}

- (BOOL)becomeFirstResponder {
    return [[self inputTextView] becomeFirstResponder];
}

- (BOOL)canBecomeFirstResponder {
    return [[self inputTextView] canBecomeFirstResponder];
}

- (BOOL)isFirstResponder {
    return [[self inputTextView] isFirstResponder];
}

- (BOOL)resignFirstResponder {
    return [[self inputTextView] resignFirstResponder];
}

#pragma mark Widgets Initialization
-(void)setUpViews {
    self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleWidth;
    
    if (inputToolbar != nil) {
        [inputToolbar removeFromSuperview];
        inputToolbar = nil;
    }
    
    if (switchVoiceButton != nil) {
        [switchVoiceButton removeFromSuperview];
        switchVoiceButton = nil;
    }
    
    if (switchAgentButton != nil) {
        [switchAgentButton removeFromSuperview];
        switchAgentButton = nil;
    }
    
    if (inputTextView != nil) {
        [inputTextView removeFromSuperview];
        inputTextView = nil;
    }
    
    if (recordVoiceButton != nil) {
        [recordVoiceButton removeFromSuperview];
        recordVoiceButton = nil;
    }
    
    if (switchEmotionButton != nil) {
        [switchEmotionButton removeFromSuperview];
        switchEmotionButton = nil;
    }
    
    if (switchPlusButton != nil) {
        [switchPlusButton removeFromSuperview];
        switchPlusButton = nil;
    }
    
    [self addSubview:[self inputToolbar]];
    [self addSubview:[self switchVoiceButton]];
    [self addSubview:[self switchAgentButton]];
    self.switchAgentButton.hidden = TRUE;
    [self addSubview:[self inputTextView]];
    [self addSubview:[self recordVoiceButton]];
    self.recordVoiceButton.hidden = TRUE;
    [self addSubview:[self switchEmotionButton]];
    [self addSubview:[self switchPlusButton]];
}


-(UIToolbar *)inputToolbar
{
    if (!inputToolbar) {
        
        CGRect frame = [self bounds];
        frame.origin.y = 0.5f;
        frame.size.height = INPUTBAR_HEIGHT;
        inputToolbar = [[UIToolbar alloc] initWithFrame:frame];
        [inputToolbar setBarStyle:UIBarStyleDefault];
        [inputToolbar setTranslucent:FALSE];
//        [inputToolbar setTintColor:[UIColor whiteColor]];
        inputToolbar.tintColor = UIColorFromRGB(0XEBEBEB);
        [inputToolbar setAutoresizingMask:UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleTopMargin];
    }
    
    return inputToolbar;
}

-(UIButton *)switchVoiceButton
{
    if (!switchVoiceButton) {
        
        switchVoiceButton = [[UIButton alloc] initWithFrame:CGRectMake(INPUTBAR_SWITCH_VOICE_LEFT_MARGIN,
                                                                       INPUTBAR_SWITCH_VOICE_TOP_MARGIN,
                                                                       INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT,
                                                                       INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT)];
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
        [switchVoiceButton addTarget:self action:@selector(handleSwitchVoiceButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        // TODO: 暂时隐藏，待录音完善之后，再发布
        [switchVoiceButton setHidden:YES];
    }
    
    return switchVoiceButton;
}

-(UIButton *)switchAgentButton {
    if (!switchAgentButton) {
        
        switchAgentButton = [[UIButton alloc] initWithFrame:CGRectMake(INPUTBAR_SWITCH_AGENT_LEFT_MARGIN,
                                                                       INPUTBAR_SWITCH_AGENT_TOP_MARGIN,
                                                                       INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT,
                                                                       INPUTBAR_SWITCH_AGENT_BUTTON_WIDTH_HEIGHT)];
        [[self switchAgentButton] setImage:[UIImage systemImageNamed:@"person.and.arrow.left.and.arrow.right"] forState:UIControlStateNormal];
        [[self switchAgentButton] setImage:[UIImage systemImageNamed:@"person.fill.and.arrow.left.and.arrow.right"] forState:UIControlStateHighlighted];
        [[self switchAgentButton] setTitle:@"转人工" forState:UIControlStateNormal];
        [[self switchAgentButton] setTitle:@"转人工" forState:UIControlStateHighlighted];
        [[[self switchAgentButton] titleLabel] setAdjustsFontSizeToFitWidth:YES];
        [[self switchAgentButton] setImageEdgeInsets:UIEdgeInsetsMake(2, 10, 10, -10)];
        [[self switchAgentButton] setTitleEdgeInsets:UIEdgeInsetsMake(15, -13, -5, 0)];
        //
        [switchAgentButton addTarget:self action:@selector(handleSwitchAgentButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
//        [self.switchAgentButton setTintColor:[UIColor orangeColor]];
        [self setButtonTitleColor:self.switchAgentButton];
    }
    return switchAgentButton;
}

-(UITextView *)inputTextView
{
    if (!inputTextView) {
        
        CGRect frame = CGRectMake( (INPUTBAR_SWITCH_VOICE_LEFT_MARGIN)
                                  + INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT + INPUTBAR_INPUT_TEXTVIEW_LEFT_MARGIN * 1.5,
                                  INPUTBAR_INPUT_TEXTVIEW_TOP_MARGIN,
                                  self.bounds.size.width - INPUTBAR_SWITCH_VOICE_BUTTON_WIDTH_HEIGHT - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT*2,
                                  INPUTBAR_INPUT_TEXTVIEW_HEIGHT);
            
        inputTextView = [[UITextView alloc] initWithFrame:frame];
        inputTextView.scrollIndicatorInsets = UIEdgeInsetsMake(10.0f, 0.0f, 10.0f, 8.0f);
        inputTextView.contentInset = UIEdgeInsetsMake(0.0f, 0.0f, 0.0f, 0.0f);
        inputTextView.scrollEnabled = YES;
        inputTextView.scrollsToTop = NO;
        inputTextView.userInteractionEnabled = YES;
//        inputTextView.textColor = [UIColor blackColor];
        inputTextView.keyboardAppearance = UIKeyboardAppearanceDefault;
        inputTextView.keyboardType = UIKeyboardTypeDefault;
        inputTextView.returnKeyType = UIReturnKeySend;
        inputTextView.font = [UIFont systemFontOfSize:kFontSize];
        
        inputTextView.layer.cornerRadius = 5.0f;
        inputTextView.layer.borderWidth = 0.7f;
        inputTextView.layer.borderColor = [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        inputTextView.delegate = self;
        [inputTextView setAutoresizingMask:UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleWidth];
    }
    
    return inputTextView;
}

-(UIButton *)recordVoiceButton
{
    if (!recordVoiceButton) {
        
        CGRect frame = [self inputTextView].frame;
        frame.origin.x -= 5.0f;
        frame.origin.y -= 1.0f;
        frame.size.width -= 40.0f;
//        frame.size.height += 2.0f;
        
        recordVoiceButton = [[UIButton alloc] initWithFrame:frame];
        [recordVoiceButton setAutoresizingMask:UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleWidth];
        [[recordVoiceButton titleLabel] setFont:[UIFont systemFontOfSize:14.0f]];
        [recordVoiceButton setTitle:@"按住 说话" forState:UIControlStateNormal];
        [recordVoiceButton setTitle:@"松开 取消" forState:UIControlStateHighlighted];
        recordVoiceButton.layer.cornerRadius = 5.0;
        recordVoiceButton.layer.masksToBounds = YES;
        recordVoiceButton.layer.borderColor = [UIColor systemGrayColor].CGColor;
        recordVoiceButton.layer.borderWidth = 0.5;
        //
        [recordVoiceButton addTarget:self action:@selector(recordVoiceButtonTouchDown:) forControlEvents:UIControlEventTouchDown];
        [recordVoiceButton addTarget:self action:@selector(recordVoiceButtonTouchUpInside:) forControlEvents:UIControlEventTouchUpInside];
        [recordVoiceButton addTarget:self action:@selector(recordVoiceButtonTouchUpOutside:) forControlEvents:UIControlEventTouchUpOutside];
        [recordVoiceButton addTarget:self action:@selector(recordVoiceButtonTouchDragInside:) forControlEvents:UIControlEventTouchDragInside];
        [recordVoiceButton addTarget:self action:@selector(recordVoiceButtonTouchDragOutside:) forControlEvents:UIControlEventTouchDragOutside];
        //
        [self setButtonTitleColor:self.recordVoiceButton];
    }
    return recordVoiceButton;
}

- (void)setButtonTitleColor:(UIButton *)button {
    UIUserInterfaceStyle mode = UITraitCollection.currentTraitCollection.userInterfaceStyle;
    if (mode == UIUserInterfaceStyleDark) { // 深色模式
        [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal]; // NSLog(@"");
    } else if (mode == UIUserInterfaceStyleLight) { // 浅色模式
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal]; // 浅色模式
    }
}

-(UIButton *)switchEmotionButton
{
    if (!switchEmotionButton) {
        
        switchEmotionButton = [[UIButton alloc] initWithFrame:
                               CGRectMake(self.bounds.size.width - INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT * 2 - INPUTBAR_SWITCH_EMOTION_RIGHT_MARGIN * 2,
                                          INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN,
                                          INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT,
                                          INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT)];

        [switchEmotionButton setAutoresizingMask:UIViewAutoresizingFlexibleTopMargin|UIViewAutoresizingFlexibleLeftMargin];
        [switchEmotionButton addTarget:self action:@selector(handleSwitchEmotionButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [switchEmotionButton setImage:[UIImage systemImageNamed:@"face.smiling" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [switchEmotionButton setImage:[UIImage systemImageNamed:@"face.smiling.inverse" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    }
    
    return switchEmotionButton;
}

-(UIButton *)switchPlusButton
{
    if (!switchPlusButton) {
        
        switchPlusButton = [[UIButton alloc] initWithFrame:
                            CGRectMake(self.bounds.size.width - INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT - INPUTBAR_SWITCH_PLUS_RIGHT_MARGIN * 2,
                                                                      INPUTBAR_SWITCH_EMOTION_PLUS_TOP_MARGIN,
                                                                      INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT,
                                                                      INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT)];
        //
        [switchPlusButton setAutoresizingMask:UIViewAutoresizingFlexibleLeftMargin|UIViewAutoresizingFlexibleTopMargin];
        [switchPlusButton addTarget:self action:@selector(handleSwitchPlusButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        //
        [switchPlusButton setImage:[UIImage systemImageNamed:@"plus.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [switchPlusButton setImage:[UIImage systemImageNamed:@"plus.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
        //
//        [switchPlusButton setTintColor:[UIColor orangeColor]];
    }
    
    return switchPlusButton;
}

-(CGFloat)inputTextViewMaxHeight {
    if (!inputTextViewMaxHeight) {
        inputTextViewMaxHeight = 100.0f;
    }
    return inputTextViewMaxHeight;
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


#pragma mark UIButton Selectors


-(void)handleSwitchVoiceButtonPressed:(id)sender {

    if ([delegate respondsToSelector:@selector(switchVoiceButtonPressed:)]) {
        [delegate performSelector:@selector(switchVoiceButtonPressed:) withObject:nil];
    }
    
    if ([[self inputTextView] isFirstResponder]) {
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    } else {
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"keyboard"] forState:UIControlStateNormal];
        [[self switchVoiceButton] setImage:[UIImage systemImageNamed:@"keyboard.fill"] forState:UIControlStateHighlighted];
        ////
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"face.smiling" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"face.smiling.inverse" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    }
}

-(void)handleSwitchAgentButtonPressed:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    if ([delegate respondsToSelector:@selector(switchAgentButtonPressed:)]) {
        [delegate performSelector:@selector(switchAgentButtonPressed:) withObject:nil];
    }
}

-(void)handleSwitchEmotionButtonPressed:(id)sender {
    //执行Delegate
    if ([delegate respondsToSelector:@selector(switchEmotionButtonPressed:)]) {
        [delegate performSelector:@selector(switchEmotionButtonPressed:) withObject:nil];
    }
    
    if ([[self inputTextView] isFirstResponder]) {
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"face.smiling" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"face.smiling.inverse" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    } else {
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"keyboard"] forState:UIControlStateNormal];
        [[self switchEmotionButton] setImage:[UIImage systemImageNamed:@"keyboard.fill"] forState:UIControlStateHighlighted];
    }
}

-(void)handleSwitchPlusButtonPressed:(id)sender {
    if ([delegate respondsToSelector:@selector(switchPlusButtonPressed:)]) {
        [delegate performSelector:@selector(switchPlusButtonPressed:) withObject:nil];
    }
}


//
-(void)recordVoiceButtonTouchDown:(id)sender {
    if ([delegate respondsToSelector:@selector(recordVoiceButtonTouchDown:)]) {
        [delegate performSelector:@selector(recordVoiceButtonTouchDown:) withObject:nil];
    }
}


-(void)recordVoiceButtonTouchUpInside:(id)sender {
    if ([delegate respondsToSelector:@selector(recordVoiceButtonTouchUpInside:)]) {
        [delegate performSelector:@selector(recordVoiceButtonTouchUpInside:) withObject:nil];
    }
}


-(void)recordVoiceButtonTouchUpOutside:(id)sender {
    if ([delegate respondsToSelector:@selector(recordVoiceButtonTouchUpOutside:)]) {
        [delegate performSelector:@selector(recordVoiceButtonTouchUpOutside:) withObject:nil];
    }
}


-(void)recordVoiceButtonTouchDragInside:(id)sender {
    if ([delegate respondsToSelector:@selector(recordVoiceButtonTouchDragInside:)]) {
        [delegate performSelector:@selector(recordVoiceButtonTouchDragInside:) withObject:nil];
    }
}

-(void)recordVoiceButtonTouchDragOutside:(id)sender {
    if ([delegate respondsToSelector:@selector(recordVoiceButtonTouchUpOutside:)]) {
        [delegate performSelector:@selector(recordVoiceButtonTouchDragOutside:) withObject:nil];
    }
}

#pragma mark UITextViewDelegate

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    if(![textView hasText] && [text isEqualToString:@""]) {
        return NO;
    }
	if ([text isEqualToString:@"\n"]) {
        NSString *content = [textView.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([content length] == 0) {
            return NO;
        }
        if ([delegate respondsToSelector:@selector(sendMessage:)]) {
            [delegate performSelector:@selector(sendMessage:) withObject:content];
        }
        //
        [self textViewDidChange:textView];
        return NO;
	}
    
	return YES;
}

-(void)textViewDidChange:(UITextView *)textView {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    CGFloat previewHeight = [self inputTextViewPreviousTextHeight];
    CGFloat textViewHeight = [self inputTextViewHeight];
    if (textViewHeight > 150) {
        return;
    }
    CGFloat deltaHeight = textViewHeight - previewHeight;
    inputTextViewPreviousTextHeight = textViewHeight;
    
    [UIView animateWithDuration:0.20
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
                         
                         CGRect barFrame = [[self inputToolbar] frame];
                         
                         barFrame.size.height += deltaHeight;
                         if (barFrame.size.height < INPUTBAR_HEIGHT)
                         {
                             barFrame.size.height = INPUTBAR_HEIGHT;
                             
                             barFrame.origin.y = [self switchVoiceButton].frame.origin.y - INPUTBAR_SWITCH_VOICE_TOP_MARGIN;
                         }
                         else if (barFrame.size.height > INPUTBAR_MAX_HEIGHT)
                         {
                             barFrame.size.height = INPUTBAR_MAX_HEIGHT;
                             
                             barFrame.origin.y = [self switchVoiceButton].frame.origin.y - 161.0f;
                         }
                         else if (previewHeight == textViewHeight
                                  && textViewHeight > INPUTBAR_MAX_HEIGHT
                                  && deltaHeight == 0.0)
                         {
                             barFrame.size.height = INPUTBAR_MAX_HEIGHT;
                             
                             barFrame.origin.y = [self switchVoiceButton].frame.origin.y - 161.0f;
                         }
                         else
                         {
                             barFrame.origin.y -= deltaHeight;
                         }
                         [[self inputToolbar] setFrame:barFrame];
                         
                         ///////////////////////////////////////////////////////////////////////
                         
                         CGRect textViewFrame = [[self inputTextView] frame];
                         textViewFrame.size.height += deltaHeight;
                         
                         if (textViewFrame.size.height < INPUTBAR_INPUT_TEXTVIEW_HEIGHT)
                         {
                             textViewFrame.size.height = INPUTBAR_INPUT_TEXTVIEW_HEIGHT;
                             
                             textViewFrame.origin.y = [self recordVoiceButton].frame.origin.y + 1;
                             
                         }
                         else if(textViewFrame.size.height > INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT)
                         {
                             textViewFrame.size.height = INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT;
                             
                             textViewFrame.origin.y = [self recordVoiceButton].frame.origin.y - 155.0f;
                         }
                         else if (previewHeight == textViewHeight
                                  && textViewHeight > INPUTBAR_MAX_HEIGHT
                                  && deltaHeight == 0.0)
                         {
                             textViewFrame.size.height = INPUTBAR_INPUT_TEXTVIEW_MAX_HEIGHT;
                             
                             textViewFrame.origin.y = [self recordVoiceButton].frame.origin.y - 155.0f;
                         }
                         else
                         {
                             textViewFrame.origin.y -= deltaHeight;
                         }
                         [[self inputTextView] setFrame:textViewFrame];

                         ///////////////////////////////////////////////////////////////////////

        
    } completion:^(BOOL finished) {
        
    }];
}


-(CGFloat)inputTextViewPreviousTextHeight {
    
    if (!inputTextViewPreviousTextHeight) {
        inputTextViewPreviousTextHeight = [self inputTextViewHeight];
    }
    return inputTextViewPreviousTextHeight;
}

-(CGFloat) inputTextViewHeight {
    
    CGFloat height = [inputTextView sizeThatFits:CGSizeMake([inputTextView frame].size.width, FLT_MAX)].height;
    
    return ceilf(height);
}


#pragma mark - 切换机器人&人工

- (void)switchToRobot {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.switchVoiceButton.hidden = TRUE;
    self.switchAgentButton.hidden = FALSE;
    self.switchEmotionButton.hidden = TRUE;
    CGRect textFrame = self.inputTextView.frame;
    textFrame.origin.x += 10;
    textFrame.size.width += INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT - 8;
    self.inputTextView.frame = textFrame;
}

- (void)switchToAgent {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    self.switchVoiceButton.hidden = FALSE;
    self.switchAgentButton.hidden = TRUE;
    self.switchEmotionButton.hidden = FALSE;
    CGRect textFrame = self.inputTextView.frame;
    textFrame.size.width -= INPUTBAR_SWITCH_EMOTION_PLUS_BUTTON_WIDTH_HEIGHT + 8;
    self.inputTextView.frame = textFrame;
}

@end
