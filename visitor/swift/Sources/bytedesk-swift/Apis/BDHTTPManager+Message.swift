//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    
    func sendMessage(json: String?, success:@escaping ((_ messageSendResult: BDMessageSendResult)->()), failure:@escaping ((_ error: String)->())) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        let params = [
            "json": json,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.messageSendURL, method: .post, parameters: params, headers: headers).responseDecodable(of: BDMessageSendResult.self) { response in
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
    
    //
    func getVisitorUnreadMessageCount(success:@escaping ((_ unreadCount: BDUnreadCount)->()), failure:@escaping ((_ error: String)->())) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        //
        AF.request(BDApiUrl.visitorUnreadMessageCountURL, method: .get, headers: headers).responseDecodable(of: BDUnreadCount.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code), message: \(data.message)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code)")
                        failure(data.message)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    
    
}
