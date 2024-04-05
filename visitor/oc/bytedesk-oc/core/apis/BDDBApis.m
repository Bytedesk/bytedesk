//
//  KFDSDBApis.m
//  bdcore
//
//  Created by 萝卜丝 on 2018/11/18.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "BDDBApis.h"
//#import <FMDB/FMDB.h>
#import "FMDB.h"

#import "BDThreadModel.h"
#import "BDQueueModel.h"
#import "BDMessageModel.h"
#import "BDContactModel.h"
#import "BDGroupModel.h"
#import "BDWorkGroupModel.h"

#import "BDSettings.h"
#import "BDUtils.h"
#import "BDConstants.h"

//#import <CocoaLumberjack/CocoaLumberjack.h>
//#ifdef DEBUG
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#endif

#define CREATE_TABLE                            @"CREATE TABLE IF NOT EXISTS"
#define COLUMN_ID_KEY                           @"id INTEGER PRIMARY KEY AUTOINCREMENT" //
#define COLUMN_TYPE_TEXT                        @"TEXT"
#define COLUMN_TYPE_VARCHAR                     @"VARCHAR(255)"
#define COLUMN_TYPE_INTEGER                     @"INTEGER"
#define COLUMN_TYPE_DATETIME                    @"DATETIME"
#define COLUMN_TYPE_BLOB                        @"BLOB"
#define COLUMN_UNIQUE                           @"UNIQUE"

#define TABLE_NAME_THREAD                       @"thread"
#define TABLE_NAME_QUEUE                        @"queue"
#define TABLE_NAME_MESSAGE                      @"message"
#define TABLE_NAME_PROFILE                      @"profile"
#define TABLE_NAME_CONTACT                      @"contact"
#define TABLE_NAME_ROLE                         @"role"
#define TABLE_NAME_WORKGROUP                    @"workgroup"
#define TABLE_NAME_GROUP                        @"groups"

//
#define COLUMN_NAME_ID                          @"id"
#define COLUMN_NAME_LOCAL_ID                    @"local_id"
#define COLUMN_NAME_SERVER_ID                   @"server_id"

#define COLUMN_NAME_TID                         @"tid"
#define COLUMN_NAME_MID                         @"mid"
#define COLUMN_NAME_WID                         @"wid"
#define COLUMN_NAME_QID                         @"qid"
#define COLUMN_NAME_UID                         @"uid"
#define COLUMN_NAME_CID                         @"cid"
#define COLUMN_NAME_GID                         @"gid"

#define COLUMN_NAME_CURRENT_UID                 @"current_uid"
#define COLUMN_NAME_SESSION_ID                  @"session_id"

#define COLUMN_NAME_VISITOR_UID                 @"visitor_uid"
#define COLUMN_NAME_VISITOR_CLIENT              @"visitor_client"
#define COLUMN_NAME_CONTACT_UID                 @"contact_uid"
#define COLUMN_NAME_GROUP_GID                   @"group_uid"
#define COLUMN_NAME_AGENT_UID                   @"agent_uid"

#define COLUMN_NAME_QUEUE_QID                   @"queue_qid"
#define COLUMN_NAME_AGENT_UID                   @"agent_uid"

#define COLUMN_NAME_THREAD_TID                  @"thread_tid"
#define COLUMN_NAME_WORKGROUP_WID               @"workgroup_wid"

#define COLUMN_NAME_IS_CLOSED                   @"is_closed"
#define COLUMN_NAME_IS_AUTO_CLOSE               @"is_auto_close"

#define COLUMN_NAME_ACTIONED_AT                 @"actioned_at"
#define COLUMN_NAME_STARTED_AT                  @"started_at"
#define COLUMN_NAME_CLOSED_AT                   @"closed_at"
#define COLUMN_NAME_CREATED_AT                  @"created_at"
#define COLUMN_NAME_ACCEPTED_AT                 @"accepted_at"
#define COLUMN_NAME_LEAVED_AT                   @"leaved_at"

#define COLUMN_NAME_TOKEN                       @"token"
#define COLUMN_NAME_TOPIC                       @"topic"

#define COLUMN_NAME_USERNAME                    @"username"
#define COLUMN_NAME_NICKNAME                    @"nickname"
#define COLUMN_NAME_REALNAME                    @"realname"
#define COLUMN_NAME_AVATAR                      @"avatar"
#define COLUMN_NAME_EMAIL                       @"email"
#define COLUMN_NAME_MOBILE                      @"mobile"

#define COLUMN_NAME_SUBDOMAIN                   @"sub_domain"
#define COLUMN_NAME_MAX_THREAD_COUNT            @"max_thread_count"

#define COLUMN_NAME_CONNECTION_STATUS           @"connection_status"
#define COLUMN_NAME_ACCEPT_STATUS               @"accept_status"

#define COLUMN_NAME_IS_ENABLED                  @"is_enabled"
#define COLUMN_NAME_IS_ROBOT                    @"is_robot"

#define COLUMN_NAME_WELCOME_TIP                 @"welcome_tip"
#define COLUMN_NAME_IS_AUTO_REPLY               @"is_auto_reply"
#define COLUMN_NAME_AUTO_REPLY_CONTENT          @"auto_reply_content"

#define COLUMN_NAME_CLIENT                      @"client"
#define COLUMN_NAME_CRAFT                       @"craft"
#define COLUMN_NAME_MEMBER_COUNT                @"member_count"
#define COLUMN_NAME_ANNOUNCEMENT                @"announcement"
#define COLUMN_NAME_IS_DISMISSED                @"is_dismissed"

#define COLUMN_NAME_VISITOR                     @"visitor"
#define COLUMN_NAME_VISITOR_CLIENT              @"visitor_client"
#define COLUMN_NAME_AGENT_CLIENT                @"agent_client"

#define COLUMN_NAME_CONTENT                     @"content"
#define COLUMN_NAME_UNREADCOUNT                 @"unread_count"
#define COLUMN_NAME_IS_CURRENT                  @"is_current"
#define COLUMN_NAME_TIMESTAMP                   @"timestamp"

//
#define COLUMN_NAME_TYPE                        @"type"
#define COLUMN_NAME_SESSION_TYPE                @"session_type"
// 图片消息
#define COLUMN_NAME_PIC_URL                     @"pic_url"
#define COLUMN_NAME_IMAGE_URL                   @"image_url"
#define COLUMN_NAME_LOCAL_IMAGE_PATH            @"local_image_path"
// 文件url
#define COLUMN_NAME_FILE_URL                    @"file_url"
#define COLUMN_NAME_FILE_NAME                   @"file_name"
#define COLUMN_NAME_FILE_SIZE                   @"file_size"
#define COLUMN_NAME_LOCAL_FILE_PATH             @"local_file_path"
// 语音消息
#define COLUMN_NAME_MEDIA_ID                    @"media_id"
#define COLUMN_NAME_FORMAT                      @"format"
#define COLUMN_NAME_VOICE_URL                   @"voice_url"
#define COLUMN_NAME_LOCAL_VOICE_PATH            @"local_voice_path"
#define COLUMN_NAME_LENGTH                      @"length"
#define COLUMN_NAME_PLAYED                      @"played"
// 视频消息 & 短视频消息
#define COLUMN_NAME_THUMB_MEDIA_ID              @"thumb_media_id"
#define COLUMN_NAME_VIDEO_OR_SHORT_URL          @"video_or_short_url"
#define COLUMN_NAME_VIDEO_OR_SHORT_THUMB_URL    @"video_or_short_thumb_url"
// 地理位置消息
#define COLUMN_NAME_LOCATION_X                  @"location_x"
#define COLUMN_NAME_LOCATION_Y                  @"location_y"
#define COLUMN_NAME_SCALE                       @"scale"
#define COLUMN_NAME_LABEL                       @"label"
// 链接消息
#define COLUMN_NAME_TITLE                       @"title"
#define COLUMN_NAME_DESCRIPTION                 @"description"
#define COLUMN_NAME_URL                         @"url"
//
#define COLUMN_NAME_ROLE                        @"role"
#define COLUMN_NAME_STATUS                      @"status"
// 标记置顶
#define COLUMN_NAME_MARK_TOP                    @"mark_top"
// 是否设置消息免打扰
#define COLUMN_NAME_MARK_DISTURB                @"mark_disturb"
// 标记未读
#define COLUMN_NAME_MARK_UNREAD                 @"mark_unread"
// 标记删除
#define COLUMN_NAME_MARK_DELETED                @"mark_deleted"
// 是否临时会话
#define COLUMN_NAME_TEMP                        @"temp"


