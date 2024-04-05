//
//  KFRecordVoiceViewHUD.m
//  ChatViewController
//
//  Created by jack on 14-5-20.
//  Copyright (c) 2014年 appkefu.com. All rights reserved.
//

#import "BDRecordVoiceViewHUD.h"
#import <AVFoundation/AVFoundation.h>
//#import "VoiceConverter.h"
#import "BDSettings.h"
#import "BDUtils.h"

#define MICROPHONE_WIDTH        50.0f
#define MICROPHONE_HEIGHT       100.0f

#define MICROWAVE_WIDTH         30.0f
#define MICROWAVE_HEIGHT        90.0f
#define MICROWAVE_LEFT_MARGIN   5.0f

#define CANCEL_LEFT_MARGIN      48.0f
#define CANCEL_TOP_MARGIN       46.0f
#define CANCEL_WIDTH            80.0f
#define CANCEL_HEIGHT           90.0f

#define HINTLABEL_LEFT_MARGIN   10.0f
#define HINTLABEL_TOP_MARIGN    120.0f
#define HINTLABEL_WIDTH         130.0f
#define HINTLABEL_HEIGHT        20.0f

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface BDRecordVoiceViewHUD ()<AVAudioRecorderDelegate>

@property (nonatomic, strong) AVAudioRecorder *voiceRecorder;
@property (nonatomic, strong) NSURL           *voicePathURL;

@property (nonatomic, strong) NSTimer         *voiceRecorderTimer;

@property (nonatomic, strong) NSString        *workgroupName;

@property (nonatomic, assign) double          voiceRecordStartTime;
@property (nonatomic, assign) double          voiceRecordEndTime;

@end

//static KFRecordVoiceViewHUD *sharedInstance = nil;

@implementation BDRecordVoiceViewHUD

@synthesize microphoneImageView,
            signalWaveImageView,
            cancelArrowImageView,

            //
            hintLabel,

            //
            voiceRecorder,
            voicePathURL,

            voiceRecorderTimer,

            //
            workgroupName,

            //
            voiceRecordStartTime,
            voiceRecordEndTime,
            voiceRecordLength;

/*
+(KFRecordVoiceViewHUD *)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[KFRecordVoiceViewHUD alloc] init];
    });
    
    return sharedInstance;
}*/

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        self.backgroundColor = UIColorFromRGB(0x525252);
        self.layer.opacity = 0.9f;
        
        self.layer.cornerRadius = 10.0;
        self.layer.masksToBounds = YES;
//        self.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        self.layer.borderColor = [UIColor systemGrayColor].CGColor;
        self.layer.borderWidth = 0.5;
        
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleTopMargin;
        
        [self setUpSubViews];
        
    }
    return self;
}


-(void)setUpSubViews
{
    [self addSubview:[self microphoneImageView]];
    [self addSubview:[self signalWaveImageView]];
    [self addSubview:[self cancelArrowImageView]];
    [self cancelArrowImageView].hidden = TRUE;
    [self addSubview:[self hintLabel]];
}


#pragma mark SubViews


