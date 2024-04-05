//
//  KFDSChatViewController.m
//  bdui
//
//  Created by 萝卜丝 on 2018/11/29.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//
#import "BDChatKFViewController.h"
#import "BDMsgTableViewCell.h"
#import "BDMsgNotificationViewCell.h"
#import "BDMsgCommodityViewCell.h"
#import "BDConstants.h"
#import "BDRateViewController.h"
#import "BDLeaveMessageViewController.h"
#import "BDFaqViewController.h"
//#import "BDChatVideoViewController.h"
#import "BDUIUtils.h"
#import "BDVideoCompress.h"
#import "BDImageViewController.h"

#import "BDInputView.h"
#import "BDEmotionView.h"
#import "BDPlusView.h"
#import "BDRecordVoiceViewHUD.h"
#import "BDWBiCloudManager.h"

#import "BDUtils.h"
#import "BDCoreApis.h"
#import "BDUIApis.h"
#import "BDSettings.h"
#import "BDMQTTApis.h"
#import "BDDBApis.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import <SafariServices/SafariServices.h>

#import <AVFoundation/AVFoundation.h>

#define MaxSelectedImageCount 9
#define NormalImagePickingTag 1045
#define ModifiedImagePickingTag 1046
#define MultipleImagePickingTag 1047
#define SingleImagePickingTag 1048

#define INPUTBAR_HEIGHT                    60
#define EMOTION_PLUS_VIEW_HEIGHT           216.0f
#define VIEW_ANIMATION_DURATION            0.25f
#define TEXTBUBBLE_MAX_TEXT_WIDTH          180.0f
#define RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT 150.0f
#define TIMESTAMP_HEIGHT                   30.0f

//static CGFloat const kToolbarHeight = 60;
//static CGFloat const kEmotionViewHeight = 232;

@interface BDChatKFViewController ()<UITableViewDelegate, UITableViewDataSource,
//UINavigationControllerBackButtonHandlerProtocol,
BDMsgTableViewCellDelegate,
UIImagePickerControllerDelegate, UINavigationControllerDelegate,
UIDocumentPickerDelegate,
UIDocumentInteractionControllerDelegate,
BDMsgCommodityViewCellDelegate,
BDEmotionViewDelegate, KFPlusViewDelegate, BDInputViewDelegate>
{}

@property (nonatomic, strong) UITableView *mTableView;
@property (nonatomic, strong) NSDictionary *mEmotionToTextDictionary;

@property(nonatomic, assign) BOOL mIsPush;
@property(nonatomic, assign) BOOL mIsRobot;
@property(nonatomic, assign) BOOL mIsThreadClosed;
@property(nonatomic, assign) BOOL mIsInternetReachable;

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
@property(nonatomic, strong) NSMutableArray<BDMessageModel *> *mMessageArray;

//@property(nonatomic, assign) NSInteger mGetMessageFromChannelPage;

@property(nonatomic, strong) NSString *mTitle;
@property(nonatomic, strong) NSString *mUid; // visitorUid/cid/gid
@property(nonatomic, strong) NSString *mWorkGroupWid; // 工作组wid
@property(nonatomic, strong) NSString *mTid; // thread.tid
@property(nonatomic, strong) NSString *mAgentUid; // 指定坐席uid
@property(nonatomic, strong) NSString *mThreadType; // 区分客服会话thread、同事会话contact、群组会话group

@property(nonatomic, strong) BDThreadModel *mThreadModel;

@property(nonatomic, assign) NSInteger rateScore;
@property(nonatomic, strong) NSString *rateNote;
@property(nonatomic, assign) BOOL rateInvite;
// 本地存储的最老一条聊天记录，server_id最小的，时间戳最旧的
@property(nonatomic, assign) NSInteger mLastMessageId;

@property(nonatomic, assign) BOOL mWithCustomDict;
@property(nonatomic, strong) NSDictionary *mCustomDict;

@property (nonatomic, strong) UIImagePickerController  *mImagePickerController;

//
@property (nonatomic, strong) BDInputView               *mInputView;
@property (nonatomic, strong) BDEmotionView             *mEmotionView;
@property (nonatomic, strong) BDPlusView                *mPlusView;
@property (nonatomic, strong) BDRecordVoiceViewHUD      *mRecordVoiceViewHUD;

@property (nonatomic, assign) CGFloat                   inputViewY;
@property (nonatomic, assign) CGFloat                   keyboardY;
@property (nonatomic, assign) CGFloat                   keyboardHeight;

@property (nonatomic, assign) BOOL                      isEmotionPlusPressedToHideKeyboard;
@property (nonatomic, assign) BOOL                      isViewControllerClosed;
@property(nonatomic, assign) BOOL                       forceEnableBackGesture;

@property(nonatomic, strong) NSString *notifyIdentifier;
@property(nonatomic, strong) NSString *commodityIdentifier;
@property(nonatomic, strong) NSString *msgIdentifier;

@end

@implementation BDChatKFViewController

@synthesize mImagePickerController,
            mTableView,
            mInputView,
            mEmotionView,
            mPlusView,
            mRecordVoiceViewHUD,
            inputViewY,
            keyboardY,
            keyboardHeight,
            isEmotionPlusPressedToHideKeyboard;

#pragma mark - 公共函数

- (void)loadView {
    [super loadView];
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    self.view.backgroundColor = UIColor.clearColor;
    //
    CGRect tableFrame = CGRectMake(0, 0, CGRectGetWidth(self.view.bounds), CGRectGetHeight(self.view.bounds)); //  - kToolbarHeight
    self.mTableView = [[UITableView alloc] initWithFrame:tableFrame];
    self.mTableView.separatorColor = [UIColor clearColor];
    self.mTableView.backgroundColor = [UIColor systemGroupedBackgroundColor];
    self.mTableView.delegate = self;
    self.mTableView.dataSource = self;
    //
    UIEdgeInsets tableViewInsets = self.mTableView.contentInset;
    tableViewInsets.bottom = INPUTBAR_HEIGHT * 2;
    self.mTableView.contentInset = tableViewInsets;
    self.mTableView.scrollIndicatorInsets = tableViewInsets;
    [self.view addSubview:self.mTableView];
    //
//    self.mGetMessageFromChannelPage = 0;
    UITapGestureRecognizer *singleFingerTap =
    [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    [self.mTableView addGestureRecognizer:singleFingerTap];
    //输入框Toolbar
    CGRect inputViewFrame = CGRectMake(0.0f, self.view.frame.size.height - INPUTBAR_HEIGHT, self.view.frame.size.width, INPUTBAR_HEIGHT);
    self.mInputView = [[BDInputView alloc] initWithFrame:inputViewFrame];
    self.mInputView.delegate = self;
    [self.view addSubview:self.mInputView];
    //
    self.mEmotionToTextDictionary = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle bundleForClass:self.class] pathForResource:@"EmotionToText" ofType:@"plist"]];
    //
    // FIXME: 加载大量图片容易引起界面卡顿，待优化
    CGRect recordVoiceViewFrame = CGRectMake((self.view.frame.size.width - RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT)/2,
                                             (self.view.frame.size.height - RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT)/2,
                                             RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT,
                                             RECORD_VOICE_VIEW_HUD_WIDTH_HEIGHT);
    self.mRecordVoiceViewHUD = [[BDRecordVoiceViewHUD alloc] initWithFrame:recordVoiceViewFrame];
    [self.view addSubview:self.mRecordVoiceViewHUD];
    self.mRecordVoiceViewHUD.hidden = TRUE;
    //
    CGRect emotionViewFrame = CGRectMake(0.0f, self.view.frame.size.height, self.view.frame.size.width, EMOTION_PLUS_VIEW_HEIGHT);
    self.mEmotionView = [[BDEmotionView alloc] initWithFrame:emotionViewFrame];
    self.mEmotionView.delegate = self;
    [self.view addSubview:self.mEmotionView];
    //
    CGRect plusViewFrame = emotionViewFrame;
    self.mPlusView = [[BDPlusView alloc] initWithFrame:plusViewFrame];
    self.mPlusView.delegate = self;
    [self.view addSubview:self.mPlusView];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.view.backgroundColor = [UIColor clearColor];
    self.title = self.mThreadModel ? self.mThreadModel.nickname : self.mTitle;
//    self.parentView = self.navigationController.view;
//    [self.view setQmui_shouldShowDebugColor:YES];
    // 方便uiTableviewcell复用
    self.notifyIdentifier = @"notifyCell";
    self.commodityIdentifier = @"commodityCell";
    self.msgIdentifier = @"msgCell";
    [self.mTableView registerClass:[BDMsgNotificationViewCell class] forCellReuseIdentifier:self.notifyIdentifier];
    [self.mTableView registerClass:[BDMsgCommodityViewCell class] forCellReuseIdentifier:self.commodityIdentifier];
    [self.mTableView registerClass:[BDMsgTableViewCell class] forCellReuseIdentifier:self.msgIdentifier];
    //
    if (self.mIsPush) {
//        self.navigationItem.leftBarButtonItem = [UIBarButtonItem qmui_backItemWithTarget:self action:@selector(handleBackButtonEvent:)];// 自定义返回按钮要自己写代码去 pop 界面
    }
    else {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemClose target:self action:@selector(handleCloseButtonEvent:)];
    }
    // 当系统的返回按钮被屏蔽的时候，系统的手势返回也会跟着失效，所以这里要手动强制打开手势返回
    self.forceEnableBackGesture = YES;
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.mTableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshMessages) forControlEvents:UIControlEventValueChanged];
    //
    self.mImagePickerController = [[UIImagePickerController alloc] init];
    self.mImagePickerController.delegate = self;
    //
    if (![BDSettings isAlreadyLogin]) {
        [BDUIApis showErrorWithVC:self withMessage:@"未初始化，请首先初始化"];
        return;
    }
    //
    [self registerNotifications];
    // 加载本地聊天记录
    [self reloadTableData];
    // 从服务器加载聊天记录, 暂时不从服务器加载
    // [self refreshMessages];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear: animated];
    [BDSettings setCurrentTid:@""];
}

#pragma mark - 初始化

