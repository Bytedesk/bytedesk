//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    // 根据问题qid查询答案
    func queryAnswer(tid: String?, qid: String?, success:@escaping ((_ answerResult: BDAnswerResult)->()), failure:@escaping ((_ error: String)->()) ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未初始化，请首先调用initBytedesk接口")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
//            "Accept": "application/json"
        ]
        let params: [String: Any] = [
            "tid": tid!,
            "qid": qid!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("queryAnswer \(params), headers: \(headers)")
        AF.request(BDApiUrl.queryAnswerURL, method: .get, parameters: params, headers: headers).responseDecodable(of: BDAnswerResult.self) { response in
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
