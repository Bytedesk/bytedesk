//
//  VideoCompress.m
//  AVFoundationVideoCustomComPressedDemo
//
//  Created by xhkj on 2019/7/17.
//  Copyright © 2019 MWM. All rights reserved.
//

#import "BDVideoCompress.h"
//#import <SVProgressHUD/SVProgressHUD.h>
#import <AVFoundation/AVFoundation.h>

@implementation BDVideoCompress

/*
 * 自定义视频压缩
 * videoUrl 原视频url路径 必传
 * outputBiteRate 压缩视频至指定比特率(bps) 可传nil 默认1500kbps
 * outputFrameRate 压缩视频至指定帧率 可传nil 默认30fps
 * outputWidth 压缩视频至指定宽度 可传nil 默认960
 * outputWidth 压缩视频至指定高度 可传nil 默认540
 * compressComplete 压缩后的视频信息回调 (id responseObjc) 可自行打印查看
 **/
+ (void)compressVideoWithVideoUrl:(NSURL *)videoUrl withBiteRate:(NSNumber * _Nullable)outputBiteRate withFrameRate:(NSNumber * _Nullable)outputFrameRate withVideoWidth:(NSNumber * _Nullable)outputWidth withVideoHeight:(NSNumber * _Nullable)outputHeight compressComplete:(void(^)(id responseObjc))compressComplete {
    
    if (!videoUrl) {
//        [SVProgressHUD showErrorWithStatus:@"视频路径不能为空"];
        return;
    }
    
    NSLog(@"===videoUrl.abs = %@, videoUrl.path = %@", videoUrl.absoluteString, videoUrl.path);
    NSInteger compressBiteRate = outputBiteRate ? [outputBiteRate integerValue] : 1500 * 1024;
    NSInteger compressFrameRate = outputFrameRate ? [outputFrameRate integerValue] : 30;
    NSInteger compressWidth = outputWidth ? [outputWidth integerValue] : 960;
    NSInteger compressHeight = outputHeight ? [outputHeight integerValue] : 540;
    //取出原视频详细资料
    AVURLAsset *asset = [AVURLAsset assetWithURL:videoUrl];
    //视频时长 S
    CMTime time = [asset duration];
    NSInteger seconds = ceil(time.value/time.timescale);
    if (seconds < 3) {
//        [SVProgressHUD showErrorWithStatus:@"请上传3秒以上的视频"];
        return;
    }
    //压缩前原视频大小MB
    unsigned long long fileSize = [[NSFileManager defaultManager] attributesOfItemAtPath:videoUrl.path error:nil].fileSize;
    float fileSizeMB = fileSize / (1024.0*1024.0);
    //取出asset中的视频文件
    AVAssetTrack *videoTrack = [asset tracksWithMediaType:AVMediaTypeVideo].firstObject;
    //压缩前原视频宽高
    NSInteger videoWidth = videoTrack.naturalSize.width;
    NSInteger videoHeight = videoTrack.naturalSize.height;
    //压缩前原视频比特率
    NSInteger kbps = videoTrack.estimatedDataRate / 1024;
    //压缩前原视频帧率
    NSInteger frameRate = [videoTrack nominalFrameRate];
    NSLog(@"\noriginalVideo\nfileSize = %.2f MB,\n videoWidth = %ld,\n videoHeight = %ld,\n video bitRate = %ld\n, video frameRate = %ld", fileSizeMB, videoWidth, videoHeight, kbps, frameRate);
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithDictionary:@{@"urlStr" : videoUrl.path}];
    //原视频比特率小于指定比特率 不压缩 返回原视频
    if (kbps <= (compressBiteRate / 1024)) {
        compressComplete(dic);
        return;
    }
    //指定压缩视频沙盒根目录
    NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    //添加文件完整路径
    NSString *outputUrlStr = [[cachesDir stringByAppendingPathComponent:@"videoTest"] stringByAppendingPathExtension:@"mp4"];
    NSLog(@"===压缩视频存放的指定路径%@===", outputUrlStr);
    //如果指定路径下已存在其他文件 先移除指定文件
    if ([[NSFileManager defaultManager] fileExistsAtPath:outputUrlStr]) {
        BOOL removeSuccess =  [[NSFileManager defaultManager] removeItemAtPath:outputUrlStr error:nil];
        if (!removeSuccess) {
//            [SVProgressHUD showErrorWithStatus:@"旧文件移除失败"];
            return;
        }
    }
    //创建视频文件读取者
    AVAssetReader *reader = [AVAssetReader assetReaderWithAsset:asset error:nil];
    //从指定文件读取视频
    AVAssetReaderTrackOutput *videoOutput = [AVAssetReaderTrackOutput assetReaderTrackOutputWithTrack:videoTrack outputSettings:[BDVideoCompress configVideoOutput]];
    //取出原视频中音频详细资料
    AVAssetTrack *audioTrack = [asset tracksWithMediaType:AVMediaTypeAudio].firstObject;
    //从音频资料中读取音频
    AVAssetReaderTrackOutput *audioOutput = [AVAssetReaderTrackOutput assetReaderTrackOutputWithTrack:audioTrack outputSettings:[BDVideoCompress configAudioOutput]];
    //将读取到的视频信息添加到读者队列中
    if ([reader canAddOutput:videoOutput]) {
        [reader addOutput:videoOutput];
    }
    //将读取到的音频信息添加到读者队列中
    if ([reader canAddOutput:audioOutput]) {
        [reader addOutput:audioOutput];
    }
    //视频文件写入者
    AVAssetWriter *writer = [AVAssetWriter assetWriterWithURL:[NSURL fileURLWithPath:outputUrlStr] fileType:AVFileTypeMPEG4 error:nil];
    //根据指定配置创建写入的视频文件
    AVAssetWriterInput *videoInput = [AVAssetWriterInput assetWriterInputWithMediaType:AVMediaTypeVideo outputSettings:[BDVideoCompress videoCompressSettingsWithBitRate:compressBiteRate withFrameRate:compressFrameRate withWidth:compressWidth WithHeight:compressHeight withOriginalWidth:videoWidth withOriginalHeight:videoHeight]];
    //根据指定配置创建写入的音频文件
    AVAssetWriterInput *audioInput = [AVAssetWriterInput assetWriterInputWithMediaType:AVMediaTypeAudio outputSettings:[BDVideoCompress audioCompressSettings]];
    if ([writer canAddInput:videoInput]) {
        [writer addInput:videoInput];
        NSLog(@"videoInput==========videoInput");
    }
    if ([writer canAddInput:audioInput]) {
        [writer addInput:audioInput];
        NSLog(@"audioInput==========audioInput");
    }
