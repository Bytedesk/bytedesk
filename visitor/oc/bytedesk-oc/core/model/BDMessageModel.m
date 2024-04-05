//
//  KFDSMessageModel.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/24.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDMessageModel.h"
#import "BDSettings.h"
#import "BDConstants.h"
#import "BDUtils.h"

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static const DDLogLevel ddLogLevel = DDLogLevelWarning;
//#endif

@implementation BDMessageModel

-(instancetype)initWithDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _mid = [dictionary objectForKey:@"mid"];
        _status = [dictionary objectForKey:@"status"];
        _local_id = [dictionary objectForKey:@"localId"];
        //
        _type = [dictionary objectForKey:@"type"];
        _session_type = [dictionary objectForKey:@"sessionType"];
        //
        if ([dictionary objectForKey:@"user"] != nil) {
            _uid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
            _username = [[dictionary objectForKey:@"user"] objectForKey:@"username"];
            _nickname = [[dictionary objectForKey:@"user"] objectForKey:@"nickname"];
            _avatar = [[dictionary objectForKey:@"user"] objectForKey:@"avatar"];
            _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
        }
        //
        if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_WORKGROUP] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_APPOINTED] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_ROBOT]) {
            
            _wid = [dictionary objectForKey:@"wid"];
//            if ([dictionary objectForKey:@"user"] != nil) {
//                _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
//            }
            //
            if ([dictionary objectForKey:@"thread"] != nil) {
                _thread_tid = [[dictionary objectForKey:@"thread"] objectForKey:@"tid"];
//                _visitor_uid = [[[dictionary objectForKey:@"thread"] objectForKey:@"visitor"] objectForKey:@"uid"];
                NSArray *array = [_thread_tid componentsSeparatedByString:@"_"];
                if ([array count] > 0) {
                    _visitor_uid = array[1];
                }
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_CONTACT]) {
            
            if ([_uid isEqualToString:[BDSettings getUid]]) {
                _cid = [dictionary objectForKey:@"cid"];
            } else {
                if ([dictionary objectForKey:@"user"] != nil) {
                    _cid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
                }
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_GROUP]) {
            _gid = [dictionary objectForKey:@"gid"];
        }
        
        _client = [dictionary objectForKey:@"client"];
        // TODO: 根据类型获取内容
        _content = [dictionary objectForKey:@"content"];
        //
        _pic_url = [dictionary objectForKey:@"picUrl"];
        _image_url = [dictionary objectForKey:@"imageUrl"];
        //
        _media_id = [dictionary objectForKey:@"mediaId"];
        _format = [dictionary objectForKey:@"format"];
        _voice_url = [dictionary objectForKey:@"voiceUrl"];
        _length = [dictionary objectForKey:@"length"];
        _played = [dictionary objectForKey:@"played"];
        //
        _file_url = [dictionary objectForKey:@"fileUrl"];
        _file_name = [dictionary objectForKey:@"fileName"];
        _file_size = [dictionary objectForKey:@"fileSize"];
        //
        _thumb_media_id = [dictionary objectForKey:@"thumbMediaId"];
        _video_or_short_url = [dictionary objectForKey:@"videoOrShortUrl"];
        _video_or_short_thumb_url = [dictionary objectForKey:@"videoOrShortThumbUrl"];
        //
        _location_x = [dictionary objectForKey:@"locationX"];
        _location_y = [dictionary objectForKey:@"locationY"];
        _scale = [dictionary objectForKey:@"scale"];
        _label = [dictionary objectForKey:@"label"];
        //
        _title = [dictionary objectForKey:@"title"];
        _mdescription = [dictionary objectForKey:@"description"];
        _url = [dictionary objectForKey:@"url"];
        //
        _created_at = [dictionary objectForKey:@"createdAt"];
        _current_uid = [BDSettings getUid];
        // 是否标记删除
        _is_mark_deleted = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *deletedArray = [dictionary objectForKey:@"deletedSet"];
//        for (NSDictionary *userDict in deletedArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark deleted uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_deleted = [NSNumber numberWithBool:TRUE];
//            }
//        }
    }
    return self;
}

