/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 14:40:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket.dto;

import java.util.Set;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.attachment.TicketAttachmentResponse;
import com.bytedesk.ticket.ticket.TicketEntity;
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
    // 
    private String title;
    private String description;
    // 
    private String status;
    private String priority;
    // 
    private String type;

    // private ThreadResponse serviceThread;
    private String serviceThreadTopic;

    // private ThreadResponse thread;
    private String threadUid;
    // 
    // private CategoryResponse category;
    private String categoryUid;

    // 使用UserProtobuf json格式化
    private UserProtobuf user;
    
    // 使用UserProtobuf json格式化
    // private WorkgroupResponse workgroup;
    private UserProtobuf workgroup;
    // 
    // 使用UserProtobuf json格式化
    // private AgentResponse assignee;
    private UserProtobuf assignee;
    // 
    // 使用UserProtobuf json格式化
    // private UserResponse reporter;
    private UserProtobuf reporter;
    // 
    private Set<TicketAttachmentResponse> attachments;
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

    // 
    private String createdAt;
    private String updatedAt;
    // 
    public static TicketResponse fromEntity(TicketEntity ticket) {
        return TicketConvertUtils.convertToResponse(ticket);
    }
} 