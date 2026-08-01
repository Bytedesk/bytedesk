/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic_subscription;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.CommonI18nExceptions;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
// import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicSubscriptionRestService extends BaseRestServiceWithExport<TopicSubscriptionEntity, TopicSubscriptionRequest, TopicSubscriptionResponse, TopicSubscriptionExcel> {

    private static final String CHAT_SUBSCRIPTION_TYPE = TopicSubscriptionTypeEnum.CHAT.name();

    private final TopicSubscriptionRepository topic_subscriptionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final UserRepository userRepository;

    private final MemberRepository memberRepository;
    
    // private final PermissionService permissionService;
    
    @Override
    public Page<TopicSubscriptionEntity> queryByOrgEntity(TopicSubscriptionRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TopicSubscriptionEntity> specs = TopicSubscriptionSpecification.search(request, authService);
        return topic_subscriptionRepository.findAll(specs, pageable);
    }

    @Override
    public Page<TopicSubscriptionResponse> queryByOrg(TopicSubscriptionRequest request) {
        Page<TopicSubscriptionEntity> topic_subscriptionPage = queryByOrgEntity(request);
        return topic_subscriptionPage.map(this::convertToResponse);
    }

    @Override
    public Page<TopicSubscriptionResponse> queryByUser(TopicSubscriptionRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        if (!StringUtils.hasText(request.getOrgUid())) {
            request.setOrgUid(user.getOrgUid());
        }
        return queryByOrg(request);
    }

    @Cacheable(value = "topic_subscription", key = "#uid", unless="#result==null")
    @Override
    public Optional<TopicSubscriptionEntity> findByUid(String uid) {
        return topic_subscriptionRepository.findByUid(uid);
    }

    // @Cacheable(value = "topic_subscription", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<TopicSubscriptionEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return topic_subscriptionRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return topic_subscriptionRepository.existsByUid(uid);
    }

    @Transactional(readOnly = true)
    public Set<String> findSubscriberUserUidsByTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return Set.of();
        }
        return topic_subscriptionRepository.findByTopicAndDeletedFalse(topic).stream()
                .map(TopicSubscriptionEntity::getUserUid)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Set<String> findSubscribedTopicsByUserUid(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            return Set.of();
        }
        return topic_subscriptionRepository.findByUserUidAndDeletedFalse(userUid).stream()
                .filter(this::isChatSubscription)
                .map(TopicSubscriptionEntity::getTopic)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public List<TopicSubscriptionEntity> findAllTopicSubscriptions() {
        return topic_subscriptionRepository.findAll();
    }

    @Transactional
    public void create(String topic, String userUid) {
        create(topic, userUid, CHAT_SUBSCRIPTION_TYPE);
    }

    @Transactional
    public TopicSubscriptionResponse create(String topic, String userUid, String type) {
        return createTypedSubscription(topic, userUid, type);
    }

    @Transactional(readOnly = true)
    public Boolean isSubscribed(TopicSubscriptionRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        String type = StringUtils.hasText(request.getType()) ? request.getType() : CHAT_SUBSCRIPTION_TYPE;
        return isTopicSubscribed(user.getUid(), request.getTopic(), type);
    }

    @Transactional
    public TopicSubscriptionResponse subscribe(TopicSubscriptionRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        String type = StringUtils.hasText(request.getType()) ? request.getType() : CHAT_SUBSCRIPTION_TYPE;
        return createTypedSubscription(request.getTopic(), user.getUid(), type);
    }

    @Transactional
    public void subscribe(String topic, String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return;
        }
        String userUid = clientId.split("/")[0];
        createChatSubscription(topic, userUid);
    }

    @Transactional
    public void unsubscribe(TopicSubscriptionRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        String type = StringUtils.hasText(request.getType()) ? request.getType() : CHAT_SUBSCRIPTION_TYPE;
        softDeleteTopicSubscriptionByType(request.getTopic(), user.getUid(), type);
    }

    @Transactional
    public void unsubscribe(String topic, String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return;
        }
        String userUid = clientId.split("/")[0];
        softDeleteChatTopicSubscription(topic, userUid);
    }

    @Transactional
    public void remove(TopicSubscriptionRequest request) {
        softDeleteAllTopicSubscriptions(request.getTopic(), request.getUserUid());
    }

    @Transactional
    public void remove(String topic, String userUid) {
        softDeleteAllTopicSubscriptions(topic, userUid);
    }

    @Transactional
    public void remove(String topic, String userUid, String type) {
        softDeleteTopicSubscriptionByType(topic, userUid, type);
    }

    @Transactional
    @Override
    public TopicSubscriptionResponse create(TopicSubscriptionRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public TopicSubscriptionResponse createSystemTopicSubscription(TopicSubscriptionRequest request) {
        return createInternal(request, true);
    }

    private TopicSubscriptionResponse createInternal(TopicSubscriptionRequest request, boolean skipPermissionCheck) {
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        UserEntity user = authService.getUser();
        if (user != null && !StringUtils.hasText(request.getUserUid())) {
            request.setUserUid(user.getUid());
        }

        if (!StringUtils.hasText(request.getUserUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        if (!StringUtils.hasText(request.getTopic())) {
            throw new RuntimeException("topic is required");
        }
        if (!StringUtils.hasText(request.getType())) {
            request.setType(TopicSubscriptionTypeEnum.CHAT.name());
        }

        Optional<TopicSubscriptionEntity> existing = findReusableSubscription(
                request.getUserUid(),
                request.getTopic(),
                request.getType());
        if (existing.isPresent()) {
            TopicSubscriptionEntity entity = existing.get();
            if (entity.isDeleted()) {
                entity.setDeleted(false);
                entity.setType(request.getType());
                return convertToResponse(save(entity));
            }
            if (!StringUtils.hasText(entity.getType())) {
                entity.setType(request.getType());
                return convertToResponse(save(entity));
            }
            return convertToResponse(entity);
        }
        
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // if (!skipPermissionCheck && !permissionService.canCreateAtLevel(TopicSubscriptionPermissions.MODULE_NAME, level)) {
        //     throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        // }
        
        TopicSubscriptionEntity entity = modelMapper.map(request, TopicSubscriptionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        if (!StringUtils.hasText(entity.getType())) {
            entity.setType(TopicSubscriptionTypeEnum.CHAT.name());
        }
        TopicSubscriptionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TopicSubscriptionResponse update(TopicSubscriptionRequest request) {
        Optional<TopicSubscriptionEntity> optional = topic_subscriptionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TopicSubscriptionEntity entity = optional.get();
            modelMapper.map(request, entity);
            if (!StringUtils.hasText(entity.getType())) {
                entity.setType(TopicSubscriptionTypeEnum.CHAT.name());
            }
            TopicSubscriptionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected TopicSubscriptionEntity doSave(TopicSubscriptionEntity entity) {
        return topic_subscriptionRepository.save(entity);
    }

    @Override
    public TopicSubscriptionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TopicSubscriptionEntity entity) {
        try {
            Optional<TopicSubscriptionEntity> latest = topic_subscriptionRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TopicSubscriptionEntity latestEntity = latest.get();
                latestEntity.setTopic(entity.getTopic());
                latestEntity.setType(StringUtils.hasText(entity.getType()) ? entity.getType() : TopicSubscriptionTypeEnum.CHAT.name());
                latestEntity.setDeleted(entity.isDeleted());
                return topic_subscriptionRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<TopicSubscriptionEntity> optional = topic_subscriptionRepository.findByUid(uid);
        if (optional.isPresent()) {
            TopicSubscriptionEntity entity = optional.get();
            
            // 权限校验当前未启用，保留删除逻辑。
            
            entity.setDeleted(true);
            save(entity);
            // topic_subscriptionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(TopicSubscriptionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TopicSubscriptionResponse convertToResponse(TopicSubscriptionEntity entity) {
        TopicSubscriptionResponse response = modelMapper.map(entity, TopicSubscriptionResponse.class);

        if (StringUtils.hasText(entity.getUserUid())) {
            userRepository.findByUid(entity.getUserUid())
                    .ifPresentOrElse(
                            user -> applyUserProfile(response, user),
                            () -> memberRepository.findByUid(entity.getUserUid())
                                    .ifPresent(member -> {
                                        if (member.getUser() != null) {
                                            response.setUsername(member.getUser().getUsername());
                                        }
                                        response.setUserNickname(member.getNickname());
                                        response.setUserAvatar(member.getAvatar());
                                    }));
        }

        return response;
    }

    private void applyUserProfile(TopicSubscriptionResponse response, UserEntity user) {
        response.setUsername(user.getUsername());
        response.setUserNickname(user.getNickname());
        response.setUserAvatar(user.getAvatar());
    }

    @Override
    public TopicSubscriptionExcel convertToExcel(TopicSubscriptionEntity entity) {
        return modelMapper.map(entity, TopicSubscriptionExcel.class);
    }

    @Override
    protected Specification<TopicSubscriptionEntity> createSpecification(TopicSubscriptionRequest request) {
        return TopicSubscriptionSpecification.search(request, authService);
    }

    @Override
    protected Page<TopicSubscriptionEntity> executePageQuery(Specification<TopicSubscriptionEntity> spec, Pageable pageable) {
        return topic_subscriptionRepository.findAll(spec, pageable);
    }
    
    public void initTopicSubscriptions(String orgUid) {
        // log.info("initTopicSubscriptionTopicSubscription");
    }

    private TopicSubscriptionResponse createChatSubscription(String topic, String userUid) {
        return createTypedSubscription(topic, userUid, CHAT_SUBSCRIPTION_TYPE);
    }

    private TopicSubscriptionResponse createTypedSubscription(String topic, String userUid, String type) {
        if (!StringUtils.hasText(topic) || !StringUtils.hasText(userUid)) {
            return null;
        }
        TopicSubscriptionRequest request = TopicSubscriptionRequest.builder()
                .topic(topic)
                .userUid(userUid)
                .type(StringUtils.hasText(type) ? type : CHAT_SUBSCRIPTION_TYPE)
                .build();
        return createSystemTopicSubscription(request);
    }

    private void softDeleteChatTopicSubscription(String topic, String userUid) {
        if (!StringUtils.hasText(topic) || !StringUtils.hasText(userUid)) {
            return;
        }
        findReusableSubscription(userUid, topic, CHAT_SUBSCRIPTION_TYPE)
                .filter(subscription -> !subscription.isDeleted())
                .ifPresent(entity -> {
                    entity.setDeleted(true);
                    entity.setType(CHAT_SUBSCRIPTION_TYPE);
                    save(entity);
                });
    }

    private void softDeleteTopicSubscriptionByType(String topic, String userUid, String type) {
        if (!StringUtils.hasText(topic) || !StringUtils.hasText(userUid) || !StringUtils.hasText(type)) {
            return;
        }
        findReusableSubscription(userUid, topic, type)
                .filter(subscription -> !subscription.isDeleted())
                .ifPresent(entity -> {
                    entity.setDeleted(true);
                    entity.setType(type);
                    save(entity);
                });
    }

    private void softDeleteAllTopicSubscriptions(String topic, String userUid) {
        if (!StringUtils.hasText(topic) || !StringUtils.hasText(userUid)) {
            return;
        }
        topic_subscriptionRepository.findByUserUidAndTopic(userUid, topic).stream()
                .filter(subscription -> !subscription.isDeleted())
                .forEach(subscription -> {
                    subscription.setDeleted(true);
                    if (!StringUtils.hasText(subscription.getType())) {
                        subscription.setType(CHAT_SUBSCRIPTION_TYPE);
                    }
                    save(subscription);
                });
    }

    private Optional<TopicSubscriptionEntity> findReusableSubscription(String userUid, String topic, String type) {
        List<TopicSubscriptionEntity> subscriptions = topic_subscriptionRepository.findByUserUidAndTopic(userUid, topic);
        return subscriptions.stream()
                .filter(item -> isCompatibleType(item, type))
                .sorted(Comparator.comparing(TopicSubscriptionEntity::isDeleted))
                .findFirst();
    }

    private boolean isTopicSubscribed(String userUid, String topic, String type) {
        if (!StringUtils.hasText(userUid) || !StringUtils.hasText(topic)) {
            return false;
        }
        return topic_subscriptionRepository.findByUserUidAndTopic(userUid, topic).stream()
                .filter(subscription -> !subscription.isDeleted())
                .anyMatch(subscription -> isCompatibleType(subscription, type));
    }

    private boolean isChatSubscription(TopicSubscriptionEntity entity) {
        return isCompatibleType(entity, CHAT_SUBSCRIPTION_TYPE);
    }

    private boolean isCompatibleType(TopicSubscriptionEntity entity, String type) {
        if (!StringUtils.hasText(entity.getType())) {
            return CHAT_SUBSCRIPTION_TYPE.equals(type);
        }
        return entity.getType().equals(type);
    }

    
    
}
