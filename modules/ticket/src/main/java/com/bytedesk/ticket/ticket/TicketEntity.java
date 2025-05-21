/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 14:09:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.comment.TicketCommentEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true, exclude = { "attachments" })
@EntityListeners({TicketEntityListener.class})
@Entity(name = "bytedesk_ticket")
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity extends BaseEntity {
    
    @Column(nullable = false)
    private String title;           // 工单标题(必填)
    
    // 非结构化内容
    private String description;     // 工单描述(选填)

    @Builder.Default
    private String status = TicketStatusEnum.NEW.name();          // 状态(新建/处理中/已解决/已关闭)
    
    @Builder.Default
    private String priority = TicketPriorityEnum.LOW.name();        // 优先级(低/中/高/紧急)

    @Builder.Default
    private String type = TicketTypeEnum.AGENT.name();        // 类型(agent/group)

    // 对应在线客服会话: 跟threadUid合并
    @Column(name = "thread_topic")
    private String topic;

    // 对应工单会话，工单会话uid。每一个在线客服会话，可以创建多个工单，每个工单对应一个工单会话
    private String threadUid;

    // 关联category，工单分类
    private String categoryUid;

    private String workgroupUid; // 工作组

    private String departmentUid; // 部门

    // 统一使用member entity
    // 使用UserProtobuf json格式化
    // 一个工单一个处理人，一个处理人可以处理多个工单
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String assignee = BytedeskConsts.EMPTY_JSON_STRING;
    
    // 统一使用member entity
    // 使用UserProtobuf json格式化
    // 一个工单一个报告人，一个报告人可以报告多个工单
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String reporter = BytedeskConsts.EMPTY_JSON_STRING;

    // 工单评论
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketCommentEntity> comments;

    // 工单附件
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketAttachmentEntity> attachments;

    // 流程实例ID
    private String processInstanceId;

    // 流程定义实体UID
    private String processEntityUid;

    // 是否评价
    @Builder.Default
    @Column(name = "is_rated")
    private Boolean rated = false;

    // 满意度评价
    @Builder.Default
    private Integer rating = TicketRatingEnum.GOOD.getValue();

    // 客户验证
    @Builder.Default
    @Column(name = "is_verified")
    private Boolean verified = false;

    // 解决时间
    private LocalDateTime resolvedTime;

    // 关闭时间
    private LocalDateTime closedTime;

    // 工单会话client
    private String client;

    // 工单创建者
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;

    // 获取工单的处理人
    public UserProtobuf getAssignee() {
        return JSON.parseObject(assignee, UserProtobuf.class);
    }
    public String getAssigneeString() {
        return assignee;
    }

    // 获取工单的报告人
    public UserProtobuf getReporter() {
        return JSON.parseObject(reporter, UserProtobuf.class);
    }
    public String getReporterString() {
        return reporter;
    }

} 