-(instancetype)initWithRobotDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _mid = [dictionary objectForKey:@"mid"];
        _status = [dictionary objectForKey:@"status"];
        _local_id = [dictionary objectForKey:@"localId"];
        //
        _type = [dictionary objectForKey:@"type"];
        _session_type = [dictionary objectForKey:@"sessionType"];
        //
        if ([dictionary objectForKey:@"user"] != nil) {
            _uid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
            _username = [[dictionary objectForKey:@"user"] objectForKey:@"username"];
            _nickname = [[dictionary objectForKey:@"user"] objectForKey:@"nickname"];
            _avatar = [[dictionary objectForKey:@"user"] objectForKey:@"avatar"];
            _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
        }
        //
        if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_WORKGROUP] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_APPOINTED] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_ROBOT]) {
            
            _wid = [dictionary objectForKey:@"wid"];
            if ([dictionary objectForKey:@"user"] != nil) {
                _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
            }
            //
            if ([dictionary objectForKey:@"thread"] != nil) {
                _thread_tid = [[dictionary objectForKey:@"thread"] objectForKey:@"tid"];
                _visitor_uid = [[[dictionary objectForKey:@"thread"] objectForKey:@"visitor"] objectForKey:@"uid"];
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_CONTACT]) {
            
            if ([_uid isEqualToString:[BDSettings getUid]]) {
                _cid = [dictionary objectForKey:@"cid"];
            } else {
                if ([dictionary objectForKey:@"user"] != nil) {
                    _cid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
                }
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_GROUP]) {
            _gid = [dictionary objectForKey:@"gid"];
        }
        
        _client = [dictionary objectForKey:@"client"];
        // TODO: 根据类型获取内容
        _content = [dictionary objectForKey:@"content"];
        //
//        NSMutableAttributedString *attributedContent = [[NSMutableAttributedString alloc] initWithString:_content];
        if ([_type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
            // 解析机器人问答
            NSMutableArray *answersArray = [dictionary objectForKey:@"answers"];
            if ([answersArray count] > 0) {
                _content = [NSString stringWithFormat:@"%@\n", _content];
            }
            for (NSDictionary *answerDict in answersArray) {
                NSLog(@"aid: %@, question: %@", answerDict[@"aid"], answerDict[@"question"]);
                //
                NSString *aid = answerDict[@"aid"];
                NSString *question = answerDict[@"question"];
                NSString *answer = answerDict[@"answer"];
                _content = [NSString stringWithFormat:@"%@\n\n<p><a href=\"robot://%@??%@??%@\">%@</a></p>", _content, aid, [BDUtils encodeString:question], [BDUtils encodeString:answer], question];
            }
            NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithData:[_content dataUsingEncoding:NSUnicodeStringEncoding] options:@{ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType } documentAttributes:nil error:nil];
            [attributedString addAttributes:@{NSFontAttributeName: [UIFont systemFontOfSize:16]} range:NSMakeRange(0, attributedString.length)];
            _contentAttr = attributedString;
            
        } else if ([_type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE] ||
                   [_type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE]) {
            // TODO: 会话自动关闭 或者 客服关闭会话，添加 ‘联系客服’ 按钮
            
        }
        //
        _pic_url = [dictionary objectForKey:@"picUrl"];
        _image_url = [dictionary objectForKey:@"imageUrl"];
        //
        _media_id = [dictionary objectForKey:@"mediaId"];
        _format = [dictionary objectForKey:@"format"];
        _voice_url = [dictionary objectForKey:@"voiceUrl"];
        _length = [dictionary objectForKey:@"length"];
        _played = [dictionary objectForKey:@"played"];
        //
        _file_url = [dictionary objectForKey:@"fileUrl"];
        _file_name = [dictionary objectForKey:@"fileName"];
        _file_size = [dictionary objectForKey:@"fileSize"];
        //
        _thumb_media_id = [dictionary objectForKey:@"thumbMediaId"];
        _video_or_short_url = [dictionary objectForKey:@"videoOrShortUrl"];
        _video_or_short_thumb_url = [dictionary objectForKey:@"videoOrShortThumbUrl"];
        //
        _location_x = [dictionary objectForKey:@"locationX"];
        _location_y = [dictionary objectForKey:@"locationY"];
        _scale = [dictionary objectForKey:@"scale"];
        _label = [dictionary objectForKey:@"label"];
        //
        _title = [dictionary objectForKey:@"title"];
        _mdescription = [dictionary objectForKey:@"description"];
        _url = [dictionary objectForKey:@"url"];
        //
        _created_at = [dictionary objectForKey:@"createdAt"];
        _current_uid = [BDSettings getUid];
        // 是否标记删除
        _is_mark_deleted = [NSNumber numberWithBool:FALSE];
    }
    return self;
}

- (instancetype)initWithRobotRightAnswerDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _mid = [dictionary objectForKey:@"mid"];
        _status = [dictionary objectForKey:@"status"];
        _local_id = [dictionary objectForKey:@"localId"];
        //
        _type = [dictionary objectForKey:@"type"];
        _session_type = [dictionary objectForKey:@"sessionType"];
        //
        if ([dictionary objectForKey:@"user"] != nil) {
            _uid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
            _username = [[dictionary objectForKey:@"user"] objectForKey:@"username"];
            _nickname = [[dictionary objectForKey:@"user"] objectForKey:@"nickname"];
            _avatar = [[dictionary objectForKey:@"user"] objectForKey:@"avatar"];
            _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
        }
        //
        if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_WORKGROUP] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_APPOINTED] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_ROBOT]) {
            
            _wid = [dictionary objectForKey:@"wid"];
            if ([dictionary objectForKey:@"user"] != nil) {
                _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
            }
            //
            if ([dictionary objectForKey:@"thread"] != nil) {
                _thread_tid = [[dictionary objectForKey:@"thread"] objectForKey:@"tid"];
                _visitor_uid = [[[dictionary objectForKey:@"thread"] objectForKey:@"visitor"] objectForKey:@"uid"];
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_CONTACT]) {
            
            if ([_uid isEqualToString:[BDSettings getUid]]) {
                _cid = [dictionary objectForKey:@"cid"];
            } else {
                if ([dictionary objectForKey:@"user"] != nil) {
                    _cid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
                }
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_GROUP]) {
            _gid = [dictionary objectForKey:@"gid"];
        }
        
        _client = [dictionary objectForKey:@"client"];
        // TODO: 根据类型获取内容
        _content = [dictionary objectForKey:@"content"];
        //
        if ([_type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
            // 解析机器人问答
            NSMutableArray *answersArray = [dictionary objectForKey:@"answers"];
            for (NSDictionary *answerDict in answersArray) {
                NSLog(@"aid: %@, question: %@", answerDict[@"aid"], answerDict[@"question"]);
                //
                NSString *aid = answerDict[@"aid"];
                NSString *question = answerDict[@"question"];
                _content = [NSString stringWithFormat:@"%@\n\n<_link>%@|%@</_link>", _content, aid, question];
            }
            //
//            NSDictionary *answer = [dictionary objectForKey:@"answer"];
//            if (![answer isKindOfClass:[NSNull class]]) {
//                NSString *aid = [answer objectForKey:@"aid"];
//                NSLog(@"aid %@", aid);
//                // TODO: 添加 有帮助 和 无帮助 评价按钮
//                _content = [NSString stringWithFormat:@"%@\n\n<_link>%@|%@</_link>", _content, [NSString stringWithFormat:@"%@:%@:%@", @"helpfull", aid, _mid], @"有帮助"];
//                _content = [NSString stringWithFormat:@"%@\n\n<_link>%@|%@</_link>", _content, [NSString stringWithFormat:@"%@:%@:%@", @"helpless", aid, _mid], @"无帮助"];
//            }
        }
        //
        _pic_url = [dictionary objectForKey:@"picUrl"];
        _image_url = [dictionary objectForKey:@"imageUrl"];
        //
        _media_id = [dictionary objectForKey:@"mediaId"];
        _format = [dictionary objectForKey:@"format"];
        _voice_url = [dictionary objectForKey:@"voiceUrl"];
        _length = [dictionary objectForKey:@"length"];
        _played = [dictionary objectForKey:@"played"];
        //
        _file_url = [dictionary objectForKey:@"fileUrl"];
        _file_name = [dictionary objectForKey:@"fileName"];
        _file_size = [dictionary objectForKey:@"fileSize"];
        //
        _thumb_media_id = [dictionary objectForKey:@"thumbMediaId"];
        _video_or_short_url = [dictionary objectForKey:@"videoOrShortUrl"];
        _video_or_short_thumb_url = [dictionary objectForKey:@"videoOrShortThumbUrl"];
        //
        _location_x = [dictionary objectForKey:@"locationX"];
        _location_y = [dictionary objectForKey:@"locationY"];
        _scale = [dictionary objectForKey:@"scale"];
        _label = [dictionary objectForKey:@"label"];
        //
        _title = [dictionary objectForKey:@"title"];
        _mdescription = [dictionary objectForKey:@"description"];
        _url = [dictionary objectForKey:@"url"];
        //
        _created_at = [dictionary objectForKey:@"createdAt"];
        _current_uid = [BDSettings getUid];
        // 是否标记删除
        _is_mark_deleted = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *deletedArray = [dictionary objectForKey:@"deletedSet"];
//        for (NSDictionary *userDict in deletedArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark deleted uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_deleted = [NSNumber numberWithBool:TRUE];
//            }
//        }
    }
    return self;
}

