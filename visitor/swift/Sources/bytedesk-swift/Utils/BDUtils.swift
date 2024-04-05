//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import Foundation
import UIKit

public class BDUtils {
    
    static func getGuid() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMddHHmmss"
        let timestamp = dateFormatter.string(from: Date())
        return timestamp + UUID().uuidString.replacingOccurrences(of: "-", with: "").lowercased()
    }
    
    static func getCurrentDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return dateFormatter.string(from: Date())
    }
    
    static func getCurrentTimestamp() -> String {
        let currentTimeStamp = Date().timeIntervalSince1970
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        let formattedTimeStamp = dateFormatter.string(from: Date(timeIntervalSince1970: currentTimeStamp))
        print("Current Timestamp: \(formattedTimeStamp)")
        return formattedTimeStamp
    }

    static func stringToDate(_ string: String) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return dateFormatter.date(from: string)
    }

    static func getOptimizedTimestamp(_ dateString: String) -> String {
        guard let date = stringToDate(dateString) else {
            return ""
        }
        
        let calendar = Calendar.current
        var dateComponents = calendar.dateComponents([.year, .month, .day], from: Date())
        
        dateComponents.day = dateComponents.day ?? 0
        let today = calendar.date(from: dateComponents) // 今天
        
        dateComponents.day = (dateComponents.day ?? 0) - 1
        let yesterday = calendar.date(from: dateComponents) // 昨天
        
        dateComponents.day = (dateComponents.day ?? 0) - 1
        let twoDaysAgo = calendar.date(from: dateComponents) // 前天
        
        dateComponents.day = (dateComponents.day ?? 0) - 5
        let lastWeek = calendar.date(from: dateComponents) // 上星期
        
        let lastMessageSentDate = date
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone.current
        var stringFromDate = ""
        
        if lastMessageSentDate.compare(today ?? Date()) == .orderedDescending {
            formatter.dateFormat = "HH:mm:ss"
            stringFromDate = formatter.string(from: lastMessageSentDate)
        } else if lastMessageSentDate.compare(yesterday ?? Date()) == .orderedDescending {
            formatter.dateFormat = "HH:mm:ss"
            stringFromDate = formatter.string(from: lastMessageSentDate)
            stringFromDate = "昨天 " + stringFromDate
        } else if lastMessageSentDate.compare(twoDaysAgo ?? Date()) == .orderedDescending {
            formatter.dateFormat = "HH:mm:ss"
            stringFromDate = formatter.string(from: lastMessageSentDate)
            stringFromDate = "前天 " + stringFromDate
        } else if lastMessageSentDate.compare(lastWeek ?? Date()) == .orderedDescending {
            formatter.dateFormat = "MM-dd HH:mm"
            stringFromDate = formatter.string(from: lastMessageSentDate)
        } else {
            formatter.dateFormat = "MM-dd HH:mm"
            stringFromDate = formatter.string(from: lastMessageSentDate)
        }
        
        return stringFromDate
    }

    static func getCurrentTimeString() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMddHHmmss"
        return dateFormatter.string(from: Date())
    }
    
    static func encodeString(_ originalUrl: String) -> String? {
        let encodeUrlSet = CharacterSet.urlQueryAllowed
        return originalUrl.addingPercentEncoding(withAllowedCharacters: encodeUrlSet)
    }

    static func decodeString(_ string: String) -> String? {
        return string.removingPercentEncoding
    }
    
    static func appendAnswersToContent(_ messageModel: BDMessageModel) -> String? {
        for j in 0..<messageModel.answers.count {
            let msgAnswer = messageModel.answers[j]
            messageModel.content = String(format: "%@\n\n<p><a href=\"robot://%@??%@??%@\">%@</a></p>", messageModel.content!, msgAnswer.aid!, BDUtils.encodeString(msgAnswer.question!)!, BDUtils.encodeString(msgAnswer.answer!)!, msgAnswer.question!)
        }
        return messageModel.content
    }
    
    static func transformContentToContentAttr(_ content: String) -> NSAttributedString? {
        var contentAttr: NSAttributedString = NSAttributedString(string: content)
        let data = content.data(using: String.Encoding.unicode)
        if let attributedString = try? NSMutableAttributedString(data: data!, options: [.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil) {
            attributedString.addAttributes([.font: UIFont.systemFont(ofSize: 16)], range: NSRange(location: 0, length: attributedString.length))
            contentAttr = attributedString
        }
        return contentAttr
    }
    
    static func setButtonTitleColor(_ button: UIButton) {
        let mode = UITraitCollection.current.userInterfaceStyle
        if mode == .dark {
            // 深色模式
        } else if mode == .light {
            // 浅色
            button.setTitleColor(.black, for: .normal)
        }
    }
    
    static func documentTypes() -> [String] {
        return [
            "public.content",
            "public.text",
            // "public.source-code",
            // "public.image",
            // "public.audiovisual-content",
            "com.adobe.pdf",
            "com.apple.keynote.key",
            "com.microsoft.word.doc",
            "com.microsoft.excel.xls",
            "com.microsoft.powerpoint.ppt"
            // "public.rtf",
            // "public.html"
        ]
    }
    
    static func dictToJson(_ dict: [String: Any]) -> String? {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: dict, options: .prettyPrinted)
            let jsonString = String(data: jsonData, encoding: .utf8)
            return jsonString
        } catch {
            print("dictToJson error: \(error.localizedDescription)")
            return nil
        }
    }
}
