//
//  demo_kefu_ocUITestsLaunchTests.m
//  demo_kefu_ocUITests
//
//  Created by 宁金鹏 on 2023/8/15.
//

#import <XCTest/XCTest.h>

@interface demo_kefu_ocUITestsLaunchTests : XCTestCase

@end

@implementation demo_kefu_ocUITestsLaunchTests

+ (BOOL)runsForEachTargetApplicationUIConfiguration {
    return YES;
}

- (void)setUp {
    self.continueAfterFailure = NO;
}

- (void)testLaunch {
    XCUIApplication *app = [[XCUIApplication alloc] init];
    [app launch];

    // Insert steps here to perform after app launch but before taking a screenshot,
    // such as logging into a test account or navigating somewhere in the app

    XCTAttachment *attachment = [XCTAttachment attachmentWithScreenshot:XCUIScreen.mainScreen.screenshot];
    attachment.name = @"Launch Screen";
    attachment.lifetime = XCTAttachmentLifetimeKeepAlways;
    [self addAttachment:attachment];
}

@end
