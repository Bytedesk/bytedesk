//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import Foundation

public class BDWorkGroupModel: Codable {
    
    // 本地表主键id
//    var uuId: NSNumber?
    // 服务器端主键
//    var serverId: NSNumber?
    
    // 唯一数字id，保证唯一性
    var wid: String?
    // 工作组名称
    var nickname: String?
    // 默认头像
    var avatar: String?
    // 是否默认机器人接待
    var isDefaultRobot: Bool?
    // 是否无客服在线时，启用机器人接待
    var isOfflineRobot: Bool?
    // 宣传语，对话框顶部，
    var slogan: String?
    // 进入页面欢迎语
    var welcomeTip: String?
    // 接入客服欢迎语
    var acceptTip: String?
    // 非工作时间提示
    var nonWorkingTimeTip: String?
    // 离线提示
    var offlineTip: String?
    // 客服关闭会话提示语
    var closeTip: String?
    // 会话自动关闭会话提示语
    var autoCloseTip: String?
    // 是否强制评价
    var isForceRate: Bool?
    // 路由类型
    var routeType: String?
    // 是否是系统分配的默认工作组，不允许删除
    var isDefault: Bool?
    // 描述、介绍, 对话框 右侧 "关于我们"
    var about: String?
    // 左上角，昵称下面描述语
    var description: String?
//     当前登录用户Uid
//    var currentUid: String?
    
}
