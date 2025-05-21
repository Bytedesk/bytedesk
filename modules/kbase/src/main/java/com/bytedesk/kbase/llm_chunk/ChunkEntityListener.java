/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 15:39:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_chunk.event.ChunkCreateEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkDeleteEvent;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChunkEntityListener {

    @PostPersist
    public void onPostPersist(ChunkEntity chunk) {
        log.info("ChunkEntityListener onPostPersist: {}", chunk.getName());
        ChunkEntity clonedChunk = SerializationUtils.clone(chunk);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new ChunkCreateEvent(clonedChunk));
    }

    @PostUpdate
    public void onPostUpdate(ChunkEntity chunk) {
        log.info("ChunkEntityListener onPostUpdate: {}", chunk.getName());
        ChunkEntity clonedChunk = SerializationUtils.clone(chunk);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (chunk.getDeleted()) {
            log.info("ChunkEntityListener onPostUpdate: Chunk is deleted");
            publisher.publishEvent(new ChunkDeleteEvent(clonedChunk));
        }else {
            log.info("ChunkEntityListener onPostUpdate: Chunk is update");
            publisher.publishEvent(new ChunkUpdateEvent(clonedChunk));
        }   
    }
}