- (instancetype)initWithRobotNoAnswerDictionary:(NSDictionary *)dictionary {
    
    self = [super init];
    if (self) {
        //
        // TODO: 不再引用服务器端id
        _server_id = [dictionary objectForKey:@"id"];
        _mid = [dictionary objectForKey:@"mid"];
        _status = [dictionary objectForKey:@"status"];
        _local_id = [dictionary objectForKey:@"localId"];
        //
        _type = [dictionary objectForKey:@"type"];
        _session_type = [dictionary objectForKey:@"sessionType"];
        //
        if (![[dictionary objectForKey:@"user"] isKindOfClass:[NSNull class]]) {
            _uid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
            _username = [[dictionary objectForKey:@"user"] objectForKey:@"username"];
            _nickname = [[dictionary objectForKey:@"user"] objectForKey:@"nickname"];
            _avatar = [[dictionary objectForKey:@"user"] objectForKey:@"avatar"];
            _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
        }
        //
        if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_WORKGROUP] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_APPOINTED] ||
            [_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_ROBOT]) {
            
            _wid = [dictionary objectForKey:@"wid"];
            if (![[dictionary objectForKey:@"user"] isKindOfClass:[NSNull class]]) {
                _visitor = [[dictionary objectForKey:@"user"] objectForKey:@"visitor"];
            }
            //
            if (![[dictionary objectForKey:@"thread"] isKindOfClass:[NSNull class]]) {
                _thread_tid = [[dictionary objectForKey:@"thread"] objectForKey:@"tid"];
                _visitor_uid = [[[dictionary objectForKey:@"thread"] objectForKey:@"visitor"] objectForKey:@"uid"];
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_CONTACT]) {
            
            if ([_uid isEqualToString:[BDSettings getUid]]) {
                _cid = [dictionary objectForKey:@"cid"];
            } else {
                if (![[dictionary objectForKey:@"user"] isKindOfClass:[NSNull class]]) {
                    _cid = [[dictionary objectForKey:@"user"] objectForKey:@"uid"];
                }
            }
            
        } else if ([_session_type isEqualToString:BD_MESSAGE_SESSION_TYPE_GROUP]) {
            _gid = [dictionary objectForKey:@"gid"];
        }
        
        _client = [dictionary objectForKey:@"client"];
        // TODO: 根据类型获取内容
        _content = [dictionary objectForKey:@"content"];
        //
        if ([_type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
            // 解析机器人问答
            NSMutableArray *answersArray = [dictionary objectForKey:@"answers"];
            for (NSDictionary *answerDict in answersArray) {
                NSLog(@"aid: %@, question: %@", answerDict[@"aid"], answerDict[@"question"]);
                //
                NSString *aid = answerDict[@"aid"];
                NSString *question = answerDict[@"question"];
                _content = [NSString stringWithFormat:@"%@\n\n<_link>%@|%@</_link>", _content, aid, question];
            }
            // TODO: 添加 ‘人工客服’ 点击按钮
            _content = [NSString stringWithFormat:@"%@\n\n<_link>%@|%@</_link>", _content, @"requestAgent", @"人工客服"];
        }
        //
        _pic_url = [dictionary objectForKey:@"picUrl"];
        _image_url = [dictionary objectForKey:@"imageUrl"];
        //
        _media_id = [dictionary objectForKey:@"mediaId"];
        _format = [dictionary objectForKey:@"format"];
        _voice_url = [dictionary objectForKey:@"voiceUrl"];
        _length = [dictionary objectForKey:@"length"];
        _played = [dictionary objectForKey:@"played"];
        //
        _file_url = [dictionary objectForKey:@"fileUrl"];
        _file_name = [dictionary objectForKey:@"fileName"];
        _file_size = [dictionary objectForKey:@"fileSize"];
        //
        _thumb_media_id = [dictionary objectForKey:@"thumbMediaId"];
        _video_or_short_url = [dictionary objectForKey:@"videoOrShortUrl"];
        _video_or_short_thumb_url = [dictionary objectForKey:@"videoOrShortThumbUrl"];
        //
        _location_x = [dictionary objectForKey:@"locationX"];
        _location_y = [dictionary objectForKey:@"locationY"];
        _scale = [dictionary objectForKey:@"scale"];
        _label = [dictionary objectForKey:@"label"];
        //
        _title = [dictionary objectForKey:@"title"];
        _mdescription = [dictionary objectForKey:@"description"];
        _url = [dictionary objectForKey:@"url"];
        //
        _created_at = [dictionary objectForKey:@"createdAt"];
        _current_uid = [BDSettings getUid];
        // 是否标记删除
        _is_mark_deleted = [NSNumber numberWithBool:FALSE];
//        NSMutableArray *deletedArray = [dictionary objectForKey:@"deletedSet"];
//        for (NSDictionary *userDict in deletedArray) {
//            NSString *uid = userDict[@"uid"];
//            NSLog(@"mark deleted uid %@, current_uid:%@", uid, _current_uid);
//            if ([uid isEqualToString:_current_uid]) {
//                _is_mark_deleted = [NSNumber numberWithBool:TRUE];
//            }
//        }
    }
    return self;
}


