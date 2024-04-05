//
//  KFUIUtils.m
//  bytedesk-ui
//
//  Created by 宁金鹏 on 2019/4/11.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDUIUtils.h"
#import "BDUIConstants.h"
//#import "BDCoreTextView.h"
#import <UIKit/UIKit.h>

#define CONTENT_TOP_MARGIN        2.0f
#define CONTENT_LEFT_MARGIN       15.0f
#define CONTENT_BOTTOM_MARGIN     30.0f
#define CONTENT_RIGHT_MARGIN      20.0f

@implementation BDUIUtils

+ (NSArray *)documentTypes {
    
    return @[@"public.content",
           @"public.text",
//           @"public.source-code",
//           @"public.image",
//           @"public.audiovisual-content",
           @"com.adobe.pdf",
           @"com.apple.keynote.key",
           @"com.microsoft.word.doc",
           @"com.microsoft.excel.xls",
           @"com.microsoft.powerpoint.ppt"
//           @"public.rtf",
//           @"public.html"
             ];
}

+ (CGSize)sizeOfRobotContent:(NSString *)msgContent {
    //
    UITextView *kfCoreTextView = [[UITextView alloc] initWithFrame:CGRectZero];
    kfCoreTextView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    kfCoreTextView.userInteractionEnabled = YES;
    kfCoreTextView.scrollEnabled = NO;
    //
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithData:[msgContent dataUsingEncoding:NSUnicodeStringEncoding] options:@{ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType } documentAttributes:nil error:nil];
    // NSMutableAttributedString
    [attributedString addAttributes:@{NSFontAttributeName: [UIFont systemFontOfSize:16]} range:NSMakeRange(0, attributedString.length)];
    [kfCoreTextView setAttributedText:attributedString];
//    [kfCoreTextView setText:msgContent];
    //
    int width = BDScreen.width * 3 / 5;
    [kfCoreTextView setFrame:CGRectMake(0, 0, width, FLT_MAX)];
    [kfCoreTextView sizeToFit];
    if (kfCoreTextView.frame.size.height < 40) {
        CGRect frame = kfCoreTextView.frame;
        frame.size.height = 40;
        [kfCoreTextView setFrame:frame];
    }
    return kfCoreTextView.frame.size;
    
//    CGSize size = [msgContent boundingRectWithSize:CGSizeMake(200, FLT_MAX)
//                                           options:NSStringDrawingUsesLineFragmentOrigin
//                                        attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
//                                           context:nil].size;
//
//    BDCoreTextView *kfCoreTextView = [[BDCoreTextView alloc] init];
//    kfCoreTextView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
//    [kfCoreTextView setText:msgContent];
//
//    CGRect frame = CGRectMake(CONTENT_LEFT_MARGIN + 5,
//                              CONTENT_TOP_MARGIN + 8,
//                              size.width,
//                              size.height + 17);
//    kfCoreTextView.autoresizingMask = UIViewAutoresizingFlexibleRightMargin;
//    kfCoreTextView.frame = frame;
//    [kfCoreTextView fitToSuggestedHeight];
//
//    return kfCoreTextView.frame.size;
}


+ (CGSize)sizeOfRobotContentAttr:(NSAttributedString *)msgContent {
    //
    UITextView *kfCoreTextView = [[UITextView alloc] initWithFrame:CGRectZero];
    kfCoreTextView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    kfCoreTextView.userInteractionEnabled = YES;
    kfCoreTextView.scrollEnabled = NO;
    //
    [kfCoreTextView setAttributedText:msgContent];
    //
    int width = BDScreen.width * 3 / 5;
    [kfCoreTextView setFrame:CGRectMake(0, 0, width, FLT_MAX)];
    [kfCoreTextView sizeToFit];
    if (kfCoreTextView.frame.size.height < 40) {
        CGRect frame = kfCoreTextView.frame;
        frame.size.height = 40;
        [kfCoreTextView setFrame:frame];
    }
    return kfCoreTextView.frame.size;
}

@end
