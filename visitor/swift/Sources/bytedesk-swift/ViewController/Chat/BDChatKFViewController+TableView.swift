//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit
import SafariServices

extension BDChatKFViewController: UITableViewDelegate, UITableViewDataSource, BDMsgTableViewCellDelegate {
    
    
    // MARK: UITableViewDataSource
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return mMessageArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let messageModel = mMessageArray[indexPath.row]
        if (messageModel.isNotification()) {
            let cell = tableView.dequeueReusableCell(withIdentifier: notifyIdentifier!, for: indexPath) as? BDMsgNotificationViewCell
            cell!.initWithMessageModel(messageModel)
            cell?.tag = indexPath.row
            return cell!
        } else if (messageModel.type == BD_MESSAGE_TYPE_COMMODITY) {
            let cell = tableView.dequeueReusableCell(withIdentifier: commodityIdentifier!, for: indexPath) as? BDMsgCommodityViewCell
            cell!.initWithMessageModel(messageModel)
            cell?.tag = indexPath.row
            cell?.delegate = self
            return cell!
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: msgIdentifier!, for: indexPath) as? BDMsgTableViewCell
            cell!.initWithMessageModel(messageModel)
            cell?.tag = indexPath.row
            cell?.delegate = self
            return cell!
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        // TODO: 需要计算
        let messageModel = mMessageArray[indexPath.row]
        if (messageModel.isNotification()) {
            return 55
        } else {
            //
            if (messageModel.type == BD_MESSAGE_TYPE_TEXT) {
                return messageModel.contentSize().height + messageModel.contentViewInsets().top + messageModel.contentViewInsets().bottom + TIMESTAMP_HEIGHT
            } else if (messageModel.type == BD_MESSAGE_TYPE_ROBOT) {
                return messageModel.contentAttrSize().height + messageModel.contentViewInsets().top + messageModel.contentViewInsets().bottom + TIMESTAMP_HEIGHT
            } else if (messageModel.type == BD_MESSAGE_TYPE_IMAGE) {
                return 250
            } else if (messageModel.type == BD_MESSAGE_TYPE_VOICE) {
                return 90
            } else if (messageModel.type == BD_MESSAGE_TYPE_FILE) {
                return 100
            } else if (messageModel.type == BD_MESSAGE_TYPE_VIDEO) {
                return 120
            } else if (messageModel.type == BD_MESSAGE_TYPE_COMMODITY) {
                return 100
            } else {
                return 80
            }
        }
    }
    
    // MARK: UITableViewDelegate
    // TODO: 123
    // FIXME: 345
    
    // MARK: BDMsgViewCellDelegate
    
    func removeCell(_ tag: Int) {
        print(#function)
        
        let alert = UIAlertController(title: "", message: "确定要删除", preferredStyle: .alert)
        let yesButton = UIAlertAction(title: "确定", style: .default) { _ in
            let indexPath = IndexPath(row: tag, section: 0)
            if indexPath.row < self.mMessageArray.count {
                let itemToDelete = self.mMessageArray[indexPath.row]
                BDCoreApis.deleteMessageByMid(itemToDelete.mid!)
                //
                self.mMessageArray.remove(at: indexPath.row)
                self.mTableView!.deleteRows(at: [indexPath], with: .fade)
            }
        }
        
        let cancelButton = UIAlertAction(title: "取消", style: .default, handler: nil)
        alert.addAction(yesButton)
        alert.addAction(cancelButton)
        present(alert, animated: true, completion: nil)
    }
    
    func recallCell(_ tag: Int) {
        debugPrint("\(#function)")
        
    }
    
    func imageViewClicked(_ imageView: UIImageView) {
//        debugPrint("\(#function)")
        showFullScreenImage(image: imageView.image!)
    }
    
    func robotLinkClicked(_ label: String, withKey key: String) {
        // debugPrint("+tableview \(#function), label: \(label), key: \(key)")
//        BDToast.show(message: label)
        if (key.hasPrefix("helpfull")) {
            //
        } else if (key.hasPrefix("helpless")) {
            //
        } else if (key.hasPrefix("requestAgent")) {
            // 转人工
            requestAgent()
        } else if (key.hasPrefix("httplink")) {
            // 使用SFSafariViewController打开网页
            if let url = URL(string: label) {
                let config = SFSafariViewController.Configuration()
                config.entersReaderIfAvailable = true
                let vc = SFSafariViewController(url: url, configuration: config)
                present(vc, animated: true)
            }
        }
    }
    
    func robotQuestionClicked(aid: String, question: String, answer: String) {
        // debugPrint("tableview \(aid), \(question), \(answer)")
        
        let _ = self.insertLocalMessageAndReload(content: question, type: BD_MESSAGE_TYPE_TEXT, isSend: true)
        
        let _ = self.insertLocalMessageAndReload(content: answer, type: BD_MESSAGE_TYPE_ROBOT, isSend: false)
    }
    
    func insertLocalMessageAndReload(content: String, type: String, isSend: Bool) -> String {
        
        let localId = BDUtils.getGuid()
        // TODO: 插入本地聊天记录，并显示
        let messageModel = BDMessageModel()
        messageModel.mid = localId
        messageModel.type = type
        messageModel.content = content
        messageModel.contentAttr = BDUtils.transformContentToContentAttr(content)
        if (type == BD_MESSAGE_TYPE_IMAGE) {
            messageModel.imageUrl = content
        } else if (type == BD_MESSAGE_TYPE_FILE) {
            messageModel.imageUrl = content
//            messageModel.fileUrl = content
        } else if (type == BD_MESSAGE_TYPE_VOICE) {
            messageModel.imageUrl = content
//            messageModel.voiceUrl = content
        } else if (type == BD_MESSAGE_TYPE_VIDEO) {
            messageModel.imageUrl = content
//            messageModel.videoUrl = content
        }
        messageModel.createdAt = BDUtils.getCurrentDate()
        //
        messageModel.thread?.tid = mThreadModel?.tid
        messageModel.thread?.topic = mThreadModel?.topic
        messageModel.thread?.type = mThreadModel?.type
        //
        if (isSend) {
            messageModel.user?.uid = BDSettings.getUid()
        } else {
            // TODO:
            messageModel.user?.uid = ""
        }
        mMessageArray.append(messageModel)
        //
        let insertIndexPath = IndexPath(row: mMessageArray.count - 1, section: 0)
        mTableView?.beginUpdates()
        mTableView?.insertRows(at: [insertIndexPath], with: .bottom)
        mTableView?.endUpdates()
        
        BDCoreApis.insertMessage(messageModel)
        mTableView?.scrollToRow(at: insertIndexPath, at: .middle, animated: true)
        //
        return localId
    }
    
    
    func showFullScreenImage(image: UIImage) {
        let imageVC = BDImageViewController()
        imageVC.image = image
        
        let imageNavigationController = UINavigationController.init(rootViewController: imageVC)
        self.navigationController!.present(imageNavigationController, animated: true)
    }
}
