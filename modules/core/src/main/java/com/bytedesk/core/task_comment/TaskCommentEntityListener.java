/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 17:00:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_comment;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.task_comment.event.TaskCommentCreateEvent;
import com.bytedesk.core.task_comment.event.TaskCommentUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskCommentEntityListener {

    @PostPersist
    public void onPostPersist(TaskCommentEntity task_comment) {
        log.info("onPostPersist: {}", task_comment);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TaskCommentCreateEvent(task_comment));
    }

    @PostUpdate
    public void onPostUpdate(TaskCommentEntity task_comment) {
        log.info("onPostUpdate: {}", task_comment);
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new TaskCommentUpdateEvent(task_comment));
    }
    
}
