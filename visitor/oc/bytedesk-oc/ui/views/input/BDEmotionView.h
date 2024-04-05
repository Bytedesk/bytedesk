//
//  BDEmotionView.h
//  ChatViewController
//
//  Created by jack on 14-5-4.
//  Copyright (c) 2014年 appkefu.com. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol BDEmotionFaceViewDelegate <NSObject>

-(void)emotionFaceButtonPressed:(id)sender;

@end

//
@interface BDEmotionFaceView : UIView

@property (nonatomic, weak) id<BDEmotionFaceViewDelegate> delegate;

@end


@protocol BDEmotionViewDelegate <NSObject>

-(void)emotionFaceButtonPressed:(id)sender;
-(void)emotionViewSendButtonPressed:(id)sender;

@end


//
@interface BDEmotionView : UIView

@property (nonatomic, weak) id<BDEmotionViewDelegate> delegate;

@end
