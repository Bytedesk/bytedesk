/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-10 12:23:34
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    public void create(String topic, String uid) {
        TopicRequest topicRequest = TopicRequest.builder()
                .topic(topic)
                .userUid(uid)
                // .qos(1)
                .build();
        create(topicRequest);
    }

    @Transactional
    private void create(TopicRequest topicRequest) {

        // if (existsByTopicAndUid(topicRequest.getTopic(), topicRequest.getUid())) {
        //     return;
        // }

        Optional<Topic> topicOptional = findByUserUid(topicRequest.getUserUid());
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
        topicRequest.setUid(uidUtils.getCacheSerialUid());
        // 
        Topic topic = modelMapper.map(topicRequest, Topic.class);
        topic.getTopics().add(topicRequest.getTopic());
        // Topics topicsObject = new Topics();
        // topicsObject.getTopics().add(topicRequest.getTopic());
        // topic.setTopic(JSON.toJSONString(topicsObject));
        // 
        save(topic);
    }
    

    public void subscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client/deviceUid
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
                    .userUid(uid)
                    // .qos(qos)
                    .build();
            topicRequest.getClientIds().add(clientId);
            create(topicRequest);
        }
    }

    public void unsubscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client/deviceUid
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
        // 用户clientId格式: uid/client/deviceUid
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (!topic.getClientIds().contains(clientId)) {
                log.info("addClientId: {}", clientId);
                // FIXME: org.springframework.orm.ObjectOptimisticLockingFailureException: Row
                // was updated or deleted by another transaction (or unsaved-value mapping was
                // incorrect) : [com.bytedesk.core.topic.Topic#16]
                topic.getClientIds().add(clientId);
                save(topic);
            }
        }
    }

    @Async
    public void removeClientId(String clientId) {
        // 用户clientId格式: uid/client/deviceUid
        Optional<Topic> topicOptional = findByClientId(clientId);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (topic.getClientIds().contains(clientId)) {
                log.info("removeClientId: {}", clientId);
                // FIXME: org.springframework.orm.ObjectOptimisticLockingFailureException: Row
                // was updated or deleted by another transaction (or unsaved-value mapping was
                // incorrect) : [com.bytedesk.core.topic.Topic#16]
                topic.getClientIds().remove(clientId);
                save(topic);
            }
        }
    }

    @Cacheable(value = "topic", key = "#uid")
    public Optional<Topic> findByUid(String uid) {
        return topicRepository.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    public Optional<Topic> findByClientId(String clientId) {
        // 用户clientId格式: uid/client/deviceUid
        final String uid = clientId.split("/")[0];
        return findByUserUid(uid);
    }

    @Cacheable(value = "topic", key = "#uid", unless = "#result == null")
    public Optional<Topic> findByUserUid(String uid) {
        return topicRepository.findFirstByUserUid(uid);
    }

    @Cacheable(value = "topic", key = "#topic", unless="#result == null")
    public Set<Topic> findByTopic(String topic) {
        // List<Topic> topics = topicRepository.findByTopicStartsWith(topic);
        Set<Topic> topics = topicRepository.findByTopicsContains(topic);
        return topics;
        // return topics.stream().map(this::convertToTopicResponse).toList();
    }

    @Caching(put = {
        @CachePut(value = "topic", key = "#topic.userUid")
    })
    public Topic save(Topic topic) {
        try {
            return topicRepository.save(topic);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 乐观锁冲突处理逻辑
            handleOptimisticLockingFailureException(e, topic);
        }
        return null;
    }

    // TODO: 待处理
    private void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Topic topic) {
        // 可以在这里实现重试逻辑，例如使用递归调用或定时任务
        // 也可以记录日志、发送通知或执行其他业务逻辑
        log.error("Optimistic locking failure for topic: {}", topic.getUserUid());
        // e.printStackTrace();
        // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
    }

    // TODO: 需要从原先uid的缓存列表中删除，然后添加到新的uid的换成列表中
    // @CachePut(value = "topic", key = "#uid")
    public void update(String uid, String userUid) {
        Optional<Topic> optionalTopic = findByUid(uid);
        optionalTopic.ifPresent(topic -> {
            topic.setUserUid(userUid);
            topicRepository.save(topic);
        });
    }

    @CacheEvict(value = "topic", key = "#topic.userUid")
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }

    public TopicResponse convertToTopicResponse(Topic topic) {
        return modelMapper.map(topic, TopicResponse.class);
    }

}
