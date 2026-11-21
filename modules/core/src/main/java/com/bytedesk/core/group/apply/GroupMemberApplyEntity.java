/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.apply;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

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

/**
 * 入群申请（公开群 needApproval=true 时）
 * 单条当前申请模型：每个 (orgUid, groupUid, userUid) 唯一，重新申请递增 attempt
 * 申请人 userUid 直接使用 BaseEntity 继承字段（列名 user_uid，默认驼峰转下划线策略）
 * groupUid 存字符串，避免实体回环关联与懒加载问题（与 GroupEntity.robotUids 同理）
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_team_group_apply", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_group_apply_org_group_user",
                columnNames = { "org_uid", "group_uid", "user_uid" })
}, indexes = {
        @jakarta.persistence.Index(name = "idx_group_apply_group_status", columnList = "org_uid,group_uid,apply_status"),
        @jakarta.persistence.Index(name = "idx_group_apply_user", columnList = "org_uid,user_uid,apply_status")
})
public class GroupMemberApplyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 目标群 uid */
    @Column(name = "group_uid", length = 64, nullable = false)
    private String groupUid;

    /** 申请状态 */
    @Builder.Default
    @Column(name = "apply_status", nullable = false)
    private String status = GroupMemberApplyStatusEnum.PENDING.name();

    /** 申请轮次：终态后重新申请递增 */
    @Builder.Default
    @Column(name = "attempt", nullable = false)
    private Integer attempt = 1;

    /** 申请理由（可选） */
    @Column(name = "reason", length = 255)
    private String reason;

    /** 拒绝原因 */
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    /** 处理人 userUid（群主/群管理员） */
    @Column(name = "handled_by_uid", length = 64)
    private String handledByUid;

    /** 处理时间 */
    @Column(name = "handled_at")
    private ZonedDateTime handledAt;

    public boolean isPending() {
        return GroupMemberApplyStatusEnum.PENDING.name().equals(status);
    }
}
