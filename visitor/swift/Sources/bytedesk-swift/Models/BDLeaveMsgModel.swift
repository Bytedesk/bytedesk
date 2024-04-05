//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import Foundation

public class BDLeaveMsgModel: Codable {
    
    var lid: String? // 唯一lid
    var avatar: String? // 留言人头像
    var nickname: String? // 留言人昵称
    var client: String? // 留言人来源
    var mobile: String? // 手机
    var email: String? // 邮箱
    var content: String? // 留言内容
    var createdAt: String? // 留言时间
    var reply: String? // 回复内容
    var replied: Bool // 是否已经回复
    
}
