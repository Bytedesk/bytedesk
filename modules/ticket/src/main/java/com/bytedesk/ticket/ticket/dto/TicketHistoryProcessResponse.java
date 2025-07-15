/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 09:13:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 14:48:15
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

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryProcessResponse {
    private String processInstanceId;      // 流程实例ID
    private String processDefinitionId;    // 流程定义ID
    private String processDefinitionName;  // 流程定义名称
    private String processDefinitionKey;   // 流程定义Key
    private Integer processDefinitionVersion; // 流程定义版本
    private String businessKey;            // 业务键(ticketUid)
    private String startUserId;           // 发起人ID
    private Date startTime;               // 开始时间
    private Date endTime;                 // 结束时间
    private Long durationInMillis;        // 持续时间(毫秒)
    private String deleteReason;          // 删除原因
    private String tenantId;              // 租户ID
    private String name;                  // 流程名称
    private String description;           // 描述
    private String status;                // 状态
    // 
    private UserProtobuf assignee;              // 分配给谁
    private String priority;              // 优先级
    private String categoryUid;           // 分类UID
    // 
    public String getStartTime() {
        return BdDateUtils.formatDatetimeToString(startTime);
    }

    public String getEndTime() {
        return BdDateUtils.formatDatetimeToString(endTime);
    }
} 