static BDDBApis *sharedInstance = nil;

// https://github.com/ccgus/fmdb
// 打开MesaSQLite编辑数据库文件

@interface BDDBApis() {
    FMDatabase      *db;
    FMDatabaseQueue *dbqueue;
}

@end

@implementation BDDBApis

-(id) init {
    self = [super init];
    if (self) {
        NSString *bytedeskDBPath = [NSTemporaryDirectory() stringByAppendingPathComponent:@"bytedesk-v12.db"];
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, bytedeskDBPath);
        db = [FMDatabase databaseWithPath:bytedeskDBPath];
        dbqueue = [FMDatabaseQueue databaseQueueWithPath:bytedeskDBPath];
        [self createTables];
    }
    return self;
}

+ (BDDBApis *)sharedInstance {
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[BDDBApis alloc] init];
    });
    return sharedInstance;
}

-(BOOL) openFMDB {
    //
    if (![self->db open]) {
        NSLog(@"open db error");
        db = nil;
        return FALSE;
    }
    return TRUE;
}

-(void) createTables {
    //
    if ([self openFMDB]) {
        [self createThreadTable];
        [self createQueueTable];
        [self createMessageTable];
        [self createContactTable];
        [self createGroupTable];
        [self createWorkGroupTable];
    }
}

-(void) closeFMDB {
    //
    if (db != nil) {
        [self->db close];
    }
}

#pragma mark 创建表

