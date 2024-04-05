//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    
    func requestThread(uid: String?, type: String?, success:@escaping ((_ threadResult: BDThreadResult)->()), failure:@escaping ((_ error: String)->()) ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
//            "Accept": "application/json"
        ]
        let params = [
            "wId": uid!,
            "type": type!,
            "aId": uid!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("requestThread \(params), headers: \(headers)")
        AF.request(BDApiUrl.requestThreadURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDThreadResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    success(data)
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    // 转人工
    func requestAgent(wid: String?, success:@escaping ((_ threadResult: BDThreadResult)->()), failure:@escaping ((_ error: String)->()) ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
//            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "wId": wid!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("\(#function) \(params), headers: \(headers)")
        AF.request(BDApiUrl.requestAgentURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDThreadResult.self) { response in
            // debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    success(data)
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    
}
