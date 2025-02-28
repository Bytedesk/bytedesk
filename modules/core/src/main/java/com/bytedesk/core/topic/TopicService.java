/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 14:45:14
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
import org.springframework.cache.annotation.Caching;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.OptimisticLockingHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    // private final ActionRestService actionService;

    private final OptimisticLockingHandler optimisticLockingHandler;

    // private final ConcurrentHashMap<String, String> concurrentMap = new ConcurrentHashMap<>();

    public void create(String topic, String uid) {
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(topic)
                .userUid(uid)
                .build();
        create(topicRequest);
    }

    public void create(TopicRequest topicRequest) {
        Optional<TopicEntity> topicOptional = findByUserUid(topicRequest.getUserUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topicRequest.getTopic())) {
                return;
            }
            log.info("add topic: {}", topicRequest.getTopic());
            topicElement.getTopics().add(topicRequest.getTopic());
            save(topicElement);
            // 
            return;
        }
        topicRequest.setUid(uidUtils.getCacheSerialUid());
        // 
        TopicEntity topic = modelMapper.map(topicRequest, TopicEntity.class);
        topic.getTopics().add(topicRequest.getTopic());
        // 
        save(topic);
    }

    public void remove(TopicRequest topicRequest) {
        Optional<TopicEntity> topicOptional = findByUserUid(topicRequest.getUserUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            topicElement.getTopics().remove(topicRequest.getTopic());
            save(topicElement);
        }
    }

    public void remove(String topic, String userUid) {
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (!topicElement.getTopics().contains(topic)) {
                return;
            }
            log.info("remove topic: {}, userUid {}", topic, userUid);
            topicElement.getTopics().remove(topic);
            save(topicElement);
        }
    }
    
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
            // 
            save(topicElement);
        } else {
            // create
            final String uid = clientId.split("/")[0];
            TopicRequest topicRequest = TopicRequest.builder()
                    .topic(topic)
                    .userUid(uid)
                    // .qos(qos)
                    .build();
            topicRequest.getClientIds().add(clientId);
            create(topicRequest);
        }
    }

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
            // 
            save(topicElement);
        }
        // final String uid = clientId.split("/")[0];
        // deleteByTopicAndUid(topic, uid);
    }

    @Async
    public void addClientId(String clientId) {
        // concurrentMap.remove(clientId);

        // 用户clientId格式: userUid/client/deviceUid
        Optional<TopicEntity> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            TopicEntity topic = topicOptional.get();
            if (!topic.getClientIds().contains(clientId)) {
                log.info("addClientId: {}", clientId);
                topic.getClientIds().add(clientId);
                save(topic);
            }
        }
    }

    private void doRemoveClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        Optional<TopicEntity> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            TopicEntity topic = topicOptional.get();
            if (topic.getClientIds().contains(clientId)) {
                log.info("removeClientId: {}", clientId);
                topic.getClientIds().remove(clientId);
                save(topic);
            }
        }
    }

    @Async
    public void removeClientId(String clientId) {
        // TODO: 防止客户端频繁闪断重连的情况，延迟执行，防止频繁删除
        // concurrentMap.put(clientId, clientId);
        doRemoveClientId(clientId);
    }

    // 5分钟没有重连成功的话，就删除掉
    // @Scheduled(fixedDelay = 5 * 60 * 1000)
    // public void scheduleTask() {
    //     // log.info("scheduleTask");
    //     concurrentMap.forEach((key, value) -> {
    //         doRemoveClientId(key);
    //     });
    // }

    @Cacheable(value = "topic", key = "#uid")
    public Optional<TopicEntity> findByUid(String uid) {
        return topicRepository.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    public Optional<TopicEntity> findByClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        return findByUserUid(userUid);
    }

    @Cacheable(value = "topic", key = "#uid", unless = "#result == null")
    public Optional<TopicEntity> findByUserUid(String uid) {
        return topicRepository.findFirstByUserUid(uid);
    }

    @Cacheable(value = "topic", key = "#topic", unless="#result == null")
    public Set<TopicEntity> findFirstByTopic(String topic) {
        return topicRepository.findFirstByTopicsContains(topic);
    }

    @Caching(put = {
        @CachePut(value = "topic", key = "#topic.userUid")
    })
    public TopicEntity save(TopicEntity topic) {
        try {
            return optimisticLockingHandler.executeWithRetry(
                () -> topicRepository.save(topic),
                "topic",
                topic.getUid(),
                topic
            );
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Failed to save topic after retries", e);
            return null;
        }
    }

    // TODO: 需要从原先uid的缓存列表中删除，然后添加到新的uid的换成列表中
    // @CachePut(value = "topic", key = "#uid")
    public void update(String uid, String userUid) {
        Optional<TopicEntity> optionalTopic = findByUid(uid);
        optionalTopic.ifPresent(topic -> {
            topic.setUserUid(userUid);
            topicRepository.save(topic);
        });
    }

    @CacheEvict(value = "topic", key = "#topic.userUid")
    public void delete(TopicEntity topic) {
        topicRepository.delete(topic);
    }

    public TopicResponse convertToTopicResponse(TopicEntity topic) {
        return modelMapper.map(topic, TopicResponse.class);
    }

}