- (void) createThreadTable {
    //
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@, %@ %@ %@, %@ %@, %@ %@,%@ %@, %@ %@, %@ %@, %@ %@, %@ %@,%@ %@, %@ %@, %@ %@,%@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
       
                           CREATE_TABLE,
                           TABLE_NAME_THREAD,
                           
                           COLUMN_ID_KEY,
                           
//                           COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER, // %@ %@,
                           COLUMN_NAME_TID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_TOKEN, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_TOPIC, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                           COLUMN_NAME_SESSION_ID, COLUMN_TYPE_TEXT,
                           
                           COLUMN_NAME_CONTENT, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_TIMESTAMP, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_UNREADCOUNT, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_IS_CURRENT, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_TYPE, COLUMN_TYPE_TEXT,
        
                           COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_QUEUE_QID, COLUMN_TYPE_TEXT,
                             
                           COLUMN_NAME_VISITOR_UID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_VISITOR_CLIENT, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_CONTACT_UID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_GROUP_GID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_AGENT_UID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_WORKGROUP_WID, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_STARTED_AT, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_IS_CLOSED, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_IS_AUTO_CLOSE, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_CLOSED_AT, COLUMN_TYPE_TEXT,
                           COLUMN_NAME_CRAFT, COLUMN_TYPE_TEXT,
                             
                           COLUMN_NAME_MARK_TOP, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_MARK_DISTURB, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_MARK_UNREAD, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_MARK_DELETED, COLUMN_TYPE_INTEGER,
                           COLUMN_NAME_TEMP, COLUMN_TYPE_INTEGER,
                             
                           COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

- (void) createQueueTable {
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
                       
                       CREATE_TABLE,
                       TABLE_NAME_QUEUE,
                       
                       COLUMN_ID_KEY,
                       
                       COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_QID, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                       
                       COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                             
                       COLUMN_NAME_VISITOR_UID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VISITOR_CLIENT, COLUMN_TYPE_TEXT,
                    
                       COLUMN_NAME_AGENT_UID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_AGENT_CLIENT, COLUMN_TYPE_TEXT,
                    
                       COLUMN_NAME_THREAD_TID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_WORKGROUP_WID, COLUMN_TYPE_TEXT,
                             
                       COLUMN_NAME_ACTIONED_AT, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_STATUS, COLUMN_TYPE_TEXT,
                       
                       COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

- (void) createMessageTable {
    
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@, %@ %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
                       
                       CREATE_TABLE,
                       TABLE_NAME_MESSAGE,
                       
                       COLUMN_ID_KEY,
                       
                       COLUMN_NAME_LOCAL_ID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_MID, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                       COLUMN_NAME_WID, COLUMN_TYPE_TEXT,
                       
                       COLUMN_NAME_CID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_GID, COLUMN_TYPE_TEXT,
                       
                       COLUMN_NAME_TYPE, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_SESSION_TYPE, COLUMN_TYPE_TEXT,
                             
                       COLUMN_NAME_CLIENT, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_CONTENT, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_PIC_URL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_IMAGE_URL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LOCAL_IMAGE_PATH, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_FILE_URL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_FILE_NAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_FILE_SIZE, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LOCAL_FILE_PATH, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_MEDIA_ID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_FORMAT, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VOICE_URL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LOCAL_VOICE_PATH, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LENGTH, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_PLAYED, COLUMN_TYPE_INTEGER,
                       //
                       COLUMN_NAME_THUMB_MEDIA_ID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VIDEO_OR_SHORT_URL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VIDEO_OR_SHORT_THUMB_URL, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_LOCATION_X, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LOCATION_Y, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_SCALE, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_LABEL, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_TITLE, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_DESCRIPTION, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_URL, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_CREATED_AT, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_STATUS, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_UID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_USERNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                       //
                       COLUMN_NAME_THREAD_TID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VISITOR_UID, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_VISITOR, COLUMN_TYPE_INTEGER,
                             
                       COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

- (void) createContactTable {
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@,%@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
                       CREATE_TABLE,
                       TABLE_NAME_CONTACT,
                       
                       COLUMN_ID_KEY,
                       
                       COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_UID, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                       
                       COLUMN_NAME_USERNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_EMAIL, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_MOBILE, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_REALNAME, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_SUBDOMAIN, COLUMN_TYPE_TEXT,
                       
                       COLUMN_NAME_CONNECTION_STATUS, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_ACCEPT_STATUS, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_IS_ENABLED, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_IS_ROBOT, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_WELCOME_TIP, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_IS_AUTO_REPLY, COLUMN_TYPE_INTEGER,
                       COLUMN_NAME_AUTO_REPLY_CONTENT, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_DESCRIPTION, COLUMN_TYPE_TEXT,
                       COLUMN_NAME_MAX_THREAD_COUNT, COLUMN_TYPE_INTEGER,
                       
                       COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

- (void) createGroupTable {
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
                             CREATE_TABLE,
                             TABLE_NAME_GROUP,
                             
                             COLUMN_ID_KEY,
                             
                             COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER,
                             COLUMN_NAME_GID, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                             
                             COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_TYPE, COLUMN_TYPE_TEXT,
                             
                             COLUMN_NAME_MEMBER_COUNT, COLUMN_TYPE_INTEGER,
                             COLUMN_NAME_DESCRIPTION, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_ANNOUNCEMENT, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_IS_DISMISSED, COLUMN_TYPE_INTEGER,
                             
                             COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

- (void) createWorkGroupTable {
    [self->db executeUpdate:[NSString stringWithFormat:@"%@ %@(%@, %@ %@, %@ %@ %@, %@ %@, %@ %@, %@ %@, %@ %@) ",
                             CREATE_TABLE,
                             TABLE_NAME_WORKGROUP,
                             
                             COLUMN_ID_KEY,
                             
                             COLUMN_NAME_SERVER_ID, COLUMN_TYPE_INTEGER,
                             COLUMN_NAME_WID, COLUMN_TYPE_TEXT, COLUMN_UNIQUE,
                             
                             COLUMN_NAME_NICKNAME, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_AVATAR, COLUMN_TYPE_TEXT,
                             COLUMN_NAME_DESCRIPTION, COLUMN_TYPE_TEXT,
                             
                             COLUMN_NAME_CURRENT_UID, COLUMN_TYPE_TEXT]];
}

#pragma mark CRUD


- (BOOL) insertThread:(BDThreadModel *)thread {
    
    // FIXME: 首先删除此访客之前的会话thread，待优化
//    if ([thread.type isEqualToString:BD_THREAD_TYPE_THREAD]) {
//        [self deleteThreadUser:thread.visitor_uid];
//    }

    // 已经标记删除
//    if ([thread.is_mark_deleted boolValue]) {
//        return false;
//    }
    
//    NSLog(@"1");
    
    // 插入新记录
    BOOL result = false;
    if ([self openFMDB]) {
//        NSLog(@"2");
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ",
                                    
                                    TABLE_NAME_THREAD,
                                    
//                                    COLUMN_NAME_SERVER_ID,
                                    COLUMN_NAME_TID,
                                    COLUMN_NAME_TOKEN,
                                    COLUMN_NAME_TOPIC,
                                          
                                    COLUMN_NAME_SESSION_ID,
                                    
                                    COLUMN_NAME_CONTENT,
                                    COLUMN_NAME_TIMESTAMP,
                                    COLUMN_NAME_UNREADCOUNT,
                                    
                                    COLUMN_NAME_IS_CURRENT,
                                    COLUMN_NAME_TYPE,
                                    COLUMN_NAME_NICKNAME,
                                    COLUMN_NAME_AVATAR,
                                          
                                    COLUMN_NAME_QUEUE_QID,
                                          
                                    COLUMN_NAME_VISITOR_UID,
                                    COLUMN_NAME_VISITOR_CLIENT,
                                    COLUMN_NAME_CONTACT_UID,
                                    COLUMN_NAME_GROUP_GID,
                                    COLUMN_NAME_AGENT_UID,
                                          
                                    COLUMN_NAME_WORKGROUP_WID,
                                    COLUMN_NAME_STARTED_AT,
                                    COLUMN_NAME_IS_CLOSED,
                                    COLUMN_NAME_IS_AUTO_CLOSE,
                                    COLUMN_NAME_CLOSED_AT,
                                          
                                    COLUMN_NAME_MARK_TOP,
                                    COLUMN_NAME_MARK_DISTURB,
                                    COLUMN_NAME_MARK_UNREAD,
                                    COLUMN_NAME_MARK_DELETED,
                                    COLUMN_NAME_TEMP,
                                    
                                    COLUMN_NAME_CURRENT_UID],
                  
//                  thread.server_id,
                  thread.tid,
                  thread.token,
                  thread.topic,
                  
                  thread.session_id,
                  
                  thread.content,
                  thread.timestamp,
                  thread.unread_count,
                  
                  thread.is_current,
                  thread.type,
                  thread.nickname,
                  thread.avatar,
                  
                  thread.queue_qid,
                  
                  thread.visitor_uid,
                  thread.client,
                  thread.contact_uid,
                  thread.group_gid,
                  thread.agent_uid,
                  
                  thread.workgroup_wid,
                  thread.started_at,
                  thread.is_closed,
                  thread.is_auto_close,
                  thread.closed_at,
                  
                  thread.is_mark_top,
                  thread.is_mark_disturb,
                  thread.is_mark_unread,
                  thread.is_mark_deleted,
                  thread.is_temp,
                  
                  thread.current_uid
                  ];
//        NSLog(@"3");
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
//    NSLog(@"4");
    return result;
}

// TODO: 填充
- (BOOL) createThread:(BDThreadModel *)thread {

    return TRUE;
}

// TODO: 填充
- (BOOL) updateThread:(BDThreadModel *)thread {
    
    return TRUE;
}


- (BOOL)deleteThread:(NSString *)tId {
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                    
                                    TABLE_NAME_THREAD,
                                    COLUMN_NAME_CURRENT_UID,
                                    COLUMN_NAME_TID],
                  
                  [BDSettings getUid],
                  tId];
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (BOOL) deleteThreadUser:(NSString *)uId {
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                          
                                          TABLE_NAME_THREAD,
                                          COLUMN_NAME_CURRENT_UID,
                                          COLUMN_NAME_VISITOR_UID],
                  
                  [BDSettings getUid],
                  uId];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (NSMutableArray *) getThreads {
    
    NSMutableArray *threadArray = [[NSMutableArray alloc] init];
    NSMutableArray *topArray = [self getTopThreads];
    NSMutableArray *recentArray = [self getRecentThreads];
    [threadArray addObjectsFromArray:topArray];
    [threadArray addObjectsFromArray:recentArray];
    return threadArray;
    
    //
//    if ([self openFMDB]) {
//        //
//        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
//        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
//            //
//            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc, %@ desc",
//                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TIMESTAMP],
//                                      [BDSettings getUid]];
//            //
//            while ([resultSet next]) {
//
//                BDThreadModel *thread = [[BDThreadModel alloc] init];
//
////                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
//                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
//                thread.token = [resultSet stringForColumn:COLUMN_NAME_TOKEN];
//                thread.topic = [resultSet stringForColumn:COLUMN_NAME_TOPIC];
//                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
//
//                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
//                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
//                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
//
//                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
//                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
//                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
//                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
//
//                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
//                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
//                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
//
//                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
//                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
//                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
//                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
//
//                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
//                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
//                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
//                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
//
//                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
//
//                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
//                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
//                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
//                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
//                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
//
//                [threadArray addObject:thread];
//            }
//        }];
//        return threadArray;
//    }
//    return false;
}


- (NSMutableArray *) getTopThreads {
    //
    if ([self openFMDB]) {
        //
        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            //
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = 1 order by %@ desc",
                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TIMESTAMP],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                
                BDThreadModel *thread = [[BDThreadModel alloc] init];
                
//                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
//                thread.token = [resultSet stringForColumn:COLUMN_NAME_TOKEN];
                thread.topic = [resultSet stringForColumn:COLUMN_NAME_TOPIC];
//                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
                
                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
                
                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
//                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
//                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
//                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
//                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
//                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
//                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
//                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
//                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
//                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
                
                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
//                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
//                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
                
                [threadArray addObject:thread];
            }
        }];
        return threadArray;
    }
    return false;
}

- (NSMutableArray *) getRecentThreads {
    //
    if ([self openFMDB]) {
        //
        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            //
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = 0 order by %@ desc",
                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TIMESTAMP],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                
                BDThreadModel *thread = [[BDThreadModel alloc] init];
                
//                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
//                thread.token = [resultSet stringForColumn:COLUMN_NAME_TOKEN];
                thread.topic = [resultSet stringForColumn:COLUMN_NAME_TOPIC];
//                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
                
                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
                
                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
//                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
//                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
//                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
//                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
//                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
//                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
//                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
//                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
//                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
                
                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
//                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
//                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
                
                [threadArray addObject:thread];
            }
        }];
        return threadArray;
    }
    return false;
}



- (NSMutableArray *) getIMThreads {
    //
    if ([self openFMDB]) {
        //
        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            //
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and (type = 'contact' or type = 'group') and %@ != 1 order by %@ desc, %@ desc",
                                                             TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_MARK_DELETED, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TIMESTAMP],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                
                BDThreadModel *thread = [[BDThreadModel alloc] init];
                
//                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
                
                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
                
                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
                
                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
                
                [threadArray addObject:thread];
            }
        }];
        return threadArray;
    }
    return false;
}


