/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.jobtitle;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 职务字典实体：组织级职务选项，供成员 jobTitle 快速选择。
 * 成员侧仍保存职务名称字符串，本实体仅提供选项，不与成员建立外键关联。
 *
 * Database Table: bytedesk_core_jobtitle
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_jobtitle")
public class JobTitleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 职务名称（展示与匹配值，成员保存该字符串）
     */
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    /**
     * 职务描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 排序值，越小越靠前
     */
    @Column(name = "sort_order")
    private Integer order;

    /**
     * 有效项唯一键：sha256(orgUid + ':' + normalized(name))。
     * 服务端计算并维护，软删除时置 NULL，允许多个已删除项并存；
     * 单列唯一约束保证同组织同名的有效项不可重复。
     * 不接受客户端提交该字段。
     */
    @Column(name = "active_key", length = 64, unique = true)
    private String activeKey;
}
