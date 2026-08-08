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
package com.bytedesk.ai.tool;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.ai.tool.utils.McpExposureModeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Registry entry for an AI tool binding that can be exposed to robots or other
 * orchestration flows.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ToolEntityListener.class})
@Table(name = "bytedesk_ai_tool")
public class ToolEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Stable unique key used by the frontend and runtime for deduplication.
     */
    @Column(name = "tool_key")
    private String key;

    /**
     * Display name of the tool.
     */
    private String name;

    /**
     * Description of the tool.
     */
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Registry type such as BUILTIN or CUSTOM.
     */
    @Builder.Default
    @Column(name = "tool_type")
    private String type = ToolTypeEnum.CUSTOM.name();

    /**
     * Functional category shown in admin UI (utility, workflow, data, etc.).
     */
    @Column(name = "category_name")
    private String category;

    /**
     * Optional emoji/icon marker.
     */
    private String icon;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Column(name = "binding_type")
    private String bindingType;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "class_name")
    private String className;

    @Column(name = "method_name")
    private String methodName;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String endpoint;

    @Column(name = "input_schema", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String inputSchema;

    @Column(name = "output_schema", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String outputSchema;

    @Column(name = "system_prompt", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String systemPrompt;

    @Builder.Default
    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Builder.Default
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Column(name = "intent_keywords", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String intentKeywords;

    @Builder.Default
    @Column(name = "intent_match_mode")
    private String intentMatchMode = "KEYWORD";

    @Builder.Default
    @Column(name = "mcp_exposure_mode")
    private String mcpExposureMode = McpExposureModeEnum.NONE.name();

    @Column(name = "allowed_methods", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String allowedMethods;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String metadata;
}