- (NSMutableArray *) getOnGoingThreads {
    //
    if ([self openFMDB]) {
        //
        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            //
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = 0 order by %@ desc",
                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_IS_CLOSED, COLUMN_NAME_TIMESTAMP],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                
                BDThreadModel *thread = [[BDThreadModel alloc] init];
                
//                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
//                thread.token = [resultSet stringForColumn:COLUMN_NAME_TOKEN];
                thread.topic = [resultSet stringForColumn:COLUMN_NAME_TOPIC];
//                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
                
                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
                
                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
//                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
//                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
//                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
//                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
//                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
//                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
//                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
//                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
//                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
                
                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
//                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
//                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
                
                [threadArray addObject:thread];
            }
        }];
        return threadArray;
    }
    return false;
}

- (NSMutableArray *) getHistoryThreads {
    //
    if ([self openFMDB]) {
        //
        NSMutableArray *threadArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            //
//            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = 1 order by %@ desc",
//                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_IS_CLOSED, COLUMN_NAME_TIMESTAMP],
//                                      [BDSettings getUid]];
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc",
                                                       TABLE_NAME_THREAD, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_TIMESTAMP],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                
                BDThreadModel *thread = [[BDThreadModel alloc] init];
                
//                thread.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                thread.tid = [resultSet stringForColumn:COLUMN_NAME_TID];
//                thread.token = [resultSet stringForColumn:COLUMN_NAME_TOKEN];
                thread.topic = [resultSet stringForColumn:COLUMN_NAME_TOPIC];
//                thread.session_id = [resultSet stringForColumn:COLUMN_NAME_SESSION_ID];
                
                thread.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                thread.timestamp = [resultSet stringForColumn:COLUMN_NAME_TIMESTAMP];
                thread.unread_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_UNREADCOUNT]];
                
                thread.is_current = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CURRENT]];
                thread.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                thread.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                thread.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
//                thread.queue_qid = [resultSet stringForColumn:COLUMN_NAME_QUEUE_QID];
//                thread.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                thread.client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
//                thread.contact_uid = [resultSet stringForColumn:COLUMN_NAME_CONTACT_UID];
//                thread.group_gid = [resultSet stringForColumn:COLUMN_NAME_GROUP_GID];
//                thread.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
//                thread.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
//                thread.started_at = [resultSet stringForColumn:COLUMN_NAME_STARTED_AT];
                thread.is_closed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_CLOSED]];
//                thread.is_auto_close = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_AUTO_CLOSE]];
//                thread.closed_at = [resultSet stringForColumn:COLUMN_NAME_CLOSED_AT];
                
                thread.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                thread.is_mark_top = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_TOP]];
                thread.is_mark_disturb = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DISTURB]];
                thread.is_mark_unread = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_UNREAD]];
//                thread.is_mark_deleted = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MARK_DELETED]];
//                thread.is_temp = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_TEMP]];
                
                [threadArray addObject:thread];
            }
        }];
        return threadArray;
    }
    return false;
}


- (BOOL) clearThreads {
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                    TABLE_NAME_THREAD,
                                    COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid]];
        
        if (!result) {
            NSLog(@"%s, error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    
    return result;
}

- (BOOL) clearThreadUnreadCount:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 0 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_UNREADCOUNT, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}


- (BOOL) markTopThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 1 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}
- (BOOL) unmarkTopThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 0 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_TOP, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) markDisturbThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 1 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_DISTURB, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}
- (BOOL) unmarkDisturbThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 0 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_DISTURB, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) markUnreadThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 1 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_UNREAD, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}
- (BOOL) unmarkUnreadThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 0 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_UNREAD, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) markDeletedThread:(NSString *)tId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 1 where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_MARK_DELETED, COLUMN_NAME_TID],
                  tId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) markDeletedMessage:(NSString *)mId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = 0 where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_MARK_DELETED, COLUMN_NAME_MID],
                  mId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

// TODO: 根据tid标记所有消息已删除
- (BOOL) markClearMessage:(NSString *)tId {
    //
    BOOL result = false;
    return result;
}


- (BOOL) insertQueue:(BDQueueModel *)queue {
    BOOL result = false;
    if ([self openFMDB]) {
        // 1. 首先判断是否存在，如果已经存在，则更新update, 否则插入insert
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
                                    
                                    TABLE_NAME_QUEUE,
                                    
                                    COLUMN_NAME_SERVER_ID,
                                    COLUMN_NAME_QID,
                                    
                                    COLUMN_NAME_NICKNAME,
                                    COLUMN_NAME_AVATAR,
                                          
                                    COLUMN_NAME_VISITOR_UID,
                                    COLUMN_NAME_VISITOR_CLIENT,
                                    
                                    COLUMN_NAME_AGENT_UID,
                                    COLUMN_NAME_AGENT_CLIENT,
                                    
                                    COLUMN_NAME_THREAD_TID,
                                    COLUMN_NAME_WORKGROUP_WID,
                                    
                                    COLUMN_NAME_ACTIONED_AT,
                                    COLUMN_NAME_STATUS,

                                    COLUMN_NAME_CURRENT_UID],
                  
                  queue.server_id,
                  queue.qid,
            
                  queue.nickname,
                  queue.avatar,
                  
                  queue.visitor_uid,
                  queue.visitor_client,
                  
                  queue.agent_uid,
                  queue.agent_client,
                  
                  queue.thread_tid,
                  queue.workgroup_wid,
                  
                  queue.actioned_at,
                  queue.status,
                 
                  queue.current_uid];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL)deleteQueue:(NSString *)qId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                    TABLE_NAME_QUEUE,
                                    COLUMN_NAME_CURRENT_UID,
                                    COLUMN_NAME_QID],
                  [BDSettings getUid],
                  qId];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (NSMutableArray *) getQueues {
    //
    if ([self openFMDB]) {
        
        NSMutableArray *queueArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc", TABLE_NAME_QUEUE, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_ID], [BDSettings getUid]];
            //
            while ([resultSet next]) {
                BDQueueModel *queue = [[BDQueueModel alloc] init];
                //
                queue.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                queue.qid = [resultSet stringForColumn:COLUMN_NAME_QID];
                
                queue.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                queue.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
                queue.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                queue.visitor_client = [resultSet stringForColumn:COLUMN_NAME_VISITOR_CLIENT];
                
                queue.agent_uid = [resultSet stringForColumn:COLUMN_NAME_AGENT_UID];
                queue.agent_client = [resultSet stringForColumn:COLUMN_NAME_AGENT_CLIENT];
                
                queue.thread_tid = [resultSet stringForColumn:COLUMN_NAME_THREAD_TID];
                queue.workgroup_wid = [resultSet stringForColumn:COLUMN_NAME_WORKGROUP_WID];
                
                queue.actioned_at = [resultSet stringForColumn:COLUMN_NAME_ACTIONED_AT];
                queue.status = [resultSet stringForColumn:COLUMN_NAME_STATUS];
                
                queue.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                //
                [queueArray addObject:queue];
            }
        }];
        return queueArray;
    }
    return false;
}

- (NSNumber *)getQueueCount {
    //
    if ([self openFMDB]) {
        
        FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select count(*) as count from %@ where %@ = ? ", TABLE_NAME_QUEUE, COLUMN_NAME_CURRENT_UID], [BDSettings getUid]];
        //
        [resultSet next];
        //
        return [NSNumber numberWithInt:[resultSet intForColumn:@"count"]];
    }
    return 0;
}

- (BOOL) clearQueues {
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                    TABLE_NAME_QUEUE,
                                    COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid]];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    
    return result;
}

