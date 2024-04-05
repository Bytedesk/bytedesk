//
//  AppDelegate.swift
//  demo_kefu_uikit
//
//  Created by 宁金鹏 on 2023/9/2.
//

import UIKit
import bytedesk_swift

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        // 初始化萝卜丝智能客服
        initBytedesk()
        
        return true
    }
    
    // 开发文档：https://github.com/pengjinning/bytedesk-ios
    // 获取appkey：登录后台->渠道->APP->appkey列
    // 获取subDomain，也即企业号：登录后台->客服->账号->企业号列
    // MARK: 开发者需要到客服管理后台获取真实数据, 并将appkey 和 subdomain两个参数替换为真实值
    //注：管理后台网址 https://www.weikefu.net/admin/
    func initBytedesk() {
        // 初始化
        BDCoreApis.initBytedesk(appkey: "a3f79509-5cb6-4185-8df9-b1ce13d3c655", subDomain: "vip") { loginResult in
            print("萝卜丝：初始化成功")
        } onFailure: { error in
            print("萝卜丝：初始化失败: \(error)")
        }
    }
    
    

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }


}

