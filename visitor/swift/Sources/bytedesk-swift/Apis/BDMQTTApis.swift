//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation
import CocoaMQTT
import CocoaMQTTWebSocket

// https://github.com/emqx/CocoaMQTT
// Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDMQTTApis: NSObject, CocoaMQTTDelegate {
    //
    //    var mqtt5: CocoaMQTT5?
    var mqtt: CocoaMQTT?
    //
    class func sharedInstance() -> BDMQTTApis {
        struct Static {
            static let instance = BDMQTTApis()
        }
        return Static.instance
    }
    //
    public func connect() {
        let clientID = String(format: "%@/%@", BDSettings.getUid()!, BDSettings.getClient()!)
        // debugPrint("clientID \(clientID), host: \(BD_MQTT_HOST), port: \(BD_MQTT_PORT), url: \(BD_MQTT_WEBSOCKET_WSS_URL)")
        ///MQTT 3.1.1
//        if (BD_IS_WEBSOCKET_WSS_CONNECTION) {
//            let websocket = CocoaMQTTWebSocket(uri: BD_MQTT_WEBSOCKET_WSS_URL)
//            mqtt = CocoaMQTT(clientID: clientID, host: BD_MQTT_HOST, port: UInt16(BD_MQTT_PORT), socket: websocket)
//        } else {
            mqtt = CocoaMQTT(clientID: clientID, host: BD_MQTT_HOST, port: UInt16(BD_MQTT_PORT))
//        }
        mqtt!.logLevel = .debug
        mqtt!.enableSSL = false //BDConfig.isWebSocketWssConnection()!
//        mqtt.username = ""
//        mqtt.password = ""
//        mqtt.willMessage = CocoaMQTTMessage(topic: "/will", message: "dieout")
        mqtt!.keepAlive = 60
        mqtt!.autoReconnect = true
        mqtt!.autoReconnectTimeInterval = 10
        mqtt!.delegate = self
        _ = mqtt!.connect()
        //
        mqtt!.didReceiveMessage = { mqtt, message, id in
            print("Message received in topic \(message.topic)")
        }
    }
    
    public func isConnected() -> Bool {
        return mqtt!.connState == .connected
    }
        
    //
    public func subscribeTopic(topic: String?) {
        // debugPrint("\(#function)")
    }
    //
    public func unsubscribeTopic(topic: String?) {
        // debugPrint("\(#function)")
    }
    
    public func sendMessageProtobuf(mid: String, type: String, content: String, tid: String, topic: String, threadType: String, threadNickname: String, threadAvatar: String, threadClient: String, extraParam: BDExtraParam?) {
        // debugPrint("\(#function)")
        //
        var threadProto = Bytedesk_Thread()
        threadProto.tid = tid
        threadProto.type = threadType
        threadProto.topic = topic
        threadProto.nickname = threadNickname
        threadProto.avatar = threadAvatar
        threadProto.client = threadClient
        threadProto.timestamp = BDUtils.getCurrentDate()
        threadProto.unreadCount = 0

        var userProto = Bytedesk_User()
        userProto.uid = BDSettings.getUid()!
        userProto.username = BDSettings.getUsername()!
        userProto.nickname = BDSettings.getNickname()!
        userProto.avatar = BDSettings.getAvatar()!

        var userExtraDict = [String: Any]()
        userExtraDict["agent"] = false
        let userExtraJsonData = try? JSONSerialization.data(withJSONObject: userExtraDict)
        let userExtraJsonString = String(data: userExtraJsonData ?? Data(), encoding: .utf8)
        userProto.extra = userExtraJsonString!

        var messageProto = Bytedesk_Message()
        messageProto.mid = mid
        messageProto.type = type
        messageProto.timestamp = BDUtils.getCurrentDate()
        messageProto.client = CLIENT_SWIFT
        messageProto.version = "1"
        messageProto.encrypted = false

        if type == BD_MESSAGE_TYPE_TEXT {
            threadProto.content = content

            var textProto = Bytedesk_Text()
            textProto.content = content
            messageProto.text = textProto
        } else if type == BD_MESSAGE_TYPE_IMAGE {
            threadProto.content = "[图片]"

            var imageProto = Bytedesk_Image()
            imageProto.imageURL = content
            messageProto.image = imageProto
        } else if type == BD_MESSAGE_TYPE_VOICE {
            threadProto.content = "[语音]"

            var voiceProto = Bytedesk_Voice()
            voiceProto.voiceURL = content
            messageProto.voice = voiceProto
        } else if type == BD_MESSAGE_TYPE_FILE {
            threadProto.content = "[文件]"

            var fileProto = Bytedesk_File()
            fileProto.fileURL = content
            messageProto.file = fileProto
        } else if type == BD_MESSAGE_TYPE_VIDEO || type == BD_MESSAGE_TYPE_SHORTVIDEO {
            threadProto.content = "[视频]"

            var videoProto = Bytedesk_Video()
            videoProto.videoOrShortURL = content
            messageProto.video = videoProto
        } else if type == BD_MESSAGE_TYPE_COMMODITY {
            threadProto.content = "[商品]"

            var textProto = Bytedesk_Text()
            textProto.content = content
            messageProto.text = textProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW {
            var previewProto = Bytedesk_Preview()
            previewProto.content = extraParam!.previewContent!
            messageProto.preview = previewProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT {
            var receiptProto = Bytedesk_Receipt()
            receiptProto.mid = extraParam!.receiptMid!
            receiptProto.status = extraParam!.receiptStatus!
            messageProto.receipt = receiptProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_RECALL {
            var recallProto = Bytedesk_Recall()
            recallProto.mid = extraParam!.recallMid!
            messageProto.recall = recallProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST {
            threadProto.content = "[表单]"

            var extraProto = Bytedesk_Extra()
            extraProto.content = content
            messageProto.extra = extraProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER {
            var transferProto = Bytedesk_Transfer()
            transferProto.topic = extraParam!.transferTopic!
            transferProto.content = extraParam!.transferContent!
            messageProto.transfer = transferProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT {
            var transferProto = Bytedesk_Transfer()
            transferProto.topic = extraParam!.transferTopic!
            transferProto.accept = true
            messageProto.transfer = transferProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT {
            var transferProto = Bytedesk_Transfer()
            transferProto.topic = extraParam!.transferTopic!
            transferProto.accept = false
            messageProto.transfer = transferProto
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE {
            // TODO: 会话邀请
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT {
            // TODO: 接受会话邀请
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT {
            // TODO: 拒绝会话邀请
        } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE {
            var extraProto = Bytedesk_Extra()
            extraProto.content = content
            messageProto.extra = extraProto
        } else if type.hasPrefix("notification_webrtc") {
            // TODO: webrtc
        } else {
            var textProto = Bytedesk_Text()
            textProto.content = content
            messageProto.text = textProto
        }
        messageProto.user = userProto
        messageProto.thread = threadProto
        // TODO: 发送
        do {
            let message = try CocoaMQTTMessage(topic: topic, payload: [UInt8](messageProto.serializedData()), qos: .qos1, retained: false)
            mqtt!.publish(message)
        } catch {}
    }
    
    public func sendMessageProtobuf(mid: String, type: String, content: String, thread: BDThreadModel, extraParam: BDExtraParam?) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: type, content: content, tid: thread.tid!, topic: thread.topic!, threadType: thread.type!, threadNickname: thread.visitor!.nickname!, threadAvatar: thread.visitor!.avatar!, threadClient: thread.client!, extraParam: extraParam)
    }
    
    public func sendTextMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_TEXT, content: content, thread: thread, extraParam: nil)
    }
    
    public func sendImageMessageProtobuf(mid: String, imageUrl: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_IMAGE, content: imageUrl, thread: thread, extraParam: nil)
    }
    
    public func sendVoiceMessageProtobuf(mid: String, voiceUrl: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_VOICE, content: voiceUrl, thread: thread, extraParam: nil)
    }
    
    public func sendFileMessageProtobuf(mid: String, fileUrl: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_FILE, content: fileUrl, thread: thread, extraParam: nil)
    }
    
    public func sendVideoMessageProtobuf(mid: String, videoUrl: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_VIDEO, content: videoUrl, thread: thread, extraParam: nil)
    }
    
    public func sendCommodityMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_COMMODITY, content: content, thread: thread, extraParam: nil)
    }
    
    public func sendInviteRateMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE, content: content, thread: thread, extraParam: nil)
    }
    
    public func sendFormRequestMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        // debugPrint("\(#function)")
        sendMessageProtobuf(mid: mid, type: BD_MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST, content: content, thread: thread, extraParam: nil)
    }
    
    public func sendPreviewMessageProtobuf(thread: BDThreadModel, previewContent: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.previewContent = previewContent
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW, content: "content", thread: thread, extraParam: extraParam)
    }
    
    public func sendRecallMessageProtobuf(thread: BDThreadModel, recallMid: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.recallMid = recallMid
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW, content: "content", thread: thread, extraParam: extraParam)
    }
    
    public func sendReceiptReceivedMessageProtobuf(thread: BDThreadModel, receiptMid: String) {
        // debugPrint("\(#function)")
        sendReceiptMessageProtobuf(thread: thread, receiptMid: receiptMid, receiptStatus: BD_MESSAGE_STATUS_RECEIVED)
    }
    
    public func sendReceiptReadMessageProtobuf(thread: BDThreadModel, receiptMid: String) {
        // debugPrint("\(#function)")
        sendReceiptMessageProtobuf(thread: thread, receiptMid: receiptMid, receiptStatus: BD_MESSAGE_STATUS_READ)
    }
    
    public func sendReceiptMessageProtobuf(thread: BDThreadModel, receiptMid: String, receiptStatus: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.receiptMid = receiptMid
        extraParam.receiptStatus = receiptStatus
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT, content: "", thread: thread, extraParam: extraParam)
    }
    
    public func sendTransferMessageProtobuf(thread: BDThreadModel, transferTopic: String, transferContent: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.transferTopic = transferTopic
        extraParam.transferContent = transferContent
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER, content: "", thread: thread, extraParam: extraParam)
    }
    
    public func sendTransferAcceptMessageProtobuf(thread: BDThreadModel, transferTopic: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.transferTopic = transferTopic
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT, content: "", thread: thread, extraParam: extraParam)
    }
    
    public func sendTransferRejectMessageProtobuf(thread: BDThreadModel, transferTopic: String) {
        // debugPrint("\(#function)")
        let extraParam = BDExtraParam()
        extraParam.transferTopic = transferTopic
        sendMessageProtobuf(mid: UUID().uuidString, type: BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT, content: "", thread: thread, extraParam: extraParam)
    }
    //
    public func disconnect() {
        // debugPrint("\(#function)")
        mqtt!.disconnect()
    }
    
    // MARK: CocoaMQTTDelegate
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        // debugPrint("\(#function)")
    }
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        // debugPrint("发送消息：\(#function)")
    }
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
        // debugPrint("发送回执：\(#function)")
    }
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage mqttMessage: CocoaMQTTMessage, id: UInt16 ) {
        // debugPrint("收到消息：\(#function), id \(id)")
        do {
            let message = try Bytedesk_Message(serializedData: Data(mqttMessage.payload))
            // debugPrint("protobuf type \(message.type), content \(message.text.content)")
            // 会话
            let threadModel = BDThreadModel()
            threadModel.tid = message.thread.tid
            threadModel.type = message.thread.type
            threadModel.content = message.thread.content
            threadModel.timestamp = message.thread.timestamp
            threadModel.topic = message.thread.topic
            threadModel.client = message.client
            threadModel.visitor?.nickname = message.thread.nickname
            threadModel.visitor?.avatar = message.thread.avatar
            // 消息
            let messageModel = BDMessageModel()
            messageModel.mid = message.mid
            messageModel.type = message.type
            messageModel.client = message.client
            messageModel.status = BD_MESSAGE_STATUS_STORED
            messageModel.createdAt = message.timestamp
            //
            messageModel.user?.uid = message.user.uid
            messageModel.user?.nickname = message.user.nickname
            messageModel.user?.avatar = message.user.avatar
            //
            messageModel.thread = threadModel
            //
            let type = message.type
            var sendReceipt = false
            if type == BD_MESSAGE_TYPE_TEXT {
                sendReceipt = true
                messageModel.content = message.text.content
            } else if type == BD_MESSAGE_TYPE_IMAGE {
                sendReceipt = true
                messageModel.imageUrl = message.image.imageURL
            } else if type == BD_MESSAGE_TYPE_VOICE {
                sendReceipt = true
                messageModel.voiceUrl = message.voice.voiceURL
            } else if type == BD_MESSAGE_TYPE_FILE {
                sendReceipt = true
                messageModel.fileUrl = message.file.fileURL
            } else if type == BD_MESSAGE_TYPE_VIDEO || type == BD_MESSAGE_TYPE_SHORTVIDEO {
                sendReceipt = true
                messageModel.videoUrl = message.video.videoOrShortURL
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_PREVIEW {
                // TODO: 消息预知
                BDNotify.notifyMessagePreview(messageModel)
                return
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_RECEIPT {
                // 消息回执：送达/已读
                let receiptMid = message.receipt.mid
                let receiptStatus = message.receipt.status
                // TODO: 更新数据库中消息送达、已读状态
//                BDDBApis.sharedInstance.updateMessage(receiptMid, withStatus: receiptStatus)
                BDNotify.notifyReloadCell(receiptMid, status: receiptStatus)
                return
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_RECALL {
                let recallMid = message.recall.mid
                // TODO: 消息撤回，数据库中删除
//                BDDBApis.sharedInstance.deleteMessage(recallMid)
                BDNotify.notifyMessageRecall(recallMid)
                return
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER {
                // 会话转接
                // 过滤掉自己发送的消息
                if BDSettings.getUid() == message.user.uid {
                    return
                }
                BDNotify.notifyTransferMessage(messageModel)
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_ACCEPT {
                // 接受会话转接
                // 过滤掉自己发送的消息
                if BDSettings.getUid() == message.user.uid {
                    return
                }
                BDNotify.notifyTransferAcceptMessage(messageModel)
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_TRANSFER_REJECT {
                // 拒绝会话转接
                // 过滤掉自己发送的消息
                if BDSettings.getUid() == message.user.uid {
                    return
                }
                BDNotify.notifyTransferRejectMessage(messageModel)
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE {
                // TODO: 会话邀请
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_ACCEPT {
                // TODO: 接受会话邀请
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_REJECT {
                // TODO: 拒绝会话邀请
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_INVITE_RATE {
                // TODO: 邀请评价
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_RATE_RESULT {
                // TODO: 评价结果
            } else if type == BD_MESSAGE_TYPE_NOTIFICATION_WEBRTC_INVITE_VIDEO {
                
            } else {
                // TODO: 其他消息类型
                messageModel.content = message.text.content
            }
            // 非自己发送 && 非系统通知消息 && 非当前会话
//            NSLog("%@, getCurrentTid %@, tid %@", #function, BDSettings.getCurrentTid(), threadModel.tid)
//            if messageModel.isSend! || messageModel.type.hasPrefix("notification_") || BDSettings.getCurrentTid() == threadModel.tid {
//                threadModel.setUnread_count(0)
//            } else {
//                threadModel.setUnread_count(1)
//            }
            // 发送消息回执：送达、已读
            if sendReceipt && !messageModel.isSend() {
//                sendReceiptReceivedMessageProtobuf(thread: threadModel, receiptMid: message.mid)
            }
            //
            if type == BD_MESSAGE_TYPE_NOTIFICATION_THREAD ||
                type == BD_MESSAGE_TYPE_NOTIFICATION_THREAD_REENTRY ||
                //
                type == BD_MESSAGE_TYPE_TEXT ||
                type == BD_MESSAGE_TYPE_IMAGE ||
                type == BD_MESSAGE_TYPE_VOICE ||
                type == BD_MESSAGE_TYPE_FILE ||
                type == BD_MESSAGE_TYPE_VIDEO ||
                type == BD_MESSAGE_TYPE_SHORTVIDEO ||
                
                type == BD_MESSAGE_TYPE_LOCATION ||
                type == BD_MESSAGE_TYPE_LINK ||
                type == BD_MESSAGE_TYPE_EVENT ||
                type == BD_MESSAGE_TYPE_CUSTOM ||
                type == BD_MESSAGE_TYPE_RED_PACKET ||
                type == BD_MESSAGE_TYPE_COMMODITY ||
                
                type == BD_MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE ||
                type == BD_MESSAGE_TYPE_NOTIFICATION_VISITOR_CLOSE ||
                type == BD_MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE {
                //
                // NSLog("0")
                BDCoreApis.insertMessage(messageModel)
                // 通知界面
                BDNotify.notifyMessageAdd(messageModel)
            }
//
            // 非自己发送的消息
            if !messageModel.isSend() {
                // 收到消息震动
//                if BDSettings.shouldVibrateWhenReceiveMessage() {
//                    BDNotify.sharedInstance().backgroundMessageReceivedVibrate()
//                }
//                // 收到消息播放提示音
//                if BDSettings.shouldRingWhenReceiveMessage() {
//                    BDNotify.sharedInstance().playMessageReceivedSound()
//                }
            }
            
        } catch {
            // debugPrint("收到消息 解析 \(error)")
        }
        
    }
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        // debugPrint("\(#function)")
    }
    
    ///
    public func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopics topics: [String]) {
        // debugPrint("\(#function)")
    }
    
    ///
    public func mqttDidPing(_ mqtt: CocoaMQTT) {
        // debugPrint("\(#function)")
    }
    
    ///
    public func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
        // debugPrint("\(#function)")
    }
    
    ///
    public func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        // debugPrint("长链接断开：\(#function)")
    }
    
    /// Manually validate SSL/TLS server certificate.
    ///
    /// This method will be called if enable  `allowUntrustCACertificate`
//    @objc public func mqtt(_ mqtt: CocoaMQTT, didReceive trust: SecTrust, completionHandler: @escaping (Bool) -> Void) {
//        // debugPrint("\(#function)")
//    }
//
//    @objc public func mqttUrlSession(_ mqtt: CocoaMQTT, didReceiveTrust trust: SecTrust, didReceiveChallenge challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
//        // debugPrint("\(#function)")
//    }
//
//    ///
//    @objc public func mqtt(_ mqtt: CocoaMQTT, didPublishComplete id: UInt16) {
//        // debugPrint("\(#function)")
//    }
//
//    ///
//    @objc public func mqtt(_ mqtt: CocoaMQTT, didStateChangeTo state: CocoaMQTTConnState) {
//        // debugPrint("\(#function)")
//    }
    
    
    
}
