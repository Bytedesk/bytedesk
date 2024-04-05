//
//  BDRateTableViewController.m
//  bytedesk-oc
//
//  Created by 宁金鹏 on 2023/8/24.
//

#import "BDRateViewController.h"
#import "UITextView+Placeholder.h"
#import "BDCoreApis.h"
#import "BDUIApis.h"
#import "BDUtils.h"

#define DeviceHeight [[UIScreen mainScreen] bounds].size.height
#define DeviceWidth [[UIScreen mainScreen] bounds].size.width

@interface BDRateViewController ()

@property(nonatomic, strong) UIButton *upButton;
@property(nonatomic, strong) UIButton *downButton;

@property(nonatomic, strong) UIButton *downChoiceButton1;
@property(nonatomic, strong) UIButton *downChoiceButton2;
@property(nonatomic, strong) UIButton *downChoiceButton3;
@property(nonatomic, strong) UIButton *downChoiceButton4;

@property(nonatomic, assign) NSString *threadTid;
@property(nonatomic, assign) BOOL isSolved;
@property(nonatomic, assign) NSString *note;

@property(nonatomic, assign) BOOL mIsPush;
@property(nonatomic, assign) BOOL forceEnableBackGesture;

@end

@implementation BDRateViewController

@synthesize upButton, downButton, downChoiceButton1, downChoiceButton2, downChoiceButton3, downChoiceButton4;

- (void)initWithThreadTid:(NSString *)tid withPush:(BOOL)isPush{
    NSLog(@"1.%s, threadTid: %@", __PRETTY_FUNCTION__, tid);
    self.threadTid = tid;
    self.isSolved = NO;
    self.note = @"";
    self.mIsPush = isPush;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"2.%s", __PRETTY_FUNCTION__);
    self.title = @"服务评价";
    self.view.backgroundColor = [UIColor whiteColor];
    if(@available(iOS 13.0,*)) {
        UIUserInterfaceStyle mode = UITraitCollection.currentTraitCollection.userInterfaceStyle;
        if (mode == UIUserInterfaceStyleDark) {
            self.view.backgroundColor = [UIColor blackColor];
        } else {
            self.view.backgroundColor = [UIColor whiteColor];
        }
    }
    //
    if (self.mIsPush) {
        //
    }
    else {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemClose target:self action:@selector(handleCloseButtonEvent:)];
//        self.navigationItem.leftBarButtonItem = [UIBarButtonItem qmui_closeItemWithTarget:self action:@selector(handleCloseButtonEvent:)];
    }
    self.forceEnableBackGesture = YES;// 当系统的返回按钮被屏蔽的时候，系统的手势返回也会跟着失效，所以这里要手动强制打开手势返回
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"3.%s", __PRETTY_FUNCTION__);
}

- (void)viewWillLayoutSubviews {
    NSLog(@"4.%s", __PRETTY_FUNCTION__);
    [super viewWillLayoutSubviews];
    //
    [self initRateRobotView];
}

#pragma mark - 初始化评价机器人界面

- (void)initRateRobotView {
    self.view.frame = CGRectMake(self.view.frame.origin.x, DeviceHeight / 3, DeviceWidth, DeviceHeight * 2 / 3);
    self.view.layer.cornerRadius = 20.0;
    //
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake((DeviceWidth - 100)/2, 20, 100, 20)];
    [titleLabel setText:@"服务评价"];
    [self.view addSubview:titleLabel];
    //
    UIButton *closeButton = [[UIButton alloc] initWithFrame:CGRectMake(DeviceWidth - 40, 15, 30, 30)];
    [closeButton setImage:[UIImage systemImageNamed:@"xmark.circle" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateNormal];
    [closeButton setImage:[UIImage systemImageNamed:@"xmark.circle.fill" withConfiguration:[UIImageSymbolConfiguration configurationWithPointSize:25]] forState:UIControlStateHighlighted];
    [closeButton addTarget:self action:@selector(closeButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:closeButton];
    [BDUtils setButtonTitleColor:closeButton];
    //
    UILabel *tipLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 50, DeviceWidth - 40, 40)];
    [tipLabel setText:@"机器人是否解决您的问题？"];
    [tipLabel setTextAlignment:NSTextAlignmentCenter];
    [self.view addSubview:tipLabel];
    //
    upButton = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)/3, 100, 150, 40)];
    [upButton setImage:[UIImage systemImageNamed:@"hand.thumbsup"] forState:UIControlStateNormal];
    [upButton setImage:[UIImage systemImageNamed:@"hand.thumbsup.fill"] forState:UIControlStateSelected];
    [upButton setImage:[UIImage systemImageNamed:@"hand.thumbsup.fill"] forState:UIControlStateHighlighted];
    [upButton setTitle:@"已解决" forState:UIControlStateNormal];
    [upButton setTitle:@"已解决(✅)" forState:UIControlStateSelected];
