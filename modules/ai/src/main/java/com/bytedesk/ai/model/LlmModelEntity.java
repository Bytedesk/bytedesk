/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:19:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 16:31:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * LLM model entity for AI model management
 * Manages large language model configurations and provider relationships
 * 
 * Database Table: bytedesk_ai_model
 * Purpose: Stores LLM model definitions, types, and provider associations
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ LlmModelEntityListener.class })
@Table(name = "bytedesk_ai_model")
public class LlmModelEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Model name used for API calls
     */
    private String name;
    
    /**
     * User-friendly nickname for the model
     */
    private String nickname;

    /**
     * Description of the model capabilities
     */
    @Builder.Default
    private String description = BytedeskConsts.EMPTY_STRING;

    /**
     * Type of LLM model (TEXT, EMBEDDING, etc.)
     */
    @Builder.Default
    @Column(name = "model_type")
    private String type = LlmModelTypeEnum.TEXT.name();

    /**
     * Associated provider UID
     */
    private String providerUid;

    /**
     * Name of the model provider
     */
    private String providerName;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // 是否允许租户调用系统API，默认不开启。如果开启了，则租户默认调用系统API
    @Builder.Default
    @Column(name = "is_system_enabled")
    private Boolean systemEnabled = false;

}
