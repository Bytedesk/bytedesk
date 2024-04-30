/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 09:30:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 08:39:50
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

import com.bytedesk.core.topic.TopicService;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AgentListener {

    // 无法注入bean，否则报错
    // private final TopicService topicService;
    
    // @PrePersist
    // public void prePersist(Agent agent) {
    //     log.info("prePersist {}", agent.getUid());
    // }

    @PostPersist
    public void postPersist(Agent agent) {
        log.info("postPersist {}", agent.getUid());
        // topicService.create(agent.getUid(), agent.getUser().getUid());
        // 这里可以记录日志、发送通知等
        // create agent topic
        TopicService topicService = ApplicationContextHolder.getBean(TopicService.class);
        // 
        topicService.create(agent.getUid(), agent.getUser().getUid());
    }

    // @PreUpdate
    // public void preUpdate(Agent agent) {
    //     log.info("preUpdate {}", agent.getUid());
    // }

    // @PostUpdate
    // public void postUpdate(Agent agent) {
    //     log.info("postUpdate {}", agent.getUid());
    //     // TODO: 切换agent对应的user时，需要更新topic：从原user缓存中删除，然后添加到新user缓存中
    //     // topicService.update(agent.getUid(), agent.getUser().getUid());
    // }

    // @PreRemove
    // public void preRemove(Agent agent) {
    //     log.info("preRemove {}", agent.getUid());
    // }

    // @PostRemove
    // public void postRemove(Agent agent) {
    //     log.info("postRemove {}", agent.getUid());
    //     // topicService.deleteByTopicAndUid(agent.getUid(), agent.getUser().getUid());
    // }
    
}
