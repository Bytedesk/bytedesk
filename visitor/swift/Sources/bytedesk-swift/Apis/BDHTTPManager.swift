//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/19.
//

import Foundation
import Alamofire

public class BDHTTPManager: NSObject {
    //
    class func sharedInstance() -> BDHTTPManager {
        struct Static {
            static let instance = BDHTTPManager()
        }
        return Static.instance
    }
    // 匿名注册用户
    func registerAnonymous(appkey: String?, subDomain: String?, success:@escaping ((_ registerResult: BDUserResult)->()), failure:@escaping ((_ error: String)->()) ) {
        //
        let params: [String: Any] = [
            "appkey": appkey!,
            "subDomain": subDomain!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.registerAnonymousURL, method: .get, parameters: params).responseDecodable(of: BDUserResult.self) { response in
//             debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // debugPrint("success \(data), status_code: \(data.status_code!), message: \(data.message!)")
                    if (data.status_code == 200) {
                        BDSettings.setUserInfo(data.data!)
                        BDSettings.setPassword(data.data?.username)
                        success(data)
                    } else {
                        // debugPrint("failure status: \(data.status_code!)")
                        BDSettings.clearUserInfo()
                        failure(data.message!)
                    }
                    return
                case .failure(let error):
                    // debugPrint("failure \(error)")
                    failure(error.localizedDescription)
            }
           }
    }
    
    // 自定义用户名
    func registerUser(username: String?, nickname: String?, avatar: String?, password: String?, subDomain: String?, success:@escaping ((_ registerResult: BDUserResult)->()), failure:@escaping ((_ error: String)->()) ) {
        //
        if (username!.isEmpty || username!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            failure("用户名不能为空")
            return
        }
        if (password!.isEmpty || password!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            failure("密码不能为空")
            return
        }
        //
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type": "application/json"
        ]
        //
        let params: [String: Any] = [
            "username": username!,
            "nickname": nickname!,
            "avatar": avatar!,
            "password": password!,
            "subDomain": subDomain!,
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.registerUserURL, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseString { response in
            //
            switch response.result {
            case .success(let stringValue):
                print("Raw Response: \(stringValue)")
                // 解析数据
                if let contentData = stringValue.data(using: .utf8) {
                    do {
                        let resultDict = try JSONSerialization.jsonObject(with: contentData, options: []) as? [String: Any]
                        if let status_code = resultDict!["status_code"] as? Int {
                            print("status_code: \(status_code)")
                            if (status_code == 200) {
                                
                                if let data = response.data {
                                    do {
                                        let decoder = JSONDecoder()
                                        let parsedUserResult = try decoder.decode(BDUserResult.self, from: data)
                //                        // 在此处使用解析后的数据
                                        BDSettings.setUserInfo(parsedUserResult.data!)
                                        BDSettings.setPassword(password!)
                                        success(parsedUserResult)
                                    } catch {
                                        print("Error decoding data: \(error)")
                                    }
                                }
                                
                            } else {
                                debugPrint("用户名已经存在，单独处理")
                                let uid = resultDict!["data"] as? String
                                let usernameCompose = String(format: "%@@%@", username!, subDomain!)
                                //
                                BDSettings.setUid(uid!)
                                BDSettings.setUsername(usernameCompose)
                                BDSettings.setPassword(password)
                                BDSettings.setNickname(nickname)
                                BDSettings.setAvatar(avatar)
                                BDSettings.setSubDomain(subDomain)
                                //
                                let user = BDUserModel()
                                user.uid = uid!
                                user.username = usernameCompose
                                user.nickname = nickname
                                user.avatar = avatar
                                user.subDomain = subDomain
                                //
                                let userResult = BDUserResult()
                                userResult.status_code = -1
                                userResult.message = "用户名已经存在，直接登录"
                                userResult.data = user
                                //
                                success(userResult)
                            }
                            
                        } else {
                            failure("未找到status_code")
                        }
                    } catch {
                        print("JSONSerialization error: \(error.localizedDescription)")
                    }
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }
    
    //
    // 匿名登录
    func loginAnonymous(appkey: String?, subDomain: String?, success:@escaping ((_ loginResult: BDPassport)->()), failure:@escaping ((_ error: String)->())) {
        //
        let username = BDSettings.getUsername()
        var password = BDSettings.getPassword()
        if (password!.isEmpty || password!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            password = username
        }
        //
        loginUser(username: username, password: password, appkey: appkey, subDomain: subDomain) { loginResult in
            success(loginResult)
        } failure: { error in
            failure(error)
        }
    }
    
    //
    func loginUser(username: String?, password: String?, appkey: String?, subDomain: String?, success:@escaping ((_ loginResult: BDPassport)->()), failure:@escaping ((_ error: String)->())) {
        if (username!.isEmpty || username!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            failure("用户名不能为空")
            return
        }
        if (password!.isEmpty || password!.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).count == 0) {
            failure("密码不能为空")
            return
        }
        //
        let headers: HTTPHeaders = [
            "Authorization": "Basic Y2xpZW50OnNlY3JldA==",
        ]
        //
        let params: [String: Any] = [
            "username": username!,
            "password": password!,
            "grant_type": "password",
            "scope": "all"
        ]
         debugPrint("params: \(params)")
        //
        AF.request(BDApiUrl.loginPasswordURL, method: .post, parameters: params, headers: headers).responseDecodable(of: BDPassport.self) { response in
             debugPrint("Response: \(response)")
            switch response.result {
               case let .success(data):
//                  debugPrint("success \(data)")
                 BDSettings.setAccessToken(data.access_token!)
                 success(data)
                 return
                case .failure(let error):
                // debugPrint("failure \(error)")
                failure(error.localizedDescription)
            }
           }
    }
    
    
    func getWorkGroupStatus(wid: String?, success:@escaping ((_ statusResult: BDStatusResult)->()), failure:@escaping ((_ error: String)->()) ) {
        //
        let params: [String: Any] = [
            "wid": wid!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("getWorkGroupStatus \(params)")
        AF.request(BDApiUrl.getWorkGroupStatus, method: .get, parameters: params).responseDecodable(of: BDStatusResult.self) { response in
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
    
    func getAgentStatus(uid: String?, success:@escaping ((_ statusResult: BDStatusResult)->()), failure:@escaping ((_ error: String)->()) ) {
        //
        let params: [String: Any] = [
            "uid": uid!,
            "client": BDSettings.getClient()!
        ]
        // debugPrint("getAgentStatus \(params)")
        AF.request(BDApiUrl.getAgentStatus, method: .get, parameters: params).responseDecodable(of: BDStatusResult.self) { response in
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


    func logout(success:@escaping ((_ statusResult: BDStatusResult)->()), failure:@escaping ((_ error: String)->()) ) {
        if (!BDSettings.isAlreadyLogin()!) {
            return failure("未登录，无需logout退出")
        }
        let accessToken = BDSettings.getAccessToken()
        let headers: HTTPHeaders = [
            "Authorization": "Bearer \(accessToken!)",
            "Accept": "application/json",
            "Content-Type": "application/json"
        ]
        let params: [String: Any] = [
            "client": BDSettings.getClient()!
        ]
        //
        AF.request(BDApiUrl.logoutURL, method: .post, parameters: params, encoding: JSONEncoding.default, headers: headers).responseDecodable(of: BDStatusResult.self) { response in
             debugPrint("Response: \(response)")
            switch response.result {
                case let .success(data):
                    // 清空本地缓存用户数据
                    BDSettings.clearUserInfo()
                
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
    func debugFun(_ url: String, method: HTTPMethod, params: Parameters?, headers: HTTPHeaders?) {
        AF.request(url, method: method, parameters: params, headers: headers).responseString { response in
            switch response.result {
            case .success(let stringValue):
                print("Raw Response: \(stringValue)")
                // 解析数据
                if let data = response.data {
                    do {
//                        let decoder = JSONDecoder()
//                        let parsedData = try decoder.decode(BDThreadResult.self, from: data)
//                        // 在此处使用解析后的数据
//                        print("Parsed Data: \(parsedData)")
                    } catch {
                        print("Error decoding data: \(error)")
                    }
                }
            case .failure(let error):
                print("Error: \(error)")
            }
        }
    }

    // 使用Alamofire发送网络请求并获取原始返回字符串数据
//        AF.request(API.requestThreadURL, method: .get, parameters: params, headers: headers).responseString { response in
//            switch response.result {
//            case .success(let stringValue):
//                print("Raw Response: \(stringValue)")
//                // 解析数据
//                if let data = response.data {
//                    do {
//                        let decoder = JSONDecoder()
//                        let parsedData = try decoder.decode(BDThreadResult.self, from: data)
//                        // 在此处使用解析后的数据
//                        print("Parsed Data: \(parsedData)")
//                        success(parsedData)
//                    } catch {
//                        print("Error decoding data: \(error)")
//                        failure(error.localizedDescription)
//                    }
//                }
//            case .failure(let error):
//                print("Error: \(error)")
//                failure(error.localizedDescription)
//            }
//        }
    
    //
    //MARK:数据请求方法
//    func afRequest(url:String, type:HTTPMethod, params:[String : AnyObject]?, success:@escaping ((_ json: JSON)->()), failure:@escaping ((_ json: String)->()) ){
////        let headers: HTTPHeaders = [
////            "Authorization": "Basic Y2xpZW50OnNlY3JldA==",
////            "Accept": "application/json"
////        ]
//        //
////        AF.request(url).responseDecodable(of: BDHttpResult.self) { response in
////            // debugPrint("Response: \(response)")
////            switch response.result {
////               case let .success(data):
////    //             success(data)
////               case let .failure(error):
////    //             failure(error)
////            }
////           }
//
////        AF.request(url, method: type, parameters: params, encoding: URLEncoding.default, headers: headers).responseJSON { (response) in
////            debugPrint(response)
////            // debugPrint("--------------------")
////
////            debugPrint(response.result)
////
////            switch response.result {
////                case .success(let value):
////                success(JSON(rawValue: value)!)
//////                    completion(try? SomeRequest(protobuf: value))
////                case .failure(let error):
////                    print(error)
//////                    failure(String(data: error, encoding: .utf8)!)
////            }
////        }
//
//    }

  
}
