//
//  KFDSNotificationViewCell.m
//  feedback
//
//  Created by 萝卜丝 on 2018/2/21.
//  Copyright © 2018年 萝卜丝. All rights reserved.
//

#import "BDMsgNotificationViewCell.h"
#import "BDUtils.h"

@interface BDMsgNotificationViewCell()

@end


@implementation BDMsgNotificationViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
//        [self setQmui_shouldShowDebugColor:YES];
        self.backgroundColor = [UIColor systemGroupedBackgroundColor];
    }
    return self;
}

//- (void)awakeFromNib {
//    [super awakeFromNib];
//    // Initialization code
//}
//
//- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
//    [super setSelected:selected animated:animated];
//    // Configure the view for the selected state
//}

- (void)initWithMessageModel:(BDMessageModel *)messageModel {
    //
    _messageModel = messageModel;
    [self addSubviews];
    //
    [self setNeedsLayout];
}


- (void)addSubviews {
    
    if (_timestampLabel) {
        [_timestampLabel removeFromSuperview];
        _timestampLabel = nil;
    }

    if (_contentLabel) {
        [_contentLabel removeFromSuperview];
        _contentLabel = nil;
    }
    
    [self.contentView addSubview:self.timestampLabel];
    [self.contentView addSubview:self.contentLabel];
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


- (void)layoutSubviews {
    [super layoutSubviews];
    
    [self layoutTimestampLabel];
    [self layoutContentLabel];
}


- (void)layoutTimestampLabel {
    //
    NSString *timestampString = [BDUtils getOptimizedTimestamp:_messageModel.created_at];
    CGSize timestampSize = [timestampString sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0f]}];
    _timestampLabel.frame = CGRectMake((self.bounds.size.width - timestampSize.width - 10)/2, 0.5f, timestampSize.width + 10.0f, timestampSize.height+1);
    [_timestampLabel setText:timestampString];
}


- (void)layoutContentLabel {
    //
    [_contentLabel setText:_messageModel.content];
    CGSize contentSize = [_contentLabel sizeThatFits:CGSizeMake(300, CGFLOAT_MAX)];
//    CGSize contentSize = [_messageModel.content sizeWithAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:11.0f]}];
//    NSLog(@"contentSize:%fld, %fld", contentSize.width, contentSize.height);
    
    _contentLabel.frame = CGRectMake((self.bounds.size.width - contentSize.width - 10)/2, 20.5f, contentSize.width, contentSize.height);
}




@end










