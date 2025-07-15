/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 07:31:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 14:48:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.dto;
import java.util.Date;
import java.util.Map;

import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryActivityResponse {
    private String id;                    // 活动实例ID
    private String activityId;            // 活动定义ID
    private String activityName;          // 活动名称
    private String activityType;          // 活动类型
    private String processDefinitionId;   // 流程定义ID
    private String processInstanceId;     // 流程实例ID
    private String executionId;           // 执行实例ID
    private String taskId;                // 任务ID
    private String calledProcessInstanceId; // 被调用的子流程实例ID
    private String assignee;              // 处理人
    private Date startTime;               // 开始时间
    private Date endTime;                 // 结束时间
    private Long durationInMillis;        // 持续时间
    private String tenantId;              // 租户ID
    private String description;           
    
    private Map<String, Object> taskLocalVariables;    // 任务局部变量
    private Map<String, Object> processVariables;      // 流程变量

    // 
    public String getStartTime() {
        return BdDateUtils.formatDatetimeToString(startTime);
    }

    public String getEndTime() {
        return BdDateUtils.formatDatetimeToString(endTime);
    }
} 