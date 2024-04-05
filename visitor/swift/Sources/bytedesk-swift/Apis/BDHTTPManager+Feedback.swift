//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/15.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    func createFeedback(adminUid: String?, categoryCid: String?, mobile: String?, content: String?, imageUrl: String?, success:@escaping ((_ feedbackResult: BDFeedbackResult)->()), failure:@escaping ((_ error: String)->())  ) {
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
            "uid": adminUid!,
            "cid": categoryCid!,
            "mobile": mobile!,
            "content": content!,
            "fileUrl": imageUrl!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.createFeedbackUrl, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: BDFeedbackResult.self) { response in
             debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                     debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                         debugPrint("failure status: \(data.status_code!)")
                        failure(data.message!)
                    }
                    //
                    return
                case .failure(let error):
                     debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    //
    func queryFeedback(page: Int?, size: Int?, success:@escaping ((_ feedbackResultPage: BDFeedbackResultPage)->()), failure:@escaping ((_ error: String)->())) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "page": page!,
            "size": size!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.queryFeedbackUrl, method: .get, parameters: params, headers: headers).responseDecodable(of: BDFeedbackResultPage.self) { response in
             debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                     debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code)")
                        failure(data.message!)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
}
