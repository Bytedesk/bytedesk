//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/21.
//

import Foundation

public class BDUserModel: Codable, Identifiable, ObservableObject {
    //
    public var uid: String?
    public var username: String?
    public var nickname: String?
    public var avatar: String?
    public var mobile: String?
    public var description: String?
    public var sex: Bool?
    public var location: String?
    public var birthday: String?
    public var subDomain: String?
    //
//    init(uid: String, nickname: String, avatar: String) {
//        self.uid = uid
//        self.nickname = nickname
//        self.avatar = avatar
//    }
}