- (BOOL)isEqual:(id)object {
    BDMessageModel *message = (BDMessageModel *)object;
    if ([_mid isEqualToString:message.mid]) {
        return TRUE;
    }
    return FALSE;
}

- (BOOL)isSend {
//    NSLog(@"%s uid:%@, currentUid:%@", __PRETTY_FUNCTION__, _uid, _current_uid);
    return [_uid isEqualToString:_current_uid];
}

- (BOOL)isNotification {
    // 新会话进入做特殊处理
    if ([_type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_THREAD]) {
        return NO;
    }
    //
    if ([_type hasPrefix:BD_MESSAGE_TYPE_NOTIFICATION]) {
        return YES;
    }
    //
    return NO;
}

- (BOOL)isRobot {
    return [_type isEqualToString:BD_MESSAGE_TYPE_ROBOT];
}

- (BOOL)isClientSystem {
    return [_client isEqualToString:BD_CLIENT_SYSTEM];
}

- (CGSize)contentSize {
    
    if ([_type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        //  || [_type isEqualToString:BD_MESSAGE_TYPE_ROBOT]
        return [_content boundingRectWithSize:CGSizeMake(200, FLT_MAX)
                                      options:NSStringDrawingUsesLineFragmentOrigin
                                   attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
                                      context:nil].size;
        
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        //
        return CGSizeMake(150, 200);
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
        //
        return CGSizeMake(20 + 8 * _length.intValue, 30);
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
        //
        return CGSizeMake(150, 50);
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_VIDEO] ||
               [_type isEqualToString:BD_MESSAGE_TYPE_SHORTVIDEO]) {
        //
        return CGSizeMake(100, 100);
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_QUESTIONNAIRE]) {
        //
        return CGSizeMake(150, 50);
    } else if ([_type isEqualToString:BD_MESSAGE_TYPE_RED_PACKET]) {
        //
        return CGSizeMake(150, 200);
    }
    //
    return [_content boundingRectWithSize:CGSizeMake(200, FLT_MAX)
                                  options:NSStringDrawingUsesLineFragmentOrigin
                               attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
                                  context:nil].size;
}


- (UIEdgeInsets)contentViewInsets {
    return UIEdgeInsetsMake(10, 13, 13, 10);
}



@end








