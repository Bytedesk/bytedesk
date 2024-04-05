//
//  KFFeedbackViewController.m
//  demo
//
//  Created by 萝卜丝 on 2018/12/30.
//  Copyright © 2018 bytedesk.com. All rights reserved.
//

#import "BDFeedbackViewController.h"
#import "BDFeedbackRecordViewController.h"
#import "BDVideoCompress.h"
#import "UITextView+Placeholder.h"

#import <AFNetworking/UIImageView+AFNetworking.h>
#import "BDCoreApis.h"
#import "BDSettings.h"
#import "BDUtils.h"
#import "BDUIApis.h"

#define DeviceHeight [[UIScreen mainScreen] bounds].size.height
#define DeviceWidth [[UIScreen mainScreen] bounds].size.width

@interface BDFeedbackViewController ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate>
//
@property(nonatomic, strong) NSString *mAdminUid;
@property(nonatomic, strong) NSMutableDictionary *mCategoryDict;

@property(nonatomic, strong) NSString *mCategoryName;
@property(nonatomic, strong) NSString *mCategoryCid;
@property(nonatomic, strong) NSString *mImageUrl;

@property(nonatomic, strong) UITextView *contentTextView;
@property(nonatomic, strong) UIImageView *imageView;
@property(nonatomic, strong) UITextField *contactTextField;

@property(nonatomic, assign) BOOL mIsPush;
@property(nonatomic, assign) BOOL forceEnableBackGesture;

@property (nonatomic, strong) UIImagePickerController *mImagePickerController;

@end

@implementation BDFeedbackViewController

@synthesize contentTextView, imageView, contactTextField;

- (void)initWithUid:(NSString *)uid withPush:(BOOL)isPush; {
    NSLog(@"1.%s", __PRETTY_FUNCTION__);
    self.mAdminUid = uid;
    self.mIsPush = isPush;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"2.%s", __PRETTY_FUNCTION__);
    self.title = @"意见反馈";
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
    self.mCategoryName = @"";
    self.mCategoryCid = @"";
    self.mImageUrl = @"";
    
    // TODO: 待上线
    [self getFeedbackCategories];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"我的反馈" style:UIBarButtonItemStylePlain target:self action:@selector(showFeedbackRecords)];
    //
    if (self.mIsPush) {
        //
    }
    else {
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemClose target:self action:@selector(handleCloseButtonEvent:)];
//        self.navigationItem.leftBarButtonItem = [UIBarButtonItem qmui_closeItemWithTarget:self action:@selector(handleCloseButtonEvent:)];
    }
    self.forceEnableBackGesture = YES;// 当系统的返回按钮被屏蔽的时候，系统的手势返回也会跟着失效，所以这里要手动强制打开手势返回
    //
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissKeyboard:)];
    singleTap.numberOfTapsRequired = 1;
    singleTap.numberOfTouchesRequired = 1;
    [self.view addGestureRecognizer:singleTap];
    
    // Dismiss the keyboard if user double taps on the background
    UITapGestureRecognizer *doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissKeyboard:)];
    doubleTap.numberOfTapsRequired = 2;
    doubleTap.numberOfTouchesRequired = 1;
    [self.view addGestureRecognizer:doubleTap];
    
    //
    self.mImagePickerController = [[UIImagePickerController alloc] init];
    self.mImagePickerController.delegate = self;
    self.mImagePickerController.allowsEditing = YES;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"3.%s", __PRETTY_FUNCTION__);
}

