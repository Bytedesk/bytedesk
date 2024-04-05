//
//  APPColorScheme.m
//  DarkModeDemoOC
//
//  Created by Xueliang Chen on 2/1/20.
//  Copyright © 2020 ChienErrant. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "APPColorScheme.h"
#import "UIColor+Hex.h"

@implementation APPColorScheme

+ (UIColor*)colorWithDarkMode:(UIColor *)darkColor lightColor:(UIColor *)lightColor{
    
    UIColor* color = nil;
    if(@available(iOS 13.0,*)){
        color = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection){
            if(traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark){
                return darkColor;
            }
            else{
                return lightColor;
            }
        }];
    }
    else{
        color = lightColor;
    }
    
    return color;
}

+ (UIColor*)colorBlue{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x429fde]  lightColor:[UIColor colorWithHex:0x0097ff]];
}
+ (UIColor*)colorOrange{
    return [self colorWithDarkMode:[UIColor colorWithHex:0xce6647]  lightColor:[UIColor colorWithHex:0xff7850]];
}
+ (UIColor*)colorGreen{
    return [self colorWithDarkMode:[UIColor colorWithHex:0xb9ff50]  lightColor:[UIColor colorWithHex:0x9aca52]];
}
+ (UIColor*)colorWhite{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x000000]  lightColor:[UIColor colorWithHex:0xffffff]];
}
+ (UIColor*)colorGrayLighter{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x262626]  lightColor:[UIColor colorWithHex:0xe5e5e5]];
}
+ (UIColor*)colorGrayLight{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x3a3a3c]  lightColor:[UIColor colorWithHex:0xcccccc]];
}
+ (UIColor*)colorGrayNormal{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x666666]  lightColor:[UIColor colorWithHex:0x999999]];
}
+ (UIColor*)colorGrayHeavy{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x999999]  lightColor:[UIColor colorWithHex:0x666666]];
}
+ (UIColor*)colorGrayHeavier{
    return [self colorWithDarkMode:[UIColor colorWithHex:0xcccccc]  lightColor:[UIColor colorWithHex:0x333333]];
}
+ (UIColor*)colorBlack{
    return [self colorWithDarkMode:[UIColor colorWithHex:0xffffff]  lightColor:[UIColor colorWithHex:0x000000]];
}
+ (UIColor*)colorForeground{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x1a1a1a]  lightColor:[UIColor colorWithHex:0xffffff]];
}
+ (UIColor*)colorBackground{
    return [self colorWithDarkMode:[UIColor colorWithHex:0x000000]  lightColor:[UIColor colorWithHex:0xf7f7f7]];
}


@end
