//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/9.
//

import Foundation

public class BDQueueModel: Codable, Identifiable, ObservableObject {
    
//    var uuId: NSNumber? // 本地表主键id
//    var serverId: NSNumber? // 服务器端主键
    
    var qid: String? // 唯一数字id，保证唯一性
    var nickname: String? // 访客昵称
    var avatar: String? // 访客头像
    var visitorUid: String? // 访客uid
    var visitorClient: String? // 访客来源客户端
    var agentUid: String? // 客服uid
    var agentClient: String? // 客服接入端
    var threadTid: String? // 所属thread tid
    var workgroupWid: String? // 所属工作组 wid
    var actionedAt: String? // 接入客服时间
    var status: String? // queuing/accepted/leaved
    var currentUid: String? // 当前登录用户Uid
    
    
}
