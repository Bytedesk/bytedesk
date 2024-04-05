//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/8.
//

import UIKit

extension BDChatKFViewController {
    
    // MARK: 发送消息
    
    func sendTextMessage(_ content: String?) {
        // debugPrint("\(#function) content: \(content!)")
        
        let localId = self.insertLocalMessageAndReload(content: content!, type: BD_MESSAGE_TYPE_TEXT, isSend: true)
        //
        BDCoreApis.sendTextMessageProtobuf(mid: localId, content: content!, thread: mThreadModel!)
    }
    
    func sendImageMessage(_ imageUrl: String?) {
        // debugPrint("\(#function) imageUrl: \(imageUrl!)")
        
        let localId = self.insertLocalMessageAndReload(content: imageUrl!, type: BD_MESSAGE_TYPE_IMAGE, isSend: true)
        
        BDCoreApis.sendImageMessageProtobuf(mid: localId, imageUrl: imageUrl!, thread: mThreadModel!)
    }
    
    func sendFileMessage(_ fileUrl: String?) {
        // debugPrint("\(#function) fileUrl: \(fileUrl!)")
        
        let localId = self.insertLocalMessageAndReload(content: fileUrl!, type: BD_MESSAGE_TYPE_FILE, isSend: true)
        
        BDCoreApis.sendFileMessageProtobuf(mid: localId, fileUrl: fileUrl!, thread: mThreadModel!)
    }
    
    func sendVoiceMessage(_ voiceUrl: String?) {
        // debugPrint("\(#function) voiceUrl:\(voiceUrl!)")
        
        let localId = self.insertLocalMessageAndReload(content: voiceUrl!, type: BD_MESSAGE_TYPE_VOICE, isSend: true)
        
        BDCoreApis.sendVoiceMessageProtobuf(mid: localId, voiceUrl: voiceUrl!, thread: mThreadModel!)
    }
    
    func sendVideoMessage(_ videoUrl: String?) {
        // debugPrint("\(#function) videoUrl\(videoUrl!)")
        
        let localId = self.insertLocalMessageAndReload(content: videoUrl!, type: BD_MESSAGE_TYPE_VIDEO, isSend: true)
        
        BDCoreApis.sendVideoMessageProtobuf(mid: localId, videoUrl: videoUrl!, thread: mThreadModel!)
    }
    
    func sendCommodityMessage(_ content: String?) {
        
        let localId = self.insertLocalMessageAndReload(content: content!, type: BD_MESSAGE_TYPE_COMMODITY, isSend: true)
        
        BDCoreApis.sendCommodityMessageProtobuf(mid: localId, content: content!, thread: mThreadModel!)
    }
    
    
}