- (BDMessageModel *) insertTextMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                   withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_TEXT withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertRobotMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                             withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_ROBOT withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertImageMessageLocal:(NSString *)tid withWorkGroupWid:(NSString *)wid
                     withContent:(NSString *)content withLocalId:(NSString *)localId
                 withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_IMAGE withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertVoiceMessageLocal:(NSString *)tid
                            withWorkGroupWid:(NSString *)wid
                                 withContent:(NSString *)content
                                 withLocalId:(NSString *)localId
                             withSessionType:(NSString *)sessionType
                                 withVoiceLength:(int)voiceLength
                                  withFormat:format
                                    withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_VOICE withSessionType:sessionType withVoiceLength:voiceLength withFormat:format withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertFileMessageLocal:(NSString *)tid
                           withWorkGroupWid:(NSString *)wid
                                withContent:(NSString *)content
                                withLocalId:(NSString *)localId
                            withSessionType:(NSString *)sessionType
                                 withFormat:format
                               withFileName:(NSString *)fileName
                               withFileSize:(NSString *)fileSize
                                   withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_FILE withSessionType:sessionType withVoiceLength:0 withFormat:format withFileName:fileName withFileSize:fileSize withSend:isSend];
}

- (BDMessageModel *) insertVideoMessageLocal:(NSString *)tid withWorkGroupWid:(NSString *)wid
                     withContent:(NSString *)content withLocalId:(NSString *)localId
                 withSessionType:(NSString *)sessionType
                                    withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_VIDEO withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertCommodityMessageLocal:(NSString *)tid
                                withWorkGroupWid:(NSString *)wid
                                     withContent:(NSString *)content
                                     withLocalId:(NSString *)localId
                                 withSessionType:(NSString *)sessionType
                                        withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_COMMODITY withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}

- (BDMessageModel *) insertRedPacketMessageLocal:(NSString *)tid
                                withWorkGroupWid:(NSString *)wid
                                     withContent:(NSString *)content
                                     withLocalId:(NSString *)localId
                                 withSessionType:(NSString *)sessionType
                                        withSend:(BOOL)isSend {
    return [self insertMessageLocal:tid withWorkGroupWid:wid withContent:content withLocalId:localId withType:BD_MESSAGE_TYPE_RED_PACKET withSessionType:sessionType withVoiceLength:0 withFormat:@"" withFileName:@"" withFileSize:@"" withSend:isSend];
}



- (BDMessageModel *) insertMessageLocal:(NSString *)tid
                       withWorkGroupWid:(NSString *)wid
                            withContent:(NSString *)content
                            withLocalId:(NSString *)localId
                               withType:(NSString *)type
                        withSessionType:(NSString *)sessionType
                        withVoiceLength:(int)voiceLength
                             withFormat:(NSString *)format
                           withFileName:(NSString *)fileName
                           withFileSize:(NSString *)fileSize
                               withSend:(BOOL)isSend {
    
    BDMessageModel *message = [[BDMessageModel alloc] init];
    // 设置mid === localId
    message.mid = localId;
    
    if ([sessionType isEqualToString:BD_THREAD_TYPE_CONTACT]) {
        message.cid = tid;
    } else if ([sessionType isEqualToString:BD_THREAD_TYPE_GROUP]) {
        message.gid = tid;
    } else {
        message.wid = wid;
        message.thread_tid = tid;
    }
    message.local_id = localId;
    message.type = type;
    message.client = [BDSettings getClient];
    //
    if ([type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        message.content = content;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
        message.content = content;
        message.contentAttr = [BDUtils transformContentToContentAttr:content];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
        message.image_url = content;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
        message.voice_url = content;
        message.length = [NSNumber numberWithInt:voiceLength];
        message.format = format;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
        message.file_url = content;
        message.format = format;
        message.file_name = fileName;
        message.file_size = fileSize;
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_VIDEO]) {
        message.video_or_short_url = content;
    } else {
        message.content = content;
    }
    //
    message.status = BD_MESSAGE_STATUS_SENDING;
    message.session_type = sessionType;
    message.created_at = [BDUtils getCurrentDate];
    //
    if (isSend) {
        message.uid = [BDSettings getUid];
        message.username = [BDSettings getUsername];
        message.nickname = [BDSettings getNickname];
        message.avatar = [BDSettings getAvatar];
    } else {
        message.uid = @"";
    }
    
    message.current_uid = [BDSettings getUid];
//    message.visitor
    
    [self insertMessageLocal:message];
    
    return message;
}

- (BOOL) insertMessage:(BDMessageModel *)message {
    
    // TODO: 消息回执和消息预知 没有id字段, 暂不处理此类型，后续处理
    // TODO: 对于访客端暂时忽略连接状态信息
    if ([message.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT] ||
        [message.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW] ||
        [message.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_CONNECT] ||
        [message.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_DISCONNECT] ||
        [message.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_KICKOFF] ||
        // 弹窗处理
        [message.type isEqualToString:BD_MESSAGE_TYPE_QUESTIONNAIRE] ||
        [message.type isEqualToString:BD_MESSAGE_TYPE_WORKGROUP]) {
        return false;
    }
    
//    // 已经标记删除
//    if ([message.is_mark_deleted boolValue]) {
//        return false;
//    }
    // NSLog(@"local id:%@", message.local_id);
    
    // 更新本地发送消息
    if (message.local_id != nil && message.local_id != NULL &&
        ![message.local_id isKindOfClass:[NSNull class]] &&
        ![message.local_id isEqualToString:@"null"]) {
        //
        [self updateMessage:message.local_id withServerId:message.server_id withMid:message.mid withStatus:message.status];
    }
    
    return [self insertMessageLocal:message];
}

- (BOOL) insertMessageLocal:(BDMessageModel *)message {
    
    //
    BOOL result = false;
    //
    if ([self openFMDB]) {
        // 1. 首先判断是否存在，如果已经存在，则更新update, 否则插入insert
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@,%@) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                                          
                                          TABLE_NAME_MESSAGE,
                                          
                                          COLUMN_NAME_LOCAL_ID,
                                          COLUMN_NAME_SERVER_ID,
                                          COLUMN_NAME_MID,
                                          COLUMN_NAME_WID,
                                          COLUMN_NAME_CID,
                                          COLUMN_NAME_GID,
                                          //
                                          COLUMN_NAME_TYPE,
                                          COLUMN_NAME_SESSION_TYPE,
                                          //
                                          COLUMN_NAME_CONTENT,
                                          //
                                          COLUMN_NAME_PIC_URL,
                                          COLUMN_NAME_IMAGE_URL,
                                          //
                                          COLUMN_NAME_FILE_URL,
                                          COLUMN_NAME_FILE_NAME,
                                          COLUMN_NAME_FILE_SIZE,
                                          //
                                          COLUMN_NAME_MEDIA_ID,
                                          COLUMN_NAME_FORMAT,
                                          COLUMN_NAME_VOICE_URL,
                                          COLUMN_NAME_LENGTH,
                                          COLUMN_NAME_PLAYED,
                                          //
                                          COLUMN_NAME_THUMB_MEDIA_ID,
                                          COLUMN_NAME_VIDEO_OR_SHORT_URL,
                                          COLUMN_NAME_VIDEO_OR_SHORT_THUMB_URL,
                                          //
                                          COLUMN_NAME_LOCATION_X,
                                          COLUMN_NAME_LOCATION_Y,
                                          COLUMN_NAME_SCALE,
                                          COLUMN_NAME_LABEL,
                                          //
                                          COLUMN_NAME_TITLE,
                                          COLUMN_NAME_DESCRIPTION,
                                          COLUMN_NAME_URL,
                                          //
                                          COLUMN_NAME_CREATED_AT,
                                          COLUMN_NAME_STATUS,
                                          //
                                          COLUMN_NAME_UID,
                                          COLUMN_NAME_USERNAME,
                                          COLUMN_NAME_NICKNAME,
                                          COLUMN_NAME_AVATAR,
                                          //
                                          COLUMN_NAME_VISITOR,
                                          COLUMN_NAME_THREAD_TID,
                                          COLUMN_NAME_VISITOR_UID,
                                          COLUMN_NAME_CURRENT_UID],
                  
                  message.local_id,
                  message.server_id,
                  message.mid,
                  message.wid,
                  message.cid,
                  message.gid,
                  
                  message.type,
                  message.session_type,
                  
                  message.content,
                  
                  message.pic_url,
                  message.image_url,
                  
                  message.file_url,
                  message.file_name,
                  message.file_size,
                  
                  message.media_id,
                  message.format,
                  message.voice_url,
                  message.length,
                  message.played,
                  
                  message.thumb_media_id,
                  message.video_or_short_url,
                  message.video_or_short_thumb_url,
                  
                  message.location_x,
                  message.location_y,
                  message.scale,
                  message.label,
                  
                  message.title,
                  message.mdescription,
                  message.url,
                  
                  message.created_at,
                  message.status,
                  
                  message.uid,
                  message.username,
                  message.nickname,
                  message.avatar,
                  
                  message.visitor,
                  message.thread_tid,
                  message.visitor_uid,
                  message.current_uid
                  ];
        
        if (!result) {
            NSLog(@"插入失败 error = %@", [self->db lastErrorMessage]);
        }
    }
    
    return result;
}