- (void)viewWillLayoutSubviews {
    NSLog(@"4.%s", __PRETTY_FUNCTION__);
    [super viewWillLayoutSubviews];
    //
    contentTextView = [[UITextView alloc] initWithFrame:CGRectMake(10, 120, DeviceWidth - 20, 100)];
    contentTextView.font = [UIFont systemFontOfSize: 16];
    contentTextView.placeholder = @"请将您的建议或您遇到的问题告诉我们，我们会认真的听取并及时反馈";
    contentTextView.layer.cornerRadius = 5.0;
    contentTextView.layer.masksToBounds = YES;
//    contentTextView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    contentTextView.layer.borderColor = [UIColor systemGrayColor].CGColor;
    contentTextView.layer.borderWidth = 0.5;
    [self.view addSubview:contentTextView];
    //
    imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, 250, 100, 100)];
    [imageView setImage:[UIImage imageNamed:@"AlbumAddBtn.png" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
    imageView.userInteractionEnabled = YES;
//    [imageView ]
    imageView.layer.cornerRadius = 5.0;
    imageView.layer.masksToBounds = YES;
//    imageView.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    imageView.layer.borderColor = [UIColor systemGrayColor].CGColor;
    imageView.layer.borderWidth = 0.5;
    [self.view addSubview:imageView];
    //
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(presentViewController:)];
    singleTap.numberOfTapsRequired = 1;
    singleTap.numberOfTouchesRequired = 1;
    [imageView addGestureRecognizer:singleTap];
    //
    contactTextField = [[UITextField alloc] initWithFrame:CGRectMake(10, 380, DeviceWidth - 20, 40)];
    contactTextField.font = [UIFont systemFontOfSize: 16];
    contactTextField.clearsOnBeginEditing = YES;
    contactTextField.clearsOnInsertion = YES;
    contactTextField.placeholder = @"联系方式：请输入您的手机号、微信或邮箱";
    contactTextField.layer.cornerRadius = 5.0;
    contactTextField.layer.masksToBounds = YES;
//    contactTextField.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    contactTextField.layer.borderColor = [UIColor systemGrayColor].CGColor;
    contactTextField.layer.borderWidth = 0.5;
    [self.view addSubview:contactTextField];
    //
    UIButton *submitButton = [[UIButton alloc] initWithFrame:CGRectMake(10, 450, DeviceWidth - 20, 40)];
    [submitButton setBackgroundColor:[UIColor systemOrangeColor]];
    [submitButton setTitle:@"提交意见反馈" forState:UIControlStateNormal];
    [submitButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [submitButton addTarget:self action:@selector(submitButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    submitButton.layer.cornerRadius = 20.0;
    submitButton.layer.masksToBounds = YES;
//    submitButton.layer.borderColor = [UIColor colorWithWhite:0.0 alpha:0.2].CGColor;
    submitButton.layer.borderColor = [UIColor systemGrayColor].CGColor;
    submitButton.layer.borderWidth = 0.5;
    [self.view addSubview:submitButton];
    //
//    [self initRobotView];
}


#pragma mark -

- (void)submitButtonClicked:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    NSString *content = contentTextView.text;
    NSString *mobile = contactTextField.text;
    NSLog(@"%s, content=%@, contact=%@", __PRETTY_FUNCTION__, content, mobile);
    
    if ([content stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]].length == 0 ||
        [mobile stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]].length == 0) {
        [BDUIApis showTipWithVC:self withMessage:@"留言内容 或 联系方式不能为空"];
        return;
    }
    
    [BDUIApis showTipWithVC:self withMessage:@"提交反馈中，请稍后"];
    __weak __typeof(self)weakSelf = self;
    [BDCoreApis createFeedback:self.mAdminUid
                     withCid:self.mCategoryCid
                 withContent:content
                  withMobile:mobile
                 withFileUrl:self.mImageUrl
               resultSuccess:^(NSDictionary *dict) {

                   NSString *message = [dict objectForKey:@"message"];
                   NSNumber *status_code = [dict objectForKey:@"status_code"];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
                            [BDUIApis showTipWithVC:weakSelf withMessage:message];
                        } else {
                            [BDUIApis showTipWithVC:weakSelf withMessage:message];
                        }
                    });
                   
               } resultFailed:^(NSError *error) {
                   NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
                   if (error) {
                       [BDUIApis showErrorWithVC:weakSelf withMessage:error.localizedDescription];
                   }
               }];
}



#pragma mark -

- (void)showFeedbackRecords {
    BDFeedbackRecordViewController *bdFeedbackRecordVC = [[BDFeedbackRecordViewController alloc] init];
    [self.navigationController pushViewController:bdFeedbackRecordVC animated:YES];
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


#pragma mark - UIScrollViewDelegate

-(void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
//    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self.view endEditing:YES];
}

#pragma mark - TouchGestures

-(void)dismissKeyboard:(id)sender {
    [self.view endEditing:YES];
}


#pragma mark - KFFormSingleImageViewCellDelegate

- (void)presentViewController:(id)sender {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    [self authorizationPresentAlbumViewController];
}

- (void)authorizationPresentAlbumViewController {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
//    if ([QMUIAssetsManager authorizationStatus] == QMUIAssetAuthorizationStatusNotDetermined) {
//        [QMUIAssetsManager requestAuthorization:^(QMUIAssetAuthorizationStatus status) {
//            dispatch_async(dispatch_get_main_queue(), ^{
//                [self presentAlbumViewController];
//            });
//        }];
//    } else {
        [self presentAlbumViewController];
//    }
}

- (void)presentAlbumViewController {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    self.mImagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self presentViewController:self.mImagePickerController animated:YES completion:nil];
}

