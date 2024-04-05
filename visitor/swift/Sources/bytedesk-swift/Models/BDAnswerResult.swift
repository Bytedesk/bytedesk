//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import Foundation

public class BDAnswerResult: Codable {
    //
    var status_code: Int?
    var message: String?
    var data: BDAnswerModel?
}