- (void)initWithWorkGroupWid:(NSString *)wId withTitle:(NSString *)title withPush:(BOOL)isPush {
    // titleView状态：1. 连接中...(发送请求到服务器，进入队列)，2. 排队中...(队列中等待客服接入会话), 3. 接入会话（一闪而过）
    self.mIsRobot = NO;
    self.mIsPush = isPush;
    self.mIsThreadClosed = NO;
//    self.titleView.needsLoadingView = NO;
//    self.titleView.loadingViewHidden = YES;
    self.mTitle = title;
    self.navigationItem.title = title;
//    self.titleView.title = title;
//    self.titleView.subtitle = @"连接中...";
//    self.titleView.style = QMUINavigationTitleViewStyleSubTitleVertical;
    //
    self.mWorkGroupWid = wId;
    self.mUid = wId;
    self.mThreadModel = [[BDThreadModel alloc] init];
    self.mThreadType = BD_THREAD_TYPE_WORKGROUP;
    //
    self.rateScore = 5;
    self.rateNote = @"";
    self.rateInvite = false;
    self.mLastMessageId = INT_MAX;
    //
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage systemImageNamed:@"ellipsis"] style:UIBarButtonItemStylePlain target:self action:@selector(handleRightBarButtonItemClicked:)];
    //
    [BDCoreApis requestThreadWithWorkGroupWid:wId resultSuccess:^(NSDictionary *dict) {
        [self dealWithRequestThreadResult:dict];
    } resultFailed:^(NSError *error) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

- (void)initWithWorkGroupWid:(NSString *)wId withTitle:(NSString *)title withPush:(BOOL)isPush withCustom:(NSDictionary *)custom {
    //
    self.mWithCustomDict = TRUE;
    self.mCustomDict = custom;
    //
    [self initWithWorkGroupWid:wId withTitle:title withPush:isPush];
}


- (void)initWithAgentUid:(NSString *)uId withTitle:(NSString *)title withPush:(BOOL)isPush {
    //
    self.mIsRobot = NO;
    self.mIsPush = isPush;
    self.mIsThreadClosed = NO;
//    self.titleView.needsLoadingView = YES;
//    self.titleView.loadingViewHidden = NO;
    self.mTitle = title;
//    self.titleView.title = title;
//    self.titleView.subtitle = @"连接中...";
//    self.titleView.style = QMUINavigationTitleViewStyleSubTitleVertical;
    //
    self.mAgentUid = uId;
    self.mUid = uId;
    self.mThreadModel = [[BDThreadModel alloc] init];
    self.mThreadType = BD_THREAD_TYPE_APPOINTED;
    //
    self.rateScore = 5;
    self.rateNote = @"";
    self.rateInvite = false;
    self.mLastMessageId = INT_MAX;
    //
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage systemImageNamed:@"ellipsis"] style:UIBarButtonItemStylePlain target:self action:@selector(handleRightBarButtonItemClicked:)];
    //
    [BDCoreApis requestThreadWithAgentUid:uId resultSuccess:^(NSDictionary *dict) {
        //
        [self dealWithRequestThreadResult:dict];
    } resultFailed:^(NSError *error) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}


- (void) initWithAgentUid:(NSString *)uId withTitle:(NSString *)title withPush:(BOOL)isPush withCustom:(NSDictionary *)custom {
    //
    self.mWithCustomDict = TRUE;
    self.mCustomDict = custom;
    //
    [self initWithAgentUid:uId withTitle:title withPush:isPush];
}

/**
 * 返回结果代码：
 *
 * 200：请求会话成功-创建新会话
 * 201：请求会话成功-继续进行中会话
 * 202：请求会话成功-排队中
 * 203：请求会话成功-当前非工作时间，请自助查询或留言
 * 204：请求会话成功-当前无客服在线，请自助查询或留言
 *
 * -1: 请求会话失败-access token无效
 * -2：请求会话失败-wId不存在
 */
- (void)dealWithRequestThreadResult:(NSDictionary *)dict {
    // 如果点击了左上角返回或关闭按钮之后，网络请求才返回m，则不需要继续处理此返回结果
    if (self.isViewControllerClosed) {
        return;
    }
    //
    NSString *message = [dict objectForKey:@"message"];
    NSNumber *status_code = [dict objectForKey:@"status_code"];
    NSLog(@"%s message:%@, status_code:%@", __PRETTY_FUNCTION__, message, status_code);
    self.mIsRobot = FALSE;
    [self.mInputView switchToAgent];
    [self.mPlusView switchToAgent];
    // 解析数据
    self.mThreadModel = [[BDThreadModel alloc] initWithKeFuRequestDictionary:dict[@"data"][@"thread"]];
    self.mTid = self.mThreadModel.tid;
    // 修改UI界面
    NSNumber *appointed = dict[@"data"][@"thread"][@"appointed"];
    if ([appointed boolValue]) {
        self.navigationItem.title = dict[@"data"][@"thread"][@"agent"][@"nickname"];
    } else {
        self.navigationItem.title = dict[@"data"][@"thread"][@"workGroup"][@"nickname"];
    }
    //
    if ([status_code isEqualToNumber:[NSNumber numberWithInt:206]]) {
        // 保存聊天记录
        BDMessageModel *messageModel = [[BDMessageModel alloc] initWithRobotDictionary:dict[@"data"]];
        [[BDDBApis sharedInstance] insertMessage:messageModel];
        [self reloadTableData];
    } else {
        // 保存聊天记录
        BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:dict[@"data"]];
        [[BDDBApis sharedInstance] insertMessage:messageModel];
        [self reloadTableData];
    }
    //
    if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]] ||
        [status_code isEqualToNumber:[NSNumber numberWithInt:201]]) {
        // 创建新会话 / 继续进行中会话

        // TODO: 发送商品信息
        if (self.mWithCustomDict) {
            NSString *customJson = [BDUtils dictToJson:self.mCustomDict];
            [self sendCommodityMessage:customJson];
        }
        
    } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:202]]) {
        // 提示排队中

        
    } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:203]]) {
        // 当前非工作时间，请自助查询或留言
        
        // 跳转留言页面
        [self shareLeaveMsgButtonPressed:nil];
        
    } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:204]]) {
        // 当前无客服在线，请自助查询或留言

        // 跳转留言页面
        [self shareLeaveMsgButtonPressed:nil];
        
    } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:205]]) {
        // 咨询前问卷
        // 修改UI界面
//        self.titleView.title = dict[@"data"][@"thread"][@"workGroup"][@"nickname"];
////        self.titleView.subtitle = dict[@"message"];
//        self.titleView.needsLoadingView = NO;
//
    } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:206]]) {
        // 返回机器人初始欢迎语 + 欢迎问题列表
        NSLog(@"robot dict %@", dict);
        self.mIsRobot = YES;
        
        // 切换到机器人模式
        [self.mInputView switchToRobot];
        [self.mPlusView switchToRobot];
        
    } else {
        // 请求会话失败
        [BDUIApis showErrorWithVC:self withMessage:dict[@"message"]];
    }
}

//- (void)setNavigationItemsIsInEditMode:(BOOL)isInEditMode animated:(BOOL)animated {
////    [super setNavigationItemsIsInEditMode:isInEditMode animated:animated];
//}

// 针对Present打开模式，左上角返回按钮处理action
- (void)handleCloseButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.isViewControllerClosed = YES;
    [self.navigationController dismissViewControllerAnimated:YES completion:^{
    }];
}

// 针对Push打开模式，左上角返回按钮处理action
- (void)handleBackButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.isViewControllerClosed = YES;
    [self.navigationController popViewControllerAnimated:YES];
}

- (BOOL)forceEnableInteractivePopGestureRecognizer {
    return self.forceEnableBackGesture;
}

- (void)handleRightBarButtonItemClicked:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    UIAlertController *actionSheet = [UIAlertController alertControllerWithTitle:@"提示" message:@"" preferredStyle:UIAlertControllerStyleActionSheet];
    [actionSheet addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        // Cancel button tappped.
        [self dismissViewControllerAnimated:YES completion:^{
        }];
    }]];
    [actionSheet addAction:[UIAlertAction actionWithTitle:@"清空聊天记录" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        //
        if ([self.mThreadType isEqualToString:BD_THREAD_REQUEST_TYPE_APPOINTED]) {
            NSLog(@"清空指定坐席 %@", self.mTid);
            [BDCoreApis clearMessagesWithThread:self.mTid];
        } else {
            NSLog(@"清空聊天记录-工作组 %@", self.mWorkGroupWid);
            [BDCoreApis clearMessagesWithWorkGroup:self.mWorkGroupWid];
        }
        [self reloadTableData];
//        //
//        [self dismissViewControllerAnimated:YES completion:^{
//        }];
    }]];
    // Present action sheet.
    [self presentViewController:actionSheet animated:YES completion:nil];
}

#pragma mark - tabview回调

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self.mMessageArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    //
    BDMessageModel *messageModel = [self.mMessageArray objectAtIndex:indexPath.row];
    if ([messageModel isNotification]) {
        //        NSLog(@"通知 type: %@, content: %@", messageModel.type, messageModel.content);
        BDMsgNotificationViewCell *cell = [tableView dequeueReusableCellWithIdentifier:self.notifyIdentifier];
        if (!cell) {
            cell = [[BDMsgNotificationViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:self.notifyIdentifier];
        }
        [cell initWithMessageModel:messageModel];
        cell.tag = indexPath.row;
        // 存储id最小的
        if ([messageModel.server_id integerValue] < self.mLastMessageId) {
            self.mLastMessageId = [messageModel.server_id integerValue];
        }
        //        NSLog(@"server_id: %@, lastMessageId: %li", messageModel.server_id, (long)self.mLastMessageId);
        //
        return cell;
    } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_COMMODITY]) {
        //        NSLog(@"商品 type: %@, content: %@", messageModel.type, messageModel.content);
        BDMsgCommodityViewCell *cell = [tableView dequeueReusableCellWithIdentifier:self.commodityIdentifier];
        if (!cell) {
            cell = [[BDMsgCommodityViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:self.commodityIdentifier];
        }
        [cell initWithMessageModel:messageModel];
        cell.tag = indexPath.row;
        cell.delegate = self;
        // 存储id最小的
        if ([messageModel.server_id integerValue] < self.mLastMessageId) {
            self.mLastMessageId = [messageModel.server_id integerValue];
        }
        //        NSLog(@"server_id: %@, lastMessageId: %li", messageModel.server_id, (long)self.mLastMessageId);
        //
        return cell;
    } else {
        //
        BDMsgTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:self.msgIdentifier];
        if (!cell) {
            cell = [[BDMsgTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:self.msgIdentifier];
            cell.delegate = self;
        }
        //
        [cell initWithMessageModel:messageModel];
        cell.tag = indexPath.row;
        // 存储id最小的
        if ([messageModel.server_id integerValue] < self.mLastMessageId) {
            self.mLastMessageId = [messageModel.server_id integerValue];
        }
        cell.delegate = self;
//      NSLog(@"server_id: %@, lastMessageId: %li", messageModel.server_id, (long)self.mLastMessageId);
        //
        return cell;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    //
    CGFloat height = 0.0;
    //
    BDMessageModel *messageModel = [self.mMessageArray objectAtIndex:indexPath.row];
    if ([messageModel isNotification]) {
        height = 55;
    } else {
        //
        if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
            //
            height = messageModel.contentSize.height + messageModel.contentViewInsets.top + messageModel.contentViewInsets.bottom + TIMESTAMP_HEIGHT;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
            CGSize size = [BDUIUtils sizeOfRobotContentAttr:messageModel.contentAttr];
            height = size.height + messageModel.contentViewInsets.top + messageModel.contentViewInsets.bottom + TIMESTAMP_HEIGHT;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_IMAGE]) {
            height = 280;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_VOICE]) {
            height = 90;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_FILE]) {
            height = 100;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_VIDEO]) {
            height = 120;
        } else if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_COMMODITY]) {
            height = 100;
        } else {
            height = 80;
        }
