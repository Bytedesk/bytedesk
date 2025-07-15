/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-19 07:18:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 14:48:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryTaskResponse {
    private String taskId;                 // 任务ID
    private String taskName;               // 任务名称
    private String taskDefinitionKey;      // 任务定义Key
    private String taskDefinitionId;       // 任务定义ID
    private String description;            // 任务描述
    private String category;               // 任务类别
    private String formKey;                // 表单Key
    private String processInstanceId;      // 所属流程实例ID
    private String processDefinitionId;    // 所属流程定义ID
    private String executionId;            // 执行实例ID
    
    private String assignee;               // 任务处理人
    private String owner;                  // 任务所有者
    private List<String> candidateUsers;   // 候选用户列表
    private List<String> candidateGroups;  // 候选组列表
    
    private Integer priority;              // 优先级
    private Date createTime;               // 创建时间
    private Date dueDate;                  // 截止时间
    private Date claimTime;                // 认领时间
    private Date endTime;                  // 完成时间
    private Long durationInMillis;         // 持续时间(毫秒)
    private String deleteReason;           // 删除原因
    private String tenantId;               // 租户ID
    
    private Map<String, Object> taskLocalVariables;    // 任务局部变量
    private Map<String, Object> processVariables;      // 流程变量

    // 
    public String getCreateTime() {
        return BdDateUtils.formatDatetimeToString(createTime);
    }

    public String getDueDate() {
        return BdDateUtils.formatDatetimeToString(dueDate);
    }

    public String getClaimTime() {
        return BdDateUtils.formatDatetimeToString(claimTime);
    }

    public String getEndTime() {
        return BdDateUtils.formatDatetimeToString(endTime);
    }
}