- (NSMutableArray *) getMessagesWithType:(NSString *)type withUid:(NSString *)uid {
    
    if ([self openFMDB]) {
        
        NSMutableArray *messageArray = [[NSMutableArray alloc] init];
        [dbqueue inDatabase:^(FMDatabase *mydb) {
            //
            NSString *column = COLUMN_NAME_VISITOR_UID;
            if ([type isEqualToString:BD_GET_MESSAGE_TYPE_WORKGROUP]) {
                column = COLUMN_NAME_WID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_THREAD]) {
                column = COLUMN_NAME_THREAD_TID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_USER]) {
                column = COLUMN_NAME_VISITOR_UID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_CONTACT]){
                column = COLUMN_NAME_CID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_GROUP]) {
                column = COLUMN_NAME_GID;
            }
            //
            NSString *sql = [NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = ? order by %@ asc", TABLE_NAME_MESSAGE, COLUMN_NAME_CURRENT_UID, column, COLUMN_NAME_CREATED_AT];
//            NSLog(@"type %@, tid: %@, column %@, sql %@", type, uid, column, sql);
            FMResultSet *resultSet = [self->db executeQuery:sql, [BDSettings getUid], uid];
            //
            while ([resultSet next]) {
                //
                BDMessageModel *message = [[BDMessageModel alloc] init];
                //
                message.uu_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_ID]];
                //
                message.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                message.mid = [resultSet stringForColumn:COLUMN_NAME_MID];
                message.wid = [resultSet stringForColumn:COLUMN_NAME_WID];
                message.cid = [resultSet stringForColumn:COLUMN_NAME_CID];
                message.gid = [resultSet stringForColumn:COLUMN_NAME_GID];
                
                message.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                message.session_type = [resultSet stringForColumn:COLUMN_NAME_SESSION_TYPE];
                
                message.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                
                message.pic_url = [resultSet stringForColumn:COLUMN_NAME_PIC_URL];
                message.image_url = [resultSet stringForColumn:COLUMN_NAME_IMAGE_URL];
                message.local_image_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_IMAGE_PATH];
                
                message.file_url = [resultSet stringForColumn:COLUMN_NAME_FILE_URL];
                message.file_name = [resultSet stringForColumn:COLUMN_NAME_FILE_NAME];
                message.file_size = [resultSet stringForColumn:COLUMN_NAME_FILE_SIZE];
                message.local_file_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_FILE_PATH];
                
                message.media_id = [resultSet stringForColumn:COLUMN_NAME_MEDIA_ID];
                message.format = [resultSet stringForColumn:COLUMN_NAME_FORMAT];
                message.voice_url = [resultSet stringForColumn:COLUMN_NAME_VOICE_URL];
                message.length = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_LENGTH]];
                message.played = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_PLAYED]];
                message.local_voice_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_VOICE_PATH];
                
                message.thumb_media_id = [resultSet stringForColumn:COLUMN_NAME_THUMB_MEDIA_ID];
                message.video_or_short_url = [resultSet stringForColumn:COLUMN_NAME_VIDEO_OR_SHORT_URL];
                message.video_or_short_thumb_url = [resultSet stringForColumn:COLUMN_NAME_VIDEO_OR_SHORT_THUMB_URL];
                
                message.location_x = [resultSet stringForColumn:COLUMN_NAME_LOCATION_X];
                message.location_y = [resultSet stringForColumn:COLUMN_NAME_LOCATION_Y];
                message.scale = [resultSet stringForColumn:COLUMN_NAME_SCALE];
                message.label = [resultSet stringForColumn:COLUMN_NAME_LABEL];
                
                message.title = [resultSet stringForColumn:COLUMN_NAME_TITLE];
                message.mdescription = [resultSet stringForColumn:COLUMN_NAME_DESCRIPTION];
                message.url = [resultSet stringForColumn:COLUMN_NAME_URL];
                
                message.created_at = [resultSet stringForColumn:COLUMN_NAME_CREATED_AT];
                message.status = [resultSet stringForColumn:COLUMN_NAME_STATUS];
                
                message.uid = [resultSet stringForColumn:COLUMN_NAME_UID];
                message.username = [resultSet stringForColumn:COLUMN_NAME_USERNAME];
                message.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                message.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
                message.visitor = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_VISITOR]];
                message.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                message.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
//                NSLog(@"content %@", message.content);
                
//                NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithData:[message.content dataUsingEncoding:NSUnicodeStringEncoding] options:@{ NSDocumentTypeDocumentAttribute: NSHTMLTextDocumentType } documentAttributes:nil error:nil];
//                [attributedString addAttributes:@{NSFontAttributeName: [UIFont systemFontOfSize:16]} range:NSMakeRange(0, attributedString.length)];
//                message.contentAttr = attributedString;
                
                [messageArray addObject:message];
            }
        }];
        return messageArray;
        
    }
    return false;
}

- (void)clearMessagesWithType:(NSString *)type withUid:(NSString *)uid {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        NSString *column = COLUMN_NAME_VISITOR_UID;
        if ([type isEqualToString:BD_GET_MESSAGE_TYPE_WORKGROUP]) {
            column = COLUMN_NAME_WID;
        } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_THREAD]) {
            column = COLUMN_NAME_THREAD_TID;
        } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_USER]) {
            column = COLUMN_NAME_VISITOR_UID;
        } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_CONTACT]){
            column = COLUMN_NAME_CID;
        } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_GROUP]) {
            column = COLUMN_NAME_GID;
        }
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                          TABLE_NAME_MESSAGE,
                                          column
                                          ],
                  uid];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
}

