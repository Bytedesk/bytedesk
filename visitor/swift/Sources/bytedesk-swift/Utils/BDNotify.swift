//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/9.
//

import Foundation
import AudioToolbox

extension Notification.Name {
    //
    public static let kOAuthResult = Notification.Name(BD_NOTIFICATION_OAUTH_RESULT)
    public static let kInitStatus = Notification.Name(BD_NOTIFICATION_INIT_STATUS)
    public static let kConnectionStatus = Notification.Name(BD_NOTIFICATION_CONNECTION_STATUS)
    //
    public static let kThreadAdd = Notification.Name(BD_NOTIFICATION_THREAD_ADD)
    public static let kThreadDelete = Notification.Name(BD_NOTIFICATION_THREAD_DELETE)
    public static let kThreadClose = Notification.Name(BD_NOTIFICATION_THREAD_CLOSE)
    public static let kThreadUpdate = Notification.Name(BD_NOTIFICATION_THREAD_UPDATE)
    //
    public static let kTransfer = Notification.Name(BD_NOTIFICATION_TRANSFER)
    public static let kTransferAccept = Notification.Name(BD_NOTIFICATION_TRANSFER_ACCEPT)
    public static let kTransferReject = Notification.Name(BD_NOTIFICATION_TRANSFER_REJECT)
    //
    public static let kQueueAdd = Notification.Name(BD_NOTIFICATION_QUEUE_ADD)
    public static let kQueueDelete = Notification.Name(BD_NOTIFICATION_QUEUE_DELETE)
    public static let kQueueAccept = Notification.Name(BD_NOTIFICATION_QUEUE_ACCEPT)
    public static let kQueueUpdate = Notification.Name(BD_NOTIFICATION_QUEUE_UPDATE)
    //
    public static let kMessageLocalID = Notification.Name(BD_NOTIFICATION_MESSAGE_LOCALID)
    public static let kMessageAdd = Notification.Name(BD_NOTIFICATION_MESSAGE_ADD)
    public static let kMessageDelete = Notification.Name(BD_NOTIFICATION_MESSAGE_DELETE)
    public static let kMessagePreview = Notification.Name(BD_NOTIFICATION_MESSAGE_PREVIEW)
    public static let kMessageRecall = Notification.Name(BD_NOTIFICATION_MESSAGE_RECALL)
    public static let kMessageStatus = Notification.Name(BD_NOTIFICATION_MESSAGE_STATUS)
    //
    public static let kUploadPercentage = Notification.Name(BD_UPLOAD_NOTIFICATION_PERCENTAGE)
    public static let kUploadError = Notification.Name(BD_UPLOAD_NOTIFICATION_ERROR)
    //
    public static let kProfileUpdate = Notification.Name(BD_NOTIFICATION_PROFILE_UPDATE)
    public static let kKickOff = Notification.Name(BD_NOTIFICATION_KICKOFF)
}

public class BDNotify: NSObject {
    //
    class func sharedInstance() -> BDNotify {
        struct Static {
            static let instance = BDNotify()
        }
        return Static.instance
    }
    //
    static func notifyOAuthResult(_ isSuccess: Bool) {
        NotificationCenter.default.post(name: .kOAuthResult, object: NSNumber(value: isSuccess))
    }
    
    static func notifyInitStatus(_ status: String) {
        NotificationCenter.default.post(name: .kInitStatus, object: status)
    }
    
    static func notifyConnnectionStatus(_ status: String) {
        NotificationCenter.default.post(name: .kConnectionStatus, object: status)
    }
    
    static func notifyThreadAdd(_ threadModel: BDThreadModel) {
        NotificationCenter.default.post(name: .kThreadAdd, object: threadModel)
    }
    
    static func notifyThreadDelete(_ tid: String) {
        NotificationCenter.default.post(name: .kThreadDelete, object: tid)
    }
    
    static func notifyThreadClose(_ tid: String) {
        NotificationCenter.default.post(name: .kThreadDelete, object: tid)
    }
    
    static func notifyQueueAdd(_ queueModel: BDQueueModel) {
        NotificationCenter.default.post(name: .kQueueAdd, object: queueModel)
    }
    
    static func notifyQueueDelete(_ qid: String) {
        NotificationCenter.default.post(name: .kQueueDelete, object: qid)
    }
    
    static func notifyQueueAccept(_ qid: String) {
        NotificationCenter.default.post(name: .kQueueAccept, object: qid)
    }
    
    static func notifyReloadCellSuccess(_ localId: String) {
        notifyReloadCell(localId, status: BD_MESSAGE_STATUS_STORED)
    }
    
    static func notifyReloadCell(_ localId: String, status: String) {
        let dict: [String: Any] = ["localId": localId, "status": status]
        NotificationCenter.default.post(name: .kMessageLocalID, object: dict)
    }
    
