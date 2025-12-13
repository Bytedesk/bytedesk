/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 22:58:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

// import java.util.Objects;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.agent.event.AgentCreateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AgentEntityListener {


    @PostPersist
    public void postPersist(AgentEntity agent) {
        // topicService.create(agent.getUid(), agent.getUser().getUid());
        UserEntity user = agent.getMember().getUser();
        log.info("agent postPersist {} user {}", agent.getUid(), user.getUid());
        // 这里可以记录日志、发送通知等
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        // 默认订阅客服主题
        bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getOrgAgentTopic(agent.getUid()), user.getUid());
        bytedeskEventPublisher.publishEvent(new AgentCreateEvent(this, agent));
    }

    @PostUpdate
    public void postUpdate(AgentEntity agent) {
        log.info("postUpdate {}", agent.getUid());
    }

}