- (NSMutableArray *) getMessagesPage:(NSUInteger)page withSize:(NSUInteger)size withType:(NSString *)type withUid:(NSString *)uid {
    //
    if ([self openFMDB]) {
        // 使用thread_id作为channel
        
        NSMutableArray *messageArray = [[NSMutableArray alloc] init];
        [dbqueue inDatabase:^(FMDatabase *mydb) {
            
            //
            NSString *column = COLUMN_NAME_VISITOR_UID;
            if ([type isEqualToString:BD_GET_MESSAGE_TYPE_WORKGROUP]) {
                column = COLUMN_NAME_WID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_THREAD]) {
                column = COLUMN_NAME_THREAD_TID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_USER]) {
                column = COLUMN_NAME_VISITOR_UID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_CONTACT]){
                column = COLUMN_NAME_CID;
            } else if ([type isEqualToString:BD_GET_MESSAGE_TYPE_GROUP]) {
                column = COLUMN_NAME_GID;
            }
            
            NSUInteger offset = page * size;
            // SELECT ... FROM ... WHERE ... ORDER BY ... LIMIT ...
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? and %@ = ? order by %@ asc limit %lu, %lu",
                                                             TABLE_NAME_MESSAGE, COLUMN_NAME_CURRENT_UID, column, COLUMN_NAME_SERVER_ID, (unsigned long)offset, (unsigned long)size],
                                      [BDSettings getUid],
                                      uid];
            //
            while ([resultSet next]) {
                //
                BDMessageModel *message = [[BDMessageModel alloc] init];
                //
                message.uu_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_ID]];
                //
                message.server_id = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_SERVER_ID]];
                message.mid = [resultSet stringForColumn:COLUMN_NAME_MID];
                message.wid = [resultSet stringForColumn:COLUMN_NAME_WID];
                message.cid = [resultSet stringForColumn:COLUMN_NAME_CID];
                message.gid = [resultSet stringForColumn:COLUMN_NAME_GID];
                
                message.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                message.session_type = [resultSet stringForColumn:COLUMN_NAME_SESSION_TYPE];
                
                message.content = [resultSet stringForColumn:COLUMN_NAME_CONTENT];
                
                message.pic_url = [resultSet stringForColumn:COLUMN_NAME_PIC_URL];
                message.image_url = [resultSet stringForColumn:COLUMN_NAME_IMAGE_URL];
                message.local_image_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_IMAGE_PATH];
                
                message.file_url = [resultSet stringForColumn:COLUMN_NAME_FILE_URL];
                message.file_name = [resultSet stringForColumn:COLUMN_NAME_FILE_NAME];
                message.file_size = [resultSet stringForColumn:COLUMN_NAME_FILE_SIZE];
                message.local_file_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_FILE_PATH];
                
                message.media_id = [resultSet stringForColumn:COLUMN_NAME_MEDIA_ID];
                message.format = [resultSet stringForColumn:COLUMN_NAME_FORMAT];
                message.voice_url = [resultSet stringForColumn:COLUMN_NAME_VOICE_URL];
                message.length = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_LENGTH]];
                message.played = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_PLAYED]];
                message.local_voice_path = [resultSet stringForColumn:COLUMN_NAME_LOCAL_VOICE_PATH];
                
                message.thumb_media_id = [resultSet stringForColumn:COLUMN_NAME_THUMB_MEDIA_ID];
                message.video_or_short_url = [resultSet stringForColumn:COLUMN_NAME_VIDEO_OR_SHORT_URL];
                message.video_or_short_thumb_url = [resultSet stringForColumn:COLUMN_NAME_VIDEO_OR_SHORT_THUMB_URL];
                
                message.location_x = [resultSet stringForColumn:COLUMN_NAME_LOCATION_X];
                message.location_y = [resultSet stringForColumn:COLUMN_NAME_LOCATION_Y];
                message.scale = [resultSet stringForColumn:COLUMN_NAME_SCALE];
                message.label = [resultSet stringForColumn:COLUMN_NAME_LABEL];
                
                message.title = [resultSet stringForColumn:COLUMN_NAME_TITLE];
                message.mdescription = [resultSet stringForColumn:COLUMN_NAME_DESCRIPTION];
                message.url = [resultSet stringForColumn:COLUMN_NAME_URL];
                
                message.created_at = [resultSet stringForColumn:COLUMN_NAME_CREATED_AT];
                message.status = [resultSet stringForColumn:COLUMN_NAME_STATUS];
                
                message.uid = [resultSet stringForColumn:COLUMN_NAME_UID];
                message.username = [resultSet stringForColumn:COLUMN_NAME_USERNAME];
                message.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                message.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
                message.visitor = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_VISITOR]];
                message.visitor_uid = [resultSet stringForColumn:COLUMN_NAME_VISITOR_UID];
                message.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                
                [messageArray addObject:message];
            }
        }];
        return messageArray;
        
    }
    return false;
}


- (BOOL) updateMessage:(NSString *)localId withServerId:(NSNumber *)serverId withMid:(NSString *)mid withStatus:(NSString *)status {
    // NSLog(@"do update local id:%@, server id:%@, mid:%@, status:%@", localId, serverId, mid, status);
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ?, %@ = ?, %@ = ? where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_SERVER_ID, COLUMN_NAME_MID, COLUMN_NAME_STATUS, COLUMN_NAME_LOCAL_ID],
                  serverId,
                  mid,
                  status,
                  localId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateMessageSuccess:(NSString *)localId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_STATUS, COLUMN_NAME_LOCAL_ID],
                  BD_MESSAGE_STATUS_STORED,
                  localId];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateMessageError:(NSString *)localId {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_STATUS, COLUMN_NAME_LOCAL_ID],
                  BD_MESSAGE_STATUS_ERROR,
                  localId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        } else {
            NSLog(@"更新发送消息失败状态成功 %@", localId);
        }
    }
    return result;
}

- (BOOL) updateMessage:(NSString *)localId withStatus:(NSString *)status {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? and %@ != 'read' ",
                                    TABLE_NAME_MESSAGE, COLUMN_NAME_STATUS, COLUMN_NAME_LOCAL_ID, COLUMN_NAME_STATUS],
                  status,
                  localId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateMessage:(NSString *)mId withLocalFilePath:(NSString *)localFilePath {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_LOCAL_FILE_PATH, COLUMN_NAME_MID],
                  localFilePath,
                  mId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateMessageLocal:(NSString *)localId withLocalFilePath:(NSString *)localFilePath {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_MESSAGE, COLUMN_NAME_LOCAL_FILE_PATH, COLUMN_NAME_LOCAL_ID],
                  localFilePath,
                  localId];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) deleteMessage:(NSString *)mid {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                    TABLE_NAME_MESSAGE,
                                    COLUMN_NAME_CURRENT_UID,
                                    COLUMN_NAME_MID],
                  [BDSettings getUid],
                  mid];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (BOOL) deleteThreadMessages:(NSString *)tid {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                          TABLE_NAME_MESSAGE,
                                          COLUMN_NAME_CURRENT_UID,
                                          COLUMN_NAME_THREAD_TID],
                  [BDSettings getUid],
                  tid];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (BOOL) deleteContactMessages:(NSString *)uid {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE (%@ = ? AND %@ = ?) or (%@ = ? AND %@ = ?)",
                                          TABLE_NAME_MESSAGE,
                                          COLUMN_NAME_CURRENT_UID,
                                          COLUMN_NAME_CID,
                                          COLUMN_NAME_CID,
                                          COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid],
                  uid,
                  uid,
                  [BDSettings getUid]];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (BOOL) deleteGroupMessages:(NSString *)gid {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ? AND %@ = ?",
                                          TABLE_NAME_MESSAGE,
                                          COLUMN_NAME_CURRENT_UID,
                                          COLUMN_NAME_GID],
                  [BDSettings getUid],
                  gid];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}

- (BOOL) clearMessages {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                          TABLE_NAME_MESSAGE,
                                          COLUMN_NAME_CURRENT_UID
                                          ],
                  [BDSettings getUid]];
        
        if (!result) {
            NSLog(@"%s error = %@", __PRETTY_FUNCTION__, [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    return result;
}


- (BOOL) insertContact:(BDContactModel *)contact {
    
    // 暂不插入自己
    if ([contact.uid isEqualToString:[BDSettings getUid]]) {
        return FALSE;
    }
    
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@,%@,%@,%@) values(?,?,?,?,?,?,?,?);",
                                    TABLE_NAME_CONTACT,
                                    COLUMN_NAME_UID,
                                    COLUMN_NAME_CURRENT_UID,
                                    COLUMN_NAME_USERNAME,
                                    COLUMN_NAME_NICKNAME,
                                    COLUMN_NAME_REALNAME,
                                    COLUMN_NAME_AVATAR,
                                    COLUMN_NAME_EMAIL,
                                    COLUMN_NAME_MOBILE
                                          ],
                  contact.uid,
                  contact.current_uid,
                  contact.username,
                  contact.nickname,
                  contact.real_name,
                  contact.avatar,
                  contact.email,
                  contact.mobile
                  ];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}


