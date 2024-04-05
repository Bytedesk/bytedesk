//
//  ViewController.m
//  demo_kefu_oc
//
//  Created by 宁金鹏 on 2023/8/15.
//

#import "ViewController.h"

// TODO: 1. 接口列表
// TODO: 2. 成功调用现有framework SDK
// TODO: 3. swiftui版framework SDK
// TODO: 4. swiftui版package SDK
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    UIView *hello = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
    [hello setBackgroundColor:[UIColor redColor]];
    [self.view addSubview:hello];
}

@end
