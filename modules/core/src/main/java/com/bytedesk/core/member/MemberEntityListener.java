/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:46:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 10:01:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;


// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.member.event.MemberCreateEvent;
import com.bytedesk.core.member.event.MemberUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

// @Async
@Slf4j
@Component
public class MemberEntityListener {

    @PostPersist
    public void postPersist(MemberEntity member) {
        MemberEntity clonedMember = SerializationUtils.clone(member);
        // UserEntity user = clonedMember.getUser();
        log.info("postPersist member {}", clonedMember.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MemberCreateEvent(this, clonedMember));
    }
    
    @PostUpdate
    public void postUpdate(MemberEntity member) {
        MemberEntity clonedMember = SerializationUtils.clone(member);
        // UserEntity user = clonedMember.getUser();
        log.info("postUpdate member {}", clonedMember.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MemberUpdateEvent(this, clonedMember));
    }


}