//        NSLog(@"%s, type: %@, height: %f", __PRETTY_FUNCTION__, messageModel.type, height);
    }
    //
    return height;
}

-(void)tableViewScrollToBottom:(BOOL)animated {
//    NSLog(@"tableViewScrollToBottom");
    NSInteger rows = [self.mTableView numberOfRowsInSection:0];
    if(rows > 0) {
        [self.mTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:rows - 1 inSection:0]
                              atScrollPosition:UITableViewScrollPositionBottom
                                      animated:animated];
    }
}


#pragma mark - 加载本地数据库聊天记录

- (void)reloadTableData {
    //
    if ([self.mThreadType isEqualToString:BD_THREAD_REQUEST_TYPE_APPOINTED]) {
        NSLog(@"1. 访客端获取聊天记录: 指定坐席 %@", self.mTid);
        self.mMessageArray = [BDCoreApis getMessagesWithThread:self.mTid];
    } else {
        NSLog(@"1. 访客端获取聊天记录：工作组 %@", self.mWorkGroupWid);
        self.mMessageArray = [BDCoreApis getMessagesWithWorkGroup:self.mWorkGroupWid];
    }
    //
    for (int i = 0; i < self.mMessageArray.count; i++) {
        BDMessageModel *messageModel = [self.mMessageArray objectAtIndex:i];
        if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
            messageModel.contentAttr = [BDUtils transformContentToContentAttr:messageModel.content];
        }
        self.mMessageArray[i] = messageModel;
    }
    // 刷新tableView
    [self.mTableView reloadData];
    [self tableViewScrollToBottom:NO];
}

- (void)reloadCellDataStatus:(NSString *)localId status:(NSString *)status {
    for (int i = 0; i < [self.mMessageArray count]; i++) {
        BDMessageModel *message = [self.mMessageArray objectAtIndex:i];
        if (![message.local_id isKindOfClass:[NSNull class]] &&
            [message.local_id isEqualToString:localId]) {
            // 更新内存数据 BD_MESSAGE_STATUS_STORED
            // 如果已经设置为read则直接返回
            if ([message.status isEqualToString:BD_MESSAGE_STATUS_READ]) {
                return;
            }
            message.status = status;
            // 更新UI
            NSIndexPath *reloadIndexPath = [NSIndexPath indexPathForRow:i inSection:0];
            [self.mTableView beginUpdates];
            [self.mTableView reloadRowsAtIndexPaths:@[reloadIndexPath] withRowAnimation:UITableViewRowAnimationNone];
            [self.mTableView endUpdates];
        }
    }
}

- (void)reloadCellDataError:(NSString *)localId {
    for (int i = 0; i < [self.mMessageArray count]; i++) {
        BDMessageModel *message = [self.mMessageArray objectAtIndex:i];
        if (![message.local_id isKindOfClass:[NSNull class]] &&
            [message.local_id isEqualToString:localId]) {
            // 更新内存数据
            message.status = BD_MESSAGE_STATUS_ERROR;
            // 更新UI
            NSIndexPath *reloadIndexPath = [NSIndexPath indexPathForRow:i inSection:0];
            [self.mTableView beginUpdates];
            [self.mTableView reloadRowsAtIndexPaths:@[reloadIndexPath] withRowAnimation:UITableViewRowAnimationNone];
            [self.mTableView endUpdates];
        }
    }
}

- (void)updateCurrentThread {
    NSString *preTid = [BDSettings getCurrentTid];
    [BDCoreApis updateCurrentThread:preTid currentTid:self.mTid resultSuccess:^(NSDictionary *dict) {
        [BDSettings setCurrentTid:self.mTid];
    } resultFailed:^(NSError *error) {
        NSLog(@"updateCurrentThread %@", error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

#pragma mark - UINavigationControllerBackButtonHandlerProtocol 拦截退出界面

- (BOOL)shouldHoldBackButtonEvent {
    return YES;
}

- (BOOL)canPopViewController {
    // 这里不要做一些费时的操作，否则可能会卡顿。
    self.isViewControllerClosed = YES;
    [BDCoreApis cancelAllHttpRequest];
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self unregisterNotifications];
    // 保存草稿
//    return NO; //拦截，不能退出界面
    return YES; //
}

#pragma mark - 通知订阅 Notifications

- (void)registerNotifications {
    
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWillShowKeyboard:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWillHideKeyboard:) name:UIKeyboardWillHideNotification object:nil];
    //
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyQueueAccept:) name:BD_NOTIFICATION_QUEUE_ACCEPT object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyThreadClose:) name:BD_NOTIFICATION_THREAD_CLOSE object:nil];
    //
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyReloadCellStatus:) name:BD_NOTIFICATION_MESSAGE_LOCALID object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessageAdd:) name:BD_NOTIFICATION_MESSAGE_ADD object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessageDelete:) name:BD_NOTIFICATION_MESSAGE_DELETE object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessagePreview:) name:BD_NOTIFICATION_MESSAGE_PREVIEW object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessageRecall:) name:BD_NOTIFICATION_MESSAGE_RECALL object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyMessageStatus:) name:BD_NOTIFICATION_MESSAGE_STATUS object:nil];
    //
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notifyKickoff:) name:BD_NOTIFICATION_KICKOFF object:nil];

}

- (void)unregisterNotifications {
    
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

#pragma mark - KFDSInputViewDelegate

-(void)textFieldDidChange :(UITextField *)textField{
    NSString *content = textField.text;
//    NSLog( @"text changed: %@", content);
    // 发送消息预知
    [[BDMQTTApis sharedInstance] sendPreviewMessageProtobufThread:self.mThreadModel previewContent:content];
}

#pragma mark - 发送消息

// TODO: 区分发送消息
-(void)sendTextMessage:(NSString *)content {
    
    // 增加判断content长度，限制<512
    if ([content length] >= 500) {
        [BDUIApis showErrorWithVC:self withMessage:@"消息太长，请分多次发送"];
        return;
    }
    
    NSString *localId = [self insertLocalMessageAndReload:content withType:BD_MESSAGE_TYPE_TEXT withSend:YES];
    // 异步发送消息
    [[BDMQTTApis sharedInstance] sendTextMessageProtobuf:localId content:content thread:self.mThreadModel];
}

//
-(void)sendImageMessage:(NSString *)imageUrl {
    
    NSString *localId = [BDUtils getGuid];
    // 插入本地消息
    BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertImageMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:imageUrl withLocalId:localId withSessionType:self.mThreadType withSend:YES];
    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.image_url);
    
    // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
    [self.mMessageArray addObject:messageModel];
    
    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    [self tableViewScrollToBottom:YES];
    //
    [[BDMQTTApis sharedInstance] sendImageMessageProtobuf:localId content:imageUrl thread:self.mThreadModel];
}

-(void)sendFileMessage:(NSString *)fileUrl {
    
    NSString *localId = [BDUtils getGuid];
    // 插入本地消息
    BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertFileMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:fileUrl withLocalId:localId withSessionType:self.mThreadType withFormat:@"" withFileName:@"" withFileSize:@"" withSend:YES];
    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.image_url);
    
    // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
    [self.mMessageArray addObject:messageModel];
    
    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    [self tableViewScrollToBottom:YES];
    //
    [[BDMQTTApis sharedInstance] sendFileMessageProtobuf:localId content:fileUrl thread:self.mThreadModel];
}

-(void)sendVoiceMessage:(NSString *)voiceUrl {
    
    NSString *localId = [BDUtils getGuid];
    // 插入本地消息
    BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertVoiceMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:voiceUrl withLocalId:localId withSessionType:self.mThreadType withVoiceLength:0 withFormat:@"" withSend:YES];
    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.image_url);
    
    // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
    [self.mMessageArray addObject:messageModel];
    
    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    [self tableViewScrollToBottom:YES];
    //
    [[BDMQTTApis sharedInstance] sendVoiceMessageProtobuf:localId content:voiceUrl thread:self.mThreadModel];
}

-(void)sendVideoMessage:(NSString *)videoUrl {
    
    NSString *localId = [BDUtils getGuid];
    // 插入本地消息
    BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertVideoMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:videoUrl withLocalId:localId withSessionType:self.mThreadType withSend:YES];
    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.image_url);
    
    // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
    [self.mMessageArray addObject:messageModel];
    
    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    [self tableViewScrollToBottom:YES];
    //
    [[BDMQTTApis sharedInstance] sendVideoMessageProtobuf:localId content:videoUrl thread:self.mThreadModel];
}

- (void)sendRobotMessage:(NSString *)content {
    
    // TODO: 插入本地消息，显示发送状态
    
    // 请求机器人问答
    [BDCoreApis messageAnswer:self.mWorkGroupWid withMessage:content resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            // 成功查询到答案
            NSDictionary *queryMessageDict = dict[@"data"][@"query"];
            NSDictionary *replyMessageDict = dict[@"data"][@"reply"];
            //
            BDMessageModel *queryMessageModel = [[BDMessageModel alloc] initWithDictionary:queryMessageDict];
            [[BDDBApis sharedInstance] insertMessage:queryMessageModel];
            //
            BDMessageModel *replyMessageModel = [[BDMessageModel alloc] initWithRobotRightAnswerDictionary:replyMessageDict];
            [[BDDBApis sharedInstance] insertMessage:replyMessageModel];
            
        } else if ([status_code isEqualToNumber:[NSNumber numberWithInt:201]]) {
            // 未匹配到答案
            NSDictionary *queryMessageDict = dict[@"data"][@"query"];
            NSDictionary *replyMessageDict = dict[@"data"][@"reply"];
            //
            BDMessageModel *queryMessageModel = [[BDMessageModel alloc] initWithDictionary:queryMessageDict];
            [[BDDBApis sharedInstance] insertMessage:queryMessageModel];
            //
            BDMessageModel *replyMessageModel = [[BDMessageModel alloc] initWithRobotNoAnswerDictionary:replyMessageDict];
            [[BDDBApis sharedInstance] insertMessage:replyMessageModel];
        } else {
            //
            NSString *message = dict[@"message"];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, message);
            [BDUIApis showErrorWithVC:self withMessage:message];
        }
        //
        [self reloadTableData];
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

