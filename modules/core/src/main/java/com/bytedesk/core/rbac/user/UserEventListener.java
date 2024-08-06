/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-04 10:21:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-04 11:04:43
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

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
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
        User user = event.getUser();
        log.info("topic onUserCreateEvent: {}", user.getUid());
        // 默认订阅用户主题
        // topicService.create(TopicUtils.getUserTopic(user.getUid()), user.getUid());
        // 
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(TopicUtils.getUserTopic(user.getUid()))
                .userUid(user.getUid())
                .build();
        topicCacheService.push(JSON.toJSONString(topicRequest));
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            // topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
            // 
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            topicCacheService.push(JSON.toJSONString(topicRequestOrg));
        }
    }

    @EventListener
    public void onUserUpdateEvent(UserUpdateEvent event) {
        User user = event.getUser();
        log.info("topic onUserUpdateEvent: {}", user.getUid());
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            // topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
            // 
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            topicCacheService.push(JSON.toJSONString(topicRequestOrg));
        }
    }


}
