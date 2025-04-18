/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:46:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:00:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;


// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.team.member.event.MemberCreateEvent;
import com.bytedesk.team.member.event.MemberUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

// @Async
@Slf4j
@Component
public class MemberEntityListener {

    // @PrePersist
    // public void prePersist(Member member) {
    //     log.info("prePersist {}", member.getUid());
    // }

    @PostPersist
    public void postPersist(MemberEntity member) {
        MemberEntity clonedMember = SerializationUtils.clone(member);
        UserEntity user = clonedMember.getUser();
        log.info("postPersist member {}, user {}", clonedMember.getUid(), user.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MemberCreateEvent(this, clonedMember));
        // 默认订阅成员主题
        // bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getMemberTopic(clonedMember.getUid()), user.getUid());
        // // 
        // Iterator<Department> iterator = clonedMember.getDepartments().iterator();
        // while (iterator.hasNext()) {
        //     Department department = iterator.next();
        //     // 订阅部门主题
        //     bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getDepartmentTopic(department
        //             .getUid()),
        //             user.getUid());
        // }
    }

    // @PreUpdate
    // public void preUpdate(Member member) {
    //     log.info("preUpdate {}", member.getUid());
    // }

    @PostUpdate
    public void postUpdate(MemberEntity member) {
        // log.info("postUpdate member {}", member.getUid());
        MemberEntity clonedMember = SerializationUtils.clone(member);
        UserEntity user = clonedMember.getUser();
        log.info("postUpdate member {}, user {}", clonedMember.getUid(), user.getUid());
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MemberUpdateEvent(this, clonedMember));

        // TODO: 删除旧的部门主题
        // BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        // Iterator<Department> iterator = clonedMember.getDepartments().iterator();
        // while (iterator.hasNext()) {
        //     Department department = iterator.next();
        //     // 订阅部门主题
        //     bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getDepartmentTopic(department
        //             .getUid()),
        //             user.getUid());
        // }
    }

    // @PreRemove
    // public void preRemove(Member member) {
    //     log.info("preRemove {}", member.getUid());
    // }

    // @PostRemove
    // public void postRemove(Member member) {
    //     log.info("postRemove {}", member.getUid());
    // }
    
}