- (void)queryRobotAnswer:(NSString *)aid {
    
    // TODO: 插入本地消息，显示发送状态
    //
    [BDCoreApis queryAnswer:self.mTid withQuestinQid:aid resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            NSDictionary *queryMessageDict = dict[@"data"][@"query"];
            NSDictionary *replyMessageDict = dict[@"data"][@"reply"];
            
            //
            BDMessageModel *queryMessageModel = [[BDMessageModel alloc] initWithDictionary:queryMessageDict];
            [[BDDBApis sharedInstance] insertMessage:queryMessageModel];
            
            //
            BDMessageModel *replyMessageModel = [[BDMessageModel alloc] initWithRobotRightAnswerDictionary:replyMessageDict];
            [[BDDBApis sharedInstance] insertMessage:replyMessageModel];
            
        } else {
            //
            NSString *message = dict[@"message"];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, message);
            [BDUIApis showErrorWithVC:self withMessage:message];
        }
        //
        [self reloadTableData];
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

- (void)rateRobotAnswer:(NSString *)aid withMid:(NSString *)mid withRate:(BOOL)rate {
    //
    [BDCoreApis rateAnswer:aid withMessageMid:mid withRate:rate resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
//            NSDictionary *messageDict = dict[@"data"];
//            BDMessageModel *messageModel = [[BDMessageModel alloc] initWithDictionary:messageDict];
//            [[BDDBApis sharedInstance] insertMessage:messageModel];
        } else {
            //
            NSString *message = dict[@"message"];
            NSLog(@"%s %@", __PRETTY_FUNCTION__, message);
            [BDUIApis showErrorWithVC:self withMessage:message];
        }
        //
        [self reloadTableData];
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

- (void)requestAgent {
    [BDUIApis showTipWithVC:self withMessage:@"转接人工客服中..."];
    
    // 请求人工客服
    // TODO: mTidOrUidOrGid 替换为 agentUid, agentUid不能为空
    [BDCoreApis requestAgent:self.mWorkGroupWid resultSuccess:^(NSDictionary *dict) {
        
        [self dealWithRequestThreadResult:dict];
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

//
- (void)uploadVideoData:(NSData *)videoData {
    // TODO: 限制视频大小、压缩视频
    // TODO: 显示上传进度
    [self startLoadingWithText:@"上传中..."];
    
    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid];
    NSString *videoName = [NSString stringWithFormat:@"%@_%@.mp4", [BDUtils getCurrentTimeString], [BDSettings getUsername]];
    [BDCoreApis uploadVideoData:videoData withVideoName:videoName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
        [self stopLoading];
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            NSString *videoUrl = dict[@"data"];
            
            // 插入本地消息
            BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertVideoMessageLocal:self.mTid withWorkGroupWid:self.mWorkGroupWid withContent:videoUrl withLocalId:localId withSessionType:self.mThreadType withSend:YES];
            NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.video_or_short_url);
            
            // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
            NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
            [self.mMessageArray addObject:messageModel];
            
            [self.mTableView beginUpdates];
            [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
            [self.mTableView endUpdates];
            [self tableViewScrollToBottom:YES];
            
            // 异步发送视频消息
            [[BDMQTTApis sharedInstance] sendVideoMessageProtobuf:localId content:videoUrl thread:self.mThreadModel];
            
        } else {
            [BDUIApis showErrorWithVC:self withMessage:@"发送视频错误"];
        }
        
    } resultFailed:^(NSError *error) {
        [self stopLoading];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
    }];
}

// 上传并发送图片
- (void)uploadImageData:(NSData *)imageData {
    // TODO: 显示上传进度
    [self startLoadingWithText:@"上传中..."];
    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid];
    NSString *imageName = [NSString stringWithFormat:@"%@_%@.png", [BDUtils getCurrentTimeString], [BDSettings getUsername]];
    //
    [BDCoreApis uploadImageData:imageData withImageName:imageName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
        [self stopLoading];
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            NSString *imageUrl = dict[@"data"];
            
            // 插入本地消息
            BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertImageMessageLocal:self.mTid withWorkGroupWid:self.mWorkGroupWid withContent:imageUrl withLocalId:localId withSessionType:self.mThreadType withSend:YES];
            NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.image_url);
            
            // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
            NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
            [self.mMessageArray addObject:messageModel];
            
            [self.mTableView beginUpdates];
            [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
            [self.mTableView endUpdates];
            [self tableViewScrollToBottom:YES];
            
            // 异步发送图片消息
//            [[BDMQTTApis sharedInstance] sendImageMessageProtobuf:localId content:imageUrl
//            tid:self.mTidOrUidOrGid topic:self.mThreadModel.topic threadType:self.mThreadType threadNickname:self.mThreadModel.nickname threadAvatar:self.mThreadModel.avatar];
            [[BDMQTTApis sharedInstance] sendImageMessageProtobuf:localId content:imageUrl thread:self.mThreadModel];

        } else {
            [BDUIApis showErrorWithVC:self withMessage:@"发送图片错误"];
        }
        
    } resultFailed:^(NSError *error) {
        [self stopLoading];
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
    }];
}

- (void)uploadAmrVoice:(NSString *)amrVoiceFileName voiceLength:(int)voiceLength {
    
    NSString *amrVoiceFilePath = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), amrVoiceFileName];
    NSData *voiceData = [NSData dataWithContentsOfFile:amrVoiceFilePath];
//    NSLog(@"amrVoiceFileName: %@", amrVoiceFilePath);

    // TODO: 语音发送之后，上传成功之前，增加发送语音消息气泡

    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid];
    [BDCoreApis uploadVoiceData:voiceData withVoiceName:amrVoiceFileName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
        
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
            NSString *localId = [BDUtils getGuid];
            NSString *voiceUrl = dict[@"data"];
            
            // 插入本地消息
            BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertVoiceMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:voiceUrl withLocalId:localId withSessionType:self.mThreadType withVoiceLength:voiceLength  withFormat:@"amr" withSend:YES];
            NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.voice_url);
            
            // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
            NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
            [self.mMessageArray addObject:messageModel];
            //
            [self.mTableView beginUpdates];
            [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
            [self.mTableView endUpdates];
            [self tableViewScrollToBottom:YES];
            
            //
            [[BDMQTTApis sharedInstance] sendVoiceMessageProtobuf:localId content:voiceUrl thread:self.mThreadModel];
//            [[BDMQTTApis sharedInstance] sendVoiceMessageProtobuf:localId content:voiceUrl
//            tid:self.mUid topic:self.mThreadModel.topic threadType:self.mThreadType threadNickname:self.mThreadModel.nickname threadAvatar:self.mThreadModel.avatar];
            
        } else {
            [BDUIApis showErrorWithVC:self withMessage:@"发送录音错误"];
        }
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}


// 发送商品消息
-(void)sendCommodityMessage:(NSString *)content {
    NSLog(@"%s, content:%@, tid:%@, sessionType:%@ ", __PRETTY_FUNCTION__, content, self.mTid,  self.mThreadType);
    
//    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid];
//
    // 插入本地消息, 可通过返回的messageModel首先更新本地UI，然后再发送消息
    BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertCommodityMessageLocal:self.mTid withWorkGroupWid:self.mWorkGroupWid withContent:content withLocalId:localId withSessionType:self.mThreadType withSend:YES];
//    NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.content);

    // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
    [self.mMessageArray addObject:messageModel];

    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    [self tableViewScrollToBottom:YES];
    
    //
    [[BDMQTTApis sharedInstance] sendCommodityMessageProtobuf:localId content:content thread:self.mThreadModel];
}

#pragma mark - BDMsgViewCellDelegate

- (void)saveImageCellWith:(NSInteger)tag {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    图片保存到相册
//    UIImageWriteToSavedPhotosAlbum(self.qrImageView.image, self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
}

//图片保存到相册-回调
-(void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if (!error) {
        [BDUIApis showTipWithVC:self withMessage:@"已保存到相册"];
    } else {
        [BDUIApis showErrorWithVC:self withMessage:@"保存失败"];
    }
}

- (void)removeCellWith:(NSInteger)tag {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    UIAlertController * alert = [UIAlertController
                                 alertControllerWithTitle:@""
                                 message:@"确定要删除"
                                 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction* yesButton = [UIAlertAction
                                actionWithTitle:@"确定"
                                style:UIAlertActionStyleDefault
                                handler:^(UIAlertAction * action) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:tag inSection:0];
        if (indexPath.row < [self.mMessageArray count]) {
            BDMessageModel *itemToDelete = [self.mMessageArray objectAtIndex:indexPath.row];
            //
            [BDCoreApis markDeletedMessage:itemToDelete.mid resultSuccess:^(NSDictionary *dict) {
                //
                [self.mMessageArray removeObjectAtIndex:indexPath.row];
                [self.mTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
                
            } resultFailed:^(NSError *error) {
                [BDUIApis showErrorWithVC:self withMessage:@"删除失败"];
            }];
        }
    }];
    
    UIAlertAction* cancelButton = [UIAlertAction
                                   actionWithTitle:@"取消"
                                   style:UIAlertActionStyleDefault
                                   handler:^(UIAlertAction * action) {
    }];
    [alert addAction:yesButton];
    [alert addAction:cancelButton];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)recallCellWith:(NSInteger)tag {
    //
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:tag inSection:0];
    if (indexPath.row < [self.mMessageArray count]) {
        BDMessageModel *itemToDelete = [self.mMessageArray objectAtIndex:indexPath.row];
        //
        [BDCoreApis markDeletedMessage:itemToDelete.mid resultSuccess:^(NSDictionary *dict) {
            //
            [self.mMessageArray removeObjectAtIndex:indexPath.row];
            [self.mTableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            //
            [[BDMQTTApis sharedInstance] sendRecallMessageProtobufThread:self.mThreadModel recallMid:itemToDelete.mid];
            
        } resultFailed:^(NSError *error) {
            [BDUIApis showErrorWithVC:self withMessage:@"撤回失败"];
        }];
    }
}

//#pragma mark 点击客服头像跳转到客服详情页面：展示客服评价记录
//#pragma mark 点击访客头像进入个人详情页
//
//- (void)avatarClicked:(BDMessageModel *)messageModel {
//    NSLog(@"%s %@", __PRETTY_FUNCTION__, messageModel.avatar);
//}

- (void)linkUrlClicked:(NSString *)url {
    NSLog(@"%s %@", __PRETTY_FUNCTION__, url);
    
    NSURL *urlToOpen = [[NSURL alloc] initWithString:url];
    //    [[UIApplication sharedApplication] openURL:urlToOpen];
    [[UIApplication sharedApplication] openURL:urlToOpen options:@{} completionHandler:^(BOOL success) {
        if (success) {
            NSLog(@"Opened url");
        }
    }];
}

//TODO: 增加上拉、下拉关闭图片
#pragma mark 打开放大图片

- (void) imageViewClicked:(UIImageView *)imageView {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self showFullScreenImage:imageView.image];
}


- (void) fileViewClicked:(NSString *)fileUrl {
    NSLog(@"%s %@", __PRETTY_FUNCTION__, fileUrl);
}

