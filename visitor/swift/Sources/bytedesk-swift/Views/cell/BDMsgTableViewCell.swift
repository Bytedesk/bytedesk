//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/5.
//

import UIKit

protocol BDMsgTableViewCellDelegate: AnyObject {
    
//    func saveImageCell(with tag: Int)
    func removeCell(_ tag: Int)
    func recallCell(_ tag: Int)
////    func avatarClicked(_ messageModel: BDMessageModel)
//    func linkUrlClicked(_ url: String)
    func imageViewClicked(_ imageView: UIImageView)
//    func fileViewClicked(_ fileUrl: String)
//    func videoViewClicked(_ videoUrl: String)
//    func sendErrorStatusButtonClicked(_ model: BDMessageModel)
    func robotLinkClicked(_ label: String, withKey key: String)
    //
    func robotQuestionClicked(aid: String, question: String, answer: String)
//    func robotRateUpBtnClicked(_ messageModel: BDMessageModel)
//    func robotRateDownBtnClicked(_ messageModel: BDMessageModel)
////    func shouldReloadTable()
}

class BDMsgTableViewCell: UITableViewCell, BDMsgBaseContentViewDelegate {
    
    weak var delegate: BDMsgTableViewCellDelegate?
    
    var timestampLabel: UILabel?
    var baseContentView: BDMsgBaseContentView?
    
    var messageModel: BDMessageModel?
    
    // used when the view is instantiated in code.
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        // debugPrint("BDMsgTableViewCell \(#function)")
        //
        self.selectionStyle = UITableViewCell.SelectionStyle.none
        self.backgroundColor = UIColor.systemGroupedBackground
//        self.backgroundColor = UIColor.green
    }

    // for when the view is created via Interface Builder.
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        fatalError("init(coder:) has not been implemented")
    }
    
    func initWithMessageModel(_ msgModel: BDMessageModel?) {
        messageModel = msgModel
        //
        setupView()
        //
        setNeedsLayout()
    }
    
    func setupView() {
        // debugPrint("BDMsgTableViewCell \(#function)")
        if (timestampLabel != nil) {
            timestampLabel?.removeFromSuperview()
            timestampLabel = nil
        }
        if (baseContentView != nil) {
            baseContentView?.removeFromSuperview()
            baseContentView = nil
        }
        //
        timestampLabel = UILabel()
        timestampLabel?.font = UIFont.systemFont(ofSize: 11)
        timestampLabel?.textColor = UIColor.systemGray
        timestampLabel?.text = messageModel?.createdAt
        addSubview(timestampLabel!)
        //
        if messageModel!.type == BD_MESSAGE_TYPE_TEXT {
            baseContentView = BDMsgTextContentView.init(frame: CGRect.zero)
        } else if messageModel!.type == BD_MESSAGE_TYPE_IMAGE {
            baseContentView = BDMsgImageContentView.init(frame: CGRect.zero)
        } else if messageModel!.type == BD_MESSAGE_TYPE_VOICE {
            baseContentView = BDMsgVoiceContentView.init(frame: CGRect.zero)
        } else if messageModel!.type == BD_MESSAGE_TYPE_FILE {
            baseContentView = BDMsgFileContentView.init(frame: CGRect.zero)
        } else if messageModel!.type == BD_MESSAGE_TYPE_VIDEO {
//            bubbleView = BDMsgVideoContentView.init(frame: CGRect.zero)
        } else if messageModel!.type == BD_MESSAGE_TYPE_ROBOT
                    || messageModel!.type == BD_MESSAGE_TYPE_ROBOTV2 {
            baseContentView = BDMsgRobotContentView.init(frame: .zero)
        } else {
            // TODO: 当前版本暂不支持查看此消息，请升级。暂未处理的类型，全部当做text类型处理
            baseContentView = BDMsgTextContentView.init(frame: CGRect.zero)
        }
        baseContentView!.delegate = self
        addSubview(baseContentView!)
        baseContentView?.initWithMessageModel(messageModel)
        //
        let longPressGesture = UILongPressGestureRecognizer(target: self, action: #selector(handleLongPress(_:)))
        addGestureRecognizer(longPressGesture)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        let createdAtSize = self.messageModel?.createdAtSize()
        timestampLabel?.frame = CGRect(x: (self.bounds.size.width - createdAtSize!.width)/2, y: 0.5, width: createdAtSize!.width, height: createdAtSize!.height)
    }
    
    // MARK: BDMsgBaseContentViewDelegate
    
    func imageViewClicked(_ imageView: UIImageView) {
        delegate?.imageViewClicked(imageView)
    }
    
    func robotLinkClicked(_ label: String, withKey key: String) {
        // debugPrint("cell \(#function)")
        delegate?.robotLinkClicked(label, withKey: key)
    }
    
    func robotQuestionClicked(aid: String, question: String, answer: String) {
        // debugPrint("cell \(#function)")
        delegate?.robotQuestionClicked(aid: aid, question: question, answer: answer)
    }
    
    // MARK:
    @objc private func handleLongPress(_ gesture: UILongPressGestureRecognizer) {
        debugPrint("\(#function)")
//        guard gesture.state == .began, let tableView = superview as? UITableView, let indexPath = tableView.indexPath(for: self) else {
//            return
//        }
        becomeFirstResponder()
        
        let copyMenu = UIMenuItem(title: "复制", action: #selector(copyAction(_:)))
        let deleteMenu = UIMenuItem(title: "删除", action: #selector(deleteAction(_:)))
        
        let menuController = UIMenuController.shared
        menuController.menuItems = [copyMenu, deleteMenu]
        menuController.showMenu(from: baseContentView!, rect: baseContentView!.frame)
    }
    
    override var canBecomeFirstResponder: Bool {
        return true
    }
    
    override func copy(_ sender: Any?) {
//        debugPrint("\(#function)")
        // 复制操作的实现
        let pasteboard = UIPasteboard.general
        if (self.messageModel?.type == BD_MESSAGE_TYPE_TEXT) {
            pasteboard.string = self.messageModel?.content
        } else if (self.messageModel?.type == BD_MESSAGE_TYPE_IMAGE) {
            pasteboard.string = self.messageModel?.imageUrl
        } else if (self.messageModel?.type == BD_MESSAGE_TYPE_FILE) {
            pasteboard.string = self.messageModel?.fileUrl
        } else if (self.messageModel?.type == BD_MESSAGE_TYPE_VOICE) {
            pasteboard.string = self.messageModel?.voiceUrl
        } else if (self.messageModel?.type == BD_MESSAGE_TYPE_VIDEO) {
            pasteboard.string = self.messageModel?.videoUrl
        } else if (self.messageModel?.type == BD_MESSAGE_TYPE_ROBOT) {
            pasteboard.string = self.messageModel?.content
        } else {
            pasteboard.string = self.messageModel?.content
        }
        //
        resignFirstResponder()
    }
    
    @objc private func copyAction(_: Any?) {
//        debugPrint("\(#function)")
        // 使用 copy(_:) 方法进行复制操作
        copy(nil)
    }
    
    @objc private func deleteAction(_: Any?) {
        debugPrint("\(#function)")
        // 删除操作的实现
        delegate?.removeCell(self.tag)
    }
}
