//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/10.
//

import UIKit

extension BDChatKFViewController {
    
    // MARK: 显示或隐藏键盘
    
    @objc func handleWillShowKeyboard(_ notification: Notification) {
        // debugPrint("\(#function)")
//        let keyboardRect = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue).cgRectValue
//        let duration = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? Double
//        let animationCurve = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber
        //
        let keyboardData = keyboardInfoFromNotification(notification)
        keyboardY = keyboardData.endFrame.origin.y
        keyboardHeight = keyboardData.endFrame.size.height
        //
        UIView.animate(withDuration: keyboardData.animationDuration) {
            // debugPrint("1. \(#function)")
            //
            var inputViewFrame = self.mInputView?.frame
            inputViewFrame?.origin.y = self.keyboardY! - self.INPUTBAR_HEIGHT
            self.mInputView?.frame = inputViewFrame!
            //
            var tableViewInsets = self.mTableView?.contentInset
            tableViewInsets?.bottom = self.keyboardHeight! + self.INPUTBAR_HEIGHT
            self.mTableView?.contentInset = tableViewInsets!
            self.mTableView?.scrollIndicatorInsets = tableViewInsets!
        }
        //
        tableViewScrollToBottom(true)
    }
    
    @objc func handleWillHideKeyboard(_ notification: Notification) {
        // debugPrint("\(#function)")
        //
        let keyboardData = keyboardInfoFromNotification(notification)
        keyboardY = keyboardData.endFrame.origin.y
        keyboardHeight = keyboardData.endFrame.size.height
        //
        UIView.animate(withDuration: keyboardData.animationDuration) {
            // debugPrint("1. \(#function)")
            //
            var inputViewFrame = self.mInputView?.frame
            inputViewFrame?.origin.y = self.keyboardY! - self.INPUTBAR_HEIGHT
            self.mInputView?.frame = inputViewFrame!
            //
            var plusViewFrame = self.mPlusView?.frame
            plusViewFrame?.origin.y = inputViewFrame!.origin.y + self.INPUTBAR_HEIGHT
            self.mPlusView?.frame = plusViewFrame!
            //
            var emotionViewFrame = self.mEmotionView?.frame
            emotionViewFrame?.origin.y = inputViewFrame!.origin.y + self.INPUTBAR_HEIGHT
            self.mEmotionView?.frame = emotionViewFrame!
        }
        //
        tableViewScrollToBottom(true)
    }
    
    /**
     */
    public func keyboardInfoFromNotification(_ notification: Notification) -> (beginFrame: CGRect, endFrame: CGRect, animationCurve: UIView.AnimationOptions, animationDuration: Double) {
        let userInfo = (notification as NSNotification).userInfo!
        let beginFrameValue = userInfo[UIResponder.keyboardFrameBeginUserInfoKey] as! NSValue
        let endFrameValue = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue
        let animationCurve = userInfo[UIResponder.keyboardAnimationCurveUserInfoKey] as! NSNumber
        let animationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as! NSNumber

        return (
            beginFrame:         beginFrameValue.cgRectValue,
            endFrame:           endFrameValue.cgRectValue,
            animationCurve:     UIView.AnimationOptions(rawValue: UInt(animationCurve.uintValue << 16)),
            animationDuration:  animationDuration.doubleValue)
    }
}
