//
//  BDMsgQuestionnairViewCell.m
//  bytedesk-ui
//
//  Created by 萝卜丝 on 2019/2/20.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDMsgQuestionnairViewCell.h"

#import "BDUIConstants.h"

#import "BDM80AttributedLabel.h"
#import "BDM80AttributedLabel+BDUI.h"

#import "BDInputEmotionManager.h"
#import "BDInputEmotionParser.h"

@interface BDMsgQuestionnairViewCell()<M80AttributedLabelDelegate>

@end

@implementation BDMsgQuestionnairViewCell


- (instancetype)initMessageContentView
{
    if (self = [super initMessageContentView]) {
        _textLabel = [[BDM80AttributedLabel alloc] initWithFrame:CGRectZero];
        _textLabel.delegate = self;
        _textLabel.numberOfLines = 0;
//        _textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _textLabel.backgroundColor = [UIColor clearColor];
        _textLabel.font = [UIFont systemFontOfSize:12.0f];
        [self addSubview:_textLabel];
    }
    return self;
}

//- (BOOL)canBecomeFirstResponder {
//    return YES;
//}

- (void)initWithMessageModel:(BDMessageModel *)data{
    [super initWithMessageModel:data];
    //    NSLog(@"%s, type: %@, content: %@", __PRETTY_FUNCTION__, self.model.type, self.model.content);
    
    NSString *text = self.model.content;
    //    [_textLabel bdui_setText:text];
    
    // TODO: 放在bdui_setText函数中表情无法显示，暂时复制代码到此处，bug未知？？
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
    
    UIEdgeInsets contentInsets = self.model.contentViewInsets;
    
    CGSize size = [_textLabel sizeThatFits:CGSizeMake(200, CGFLOAT_MAX)];
    self.model.contentSize = size;
    
    CGRect labelFrame = CGRectZero;
    CGRect bubbleFrame = CGRectZero;
    CGRect boundsFrame = CGRectZero;
    
    if ([self.model isSend]) {
        labelFrame = CGRectMake(contentInsets.left+2, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 5, contentInsets.top + size.height + contentInsets.bottom );
        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width - 55, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
    }
    else {
        labelFrame = CGRectMake(contentInsets.left+3, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 5, contentInsets.top + size.height + contentInsets.bottom );
        boundsFrame = CGRectMake(50, 40, bubbleFrame.size.width, bubbleFrame.size.height);
    }
    self.frame = boundsFrame;
    
    self.textLabel.frame = labelFrame;
    self.bubbleView.frame = bubbleFrame;
    self.model.contentSize = boundsFrame.size;
}


#pragma mark - M80AttributedLabelDelegate

- (void)m80AttributedLabel:(BDM80AttributedLabel *)label clickedOnLink:(id)linkData{
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
}

@end
