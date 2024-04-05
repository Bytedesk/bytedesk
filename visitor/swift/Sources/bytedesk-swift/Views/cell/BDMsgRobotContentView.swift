//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

class BDMsgRobotContentView: BDMsgBaseContentView, UITextViewDelegate {
    
    var contentTextView: UITextView?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDMsgRobotContentView \(#function)")
        self.isUserInteractionEnabled = true

        setupView()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    override var canBecomeFirstResponder: Bool {
        return true
    }
    
    override func setupView() {
        super.setupView()
        // debugPrint("BDMsgRobotContentView \(#function)")
        if (contentTextView == nil) {
            contentTextView?.removeFromSuperview()
            contentTextView = nil
        }
        //
        contentTextView = UITextView()
        contentTextView?.backgroundColor = UIColor.clear
        contentTextView?.delegate = self
        contentTextView?.isEditable = false
        contentTextView?.showsVerticalScrollIndicator = false
        contentTextView?.dataDetectorTypes = UIDataDetectorTypes.all
        contentTextView?.isUserInteractionEnabled = true
        contentTextView?.isScrollEnabled = false
        //
//        contentTextView!.layer.cornerRadius = 10.0
//        contentTextView!.clipsToBounds = true
        bubbleView!.addSubview(contentTextView!)
    }
    
    override func initWithMessageModel(_ message: BDMessageModel?) {
        super.initWithMessageModel(message)
        contentTextView?.attributedText = message?.contentAttr
        
        setNeedsLayout()
    }
    
    
    override func layoutSubviews() {
        super.layoutSubviews()
        // debugPrint("BDMsgRobotContentView \(#function)")
        let contentAttrSize = self.messageModel!.contentAttrSize()
        let contentViewInsets = self.messageModel!.contentViewInsets()
        //
        let width = contentViewInsets.left + contentAttrSize.width + contentViewInsets.right
        let height = contentAttrSize.height
        //
        var contentAttrFrame: CGRect?
        var bubbleFrame: CGRect?
        var backFrame: CGRect?
        //
        if (self.messageModel!.isSend()) {
            contentAttrFrame = CGRect(x: 0, y: 0, width: width, height: height)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.screenWidth! - width - self.xMargin, y: yTop, width: width, height: height)
//            // debugPrint("BDMsgRobotContentView 1: \(#function), \(contentAttrFrame!), \(bubbleFrame!), \(backFrame!)")
        } else {
            contentAttrFrame = CGRect(x: 0, y: 0, width: width, height: height)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.xMargin, y: self.yTop, width: width, height: height)
//            // debugPrint("BDMsgRobotContentView 2: \(#function), \(contentAttrFrame!), \(bubbleFrame!), \(backFrame!)")
        }
        contentTextView!.frame = contentAttrFrame!
        bubbleView!.frame = bubbleFrame!
        self.frame = backFrame!
        //
//        self.backgroundColor = UIColor.black
//        contentTextView?.backgroundColor = UIColor.red
//        bubbleView?.backgroundColor = UIColor.blue
    }
    
    // MARK: UITextViewDelegate
    
    func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange, interaction: UITextItemInteraction) -> Bool {
        // debugPrint("\(#function) URL: \(URL.absoluteString)")

        var key = URL.absoluteString
        if key.hasPrefix("robot://") {
            // key的格式如下：robot://aid??question??answer
            let arrayWithLabel = key.components(separatedBy: "??")
            //
            let keyWithoutLabel = arrayWithLabel[0]
            let arrayWithKey = keyWithoutLabel.components(separatedBy: "//")
            //
            let aid = arrayWithKey.last ?? ""
            //
            var question = arrayWithLabel[1]
            question = BDUtils.decodeString(question) ?? ""
            //
            var answer = arrayWithLabel.last ?? ""
            answer = BDUtils.decodeString(answer) ?? ""
            //
            print("\(#function) aid: \(aid), question: \(question), answer: \(answer)")
            
            delegate?.robotQuestionClicked(aid: aid, question: question, answer: answer)
//
            // 返回 true 除了监听点击事件之外还监听长按事件，长按弹出copy share按钮，点击open会跳转到应用外打开链接
            // 返回 false 只监听链接的点击事件，不监听其他事件，例如长按弹出copy share按钮，这样我们可以自定义弹出按钮，然后进行copy和应用内打开链接等操作
            return false
        } else {
            let label = key
            key = "httplink"
            
            delegate?.robotLinkClicked(label, withKey: key)
            
            return false
        }
    }
    
}