- (void) videoViewClicked:(NSString *)videoUrl {
    NSLog(@"%s %@", __PRETTY_FUNCTION__, videoUrl);
    //
//    BDChatVideoViewController *videoViewController = [[BDChatVideoViewController alloc] init];
//    videoViewController.videoUrl = videoUrl;
//    [self.navigationController pushViewController:videoViewController animated:YES];
}

- (void) sendErrorStatusButtonClicked:(BDMessageModel *)model {
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void) robotLinkClicked:(NSString *)label withKey:(NSString *)key; {
    NSLog(@"%s label:%@, key:%@", __PRETTY_FUNCTION__, label, key);
//    [BDUIApis showTipWithVC:self withMessage:key];
    
    if ([key hasPrefix:@"helpfull"]) {
        // 评价机器人答案 ‘有帮助’
        NSArray *array = [key componentsSeparatedByString:@":"];
        NSString *aid = [array objectAtIndex:1];
        NSString *mid = [array objectAtIndex:2];
        NSLog(@"有帮助 %@, aid: %@, mid: %@", [array objectAtIndex:0], aid, mid);
        //
        [self rateRobotAnswer:aid withMid:mid withRate:YES];
        
    } else if ([key hasPrefix:@"helpless"]) {
        // 评价机器人答案 ‘无帮助’
        NSArray *array = [key componentsSeparatedByString:@":"];
        NSString *aid = [array objectAtIndex:1];
        NSString *mid = [array objectAtIndex:2];
        NSLog(@"无帮助 %@, aid: %@, mid: %@", [array objectAtIndex:0], aid, mid);
        //
        [self rateRobotAnswer:aid withMid:mid withRate:NO];
        
    } else if ([key isEqualToString:@"requestAgent"]) {
        // 会话关闭后，点击通知中 ‘人工客服’
        NSLog(@"人工客服");
        [self requestAgent];
        
    } else if ([key hasPrefix:@"question"]) {
        //
        NSArray *array = [key componentsSeparatedByString:@":"];
        NSString *aid = [array lastObject];
        NSLog(@"查询问题答案, aid: %@, label:%@", aid, label);
        // 此处key即为aid
        [self queryRobotAnswer:aid];
    } else if ([key hasPrefix:@"httplink"]) {
        // 打开http链接
        SFSafariViewController *safariVC = [[SFSafariViewController alloc] initWithURL:[NSURL URLWithString:label]];
        [self.navigationController presentViewController:safariVC animated:YES completion:^{
        }];
    }
}

- (void) robotQuestionClicked:(NSString *)aid withQuestion:(NSString *)question withAnswer:(NSString *)answer {
    NSLog(@"%s, aid: %@, question:%@, answer: %@", __PRETTY_FUNCTION__, aid, question, answer);
    
    [self insertLocalMessageAndReload:question withType:BD_MESSAGE_TYPE_TEXT withSend:YES];
    
    [self insertLocalMessageAndReload:answer withType:BD_MESSAGE_TYPE_ROBOT withSend:NO];
    
}

- (NSString *) insertLocalMessageAndReload:(NSString *)content withType:(NSString *)type withSend:(BOOL)isSend {
    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid]; //[[NSUUID UUID] UUIDString];
    NSLog(@"%s, content:%@, tid:%@, sessionType:%@, localId:%@ ", __PRETTY_FUNCTION__, content, self.mTid,  self.mThreadType, localId);
    
    BDMessageModel *messageModel;
    if ([type isEqualToString:BD_MESSAGE_TYPE_TEXT]) {
        // 插入本地消息, 可通过返回的messageModel首先更新本地UI，然后再发送消息
        messageModel = [[BDDBApis sharedInstance] insertTextMessageLocal:self.mTid withWorkGroupWid:self.mWorkGroupWid withContent:content withLocalId:localId withSessionType:self.mThreadType withSend:isSend];
    } else if ([type isEqualToString:BD_MESSAGE_TYPE_ROBOT]) {
        messageModel = [[BDDBApis sharedInstance] insertRobotMessageLocal:self.mTid withWorkGroupWid:self.mWorkGroupWid withContent:content withLocalId:localId withSessionType:self.mThreadType withSend:isSend];
    }

    // 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
    [self.mMessageArray addObject:messageModel];
    NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count]-1 inSection:0];
    //
    [self.mTableView beginUpdates];
    [self.mTableView insertRowsAtIndexPaths:@[insertIndexPath] withRowAnimation:UITableViewRowAnimationBottom];
    [self.mTableView endUpdates];
    // FIXME: 未往上滚动？
    [self tableViewScrollToBottom:NO];
    
    return localId;
}

// 答案有帮助
- (void) robotRateUpBtnClicked:(BDMessageModel *)messageModel {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO: 待完善
    BDRateViewController *ratevc = [[BDRateViewController alloc] init];
    [ratevc initWithThreadTid:self.mThreadModel.tid withPush:NO];
    [self.navigationController presentViewController:ratevc animated:YES completion:^{

    }];
}

// 答案无帮助
- (void) robotRateDownBtnClicked:(BDMessageModel *)messageModel {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO: 待完善
    BDRateViewController *ratevc = [[BDRateViewController alloc] init];
    [ratevc initWithThreadTid:self.mThreadModel.tid withPush:NO];
    [self.navigationController presentViewController:ratevc animated:YES completion:^{

    }];
}

#pragma mark BDMsgCommodityViewCellDelegate

- (void)sendCommodityButtonClicked:(BDMessageModel *)messageModel {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self sendCommodityMessage:messageModel.content];
}

//- (void)commodityBackgroundClicked:(BDMessageModel *)messageModel {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
//}

#pragma mark UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    [picker dismissViewControllerAnimated:YES completion:^{
        [self performSelector:@selector(dealWithImage:) withObject:info];
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)dealWithImage:(NSDictionary *)info {
    //
    NSString *mediaType = [info objectForKey:UIImagePickerControllerMediaType];
    if([mediaType isEqualToString:@"public.movie"]) {
        //被选中的是视频
        // TODO：上传视频.
        NSURL *videoURL = [info objectForKey:UIImagePickerControllerMediaURL];
        NSLog(@"videoURL %@", [videoURL absoluteString]);
        //
//        NSData *videoData = [NSData dataWithContentsOfURL:videoURL];
//        [self uploadVideoData:videoData];
//
        [BDVideoCompress compressVideoWithVideoUrl:videoURL withBiteRate:@(1500 * 1024) withFrameRate:@(30) withVideoWidth:@(960) withVideoHeight:@(540) compressComplete:^(id responseObjc) {
            //
            NSString *filePathStr = [responseObjc objectForKey:@"urlStr"];
            NSURL *compressvideourl = [NSURL fileURLWithPath:filePathStr];
            //
            NSData *videoData = [NSData dataWithContentsOfURL:compressvideourl];
            [self uploadVideoData:videoData];
        }];
    }
    else if([mediaType isEqualToString:@"public.image"]) {
        NSLog(@"拍照 %s", __PRETTY_FUNCTION__);
        //获取照片实例
        UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
        UIImageOrientation imageOrientation = image.imageOrientation;
        if (imageOrientation != UIImageOrientationUp) {
            UIGraphicsBeginImageContext(image.size);
            [image drawInRect:CGRectMake(0, 0, image.size.width, image.size.height)];
            image = UIGraphicsGetImageFromCurrentImageContext();
            UIGraphicsEndImageContext();
        }
        NSData *imageData = UIImageJPEGRepresentation(image, 0.6); // 压缩
        [self uploadImageData:imageData];
    }
}

// 保留图片到相册
-(void)saveImageToDisk:(NSDictionary *)info {
    //    UIImage *image = [info objectForKey:@"image"];
    //    NSString *imageName = [info objectForKey:@"imagename"];
    //    [[KFUtils sharedInstance] saveImage:image withName:imageName];
    //
    //    dispatch_async(dispatch_get_main_queue(), ^{
    //        [kfTableView reloadData];
    //    });
}

#pragma mark - 点击页面

- (void)handleSingleTap:(UIGestureRecognizer *)gestureRecognizer {
    //    NSLog(@"%s", __PRETTY_FUNCTION__);
//    [self.toolbarTextField resignFirstResponder];
//    [self hideToolbarViewWithKeyboardUserInfo:nil];
    [self.mInputView resignFirstResponder];
}

#pragma mark - 下拉刷新

- (void)refreshMessages {
    //
    NSLog(@"1. 客服会话：访客端拉取服务器聊天记录 %li", (long)self.mLastMessageId);
//        分页加载聊天记录
//        [BDCoreApis getMessageWithUser:[BDSettings getUid]
//                              withPage:self.mGetMessageFromChannelPage
//                         resultSuccess:^(NSDictionary *dict) {
//
//             self.mGetMessageFromChannelPage += 1;
//             [self reloadTableData];
//             [self.mRefreshControl endRefreshing];
//
//         } resultFailed:^(NSError *error) {
//             NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
//             // [QMUITips showError:@"加载失败" inView:view hideAfterDelay:2.0f];
//             [self.mRefreshControl endRefreshing];
//         }];
    
    [self.mRefreshControl endRefreshing];

}


- (void)startLoadingWithText:(NSString *)text {
    // [QMUITips showLoading:text inView:self.view];
    [BDUIApis showTipWithVC:self withMessage:text];
}

- (void)stopLoading {
    // [QMUITips hideAllToastInView:self.view animated:YES];
}

#pragma mark -

-(void)clearMessages {
    NSLog(@"清空内存聊天记录");
    
    [self reloadTableData];
}

#pragma mark - 拍照等Plus

// 选择图片
-(void)sharePickPhotoButtonPressed:(id)sender {
    NSLog(@"照片 %s", __PRETTY_FUNCTION__);
    self.mImagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self presentViewController:self.mImagePickerController animated:YES completion:nil];
//    [self authorizationPresentAlbumViewControllerWithTitle:@"选择图片" contentType:QMUIAlbumContentTypeOnlyPhoto];
}

