//
//  KFThreadViewController.m
//  linphone
//
//  Created by 萝卜丝 on 2018/10/12.
//

#import "KFVisitorThreadViewController.h"
#import "KFThreadTableViewCell.h"

#import <AFNetworking/UIImageView+AFNetworking.h>
#import <bytedesk-core/bdcore.h>

@interface KFVisitorThreadViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
@property(nonatomic, strong) NSMutableArray<BDThreadModel *> *mThreadArray;

@property(nonatomic, assign) NSInteger mThreadPage;

@end

@implementation KFVisitorThreadViewController


- (id)initWithStyle:(UITableViewStyle)style {
    if (self = [super initWithStyle:style]) {
        //
        self.mThreadArray = [[NSMutableArray alloc] init];
        self.mThreadPage = 0;
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    //
    [self refreshControlSelector];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
}

#pragma mark - 工具方法

- (void)reloadTableData {
    // 读取本地数据
    self.mThreadArray = [BDCoreApis getThreads];
    if ([self.mThreadArray count] == 0 && [BDCoreApis getQueueCount] == 0) {
        //TODO: bug 当对话记录内容为空的时候，无法点击进入排队页面
//        [self showEmptyViewWithText:@"对话记录为空" detailText:@"暂无对话记录" buttonTitle:nil buttonAction:NULL];
    }
    else {
//        [self hideEmptyView];
    }
    [self.tableView reloadData];
}

#pragma mark - <QMUITableViewDataSource, QMUITableViewDelegate>

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.mThreadArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *identifier = @"threadCell";
    KFThreadTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (!cell) {
        cell = [[KFThreadTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    //
    BDThreadModel *threadModel = self.mThreadArray[indexPath.row];
    [cell initWithThreadModel:threadModel];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 55;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
//    DDLogInfo(@"%s", __PRETTY_FUNCTION__);

    // TODO: 会话列表中区分会话类型：一对一、群组、工作组
//    [KFDSUIApis adminPushChat:self.navigationController
//              withThreadModel:[self.mThreadArray objectAtIndex:indexPath.row - 1]];
}

#pragma mark - Selectors

- (void)refreshControlSelector {
//    DDLogInfo(@"%s", __PRETTY_FUNCTION__);
    UIView *parentView = self.navigationController.view;
    //
    [BDCoreApis visitorGetThreadsPage:self.mThreadPage
                        resultSuccess:^(NSDictionary *dict) {
                            
                            self.mThreadPage++;
                            
                            [self reloadTableData];
                            [self.mRefreshControl endRefreshing];
                            
                        } resultFailed:^(NSError *error) {
//                            DDLogInfo(@"%s, %@", __PRETTY_FUNCTION__, error);
//                            [QMUITips showError:@"加载失败" inView:parentView hideAfterDelay:2.0f];
                            [self.mRefreshControl endRefreshing];
                        }];
}



@end







