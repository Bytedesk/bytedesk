/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-14 09:04:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-18 14:59:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OllamaNotAvailableCondition implements Condition {

    private final OllamaAvailableCondition ollamaAvailableCondition = new OllamaAvailableCondition();
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !ollamaAvailableCondition.matches(context, metadata);
    }
    
}
