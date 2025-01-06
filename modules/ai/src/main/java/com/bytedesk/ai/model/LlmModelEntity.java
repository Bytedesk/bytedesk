/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:19:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 10:37:00
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
import com.bytedesk.core.enums.LevelEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ LlmModelEntityListener.class })
@Table(name = "bytedesk_ai_model")
public class LlmModelEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 模型名称，用于调用
    private String name;
    
    // 便于记忆
    private String nickname;

    @Builder.Default
    private String description = BytedeskConsts.EMPTY_STRING;

    private String providerUid;

    @Builder.Default
    private String level = LevelEnum.PLATFORM.name();

}
