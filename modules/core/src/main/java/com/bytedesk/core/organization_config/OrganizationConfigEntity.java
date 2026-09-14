/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.organization_config;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 组织级配置实体（通用 KV）
 *
 * 存储组织级运行时配置覆盖值（如 TTS/ASR/OCR apiKey、默认模型等），
 * 优先级高于系统级静态配置（properties / compose 环境变量）：
 *
 *   最终生效值 = 组织覆盖值（configValue 非空）
 *              └─ 否则回退系统级静态配置
 *
 * 扩展方式：新增配置项只需在 OrganizationConfigKeyEnum 注册 key，无需改表。
 *
 * 与 platform 级 SystemConfigEntity 的区别：
 * - 作用域为组织（orgUid 为各组织真实 uid，非哨兵值）；
 * - 权限为组织管理员（ADMIN/SUPER），读写均按当前登录用户所属组织隔离。
 *
 * Database Table: bytedesk_core_organization_config
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_organization_config",
        uniqueConstraints = @UniqueConstraint(columnNames = {"config_key", "org_uid"}),
        indexes = @Index(name = "idx_organization_config_org_uid", columnList = "org_uid"))
public class OrganizationConfigEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 配置键，受控于 OrganizationConfigKeyEnum，例：ai.asr.apiKey / ai.tts.model
     */
    @Column(name = "config_key", nullable = false, length = 128)
    private String configKey;

    /**
     * 配置值（统一 TEXT 存储，valueType 决定解析方式；apiKey 类明文存储，查询接口脱敏返回）
     */
    @Column(name = "config_value", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String configValue;

    /**
     * 值类型：STRING / BOOLEAN / INTEGER / JSON / TEXT
     */
    @Builder.Default
    @Column(name = "value_type", nullable = false, length = 16)
    private String valueType = "STRING";

    /**
     * 分组（前端分组渲染）：AI_ASR / AI_TTS / AI_OCR 等
     */
    @Column(name = "config_group", length = 32)
    private String configGroup;

    /**
     * 展示名（供后台 UI 展示与未来审计）
     */
    @Column(name = "display_name", length = 128)
    private String displayName;

    /**
     * 配置项说明
     */
    @Column(name = "description", length = 512)
    private String description;

    /**
     * 是否在后台可视化页面展示与编辑（false 的 key 仅程序内使用）
     */
    @Builder.Default
    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    /**
     * 排序权重
     */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
