//
//  SceneDelegate.swift
//  demo_kefu_uikit
//
//  Created by 宁金鹏 on 2023/9/2.
//

import UIKit
import bytedesk_swift

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        guard let _ = (scene as? UIWindowScene) else { return }
        //
        let vc = KFApiTableViewController()
        let navRoot = UINavigationController(rootViewController: vc)
        self.window?.rootViewController = navRoot
        self.window?.backgroundColor = UIColor.white
        
        // 萝卜丝：注册截屏通知
        NotificationCenter.default.addObserver(self, selector: #selector(userDidTakeScreenshot), name: UIApplication.userDidTakeScreenshotNotification, object: nil)
        
        // 用于调试，测试主题颜色
//        self.window?.overrideUserInterfaceStyle = UIUserInterfaceStyle.dark
//        self.window?.overrideUserInterfaceStyle = UIUserInterfaceStyle.light
//        self.window.overrideUserInterfaceStyle = UITraitCollection.currentTraitCollection.userInterfaceStyle; // 跟随系统的dark Mode
    }
    
    let kDefaultWorkGroupWid = "201807171659201"
    @objc func userDidTakeScreenshot() {
        // TODO: 调用萝卜丝截屏接口，开发中
        print("检测到截屏显示 联系客服、意见反馈、分享截图，注：模拟器不支持，仅支持真机")
        
        BDUIApis.sharedInstance().showScreenshot(window: self.window!,
                                backgroundColor: UIColor.black,
                                workGroupWid: kDefaultWorkGroupWid,
                                showKefu: true,
                                showFeedback: true,
                                showShare: true) {
            
        } feedbackCallback: {
            
        } shareCallback: {
            
        }
    }

    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // This occurs shortly after the scene enters the background, or when its session is discarded.
        // Release any resources associated with this scene that can be re-created the next time the scene connects.
        // The scene may re-connect later, as its session was not necessarily discarded (see `application:didDiscardSceneSessions` instead).
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
        // Use this method to restart any tasks that were paused (or not yet started) when the scene was inactive.
    }

    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
        // This may occur due to temporary interruptions (ex. an incoming phone call).
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
        // Use this method to undo the changes made on entering the background.
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // to restore the scene back to its current state.
    }


}

