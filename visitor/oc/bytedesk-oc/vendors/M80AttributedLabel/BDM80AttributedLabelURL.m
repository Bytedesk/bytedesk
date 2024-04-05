//
//  BDM80AttributedLabelURL.m
//  BDM80AttributedLabel
//
//  Created by amao on 13-8-31.
//  Copyright (c) 2013年 www.xiangwangfeng.com. All rights reserved.
//

#import "BDM80AttributedLabelURL.h"


@implementation BDM80AttributedLabelURL

+ (BDM80AttributedLabelURL *)urlWithLinkData:(id)linkData
                                     range:(NSRange)range
                                     color:(UIColor *)color
{
    BDM80AttributedLabelURL *url  = [[BDM80AttributedLabelURL alloc]init];
    url.linkData                = linkData;
    url.range                   = range;
    url.color                   = color;
    return url;
    
}


@end
