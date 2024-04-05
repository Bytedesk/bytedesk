//
//  BDCommodityTableViewCell.m
//  bytedesk-ui
//
//  Created by 萝卜丝 on 2019/3/15.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "BDMsgCommodityViewCell.h"
//@import AFNetworking;
#import <AFNetworking/UIImageView+AFNetworking.h>
//#import "UIImageView+AFNetworking.h"
#import "BDUtils.h"

@interface BDMsgCommodityViewCell ()

@property(nonatomic, strong) UIView *backgrounView;
@property(nonatomic, strong) NSDictionary *customDict;

@end

@implementation BDMsgCommodityViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)initWithMessageModel:(BDMessageModel *)messageModel {
    //
    _messageModel = messageModel;
    NSData *contentData = [messageModel.content dataUsingEncoding:NSUTF8StringEncoding];
    _customDict = [NSJSONSerialization JSONObjectWithData:contentData options:0 error:nil];
    //
    [self addSubviews];
    [self setNeedsLayout];
}

- (void)addSubviews {
    
    if (_timestampLabel) {
        [_timestampLabel removeFromSuperview];
        _timestampLabel = nil;
    }
    
    if (_commodityImageView) {
        [_commodityImageView removeFromSuperview];
        _commodityImageView = nil;
    }
    
    if (_titleLabel) {
        [_titleLabel removeFromSuperview];
        _titleLabel = nil;
    }
    
    if (_priceLabel) {
        [_priceLabel removeFromSuperview];
        _priceLabel = nil;
    }
    
    if (_contentLabel) {
        [_contentLabel removeFromSuperview];
        _contentLabel = nil;
    }
    
    if (_sendButton) {
        [_sendButton removeFromSuperview];
        _sendButton = nil;
    }
    
    if (_backgrounView) {
        [_backgrounView removeFromSuperview];
        _backgrounView = nil;
    }
    
    [self.contentView addSubview:self.backgrounView];
    
    [self.backgrounView addSubview:self.timestampLabel];
    [self.backgrounView addSubview:self.commodityImageView];
    [self.backgrounView addSubview:self.titleLabel];
    [self.backgrounView addSubview:self.priceLabel];
    [self.backgrounView addSubview:self.contentLabel];
    [self.backgrounView addSubview:self.sendButton];
}

- (UIView *)backgrounView {
    if (!_backgrounView) {
        _backgrounView = [[UIView alloc] initWithFrame:CGRectZero];
//        _backgrounView.backgroundColor = [UIColor grayColor];
        //
        _backgrounView.layer.cornerRadius = 5.0;
        _backgrounView.layer.masksToBounds = YES;
//        _backgrounView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        _backgrounView.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        _backgrounView.layer.borderWidth = 0.5;
    }
    return _backgrounView;
}

- (UIImageView *)commodityImageView {
    if (!_commodityImageView) {
        _commodityImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
//        _commodityImageView.backgroundColor = [UIColor blackColor];
        _commodityImageView.userInteractionEnabled = YES;
    }
    return _commodityImageView;
}

- (UILabel *)timestampLabel {
    //
    if (!_timestampLabel) {
        _timestampLabel = [[UILabel alloc] init];
        _timestampLabel.textColor = [UIColor grayColor];
        _timestampLabel.font = [UIFont systemFontOfSize:11.0f];
//        _timestampLabel.canPerformCopyAction = YES;
        _timestampLabel.textAlignment = NSTextAlignmentCenter;
        [_timestampLabel sizeToFit];
    }
    return _timestampLabel;
}

- (UILabel *)titleLabel {
    //
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.numberOfLines = 0;
        _titleLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _titleLabel.textColor = [UIColor grayColor];
        _titleLabel.font = [UIFont systemFontOfSize:11.0f];
//        _titleLabel.canPerformCopyAction = YES;
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        [_titleLabel sizeToFit];
    }
    return _titleLabel;
}