// 拍照
-(void)shareTakePhotoButtonPressed:(id)sender {
    NSLog(@"拍照 %s", __PRETTY_FUNCTION__);
    //
    if (TARGET_IPHONE_SIMULATOR) {
        NSLog(@"模拟器不支持");
        return;
    }
    
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if(authStatus == AVAuthorizationStatusDenied
       || authStatus ==AVAuthorizationStatusRestricted) {
        // The user has explicitly denied permission for media capture.
        UIAlertController * alert = [UIAlertController
                                     alertControllerWithTitle:@""
                                     message:@"请在iPhone的‘设置-隐私-相机’选项中，允许访问你的相机"
                                     preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* yesButton = [UIAlertAction
                                    actionWithTitle:@"确定"
                                    style:UIAlertActionStyleDefault
                                    handler:^(UIAlertAction * action) {
                                        //Handle your yes please button action here
                                    }];
        [alert addAction:yesButton];
        [self presentViewController:alert animated:YES completion:nil];
//        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
//                    if(granted){
//                        NSLog(@"Granted access to %@", AVMediaTypeVideo);
//                    } else {
//                        NSLog(@"Not granted access to %@", AVMediaTypeVideo);
//                    }
//                }];
    } else if(authStatus == AVAuthorizationStatusAuthorized) {//允许访问
        // The user has explicitly granted permission for media capture,
        //or explicit user permission is not necessary for the media type in question.
//        dispatch_sync(dispatch_get_main_queue(), ^{
        self.mImagePickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
//        self.mImagePickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString *)kUTTypeImage, nil];
//        self.mImagePickerController.videoQuality = UIImagePickerControllerQualityTypeLow; // 为保证发送成功率，暂时设置为low
        [self presentViewController:mImagePickerController animated:YES completion:nil];
//        });

    } else if(authStatus == AVAuthorizationStatusNotDetermined){
        // Explicit user permission is required for media capture, but the user has not yet granted or denied such permission.
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            if(granted){//点击允许访问时调用
                //用户明确许可与否，媒体需要捕获，但用户尚未授予或拒绝许可。
                //NSLog(@"Granted access to %@", AVMediaTypeVideo);
                dispatch_sync(dispatch_get_main_queue(), ^{
                     self.mImagePickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
//                    self.mImagePickerController.mediaTypes = [[NSArray alloc] initWithObjects:(NSString *)kUTTypeImage, nil];
//                    self.mImagePickerController.videoQuality = UIImagePickerControllerQualityTypeLow; // 为保证发送成功率，暂时设置为low
                    [self presentViewController:self.mImagePickerController animated:YES completion:nil];
                });
            }
            else {
                //NSLog(@"Not granted access to %@", AVMediaTypeVideo);
            }
        }];
    } else {
        NSLog(@"Unknown authorization status");
    }
}

// https://github.com/wenmobo/WBDocumentBrowserDemo
// https://github.com/Unlimitzzh/FileAccess_iCloud_QQ_Wechat
- (void)shareFileButtonPressed:(id)sender {
    NSLog(@"发送文件消息 %s", __PRETTY_FUNCTION__);
    // 直接选择文件
    UIDocumentPickerViewController *documentPickerViewController = [[UIDocumentPickerViewController alloc] initWithDocumentTypes:[BDUIUtils documentTypes] inMode:UIDocumentPickerModeOpen];
    documentPickerViewController.delegate = self;
    [self presentViewController:documentPickerViewController
                       animated:YES
                     completion:nil];
}

- (void)shareLeaveMsgButtonPressed:(id)sender {
    NSLog(@"留言 %s", __PRETTY_FUNCTION__);
    
    BDLeaveMessageViewController *leaveMessageVC = [[BDLeaveMessageViewController alloc] init];
    [leaveMessageVC initWithType:self.mThreadType withUid:self.mUid withPush:NO];
    UINavigationController *leavenavigationController = [[UINavigationController alloc] initWithRootViewController:leaveMessageVC];
    [self.navigationController presentViewController:leavenavigationController animated:YES completion:^{
        
    }];
}

- (void)shareRateButtonPressed:(id)sender {
    NSLog(@"服务评价 %s", __PRETTY_FUNCTION__);
    
    BDRateViewController *ratevc = [[BDRateViewController alloc] init];
    [ratevc initWithThreadTid:self.mThreadModel.tid withPush:NO];
    [self.navigationController presentViewController:ratevc animated:YES completion:^{

    }];
}

- (void)shareShowFAQButtonPressed:(id)sender {
    NSLog(@"常见问题 %s", __PRETTY_FUNCTION__);
    
    BDFaqViewController *faqvc = [[BDFaqViewController alloc] init];
    [faqvc initWithType:self.mThreadType withUid:self.mUid withPush:NO];
    UINavigationController *faqnavigationController = [[UINavigationController alloc] initWithRootViewController:faqvc];
    [self.navigationController presentViewController:faqnavigationController animated:YES completion:^{
        
    }];
}

#pragma mark - 选取文件 UIDocumentPickerDelegate

- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(NSArray<NSURL *> *)urls {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    for (NSURL *url in urls) {
        NSLog(@"url: %@", url);
        [self getFileFromiCloud:url];
    }
}

- (void)documentPickerWasCancelled:(UIDocumentPickerViewController *)controller {
    NSLog(@"%s",__func__);
}

- (void)getFileFromiCloud:(NSURL *)url {
    
    // 某些文件url末尾带有'/',去掉
    NSString *urlString = [url absoluteString];
    if ([urlString hasSuffix:@"/"]) {
        urlString = [urlString substringWithRange:NSMakeRange(0, [urlString length] - 1)];
    }
    
    NSArray *array = [urlString componentsSeparatedByString:@"/"];
    NSString *fileName = [array lastObject];
    //        NSLog(@"fileName 1: %@", fileName);
    fileName = [fileName stringByRemovingPercentEncoding];
    //        NSLog(@"fileName 2: %@", fileName);
    
    if ([BDWBiCloudManager iCloudEnable]) {
        [BDWBiCloudManager wb_downloadWithDocumentURL:url
                                     completedBlock:^(id contents, NSString *type) {
                                         
                                         NSLog(@"type: %@", type);
                                         
                                         if ([contents isKindOfClass:[NSData class]]) {
                                             NSData *data = contents;
                                             // 上传文件 并发送
                                             [self uploadFileData:data fileName:fileName fileType:type];
                                             
                                         } else if ([contents isKindOfClass:[NSFileWrapper class]]) {
                                             
                                             NSFileWrapper *fileWrapper = contents;
                                             NSLog(@"filename: %@, attr: %@", fileWrapper.filename, fileWrapper.fileAttributes);
                                             if ([fileWrapper isDirectory]) {
                                                 // FIXME: 选取文件，提示文件夹？
                                                NSLog(@"文件夹");
                                                 [BDUIApis showErrorWithVC:self withMessage:@"不能选择文件夹"];
                                             } else {
                                                 NSLog(@"文件");
                                                 [self uploadFileData:[fileWrapper regularFileContents] fileName:fileName  fileType:type];
                                             }
                                             
                                         } else {
                                            NSLog(@"其他类型文件");
                                             [BDUIApis showErrorWithVC:self withMessage:@"其他类型文件"];
                                         }
                                     }];
    } else {
        NSLog(@"开发者首先需要到Apple开发者后台开启iCloud权限，iCloud未启用, 参考：https://www.weikefu.net/assets/spm/icloud-dev.png，或 https://www.weikefu.net/assets/spm/icloud-xcode.png");
        [BDUIApis showErrorWithVC:self withMessage:@"iCloud未启用，开发者首先需要到Apple开发者后台开启iCloud权限"];
    }
}

- (void)uploadFileData:(NSData *)fileData fileName:(NSString *)fileName fileType:(NSString *)fileType {
    
    NSString *fileSize = [NSString stringWithFormat:@"%.2fmb", (float)[fileData length]/1024.0f/1024.0f];
    NSLog(@"%s fileName: %@, fileSize: %@", __PRETTY_FUNCTION__, fileName, fileSize);
    
    // TODO: 选取文件之后，上传成功之前，增加发送文件消息气泡，增加文件发送进度
    
    // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
    NSString *localId = [BDUtils getGuid];
    [BDCoreApis uploadFileData:fileData withFileName:fileName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            // 自定义发送消息本地id，消息发送成功之后，服务器会返回此id，可以用来判断消息发送状态
            NSString *localId = [BDUtils getGuid];
            NSString *fileUrl = dict[@"data"];
            
            // 插入本地消息
            BDMessageModel *messageModel = [[BDDBApis sharedInstance] insertFileMessageLocal:self.mUid withWorkGroupWid:self.mWorkGroupWid withContent:fileUrl withLocalId:localId withSessionType:self.mThreadType withFormat:fileType withFileName:fileName withFileSize:fileSize withSend:YES];
            NSLog(@"%s %@ %@", __PRETTY_FUNCTION__, localId, messageModel.file_url);
            
            // TODO: 立刻更新UI，插入消息到界面并显示发送状态 activity indicator
            NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
            [self.mMessageArray addObject:messageModel];
            
            [self.mTableView beginUpdates];
            [self.mTableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
            [self.mTableView endUpdates];
            [self tableViewScrollToBottom:YES];
            
            // 发送文件消息
            [[BDMQTTApis sharedInstance] sendFileMessageProtobuf:localId content:fileUrl thread:self.mThreadModel];
        } else {
            [BDUIApis showErrorWithVC:self withMessage:@"发送文件错误"];
        }
        
    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
    }];
}

#pragma mark - 表情

-(void)emotionFaceButtonPressed:(id)sender
{
    UIButton *emotionButton = (UIButton *)sender;
    NSString *emotionText = [[self mEmotionToTextDictionary] objectForKey:[NSString stringWithFormat:@"Expression_%ld", (long)emotionButton.tag]];
//    NSLog(@"emotion %@", emotionText);
    
    //取余为0，即整除
    if (emotionButton.tag%21 == 0)
    {
        emotionText = @"删除";
    }
    
    NSString *content = [self.mInputView.inputTextView text];
    NSInteger contentLength = [content length];
    NSString *newContent;

    if ([emotionText isEqualToString:@"删除"])
    {
        if (contentLength > 0)
        {
            if ([@"]" isEqualToString:[content substringFromIndex:contentLength - 1]])
            {
                if ([content rangeOfString:@"["].location == NSNotFound)
                {
                    newContent = [content substringToIndex:contentLength - 1];
                }
                else
                {
                    newContent = [content substringToIndex:[content rangeOfString:@"[" options:NSBackwardsSearch].location];
                }
            }
            else
            {
                newContent = [content substringToIndex:contentLength-1];
            }

            self.mInputView.inputTextView.text = newContent;
        }
    }
    else
    {
        [self.mInputView.inputTextView setText:[NSString stringWithFormat:@"%@%@", content, emotionText]];
    }

    [self.mInputView textViewDidChange:mInputView.inputTextView];
}

-(void)emotionViewSendButtonPressed:(id)sender
{
    NSString *content = [self.mInputView.inputTextView text];

    if ([content length] == 0) {
        return;
    }
    
    [self sendMessage:content];
}

#pragma mark - 录音
//
-(void)recordVoiceButtonTouchDown:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //显示录音HUD
    self.mRecordVoiceViewHUD.hidden = FALSE;
    //开始录音
    [self.mRecordVoiceViewHUD startVoiceRecordingToUsername:self.mUid];
    [self.mTableView reloadData];
    [self tableViewScrollToBottom:YES];
}

