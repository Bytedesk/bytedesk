/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 15:11:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 12:34:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.cache.CaffeineCacheService;
import com.bytedesk.core.quartz.QuartzFiveSecondEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserCreateEvent;
import com.bytedesk.core.rbac.user.UserUpdateEvent;
import com.bytedesk.core.socket.mqtt.event.MqttConnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttDisconnectedEvent;
import com.bytedesk.core.socket.mqtt.event.MqttSubscribeEvent;
import com.bytedesk.core.socket.mqtt.event.MqttUnsubscribeEvent;
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadUpdateEvent;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TopicEventListener {

    private final TopicService topicService;

    // 此处使用static，否则在定时器中无法读取初始化时期的数据
    private final static CaffeineCacheService caffeineCacheService = new CaffeineCacheService();

    private final static String cacheKey = "topicList";

    @EventListener
    public void onTopicCreateEvent(TopicCreateEvent event) {
        log.info("topic onTopicCreateEvent: {}", event);
        // 注意：来自member和department的创建事件，大量事件会导致topicService.create()方法被调用多次，
        // 导致乐观锁冲突。所以，将事件缓存起来，然后定时刷新到数据库中
        // topicService.create(event.getTopic(), event.getUserUid());
        // 
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(event.getTopic())
                .userUid(event.getUserUid())
                .build();
        caffeineCacheService.push(cacheKey, JSON.toJSONString(topicRequest));
    }

    @EventListener
    public void onTopicUpdateEvent(TopicUpdateEvent event) {
        log.info("topic onTopicUpdateEvent: {}", event);
        // topicService.update(event.getTopic(), event.getUserUid());
    }

    @EventListener
    public void onUserCreateEvent(UserCreateEvent event) {
        User user = event.getUser();
        log.info("topic onUserCreateEvent: {}", user.getUid());
        // 默认订阅用户主题
        // topicService.create(TopicUtils.getUserTopic(user.getUid()), user.getUid());
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(TopicUtils.getUserTopic(user.getUid()))
                .userUid(user.getUid())
                .build();
        caffeineCacheService.push(cacheKey, JSON.toJSONString(topicRequest));
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            // topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            caffeineCacheService.push(cacheKey, JSON.toJSONString(topicRequestOrg));
        }
    }

    @EventListener
    public void onUserUpdateEvent(UserUpdateEvent event) {
        User user = event.getUser();
        log.info("topic onUserUpdateEvent: {}", user.getUid());
        // 默认订阅组织主题
        if (StringUtils.hasText(user.getOrgUid())) {
            // topicService.create(TopicUtils.getOrgTopic(user.getOrgUid()), user.getUid());
            TopicRequest topicRequestOrg = TopicRequest.builder()
                    .topic(TopicUtils.getOrgTopic(user.getOrgUid()))
                    .userUid(user.getUid())
                    .build();
            caffeineCacheService.push(cacheKey, JSON.toJSONString(topicRequestOrg));
        }
    }

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        log.info("topic onThreadCreateEvent: {}", thread.getUid());
        // // 机器人会话不需要订阅topic
        // if (event.getThread().getType().equals(ThreadTypeEnum.ROBOT)) {
        //     return;
        // }
        if (thread != null && thread.getOwner() != null) {
            topicService.create(thread.getTopic(), thread.getOwner().getUid());
        }
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        Thread thread = event.getThread();
        log.info("topic onThreadUpdateEvent: {}", thread.getUid());
        // 
        if (thread != null && thread.getOwner() != null) {
            topicService.create(thread.getTopic(), thread.getOwner().getUid());
        }
    }

    @EventListener
    public void onMqttConnectedEvent(MqttConnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttConnectedEvent uid {}, clientId {}", uid, clientId);
        //
        topicService.addClientId(clientId);
    }

    @EventListener
    public void onMqttDisconnectedEvent(MqttDisconnectedEvent event) {
        String clientId = event.getClientId();
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        log.info("topic onMqttDisconnectedEvent uid {}, clientId {}", uid, clientId);
        //
        topicService.removeClientId(clientId);
    }

    @EventListener
    public void onMqttSubscribeEvent(MqttSubscribeEvent event) {
        log.info("topic onMqttSubscribeEvent {}", event);
        // 
        topicService.subscribe(event.getTopic(), event.getClientId());
    }

    @EventListener
    public void onMqttUnsubscribeEvent(MqttUnsubscribeEvent event) {
        log.info("topic onMqttUnsubscribeEvent {}", event);
        // 
        topicService.unsubscribe(event.getTopic(), event.getClientId());
    }


    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // 定时刷新缓存中的topic事件到数据库中
        List<String> list = caffeineCacheService.getList(cacheKey);
        if (list != null) {
            log.info("topic onQuartzFiveSecondEvent {}", event);
            list.forEach(item -> {
                TopicRequest topicRequest = JSON.parseObject(item, TopicRequest.class);
                topicService.create(topicRequest);
            });
        }
    }
    
}
