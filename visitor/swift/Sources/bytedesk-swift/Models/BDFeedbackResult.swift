//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/15.
//

import Foundation

public class BDFeedbackResult: Codable {
    //
    public var status_code: Int?
    public var message: String?
    public var data: BDFeedbackModel?
}
