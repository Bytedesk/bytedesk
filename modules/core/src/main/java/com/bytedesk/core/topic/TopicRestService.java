/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-20 11:16:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 18:29:08
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
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

    @Cacheable(value = "topic", key = "#uid", unless="#result==null")
    @Override
    public Optional<TopicEntity> findByUid(String uid) {
        return topicRepository.findByUid(uid);
    }

    @Cacheable(value = "topic", key = "#userUid", unless="#result==null")
    public Optional<TopicEntity> findByUserUid(String userUid) {
        return topicRepository.findFirstByUserUid(userUid);
    }

    @Cacheable(value = "topic", key = "#topic", unless="#result==null")
    public Set<TopicEntity> findByTopic(String topic) {
        return topicRepository.findByTopicsContains(topic);
    }

    public Boolean existsByUid(String uid) {
        return topicRepository.existsByUid(uid);
    }

    @Override
    public TopicResponse create(TopicRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        // 设置用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }

        // 创建实体
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
    public TopicEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TopicEntity entity) {
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

    @Override
    public TopicResponse queryByUid(TopicRequest request) {
        Optional<TopicEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    // 添加clientId
    @CacheEvict(value = "topic", key = "#userUid")
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

    // 移除clientId
    @CacheEvict(value = "topic", key = "#userUid")
    public void removeClientId(String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topic = topicOptional.get();
            if (topic.getClientIds().contains(clientId)) {
                log.info("removeClientId: {}", clientId);
                topic.getClientIds().remove(clientId);
                save(topic);
            }
        }
    }

    // 订阅topic
    @CacheEvict(value = "topic", key = "#userUid")
    public void subscribe(String topic, String clientId) {
        // 用户clientId格式: uid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                log.info("topic already exists: {}", topic);
                return;
            }
            topicElement.getTopics().add(topic);
            save(topicElement);
        } else {
            // create new topic
            TopicRequest topicRequest = TopicRequest.builder()
                    .topic(topic)
                    .userUid(userUid)
                    .build();
            topicRequest.getClientIds().add(clientId);
            create(topicRequest);
        }
    }

    // 取消订阅topic
    @CacheEvict(value = "topic", key = "#userUid")
    public void unsubscribe(String topic, String clientId) {
        // 用户clientId格式: userUid/client/deviceUid
        final String userUid = clientId.split("/")[0];
        Optional<TopicEntity> topicOptional = findByUserUid(userUid);
        if (topicOptional.isPresent()) {
            TopicEntity topicElement = topicOptional.get();
            if (topicElement.getTopics().contains(topic)) {
                topicElement.getTopics().remove(topic);
                save(topicElement);
            }
        }
    }
}
