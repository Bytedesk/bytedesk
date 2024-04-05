//
//  BDImageViewController.m
//  bytedesk-oc
//
//  Created by 宁金鹏 on 2023/9/13.
//

#import "BDImageViewController.h"
@import Photos;

@interface BDImageViewController ()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIButton *saveButton;

@end

@implementation BDImageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"%s", __PRETTY_FUNCTION__);
    self.view.backgroundColor = [UIColor blackColor];
    
    self.imageView = [[UIImageView alloc] initWithFrame:CGRectZero];
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.imageView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.imageView];
    if (self.image) {
        [self.imageView setImage:self.image];
    }
    
    [NSLayoutConstraint activateConstraints:@[
        [self.imageView.topAnchor constraintEqualToAnchor:self.view.topAnchor],
        [self.imageView.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor],
        [self.imageView.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor],
        [self.imageView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor]
    ]];
    
    self.saveButton = [UIButton buttonWithType:UIButtonTypeSystem];
    [self.saveButton setTitle:@"保存到相册" forState:UIControlStateNormal];
    self.saveButton.translatesAutoresizingMaskIntoConstraints = NO;
    [self.saveButton addTarget:self action:@selector(saveButtonTapped) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.saveButton];
    
    [NSLayoutConstraint activateConstraints:@[
        [self.saveButton.centerXAnchor constraintEqualToAnchor:self.view.centerXAnchor],
        [self.saveButton.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-16]
    ]];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissViewController)];
    [self.view addGestureRecognizer:tapGesture];
}

- (void)setImage:(UIImage *)image {
    NSLog(@"%s image:%@", __PRETTY_FUNCTION__, image);
    _image = image;
    [self.imageView setImage:image];
}

- (void)dismissViewController {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)saveButtonTapped {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    if (self.image) {
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (status == PHAuthorizationStatusAuthorized) {
                    [self saveImageToAlbum:self.image];
                } else {
                    [self showAuthorizationAlert];
                }
            });
        }];
    } else {
        NSLog(@"image is nil");
    }
}

- (void)saveImageToAlbum:(UIImage *)image {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
        [PHAssetChangeRequest creationRequestForAssetFromImage:image];
    } completionHandler:^(BOOL success, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (success) {
                [self showSaveSuccessAlert];
            } else if (error) {
                NSLog(@"Error saving image to photo album: %@", error.localizedDescription);
                [self showSaveFailureAlert];
            }
        });
    }];
}

- (void)showAuthorizationAlert {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"无保存相册权限" message:@"请授权保存到相册." preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil];
    [alert addAction:okAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)showSaveSuccessAlert {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"保存成功" message:@"图片已经成功保存到相册." preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self.navigationController dismissViewControllerAnimated:YES completion:nil];
    }];
    [alert addAction:okAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)showSaveFailureAlert {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"保存错误" message:@"保存图片到相册失败，请重试." preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:nil];
    [alert addAction:okAction];
    [self presentViewController:alert animated:YES completion:nil];
}

@end