-(void)recordVoiceButtonTouchUpInside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);

    self.mRecordVoiceViewHUD.hidden = TRUE;
    NSString *amrVoiceFileName = [mRecordVoiceViewHUD stopVoiceRecording];
    int voiceLength = (int)mRecordVoiceViewHUD.voiceRecordLength;
    //
    if ([amrVoiceFileName isEqualToString:@"tooshort"]) {
        NSLog(@"tooshort");
    }
    else if ([amrVoiceFileName isEqualToString:@"toolong"]) {
        NSLog(@"toolong");
    }
    else
    {
        [self uploadAmrVoice:amrVoiceFileName voiceLength:voiceLength];
    }
}

-(void)recordVoiceButtonTouchUpOutside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);

    self.mRecordVoiceViewHUD.hidden = TRUE;
    [self.mRecordVoiceViewHUD cancelVoiceRecording];
}

-(void)recordVoiceButtonTouchDragInside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    self.mRecordVoiceViewHUD.microphoneImageView.hidden = FALSE;
    self.mRecordVoiceViewHUD.signalWaveImageView.hidden = FALSE;
    self.mRecordVoiceViewHUD.cancelArrowImageView.hidden = TRUE;
    //
    self.mRecordVoiceViewHUD.hintLabel.text = @"上滑取消";
    self.mRecordVoiceViewHUD.hintLabel.backgroundColor = [UIColor clearColor];
}

-(void)recordVoiceButtonTouchDragOutside:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    self.mRecordVoiceViewHUD.microphoneImageView.hidden = TRUE;
    self.mRecordVoiceViewHUD.signalWaveImageView.hidden = TRUE;
    self.mRecordVoiceViewHUD.cancelArrowImageView.hidden = FALSE;
    //
    self.mRecordVoiceViewHUD.hintLabel.text = @"松手取消";
    self.mRecordVoiceViewHUD.hintLabel.backgroundColor = [UIColor redColor];
}


-(void)sendMessage:(NSString *)content {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
//    NSString *content = self.kfInputView.inputTextView.text;
    if ([content stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]].length > 0) {
        [self sendTextMessage:content];
        [self.mInputView.inputTextView setText:@""];
    }
}


#pragma mark - 输入框回调 KFInputViewDelegate

-(void)showMenuButtonPressed:(id)sender {
    [self.view endEditing:YES];
    NSLog(@"%s", __PRETTY_FUNCTION__);
}

-(void)switchVoiceButtonPressed:(id)sender {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    
    //如果当前按住说话按钮隐藏，则将其显示，并隐藏输入框
    if ([self.mInputView recordVoiceButton].hidden) {
        NSLog(@"%s 1",__PRETTY_FUNCTION__);
        [self.mInputView recordVoiceButton].hidden = FALSE;
        [self.mInputView inputTextView].hidden = TRUE;
        [[self.mInputView inputTextView] resignFirstResponder];
    }
    //如果当前按住说话按钮显示，则将其隐藏，并显示输入框，并将其获取焦点
    else {
        NSLog(@"%s 2",__PRETTY_FUNCTION__);
        [self.mInputView recordVoiceButton].hidden = TRUE;
        [self.mInputView inputTextView].hidden = FALSE;
        [[self.mInputView inputTextView] becomeFirstResponder];
    }
    
    //
    CGFloat emotionViewFrameY = [mEmotionView frame].origin.y;
    CGFloat plusViewFrameY = [self.mPlusView frame].origin.y;
    CGFloat frameHeight = self.view.frame.size.height;
    
    //如果当前Emotion扩展处于显示状态, 则隐藏Emotion扩展，
    if (emotionViewFrameY != frameHeight) {
        NSLog(@"%s 3",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             //
                             //调整kfTableView
                             UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                             tableViewContentInsets.bottom -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //调整kfInputView到页面底部
                             CGRect inputViewFrame = [self.mInputView frame];
                             inputViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mInputView setFrame:inputViewFrame];
                             
                             //隐藏emotionView
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                         } completion:^(BOOL finished) {
                             
                         }];
        
    }
    //如果当前Plus扩展处于显示状态，则隐藏Plus扩展
    else if (plusViewFrameY != frameHeight) {
        NSLog(@"%s 4",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             
                             //调整kfTableView
                             UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                             tableViewContentInsets.bottom -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //调整kfInputView到页面底部
//                             CGRect inputViewFrame = [self.kfInputView frame];
//                             inputViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
//                             [self.kfInputView setFrame:inputViewFrame];
                             
                             CGRect plusViewFrame = [self.mPlusView frame];
                             plusViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mPlusView setFrame:plusViewFrame];
                             
                         } completion:^(BOOL finished) {
                             
                         }];
    }
}

-(void)switchAgentButtonPressed:(id)sender {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    [self requestAgent];
}

-(void)switchEmotionButtonPressed:(id)sender {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    
    //如果输入框目前处于隐藏状态,即：显示录音button状态，则：1.隐藏录音button，2.显示输入框，3.更换switchViewButton image
    if ([self.mInputView inputTextView].hidden) {
        NSLog(@"%s 1",__PRETTY_FUNCTION__);
        [self.mInputView recordVoiceButton].hidden = TRUE;
        [self.mInputView inputTextView].hidden = FALSE;
        [[self.mInputView switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self.mInputView switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    }
    
    CGFloat inputViewFrameY = [self.mInputView frame].origin.y;
    CGFloat emotionViewFrameY = [mEmotionView frame].origin.y;
    CGFloat plusViewFrameY = [self.mPlusView frame].origin.y;
    CGFloat frameHeight = self.view.frame.size.height;
    
    //当前输入工具栏在会话页面最底部，显示表情
    if (inputViewFrameY == frameHeight - INPUTBAR_HEIGHT) {
        NSLog(@"%s 2",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             
                             //调整kfTableView
                             UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                             tableViewContentInsets.bottom += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //调整kfInputView
                             CGRect inputViewFrame = [self.mInputView frame];
                             inputViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mInputView setFrame:inputViewFrame];
                             
                             //调整kfEmotionView
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                         } completion:^(BOOL finished) {
                             
                         }];
        
    }
    //当前显示表情扩展, 需要显示键盘
    else if (emotionViewFrameY != frameHeight) {
        NSLog(@"%s 3",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             
                             //输入框设置焦点，显示键盘
                             [self.mInputView becomeFirstResponder];
                             
                             //隐藏emotionView
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                             //
                         } completion:^(BOOL finished) {
                             
                         }];
    }
    //当前显示plus扩展, 需要隐藏plus扩展，显示表情扩展
    else if (plusViewFrameY != frameHeight) {
        NSLog(@"%s 4",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             //
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                             //
                             CGRect plusViewFrame = [self.mPlusView frame];
                             plusViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mPlusView setFrame:plusViewFrame];
                                                          
                         } completion:^(BOOL finished) {
                             
                         }];
    }
    //当前显示键盘, 需要隐藏键盘，显示kfEmotionView
    else {
        NSLog(@"%s 5",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
            
                            UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                            tableViewContentInsets.bottom = EMOTION_PLUS_VIEW_HEIGHT + INPUTBAR_HEIGHT;
                            [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //隐藏键盘
                             UIView *keyboard = self.mInputView.inputTextView.inputAccessoryView.superview;
                             CGRect keyboardFrame = keyboard.frame;
                             keyboardFrame.origin.y = frameHeight;
                             [keyboard setFrame:keyboardFrame];
                             
//                             isEmotionPlusPressedToHideKeyboard = TRUE;
                             
                             [self.mInputView resignFirstResponder];
                             
                             //调整inputViewFrame
                             CGRect inputViewFrame = [self.mInputView frame];
                             inputViewFrame.origin.y = frameHeight - EMOTION_PLUS_VIEW_HEIGHT - INPUTBAR_HEIGHT;
                             [self.mInputView setFrame:inputViewFrame];
                             
                             //显示kfEmotionView
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                             
                         } completion:^(BOOL finished) {
                             
                         }];
        
    }
    
    [self tableViewScrollToBottom:YES];
}

-(void)switchPlusButtonPressed:(id)sender {
    NSLog(@"%s",__PRETTY_FUNCTION__);
    
    //如果输入框目前处于隐藏状态,即：显示录音button状态，则：1.隐藏录音button，2.显示输入框，3.更换switchViewButton image
    if ([self.mInputView inputTextView].hidden) {
        NSLog(@"%s 1",__PRETTY_FUNCTION__);
        [self.mInputView recordVoiceButton].hidden = TRUE;
        [self.mInputView inputTextView].hidden = FALSE;
        [[self.mInputView switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
        [[self.mInputView switchVoiceButton] setImage:[UIImage systemImageNamed:@"mic.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    }
    
    CGFloat inputViewFrameY = [self.mInputView frame].origin.y;
    CGFloat emotionViewFrameY = [self.mEmotionView frame].origin.y;
    CGFloat plusViewFrameY = [self.mPlusView frame].origin.y;
    CGFloat frameHeight = self.view.frame.size.height;
    
    //当前输入工具栏在会话页面最底部，显示plus
    if (inputViewFrameY == frameHeight - INPUTBAR_HEIGHT) {
        NSLog(@"%s 2",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             
                             //调整kfTableView
                             UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                             tableViewContentInsets.bottom += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //调整kfInputView
                             CGRect inputViewFrame = [self.mInputView frame];
                             inputViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mInputView setFrame:inputViewFrame];
                             
                             //调整kfPlusView
                             CGRect plusViewFrame = [self.mPlusView frame];
                             plusViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mPlusView setFrame:plusViewFrame];
                             
                         } completion:^(BOOL finished) {
                             
                         }];
        
    }
    //当前显示Plus扩展, 需要显示键盘
    else if (plusViewFrameY != frameHeight) {
        NSLog(@"%s 3",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
            
             if ([self.mInputView isFirstResponder]) {
                 NSLog(@"kfInputView isFirstResponder");
                 //输入框设置焦点，显示键盘
                 [self.mInputView resignFirstResponder];
             } else {
                 //输入框设置焦点，显示键盘
                 [self.mInputView becomeFirstResponder];
                 //隐藏plusView
                 CGRect plusViewFrame = [self.mPlusView frame];
                 plusViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                 [self.mPlusView setFrame:plusViewFrame];
             }

         } completion:^(BOOL finished) {
             
         }];
    }
    //当前显示表情扩展, 需要隐藏表情扩展，显示Plus扩展
    else if (emotionViewFrameY != frameHeight) {
        NSLog(@"%s 4",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                             
                             //
                             CGRect plusViewFrame = [self.mPlusView frame];
                             plusViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mPlusView setFrame:plusViewFrame];
                             
                             //
                             CGRect emotionViewFrame = [self.mEmotionView frame];
                             emotionViewFrame.origin.y += EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mEmotionView setFrame:emotionViewFrame];
                             
                             //
                             
                         } completion:^(BOOL finished) {
                             
                         }];
    }
    //当前显示键盘, 需要隐藏键盘，显示kfPlusView
    else {
        NSLog(@"%s 5",__PRETTY_FUNCTION__);
        
        [UIView animateWithDuration:VIEW_ANIMATION_DURATION
                              delay:0.0f
                            options:UIViewAnimationOptionCurveEaseOut
                         animations:^{
                            
                            UIEdgeInsets tableViewContentInsets = [self.mTableView contentInset];
                            tableViewContentInsets.bottom = EMOTION_PLUS_VIEW_HEIGHT + INPUTBAR_HEIGHT;
                            [self.mTableView setContentInset:tableViewContentInsets];
                             
                             //隐藏键盘
                             UIView *keyboard = self.mInputView.inputTextView.inputAccessoryView.superview;
                             CGRect keyboardFrame = keyboard.frame;
                             keyboardFrame.origin.y = frameHeight;
                             [keyboard setFrame:keyboardFrame];
                                                          
                             [self.mInputView resignFirstResponder];
                             
                             //调整inputViewFrame
                             CGRect inputViewFrame = [self.mInputView frame];
                             inputViewFrame.origin.y = frameHeight - EMOTION_PLUS_VIEW_HEIGHT - INPUTBAR_HEIGHT;
                             [self.mInputView setFrame:inputViewFrame];
                             
                             //显示kfPlusView
                             CGRect plusViewFrame = [self.mPlusView frame];
                             plusViewFrame.origin.y -= EMOTION_PLUS_VIEW_HEIGHT;
                             [self.mPlusView setFrame:plusViewFrame];
                             
                         } completion:^(BOOL finished) {
                             
                         }];
        
    }
    
    [self tableViewScrollToBottom:YES];
}


