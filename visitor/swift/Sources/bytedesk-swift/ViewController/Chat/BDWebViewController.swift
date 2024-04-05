//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/15.
//

import UIKit
import WebKit

class BDWebViewViewController: UIViewController, WKNavigationDelegate {
    var webView: WKWebView!
    var safariButton: UIBarButtonItem!
    
    var mChatUrl: String?
    var mIsPush: Bool = false
    
    func initWithChatUrl(_ chatUrl: String, isPush: Bool) {
        print("1.\(#function)")
        mChatUrl = chatUrl
        mIsPush = isPush
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // 创建 Safari 按钮
        safariButton = UIBarButtonItem(title: "Safari", style: .plain, target: self, action: #selector(openInSafari))
        navigationItem.rightBarButtonItem = safariButton
        
        if !mIsPush {
            navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .close, target: self, action: #selector(handleCloseButtonEvent))
        }
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // 创建 WKWebView
        // 在present的情况下，不能放到viewdidload里面，否则会导致窗口大小不正确，输入框不能正常显示
        webView = WKWebView(frame: view.bounds)
        webView.navigationDelegate = self
        view.addSubview(webView)
        // 加载网页
        loadWebPage()
    }
    
    func loadWebPage() {
        if let url = URL(string: mChatUrl!) {
            let request = URLRequest(url: url)
            webView.load(request)
        }
    }
    
    @objc func openInSafari() {
        if let url = webView.url {
            UIApplication.shared.open(url)
        }
    }
    
    @objc func handleCloseButtonEvent() {
        self.navigationController?.dismiss(animated: true)
    }
}
