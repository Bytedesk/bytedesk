//
//  KFDSMsgVoiceContentView.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/18.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDMsgVoiceContentView.h"
#import "BDLoadProgressView.h"
//#import "KFDSMessageModel.h"
#import "BDUIConstants.h"
#import <AVFoundation/AVFoundation.h>
//#import "VoiceConverter.h"

//@import AFNetworking;

@interface BDMsgVoiceContentView ()<AVAudioPlayerDelegate>

//@property (nonatomic,strong) UIImageView * imageView;
@property (nonatomic, strong) UIButton                  *voiceButton;

//Voice
@property (nonatomic, strong) UILabel                   *voiceLengthLabel;
@property (nonatomic, strong) UIImageView               *voiceUnreadImageView;
@property (nonatomic, strong) UIActivityIndicatorView   *voiceUploadOrDownloadIndicatorView;

@property (nonatomic, strong) AVAudioPlayer             *voicePlayer;
@property (nonatomic, strong) NSTimer                   *voicePlayerTimer;
@property (nonatomic, assign) NSInteger                 voicePlaySwitchWavIconCounter;

//语音消息专属
@property (nonatomic, strong) NSString           *voiceName;
@property (nonatomic, strong) NSString           *voiceNameWithoutExt;

//@property (nonatomic,strong) KFDSLoadProgressView       *progressView;

@end

@implementation BDMsgVoiceContentView

@synthesize voiceButton,
            voiceLengthLabel,
            voiceUnreadImageView,
            voiceUploadOrDownloadIndicatorView,
            voicePlayer,
            voicePlayerTimer,
            voicePlaySwitchWavIconCounter,
            voiceName,
            voiceNameWithoutExt;

- (instancetype)initMessageContentView
{
    if (self = [super initMessageContentView]) {
        self.opaque = YES;
        //
//        _imageView  = [[UIImageView alloc] initWithFrame:CGRectZero];
////        _imageView.backgroundColor = [UIColor blackColor];
//        _imageView.userInteractionEnabled = YES;
//        [self addSubview:_imageView];
        //
//        UITapGestureRecognizer *singleTap =  [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleImageClicked:)];
//        [singleTap setNumberOfTapsRequired:1];
//        [_imageView addGestureRecognizer:singleTap];
        //
        voiceButton = [[UIButton alloc] initWithFrame:CGRectZero];
        [voiceButton addTarget:self action:@selector(handleVoiceClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:voiceButton];
        //
        voiceLengthLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        [self addSubview:voiceLengthLabel];
        //
        voiceUnreadImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        [self addSubview:voiceUnreadImageView];
        //
        voiceUploadOrDownloadIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleMedium];
        // TODO: 状态暂时不显示
//        [voiceUploadOrDownloadIndicatorView startAnimating];
        [self addSubview:voiceUploadOrDownloadIndicatorView];
        // TODO: 状态暂时隐藏
        [voiceUploadOrDownloadIndicatorView setHidden:TRUE];
        
        //
//        _progressView = [[KFDSLoadProgressView alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
//        _progressView.maxProgress = 1.0f;
//        [self addSubview:_progressView];
        
    }
    return self;
}

//- (BOOL)canBecomeFirstResponder {
//    return YES;
//}

