//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/8/21.
//

import Foundation

// , ObservableObject, Identifiable, Equatable
public class BDUserResult: Codable {
    //
    public var status_code: Int?
    public var message: String?
    public var data: BDUserModel?
}
