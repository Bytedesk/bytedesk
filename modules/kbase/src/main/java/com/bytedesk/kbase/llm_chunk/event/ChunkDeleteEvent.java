/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:34:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.kbase.llm_chunk.ChunkEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChunkDeleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private ChunkEntity chunk;

    public ChunkDeleteEvent(ChunkEntity chunk) {
        super(chunk);
        this.chunk = chunk;
    }
}
