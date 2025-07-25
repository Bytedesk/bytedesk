/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:44:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 14:19:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;

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

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ LlmProviderEntityListener.class })
@Table(name = "bytedesk_ai_provider")
public class LlmProviderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    
    private String nickname;

    // https://cdn.weiyuai.cn/assets/images/llm/model/baichuan.png
    private String logo;
    // 
    @Builder.Default
    private String description = BytedeskConsts.EMPTY_STRING;
    // 
    private String apiUrl;
    private String apiKey;
    // 
    private String webUrl;
    // 
    @Builder.Default
    private String status = LlmProviderStatusEnum.DEVELOPMENT.name();
    
}