//    [SVProgressHUD showWithStatus:@"视频压缩中..."];
    [reader startReading];
    [writer startWriting];
    [writer startSessionAtSourceTime:kCMTimeZero];
    //创建视频写入队列
    dispatch_queue_t videoQueue = dispatch_queue_create("Video Queue", DISPATCH_QUEUE_SERIAL);
    //创建音频写入队列
    dispatch_queue_t audioQueue = dispatch_queue_create("Audio Queue", DISPATCH_QUEUE_SERIAL);
    //创建一个线程组
    dispatch_group_t group = dispatch_group_create();
    //进入线程组
    dispatch_group_enter(group);
    //队列准备好后 usingBlock
    [videoInput requestMediaDataWhenReadyOnQueue:videoQueue usingBlock:^{
        BOOL completedOrFailed = NO;
        while ([videoInput isReadyForMoreMediaData] && !completedOrFailed) {
            CMSampleBufferRef sampleBuffer = [videoOutput copyNextSampleBuffer];
            if (sampleBuffer != NULL) {
                [videoInput appendSampleBuffer:sampleBuffer];
                NSLog(@"===%@===", sampleBuffer);
                CFRelease(sampleBuffer);
            } else {
                completedOrFailed = YES;
                [videoInput markAsFinished];
                dispatch_group_leave(group);
            }
        }
    }];
    dispatch_group_enter(group);
    //队列准备好后 usingBlock
    [audioInput requestMediaDataWhenReadyOnQueue:audioQueue usingBlock:^{
        BOOL completedOrFailed = NO;
        while ([audioInput isReadyForMoreMediaData] && !completedOrFailed) {
            CMSampleBufferRef sampleBuffer = [audioOutput copyNextSampleBuffer];
            if (sampleBuffer != NULL) {
                BOOL success = [audioInput appendSampleBuffer:sampleBuffer];
                NSLog(@"===%@===", sampleBuffer);
                CFRelease(sampleBuffer);
                completedOrFailed = !success;
            } else {
                completedOrFailed = YES;
            }
        }
        if (completedOrFailed) {
            [audioInput markAsFinished];
            dispatch_group_leave(group);
        }
    }];
    //完成压缩
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        if ([reader status] == AVAssetReaderStatusReading) {
            [reader cancelReading];
        }
        switch (writer.status) {
            case AVAssetWriterStatusWriting:
            {
//                [SVProgressHUD showSuccessWithStatus:@"视频压缩完成"];
                [writer finishWritingWithCompletionHandler:^{
                    [dic setObject:outputUrlStr forKey:@"urlStr"];
                    compressComplete(dic);
                }];
            }
                break;
            case AVAssetWriterStatusCancelled:
//                [SVProgressHUD showInfoWithStatus:@"取消压缩"];
                break;
            case AVAssetWriterStatusFailed:
                NSLog(@"===error：%@===", writer.error);
//                [SVProgressHUD showErrorWithStatus:[NSString stringWithFormat:@"%@",writer.error]];
                break;
            case AVAssetWriterStatusCompleted:
            {
//                [SVProgressHUD showSuccessWithStatus:@"视频压缩完成"];
                [writer finishWritingWithCompletionHandler:^{
                    [dic setObject:outputUrlStr forKey:@"urlStr"];
                    compressComplete(dic);
                }];
            }
                break;
            default:
                break;
        }
    });
}

