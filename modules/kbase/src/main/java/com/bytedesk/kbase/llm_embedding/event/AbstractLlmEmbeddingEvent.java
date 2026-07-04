/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-28 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-28 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_embedding.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.kbase.llm_embedding.LlmEmbeddingEntity;

/**
 * Ensures every llm_embedding-related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractLlmEmbeddingEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final LlmEmbeddingEntity llm_embedding;

    protected AbstractLlmEmbeddingEvent(Object source, LlmEmbeddingEntity llm_embedding) {
        super(source);
        this.llm_embedding = snapshot(llm_embedding);
    }

    public LlmEmbeddingEntity getLlmEmbedding() {
        return llm_embedding;
    }

    private LlmEmbeddingEntity snapshot(LlmEmbeddingEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot llm_embedding " + source.getUid(), ex);
        }
    }
}
