//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    
    func getAgentProfile(success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "client": BDSettings.getClient()!
        ]
        // debugPrint("\(#function) \(params), headers: \(headers)")
        AF.request(BDApiUrl.agentProfileURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    func getVisitorProfile(success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())) {
//        if (!BDSettings.isAlreadyLogin()!) {
//            return failure("未初始化，请首先调用initBytedesk接口")
//        }
//        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
//            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "uid": BDSettings.getUid()!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("\(#function), url: \(BDApiUrl.visitorProfileURL), \(params), headers: \(headers)")
//        debugFun(API.visitorProfileURL, method: .get, params: params, headers: headers)
        //
        AF.request(BDApiUrl.visitorProfileURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    
    func getProfileByUid(uid: String?, success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "uid": uid,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("\(#function) \(params), headers: \(headers)")
        AF.request(BDApiUrl.userProfileUidURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    func setNickname(nickname: String?, success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())  ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json",
            "Content-Type": "application/json"
        ]
        let params: [String: Any] = [
            "nickname": nickname!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("\(#function), url: \(BDApiUrl.setNicknameURL), \(params), headers: \(headers)")
        AF.request(BDApiUrl.setNicknameURL, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    //
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    func setAvatar(avatar: String?, success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())  ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json",
            "Content-Type": "application/json"
        ]
        let params: [String: Any] = [
            "avatar": avatar!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.setAvatarURL, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    //
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    func setDescription(description: String?, success:@escaping ((_ userResult: BDUserResult)->()), failure:@escaping ((_ error: String)->())  ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json",
            "Content-Type": "application/json"
        ]
        let params: [String: Any] = [
            "description": description!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.setDescriptionURL, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: BDUserResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    //
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    
    
    
}
