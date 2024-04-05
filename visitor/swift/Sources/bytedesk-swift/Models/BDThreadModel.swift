//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/21.
//

import Foundation

// ObservableObject, Identifiable
public class BDThreadModel: Codable, Identifiable, ObservableObject {
    //
    init() {
        visitor = BDUserModel()
        agent = BDUserModel()
        workGroup = BDWorkGroupModel()
    }
    //
    var tid: String?
    var topic: String? = ""
    var appointed: Bool? = false
    //
    var content: String?
    var timestamp: String?
    var unreadCount: Int?
    var type: String?
    //
    var client: String?
    //
    var visitor: BDUserModel?
    var agent: BDUserModel?
    var workGroup: BDWorkGroupModel?
    
}
