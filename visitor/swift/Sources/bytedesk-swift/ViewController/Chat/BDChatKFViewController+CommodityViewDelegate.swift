//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/12.
//

import UIKit

extension BDChatKFViewController: BDMsgCommodityViewCellDelegate {
    
    
    func sendCommodityButtonClicked(_ messageModel: BDMessageModel) {
        // debugPrint("\(#function)")
        
        sendCommodityMessage(messageModel.content)
    }
    
//    func commodityBackgroundClicked(_ messageModel: BDMessageModel) {
//        // debugPrint("\(#function)")
//    }
    
}
