//
//  APPColorScheme.h
//  DarkModeDemoOC
//
//  Created by Xueliang Chen on 2/1/20.
//  Copyright © 2020 ChienErrant. All rights reserved.
//
#import <UIKit/UIKit.h>

#ifndef APPColorScheme_h
#define APPColorScheme_h

@interface APPColorScheme:NSObject

+ (UIColor*)colorWithDarkMode:(UIColor*)darkColor lightColor:(UIColor*)lightColor;

@property(nonatomic,readonly,class) UIColor* colorBlue;
@property(nonatomic,readonly,class) UIColor* colorOrange;
@property(nonatomic,readonly,class) UIColor* colorGreen;

@property(nonatomic,readonly,class) UIColor* colorWhite;
@property(nonatomic,readonly,class) UIColor* colorGrayLighter;
@property(nonatomic,readonly,class) UIColor* colorGrayLight;
@property(nonatomic,readonly,class) UIColor* colorGrayNormal;
@property(nonatomic,readonly,class) UIColor* colorGrayHeavy;
@property(nonatomic,readonly,class) UIColor* colorGrayHeavier;
@property(nonatomic,readonly,class) UIColor* colorBlack;

@property(nonatomic,readonly,class) UIColor* colorForeground;
@property(nonatomic,readonly,class) UIColor* colorBackground;

@end


#endif /* APPColorScheme_h */
