//
//  KFDSMsgTextContentView.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDMsgTextContentView.h"
#import "BDUIConstants.h"

#import "BDM80AttributedLabel.h"
#import "BDM80AttributedLabel+BDUI.h"

#import "BDInputEmotionManager.h"
#import "BDInputEmotionParser.h"

#import "BDMessageModel.h"

@interface BDMsgTextContentView()<M80AttributedLabelDelegate>

@end

@implementation BDMsgTextContentView

- (instancetype)initMessageContentView {
    //
    if (self = [super initMessageContentView]) {
        //
        _textLabel = [[BDM80AttributedLabel alloc] initWithFrame:CGRectZero];
        _textLabel.delegate = self;
        _textLabel.underLineForLink = YES;
//        _textLabel.numberOfLines = 0;
//        _textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _textLabel.backgroundColor = [UIColor clearColor];
        _textLabel.font = [UIFont systemFontOfSize:16.0f];
        //
        [self addSubview:_textLabel];
    }
    //
    return self;
}

//- (BOOL)canBecomeFirstResponder {
//    return YES;
//}

- (void)initWithMessageModel:(BDMessageModel *)data{
    [super initWithMessageModel:data];
//    NSLog(@"%s, type: %@, content: %@", __PRETTY_FUNCTION__, self.model.type, self.model.content);

    NSString *text = self.model.content;
    // FIXME: 放在bdui_setText函数中表情无法显示，暂时复制代码到此处，bug未知？？
//   [_textLabel bdui_setText:text];
    
    [_textLabel setText:@""];
    NSArray *tokens = [[BDInputEmotionParser currentParser] tokens:text];
    for (BDInputTextToken *token in tokens) {
        if (token.type == BDInputTokenTypeEmoticon) {
            BDInputEmotion *emoticon = [[BDInputEmotionManager sharedManager] emotionByText:token.text];
            if (emoticon) {
                UIImage *image = [UIImage imageNamed:emoticon.filename inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil];
                if (image) {
                    [_textLabel appendImage:image
                              maxSize:CGSizeMake(18, 18)
                               margin:UIEdgeInsetsZero
                            alignment:M80ImageAlignmentCenter];
                }
                else {
                    NSString *text = token.text;
                    [_textLabel appendText:text];
                }
            }
        }
        else {
            NSString *text = token.text;
            [_textLabel appendText:text];
        }
    }
    [self setNeedsLayout];
}

- (void)layoutSubviews{
    [super layoutSubviews];
//    NSLog(@"%s", __PRETTY_FUNCTION__);
   
    UIEdgeInsets contentViewInsets = self.model.contentViewInsets;
//    CGSize contentSize = self.messageModel.contentSize()
    CGSize contentSize = [_textLabel sizeThatFits:CGSizeMake(200, CGFLOAT_MAX)];
    self.model.contentSize = contentSize;
    
    CGFloat width = contentViewInsets.left + contentSize.width + contentViewInsets.right;
    CGFloat height = contentViewInsets.top + contentSize.height + contentViewInsets.bottom;

    CGRect textContentFrame = CGRectZero;
    CGRect bubbleFrame = CGRectZero;
    CGRect backFrame = CGRectZero;

    if ([self.model isSend]) {
        textContentFrame = CGRectMake(contentViewInsets.left, contentViewInsets.top, width, height);
        bubbleFrame = CGRectMake(0, 0, contentViewInsets.left + contentSize.width + contentViewInsets.right + 5, contentViewInsets.top + contentSize.height + contentViewInsets.bottom );
        backFrame = CGRectMake(BDScreen.width - width - self.xMargin, self.yTop, bubbleFrame.size.width,  bubbleFrame.size.height);
    }
    else {
        textContentFrame = CGRectMake(contentViewInsets.left, contentViewInsets.top, width, height);
        bubbleFrame = CGRectMake(0, 0, contentViewInsets.left + contentSize.width + contentViewInsets.right + 5, contentViewInsets.top + contentSize.height + contentViewInsets.bottom );
        backFrame = CGRectMake(self.xMargin, self.yTop, bubbleFrame.size.width, bubbleFrame.size.height);
    }
    //
    self.textLabel.frame = textContentFrame;
    self.bubbleView.frame = bubbleFrame;
    self.frame = backFrame;
    //
    self.model.contentSize = backFrame.size;
}

#pragma mark - M80AttributedLabelDelegate

// FIXME: 没有调用？
- (void)m80AttributedLabel:(BDM80AttributedLabel *)label clickedOnLink:(id)linkData {
    //
    NSString *url = [NSString stringWithFormat:@"%@", linkData];
    NSLog(@"%s url:%@", __PRETTY_FUNCTION__, url);
    //
    if ([self.delegate respondsToSelector:@selector(linkUrlClicked:)]) {
        [self.delegate linkUrlClicked:url];
    }
}

//- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
//    NSLog(@"touch text view %s", __PRETTY_FUNCTION__);
//}

@end
