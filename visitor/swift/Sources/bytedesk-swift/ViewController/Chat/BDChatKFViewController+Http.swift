//
//  File.swift
//  
//
//  Created by 宁金鹏 on 2023/9/7.
//

import UIKit

extension BDChatKFViewController {
    
    
    /// 请求会话
    func requestThread() {
        // debugPrint("chatkf: \(#function), type: \(mThreadType ?? "")")
        
        if (self.mThreadType == BD_THREAD_TYPE_APPOINTED) {
            
            BDCoreApis.requestThreadAgent(agentUid: self.mUUid) { threadResult in
                self.dealWithRequestThreadResult(threadResult)
            } onFailure: { error in
                BDToast.show(message: error)
            }

        } else {
            
            BDCoreApis.requestThreadWorkgroup(workgroupWid: self.mUUid) { threadResult in
                self.dealWithRequestThreadResult(threadResult)
            } onFailure: { error in
                BDToast.show(message: error)
            }
        }
    }
    
    /// 强制转人工
    func requestAgent() {
        // debugPrint("\(#function)")
        
        BDCoreApis.requestAgent(workgroupWid: self.mUUid) { threadResult in
            self.dealWithRequestThreadResult(threadResult)
        } onFailure: { error in
            BDToast.show(message: error)
        }
    }
    
    func dealWithRequestThreadResult(_ threadResult: BDThreadResult) {
        // debugPrint("chatkf: \(#function), threadResult: \(threadResult)")
        //
        let messageModel = threadResult.data
        BDCoreApis.insertMessage(messageModel!)
        //
        mThreadModel = threadResult.data?.thread
        mTid = mThreadModel?.tid
        let appointed = mThreadModel?.appointed
        if (appointed!) {
            self.navigationItem.title = mThreadModel?.agent?.nickname
        } else {
            self.navigationItem.title = mThreadModel?.workGroup?.nickname
        }
        let statusCode = threadResult.status_code
        if (statusCode == 200 || statusCode == 201) {
            // 创建新会话 / 继续进行中会话
            
            if (self.mWithCustomDict) {
                let customJson = BDUtils.dictToJson(self.mCustomDict!)
                self.sendCommodityMessage(customJson)
            }
            
        } else if (statusCode == 202) {
            // 提示排队中
        } else if (statusCode == 203) {
            // 当前非工作时间，请自助查询或留言
            shareLeaveMsgButtonPressed()
        } else if (statusCode == 204) {
            // 当前无客服在线，请自助查询或留言
            shareLeaveMsgButtonPressed()
        } else if (statusCode == 205) {
            // 咨询前问卷
        } else if (statusCode == 206) {
            // 返回机器人初始欢迎语 + 欢迎问题列表
            mIsRobot = true
            // 切换到机器人模式
            mInputView?.switchToRobot()
            mPlusView?.switchToRobot()
            
        } else {
            // 请求会话失败
        }
        // TODO: 此方法简单粗暴，待改进，直接插入显示就好
        self.reloadTableData(false)
    }
    
    func sendRobotMessage(_ content: String?) {
        // debugPrint("\(#function)")
    }
    
    func queryRobotAnswer(_ qid: String?) {
        // debugPrint("\(#function)")
        
    }
    
    func rateRobotAnswer(_ aid: String?, mid: String?, rate: Bool?) {
        
    }
    
}
