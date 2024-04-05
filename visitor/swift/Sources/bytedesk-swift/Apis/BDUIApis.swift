//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import UIKit
import ObjectiveC.runtime

// Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDUIApis: NSObject {
    //
    public class func sharedInstance() -> BDUIApis {
        struct Static {
            static let instance = BDUIApis()
        }
        return Static.instance
    }
    
    static public func pushWorkGroupChat(navController: UINavigationController, wid: String, title: String) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithWorkGroupWid(wid: wid, title, true)
        navController.pushViewController(chatVC, animated: true)
    }
    
    static public func pushWorkGroupChat(navController: UINavigationController, wid: String, title: String, custom: [String: Any]) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithWorkGroupWid(wid: wid, title, true, custom: custom)
        navController.pushViewController(chatVC, animated: true)
    }
    
    static public func presentWorkGroupChat(navController: UINavigationController, wid: String, title: String) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithWorkGroupWid(wid: wid, title, false)
        let chatNavigationController = UINavigationController.init(rootViewController: chatVC)
        navController.present(chatNavigationController, animated: true)
    }
    
    static public func presentWorkGroupChat(navController: UINavigationController, wid: String, title: String, custom: [String: Any]) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithWorkGroupWid(wid: wid, title, false, custom: custom)
        let chatNavigationController = UINavigationController.init(rootViewController: chatVC)
        navController.present(chatNavigationController, animated: true)
    }
    
    static public func pushAppointChat(navController: UINavigationController, uid: String, title: String) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithAgentUid(uid: uid, title, true)
        navController.pushViewController(chatVC, animated: true)
    }
    
    static public func pushAppointChat(navController: UINavigationController, uid: String, title: String, custom: [String: Any]) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithAgentUid(uid: uid, title, true, custom: custom)
        navController.pushViewController(chatVC, animated: true)
    }
    
    static public func presentAppointChat(navController: UINavigationController, uid: String, title: String) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithAgentUid(uid: uid, title, false)
        let chatNavigationController = UINavigationController.init(rootViewController: chatVC)
        navController.present(chatNavigationController, animated: true)
    }
    
    static public func presentAppointChat(navController: UINavigationController, uid: String, title: String, custom: [String: Any]) {
        let chatVC = BDChatKFViewController()
        chatVC.initWithAgentUid(uid: uid, title, false, custom: custom)
        let chatNavigationController = UINavigationController.init(rootViewController: chatVC)
        navController.present(chatNavigationController, animated: true)
    }
    
    static public func pushFeedback(navController: UINavigationController, adminUid: String, title: String) {
        let feedbackVC = BDFeedbackViewController()
        feedbackVC.initWithUid(adminUid, withPush: true)
        navController.pushViewController(feedbackVC, animated: true)
    }
    
    static public func presentFeedback(navController: UINavigationController, adminUid: String, title: String) {
        let feedbackVC = BDFeedbackViewController()
        feedbackVC.initWithUid(adminUid, withPush: false)
        let feedbackNavigationController = UINavigationController.init(rootViewController: feedbackVC)
        navController.present(feedbackNavigationController, animated: true)
    }
    
    static public func pushWebView(navController: UINavigationController, chatUrl: String, title: String) {
        let webVC = BDWebViewViewController()
        webVC.initWithChatUrl(chatUrl, isPush: true)
        navController.pushViewController(webVC, animated: true)
    }
    
    static public func presentWebView(navController: UINavigationController, chatUrl: String, title: String) {
        let webVC = BDWebViewViewController()
        webVC.initWithChatUrl(chatUrl, isPush: false)
        let webNavigationController = UINavigationController.init(rootViewController: webVC)
        navController.present(webNavigationController, animated: true)
    }
    
    // MARK: 截屏反馈
    public typealias ScreenshotCallbackBlock = () -> Void
    
    public func showScreenshot(window: UIWindow,
                        backgroundColor: UIColor,
                        workGroupWid: String,
                        showKefu: Bool,
                        showFeedback: Bool,
                        showShare: Bool,
                        kefuCallback: ScreenshotCallbackBlock?,
                        feedbackCallback: ScreenshotCallbackBlock?,
                        shareCallback: ScreenshotCallbackBlock?) {
        
        // 人为截屏, 模拟用户截屏行为, 获取所截图片
        let imageScreenShoot = imageWithScreenshot()
        
        let width = 150
        let height = 250
        
        // 添加显示
        let screenshotFrame = CGRect(x: window.frame.size.width - CGFloat(width) - 5,
                                     y: window.frame.size.height/2 - CGFloat(height)/2,
                                     width: CGFloat(width),
                                     height: CGFloat(height))
        let screenshotView = UIView(frame: screenshotFrame)
        screenshotView.backgroundColor = backgroundColor
        screenshotView.layer.cornerRadius = 10
        
        // 截图
        let screenShootImageView = UIImageView(image: imageScreenShoot)
        screenShootImageView.frame = CGRect(x: 10, y: 10, width: 100, height: 105)
        screenshotView.addSubview(screenShootImageView)
        
        let closeButton = UIButton(frame: CGRect(x: width - 20, y: 5, width: 20, height: 20))
        closeButton.setImage(UIImage(named: "xmark", in: .module, with: nil), for: .normal)
        closeButton.setTitleColor(UIColor.white, for: .normal)
        closeButton.addTarget(self, action: #selector(closeScreenShoot(_:)), for: .touchUpInside)
        screenshotView.addSubview(closeButton)
        objc_setAssociatedObject(closeButton, "closeButton", screenshotView, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        
        if showKefu {
            let kefuButton = UIButton(frame: CGRect(x: 10, y: 120, width: 100, height: 20))
            kefuButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
            kefuButton.setTitle("联系客服", for: .normal)
            kefuButton.backgroundColor = UIColor.gray
            kefuButton.addTarget(self, action: #selector(kefuScreenShoot), for: .touchUpInside)
            screenshotView.addSubview(kefuButton)
        }
        
        if showFeedback {
            let feedbackButton = UIButton(frame: CGRect(x: 10, y: 145, width: 100, height: 20))
            feedbackButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
            feedbackButton.setTitle("意见反馈", for: .normal)
            feedbackButton.backgroundColor = UIColor.gray
            feedbackButton.addTarget(self, action: #selector(feedbackScreenShoot), for: .touchUpInside)
            screenshotView.addSubview(feedbackButton)
        }
        
        if showShare {
            let shareButton = UIButton(frame: CGRect(x: 10, y: 170, width: 100, height: 20))
            shareButton.titleLabel?.font = UIFont.systemFont(ofSize: 15)
            shareButton.setTitle("分享截图", for: .normal)
            shareButton.backgroundColor = UIColor.gray
            shareButton.addTarget(self, action: #selector(shareScreenShoot), for: .touchUpInside)
            screenshotView.addSubview(shareButton)
        }
        
        window.addSubview(screenshotView)
    }
    
    @objc func closeScreenShoot(_ sender: UIButton) {
        print(#function)
        if let feedbackView = objc_getAssociatedObject(sender, "closeButton") as? UIView {
            feedbackView.removeFromSuperview()
        }
    }
    
    @objc func kefuScreenShoot() {
        print(#function)
    }
    
    @objc func feedbackScreenShoot() {
        print(#function)
    }
    
    @objc func shareScreenShoot() {
        print(#function)
    }
    
    // 截取当前屏幕
    func dataWithScreenshotInPNGFormat() -> Data? {
        var imageSize = CGSize.zero
        let orientation = UIApplication.shared.windows.first?.windowScene?.interfaceOrientation ?? .unknown
        
        if orientation.isPortrait {
            imageSize = UIScreen.main.bounds.size // 竖屏
        } else {
            imageSize = CGSize(width: UIScreen.main.bounds.size.height,
                               height: UIScreen.main.bounds.size.width) // 横屏
        }
        
        UIGraphicsBeginImageContextWithOptions(imageSize, false, 0)
        guard let context = UIGraphicsGetCurrentContext() else {
            return nil
        }
        
        for window in UIApplication.shared.windows {
            context.saveGState()
            context.translateBy(x: window.center.x, y: window.center.y)
            context.concatenate(window.transform)
            context.translateBy(x: -window.bounds.size.width * window.layer.anchorPoint.x,
                                y: -window.bounds.size.height * window.layer.anchorPoint.y)
                
            if orientation == .landscapeLeft {
                context.rotate(by: CGFloat.pi/2)
                context.translateBy(x: 0, y: -imageSize.width)
            } else if orientation == .landscapeRight {
                context.rotate(by: -CGFloat.pi/2)
                context.translateBy(x: -imageSize.height, y: 0)
            } else if orientation == .portraitUpsideDown {
                context.rotate(by: CGFloat.pi)
                context.translateBy(x: -imageSize.width, y: -imageSize.height)
            }
            
            if window.responds(to: #selector(UIView.drawHierarchy(in:afterScreenUpdates:))) {
                window.drawHierarchy(in: window.bounds, afterScreenUpdates: true)
            } else {
                window.layer.render(in: context)
            }
            
            context.restoreGState()
        }
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image?.pngData()
    }
    
    // 返回截取到的图片
    func imageWithScreenshot() -> UIImage? {
        if let imageData = dataWithScreenshotInPNGFormat() {
            return UIImage(data: imageData)
        }
        return nil
    }
    
    
    // MARK: 工具函数
    
    static func showTip(with viewController: UIViewController, message: String) {
        let alertController = UIAlertController(title: nil, message: "", preferredStyle: .alert)
        if let firstSubview = alertController.view.subviews.first,
            let alertContentView = firstSubview.subviews.first {
            for subSubView in alertContentView.subviews {
                subSubView.backgroundColor = UIColor(red: 141/255.0, green: 0/255.0, blue: 254/255.0, alpha: 1.0)
            }
        }
        let attributedString = NSMutableAttributedString(string: message)
        attributedString.addAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], range: NSRange(location: 0, length: attributedString.length))
        alertController.setValue(attributedString, forKey: "attributedTitle")
        viewController.present(alertController, animated: true, completion: nil)
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            alertController.dismiss(animated: true, completion: nil)
        }
    }

    static func showError(with viewController: UIViewController, message: String) {
        let alertController = UIAlertController(title: nil, message: "", preferredStyle: .alert)
        if let firstSubview = alertController.view.subviews.first,
            let alertContentView = firstSubview.subviews.first {
            for subSubView in alertContentView.subviews {
                subSubView.backgroundColor = UIColor(red: 141/255.0, green: 0/255.0, blue: 254/255.0, alpha: 1.0)
            }
        }
        let attributedString = NSMutableAttributedString(string: message)
        attributedString.addAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], range: NSRange(location: 0, length: attributedString.length))
        alertController.setValue(attributedString, forKey: "attributedTitle")
        viewController.present(alertController, animated: true, completion: nil)
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            alertController.dismiss(animated: true, completion: nil)
        }
    }
    
}