- (void)initWithMessageModel:(BDMessageModel *)data {
    [super initWithMessageModel:data];
//    NSLog(@"%s voice_url:%@ length:%@", __PRETTY_FUNCTION__, self.model.voice_url, self.model.length);
    
    // TODO: 图片大小按照图片长宽比例显示
//    [_imageView setImageWithURL:[NSURL URLWithString:self.model.image_url] placeholderImage:[UIImage imageNamed:@"Fav_Cell_File_Img"]];
    [voiceLengthLabel setText:[NSString stringWithFormat:@"%@\"", self.model.length]];
    [voiceLengthLabel setTextAlignment:NSTextAlignmentCenter];
    [voiceLengthLabel setTextColor:[UIColor grayColor]];
    [voiceLengthLabel setFont:[UIFont systemFontOfSize:14.0f]];
    //
    [voiceUnreadImageView setImage:[UIImage imageNamed:@"VoiceNodeUnread" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
//    [voiceUnreadImageView setHidden:[self.model.played boolValue]];
    // TODO: 未读状态暂时隐藏
    [voiceUnreadImageView setHidden:TRUE];
    //
    [self setNeedsLayout];
}

- (void)layoutSubviews{
    [super layoutSubviews];
    
    UIEdgeInsets contentInsets = self.model.contentViewInsets;
    
    CGSize size = CGSizeMake(20 + 8 * self.model.length.intValue, 30);
    self.model.contentSize = size;
    
    CGRect voiceButtonFrame = CGRectZero;
    CGRect voiceLengthLabelFrame = CGRectZero;
    CGRect voiceUnreadImageViewFrame = CGRectZero;
    CGRect voiceUploadOrDownloadIndicatorViewFrame = CGRectZero;
    CGRect bubbleFrame = CGRectZero;
    CGRect boundsFrame = CGRectZero;
    
    if ([self.model isSend]) {
        //
        voiceButtonFrame = CGRectMake(contentInsets.left+2, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
//        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width - 55, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
        voiceLengthLabelFrame = CGRectMake(bubbleFrame.origin.x - 25, 23, 30, 30);
        //
        [self.voiceButton setImage:[UIImage imageNamed:@"SenderVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        self.voiceButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin;
        //
        [self.voiceUnreadImageView setHidden:TRUE];
    } else {
        //
        voiceButtonFrame = CGRectMake(contentInsets.left+3, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
//        boundsFrame = CGRectMake(50, 40, bubbleFrame.size.width, bubbleFrame.size.height);
        boundsFrame = CGRectMake(0, 40, bubbleFrame.size.width, bubbleFrame.size.height);
        voiceLengthLabelFrame = CGRectMake(bubbleFrame.origin.x + bubbleFrame.size.width, 23, 30, 30);
        voiceUnreadImageViewFrame = CGRectMake(bubbleFrame.origin.x + bubbleFrame.size.width, 0, 10, 10);
        voiceUploadOrDownloadIndicatorViewFrame = CGRectMake(bubbleFrame.origin.x + bubbleFrame.size.width, 0, 30, 30);
        //
        [self.voiceButton setImage:[UIImage imageNamed:@"ReceiverVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        self.voiceButton.autoresizingMask = UIViewAutoresizingFlexibleRightMargin;
    }
    self.frame = boundsFrame;
    //
    self.voiceButton.frame = voiceButtonFrame;
    self.voiceLengthLabel.frame = voiceLengthLabelFrame;
    self.voiceUnreadImageView.frame = voiceUnreadImageViewFrame;
    self.voiceUploadOrDownloadIndicatorView.frame = voiceUploadOrDownloadIndicatorViewFrame;
    self.bubbleView.frame = bubbleFrame;
    self.model.contentSize = boundsFrame.size;
}

- (void)handleVoiceClicked:(id *)sender {
    NSLog(@"%s, %@", __PRETTY_FUNCTION__, self.model.voice_url);
    
    voiceName = [self.model.voice_url lastPathComponent];
    voiceNameWithoutExt = [[voiceName componentsSeparatedByString:@"."] firstObject];
    
    //设置为 麦克风播放模式
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    NSError *error = nil;
    [audioSession setCategory:AVAudioSessionCategoryPlayback error:&error];
    if (error) {
        NSLog(@"Init AudioSession Error: domain:%@ code:%ld description:%@", [error domain], (long)[error code], [[error userInfo] description]);
        return;
    }
    
    //
    error = nil;
    [audioSession setActive:YES error:&error];
    if (error) {
        NSLog(@"AudioSession setActive Error: %@ %ld %@", [error domain], (long)[error code], [[error userInfo] description]);
        return;
    }
    
    if (voicePlayer != nil && voicePlayer.isPlaying) {
        //
        [voicePlayer stop];
        //
        [[self voicePlayerTimer] invalidate];
        voicePlayerTimer = nil;
        //
        if (self.model.isSend) {
            [[self voiceButton] setImage:[UIImage imageNamed:@"SenderVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        } else {
            [[self voiceButton] setImage:[UIImage imageNamed:@"ReceiverVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
        }
        //
        voicePlaySwitchWavIconCounter = 0;
        
        return;
    }
    else
    {
        NSString *voicePath = [NSString stringWithFormat:@"%@/Documents/%@.wav", NSHomeDirectory(), voiceNameWithoutExt];
        
        //接收到的，而非自己发送的需要下载语音文件
        if (!self.model.isSend) {
            NSFileManager *fileManager = [NSFileManager defaultManager];
            if (![fileManager fileExistsAtPath:voicePath])
            {
                NSLog(@"文件不存在，需下载");
                NSString *amrSavePath = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), voiceName];
                //需要替换下载
                NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
                [request setURL:[NSURL URLWithString:self.model.voice_url]];
                [request setHTTPMethod:@"GET"];
                // FIXME: 替换为NSURLSession
                NSData *returnData = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
                if([returnData writeToFile:amrSavePath atomically:YES])
                {
                    NSLog(@"%@ downloaded", self.model.voice_url);
                    if ([self.voiceName hasSuffix:@".amr"]) {
                        NSLog(@"is amr");
//                        [VoiceConverter amrToWav:amrSavePath wavSavePath:voicePath]; // 实际使用
//                        [KFUtils amrToWav:amrSavePath wavSavePath:voicePath];
                    }
                    else if ([self.voiceName hasSuffix:@".wav"]) {
                        NSLog(@"is wav");
                        voicePath = amrSavePath;
                    }
                }
            }
        }
        
        NSURL *voicePathURL = [NSURL URLWithString:voicePath];
        if ([[NSFileManager defaultManager] fileExistsAtPath:voicePath]) {
            //
            NSError *error = nil;
            voicePlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:voicePathURL error:&error];
            //voicePlayer = [[AVAudioPlayer alloc] initWithData:[NSData dataWithContentsOfURL:voicePathURL] error:&error];
            if (error) {
                NSLog(@"AVAudioPlayer init Error: domain:%@ code:%ld userInfo:%@", [error domain], (long)[error code], [error userInfo]);
                return;
            }
        }
        else {
            NSLog(@"%@ file not exist", voicePath);
        }
        voicePlayer.meteringEnabled = YES;
        voicePlayer.delegate = self;
    }
    
    [voicePlayer play];
    
    [self voicePlayerTimer];
    
//    if ([self.delegate respondsToSelector:@selector(voiceViewClicked:)]) {
//        [self.delegate imageViewClicked:_imageView];
//    }
    
}

-(NSTimer *)voicePlayerTimer {
    
    // FIXME: 关闭聊天页面的时候，停止播放语音
    if (!voicePlayerTimer) {
        voicePlayerTimer = [NSTimer scheduledTimerWithTimeInterval:0.5f
                                                            target:self
                                                          selector:@selector(updateVoicePlayingWavIcon)
                                                          userInfo:nil
                                                           repeats:YES];
    }
    
    return voicePlayerTimer;
}

//- (void)updateProgress:(float)progress {
//    if (progress > 1.0) {
//        progress = 1.0;
//    }
//    self.progressView.progress = progress;
//}

#pragma mark - 更新播放icon

-(void) updateVoicePlayingWavIcon {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    if (self.model.isSend) {
        //
        [[self voiceButton] setImage:[UIImage imageNamed:[NSString stringWithFormat:@"SenderVoiceNodePlaying00%ld_ios7", (unsigned long)voicePlaySwitchWavIconCounter]  inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    } else {
        //
        [[self voiceButton] setImage:[UIImage imageNamed:[NSString stringWithFormat:@"ReceiverVoiceNodePlaying00%ld_ios7", (unsigned long)voicePlaySwitchWavIconCounter]  inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    }
    voicePlaySwitchWavIconCounter = (++voicePlaySwitchWavIconCounter)%4;
}

#pragma mark - AVAudioPlayerDelegate

-(void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    
    [[self voicePlayerTimer] invalidate];
    voicePlayerTimer = nil;
    
    if (self.model.isSend) {
        [[self voiceButton] setImage:[UIImage imageNamed:@"SenderVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    } else {
        [[self voiceButton] setImage:[UIImage imageNamed:@"ReceiverVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    }
    
    voicePlaySwitchWavIconCounter = 0;
}

-(void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player error:(NSError *)error {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    
    [[self voicePlayerTimer] invalidate];
    voicePlayerTimer = nil;
    
    if (self.model.isSend) {
        [[self voiceButton] setImage:[UIImage imageNamed:@"SenderVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    } else {
        [[self voiceButton] setImage:[UIImage imageNamed:@"ReceiverVoiceNodePlaying_ios7" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil] forState:UIControlStateNormal];
    }
    
    voicePlaySwitchWavIconCounter = 0;
}


@end
