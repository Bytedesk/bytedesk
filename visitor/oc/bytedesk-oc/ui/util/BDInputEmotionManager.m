//
//  BDInputEmotionManager.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/24.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDInputEmotionManager.h"

@implementation BDInputEmotion
@end

@implementation BDInputEmotionCatalog
@end

@implementation BDInputEmotionLayout

- (id)initEmojiLayout:(CGFloat)width
{
    self = [super init];
    if (self)
    {
//        _rows            = KFDSKit_EmojRows;
//        _columes         = ((width - KFDSKit_EmojiLeftMargin - KFDSKit_EmojiRightMargin) / KFDSKit_EmojImageWidth);
//        _itemCountInPage = _rows * _columes -1;
//        _cellWidth       = (width - KFDSKit_EmojiLeftMargin - KFDSKit_EmojiRightMargin) / _columes;
//        _cellHeight      = KFDSKit_EmojCellHeight;
//        _imageWidth      = KFDSKit_EmojImageWidth;
//        _imageHeight     = KFDSKit_EmojImageHeight;
//        _emoji           = YES;
    }
    return self;
}

- (id)initCharletLayout:(CGFloat)width{
    self = [super init];
    if (self)
    {
//        _rows            = KFDSKit_PicRows;
//        _columes         = ((width - KFDSKit_EmojiLeftMargin - KFDSKit_EmojiRightMargin) / KFDSKit_PicImageWidth);
//        _itemCountInPage = _rows * _columes;
//        _cellWidth       = (width - KFDSKit_EmojiLeftMargin - KFDSKit_EmojiRightMargin) / _columes;
//        _cellHeight      = KFDSKit_PicCellHeight;
//        _imageWidth      = KFDSKit_PicImageWidth;
//        _imageHeight     = KFDSKit_PicImageHeight;
//        _emoji           = NO;
    }
    return self;
}
@end


@interface BDInputEmotionManager ()

@property(nonatomic, strong) NSDictionary *textToEmotionDictionary;

@end

@implementation BDInputEmotionManager

+ (instancetype)sharedManager
{
    static BDInputEmotionManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[BDInputEmotionManager alloc]init];
    });
    return instance;
}

- (id)init
{
    if (self = [super init])
    {
        [self parsePlist];
    }
    return self;
}

- (void)parsePlist {
    
    _textToEmotionDictionary = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle bundleForClass:self.class] pathForResource:@"TextToEmotion" ofType:@"plist"]];
    
}


- (BDInputEmotion *)emotionByText:(NSString *)text {
    
    BDInputEmotion *emotion = nil;
    
    if ([text length]) {
        
        NSString *filename = [_textToEmotionDictionary objectForKey:text];
        if (filename && [filename length]) {
            
            emotion = [BDInputEmotion new];
            emotion.filename = filename;
        }
    }
    
    return emotion;
}

@end






