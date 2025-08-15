/*
 * @Author: import java.util.HashSet;
 * @Date: 2024-05-29 15:11:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 17:49:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.event.UserLogoutEvent;
import com.bytedesk.core.socket.mqtt.MqttConnectionService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.topic.event.TopicCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TopicEventListener {

    private final TopicService topicService;

    private final TopicCacheService topicCacheService;

    private final MqttConnectionService mqttConnectionService;

    private final ThreadRestService threadRestService;

    @EventListener
    public void onTopicCreateEvent(TopicCreateEvent event) {
        log.info("topic onTopicCreateEvent: {}", event);
        //
        TopicRequest request = TopicRequest.builder()
                .topic(event.getTopic())
                .userUid(event.getUserUid())
                .build();
        topicCacheService.pushRequest(request);
    }

    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // 定时刷新缓存中的topic事件到数据库中
        List<String> topicRequestList = topicCacheService.getTopicRequestList();
        if (topicRequestList != null) {
            topicRequestList.forEach(item -> {
                // log.info("topic onQuartzFiveSecondEvent {}", item);
                TopicRequest topicRequest = JSON.parseObject(item, TopicRequest.class);
                topicService.create(topicRequest);
            });
        }
        List<String> clientIdList = topicCacheService.getClientIdList();
        if (clientIdList!= null) {
            clientIdList.forEach(item -> {
                // log.info("topic onQuartzFiveSecondEvent {}", item);
                topicService.addClientId(item);
            });
        }
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        Set<String> clientIds = mqttConnectionService.getConnectedClientIds();
        // log.info("topic QuartzOneMinEvent {}", clientIds);
        // current connected clientIds
        if (clientIds != null) {
            // 不再直接处理每个clientId，而是将它们添加到缓存中，由五秒定时器统一处理
            for (String clientId : clientIds) {
                // 用户clientId格式: userUid/client/deviceUid
                // log.info("topic onQuartzOneMinEvent connected clientId: {}", clientId);
                // 将clientId添加到缓存中，而不是直接调用topicService.addClientId
                topicCacheService.pushClientId(clientId);
            }
            
            // TODO: 可以在这里添加清理逻辑，删除不在当前连接列表中的clientId
            // 这样可以确保只有活跃连接被保留在topic中
        }
    }

    @EventListener
    public void onUserLogoutEvent(UserLogoutEvent event) {
        // UserLogoutEvent userLogoutEvent = event.getObject();
        UserEntity user = event.getUser();
        log.info("topic onUserLogoutEvent: {}", user.getUsername());
        // TODO: user logout event, remove user from topic
    }

    @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        log.info("topic onQuartzDay0Event: 开始清理已结束的会话topics");
        
        // 获取所有的 TopicEntity
        List<TopicEntity> allTopics = topicService.findAll();
        
        for (TopicEntity topicEntity : allTopics) {
            Set<String> topics = topicEntity.getTopics();
            Set<String> topicsToRemove = new HashSet<>();
            
            // 遍历每个 topic，检查是否可以被移除
            for (String topic : topics) {
                // 1.检查是否是一对一AGENT会话或工作组WORKGROUP会话
                if (topic.startsWith(TopicUtils.TOPIC_ORG_AGENT_PREFIX) || 
                    topic.startsWith(TopicUtils.TOPIC_ORG_WORKGROUP_PREFIX)) {
                    
                    // 2.根据topic查询所有相关的threadEntity
                    List<ThreadEntity> relatedThreads = threadRestService.findListByTopic(topic);
                    
                    // 3.检查所有关联的会话是否都已关闭
                    boolean allClosed = true;
                    for (ThreadEntity thread : relatedThreads) {
                        if (!ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())) {
                            allClosed = false;
                            break;
                        }
                    }
                    
                    // 4.如果所有关联会话都已关闭，则将该topic添加到待移除列表
                    if (allClosed && !relatedThreads.isEmpty()) {
                        topicsToRemove.add(topic);
                        log.info("标记待删除topic: {} 从 userUid: {}", topic, topicEntity.getUserUid());
                    }
                }
            }
            
            // 从topics集合中移除符合条件的topic
            for (String topicToRemove : topicsToRemove) {
                topicService.remove(topicToRemove, topicEntity.getUserUid());
                log.info("成功删除topic: {} 从 userUid: {}", topicToRemove, topicEntity.getUserUid());
            }
        }
        
        log.info("topic onQuartzDay0Event: 已完成清理已结束的会话topics");
    }

}
