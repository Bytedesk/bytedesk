/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-04 10:21:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:03:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.user.event.UserCreateEvent;
import com.bytedesk.core.rbac.user.event.UserUpdateEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UserEventListener {

    private final TopicCacheService topicCacheService;

    @EventListener
    public void onUserCreateEvent(UserCreateEvent event) {
        UserEntity user = event.getUser();
        log.info("topic onUserCreateEvent: {}", user.getUid());
        // 默认订阅用户主题
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(TopicUtils.getUserTopic(user.getUid()))
                .userUid(user.getUid())
                .build();
        topicCacheService.pushRequest(topicRequest);
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(topicRequestOrg);
        }
    }

    @EventListener
    public void onUserUpdateEvent(UserUpdateEvent event) {
        UserEntity user = event.getUser();
        log.info("topic onUserUpdateEvent: {}", user.getUid());
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(topicRequestOrg);
        }
    }

}
