//
//  BDInputEmotionParser.h
//  feedback
//
//  Created by 萝卜丝 on 2018/2/22.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum : NSUInteger {
    BDInputTokenTypeText,
    BDInputTokenTypeEmoticon,
} KFDSInputTokenType;

@interface BDInputTextToken : NSObject

@property (nonatomic,copy)      NSString    *text;
@property (nonatomic,assign)    KFDSInputTokenType   type;

@end


@interface BDInputEmotionParser : NSObject

+ (instancetype)currentParser;
- (NSArray *)tokens:(NSString *)text;

@end
