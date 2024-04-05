//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

class BDMsgTextContentView: BDMsgBaseContentView {
    
    var textLabel: UILabel?
    
    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDMsgTextContentView \(#function)")

        setupView()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    override func setupView() {
        super.setupView()
        // debugPrint("BDMsgTextContentView \(#function)")
        if (textLabel != nil) {
            textLabel?.removeFromSuperview()
            textLabel = nil
        }
        //
        textLabel = UILabel()
        textLabel?.numberOfLines = 0
        textLabel!.font = UIFont.systemFont(ofSize: 16.0)
        textLabel?.textAlignment = .left
        bubbleView!.addSubview(textLabel!)
    }
    
    override func initWithMessageModel(_ message: BDMessageModel?) {
        super.initWithMessageModel(message)
        textLabel?.text = message?.content
        
        setNeedsLayout()
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        // debugPrint("BDMsgTextContentView \(#function)")
        let contentSize = self.messageModel!.contentSize()
        //textLabel!.sizeThatFits(CGSizeMake(200, CGFLOAT_MAX)) //self.messageModel!.contentSize()
        let contentViewInsets = self.messageModel!.contentViewInsets()
        //
        let width = contentViewInsets.left + contentSize.width + contentViewInsets.right
        let height = contentViewInsets.top + contentSize.height + contentViewInsets.bottom
        //
        var textContentFrame: CGRect?
        var bubbleFrame: CGRect?
        var backFrame: CGRect?
        //
        if (self.messageModel!.isSend()) {
            textContentFrame = CGRect(x: contentViewInsets.left, y: 0, width: width, height: height)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.screenWidth! - width - self.xMargin, y: yTop, width: width, height: height)
//            // debugPrint("BDMsgTextContentView 1: \(#function), \(textContentFrame!), \(bubbleFrame!), \(backFrame!)")
        } else {
            textContentFrame = CGRect(x: contentViewInsets.left, y: 0, width: width, height: height)
            bubbleFrame = CGRect(x: 0, y: 0, width: width, height: height)
            backFrame = CGRect(x: self.xMargin, y: self.yTop, width: width, height: height)
//            // debugPrint("BDMsgTextContentView 2: \(#function), \(textContentFrame!), \(bubbleFrame!), \(backFrame!)")
        }
        //
        textLabel!.frame = textContentFrame!
        bubbleView!.frame = bubbleFrame! // FIXME: 宽度显示不正常？！
        self.frame = backFrame!
        //
//        self.backgroundColor = UIColor.black
//        textLabel?.backgroundColor = UIColor.red
//        bubbleView?.backgroundColor = UIColor.blue
    }
    

}
