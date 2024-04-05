//
//  BDLeaveRecordViewController.m
//  bytedesk-oc
//
//  Created by 宁金鹏 on 2023/8/24.
//

#import "BDLeaveHistoryViewController.h"

#import "BDLeaveMsgModel.h"
#import "BDCoreApis.h"

@interface BDLeaveHistoryViewController ()

@property(nonatomic, strong) UIRefreshControl *mRefreshControl;
@property(nonatomic, strong) NSMutableArray *mLeaveArray;

@property(nonatomic, assign) int page;
@property(nonatomic, assign) int size;

@end

@implementation BDLeaveHistoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //
    self.title = @"我的留言";
    self.page = 0;
    self.size = 20;
    //
    self.mLeaveArray = [[NSMutableArray alloc] init];
    //
    self.mRefreshControl = [[UIRefreshControl alloc] initWithFrame:CGRectMake(0, 0, 20, 20)];
    [self.tableView addSubview:self.mRefreshControl];
    [self.mRefreshControl addTarget:self action:@selector(refreshControlSelector) forControlEvents:UIControlEventValueChanged];
    //
    [self.mRefreshControl beginRefreshing];
    [self getLeaveRecords];
}

#pragma mark - <UITableViewDataSource, UITableViewDelegate>

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if ([self.mLeaveArray count] == 0) {
        // 内容为空
        return 1;
    }
    return [self.mLeaveArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    if ([self.mLeaveArray count] == 0) {
        cell.detailTextLabel.text = @"暂未留言";
        return cell;
    }
    
    //
    BDLeaveMsgModel *leaveModel = [self.mLeaveArray objectAtIndex:indexPath.row];
    cell.textLabel.text = leaveModel.content;
    if ([leaveModel.reply isKindOfClass:[NSNull class]]) {
        cell.detailTextLabel.text = @"暂未回复";
    } else {
        cell.detailTextLabel.text = leaveModel.reply;
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
//    BDLeaveModel *feedbackModel = [self.mLeaveArray objectAtIndex:indexPath.row];
    //
}

#pragma mark -

- (void)refreshControlSelector {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self getLeaveRecords];
}

#pragma mark -

- (void)getLeaveRecords {
    //
    [BDCoreApis getLeaveMessagesVisitorPage:self.page withSize:self.size resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, %@", __PRETTY_FUNCTION__, dict);
        //
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            //
            NSMutableArray *leaveMsgArray = dict[@"data"][@"content"];
            for (NSDictionary *leaveMsgDict in leaveMsgArray) {
                BDLeaveMsgModel *leaveMsgModel = [[BDLeaveMsgModel alloc] initWithDictionary:leaveMsgDict];
                //
                if (![self.mLeaveArray containsObject:leaveMsgModel]) {
                    [self.mLeaveArray addObject:leaveMsgModel];
                }
            }
            //
            [self.tableView reloadData];
            [self.mRefreshControl endRefreshing];
            //
//            if ([self.mLeaveArray count] == 0) {
//                [self showEmptyViewWithText:@"我的反馈" detailText:@"暂无反馈" buttonTitle:nil buttonAction:NULL];
//            } else {
//                [self hideEmptyView];
//            }

        } else {
            //
            NSString *message = dict[@"message"];
//            // [QMUITips showError:message inView:self.view hideAfterDelay:2];
            
            [self.mRefreshControl endRefreshing];
        }
    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
        [self.mRefreshControl endRefreshing];
        //
        if (error) {
//            // [QMUITips showError:error.localizedDescription inView:self.view hideAfterDelay:2];
        }
        
    }];
}


@end
