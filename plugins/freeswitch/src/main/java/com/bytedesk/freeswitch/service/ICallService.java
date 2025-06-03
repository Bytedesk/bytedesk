/*
 * @Author: GitHub Copilot
 * @Date: 2025-06-03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service;

/**
 * 呼叫服务接口
 */
public interface ICallService {
    
    /**
     * 发起呼叫
     *
     * @param fromUser 主叫用户ID
     * @param toUser   被叫用户ID
     * @return 呼叫ID
     */
    String makeCall(String fromUser, String toUser);
    
    /**
     * 应答呼叫
     *
     * @param callId 呼叫ID
     * @return 是否成功
     */
    boolean answerCall(String callId);
    
    /**
     * 拒绝呼叫
     *
     * @param callId 呼叫ID
     * @return 是否成功
     */
    boolean rejectCall(String callId);
    
    /**
     * 结束呼叫
     *
     * @param callId 呼叫ID
     * @return 是否成功
     */
    boolean endCall(String callId);
    
    /**
     * 发送DTMF按键
     *
     * @param callId 呼叫ID
     * @param digit  按键值
     * @return 是否成功
     */
    boolean sendDtmf(String callId, String digit);
    
    /**
     * 静音/取消静音
     *
     * @param callId 呼叫ID
     * @param mute   是否静音
     * @return 是否成功
     */
    boolean toggleMute(String callId, boolean mute);
    
    /**
     * 处理呼叫开始
     *
     * @param callerId    主叫号码
     * @param destination 被叫号码
     * @param uuid        呼叫UUID
     */
    void handleCallStart(String callerId, String destination, String uuid);
    
    /**
     * 处理呼叫应答
     *
     * @param uuid 呼叫UUID
     */
    void handleCallAnswered(String uuid);
    
    /**
     * 处理呼叫结束
     *
     * @param uuid        呼叫UUID
     * @param hangupCause 挂断原因
     */
    void handleCallEnd(String uuid, String hangupCause);
    
    /**
     * 处理DTMF按键
     *
     * @param uuid  呼叫UUID
     * @param digit 按键值
     */
    void handleDtmf(String uuid, String digit);
}
