/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-16 09:10:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRestService topicRestService;

    public void create(String topic, String userUid) {
        TopicRequest request = TopicRequest.builder()
                .topic(topic)
                .userUid(userUid)
                .build();
        create(request);
    }

    // 创建topic
    @Transactional
    public void create(TopicRequest request) {
        topicRestService.create(request);
    }

    // 删除topic
    // @Transactional
    // public void remove(TopicRequest topicRequest) {
    //     topicRestService.remove(topicRequest);
    // }

    // 删除topic
    @Transactional
    public void remove(String topic, String userUid) {
        topicRestService.remove(topic, userUid);
    }

    // 订阅topic
    @Transactional
    public void subscribe(String topic, String clientId) {
        topicRestService.subscribe(topic, clientId);
    }

    // 取消订阅topic
    @Transactional
    public void unsubscribe(String topic, String clientId) {
        topicRestService.unsubscribe(topic, clientId);
    }

    @Transactional
    public void addClientId(String clientId) {
        topicRestService.addClientId(clientId);
    }

    @Transactional
    public void removeClientId(String clientId) {
        topicRestService.removeClientId(clientId);
    }

    // @Cacheable(value = "topic", key = "#uid")
    // public Optional<TopicEntity> findByUid(String uid) {
    //     return topicRestService.findByUid(uid);
    // }

    // @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    // public Optional<TopicEntity> findByClientId(String clientId) {
    //     // 用户clientId格式: userUid/client/deviceUid
    //     final String userUid = clientId.split("/")[0];
    //     return findByUserUid(userUid);
    // }

    // @Cacheable(value = "topic", key = "#uid", unless = "#result == null")
    // public Optional<TopicEntity> findByUserUid(String uid) {
    //     return topicRestService.findByUid(uid);
    // }

    // @Cacheable(value = "topic", key = "#topic", unless="#result == null")
    // public Set<TopicEntity> findByTopic(String topic) {
    //     return topicRestService.findByTopic(topic);
    // }

    // public void update(String uid, String userUid) {
    //     Optional<TopicEntity> optionalTopic = findByUid(uid);
    //     optionalTopic.ifPresent(topic -> {
    //         topic.setUserUid(userUid);
    //         topicRestService.save(topic);
    //     });
    // }

    // @CacheEvict(value = "topic", key = "#topic.userUid")
    // public void delete(TopicEntity topic) {
    //     topicRestService.deleteByUid(topic.getUid());
    // }

    // public TopicResponse convertToTopicResponse(TopicEntity topic) {
    //     return topicRestService.convertToResponse(topic);
    // }

}
