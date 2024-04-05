//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation
import Alamofire

// 1.定义一个闭包类型
typealias swiftBlock = (_ result : NSDictionary) -> Void
// 2. 声明一个变量
var successCallBack: swiftBlock?
var failedCallBack: swiftBlock?

// Swift 中的类如果要供Objective-C 调用，必须也继承自NSObject
public class BDHttpApis: NSObject {
    
    static func registerAnonymous(appkey: String?, subDomain: String?, onSuccess:@escaping ((_ registerResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        BDHTTPManager.sharedInstance().registerAnonymous(appkey: appkey, subDomain: subDomain) { registerResult in
            onSuccess(registerResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    // 自定义用户名
    static func registerUser(username: String?, nickname: String?, avatar: String?, password: String?, subDomain: String?, onSuccess:@escaping ((_ registerResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->()) ) {
        //
        BDHTTPManager.sharedInstance().registerUser(username: username, nickname: nickname, avatar: avatar, password: password, subDomain: subDomain) { registerResult in
            onSuccess(registerResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func loginAnonymous(appkey: String?, subDomain: String?, onSuccess:@escaping ((_ loginResult: BDPassport)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        BDHTTPManager.sharedInstance().loginAnonymous(appkey: appkey, subDomain: subDomain) { loginResult in
            onSuccess(loginResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    //
    static func loginUser(username: String?, password: String?, appkey: String?, subDomain: String?, onSuccess:@escaping ((_ loginResult: BDPassport)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().loginUser(username: username, password: password, appkey: appkey, subDomain: subDomain) { loginResult in
            onSuccess(loginResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func requestThreadWorkgroup(workgroupWid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        //
        BDHTTPManager.sharedInstance().requestThread(uid: workgroupWid, type: BD_THREAD_TYPE_WORKGROUP) { threadResult in
            onSuccess(threadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func requestThreadAgent(agentUid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        //
        BDHTTPManager.sharedInstance().requestThread(uid: agentUid, type: BD_THREAD_TYPE_APPOINTED) { threadResult in
            onSuccess(threadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func requestAgent(workgroupWid: String?, onSuccess:@escaping ((_ threadResult: BDThreadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        //
        BDHTTPManager.sharedInstance().requestAgent(wid: workgroupWid) { threadResult in
            onSuccess(threadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func queryAnswer(threadTid: String?, questionQid: String?, onSuccess:@escaping ((_ answerResult: BDAnswerResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("http \(#function)")
        
        BDHTTPManager.sharedInstance().queryAnswer(tid: threadTid, qid: questionQid) { answerResult in
            onSuccess(answerResult)
        } failure: { error in
            onFailure(error)
        }
    }

    static func getWorkGroupStatus(workgroupWid: String?, onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->()) ) {
        // debugPrint("http \(#function)")
        
        BDHTTPManager.sharedInstance().getWorkGroupStatus(wid: workgroupWid) { statusResult in
            onSuccess(statusResult)
        } failure: { error in
            onFailure(error)
        }

    }
    
    static func getAgentStatus(agentUid: String?, onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->()) ) {
        // debugPrint("http \(#function)")
        
        BDHTTPManager.sharedInstance().getAgentStatus(uid: agentUid) { statusResult in
            onSuccess(statusResult)
        } failure: { error in
            onFailure(error)
        }

    }
    
    static func sendTextMessage(content: String, onSuccess:@escaping ((_ messageSendResult: BDMessageSendResult)->()), onFailure:@escaping ((_ json: String)->())) {
        let json = ""
        // debugPrint("http sendTextMessage")
        //
        BDHTTPManager.sharedInstance().sendMessage(json: json) { messageSendResult in
            onSuccess(messageSendResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func setNickname(nickname: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHTTPManager.sharedInstance().setNickname(nickname: nickname) { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func setAvatar(avatar: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHTTPManager.sharedInstance().setAvatar(avatar: avatar) { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func setDescription(description: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        
        BDHTTPManager.sharedInstance().setDescription(description: description) { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
//    static func
    
    static func getAgentProfile(onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().getAgentProfile { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }

    }
    
    static func getVisitorProfile(onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().getVisitorProfile { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func getUserProfileByUid(uid: String?, onSuccess:@escaping ((_ userResult: BDUserResult)->()), onFailure:@escaping ((_ error: String)->())) {
        // debugPrint("getUserProfileByUid \(uid!)")
        //
        BDHTTPManager.sharedInstance().getProfileByUid(uid: uid) { userResult in
            onSuccess(userResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func uploadImage(imageData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().uploadImage(imageData: imageData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func uploadFile(fileData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().uploadFile(fileData: fileData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func uploadVoice(voiceData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().uploadVoice(voiceData: voiceData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func uploadVideo(videoData: Data?, fileName: String?, onSuccess:@escaping ((_ uploadResult: BDUploadResult)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().uploadVideo(videoData: videoData, fileName: fileName) { uploadResult in
            onSuccess(uploadResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func createLeaveMessage(type: String?, uid: String?, mobile: String?, content: String?, imageUrl: String?, onSuccess:@escaping ((_ leaveMsgResult: BDLeaveMsgResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        //
        BDHTTPManager.sharedInstance().createLeaveMessage(type: type, uid: uid, mobile: mobile, content: content, imageUrl: imageUrl) { leaveMsgResult in
            onSuccess(leaveMsgResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func queryLeaveMessage(page: Int?, size: Int?, onSuccess:@escaping ((_ leaveMsgResultPage: BDLeaveMsgResultPage)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().queryLeaveMessage(page: page, size: size) { leaveMsgResultPage in
            onSuccess(leaveMsgResultPage)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func createFeedback(adminUid: String?, categoryCid: String?, mobile: String?, content: String?, imageUrl: String?, onSuccess:@escaping ((_ feedbackResult: BDFeedbackResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        //
        BDHTTPManager.sharedInstance().createFeedback(adminUid: adminUid, categoryCid: categoryCid, mobile: mobile, content: content, imageUrl: imageUrl) { feedbackResult in
            onSuccess(feedbackResult)
        } failure: { error in
            onFailure(error)
        }

    }
    
    static func queryFeedback(page: Int?, size: Int?, onSuccess:@escaping ((_ feedbackResultPage: BDFeedbackResultPage)->()), onFailure:@escaping ((_ error: String)->())) {
        //
        BDHTTPManager.sharedInstance().queryFeedback(page: page, size: size) { feedbackResultPage in
            onSuccess(feedbackResultPage)
        } failure: { error in
            onFailure(error)
        }
    }
    
    //
    static func rate(tid: String?, score: Int?, note: String?, invite: Bool?, onSuccess:@escaping ((_ rateResult: BDRateResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        //
        BDHTTPManager.sharedInstance().rate(tid: tid, score: score, note: note, invite: invite) { rateResult in
            onSuccess(rateResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    static func logout(onSuccess:@escaping ((_ statusResult: BDStatusResult)->()), onFailure:@escaping ((_ error: String)->())  ) {
        //
        BDHTTPManager.sharedInstance().logout { statusResult in
            //
            onSuccess(statusResult)
        } failure: { error in
            onFailure(error)
        }
    }
    
    
}
