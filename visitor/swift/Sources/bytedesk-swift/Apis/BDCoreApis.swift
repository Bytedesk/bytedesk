//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation

// Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDCoreApis: NSObject {
    
    // SDK初始化
    static public func initBytedesk(appkey: String?, subDomain: String?, onSuccess:@escaping ((_ loginResult: BDPassport)->()), onFailure:@escaping ((_ error: String)->())) {
        // TODO: 未处理token过期的情况
//        if (BDSettings.isAlreadyLogin()!) {
//            // debugPrint("isAlreadyLogin")
//            return;
//        }
        //
        let username = BDSettings.getUsername()!;
        if (username.isEmpty || username.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            // 注册
            BDHttpApis.registerAnonymous(appkey: appkey, subDomain: subDomain) { registerResult in
                // 登录
                BDHttpApis.loginAnonymous(appkey: appkey, subDomain: subDomain) { loginResult in
                    // 建立长链接
                    BDCoreApis.connect();
                    // 上传设备信息
    //                [[BDHttpApis sharedInstance] uploadDeviceInfo]
                    //
                    onSuccess(loginResult)
                } onFailure: { error in
                    onFailure(error)
                }
            } onFailure: { error in
                onFailure(error)
            }
        } else {
            // debugPrint("username: \(String(describing: username))")
            // TODO: 直接登录
            BDHttpApis.loginAnonymous(appkey: appkey, subDomain: subDomain) { registerResult in
                // 建立长链接
                BDCoreApis.connect()
                // // 上传设备信息
//                [[BDHttpApis sharedInstance] uploadDeviceInfo]
                //
                onSuccess(registerResult)
            } onFailure: { error in
                onFailure(error)
            }
        }
    }
    
    //
    static public func initBytedesk(username: String?, nickname: String?, avatar: String?, appkey: String?, subDomain: String?, onSuccess:@escaping ((_ loginResult: BDPassport)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        let usernameLocal = BDSettings.getUsername()!
        if (!usernameLocal.isEmpty && usernameLocal.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count > 0) {
            let passwordLocal = BDSettings.getPassword()!
            debugPrint("usernameLocal:\(usernameLocal), passwordLocal:\(passwordLocal)")
            // 登录
            BDHttpApis.loginUser(username: usernameLocal, password: passwordLocal, appkey: appkey, subDomain: subDomain) { loginResult in
                // 建立长链接
                BDCoreApis.connect();
                // 回调
                onSuccess(loginResult)
            } onFailure: { error in
                onFailure(error)
            }
        } else {
            // 注册
            let password = username
            BDHttpApis.registerUser(username: username, nickname: nickname, avatar: avatar, password: password, subDomain: subDomain) { registerResult in
                // 登录
                let usernameCompose = String(format: "%@@%@", username!, subDomain!)
                BDHttpApis.loginUser(username: usernameCompose, password: password, appkey: appkey, subDomain: subDomain) { loginResult in
                    // 建立长链接
                    BDCoreApis.connect();
                    // 回调
                    onSuccess(loginResult)
                } onFailure: { error in
                    onFailure(error)
                }
            } onFailure: { error in
                onFailure(error)
            }
        }
    }

    
    // 建立长链接
    static public func connect() {
        BDMQTTApis.sharedInstance().connect()
    }
    
    // 请求技能组会话
    static public func requestThreadWorkgroup(workgroupWid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.requestThreadWorkgroup(workgroupWid: workgroupWid) { threadResult in
            onSuccess(threadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    // 请求指定客服会话
    static public func requestThreadAgent(agentUid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.requestThreadAgent(agentUid: agentUid) { threadResult in
            onSuccess(threadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    // 请求人工客服
    static public func requestAgent(workgroupWid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.requestAgent(workgroupWid: workgroupWid) { threadResult in
            onSuccess(threadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func queryAnswer(threadTid: String?, questionQid: String?, onSuccess:@escaping ((_ answerResult: BDAnswerResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.queryAnswer(threadTid: threadTid, questionQid: questionQid) { answerResult in
            onSuccess(answerResult)
        } onFailure: { error in
            onFailure(error)
        }
    }

    static public func getWorkGroupStatus(workgroupWid: String?, onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->()) ) {
        
        BDHttpApis.getWorkGroupStatus(workgroupWid: workgroupWid) { statusResult in
            onSuccess(statusResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func getAgentStatus(agentUid: String?, onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->()) ) {
        
        BDHttpApis.getAgentStatus(agentUid: agentUid) { statusResult in
            onSuccess(statusResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func getAgentProfile(onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.getAgentProfile { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func getVisitorProfile(onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.getVisitorProfile { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    
    static public func getUserProfileByUid(_ uid: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("getUserProfileByUid \(uid!)")
        //
        BDHttpApis.getUserProfileByUid(uid: uid) { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func setNickname(_ nickname: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.setNickname(nickname: nickname) { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func setAvatar(_ avatar: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.setAvatar(avatar: avatar) { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func setDescription(_ description: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.setDescription(description: description) { userResult in
            onSuccess(userResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func uploadImage(imageData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.uploadImage(imageData: imageData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func uploadFile(fileData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.uploadFile(fileData: fileData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func uploadVoice(voiceData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.uploadVoice(voiceData: voiceData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func uploadVideo(videoData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.uploadVideo(videoData: videoData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    /// 发送文本消息
    static func sendTextMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendTextMessageProtobuf(mid: mid, content: content, thread: thread)
    }
    
    ///
    static func sendImageMessageProtobuf(mid: String, imageUrl: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendImageMessageProtobuf(mid: mid, imageUrl: imageUrl, thread: thread)
    }
    
    static func sendVoiceMessageProtobuf(mid: String, voiceUrl: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendVoiceMessageProtobuf(mid: mid, voiceUrl: voiceUrl, thread: thread)
    }
    
    static func sendFileMessageProtobuf(mid: String, fileUrl: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendFileMessageProtobuf(mid: mid, fileUrl: fileUrl, thread: thread)
    }
    
    static func sendVideoMessageProtobuf(mid: String, videoUrl: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendVideoMessageProtobuf(mid: mid, videoUrl: videoUrl, thread: thread)
    }
    
    static func sendCommodityMessageProtobuf(mid: String, content: String, thread: BDThreadModel) {
        
        BDMQTTApis.sharedInstance().sendCommodityMessageProtobuf(mid: mid, content: content, thread: thread)
    }
    
    /// 插入
    static func insertMessage(_ messageModel: BDMessageModel) {
        
        BDDBApis.sharedInstance().insertMessage(messageModel)
    }
    
    /// 查询
    static func getMessagesWithThread(_ tid: String) -> [BDMessageModel] {
        
        return BDDBApis.sharedInstance().queryMessagesByThreadTid(tid)
//        return BDDBApis.sharedInstance().queryMessage()
    }
    
    /// 查询
    static func getMessagesWithWorkGroup(_ wid: String) -> [BDMessageModel] {
        let threadTopic = wid + "/" + BDSettings.getUid()!
        return BDDBApis.sharedInstance().queryMessagesByThreadTopic(threadTopic)
    }
    
    //
    static func createLeaveMessage(type: String?, uid: String?, mobile: String?, content: String?, imageUrl: String?, onSuccess:@escaping ((_ leaveMsgResult: BDLeaveMsgResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        
        BDHttpApis.createLeaveMessage(type: type, uid: uid, mobile: mobile, content: content, imageUrl: imageUrl) { leaveMsgResult in
            onSuccess(leaveMsgResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func queryLeaveMessage(page: Int?, size: Int?, onSuccess:@escaping ((_ leaveMsgResultPage: BDLeaveMsgResultPage)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHttpApis.queryLeaveMessage(page: page, size: size) { leaveMsgResultPage in
            onSuccess(leaveMsgResultPage)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func createFeedback(adminUid: String?, categoryCid: String?, mobile: String?, content: String?, imageUrl: String?, onSuccess:@escaping ((_ feedbackResult: BDFeedbackResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        //
        BDHttpApis.createFeedback(adminUid: adminUid, categoryCid: categoryCid, mobile: mobile, content: content, imageUrl: imageUrl) { feedbackResult in
            onSuccess(feedbackResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static func queryFeedback(page: Int?, size: Int?, onSuccess:@escaping ((_ feedbackResultPage: BDFeedbackResultPage)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHttpApis.queryFeedback(page: page, size: size) { feedbackResultPage in
            onSuccess(feedbackResultPage)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    //
    static func rate(tid: String?, score: Int?, note: String?, invite: Bool?, onSuccess:@escaping ((_ rateResult: BDRateResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        
        BDHttpApis.rate(tid: tid, score: score, note: note, invite: invite) { rateResult in
            onSuccess(rateResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    
    
    /// 清空记录
    static func clearMessages() {
        
        BDDBApis.sharedInstance().clearMessageTable()
    }
    
    static func deleteMessagesByThreadTid(_ threadTid: String) {
        
        BDDBApis.sharedInstance().deleteMessagesByThreadTid(threadTid)
    }
    
    static func deleteMessagesByWorkGroup(_ wid: String) {
        
        let threadTopic = wid + "/" + BDSettings.getUid()!
        BDDBApis.sharedInstance().deleteMessagesByThreadTopic(threadTopic)
    }
    
    static func deleteMessageByMid(_ mid: String) {
        
        BDDBApis.sharedInstance().deleteMessageByMid(mid)
    }
    
    
    static public func logout(onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        
        BDHttpApis.logout { statusResult in
            // 断开长链接
            disconnect()
            // 回调
            onSuccess(statusResult)
        } onFailure: { error in
            onFailure(error)
        }
    }
    
    static public func disconnect() {
        
        BDMQTTApis.sharedInstance().disconnect()
    }
    
}
