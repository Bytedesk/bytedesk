//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit

extension BDChatKFViewController {
    
    // MARK: 监听通知
    
    func registerNotifications() {
        // debugPrint("\(#function)")
        // 注册键盘即将出现通知
        NotificationCenter.default.addObserver(self, selector: #selector(handleWillShowKeyboard(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        // 注册键盘即将隐藏通知
        NotificationCenter.default.addObserver(self, selector: #selector(handleWillHideKeyboard(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        //
        NotificationCenter.default.addObserver(self, selector: #selector(notifyQueueAccept(_:)), name: .kQueueAccept, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyThreadClose(_:)), name: .kThreadClose, object: nil)
        //
        NotificationCenter.default.addObserver(self, selector: #selector(notifyReloadCellStatus(_:)), name: .kMessageLocalID, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyMessageAdd(_:)), name: .kMessageAdd, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyMessageDelete(_:)), name: .kMessageDelete, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyMessagePreview(_:)), name: .kMessagePreview, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyMessageRecall(_:)), name: .kMessageRecall, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(notifyMessageStatus(_:)), name: .kMessageStatus, object: nil)
        //
        NotificationCenter.default.addObserver(self, selector: #selector(notifyKickoff(_:)), name: .kKickOff, object: nil)
    }
    
    func unregisterNotifications() {
        // debugPrint("\(#function)")
        // 移除键盘监听
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    
    @objc func notifyQueueAccept(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyThreadClose(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyReloadCellStatus(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyMessageAdd(_ notification: Notification) {
        // debugPrint("\(#function)")
        // TODO: 此方法简单粗暴，待改进，直接插入显示就好
        self.reloadTableData(true)
    }
    
    @objc func notifyMessageDelete(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyMessagePreview(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyMessageRecall(_ notification: Notification) {
        // debugPrint("\(#function)")
        
    }
    
    @objc func notifyMessageStatus(_ notification: Notification) {
        // debugPrint("\(#function)")
    }
    
    @objc func notifyKickoff(_ notification: Notification) {
        // debugPrint("\(#function)")
    }
    
    
}
