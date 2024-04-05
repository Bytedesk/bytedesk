//
//  BDM80AttributedLabel+FeedBack.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/24.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDM80AttributedLabel+BDUI.h"
#import "BDInputEmotionManager.h"
#import "BDInputEmotionParser.h"

@implementation BDM80AttributedLabel (BDUI)

- (void)bdui_setText:(NSString *)text {
    
    [self setText:@""];
    NSArray *tokens = [[BDInputEmotionParser currentParser] tokens:text];
    for (BDInputTextToken *token in tokens) {
        if (token.type == BDInputTokenTypeEmoticon) {
            BDInputEmotion *emoticon = [[BDInputEmotionManager sharedManager] emotionByText:token.text];
            if (emoticon) {
                UIImage *image = [UIImage imageNamed:emoticon.filename inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil];
                if (image) {
                    [self appendImage:image
                                    maxSize:CGSizeMake(18, 18)
                                     margin:UIEdgeInsetsZero
                                  alignment:M80ImageAlignmentCenter];
                }
                else {
                    NSString *text = token.text;
                    [self appendText:text];
                }
            }
        }
        else {
            NSString *text = token.text;
            [self appendText:text];
        }
    }
}

@end
