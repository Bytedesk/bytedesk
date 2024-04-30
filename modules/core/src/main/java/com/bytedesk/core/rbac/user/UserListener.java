/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 09:00:16
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

import org.springframework.stereotype.Component;

import com.bytedesk.core.topic.TopicService;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserListener {

    // 无法注入bean，否则报错
    // private static TopicService topicService;

    @PostPersist
    public void postPersist(User user) {
        log.info("user postPersist {}", user.getUid());
        // 这里可以记录日志、发送通知等
        // create user topic
        TopicService topicService = ApplicationContextHolder.getBean(TopicService.class);
        // 
        topicService.create(user.getUid(), user.getUid());
    }

    // @PreUpdate
    // public void preUpdate(User user) {
    //     log.info("preUpdate {}", user.getUid());
    // }

    // @PostUpdate
    // public void postUpdate(User user) {
    //     log.info("postUpdate {}", user.getUid());
    // }

    // @PreRemove
    // public void preRemove(User user) {
    //     log.info("preRemove {}", user.getUid());
    // }

    // @PostRemove
    // public void postRemove(User user) {
    //     log.info("postRemove {}", user.getUid());
    //     // topicService.deleteByTopicAndUid(user.getUid(), user.getUid());
    // }

}
