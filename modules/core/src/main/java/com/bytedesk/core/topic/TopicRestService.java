/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-20 11:16:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 14:56:31
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicRestService extends BaseRestService<TopicEntity, TopicRequest, TopicResponse> {

    private final TopicRepository topicRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final AuthService authService;

    public Page<TopicEntity> queryByOrgEntity(TopicRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TopicEntity> spec = TopicSpecification.search(request);
        return topicRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TopicResponse> queryByOrg(TopicRequest request) {
        Page<TopicEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TopicResponse> queryByUser(TopicRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Override
    public TopicResponse queryByUid(TopicRequest request) {
        Optional<TopicEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException("Failed to query topic by uid: " + request.getUid());
        }
    }

    @Cacheable(value = "topic", key = "#clientId", unless = "#result == null")
    public Optional<TopicEntity> findByClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        return findByUserUid(userUid);
    }

    @Cacheable(value = "topic", key = "#uid", unless = "#result==null")
    @Override
    public Optional<TopicEntity> findByUid(String uid) {
        return topicRepository.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#userUid", unless = "#result==null")
    public Optional<TopicEntity> findByUserUid(String userUid) {
        return topicRepository.findFirstByUserUid(userUid);
    }

    @Cacheable(value = "topic", key = "#topic", unless = "#result==null")
    public Set<TopicEntity> findByTopic(String topic) {
        return topicRepository.findByTopicsContains(topic);
    }

    public Boolean existsByUid(String uid) {
        return topicRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TopicResponse create(TopicRequest request) {
        // 设置用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }

        // 检查是否已存在该用户的记录
        Optional<TopicEntity> existingTopic = findByUserUid(request.getUserUid());
        if (existingTopic.isPresent()) {
            // 如果存在，更新现有记录
            TopicEntity entity = existingTopic.get();
            // 添加新的topics
            if (request.getTopic() != null) {
                entity.getTopics().add(request.getTopic());
            }
            if (request.getTopics() != null) {
                entity.getTopics().addAll(request.getTopics());
            }
            // 添加新的clientIds
            if (request.getClientIds() != null) {
                entity.getClientIds().addAll(request.getClientIds());
            }

            TopicEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update topic failed");
            }
            return convertToResponse(savedEntity);
        }

        // 如果不存在，创建新记录
        TopicEntity entity = modelMapper.map(request, TopicEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        entity.getTopics().add(request.getTopic());
        if (request.getTopics() != null) {
            entity.getTopics().addAll(request.getTopics());
        }

        // 保存实体
        TopicEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create topic failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TopicResponse update(TopicRequest request) {
        Optional<TopicEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TopicEntity entity = optional.get();
            modelMapper.map(request, entity);
            // 更新topics
            if (request.getTopic() != null) {
                entity.getTopics().add(request.getTopic());
            }
            if (request.getTopics() != null) {
                entity.getTopics().addAll(request.getTopics());
            }
            // 更新clientIds
            if (request.getClientIds() != null) {
                entity.getClientIds().addAll(request.getClientIds());
            }

            TopicEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update topic failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Topic not found");
        }
    }

    @Override
    protected TopicEntity doSave(TopicEntity entity) {
        return topicRepository.save(entity);
    }

    @Override
    public TopicEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TopicEntity entity) {
        try {
            Optional<TopicEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                TopicEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.getTopics().addAll(entity.getTopics());
                latestEntity.getClientIds().addAll(entity.getClientIds());
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TopicEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            TopicEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException("Topic not found");
        }
    }

    @Override
    public void delete(TopicRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TopicResponse convertToResponse(TopicEntity entity) {
        return modelMapper.map(entity, TopicResponse.class);
    }

    public Boolean isSubscribed(TopicRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        Optional<TopicEntity> topicOptional = findByUserUid(user.getUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            return topicElement.getTopics().contains(request.getTopic());
        } else {
            return false;
        }
    }

    // 删除topic
    @Transactional
    public void remove(TopicRequest topicRequest) {
        Optional<TopicEntity> topicOptional = findByUserUid(topicRequest.getUserUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            topicElement.getTopics().remove(topicRequest.getTopic());
            save(topicElement);
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
            save(topicElement);
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
            save(topicElement);
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

    @Transactional
    public TopicResponse subscribe(TopicRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        String topic = request.getTopic();
        Optional<TopicEntity> topicOptional = findByUserUid(user.getUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                return convertToResponse(topicElement);
            } else {
                topicElement.getTopics().add(topic);
            }
            TopicEntity savedEntity = save(topicElement);
            if (savedEntity == null) {
                throw new RuntimeException("Update topic failed");
            }
            return convertToResponse(savedEntity);
        } else {
            // create new topic
            request.setTopic(topic);
            request.setUserUid(user.getUid());
            return create(request);
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
            save(topicElement);
        }
    }

    public TopicResponse unsubscribe(TopicRequest request) { 
         UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        String topic = request.getTopic();
        Optional<TopicEntity> topicOptional = findByUserUid(user.getUid());
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (!topicElement.getTopics().contains(topic)) {
                return convertToResponse(topicElement);
            }
            topicElement.getTopics().remove(topic);
           TopicEntity savedEntity = save(topicElement);
            if (savedEntity == null) {
                throw new RuntimeException("Update topic failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Topic not found");
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
                save(topic);
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
                save(topic);
            }
        }
    }

}
