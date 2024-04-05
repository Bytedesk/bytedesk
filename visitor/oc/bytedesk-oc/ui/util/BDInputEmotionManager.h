//
//  BDInputEmotionManager.h
//  feedback
//
//  Created by 萝卜丝 on 2018/2/24.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BDInputEmotion : NSObject

//@property (nonatomic,strong)    NSString    *EmotionID;
@property (nonatomic,strong)    NSString    *text;
@property (nonatomic,strong)    NSString    *filename;

@end


@interface BDInputEmotionLayout : NSObject

@property (nonatomic, assign) NSInteger rows;               //行数
@property (nonatomic, assign) NSInteger columes;            //列数
@property (nonatomic, assign) NSInteger itemCountInPage;    //每页显示几项
@property (nonatomic, assign) CGFloat   cellWidth;          //单个单元格宽
@property (nonatomic, assign) CGFloat   cellHeight;         //单个单元格高
@property (nonatomic, assign) CGFloat   imageWidth;         //显示图片的宽
@property (nonatomic, assign) CGFloat   imageHeight;        //显示图片的高
@property (nonatomic, assign) BOOL      emoji;

- (id)initEmojiLayout:(CGFloat)width;

- (id)initCharletLayout:(CGFloat)width;

@end

@interface BDInputEmotionCatalog : NSObject

@property (nonatomic,strong)    BDInputEmotionLayout *layout;
@property (nonatomic,strong)    NSString        *catalogID;
@property (nonatomic,strong)    NSString        *title;
@property (nonatomic,strong)    NSDictionary    *id2Emotions;
@property (nonatomic,strong)    NSDictionary    *tag2Emotions;
@property (nonatomic,strong)    NSArray         *Emotions;
@property (nonatomic,strong)    NSString        *icon;             //图标
@property (nonatomic,strong)    NSString        *iconPressed;      //小图标按下效果
@property (nonatomic,assign)    NSInteger       pagesCount;        //分页数
@end

@interface BDInputEmotionManager : NSObject

+ (instancetype)sharedManager;

- (BDInputEmotion *)emotionByText:(NSString *)text;

@end





