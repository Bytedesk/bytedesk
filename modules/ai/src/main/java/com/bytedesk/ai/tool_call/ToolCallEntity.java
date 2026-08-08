/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool_call;

import java.time.ZonedDateTime;

import com.bytedesk.ai.tool.ToolTypeEnum;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ai_tool_call")
public class ToolCallEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "tool_uid")
    private String toolUid;

    @Column(name = "tool_key")
    private String toolKey;

    @Column(name = "runtime_tool_name")
    private String runtimeToolName;

    @Column(name = "binding_type")
    private String bindingType;

    private String name;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    @Column(name = "tool_call_type")
    private String type = ToolTypeEnum.CUSTOM.name();

    @Column(name = "provider_name")
    private String provider;

    @Column(name = "model_name")
    private String model;

    @Column(name = "robot_uid")
    private String robotUid;

    @Column(name = "thread_uid")
    private String threadUid;

    @Column(name = "message_uid")
    private String messageUid;

    @Builder.Default
    @Column(name = "call_status")
    private String status = ToolCallStatusEnum.PENDING.name();

    @Builder.Default
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Builder.Default
    @Column(name = "is_approved")
    private Boolean approved = false;

    @Column(name = "audit_uid")
    private String auditUid;

    @Builder.Default
    @Column(name = "duration_ms")
    private Long durationMs = 0L;

    @Column(name = "request_payload", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String responsePayload;

    @Column(name = "error_message", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    @Column(name = "tool_context", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String toolContext;

    @Column(name = "started_at")
    private ZonedDateTime startedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;
}