- (UILabel *)priceLabel {
    //
    if (!_priceLabel) {
        _priceLabel = [[UILabel alloc] init];
        _priceLabel.numberOfLines = 0;
        _priceLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _priceLabel.textColor = [UIColor redColor];
        _priceLabel.font = [UIFont systemFontOfSize:11.0f];
//        _priceLabel.canPerformCopyAction = YES;
        _priceLabel.textAlignment = NSTextAlignmentCenter;
        [_priceLabel sizeToFit];
    }
    return _priceLabel;
}

- (UILabel *)contentLabel {
    //
    if (!_contentLabel) {
        _contentLabel = [[UILabel alloc] init];
        _contentLabel.numberOfLines = 0;
        _contentLabel.lineBreakMode = NSLineBreakByWordWrapping;
        _contentLabel.textColor = [UIColor grayColor];
        _contentLabel.font = [UIFont systemFontOfSize:11.0f];
//        _contentLabel.canPerformCopyAction = YES;
        _contentLabel.textAlignment = NSTextAlignmentCenter;
        [_contentLabel sizeToFit];
    }
    return _contentLabel;
}

- (UIButton *)sendButton {
    //
    if (!_sendButton) {
        _sendButton = [[UIButton alloc] init];
        [_sendButton setTitle:@"发送" forState:UIControlStateNormal];
        //
        _sendButton.layer.cornerRadius = 5.0;
        _sendButton.layer.masksToBounds = YES;
//        _backgrounView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
        _sendButton.layer.borderColor =  [UIColor colorWithRed:200.0f/255.0f
                                                           green:200.0f/255.0f
                                                            blue:205.0f/255.0f
                                                           alpha:1.0f].CGColor;
        _sendButton.layer.borderWidth = 0.5;
        [_sendButton addTarget:self action:@selector(handleSendButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        [BDUtils setButtonTitleColor:_sendButton];
    }
    return _sendButton;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    [self layoutBackgroundView];
    [self layoutTimestampLabel];
    [self layoutCommodityImageView];
    [self layoutTitleLabel];
    [self layoutPriceLabel];
    [self layoutContentLabel];
    [self layoutSendButton];
}

- (void)layoutBackgroundView {
    
    _backgrounView.frame = CGRectMake(50, 5, (self.bounds.size.width - 100), 80);
//    _backgrounView.backgroundColor = [UIColor lightGrayColor];
}

- (void)layoutTimestampLabel {
    //
    NSString *timestampString = [BDUtils getOptimizedTimestamp:_messageModel.created_at];
    CGSize timestampSize = [timestampString sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0f]}];
    _timestampLabel.frame = CGRectMake((self.bounds.size.width - timestampSize.width - 110)/2, 0.5f, timestampSize.width + 10.0f, timestampSize.height+1);
    [_timestampLabel setText:timestampString];
}

- (void)layoutCommodityImageView {
    
    _commodityImageView.frame = CGRectMake(10, 17, 60, 60);
    [_commodityImageView setImageWithURL:[NSURL URLWithString:[_customDict objectForKey:@"imageUrl"]] placeholderImage:[UIImage imageNamed:@"Fav_Cell_File_Img"]];
}

- (void)layoutTitleLabel {
    //
    _titleLabel.frame = CGRectMake(50, 17, 100, 20);
    _titleLabel.textColor = [UIColor blackColor];
    [_titleLabel setText:[_customDict objectForKey:@"title"]];
}

- (void)layoutContentLabel {
    //
    _contentLabel.frame = CGRectMake(50, 37, 100, 20);
    _contentLabel.textColor = [UIColor blackColor];
    [_contentLabel setText:[_customDict objectForKey:@"content"]];
}

- (void)layoutPriceLabel {
    //
    _priceLabel.frame = CGRectMake(50, 55, 100, 20);
    [_priceLabel setText:[_customDict objectForKey:@"price"]];
}

- (void)layoutSendButton {
    
    _sendButton.frame = CGRectMake(160, 50, 50, 20);
    _sendButton.titleLabel.font = [UIFont systemFontOfSize:12];
//    _sendButton.titleLabel.textColor = [UIColor blackColor];
}

#pragma mark -

- (void)handleSendButtonClicked {
    
    if ([self.delegate respondsToSelector:@selector(sendCommodityButtonClicked:)]) {
        [self.delegate sendCommodityButtonClicked:_messageModel];
    }
}

@end

