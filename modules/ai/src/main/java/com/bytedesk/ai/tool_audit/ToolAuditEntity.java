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
package com.bytedesk.ai.tool_audit;

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
@Table(name = "bytedesk_ai_tool_audit")
public class ToolAuditEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "tool_call_uid")
    private String toolCallUid;

    @Column(name = "tool_uid")
    private String toolUid;

    @Column(name = "tool_key")
    private String toolKey;

    private String name;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    @Column(name = "tool_audit_type")
    private String type = ToolTypeEnum.CUSTOM.name();

    @Builder.Default
    @Column(name = "audit_status")
    private String status = ToolAuditStatusEnum.PENDING.name();

    @Builder.Default
    @Column(name = "audit_action")
    private String action = "SUBMITTED";

    @Builder.Default
    @Column(name = "is_approved")
    private Boolean approved = false;

    @Column(name = "requester_user_uid")
    private String requesterUserUid;

    @Column(name = "approver_user_uid")
    private String approverUserUid;

    @Column(name = "request_payload", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String requestPayload;

    @Column(name = "decision_comment", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String decisionComment;

    @Column(name = "audit_context", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String auditContext;

    @Column(name = "audited_at")
    private ZonedDateTime auditedAt;
}
