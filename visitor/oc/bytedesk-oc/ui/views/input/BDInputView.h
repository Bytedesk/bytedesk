//
//  KFInputView.h
//  ChatViewController
//
//  Created by jack on 14-4-29.
//  Copyright (c) 2014年 appkefu.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol BDInputViewDelegate <NSObject>

-(void)switchVoiceButtonPressed:(id)sender;         //
-(void)switchAgentButtonPressed:(id)sender;         //
-(void)switchEmotionButtonPressed:(id)sender;       //
-(void)switchPlusButtonPressed:(id)sender;          //
-(void)sendMessage:(NSString *)content;             //
//-(void)textDidChange:(NSString *)content;
//
-(void)recordVoiceButtonTouchDown:(id)sender;       //
-(void)recordVoiceButtonTouchUpInside:(id)sender;   //
-(void)recordVoiceButtonTouchUpOutside:(id)sender;  //
-(void)recordVoiceButtonTouchDragInside:(id)sender; //
-(void)recordVoiceButtonTouchDragOutside:(id)sender;//

@end


@interface BDInputView : UIView<UITextViewDelegate>

@property (nonatomic, weak) id<BDInputViewDelegate> delegate;            //

//
@property (nonatomic, strong) UIToolbar          *inputToolbar;          //输入框主View

@property (nonatomic, strong) UIButton           *switchVoiceButton;     //
@property (nonatomic, strong) UIButton           *switchAgentButton;     // 转人工
@property (nonatomic, strong) UIButton           *recordVoiceButton;     //
@property (nonatomic, strong) UITextView         *inputTextView;         //文本输入框
@property (nonatomic, strong) UIButton           *switchEmotionButton;   //切换表情
@property (nonatomic, strong) UIButton           *switchPlusButton;      //切换扩展

@property (nonatomic, assign) CGFloat            inputTextViewMaxHeight;     //
@property (nonatomic, assign) CGFloat            inputTextViewMaxLinesCount; //

- (void)switchToRobot;

- (void)switchToAgent;

@end
