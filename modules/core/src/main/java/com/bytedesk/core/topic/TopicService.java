/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-27 12:06:52
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

import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Async
    public void create(String topic, String uid) {
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(topic)
                .uid(uid)
                // .qos(1)
                .build();
        create(topicRequest);
    }

    @Transactional
    private void create(TopicRequest topicRequest) {

        // if (existsByTopicAndUid(topicRequest.getTopic(), topicRequest.getUid())) {
        //     return;
        // }

        Optional<Topic> topicOptional = findByUid(topicRequest.getUid());
        if (topicOptional.isPresent()) {
            Topic topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topicRequest.getTopic())) {
                log.info("create: {}", topicRequest.getTopic());
                return;
            }
            topicElement.getTopics().add(topicRequest.getTopic());
            // 
            save(topicElement);
            // 
            return;
        }
        // 
        topicRequest.setTid(uidUtils.getCacheSerialUid());
        // 
        Topic topic = modelMapper.map(topicRequest, Topic.class);
        topic.getTopics().add(topicRequest.getTopic());
        // Topics topicsObject = new Topics();
        // topicsObject.getTopics().add(topicRequest.getTopic());
        // topic.setTopic(JSON.toJSONString(topicsObject));
        // 
        save(topic);
    }
    

    @Async
    public void subscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topicElement = topicOptional.get();
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
                    .uid(uid)
                    // .qos(qos)
                    .build();
            topicRequest.getClientIds().add(clientId);
            create(topicRequest);
        }
    }

    @Async
    public void unsubscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                log.info("create: {}", topic);
                return;
            }
            topicElement.getTopics().add(topic);
        }
        // final String uid = clientId.split("/")[0];
        // deleteByTopicAndUid(topic, uid);
    }

    @Async
    public void addClientId(String clientId) {
        // 用户clientId格式: uid/client
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (!topic.getClientIds().contains(clientId)) {
                log.info("addClientId: {}", clientId);
                topic.getClientIds().add(clientId);
                save(topic);
            }
        }
    }

    @Async
    public void removeClientId(String clientId) {
        // 用户clientId格式: uid/client
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (topic.getClientIds().contains(clientId)) {
                log.info("removeClientId: {}", clientId);
                topic.getClientIds().remove(clientId);
                save(topic);
            }
        }
    }

    @Cacheable(value = "topic", key = "#tid")
    public Optional<Topic> findByTid(String tid) {
        return topicRepository.findByTid(tid);
    }

    @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    public Optional<Topic> findByClientId(String clientId) {
        // 用户clientId格式: uid/client
        final String uid = clientId.split("/")[0];
        return findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#uid", unless = "#result == null")
    public Optional<Topic> findByUid(String uid) {
        return topicRepository.findFirstByUid(uid);
    }

    @Cacheable(value = "topic", key = "#topic", unless="#result == null")
    public Set<Topic> findByTopic(String topic) {
        // List<Topic> topics = topicRepository.findByTopicStartsWith(topic);
        Set<Topic> topics = topicRepository.findByTopicsContains(topic);
        return topics;
        // return topics.stream().map(this::convertToTopicResponse).toList();
    }

    @Caching(put = {
        @CachePut(value = "topic", key = "#topic.uid")
    })
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    // TODO: 需要从原先uid的缓存列表中删除，然后添加到新的uid的换成列表中
    // @CachePut(value = "topic", key = "#uid")
    public void update(String tid, String uid) {
        Optional<Topic> optionalTopic = findByTid(tid);
        optionalTopic.ifPresent(topic -> {
            topic.setUid(uid);
            topicRepository.save(topic);
        });
    }

    @CacheEvict(value = "topic", key = "#topic.uid")
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }

    public TopicResponse convertToTopicResponse(Topic topic) {
        return modelMapper.map(topic, TopicResponse.class);
    }

}
