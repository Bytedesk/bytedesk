/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 12:46:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AgentEntityListener {

    // 无法注入bean，否则报错
    // private final TopicService topicService;

    // @PrePersist
    // public void prePersist(Agent agent) {
    // log.info("prePersist {}", agent.getUid());
    // }

    @PostPersist
    public void postPersist(Agent agent) {
        // topicService.create(agent.getUid(), agent.getUser().getUid());
        Agent cloneAgent = SerializationUtils.clone(agent);
        User user = cloneAgent.getMember().getUser();
        log.info("agent postPersist {} user {}", cloneAgent.getUid(), user.getUid());
        // 这里可以记录日志、发送通知等
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        // 默认订阅客服主题
        bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getOrgAgentTopic(cloneAgent.getUid()), user.getUid());
    }

    // @PreUpdate
    // public void preUpdate(Agent agent) {
    // log.info("preUpdate {}", agent.getUid());
    // }

    // @PostUpdate
    // public void postUpdate(Agent agent) {
    // log.info("postUpdate {}", agent.getUid());
    // // TODO: 切换agent对应的user时，需要更新topic：从原user缓存中删除，然后添加到新user缓存中
    // // topicService.update(agent.getUid(), agent.getUser().getUid());
    // }

    // @PreRemove
    // public void preRemove(Agent agent) {
    // log.info("preRemove {}", agent.getUid());
    // }

    // @PostRemove
    // public void postRemove(Agent agent) {
    // log.info("postRemove {}", agent.getUid());
    // // topicService.deleteByTopicAndUid(agent.getUid(),
    // agent.getUser().getUid());
    // }

}
