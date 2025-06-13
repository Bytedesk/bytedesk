/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 15:11:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 18:35:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.List;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.event.UserLogoutEvent;
import com.bytedesk.core.socket.mqtt.MqttConnectionService;
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

    // 此处使用static，否则在定时器中无法读取初始化时期的数据
    // private final static TopicCacheService topicCacheService = new TopicCacheService();
    // private final static String cacheKey = "topicList";

    @EventListener
    public void onTopicCreateEvent(TopicCreateEvent event) {
        log.info("topic onTopicCreateEvent: {}", event);
        // 注意：来自member和department的创建事件，大量事件会导致topicService.create()方法被调用多次，
        // 导致乐观锁冲突。所以，将事件缓存起来，然后定时刷新到数据库中
        // topicService.create(event.getTopic(), event.getUserUid());
        //
        TopicRequest request = TopicRequest.builder()
                .topic(event.getTopic())
                .userUid(event.getUserUid())
                .build();
        // request.getTopics().add(event.getTopic());
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

}
