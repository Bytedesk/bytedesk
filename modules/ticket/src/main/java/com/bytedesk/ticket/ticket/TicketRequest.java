/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:03:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.Set;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String summary;
    private String description;

    // 联系称呼
    private String contactName;
    // 联系手机号
    private String phone;
    // 联系邮箱
    private String email;

    private String ticketNumber;
    // private String searchText;
    // 
    private String status;
    private String priority;
    // 
    // private String type;
    // 
    private String topic;
    private String threadUid;
    private String categoryUid;
    // 
    private String workgroupUid;
    private String departmentUid;
    // 
    private Boolean assignmentAll;

    // 工单处理人
    private String assigneeUid; // 方便get查询
    // private String assignee; // 原始json字符串
    private UserProtobuf assignee; // 方便post存储

    // 工单提出者
    private String reporterUid; // 方便get查询
    // private String reporter;  // 原始 JSON 字符串
    private UserProtobuf reporter; // 方便post存储

    // 前端根据创建时间范围查询
    private String createdAtStart;
    private String createdAtEnd;
    private Set<String> uploadUids;

    // 流程实例ID
    private String processInstanceId;
    // 流程定义实体UID
    private String processEntityUid;

    // 是否评价
    private Boolean rated;
    // 满意度评价
    private Integer rating;
    // 客户验证
    private Boolean verified;
    // 自定义表单 json schema
    private String schema;

    public String getAssigneeJson() {
        if (assignee == null) {
            return null;
        }
        return assignee.toJson();

    }

    public String getReporterJson() {
        if (reporter == null) {
            return null;
        }
        return reporter.toJson();
    }
} 