#pragma mark - UIScrollViewDelegate

-(void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self.view endEditing:YES];
    
    [UIView animateWithDuration:0.0f
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
        
        CGSize screenSize = self.view.frame.size;
        
        UIEdgeInsets tableViewInsets = self.mTableView.contentInset;
        tableViewInsets.bottom = INPUTBAR_HEIGHT * 2;
        self.mTableView.contentInset = tableViewInsets;
        self.mTableView.scrollIndicatorInsets = tableViewInsets;
        
        //调整kfInputView
        self.mInputView.frame = CGRectMake(0.0f,
                                            screenSize.height - INPUTBAR_HEIGHT,
                                            screenSize.width,
                                            INPUTBAR_HEIGHT);
        
        self.mEmotionView.frame = CGRectMake(0.0f,
                                              screenSize.height,
                                              screenSize.width,
                                              EMOTION_PLUS_VIEW_HEIGHT);
        
        self.mPlusView.frame = CGRectMake(0.0f,
                                           screenSize.height,
                                           screenSize.width,
                                           EMOTION_PLUS_VIEW_HEIGHT);
        
    } completion:^(BOOL finished) {
        
    }];
}


#pragma mark - Handle Keyboard Show/Hide

- (void)handleWillShowKeyboard:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    CGRect keyboardRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    double duration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    
    CGRect keyboardFrame = [self.view convertRect:keyboardRect fromView:nil];
    keyboardY = keyboardFrame.origin.y;
    keyboardHeight = keyboardFrame.size.height;
    
    [UIView animateWithDuration:duration
                          delay:0.0f
                        options:UIViewAnimationOptionCurveEaseIn
                     animations:^{
        
        CGRect inputViewFrame = [self.mInputView frame];
        inputViewFrame.origin.y = self.keyboardY - INPUTBAR_HEIGHT;
        [self.mInputView setFrame:inputViewFrame];
        
        UIEdgeInsets tableViewInsets = self.mTableView.contentInset;
        tableViewInsets.bottom = self.keyboardHeight + INPUTBAR_HEIGHT * 2;
        self.mTableView.contentInset = tableViewInsets;
        self.mTableView.scrollIndicatorInsets = tableViewInsets;
        
    } completion:^(BOOL finished) {
        
    }];
    
    [self tableViewScrollToBottom:YES];
}

- (void)handleWillHideKeyboard:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    CGRect keyboardRect = [[notification.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    double duration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    keyboardY = [self.view convertRect:keyboardRect fromView:nil].origin.y; // 键盘位置的y坐标
    
    [UIView animateWithDuration:duration
                          delay:0.0f
                        options:UIViewAnimationOptionCurveEaseOut
                     animations:^{
        
        //调整kfTableView
//        UIEdgeInsets tableViewContentInsets = [self.tableView contentInset];
//        tableViewContentInsets.bottom -= EMOTION_PLUS_VIEW_HEIGHT;
//        [self.tableView setContentInset:tableViewContentInsets];
        
        CGRect inputViewFrame = [self.mInputView frame];
        inputViewFrame.origin.y = self.keyboardY - INPUTBAR_HEIGHT;
        [self.mInputView setFrame:inputViewFrame];
        
        //调整kfPlusView
        CGRect plusViewFrame = [self.mPlusView frame];
        plusViewFrame.origin.y = inputViewFrame.origin.y + INPUTBAR_HEIGHT;
        [self.mPlusView setFrame:plusViewFrame];
        
        //隐藏emotionView
        CGRect emotionViewFrame = [self.mEmotionView frame];
        emotionViewFrame.origin.y = inputViewFrame.origin.y + INPUTBAR_HEIGHT;;
        [self.mEmotionView setFrame:emotionViewFrame];
        
    } completion:^(BOOL finished) {
        
    }];
    
    [self tableViewScrollToBottom:YES];
}



#pragma mark - 通知

/**
 客服接入会话通知
 
 @param notification <#notification description#>
 */
- (void)notifyQueueAccept:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //    self.titleView.subtitle = @"接入会话";
    //    self.titleView.needsLoadingView = NO;
}

/**
 客服关闭会话通知
 
 @param notification <#notification description#>
 */
- (void)notifyThreadClose:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //    self.titleView.subtitle = @"客服关闭会话";
    self.mIsThreadClosed = YES;
}

/**
 <#Description#>
 
 @param notification <#notification description#>
 */
- (void)notifyReloadCellStatus:(NSNotification *)notification {
    //
    NSDictionary *dict = [notification object];
    NSString *localId = dict[@"localId"];
    NSString *status = dict[@"status"];
    //    NSLog(@"%s %@, %@", __PRETTY_FUNCTION__, localId, status);
    
    [self reloadCellDataStatus:localId status:status];
}

/**
 收到新消息通知
 
 @param notification <#notification description#>
 */
- (void)notifyMessageAdd:(NSNotification *)notification {
    //    NSLog(@"%s", __PRETTY_FUNCTION__);
    //    [self hideEmptyView];
    
    BDMessageModel *messageModel = [notification object];
    if ([messageModel.type isEqualToString:BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE]) {
        self.rateInvite = TRUE;
        [self shareRateButtonPressed:nil];
    }
    
    // 接收到其他人发送的消息
    if (!messageModel.isSend) {
        //        NSIndexPath *insertIndexPath = [NSIndexPath indexPathForRow:[self.mMessageArray count] inSection:0];
        [self.mMessageArray addObject:messageModel];
        //
        //        [self.tableView beginUpdates];
        //        [self.tableView insertRowsAtIndexPaths:[NSMutableArray arrayWithObjects:insertIndexPath, nil] withRowAnimation:UITableViewRowAnimationBottom];
        //        [self.tableView endUpdates];
        //        [self.tableView reloadData];
        // FIXME: 未往上滚动？
        //        [self tableViewScrollToBottom:NO];
        
        [self reloadTableData];
        
        // FIXME: 仅针对单聊和客服会话有效，群聊暂不发送已读状态
        // TODO: 发送消息已读回执
        // 非系统消息
        if (![messageModel.type hasPrefix:BD_MESSAGE_TYPE_NOTIFICATION]) {
            // 消息状态
            if (messageModel.status != NULL ||
                [messageModel.status isEqualToString:BD_MESSAGE_STATUS_STORED]) {
                // TODO: 更新本地消息为已读
                
                // 不是自己发送的消息，发送已读回执
                if (![messageModel.uid isEqualToString:[BDSettings getUid]]) {
                    [[BDMQTTApis sharedInstance] sendReceiptReadMessageProtobufThread:self.mThreadModel receiptMid:messageModel.mid];
                }
            }
        }
    }
}


/**
 暂未启用
 
 @param notification <#notification description#>
 */
- (void)notifyMessageDelete:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO: 优化处理
    [self reloadTableData];
}

/**
 消息预知
 
 @param notification <#notification description#>
 */
- (void)notifyMessagePreview:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    // {"mid":"201908241850174","client":"web_admin","thread":{"tid":"201908241849491"},"type":"notification_preview",
    // "user":{"visitor":false,"uid":"201808221551193","username":"admin@test.com"},"content":"22222"}
    //    NSDictionary *dict = [notification object];
    //    NSString *threadTid = dict[@"thread"][@"tid"];
    //    NSString *content = dict[@"content"];
    BDMessageModel *messageModel = [notification object];
    if ([messageModel isSend]) {
        //  忽略掉自己的输入状态
        return;
    }
    //
    if ([messageModel.thread_tid isEqualToString:self.mTid]) {
        //        self.titleView.subtitle = @"对方正在输入...";
    }
    // 延时执行，标题还原
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        //        self.titleView.subtitle = @"会话中";
    });
    // TODO: 优化处理
    [self reloadTableData];
}

/**
 消息撤回通知
 
 @param notification <#notification description#>
 */
- (void)notifyMessageRecall:(NSNotification *)notification {
    //
    NSString *mid = [notification object];
    NSLog(@"%s mid: %@", __PRETTY_FUNCTION__, mid);
    //
    for (int i = 0; i < [self.mMessageArray count]; i++) {
        BDMessageModel *message = [self.mMessageArray objectAtIndex:i];
        //        NSLog(@"mid: %@, message.mid: %@", mid, message.mid);
        //
        if (![message.mid isKindOfClass:[NSNull class]] && [message.mid isEqualToString:mid]) {
            //
            [self.mMessageArray removeObjectAtIndex:i];
            // TODO: 优化处理
            [self reloadTableData];
            return;
        }
    }
}


/**
 发送消息状态通知
 
 @param notification <#notification description#>
 */
- (void)notifyMessageStatus:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    // TODO: 优化处理
    [self reloadTableData];
}

- (void)notifyKickoff:(NSNotification *)notification {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    NSString *content = [notification object];
}

#pragma mark -

- (void)showFullScreenImage:(UIImage *)image {
    
    BDImageViewController *imageVc = [[BDImageViewController alloc] init];
    imageVc.image = image;
    //
    UINavigationController *imageNavigationController = [[UINavigationController alloc] initWithRootViewController:imageVc];
    [self.navigationController presentViewController:imageNavigationController animated:YES completion:nil];
}

@end
