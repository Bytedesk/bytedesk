/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-18 15:07:51
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
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.GenericApplicationEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.agent.event.AgentCreateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
// import jakarta.persistence.PreUpdate;
// import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AgentEntityListener {

    /**
     * @Transient 注解用于确保oldAgent字段不会被JPA持久化到数据库中。同时，使用transient关键字来防止该字段在序列化时被包含。
     * 缺点是它增加了内存消耗，因为你需要为每个更新的实体保存一个旧状态的副本。如果你的应用处理大量的更新操作，并且实体对象很大，
     * 这可能会成为一个问题。在这种情况下，你可能需要考虑使用更高效的审计解决方案
     */
    // @Transient
    // private transient Agent oldAgent;
    
    @PostPersist
    public void postPersist(AgentEntity agent) {
        // topicService.create(agent.getUid(), agent.getUser().getUid());
        AgentEntity cloneAgent = SerializationUtils.clone(agent);
        UserEntity user = cloneAgent.getMember().getUser();
        log.info("agent postPersist {} user {}", cloneAgent.getUid(), user.getUid());
        // 这里可以记录日志、发送通知等
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        // 默认订阅客服主题
        bytedeskEventPublisher.publishTopicCreateEvent(TopicUtils.getOrgAgentTopic(cloneAgent.getUid()), user.getUid());
        //
        bytedeskEventPublisher.publishGenericApplicationEvent(
                new GenericApplicationEvent<AgentCreateEvent>(this, new AgentCreateEvent(this, cloneAgent)));
    }

    // @PreUpdate
    // public void preUpdate(Agent agent) {
    //     log.info("preUpdate {}", agent.getUid());
    //     oldAgent = SerializationUtils.clone(agent); // 保存更新前的状态
    // }

    @PostUpdate
    public void postUpdate(AgentEntity agent) {
        log.info("postUpdate {}", agent.getUid());
        // if (oldAgent != null) {
        //     // 现在你可以比较oldAgent和agent来识别哪些字段发生了变化
        //     // status before from OFFLINE to OFFLINE
        //     log.info("status before from {} to {}", oldAgent.getStatus(), agent.getStatus());
        //     // 例如:
        //     if (!Objects.equals(oldAgent.getStatus(), agent.getStatus())) {
        //         // someField字段发生了变化
        //         log.info("status changed from {} to {}", oldAgent.getStatus(), agent.getStatus());
        //     }
        //     // ...处理其他字段的变化
        // }
        // TODO: 切换agent对应的user时，需要更新topic：从原user缓存中删除，然后添加到新user缓存中
        // topicService.update(agent.getUid(), agent.getUser().getUid());
    }

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
