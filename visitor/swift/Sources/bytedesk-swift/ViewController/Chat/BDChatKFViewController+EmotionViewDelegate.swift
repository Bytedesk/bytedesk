//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit

extension BDChatKFViewController: BDEmotionViewDelegate {

    // MARK: BDEmotionViewDelegate
    func emotionFaceButtonPressed(_ tag: Int) {
        // debugPrint("chatkf: \(#function), tag: \(tag)")
        // var emojiText = String(describing:self.emotionToTextDictionary?.object(forKey: String(format: "Expression_%ld", tag)))!
        guard var emotionText = self.mEmotionToTextDictionary?["Expression_\(tag)"] as? String else { return }
        
        // 取余为0，即整除
        if tag % 21 == 0 {
            emotionText = "删除"
        }
        
        let content = self.mInputView!.inputTextView!.text
        let contentLength = content!.count
        var newContent: String?
        
        if emotionText == "删除" {
            if contentLength > 0 {
                if content![content!.index(before: content!.endIndex)] == "]" {
                    if let range = content!.range(of: "[", options: .backwards) {
                        newContent = String(content![..<range.lowerBound])
                    } else {
                        newContent = String(content![..<content!.index(before: content!.endIndex)])
                    }
                } else {
                    newContent = String(content![..<content!.index(before: content!.endIndex)])
                }
                
                self.mInputView!.inputTextView!.text = newContent
            }
        } else {
            let updatedContent = "\(content ?? "")\(emotionText)"
            self.mInputView!.inputTextView!.text = updatedContent
        }
        
        self.mInputView!.textViewDidChange(self.mInputView!.inputTextView!)
    }
    
    func emotionViewSendButtonPressed() {
        // debugPrint("chatkf: \(#function)")
        let content = mInputView?.inputTextView?.text
        if (content!.isEmpty) {
            return
        }
        sendMessage(content!)
    }
    
}
