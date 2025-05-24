/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 10:17:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-24 10:20:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.model;

import lombok.Data;

/**
 * 呼叫信息
 */
@Data
public class CallInfo {

    /**
     * 呼叫ID
     */
    private String callId;
    
    /**
     * 主叫用户ID
     */
    private String fromUser;
    
    /**
     * 被叫用户ID
     */
    private String toUser;
    
    /**
     * FreeSwitch分配的UUID
     */
    private String uuid;
    
    /**
     * 呼叫状态
     * CALLING - 呼叫中
     * RINGING - 振铃中
     * ANSWERED - 已接通
     * REJECTED - 已拒绝
     * ENDED - 已结束
     * FAILED - 失败
     */
    private String status;
    
    /**
     * 开始时间
     */
    private long startTime;
    
    /**
     * 应答时间
     */
    private long answerTime;
    
    /**
     * 结束时间
     */
    private long endTime;
    
    /**
     * 挂断原因
     */
    private String hangupCause;
    
    /**
     * 获取通话时长（秒）
     */
    public int getDuration() {
        if (endTime > 0 && answerTime > 0) {
            return (int) ((endTime - answerTime) / 1000);
        } else if (endTime > 0 && startTime > 0) {
            return (int) ((endTime - startTime) / 1000);
        }
        return 0;
    }
}
