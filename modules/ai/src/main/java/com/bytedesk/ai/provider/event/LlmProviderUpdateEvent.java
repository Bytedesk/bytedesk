/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:18:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 22:21:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.ai.provider.LlmProviderEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LlmProviderUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final LlmProviderEntity lmProvider;

    public LlmProviderUpdateEvent(Object source, LlmProviderEntity lmProvider) {
        super(source);
        this.lmProvider = lmProvider;
    }
    
}
