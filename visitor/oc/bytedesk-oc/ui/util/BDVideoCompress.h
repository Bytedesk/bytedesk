//
//  VideoCompress.h
//  AVFoundationVideoCustomComPressedDemo
//
//  Created by xhkj on 2019/7/17.
//  Copyright © 2019 MWM. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BDVideoCompress : NSObject

/*
 * 自定义视频压缩
 * videoUrl 原视频url路径 必传
 * outputBiteRate 压缩视频至指定比特率(bps) 可传nil 默认1500kbps
 * outputFrameRate 压缩视频至指定帧率 可传nil 默认30fps
 * outputWidth 压缩视频至指定宽度 可传nil 默认960
 * outputWidth 压缩视频至指定高度 可传nil 默认540
 * compressComplete 压缩后的视频信息回调 (id responseObjc) 可自行打印查看
 **/
+ (void)compressVideoWithVideoUrl:(NSURL *)videoUrl withBiteRate:(NSNumber * _Nullable)outputBiteRate withFrameRate:(NSNumber * _Nullable)outputFrameRate withVideoWidth:(NSNumber * _Nullable)outputWidth withVideoHeight:(NSNumber * _Nullable)outputHeight compressComplete:(void(^)(id responseObjc))compressComplete;

@end

NS_ASSUME_NONNULL_END
