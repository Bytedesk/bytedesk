//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation

struct BDApiUrl {
    //此处根据本地键值 debug ,用来作为baseURL,一个可以用来做测试的接口,一个定义为线上接口.可以设置一个按钮，管理debug字段，重启app就可以更换接口
    // http://localhost:8000/spider/meiyu/status
    // http://localhost:8000/uid/baidu2
//    static let hostName = "http://localhost:8000"
    // 注册访客
    static let registerAnonymousURL = BD_REST_API_HOST + "visitor/api/username"
    //
    static let registerUserURL = BD_REST_API_HOST + "visitor/api/register/user"
    // 登录
    static let loginPasswordURL = BD_REST_API_HOST + "oauth/token"
    // 请求会话
    static let requestThreadURL = BD_REST_API_HOST + "api/thread/request"
    // 转人工
    static let requestAgentURL = BD_REST_API_HOST + "api/thread/agent"
    //
    static let queryAnswerURL = BD_REST_API_HOST + "api/answer/query"
    //
    static let getWorkGroupStatus = BD_REST_API_HOST + "visitor/api/status/workGroup"
    //
    static let getAgentStatus = BD_REST_API_HOST + "visitor/api/status/agent"
    //
    static let messageSendURL = BD_REST_API_HOST + "api/messages/send"
    //
    static let visitorUnreadMessageCountURL = BD_REST_API_HOST + "api/messages/unreadCount/visitor"
    //
    static let agentProfileURL = BD_REST_API_HOST + "api/user/profile"
    //
    static let setNicknameURL = BD_REST_API_HOST + "api/user/nickname"
    //
    static let setAvatarURL = BD_REST_API_HOST + "api/user/avatar"
    //
    static let setDescriptionURL = BD_REST_API_HOST + "api/user/description"
    //
    static let visitorProfileURL = BD_REST_API_HOST + "visitor/api/profile"
    //
    static let userProfileUidURL = BD_REST_API_HOST + "api/user/profile/uid"
    //
    static let logoutURL = BD_REST_API_HOST + "api/user/logout"
    
    ///
    //
    static let uploadImageURL = BD_REST_API_HOST + "visitor/api/upload/image"
    //
    static let uploadAvatarURL = BD_REST_API_HOST + "visitor/api/upload/avatar"
    //
    static let uploadVoiceURL = BD_REST_API_HOST + "visitor/api/upload/voice"
    //
    static let uploadFileURL = BD_REST_API_HOST + "visitor/api/upload/file"
    //
    static let uploadVideoURL = BD_REST_API_HOST + "visitor/api/upload/video"
    
    //
    static let leaveMessageURL = BD_REST_API_HOST + "api/v2/leavemsg/save"
    
    static let queryLeaveMessageURL = BD_REST_API_HOST + "api/leavemsg/query/visitor"
    
    //
    static let rateURL = BD_REST_API_HOST + "api/rate/do"
    
    //
    static let createFeedbackUrl = BD_REST_API_HOST + "api/feedback/create"
    
    static let queryFeedbackUrl = BD_REST_API_HOST + "api/feedback/mine"
    
}
