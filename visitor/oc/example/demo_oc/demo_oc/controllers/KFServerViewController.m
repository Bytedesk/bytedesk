//
//  KFServerViewController.m
//  demo
//
//  Created by 萝卜丝 on 2019/3/28.
//  Copyright © 2019 bytedesk.com. All rights reserved.
//

#import "KFServerViewController.h"
#import <bytedesk-core/bdcore.h>

@interface KFServerViewController ()

@end

@implementation KFServerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"自定义服务器";
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    // Dismiss the keyboard if user double taps on the background
    UITapGestureRecognizer *doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissKeyboard:)];
    doubleTap.numberOfTapsRequired = 2;
    doubleTap.numberOfTouchesRequired = 1;
    [self.view addGestureRecognizer:doubleTap];
    
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if (section == 0) {
        return @"REST服务器,注意：以'/'结尾";
    } else if (section == 1) {
        return @"websocket长连接";
    }
    return @"";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // Configure the cell...
    if (!cell){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CellIdentifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    if (indexPath.section == 0) {
        
        cell.textLabel.text = @"http地址";
        cell.detailTextLabel.text = [BDConfig getRestApiHost];
        
    } else if (indexPath.section == 1) {
        
        cell.textLabel.text = @"websocket地址";
        cell.detailTextLabel.text = [BDConfig getMqttWebSocketWssURL];
    } else if (indexPath.section == 2) {
        
        cell.textLabel.text = @"恢复默认值";
    }
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 0) {
        
        // 修改为自己的服务器地址, 注意：地址以 http或https开头, '/'结尾
        //  [BDConfig setRestApiHost:@"https://api.bytedesk.com/"];
        
    }
    else if (indexPath.section == 1) {
        
        // 修改websocket服务器地址
//        [BDConfig setMqttWebSocketWssURL:@"wss://www.bytedesk.com/websocket"];
        
    } else if (indexPath.section == 3) {
//        // DDLogInfo(@"恢复默认值");
        
        [BDConfig restoreDefault];
        
        [self.tableView reloadData];
    }
}

#pragma mark - 双击界面

-(void)dismissKeyboard:(id)sender {
    [self.view endEditing:YES];
}


@end