    static func notifyMessageAdd(_ messageModel: BDMessageModel) {
        NotificationCenter.default.post(name: .kMessageAdd, object: messageModel)
    }
    
    static func notifyMessageTextSend(_ tid: String, content: String, localId: NSNumber) {
        let messageModel = BDMessageModel()
        messageModel.type = BD_MESSAGE_TYPE_TEXT
        messageModel.content = content
        messageModel.client = BDSettings.getClient()
        //
        messageModel.user?.uid = BDSettings.getUid()
        messageModel.user?.username = BDSettings.getUsername()
        messageModel.user?.nickname = BDSettings.getNickname()
        messageModel.user?.avatar = BDSettings.getAvatar()
        
        NotificationCenter.default.post(name: .kMessageAdd, object: messageModel)
    }
    
    static func notifyMessageImageSend(_ tid: String, imageUrl: String, localId: NSNumber) {
        let messageModel = BDMessageModel()
        messageModel.type = BD_MESSAGE_TYPE_IMAGE
        messageModel.imageUrl = imageUrl
        messageModel.client = BDSettings.getClient()
        //
        messageModel.user?.uid = BDSettings.getUid()
        messageModel.user?.username = BDSettings.getUsername()
        messageModel.user?.nickname = BDSettings.getNickname()
        messageModel.user?.avatar = BDSettings.getAvatar()
        
        NotificationCenter.default.post(name: .kMessageAdd, object: messageModel)
    }
    
    static func notifyMessageDelete(_ messageId: NSNumber) {
        NotificationCenter.default.post(name: .kMessageDelete, object: messageId)
    }
    
    static func notifyMessagePreview(_ message: BDMessageModel) {
        NotificationCenter.default.post(name: .kMessagePreview, object: message)
    }
    
    static func notifyMessageRecall(_ mid: String) {
        NotificationCenter.default.post(name: .kMessageRecall, object: mid)
    }
    
    static func notifyMessage(_ localId: NSNumber, status: String) {
        let dict: [String: Any] = ["status": status, "localId": localId.stringValue]
        NotificationCenter.default.post(name: .kMessageStatus, object: dict)
    }
    
    static func notifyThreadUpdate() {
        NotificationCenter.default.post(name: .kThreadUpdate, object: nil)
    }
    
    static func notifyQueueUpdate() {
        NotificationCenter.default.post(name: .kQueueUpdate, object: nil)
    }
    
    static func notifyProfileUpdate() {
        NotificationCenter.default.post(name: .kProfileUpdate, object: nil)
    }
    
   static func notifyKickoff(_ content: String) {
       NotificationCenter.default.post(name: .kKickOff, object: content)
    }
    
    ///
//    static func notifyWebRTCMessage(_ message: BDMessageModel) {
//        NotificationCenter.default.post(name: Notification.Name.BD_NOTIFICATION_WEBRTC_MESSAGE, object: message)
//    }
//
//    static func notifyOutOfDate() {
//        NotificationCenter.default.post(name: Notification.Name.BD_NOTIFICATION_OUTOFDATE, object: nil)
//    }

    static func notifyTransferMessage(_ message: BDMessageModel) {
        NotificationCenter.default.post(name: .kTransfer, object: message)
    }

    static func notifyTransferAcceptMessage(_ message: BDMessageModel) {
        NotificationCenter.default.post(name: .kTransferAccept, object: message)
    }

    static func notifyTransferRejectMessage(_ message: BDMessageModel) {
        NotificationCenter.default.post(name: .kTransferReject, object: message)
    }
    
    ///
    private static var messageReceivedSoundID: SystemSoundID = 0
    private static var messageSendSoundID: SystemSoundID = 0
    
    static func initNotifySound() {
        if let inpath = Bundle(for: self).path(forResource: "in", ofType: "caf") {
            AudioServicesCreateSystemSoundID(URL(fileURLWithPath: inpath) as CFURL, &messageReceivedSoundID)
        }
        
        if let receivepath = Bundle(for: self).path(forResource: "messageReceived", ofType: "aiff") {
            AudioServicesCreateSystemSoundID(URL(fileURLWithPath: receivepath) as CFURL, &messageSendSoundID)
        }
    }
        
    static func playMessageSendSound() {
        AudioServicesPlaySystemSound(messageSendSoundID)
    }
    
    static func playMessageReceivedSound() {
        AudioServicesPlaySystemSound(messageReceivedSoundID)
    }
    
    static func backgroundMessageReceivedVibrate() {
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }
    
    static func notifyUploadPercentage(_ percentage: String, withLocalId localId: String) {
        let dictionary: [String: Any] = ["percentage": percentage, "localId": localId]
        NotificationCenter.default.post(name: .kUploadPercentage, object: dictionary)
    }
    
    static func notifyUploadError(_ localId: String) {
        let dictionary: [String: Any] = ["localId": localId]
        NotificationCenter.default.post(name: .kUploadError, object: dictionary)
    }
}
