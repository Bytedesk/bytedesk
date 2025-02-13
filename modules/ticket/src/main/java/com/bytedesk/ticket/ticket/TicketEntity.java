/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 17:04:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.List;
import java.util.Set;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.listener.TicketEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
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

    // 结构化内容
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String form = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    private String status = TicketStatusEnum.NEW.name();          // 状态(新建/处理中/已解决/已关闭)
    
    @Builder.Default
    private String priority = TicketPriorityEnum.LOW.name();        // 优先级(低/中/高/紧急)

    @Builder.Default
    private String type = TicketTypeEnum.AGENT.name();        // 类型(agent/group)

    // 对应在线客服会话
    // @ManyToOne(fetch = FetchType.LAZY)
    // private ThreadEntity serviceThread;
    private String serviceThreadTopic;

    // 对应工单会话，工单会话uid。每一个在线客服会话，可以创建多个工单，每个工单对应一个工单会话
    // @ManyToOne(fetch = FetchType.LAZY)
    // private ThreadEntity thread;
    private String threadUid;

    // @ManyToOne(fetch = FetchType.LAZY)
    // private CategoryEntity category;
    private String categoryUid;

    // user, 使用UserProtobuf json格式化
    // 关联service thread ThreadEntity的user字段，访客信息
    @Builder.Default
    @Column(name = "ticket_user")
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    // 使用UserProtobuf json格式化
    // 一个工单一个工作组，一个工作组可以有多个工单
    // @ManyToOne(fetch = FetchType.LAZY)
    // private WorkgroupEntity workgroup;
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    // 使用UserProtobuf json格式化
    // 一个工单一个处理人，一个处理人可以处理多个工单
    // @ManyToOne(fetch = FetchType.LAZY)
    // private AgentEntity assignee;        // 处理人
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String assignee = BytedeskConsts.EMPTY_JSON_STRING;
    
    // 使用UserProtobuf json格式化
    // 一个工单一个报告人，一个报告人可以报告多个工单
    // @ManyToOne(fetch = FetchType.LAZY)
    // private UserEntity reporter;        // 报告人
    @Builder.Default
    // json字段格式，搜索时，对数据库有依赖，不方便迁移
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    // @JdbcTypeCode(SqlTypes.JSON)
    private String reporter = BytedeskConsts.EMPTY_JSON_STRING;

    // 工单评论
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketCommentEntity> comments;

    // 工单附件
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketAttachmentEntity> attachments;

    // 流程实例ID
    private String processInstanceId;
} 