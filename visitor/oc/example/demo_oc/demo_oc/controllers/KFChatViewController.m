//
//  KFVisitorChatViewController.m
//  demo
//
//  Created by 萝卜丝 on 2018/11/22.
//  Copyright © 2018年 Bytedesk.com. All rights reserved.
//

#import "KFChatViewController.h"
//#import <bytedesk-core/bdcore.h>
//#import <bytedesk-ui/bdui.h>
#import <bytedesk_oc/bytedesk_oc.h>

#define kDefaultTitle @"人工客服"

// 机器人测试组
#define kRobotWorkGroupWid @"201809061716221"
// 默认设置工作组wid
#define kDefaultWorkGroupWid @"201807171659201"
// 指定坐席uid
#define kDefaultAgentUid @"201808221551193"

@interface KFChatViewController ()

@property(nonatomic, strong) NSArray *apisArray;
@property(nonatomic, strong) NSString *unreadCount;

@end

@implementation KFChatViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.apisArray = @[
                        @"默认机器人Push:",
                        @"默认机器人Present:",
                        //
                        @"未读消息数:",
                        @"工作组会话Push:",
                        @"工作组会话Present:",
                        //
                        @"指定坐席Push:",
                        @"指定坐席Present:",
                        //
                        @"电商客服Push:",
                        @"电商客服Present:",
                       ];
    [self getUnreadCountVisitor];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 4;
}

- (nullable NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    //
    if (tableView == self.tableView) {
        //
        if (section == 0) {
            return @"默认机器人";
        } else if (section == 1) {
            return @"工作组会话";
        } else if (section == 2) {
            return @"指定坐席会话";
        } else if (section == 3){
            return @"电商客服";
        }
    }
    return @"";
}

- (nullable NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    //
    if (tableView == self.tableView) {
        //
        if (section == 0) {
            return @"后台设置默认机器人";
        }
    }
    return @"";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 1) {
        return 3;
    }
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (!cell){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    //
    if (indexPath.section == 0) {
        cell.textLabel.text = [self.apisArray objectAtIndex:indexPath.row];
        cell.detailTextLabel.text = kRobotWorkGroupWid;
    } else if (indexPath.section == 1) {
        cell.textLabel.text = [self.apisArray objectAtIndex:indexPath.row + 2];
        if (indexPath.row == 0) {
            cell.detailTextLabel.text = _unreadCount;
        } else {
            cell.detailTextLabel.text = kDefaultWorkGroupWid;
        }
    } else if (indexPath.section == 2){
        cell.textLabel.text = [self.apisArray objectAtIndex:indexPath.row + 5];
        cell.detailTextLabel.text = kDefaultAgentUid;
    } else if (indexPath.section == 3){
        cell.textLabel.text = [self.apisArray objectAtIndex:indexPath.row + 7];
        cell.detailTextLabel.text = kDefaultAgentUid;
    }
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSString *title = [self.apisArray objectAtIndex:indexPath.row];
    
    if (indexPath.section == 0) {
        //
        if (indexPath.row == 0) {
            // push工作组会话
            [BDUIApis pushWorkGroupChat:self.navigationController withWorkGroupWid:kRobotWorkGroupWid withTitle:title];
        } else {
            // present工作组会话
            [BDUIApis presentWorkGroupChat:self.navigationController withWorkGroupWid:kRobotWorkGroupWid withTitle:title];
        }
    } else if (indexPath.section == 1) {
        //
        if (indexPath.row == 0) {
            //
            [self getUnreadCountVisitor];
        } else if (indexPath.row == 1) {
            // push工作组会话
            [BDUIApis pushWorkGroupChat:self.navigationController withWorkGroupWid:kDefaultWorkGroupWid withTitle:title];
        } else {
            // present工作组会话
            [BDUIApis presentWorkGroupChat:self.navigationController withWorkGroupWid:kDefaultWorkGroupWid withTitle:title];
        }
    } else if (indexPath.section == 2){
        //
        if (indexPath.row == 0) {
            // push指定坐席会话
            [BDUIApis pushAppointChat:self.navigationController withAgentUid:kDefaultAgentUid withTitle:title];
        } else {
            // present指定坐席会话
            [BDUIApis presentAppointChat:self.navigationController withAgentUid:kDefaultAgentUid withTitle:title];
        }
    } else if (indexPath.section == 3) {
        //
        if (indexPath.row == 0) {
            // 携带商品信息工作组会话
            NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
                                  @"commodity", @"type",
                                  @"商品标题", @"title",
                                  @"商品详情", @"content",
                                  @"¥9.99", @"price",
                                  @"https://item.m.jd.com/product/12172344.html", @"url",
                                  @"https://www.kefux.com/assets/img/qrcode/luobosi_mp.png", @"imageUrl",
                                  @"123", @"id",
                                  @"345", @"categoryCode",
                                  nil];
            [BDUIApis pushWorkGroupChat:self.navigationController withWorkGroupWid:kDefaultWorkGroupWid withTitle:title withCustom:dict];
        } else {
            // 携带商品信息指定坐席
            NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
                                  @"commodity", @"type",
                                  @"商品标题", @"title",
                                  @"商品详情", @"content",
                                  @"¥9.99", @"price",
                                  @"https://item.m.jd.com/product/12172344.html", @"url",
                                  @"https://www.kefux.com/assets/img/qrcode/luobosi_mp.png",@"imageUrl",
                                  @"123", @"id",
                                  @"345", @"categoryCode",
                                  nil];
            [BDUIApis pushAppointChat:self.navigationController withAgentUid:kDefaultAgentUid withTitle:title withCustom:dict];
        }
    }
    
    
}

- (void) getUnreadCountVisitor {
    
    [BDCoreApis getUreadCountVisitorWithResultSuccess:^(NSDictionary *dict) {
        NSNumber *unreadCount = dict[@"data"];
        self.unreadCount = [unreadCount stringValue];
        [self.tableView reloadData];
    } resultFailed:^(NSError *error) {
        
    }];
}



@end