+ (NSDictionary *)videoCompressSettingsWithBitRate:(NSInteger)biteRate withFrameRate:(NSInteger)frameRate withWidth:(NSInteger)width WithHeight:(NSInteger)height withOriginalWidth:(NSInteger)originalWidth withOriginalHeight:(NSInteger)originalHeight {
    
    /*
     * AVVideoAverageBitRateKey： 比特率（码率）每秒传输的文件大小 kbps
     * AVVideoExpectedSourceFrameRateKey：帧率 每秒播放的帧数
     * AVVideoProfileLevelKey：画质水平
     BP-Baseline Profile：基本画质。支持I/P 帧，只支持无交错（Progressive）和CAVLC；
     EP-Extended profile：进阶画质。支持I/P/B/SP/SI 帧，只支持无交错（Progressive）和CAVLC；
     MP-Main profile：主流画质。提供I/P/B 帧，支持无交错（Progressive）和交错（Interlaced），也支持CAVLC 和CABAC 的支持；
     HP-High profile：高级画质。在main Profile 的基础上增加了8×8内部预测、自定义量化、 无损视频编码和更多的YUV 格式；
     **/
    NSInteger returnWidth = originalWidth > originalHeight ? width : height;
    NSInteger returnHeight = originalWidth > originalHeight ? height : width;
    
    NSDictionary *compressProperties = @{
                                         AVVideoAverageBitRateKey : @(biteRate),
                                         AVVideoExpectedSourceFrameRateKey : @(frameRate),
                                         AVVideoProfileLevelKey : AVVideoProfileLevelH264HighAutoLevel
                                         };
    if (@available(iOS 11.0, *)) {
        NSDictionary *compressSetting = @{
                                          AVVideoCodecKey : AVVideoCodecTypeH264,
                                          AVVideoWidthKey : @(returnWidth),
                                          AVVideoHeightKey : @(returnHeight),
                                          AVVideoCompressionPropertiesKey : compressProperties,
                                          AVVideoScalingModeKey : AVVideoScalingModeResizeAspectFill
                                          };
        return compressSetting;
    }else {
        NSDictionary *compressSetting = @{
                                          AVVideoCodecKey : AVVideoCodecTypeH264,
                                          AVVideoWidthKey : @(returnWidth),
                                          AVVideoHeightKey : @(returnHeight),
                                          AVVideoCompressionPropertiesKey : compressProperties,
                                          AVVideoScalingModeKey : AVVideoScalingModeResizeAspectFill
                                          };
        return compressSetting;
    }
}

//音频设置
+ (NSDictionary *)audioCompressSettings {
    AudioChannelLayout stereoChannelLayout = {
        .mChannelLayoutTag = kAudioChannelLayoutTag_Stereo,
        .mChannelBitmap = kAudioChannelBit_Left,
        .mNumberChannelDescriptions = 0,
    };
    NSData *channelLayoutAsData = [NSData dataWithBytes:&stereoChannelLayout length:offsetof(AudioChannelLayout, mChannelDescriptions)];
    NSDictionary *audioCompressSettings = @{
                                            AVFormatIDKey : @(kAudioFormatMPEG4AAC),
                                            AVEncoderBitRateKey : @(128000),
                                            AVSampleRateKey : @(44100),
                                            AVNumberOfChannelsKey : @(2),
                                            AVChannelLayoutKey : channelLayoutAsData
                                            };
    return audioCompressSettings;
}

/** 音频解码 */
+ (NSDictionary *)configAudioOutput
{
    NSDictionary *audioOutputSetting = @{
                                         AVFormatIDKey: @(kAudioFormatLinearPCM)
                                         };
    return audioOutputSetting;
}

/** 视频解码 */
+ (NSDictionary *)configVideoOutput
{
    NSDictionary *videoOutputSetting = @{
                                         (__bridge NSString *)kCVPixelBufferPixelFormatTypeKey:[NSNumber numberWithUnsignedInt:kCVPixelFormatType_422YpCbCr8],
                                         (__bridge NSString *)kCVPixelBufferIOSurfacePropertiesKey:[NSDictionary dictionary]
                                         };
    
    return videoOutputSetting;
}

@end
