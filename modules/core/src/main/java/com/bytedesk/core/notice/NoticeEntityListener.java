/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-15 09:31:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 16:19:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.notice.event.NoticeCreateEvent;
import com.bytedesk.core.notice.event.NoticeDeleteEvent;
import com.bytedesk.core.notice.event.NoticeUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NoticeEntityListener {

    @PostPersist
    public void onPostPersist(NoticeEntity noticeEntity) {
        log.info("NoticeEntityListener onPostPersist");
        NoticeEntity clonedNoticeEntity = SerializationUtils.clone(noticeEntity);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new NoticeCreateEvent(this, clonedNoticeEntity));
    }

    @PostUpdate
    public void onPostUpdate(NoticeEntity noticeEntity) {
        log.info("NoticeEntityListener onPostUpdate");
        NoticeEntity clonedNoticeEntity = SerializationUtils.clone(noticeEntity);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (clonedNoticeEntity.isDeleted()) {
            bytedeskEventPublisher.publishEvent(new NoticeDeleteEvent(this, clonedNoticeEntity));
        } else {
            bytedeskEventPublisher.publishEvent(new NoticeUpdateEvent(this, clonedNoticeEntity));
        }
    }
    
    
}
