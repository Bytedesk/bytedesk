/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 15:51:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 15:53:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.taboo.event.TabooCreateEvent;
import com.bytedesk.kbase.taboo.event.TabooUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TabooEntityListener {

    @PostPersist
    public void onTabooCreate(TabooCreateEvent event) {
        TabooEntity taboo = event.getTaboo();
        log.info("TabooEntityListener onTabooCreate: {}", taboo.toString());
        // 
        TabooEntity clonedTaboo = SerializationUtils.clone(taboo);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new TabooCreateEvent(clonedTaboo));
    }
    
    @PostUpdate
    public void onTabooUpdate(TabooUpdateEvent event) {
        TabooEntity taboo = event.getTaboo();
        log.info("TabooEntityListener onTabooUpdate: {}", taboo.toString());
        // 
        TabooEntity clonedTaboo = SerializationUtils.clone(taboo);
        // 
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new TabooUpdateEvent(clonedTaboo));
    }
    
}
