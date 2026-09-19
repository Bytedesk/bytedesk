/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-20 12:26:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.utils.BdPinyinUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostPersist;
// import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.PostUpdate;

// @Async
// @Slf4j
@Component
public class UserEntityListener {

    // 无法注入bean，否则报错
    // private static TopicService topicService;

    @PrePersist
    public void prePersist(UserEntity user) {
        populateNicknamePinyin(user);
    }

    @PreUpdate
    public void preUpdate(UserEntity user) {
        populateNicknamePinyin(user);
    }

    @PostPersist
    public void postPersist(UserEntity user) {
        // 这里可以记录日志、发送通知等
        UserEntity clonedUser = SerializationUtils.clone(user);
        // log.info("user postPersist {}", user.getUid());
        // 
        // NOTE: 用户创建发生在事务内；BytedeskEventPublisher 本身是 @Async，事件一旦发布，
        // 异步监听器（如 AssistantEventListener/PublicAccountEventListener，REQUIRES_NEW）
        // 可能在用户记录提交前就插入引用该用户的外键数据（bytedesk_core_thread.owner_id），
        // 导致外键约束违反。因此这里注册 afterCommit 回调，在提交后再发布事件。
        // 与 OrganizationEntityListener/TicketEntityListener 同一模式。
        publishAfterCommit(() -> {
            BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
            bytedeskEventPublisher.publishUserCreateEvent(clonedUser);
        });
    }

    // @PreUpdate
    // public void preUpdate(User user) {
    // log.info("preUpdate {}", user.getUid());
    // }

    @PostUpdate
    public void postUpdate(UserEntity user) {
        // create user topic
        UserEntity clonedUser = SerializationUtils.clone(user);
        // log.info("postUpdate {}", user.getUid());
        //
        publishAfterCommit(() -> {
            BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
            bytedeskEventPublisher.publishUserUpdateEvent(clonedUser);
        });
    }

    private void publishAfterCommit(Runnable publish) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
        } else {
            // 无事务上下文时保持原有行为，直接发布
            publish.run();
        }
    }

    // @PreRemove
    // public void preRemove(User user) {
    // log.info("preRemove {}", user.getUid());
    // }

    // @PostRemove
    // public void postRemove(User user) {
    // log.info("postRemove {}", user.getUid());
    // // topicService.deleteByTopicAndUid(user.getUid(), user.getUid());
    // }

    private void populateNicknamePinyin(UserEntity user) {
        if (StringUtils.hasText(user.getNickname())) {
            user.setNicknamePinyin(BdPinyinUtils.toPinYin(user.getNickname()).replace(" ", ""));
        }
    }

}
