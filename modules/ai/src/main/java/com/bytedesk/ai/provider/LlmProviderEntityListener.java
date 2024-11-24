/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:05:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 11:59:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LlmProviderEntityListener {

    @PostPersist
    public void postPersist(LlmProviderEntity entity) {
        LlmProviderEntity clonedEntity = SerializationUtils.clone(entity);
        // log.info("LlmProviderEntityListener postPersist: {}", clonedEntity.getName());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishGenericApplicationEvent(
                new GenericApplicationEvent<LlmProviderCreateEvent>(this, new LlmProviderCreateEvent(this, clonedEntity)));
    }

    @PostUpdate
    public void postUpdate(LlmProviderEntity entity) {
        LlmProviderEntity clonedEntity = SerializationUtils.clone(entity);
        // log.info("LlmProviderEntityListener postUpdate: {}", clonedEntity.getName());
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishGenericApplicationEvent(
                new GenericApplicationEvent<LlmProviderUpdateEvent>(this, new LlmProviderUpdateEvent(this, clonedEntity)));
    }
    
}