-(UIImageView *)microphoneImageView
{
    if (!microphoneImageView) {
        
        CGRect frame = CGRectMake((self.bounds.size.width - MICROPHONE_WIDTH - MICROWAVE_WIDTH)/2,
                                  (self.bounds.size.height - MICROPHONE_HEIGHT - HINTLABEL_HEIGHT)/2 + 6.0f,
                                  MICROPHONE_WIDTH,
                                  MICROPHONE_HEIGHT);
        microphoneImageView = [[UIImageView alloc] initWithFrame:frame];
        [microphoneImageView setImage:[UIImage imageNamed:@"RecordingBkg" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        microphoneImageView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
    }
    
    return microphoneImageView;
}


-(UIImageView *)signalWaveImageView {

    if (!signalWaveImageView) {
        
        CGRect frame = CGRectMake((self.bounds.size.width - MICROWAVE_WIDTH - MICROPHONE_WIDTH)/2 + MICROPHONE_WIDTH + MICROWAVE_LEFT_MARGIN,
                                  (self.bounds.size.height - MICROWAVE_HEIGHT - HINTLABEL_HEIGHT)/2,
                                  MICROWAVE_WIDTH,
                                  MICROWAVE_HEIGHT);
        
        signalWaveImageView = [[UIImageView alloc] initWithFrame:frame];
        [signalWaveImageView setImage:[UIImage imageNamed:@"RecordingSignal001" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        signalWaveImageView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
    }
    
    return signalWaveImageView;
}

-(UIImageView *)cancelArrowImageView {

    if (!cancelArrowImageView) {
        
        CGRect frame = CGRectMake((self.bounds.size.width - CANCEL_WIDTH)/2,
                                  (self.bounds.size.height - CANCEL_HEIGHT - HINTLABEL_HEIGHT)/2,
                                  CANCEL_WIDTH,
                                  CANCEL_HEIGHT);
        cancelArrowImageView = [[UIImageView alloc] initWithFrame:frame];
        [cancelArrowImageView setImage:[UIImage imageNamed:@"RecordCancel" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        cancelArrowImageView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
        
    }
    
    return cancelArrowImageView;
}

-(UILabel *)hintLabel {

    if (!hintLabel) {
        
        CGRect frame = CGRectMake(HINTLABEL_LEFT_MARGIN,
                                  HINTLABEL_TOP_MARIGN,
                                  HINTLABEL_WIDTH,
                                  HINTLABEL_HEIGHT);
        
        hintLabel = [[UILabel alloc] initWithFrame:frame];
        [hintLabel setText:@"上滑取消"];
        [hintLabel setTextColor:[UIColor whiteColor]];
        [hintLabel setFont:[UIFont systemFontOfSize:14.0f]];
        [hintLabel setTextAlignment:NSTextAlignmentCenter];
        
        hintLabel.layer.cornerRadius = 5.0f;
        hintLabel.layer.masksToBounds = YES;
        
        hintLabel.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
    }
    
    return hintLabel;
}

#pragma mark Voice Record Related Functions

//
-(AVAudioRecorder *)voiceRecorder
{
    //
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    NSError *error = nil;
    [audioSession setCategory:AVAudioSessionCategoryRecord error:&error];
    if (error) {
        NSLog(@"Init AudioSession Error: %@ %ld %@", [error domain], (long)[error code], [[error userInfo] description]);
        return nil;
    }
    
    //
    error = nil;
    [audioSession setActive:YES error:&error];
    if (error) {
        NSLog(@"AudioSession setActive Error: %@ %ld %@", [error domain], (long)[error code], [[error userInfo] description]);
        return nil;
    }
    
    
    if (!voiceRecorder) {
        
        //存储路径
        NSString *tempVoicePath = [NSString stringWithFormat:@"%@/Documents/tempVoice.wav", NSHomeDirectory()];
        NSURL    *tempVoicePathURL = [NSURL URLWithString:tempVoicePath];
        
        //NSLog(@"tempVoicePath:%@", [tempVoicePathURL path]);
        
        //录音参数设置
        NSMutableDictionary *voiceRecorderSettings = [NSMutableDictionary dictionary];
        //设置录音格式  AVFormatIDKey==kAudioFormatLinearPCM
        [voiceRecorderSettings setValue:[NSNumber numberWithInt:kAudioFormatLinearPCM] forKey:AVFormatIDKey];
        //设置录音采样率(Hz) 如：AVSampleRateKey==8000/44100/96000（影响音频的质量）
        [voiceRecorderSettings setValue:[NSNumber numberWithFloat:8000.00] forKey:AVSampleRateKey];
        //录音通道数  1 或 2
        [voiceRecorderSettings setValue:[NSNumber numberWithInt:1] forKey:AVNumberOfChannelsKey];
        //线性采样位数  8、16、24、32
        [voiceRecorderSettings setValue:[NSNumber numberWithInt:16] forKey:AVLinearPCMBitDepthKey];
        //录音的质量
        [voiceRecorderSettings setValue:[NSNumber numberWithInt:AVAudioQualityHigh] forKey:AVEncoderAudioQualityKey];
        
        //初始化recorder
        error = nil;
        voiceRecorder = [[AVAudioRecorder alloc] initWithURL:tempVoicePathURL settings:voiceRecorderSettings error:&error];
        if(error)
        {
            NSLog(@"Create VoiceRecorder Error: %@ %ld %@", [error domain], (long)[error code], [[error userInfo] description]);
            return nil;
        }
        
        //
        voiceRecorder.meteringEnabled = YES;
        voiceRecorder.delegate = self;
    }
    
    return voiceRecorder;
}

-(NSTimer *)voiceRecorderTimer
{
    if (!voiceRecorderTimer) {
        
        voiceRecorderTimer = [NSTimer scheduledTimerWithTimeInterval:0.05f
                                                      target:self
                                                    selector:@selector(updateSignalWaveMeters)
                                                    userInfo:nil
                                                     repeats:YES];
        
    }
    
    return voiceRecorderTimer;
}

-(NSURL *)voicePathURL
{
    if (!voicePathURL) {

    }
    
    return voicePathURL;
}

-(void) startVoiceRecordingToUsername:(NSString *)username
{
    [[self voiceRecorder] prepareToRecord];
    [[self voiceRecorder] record];
    //
    [self voiceRecorderTimer];
    //
    workgroupName = username;
    //
    voiceRecordStartTime = [NSDate timeIntervalSinceReferenceDate];
}

-(NSString *) stopVoiceRecording
{
    //
    [[self voiceRecorder] stop];
    //
    [[self voiceRecorderTimer] invalidate];
    voiceRecorderTimer = nil;
    //
    voiceRecordEndTime = [NSDate timeIntervalSinceReferenceDate];
    voiceRecordLength = voiceRecordEndTime - voiceRecordStartTime;
    
    if (voiceRecordLength < 1) {
        return @"tooshort";
    }
    else if (voiceRecordLength > 60) {
        return @"toolong";
    }

    NSString *tempVoicePath = [NSString stringWithFormat:@"%@/Documents/tempVoice.wav", NSHomeDirectory()];
    NSString *wavVoiceFileName = [NSString stringWithFormat:@"%@_to_%@_%@_%ld.wav", [BDSettings getUsername], workgroupName, [BDUtils getCurrentTimeString], (long)voiceRecordLength];
    NSString *amrVoiceFileName = [NSString stringWithFormat:@"%@_to_%@_%@_%ld.amr", [BDSettings getUsername], workgroupName, [BDUtils getCurrentTimeString], (long)voiceRecordLength];

//    NSLog(@"wavVoiveFileName:%@, amrfilename: %@", wavVoiceFileName, amrVoiceFileName);
    NSString *wavVoicePath = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), wavVoiceFileName];
//    NSString *amrVoicePath = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), amrVoiceFileName];

    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSError *error = nil;
    [fileManager moveItemAtPath:tempVoicePath toPath:wavVoicePath error:&error];
    if (error) {
        NSLog(@"rename file Error: %@ %ld %@", [error domain], (long)[error code], [[error userInfo] description]);
    }

    return amrVoiceFileName;
}

-(void) cancelVoiceRecording
{
    [[self voiceRecorder] stop];
    [[self voiceRecorder] deleteRecording];
    
    //
    [[self voiceRecorderTimer] invalidate];
    voiceRecorderTimer = nil;
}

#pragma mark Voice Playing


#pragma mark VoiceRecord Update Meters

-(void) updateSignalWaveMeters
{
    [[self voiceRecorder] updateMeters];
    //
    CGFloat meter = [[self voiceRecorder] averagePowerForChannel:0];
    
    if (-5.0f <= meter) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal008" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-10.0f <= meter && meter < -5.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal007" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-20.0f <= meter && meter < -10.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal006" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-30.0f <= meter && meter < -20.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal005" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-40.0f <= meter && meter < -30.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal004" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-50.0f <= meter && meter < -40.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal003" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-55.0f <= meter && meter < -50.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal002" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    else if (-60.0f <= meter && meter < -55.0f) {
        
        [[self signalWaveImageView] setImage:[UIImage imageNamed:@"RecordingSignal001" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
        
    }
    
}


#pragma mark AVAudioRecorderDelegate

-(void)audioRecorderBeginInterruption:(AVAudioRecorder *)recorder
{
    NSLog(@"%s",__PRETTY_FUNCTION__);
}

-(void)audioRecorderDidFinishRecording:(AVAudioRecorder *)recorder successfully:(BOOL)flag
{
//    NSLog(@"%s",__PRETTY_FUNCTION__);
}

-(void)audioRecorderEncodeErrorDidOccur:(AVAudioRecorder *)recorder error:(NSError *)error
{
    NSLog(@"%s",__PRETTY_FUNCTION__);
}






@end





















