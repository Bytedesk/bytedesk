/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:57:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 15:29:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.llm_faq.event.FaqCreateEvent;
import com.bytedesk.kbase.llm_faq.event.FaqDeleteEvent;
import com.bytedesk.kbase.llm_faq.event.FaqUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j  
@Component
public class FaqEntityListener {

    @PostPersist
    public void onPostPersist(FaqEntity faq) {
        // log.info("FaqEntityListener onPostPersist: {}", faq.getUid());
        FaqEntity clonedFaq = SerializationUtils.clone(faq);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new FaqCreateEvent(clonedFaq));
    }

    @PostUpdate
    public void onPostUpdate(FaqEntity faq) {
        // log.info("FaqEntityListener onPostUpdate: {}", faq.getUid());
        FaqEntity clonedFaq = SerializationUtils.clone(faq);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        
        // 只有在实体被标记为删除且没有向量处理正在进行时才发送删除事件
        if (faq.isDeleted()) {
            log.info("FaqEntityListener FaqDeleteEvent: {}, 向量状态: {}",  faq.getQuestion(), faq.getVectorStatus());
            publisher.publishEvent(new FaqDeleteEvent(clonedFaq));
        } else {
            // 如果是状态更新而不是删除操作
            publisher.publishEvent(new FaqUpdateEvent(clonedFaq));
        }
    }
    
}
