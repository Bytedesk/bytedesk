//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/13.
//

import Foundation
import Alamofire

extension BDHTTPManager {
    
    /// 上传图片
    func uploadImage(imageData: Data?, fileName: String?, success:@escaping ((_ uploadResult: BDUploadResult)->()), failure:@escaping ((_ error: String)->())  ) {
        //
        AF.upload(multipartFormData: { multipartFormData in
            // Append the file to be uploaded
            multipartFormData.append(imageData!, withName: "file", fileName: fileName!, mimeType: "image/png")
            // 自定义参数：文件名
            multipartFormData.append(fileName!.data(using: .utf8)!, withName: "file_name")
            // Add more custom parameters as needed
        }, to: BDApiUrl.uploadImageURL)
            .responseDecodable(of: BDUploadResult.self) { response in
                // debugPrint("Response: \(response)")
                switch response.result {
                    case let .success(data):
                        if (data.status_code == 200) {
                            success(data)
                        } else {
                            // debugPrint("failure status: \(data.status_code!)")
                            failure(data.message!)
                        }
                    case let .failure(error):
                        // debugPrint("failure \(error)")
                        failure(error.localizedDescription)
                }
            }
    }
    
    func uploadAvatar(imageData: Data?, fileName: String?, success:@escaping ((_ uploadResult: BDUploadResult)->()), failure:@escaping ((_ error: String)->())  ) {
        //
        AF.upload(multipartFormData: { multipartFormData in
            // Append the file to be uploaded
            multipartFormData.append(imageData!, withName: "file", fileName: fileName!, mimeType: "image/png")
            // 自定义参数：文件名
            multipartFormData.append(fileName!.data(using: .utf8)!, withName: "file_name")
            // Add more custom parameters as needed
        }, to: BDApiUrl.uploadAvatarURL)
            .responseDecodable(of: BDUploadResult.self) { response in
                // debugPrint("Response: \(response)")
                switch response.result {
                    case let .success(data):
                        if (data.status_code == 200) {
                            success(data)
                        } else {
                            // debugPrint("failure status: \(data.status_code!)")
                            failure(data.message!)
                        }
                    case let .failure(error):
                        // debugPrint("failure \(error)")
                        failure(error.localizedDescription)
                }
            }
    }
    
    func uploadFile(fileData: Data?, fileName: String?, success:@escaping ((_ uploadResult: BDUploadResult)->()), failure:@escaping ((_ error: String)->())  ) {
        //
        AF.upload(multipartFormData: { multipartFormData in
            // Append the file to be uploaded
            multipartFormData.append(fileData!, withName: "file", fileName: fileName!, mimeType: "application/octet-stream")
            // 自定义参数：文件名
            multipartFormData.append(fileName!.data(using: .utf8)!, withName: "file_name")
            // Add more custom parameters as needed
        }, to: BDApiUrl.uploadFileURL)
            .responseDecodable(of: BDUploadResult.self) { response in
                // debugPrint("Response: \(response)")
                switch response.result {
                    case let .success(data):
                        if (data.status_code == 200) {
                            success(data)
                        } else {
                            // debugPrint("failure status: \(data.status_code!)")
                            failure(data.message!)
                        }
                    case let .failure(error):
                        // debugPrint("failure \(error)")
                        failure(error.localizedDescription)
                }
            }
    }
    
    func uploadVoice(voiceData: Data?, fileName: String?, success:@escaping ((_ uploadResult: BDUploadResult)->()), failure:@escaping ((_ error: String)->())  ) {
        //
        AF.upload(multipartFormData: { multipartFormData in
            // Append the file to be uploaded
            multipartFormData.append(voiceData!, withName: "file", fileName: fileName!, mimeType: "audio/amr")
            // 自定义参数：文件名
            multipartFormData.append(fileName!.data(using: .utf8)!, withName: "file_name")
            // Add more custom parameters as needed
        }, to: BDApiUrl.uploadVoiceURL)
            .responseDecodable(of: BDUploadResult.self) { response in
                // debugPrint("Response: \(response)")
                switch response.result {
                    case let .success(data):
                        if (data.status_code == 200) {
                            success(data)
                        } else {
                            // debugPrint("failure status: \(data.status_code!)")
                            failure(data.message!)
                        }
                    case let .failure(error):
                        // debugPrint("failure \(error)")
                        failure(error.localizedDescription)
                }
            }
    }
    
    func uploadVideo(videoData: Data?, fileName: String?, success:@escaping ((_ uploadResult: BDUploadResult)->()), failure:@escaping ((_ error: String)->())  ) {
        //
        AF.upload(multipartFormData: { multipartFormData in
            // Append the file to be uploaded
            multipartFormData.append(videoData!, withName: "file", fileName: fileName!, mimeType: "video/mp4")
            // 自定义参数：文件名
            multipartFormData.append(fileName!.data(using: .utf8)!, withName: "file_name")
            // Add more custom parameters as needed
        }, to: BDApiUrl.uploadVideoURL)
            .responseDecodable(of: BDUploadResult.self) { response in
                // debugPrint("Response: \(response)")
                switch response.result {
                    case let .success(data):
                        if (data.status_code == 200) {
                            success(data)
                        } else {
                            // debugPrint("failure status: \(data.status_code!)")
                            failure(data.message!)
                        }
                    case let .failure(error):
                        // debugPrint("failure \(error)")
                        failure(error.localizedDescription)
                }
            }
    }
    
}
