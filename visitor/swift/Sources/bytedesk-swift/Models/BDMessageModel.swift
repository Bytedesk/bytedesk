//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/21.
//

import Foundation
import UIKit

//
public class BDMessageModel: Codable, Identifiable, ObservableObject {
    // ...
    init() {
        thread = BDThreadModel()
        user = BDUserModel()
    }
    // ...
    var mid: String? = ""
    var content: String? = ""
    //
    var imageUrl: String? = ""
    //
    var voiceUrl: String? = ""
    var videoUrl: String? = ""
    var fileUrl: String? = ""
    //
    var type: String? = ""
    var createdAt: String? = ""
    var status: String? = ""
    var client: String? = ""
    // ...
    var answers: [BDAnswerModel] = []
    var thread: BDThreadModel?
    var user: BDUserModel?
    //
    var currentUid: String? = ""
    //
    var contentAttr: NSAttributedString?
    // ...
    func isNotification() -> Bool {
        if type == BD_MESSAGE_TYPE_NOTIFICATION_THREAD {
            return false
        }
        if type?.hasPrefix(BD_MESSAGE_TYPE_NOTIFICATION) ?? false {
            return true
        }
        return false
    }
    // ...
    func isSend() -> Bool {
        return user?.uid == BDSettings.getUid()
    }
    //
    func createdAtSize() -> CGSize {
        return createdAt!.boundingRect(with: CGSize(width: 200, height: CGFloat.greatestFiniteMagnitude),
                              options: [.usesLineFragmentOrigin],
                              attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 11.0)],
                              context: nil).size
    }
    //
    func contentSize() -> CGSize {
        return content!.boundingRect(with: CGSize(width: 200, height: CGFloat.greatestFiniteMagnitude),
                              options: [.usesLineFragmentOrigin],
                              attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 16.0)],
                              context: nil).size
//        var height = size.height
////        if (height < 30) {
////            height = 30
////        }
//        var width = size.width
////        if (width < 30) {
////            width = 30
////        }
//        return CGSize(width: height, height: width)
    }
    //
    func contentViewInsets() -> UIEdgeInsets {
        return UIEdgeInsets.init(top: 10, left: 13, bottom: 13, right: 10)
    }
    //
    func contentAttrSize() -> CGSize {
        let BDScreen = UIScreen.main.bounds.size
        //
        let textView = UITextView(frame: .zero)
        textView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        textView.isUserInteractionEnabled = true
        textView.isScrollEnabled = false
        textView.attributedText = contentAttr
        
        let width = Int(BDScreen.width) * 3 / 5
        textView.frame = CGRect(x: 0, y: 0, width: width, height: INTPTR_MAX)
        textView.sizeToFit()
        
        if textView.frame.size.height < 40 {
            var frame = textView.frame
            frame.size.height = 40
            textView.frame = frame
        }
        return textView.frame.size
    }
    
    // MARK: - Codable
    
    enum CodingKeys: String, CodingKey {
        case mid
        case content
        case imageUrl
        case voiceUrl
        case videoUrl
        case fileUrl
        case type
        case createdAt
        case status
        case client
        case answers
        case thread
        case user
        case currentUid
    }
    
//    public required init(from decoder: Decoder) throws {
//        let container = try decoder.container(keyedBy: CodingKeys.self)
//        mid = try container.decodeIfPresent(String.self, forKey: .mid)
//        content = try container.decodeIfPresent(String.self, forKey: .content)
//        imageUrl = try container.decodeIfPresent(String.self, forKey: .imageUrl)
//        voiceUrl = try container.decodeIfPresent(String.self, forKey: .voiceUrl)
//        videoUrl = try container.decodeIfPresent(String.self, forKey: .videoUrl)
//        fileUrl = try container.decodeIfPresent(String.self, forKey: .fileUrl)
//        type = try container.decodeIfPresent(String.self, forKey: .type)
//        createdAt = try container.decodeIfPresent(String.self, forKey: .createdAt)
//        status = try container.decodeIfPresent(String.self, forKey: .status)
//        client = try container.decodeIfPresent(String.self, forKey: .client)
//        answers = try container.decodeIfPresent([BDAnswerModel].self, forKey: .answers)
//        thread = try container.decodeIfPresent(BDThreadModel.self, forKey: .thread)
//        user = try container.decodeIfPresent(BDUserModel.self, forKey: .user)
//    }
//
//    public func encode(to encoder: Encoder) throws {
//        var container = encoder.container(keyedBy: CodingKeys.self)
//        try container.encode(mid, forKey: .mid)
//        try container.encode(content, forKey: .content)
//        try container.encode(imageUrl, forKey: .imageUrl)
//        try container.encode(voiceUrl, forKey: .voiceUrl)
//        try container.encode(videoUrl, forKey: .videoUrl)
//        try container.encode(fileUrl, forKey: .fileUrl)
//        try container.encode(type, forKey: .type)
//        try container.encode(createdAt, forKey: .createdAt)
//        try container.encode(status, forKey: .status)
//        try container.encode(client, forKey: .client)
//        try container.encode(answers, forKey: .answers)
//        try container.encode(thread, forKey: .thread)
//        try container.encode(user, forKey: .user)
//    }
}


//public class BDMessageModel: Codable, Identifiable, ObservableObject {
//    //
//    init() {
//        thread = BDThreadModel()
//        user = BDUserModel()
//    }
//    //
//    var mid: String? = ""
//    var content: String? = ""
//    var contentAttr: NSAttributedString?
//    //
//    var imageUrl: String? = ""
//    var voiceUrl: String? = ""
//    var videoUrl: String? = ""
//    var fileUrl: String? = ""
////    var nickname: String? = ""
////    var avatar: String? = ""
//    var type: String? = ""
//    var topic: String? = ""
//    var createdAt: String? = ""
//    var status: String? = ""
////    var isSend: Bool? = false
////    var currentUid: String? = ""
//    var client: String? = ""
//    //
//    var thread: BDThreadModel?
//    var user: BDUserModel?
//    //
//    func isNotification() -> Bool {
//        if (type == BD_MESSAGE_TYPE_NOTIFICATION_THREAD) {
//            return false
//        }
//        if (type!.hasPrefix(BD_MESSAGE_TYPE_NOTIFICATION)) {
//            return true
//        }
//        return false
//    }
//    //
//    func isSend() -> Bool {
//        return user?.uid == BDSettings.getUid()
//    }
//    //
////    @NotCoded var contentAttr: NSAttributedString?
//    //
////    private enum CodingKeys: String, CodingKey {
////        case timestamp = "createdAt"
////    }
//}


