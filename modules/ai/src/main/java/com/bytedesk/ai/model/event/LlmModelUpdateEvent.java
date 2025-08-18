/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:23:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 22:23:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.ai.model.LlmModelEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LlmModelUpdateEvent extends ApplicationEvent {

    private final static long serialVersionUID = 1L;
    
    private final LlmModelEntity llmModelEntity;
    
    public LlmModelUpdateEvent(Object source, LlmModelEntity llmModelEntity) {
        super(source);
        this.llmModelEntity = llmModelEntity;
    }
    
}
