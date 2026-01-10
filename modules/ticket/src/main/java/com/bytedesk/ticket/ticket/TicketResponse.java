/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:02:49
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

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.attachment.TicketAttachmentResponse;
import com.bytedesk.ticket.utils.TicketConvertUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;
    // 
    private String title;
    // 问题描述
    private String description;
    // 联系称呼
    private String contactName;
    // 联系手机号
    private String phone;
    // 联系邮箱
    private String email;
    // 联系微信
    private String wechat;
    // 
    private String ticketNumber;
    // 
    private String status;
    private String priority;
    private String type;
    // 
    private String topic;
    private String visitorThreadTopic;
    // 工单对话
    private String threadUid;
    // 关联访客对话
    private String visitorThreadUid;
    // 
    private String categoryUid;
    private String workgroupUid;
    private String departmentUid;
    // 
    // 工单处理人
    // 使用UserProtobuf json格式化
    private UserProtobuf assignee;
    // 
    // 工单提出者
    // 使用UserProtobuf json格式化
    private UserProtobuf reporter;
    // 
    private Set<TicketAttachmentResponse> attachments;

    // 
     private String ticketSettingsUid;
    // 流程实例ID
    private String processInstanceId;
    // 流程定义实体UID，同时作为 Flowable 的 processDefinitionKey
    private String processEntityUid;
    private String formEntityUid;

    // 客户验证
    private Boolean verified;
    // 自定义表单 json schema
    private String schema;
    
    // 
    public static TicketResponse fromEntity(TicketEntity ticket) {
        return TicketConvertUtils.convertToResponse(ticket);
    }
} 