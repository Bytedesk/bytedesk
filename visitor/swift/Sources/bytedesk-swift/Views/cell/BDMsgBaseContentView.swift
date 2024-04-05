//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

protocol BDMsgBaseContentViewDelegate: AnyObject {
//    // TODO: text点击超链接
//    func linkUrlClicked(_ url: String)
//    // TODO: 打开放大图片
    func imageViewClicked(_ imageView: UIImageView)
//    // TODO: 点击文件消息
//    func fileViewClicked(_ fileUrl: String)
//    // TODO: 点击视频消息
//    func videoViewClicked(_ videoUrl: String)
//    // 机器人问答
    func robotLinkClicked(_ label: String, withKey key: String)
    //
    func robotQuestionClicked(aid: String, question: String, answer: String)
//    // 转人工
//    // func toAgentBtnClicked()
//    // 答案有帮助
//    func robotRateUpBtnClicked(_ messageModel: BDMessageModel)
//    // 答案无帮助
//    func robotRateDownBtnClicked(_ messageModel: BDMessageModel)
}

class BDMsgBaseContentView: UIView {
    
    weak var delegate: BDMsgBaseContentViewDelegate?
    
    var bubbleView: UIView? //UIImageView?
    var messageModel: BDMessageModel?
    //
    var screenWidth: CGFloat? = UIScreen.main.bounds.size.width
    var xMargin: CGFloat = 10
    var yTop: CGFloat = 20

    // used when the view is instantiated in code.
    override init(frame: CGRect) {
        super.init(frame: frame)
        // debugPrint("BDMsgBaseContentView \(#function)")
//        self.isUserInteractionEnabled = true

        setupView()
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func initWithMessageModel(_ message: BDMessageModel?) {
        messageModel = message
        //
        setNeedsLayout()
    }
    
    func setupView() {
        // debugPrint("BDMsgBaseContentView \(#function)")
        if (bubbleView != nil) {
            bubbleView?.removeFromSuperview()
            bubbleView = nil
        }
        //
        bubbleView = UIView()
//        bubbleView!.autoresizingMask = [.flexibleWidth]
        bubbleView!.isUserInteractionEnabled = true
        //
        bubbleView!.layer.cornerRadius = 10.0
        bubbleView!.clipsToBounds = true
//        bubbleView!.layer.borderWidth = 5
//        bubbleView!.layer.borderColor = UIColor.black.cgColor
        addSubview(bubbleView!)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
//        // debugPrint("BDMsgBaseContentView \(#function)")
//        //
        if (self.messageModel!.isSend()) {
            bubbleView?.backgroundColor = UIColor(named: "chat_me_background", in: .module, compatibleWith: nil)
        } else {
            bubbleView?.backgroundColor = UIColor(named: "chat_friend_background", in: .module, compatibleWith: nil)
        }
    }
    
    
}
