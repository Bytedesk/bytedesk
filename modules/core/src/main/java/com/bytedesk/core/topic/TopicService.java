/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 18:28:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRestService topicRestService;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    public void create(String topic, String userUid) {
        TopicRequest request = TopicRequest.builder()
                .topic(topic)
                .userUid(userUid)
                .build();
        create(request);
    }

    // 创建topic
    @Transactional
    public void create(TopicRequest topicRequest) {
        Optional<TopicEntity> topicOptional = findByUserUid(topicRequest.getUserUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicEntity = topicOptional.get();
            topicEntity.getTopics().add(topicRequest.getTopic());
            topicEntity.getTopics().addAll(topicRequest.getTopics());
            topicRestService.save(topicEntity);
            return;
        }
        topicRequest.setUid(uidUtils.getUid());
        TopicEntity topic = modelMapper.map(topicRequest, TopicEntity.class);
        topic.getTopics().add(topicRequest.getTopic());
        topicRestService.save(topic);
    }

    // 删除topic
    @Transactional
    public void remove(TopicRequest topicRequest) {
        Optional<TopicEntity> topicOptional = findByUserUid(topicRequest.getUserUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            topicElement.getTopics().remove(topicRequest.getTopic());
            topicRestService.save(topicElement);
        }
    }

    // 删除topic
    @Transactional
    public void remove(String topic, String userUid) {
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (!topicElement.getTopics().contains(topic)) {
                return;
            }
            log.info("remove topic: {}, userUid {}", topic, userUid);
            topicElement.getTopics().remove(topic);
            topicRestService.save(topicElement);
        }
    }

    // 订阅topic
    @Transactional
    public void subscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client/deviceUid
        Optional<TopicEntity> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                log.info("create: {}", topic);
                return;
            }
            topicElement.getTopics().add(topic);
            topicRestService.save(topicElement);
        } else {
            // create
            final String uid = clientId.split("/")[0];
            TopicRequest topicRequest = TopicRequest.builder()
                    .topic(topic)
                    .userUid(uid)
                    .build();
            topicRequest.getClientIds().add(clientId);
            create(topicRequest);
        }
    }

    // 取消订阅topic
    @Transactional
    public void unsubscribe(String topic, String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        Optional<TopicEntity> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                log.info("create: {}", topic);
                return;
            }
            topicElement.getTopics().add(topic);
            topicRestService.save(topicElement);
        }
    }

    @Transactional
    public void addClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topic = topicOptional.get();
            if (!topic.getClientIds().contains(clientId)) {
                log.info("addClientId: {}", clientId);
                topic.getClientIds().add(clientId);
                topicRestService.save(topic);
            }
        }
    }

    @Transactional
    public void removeClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        Optional<TopicEntity> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            TopicEntity topic = topicOptional.get();
            if (topic.getClientIds().contains(clientId)) {
                log.info("removeClientId: {}", clientId);
                topic.getClientIds().remove(clientId);
                topicRestService.save(topic);
            }
        }
    }

    @Cacheable(value = "topic", key = "#uid")
    public Optional<TopicEntity> findByUid(String uid) {
        return topicRestService.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    public Optional<TopicEntity> findByClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        return findByUserUid(userUid);
    }

    @Cacheable(value = "topic", key = "#uid", unless = "#result == null")
    public Optional<TopicEntity> findByUserUid(String uid) {
        return topicRestService.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#topic", unless="#result == null")
    public Set<TopicEntity> findByTopic(String topic) {
        return topicRestService.findByTopic(topic);
    }

    public void update(String uid, String userUid) {
        Optional<TopicEntity> optionalTopic = findByUid(uid);
        optionalTopic.ifPresent(topic -> {
            topic.setUserUid(userUid);
            topicRestService.save(topic);
        });
    }

    @CacheEvict(value = "topic", key = "#topic.userUid")
    public void delete(TopicEntity topic) {
        topicRestService.deleteByUid(topic.getUid());
    }

    public TopicResponse convertToTopicResponse(TopicEntity topic) {
        return topicRestService.convertToResponse(topic);
    }

}
