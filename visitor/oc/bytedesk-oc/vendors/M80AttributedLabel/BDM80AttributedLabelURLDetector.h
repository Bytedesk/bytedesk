//
//  BDM80AttributedLabelURLDetector.h
//  BDM80AttributedLabel
//
//  Created by amao on 2019/4/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class BDM80AttributedLabelURL;

typedef void(^M80LinkDetectCompletion)(NSArray<BDM80AttributedLabelURL *> * _Nullable links);

@protocol M80AttributedLabelCustomURLDetector <NSObject>
- (void)detectLinks:(nullable NSString *)plainText
         completion:(M80LinkDetectCompletion)completion;
@end

@interface BDM80AttributedLabelURLDetector : NSObject
@property (nonatomic,strong)    id<M80AttributedLabelCustomURLDetector> detector;

+ (instancetype)shared;

- (void)detectLinks:(nullable NSString *)plainText
         completion:(M80LinkDetectCompletion)completion;
@end

NS_ASSUME_NONNULL_END
