//
//  WBDocument.h
//  WBDocumentBrowserDemo
//
//  Created by Mr_Lucky on 2018/11/5.
//  Copyright © 2018 wenbo. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDWBDocument : UIDocument

@property (nonatomic, strong) id data;
@property(nonatomic, strong) NSString *type;

@end

NS_ASSUME_NONNULL_END
