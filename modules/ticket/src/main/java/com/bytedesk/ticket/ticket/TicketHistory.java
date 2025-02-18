package com.bytedesk.ticket.ticket;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketHistory {
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
} 