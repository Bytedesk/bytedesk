/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-30 09:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 15:38:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmProviderConfigDefault implements Serializable {
    
    // Chat 配置
    private String defaultChatProvider;
    private String defaultChatModel;
    
    // Embedding 配置
    private String defaultEmbeddingProvider;
    private String defaultEmbeddingModel;
    
    // Vision 配置
    private String defaultVisionProvider;
    private String defaultVisionModel;
    
    // Voice 配置
    private String defaultVoiceProvider;
    private String defaultVoiceModel;
    
    // Rerank 配置
    private String defaultRerankProvider;
    private String defaultRerankModel;
} 