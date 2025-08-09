/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 12:42:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:42:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.call;

import com.bytedesk.core.base.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CallCallResponse extends BaseResponse {
    
    /**
     * 呼叫UUID
     */
    private String callUuid;
    
    /**
     * 呼叫类型
     * INBOUND - 呼入
     * OUTBOUND - 呼出
     * INTERNAL - 内部
     */
    private String type;
    
    /**
     * 主叫号码
     */
    private String callerNumber;
    
    /**
     * 被叫号码
     */
    private String calleeNumber;
    
    /**
     * 呼叫状态
     * QUEUED - 排队中
     * RINGING - 振铃中
     * IN_PROGRESS - 通话中
     * COMPLETED - 已完成
     * FAILED - 失败
     * ABANDONED - 放弃
     */
    private String status;
    
    /**
     * 呼叫开始时间
     */
    private long startTime;
    
    /**
     * 呼叫结束时间
     */
    private long endTime;
    
    /**
     * 呼叫时长（秒）
     */
    private Integer duration;
    
    /**
     * 等待时长（秒）
     */
    private Integer waitTime;
    
    /**
     * 队列ID
     */
    private Long queueId;
    
    /**
     * 坐席ID
     */
    private Long agentId;
    
    /**
     * 呼叫技能（JSON格式）
     */
    private String skills;
    
    /**
     * 备注
     */
    private String notes;
}