//    [upButton setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [upButton addTarget:self action:@selector(upButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    upButton.layer.cornerRadius = 20.0;
    upButton.layer.masksToBounds = YES;
//    upButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    upButton.layer.borderColor = [UIColor systemGrayColor].CGColor;
    upButton.layer.borderWidth = 0.5;
    [self.view addSubview:upButton];
    [BDUtils setButtonTitleColor:upButton];
    //
    downButton = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)*2/3 + 150, 100, 150, 40)];
    [downButton setImage:[UIImage systemImageNamed:@"hand.thumbsdown"] forState:UIControlStateNormal];
    [downButton setImage:[UIImage systemImageNamed:@"hand.thumbsdown.fill"] forState:UIControlStateSelected];
    [downButton setImage:[UIImage systemImageNamed:@"hand.thumbsdown.fill"] forState:UIControlStateHighlighted];
    [downButton setTitle:@"未解决" forState:UIControlStateNormal];
    [downButton setTitle:@"未解决(✅)" forState:UIControlStateSelected];
//    [downButton setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [downButton addTarget:self action:@selector(downButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    downButton.layer.cornerRadius = 20.0;
    downButton.layer.masksToBounds = YES;
//    downButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    downButton.layer.borderColor = [UIColor systemGrayColor].CGColor;
    downButton.layer.borderWidth = 0.5;
    [self.view addSubview:downButton];
    [BDUtils setButtonTitleColor:downButton];
    //
    downChoiceButton1 = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)/3, 155, 150, 40)];
    downChoiceButton1.tag = 1;
    downChoiceButton1.hidden = YES;
    [downChoiceButton1 setTitle:@"答非所问" forState:UIControlStateNormal];
    [downChoiceButton1 setTitle:@"答非所问(✅)" forState:UIControlStateSelected];
    [downChoiceButton1 setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [downChoiceButton1 addTarget:self action:@selector(selectChoiceButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    downChoiceButton1.layer.cornerRadius = 20.0;
    downChoiceButton1.layer.masksToBounds = YES;
//    downChoiceButton1.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    downChoiceButton1.layer.borderColor = [UIColor systemGrayColor].CGColor;
    downChoiceButton1.layer.borderWidth = 0.5;
    [self.view addSubview:downChoiceButton1];
    //
    downChoiceButton2 = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)*2/3 + 150, 155, 150, 40)];
    downChoiceButton2.tag = 2;
    downChoiceButton2.hidden = YES;
    [downChoiceButton2 setTitle:@"理解能力差" forState:UIControlStateNormal];
    [downChoiceButton2 setTitle:@"理解能力差(✅)" forState:UIControlStateSelected];
    [downChoiceButton2 setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [downChoiceButton2 addTarget:self action:@selector(selectChoiceButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    downChoiceButton2.layer.cornerRadius = 20.0;
    downChoiceButton2.layer.masksToBounds = YES;
//    downChoiceButton2.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    downChoiceButton2.layer.borderColor = [UIColor systemGrayColor].CGColor;
    downChoiceButton2.layer.borderWidth = 0.5;
    [self.view addSubview:downChoiceButton2];
    //
    downChoiceButton3 = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)/3, 210, 150, 40)];
    downChoiceButton3.tag = 3;
    downChoiceButton3.hidden = YES;
    [downChoiceButton3 setTitle:@"问题不能回答" forState:UIControlStateNormal];
    [downChoiceButton3 setTitle:@"问题不能回答(✅)" forState:UIControlStateSelected];
    [downChoiceButton3 setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [downChoiceButton3 addTarget:self action:@selector(selectChoiceButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    downChoiceButton3.layer.cornerRadius = 20.0;
    downChoiceButton3.layer.masksToBounds = YES;
//    downChoiceButton3.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    downChoiceButton3.layer.borderColor = [UIColor systemGrayColor].CGColor;
    downChoiceButton3.layer.borderWidth = 0.5;
    [self.view addSubview:downChoiceButton3];
    //
    downChoiceButton4 = [[UIButton alloc] initWithFrame:CGRectMake((DeviceWidth - 300)*2/3 + 150, 210, 150, 40)];
    downChoiceButton4.tag = 4;
    downChoiceButton4.hidden = YES;
    [downChoiceButton4 setTitle:@"不礼貌" forState:UIControlStateNormal];
    [downChoiceButton4 setTitle:@"不礼貌(✅)" forState:UIControlStateSelected];
    [downChoiceButton4 setTitleColor:[UIColor systemBlueColor] forState:UIControlStateNormal];
    [downChoiceButton4 addTarget:self action:@selector(selectChoiceButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    downChoiceButton4.layer.cornerRadius = 20.0;
    downChoiceButton4.layer.masksToBounds = YES;
//    downChoiceButton4.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    downChoiceButton4.layer.borderColor = [UIColor systemGrayColor].CGColor;
    downChoiceButton4.layer.borderWidth = 0.5;
    [self.view addSubview:downChoiceButton4];
//    //
//    UITextView *inputTextView = [[UITextView alloc] initWithFrame:CGRectMake(20, 260, DeviceWidth - 40, 100)];
//    inputTextView.placeholder = @"欢迎给我们的服务提建议";
//    [self.view addSubview:inputTextView];
    //
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(10, DeviceHeight*2/3 - 140, DeviceWidth - 20, 40)];
    [submitButton setBackgroundColor:[UIColor systemOrangeColor]];
//    [submitButton setImage:[UIImage systemImageNamed:@"star"] forState:UIControlStateNormal];
    [submitButton setTitle:@"提交评价" forState:UIControlStateNormal];
    [submitButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked) forControlEvents:UIControlEventTouchUpInside];
    submitButton.layer.cornerRadius = 20.0;
    submitButton.layer.masksToBounds = YES;
//    submitButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    submitButton.layer.borderColor = [UIColor systemGrayColor].CGColor;
    submitButton.layer.borderWidth = 0.5;
    [self.view addSubview:submitButton];
//    [self setButtonTitleColor:submitButton];
}

- (void)upButtonClicked:(UIButton *)button {
    //
    BOOL isSelected = [button isSelected];
    [button setSelected:!isSelected];
    
    if ([button isSelected]) {
        self.isSolved = YES;
        //
        [downButton setSelected:NO];
        //
        downChoiceButton1.hidden = YES;
        downChoiceButton2.hidden = YES;
        downChoiceButton3.hidden = YES;
        downChoiceButton4.hidden = YES;
    }
}

- (void)downButtonClicked:(UIButton *)button {
    //
    BOOL isSelected = [button isSelected];
    [button setSelected:!isSelected];
    if ([button isSelected]) {
        self.isSolved = NO;
        //
        [upButton setSelected:NO];
        //
        downChoiceButton1.hidden = NO;
        downChoiceButton2.hidden = NO;
        downChoiceButton3.hidden = NO;
        downChoiceButton4.hidden = NO;
    } else {
        //
        downChoiceButton1.hidden = YES;
        downChoiceButton2.hidden = YES;
        downChoiceButton3.hidden = YES;
        downChoiceButton4.hidden = YES;
    }
}

- (void)selectChoiceButtonClicked:(UIButton *)button {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    BOOL isSelected = [button isSelected];
    [button setSelected:!isSelected];
    //
    self.note = @"";
    if (!self.isSolved) {
        if (downChoiceButton1.isSelected) {
            self.note = [NSString stringWithFormat:@"%@ 答非所问", self.note];
        }
        if (downChoiceButton2.isSelected) {
            self.note = [NSString stringWithFormat:@"%@ 理解能力差", self.note];
        }
        if (downChoiceButton3.isSelected) {
            self.note = [NSString stringWithFormat:@"%@ 问题不能回答", self.note];
        }
        if (downChoiceButton4.isSelected) {
            self.note = [NSString stringWithFormat:@"%@ 不礼貌", self.note];
        }
    }
}

- (void)submitButtonClicked {
    NSLog(@"%s, threadtid:%@, note: %@", __PRETTY_FUNCTION__, self.threadTid, self.note);
    // TODO: 网络http接口提交
    
    [BDUIApis showTipWithVC:self withMessage:@"提交评价中，请稍后"];
    __weak __typeof(self)weakSelf = self;
    [BDCoreApis visitorRate:self.threadTid
                  withScore:self.isSolved ? 5 : 0
                   withNote:self.isSolved ? @"" : self.note
                 withInvite:NO
              resultSuccess:^(NSDictionary *dict) {
        //
        NSString *message = [dict objectForKey:@"message"];
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        NSLog(@"%s message:%@, status_code:%@", __PRETTY_FUNCTION__, message, status_code);
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            [BDUIApis showTipWithVC:self withMessage:message];
            //
            [weakSelf closeButtonClicked];
        } else {
            [BDUIApis showTipWithVC:self withMessage:message];
        }
    } resultFailed:^(NSError *error) {
        
    }];
}

- (void)closeButtonClicked {
    //
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (BOOL)forceEnableInteractivePopGestureRecognizer {
    return self.forceEnableBackGesture;
}

// 针对Present打开模式，左上角返回按钮处理action
- (void)handleCloseButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    self.mIsViewControllerClosed = YES;
    [self.navigationController dismissViewControllerAnimated:YES completion:^{
    }];
}

// 针对Push打开模式，左上角返回按钮处理action
- (void)handleBackButtonEvent:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
//    self.mIsViewControllerClosed = YES;
    [self.navigationController popViewControllerAnimated:YES];
}

@end
