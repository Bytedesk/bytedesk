//
//  BDMsgFileContentView.m
//  bytedesk-ui
//
//  Created by 萝卜丝 on 2019/4/1.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDMsgFileContentView.h"
#import "BDUIConstants.h"

@interface BDMsgFileContentView ()

@property (nonatomic,strong) UIImageView * imageView;
@property(nonatomic, strong) UIButton *fileNameLabel;

@end

@implementation BDMsgFileContentView

- (instancetype)initMessageContentView
{
    if (self = [super initMessageContentView]) {
        self.opaque = YES;
        //
        _imageView  = [[UIImageView alloc] initWithFrame:CGRectZero];
        _imageView.userInteractionEnabled = YES;
//        [self addSubview:_imageView];
        //
        _fileNameLabel = [[UIButton alloc] initWithFrame:CGRectZero];
        _fileNameLabel.backgroundColor = [UIColor clearColor];
        _fileNameLabel.userInteractionEnabled = YES;
        [self addSubview:_fileNameLabel];
        [_fileNameLabel addTarget:self action:@selector(handleFileClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

//- (BOOL)canBecomeFirstResponder {
//    return YES;
//}

- (void)initWithMessageModel:(BDMessageModel *)data{
    [super initWithMessageModel:data];
    //    NSLog(@"%s %@", __PRETTY_FUNCTION__, self.model.pic_url);
    
    // TODO: 图片大小按照图片长宽比例显示
//    [_imageView setImageWithURL:[NSURL URLWithString:self.model.image_url] placeholderImage:[UIImage imageNamed:@"Fav_Cell_File_Img"]];
    
//    _fileNameLabel.text = self.model.file_url;
    [_fileNameLabel setTitle:self.model.file_url forState:UIControlStateNormal];
    
    [self setNeedsLayout];
}

- (void)layoutSubviews{
    [super layoutSubviews];
    
    UIEdgeInsets contentInsets = self.model.contentViewInsets;
    
    CGSize size = CGSizeMake(150, 50);
    self.model.contentSize = size;
    
    CGRect imageFrame = CGRectZero;
    CGRect fileNameFrame = CGRectZero;
    CGRect bubbleFrame = CGRectZero;
    CGRect boundsFrame = CGRectZero;
    
    if ([self.model isSend]) {
        imageFrame = CGRectMake(contentInsets.left+2, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
//        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width - 55, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
        boundsFrame = CGRectMake(BDScreen.width - bubbleFrame.size.width, 23, bubbleFrame.size.width,  bubbleFrame.size.height);
        fileNameFrame = imageFrame;
    }
    else {
        imageFrame = CGRectMake(contentInsets.left+3, contentInsets.top, size.width, size.height);
        bubbleFrame = CGRectMake(0, 0, contentInsets.left + size.width + contentInsets.right + 8, contentInsets.top + size.height + contentInsets.bottom + 5);
//        boundsFrame = CGRectMake(50, 40, bubbleFrame.size.width, bubbleFrame.size.height);
        boundsFrame = CGRectMake(0, 40, bubbleFrame.size.width, bubbleFrame.size.height);
        fileNameFrame = imageFrame;
    }
    self.frame = boundsFrame;
    
    self.imageView.frame = imageFrame;
    self.fileNameLabel.frame = fileNameFrame;
    self.bubbleView.frame = bubbleFrame;
    self.model.contentSize = boundsFrame.size;
}


- (void)handleFileClicked:(id)sender {
    NSLog(@"%s, %@", __PRETTY_FUNCTION__, self.model.file_url);
    
    if ([self.delegate respondsToSelector:@selector(fileViewClicked:)]) {
        [self.delegate fileViewClicked:self.model.file_url];
    }
}


@end
