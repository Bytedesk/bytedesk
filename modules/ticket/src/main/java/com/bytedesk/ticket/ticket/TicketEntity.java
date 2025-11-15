/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:00:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.comment.TicketCommentEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;

/**
 * Support ticket entity for customer service and issue tracking
 * Manages customer support requests, assignments, and resolution tracking
 * 
 * Database Table: bytedesk_ticket
 * Purpose: Stores support tickets, their status, assignments, and resolution history
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "attachments" })
@EntityListeners({TicketEntityListener.class})
@Table(
    name = "bytedesk_ticket",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_ticket_org_number", columnNames = {"org_uid", "ticket_number"})
    }
)
@Entity(name = "bytedesk_ticket")
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    /**
     * Title of the support ticket (required field)
     */
    // @Column(nullable = false)
    // private String title;           // 工单标题(必填)
    
    /**
     * Detailed description of the ticket issue (optional)
     */
    private String description;     // 工单描述(选填)

    // 联系称呼
    private String contactName;     // 联系称呼
    
    // 联系手机号
    private String phone;           // 联系手机号
    
    // 联系邮箱
    private String email;           // 联系邮箱

    /**
     * Current status of the ticket (NEW, IN_PROGRESS, RESOLVED, CLOSED)
     */
    @Builder.Default
    private String status = TicketStatusEnum.NEW.name();          // 状态(新建/处理中/已解决/已关闭)
    
    /**
     * Priority level of the ticket (LOW, MEDIUM, HIGH, URGENT)
     */
    @Builder.Default
    private String priority = TicketPriorityEnum.LOW.name();        // 优先级(低/中/高/紧急)

    /**
     * Human friendly ticket number generated from ticket settings
     */
    @Column(name = "ticket_number", length = 64)
    private String ticketNumber;

    /**
     * Type of ticket (AGENT, GROUP)
     */
    @Builder.Default
    private String type = TicketTypeEnum.AGENT.name();        // 类型(agent/group)

    /**
     * Thread topic for online customer service session
     */
    @Column(name = "thread_topic")
    private String topic;

    /**
     * Associated thread UID for ticket conversation
     */
    private String threadUid;

    /**
     * Associated category UID for ticket classification
     */
    private String categoryUid;

    /**
     * Associated workgroup UID for ticket assignment
     */
    private String workgroupUid; // 工作组

    /**
     * Associated department UID for ticket routing
     */
    private String departmentUid; // 部门

    /**
     * Ticket assignee information stored as JSON string
     * 工单处理人
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String assignee = BytedeskConsts.EMPTY_JSON_STRING;
    
    /**
     * Ticket reporter information stored as JSON string
     * 工单提出者
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String reporter = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Comments associated with the ticket
     */
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketCommentEntity> comments;

    /**
     * Attachments associated with the ticket
     */
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketAttachmentEntity> attachments;

    /**
     * Process instance ID for workflow integration
     */
    private String processInstanceId;

    /**
     * Process definition entity UID
     */
    private String processEntityUid;

    /**
     * Whether the ticket has been rated by the customer
     */
    @Builder.Default
    @Column(name = "is_rated")
    private Boolean rated = false;

    /**
     * Customer satisfaction rating for the ticket
     */
    @Builder.Default
    private Integer rating = TicketRatingEnum.GOOD.getValue();

    /**
     * Whether the customer has been verified
     */
    @Builder.Default
    @Column(name = "is_verified")
    private Boolean verified = false;

    /**
     * Timestamp when the ticket was resolved
     */
    private ZonedDateTime resolvedTime;

    /**
     * Timestamp when the ticket was closed
     */
    private ZonedDateTime closedTime;

    /**
     * Client platform from which the ticket was created
     */
    private String channel;

    /**
     * 使用自定义表单 替换默认表单
     * 
     * Form structure definition stored as JSON format
     * Contains the complete form schema generated by frontend low-code editor
     */
    @Column(name = "form_schema", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String schema;

    /**
     * 当前工单处理人 使用 assignee 字段代替
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // private UserEntity owner;
    
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