#pragma mark UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [picker dismissViewControllerAnimated:YES completion:^{
        [self performSelector:@selector(dealWithImage:) withObject:info];
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)dealWithImage:(NSDictionary *)info {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    //
    NSString *mediaType = [info objectForKey:UIImagePickerControllerMediaType];
    if([mediaType isEqualToString:@"public.movie"]) {
        NSLog(@"%s 1", __PRETTY_FUNCTION__);
        //被选中的是视频
        //        // [QMUITips showError:@"请选择图片" inView:self.view hideAfterDelay:2];
        // TODO：上传视频
        NSURL *videoURL = [info objectForKey:UIImagePickerControllerMediaURL];
        NSLog(@"videoURL %@", [videoURL absoluteString]);
        //
//        [BDVideoCompress compressVideoWithVideoUrl:videoURL withBiteRate:@(1500 * 1024) withFrameRate:@(30) withVideoWidth:@(960) withVideoHeight:@(540) compressComplete:^(id responseObjc) {
//            //
//            NSString *filePathStr = [responseObjc objectForKey:@"urlStr"];
//            NSURL *compressvideourl = [NSURL fileURLWithPath:filePathStr];
//            //
//            NSData *videoData = [NSData dataWithContentsOfURL:compressvideourl];
//            [self uploadVideoData:videoData];
//        }];
    } else if([mediaType isEqualToString:@"public.image"]) {
        NSLog(@"%s 2", __PRETTY_FUNCTION__);
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

- (void)uploadImageData:(NSData *)imageData {
    //
    [BDUIApis showTipWithVC:self withMessage:@"图片上传中"];
    NSString *localId = [[NSUUID UUID] UUIDString];
    NSString *imageName = [NSString stringWithFormat:@"%@_%@.png", [BDSettings getUsername], [BDUtils getCurrentTimeString]];
    [BDCoreApis uploadImageData:imageData withImageName:imageName withLocalId:localId resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s, uploadImageData: %@", __PRETTY_FUNCTION__, dict);
        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {
            
            self.mImageUrl = dict[@"data"];
            [self.imageView setImageWithURL:[NSURL URLWithString:self.mImageUrl] placeholderImage:[UIImage imageNamed:@"AlbumAddBtn.png" inBundle:[NSBundle bundleForClass:self.class] compatibleWithTraitCollection:nil]];
//            self.mFileUrl = imageUrl;
            NSLog(@"imageUrl %@", self.mImageUrl);
//
        } else {
            [BDUIApis showErrorWithVC:self withMessage:@"上传图片错误"];
        }
    } resultFailed:^(NSError *error) {
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

#pragma mark - 分类

- (void)getFeedbackCategories {
    //
    self.mCategoryDict = [[NSMutableDictionary alloc] init];
    [BDCoreApis getFeedbackCategories:self.mAdminUid resultSuccess:^(NSDictionary *dict) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, dict);

        NSNumber *status_code = [dict objectForKey:@"status_code"];
        if ([status_code isEqualToNumber:[NSNumber numberWithInt:200]]) {

            NSMutableArray *categoryArray = dict[@"data"];
            for (NSDictionary *categoryDict in categoryArray) {
                NSString *cid = categoryDict[@"cid"];
                NSString *name = categoryDict[@"name"];
//                NSLog(@"cid: %@, name: %@", cid, name);
                self.mCategoryDict[name] = cid;
            }
            //
            

        } else {
            NSString *message = [dict objectForKey:@"message"];
            [BDUIApis showErrorWithVC:self withMessage:message];
        }

    } resultFailed:^(NSError *error) {
        NSLog(@"%s %@", __PRETTY_FUNCTION__, error);
        if (error) {
            [BDUIApis showErrorWithVC:self withMessage:error.localizedDescription];
        }
    }];
}

- (void)chooseCategory {
    
//    if ([[self.mCategoryDict allKeys] count] == 0) {
//        // [QMUITips showError:@"请稍后或在后台创建分类" inView:self.view hideAfterDelay:2];
//        return;
//    }
    
//    QMUIDialogSelectionViewController *dialogViewController = [[QMUIDialogSelectionViewController alloc] init];
//    dialogViewController.title = @"选择分类";
//    dialogViewController.items = [self.mCategoryDict allKeys];
//    dialogViewController.cellForItemBlock = ^(QMUIDialogSelectionViewController *aDialogViewController, QMUITableViewCell *cell, NSUInteger itemIndex) {
//        cell.accessoryType = UITableViewCellAccessoryNone;// 移除点击时默认加上右边的checkbox
//    };
//    dialogViewController.heightForItemBlock = ^CGFloat (QMUIDialogSelectionViewController *aDialogViewController, NSUInteger itemIndex) {
//        return 54;// 修改默认的行高，默认为 TableViewCellNormalHeight
//    };
//    dialogViewController.didSelectItemBlock = ^(QMUIDialogSelectionViewController *aDialogViewController, NSUInteger itemIndex) {
//        [aDialogViewController hide];
//        //
//        NSString *name = [[self.mCategoryDict allKeys] objectAtIndex:itemIndex];
//        NSString *cid = [self.mCategoryDict objectForKey:name];
////        NSLog(@"name: %@, cid: %@", name, cid);
//        //
//        self.mCategoryCid = cid;
//        self.mCategoryName = name;
//        //
//        [self.tableView reloadData];
//    };
//    [dialogViewController show];
}


@end
