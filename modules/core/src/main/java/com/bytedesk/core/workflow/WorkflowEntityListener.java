/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 11:53:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.workflow.event.WorkflowCreateEvent;
import com.bytedesk.core.workflow.event.WorkflowUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WorkflowEntityListener {

    @PostPersist
    public void onPostPersist(WorkflowEntity workflow) {
        log.info("onPostPersist: {}", workflow);
        WorkflowEntity cloneWorkflow = SerializationUtils.clone(workflow);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new WorkflowCreateEvent(cloneWorkflow));
    }

    @PostUpdate
    public void onPostUpdate(WorkflowEntity workflow) {
        log.info("onPostUpdate: {}", workflow);
        WorkflowEntity cloneWorkflow = SerializationUtils.clone(workflow);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new WorkflowUpdateEvent(cloneWorkflow));
    }
    
}