- (NSMutableArray *) getContacts {
    //
    if ([self openFMDB]) {
        
        NSMutableArray *contactArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc", TABLE_NAME_CONTACT, COLUMN_NAME_CURRENT_UID, COLUMN_NAME_ID], [BDSettings getUid]];
            //
            while ([resultSet next]) {
                //
                BDContactModel *contact = [[BDContactModel alloc] init];
                //
                contact.uid = [resultSet stringForColumn:COLUMN_NAME_UID];
                contact.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                contact.username = [resultSet stringForColumn:COLUMN_NAME_USERNAME];
                contact.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                contact.real_name = [resultSet stringForColumn:COLUMN_NAME_REALNAME];
                contact.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                contact.email = [resultSet stringForColumn:COLUMN_NAME_EMAIL];
                contact.mobile = [resultSet stringForColumn:COLUMN_NAME_MOBILE];
//                contact.mdescription = [resultSet stringForColumn:COLUMN_NAME_DESCRIPTION];
//                contact.status = [resultSet stringForColumn:COLUMN_NAME_STATUS];
//                contact.roles = [resultSet stringForColumn:COLUMN_NAME_ROLES];
//                contact.workgroups = [resultSet stringForColumn:COLUMN_NAME_WORKGROUPS];
                //
                [contactArray addObject:contact];
            }
        }];
        return contactArray;
    }
    
    return false;
}

- (BOOL)clearContacts {
    
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                    TABLE_NAME_CONTACT,
                                    COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid]];
//
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }

    return result;
}

- (BOOL) insertGroup:(BDGroupModel *)group {
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@,%@,%@,%@,%@) values(?,?,?,?,?,?,?,?,?);",
                                          TABLE_NAME_GROUP,
                                          COLUMN_NAME_GID,
                                          COLUMN_NAME_NICKNAME,
                                          COLUMN_NAME_AVATAR,
                                          COLUMN_NAME_TYPE,
                                          COLUMN_NAME_MEMBER_COUNT,
                                          COLUMN_NAME_DESCRIPTION,
                                          COLUMN_NAME_ANNOUNCEMENT,
                                          COLUMN_NAME_IS_DISMISSED,
                                          COLUMN_NAME_CURRENT_UID
                                          ],
                  group.gid,
                  group.nickname,
                  group.avatar,
                  group.type,
                  group.member_count,
                  group.mdescription,
                  group.announcement,
                  group.is_dismissed,
                  group.current_uid
                  ];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (NSMutableArray *) getGroups {
    //
    if ([self openFMDB]) {
        
        NSMutableArray *groupArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc",
                                                             TABLE_NAME_GROUP,
                                                             COLUMN_NAME_CURRENT_UID,
                                                             COLUMN_NAME_ID],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                //
                BDGroupModel *group = [[BDGroupModel alloc] init];
                //
                group.gid = [resultSet stringForColumn:COLUMN_NAME_GID];
                group.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                group.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                group.type = [resultSet stringForColumn:COLUMN_NAME_TYPE];
                group.member_count = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_MEMBER_COUNT]];
                group.mdescription = [resultSet stringForColumn:COLUMN_NAME_DESCRIPTION];
                group.announcement = [resultSet stringForColumn:COLUMN_NAME_ANNOUNCEMENT];
                group.is_dismissed = [NSNumber numberWithInt:[resultSet intForColumn:COLUMN_NAME_IS_DISMISSED]];
                group.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                //
                [groupArray addObject:group];
            }
        }];
        return groupArray;
    }
    
    return false;
}

- (BOOL) updateThreadGroupNickname:(NSString *)gid withNickname:(NSString *)nickname {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_GROUP_GID, COLUMN_NAME_NICKNAME],
                  gid,
                  nickname];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateThreadUserNickname:(NSString *)uid withNickname:(NSString *)nickname {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_THREAD, COLUMN_NAME_CONTACT_UID, COLUMN_NAME_NICKNAME],
                  uid,
                  nickname];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateGroupNickname:(NSString *)gid withNickname:(NSString *)nickname {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_GROUP, COLUMN_NAME_GID, COLUMN_NAME_NICKNAME],
                  gid,
                  nickname];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) updateContactNickname:(NSString *)gid withNickname:(NSString *)nickname {
    //
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"update %@ set %@ = ? where %@ = ? ",
                                          TABLE_NAME_CONTACT, COLUMN_NAME_UID, COLUMN_NAME_NICKNAME],
                  gid,
                  nickname];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (BOOL) clearGroups {
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                          TABLE_NAME_GROUP,
                                          COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid]];
        //
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    
    return result;
}

- (BOOL) insertWorkGroup:(BDWorkGroupModel *)workGroup {
    BOOL result = false;
    if ([self openFMDB]) {
        //
        result = [self->db executeUpdate:[NSString stringWithFormat:@"replace into %@(%@,%@,%@,%@,%@) values(?,?,?,?,?);",
                                          TABLE_NAME_WORKGROUP,
                                          COLUMN_NAME_WID,
                                          COLUMN_NAME_NICKNAME,
                                          COLUMN_NAME_AVATAR,
                                          COLUMN_NAME_DESCRIPTION,
                                          COLUMN_NAME_CURRENT_UID
                                          ],
                  workGroup.wid,
                  workGroup.nickname,
                  workGroup.avatar,
                  workGroup.mdescription,
                  workGroup.current_uid
                  ];
        
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
    }
    return result;
}

- (NSMutableArray *) getWorkGroups {
    //
    if ([self openFMDB]) {
        
        NSMutableArray *workGroupArray = [[NSMutableArray alloc] init];
        [self->dbqueue inDatabase:^(FMDatabase *mydb) {
            FMResultSet *resultSet = [self->db executeQuery:[NSString stringWithFormat:@"select * from %@ where %@ = ? order by %@ desc",
                                                             TABLE_NAME_WORKGROUP,
                                                             COLUMN_NAME_CURRENT_UID,
                                                             COLUMN_NAME_ID],
                                      [BDSettings getUid]];
            //
            while ([resultSet next]) {
                //
                BDWorkGroupModel *workGroup = [[BDWorkGroupModel alloc] init];
                //
                workGroup.wid = [resultSet stringForColumn:COLUMN_NAME_WID];
                workGroup.nickname = [resultSet stringForColumn:COLUMN_NAME_NICKNAME];
                workGroup.avatar = [resultSet stringForColumn:COLUMN_NAME_AVATAR];
                
                workGroup.mdescription = [resultSet stringForColumn:COLUMN_NAME_DESCRIPTION];
                workGroup.current_uid = [resultSet stringForColumn:COLUMN_NAME_CURRENT_UID];
                //
                [workGroupArray addObject:workGroup];
            }
        }];
        return workGroupArray;
    }
    
    return false;
}

- (BOOL) clearWorkGroups {
    BOOL result = false;
    if ([self openFMDB]) {
        
        result = [self->db executeUpdate:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@ = ?",
                                          TABLE_NAME_WORKGROUP,
                                          COLUMN_NAME_CURRENT_UID],
                  [BDSettings getUid]];
        //
        if (!result) {
            NSLog(@"error = %@", [self->db lastErrorMessage]);
        }
        else {
            NSLog(@"%s delete success", __PRETTY_FUNCTION__);
        }
    }
    
    return result;
}


- (void) clearAll {
    
    [self clearMessages];
    [self clearThreads];
    [self clearQueues];
    [self clearContacts];
    [self clearWorkGroups];
    [self clearGroups];
}

@end












