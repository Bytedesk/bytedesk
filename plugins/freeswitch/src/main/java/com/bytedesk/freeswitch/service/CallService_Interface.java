/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 15:25:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 09:28:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service;

// import java.util.List;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import com.bytedesk.freeswitch.model.CallInfo;
// import com.bytedesk.freeswitch.model.UserInfo;

// /**
//  * 呼叫服务接口
//  */
// public interface CallService {
    
//     /**
//      * 发起呼叫
//      *
//      * @param fromUser 主叫用户ID
//      * @param toUser   被叫用户ID
//      * @return 呼叫ID
//      */
//     String makeCall(String fromUser, String toUser);
    
//     /**
//      * 结束呼叫
//      *
//      * @param callUuid 呼叫UUID
//      * @return 是否成功
//      */
//     boolean hangupCall(String callUuid);
    
//     /**
//      * 转接呼叫
//      *
//      * @param callUuid 呼叫UUID
//      * @param destUser 目标用户
//      * @return 是否成功
//      */
//     boolean transferCall(String callUuid, String destUser);
    
//     /**
//      * 保持/取消保持呼叫
//      *
//      * @param callUuid 呼叫UUID
//      * @param hold     是否保持
//      * @return 是否成功
//      */
//     boolean holdCall(String callUuid, boolean hold);
    
//     /**
//      * 静音/取消静音呼叫
//      *
//      * @param callUuid 呼叫UUID
//      * @param mute     是否静音
//      * @return 是否成功
//      */
//     boolean muteCall(String callUuid, boolean mute);
    
//     /**
//      * 获取呼叫详情
//      *
//      * @param callId 呼叫ID
//      * @return 呼叫信息
//      */
//     CallInfo getCall(String callId);
    
//     /**
//      * 根据状态获取呼叫列表
//      *
//      * @param status   呼叫状态
//      * @param pageable 分页信息
//      * @return 分页呼叫信息
//      */
//     Page<CallInfo> getCallsByStatus(String status, Pageable pageable);
    
//     /**
//      * 获取用户的呼叫记录
//      *
//      * @param userUid  用户ID
//      * @param status   呼叫状态
//      * @param pageable 分页信息
//      * @return 分页呼叫信息
//      */
//     Page<CallInfo> getUserCalls(String userUid, String status, Pageable pageable);
    
//     /**
//      * 获取当前活跃的呼叫
//      *
//      * @return 活跃呼叫列表
//      */
//     List<CallInfo> getActiveCalls();
    
//     /**
//      * 获取可用的座席列表
//      *
//      * @return 可用座席列表
//      */
//     List<UserInfo> getAvailableAgents();
// }
