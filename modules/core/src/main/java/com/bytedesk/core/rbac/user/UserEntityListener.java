/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 11:22:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import jakarta.persistence.PostPersist;
// import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.PostUpdate;

// @Async
// @Slf4j
@Component
public class UserEntityListener {

    // 无法注入bean，否则报错
    // private static TopicService topicService;

    @PostPersist
    public void postPersist(User user) {
        // log.info("user postPersist {}", user.getUid());
        // 这里可以记录日志、发送通知等
        // create user topic
        User clonedUser = SerializationUtils.clone(user);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishUserCreateEvent(clonedUser);

        // TopicService topicService = ApplicationContextHolder.getBean(TopicService.class);
        // // 默认订阅用户主题
        // topicService.create(TopicUtils.getUserTopic(user.getUid()), user.getUid());
        // // 默认订阅组织主题
        // if (StringUtils.hasText(user.getOrgUid())) {
        //     topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
        // }
        //
        // // 每创建一个用户，自动给此用户生成一条文件助理的会话
        // ThreadService threadService =
        // ApplicationContextHolder.getBean(ThreadService.class);
        // threadService.createFileAsistantThread(user);
        // // 每创建一个用户，自动给此用户生成一条系统通知的会话
        // threadService.createSystemNotificationChannelThread(user);
    }

    // @PreUpdate
    // public void preUpdate(User user) {
    // log.info("preUpdate {}", user.getUid());
    // }

    @PostUpdate
    public void postUpdate(User user) {
        // log.info("postUpdate {}", user.getUid());
        // create user topic
        User clonedUser = SerializationUtils.clone(user);
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishUserUpdateEvent(clonedUser);
        // 
        // TopicService topicService = ApplicationContextHolder.getBean(TopicService.class);
        // // 默认订阅组织主题
        // if (StringUtils.hasText(user.getOrgUid())) {
        //     topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
        // }
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

}
