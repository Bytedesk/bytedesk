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

import java.util.Optional;

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
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TopicSubscriptionRestService extends BaseRestServiceWithExport<TopicSubscriptionEntity, TopicSubscriptionRequest, TopicSubscriptionResponse, TopicSubscriptionExcel> {

    private final TopicSubscriptionRepository topic_subscriptionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
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
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<TopicSubscriptionEntity> topic_subscription = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (topic_subscription.isPresent()) {
        //         return convertToResponse(topic_subscription.get());
        //     }
        // }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(TopicSubscriptionPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        TopicSubscriptionEntity entity = modelMapper.map(request, TopicSubscriptionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TopicSubscriptionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create topic_subscription failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TopicSubscriptionResponse update(TopicSubscriptionRequest request) {
        Optional<TopicSubscriptionEntity> optional = topic_subscriptionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TopicSubscriptionEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            // if (!permissionService.hasEntityPermission(TopicSubscriptionPermissions.MODULE_NAME, "UPDATE", entity)) {
            //     throw new RuntimeException("无权限更新该标签数据");
            // }
            
            modelMapper.map(request, entity);
            //
            TopicSubscriptionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update topic_subscription failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TopicSubscription not found");
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
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
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
            
            // 检查用户是否有权限删除该实体
            // if (!permissionService.hasEntityPermission(TopicSubscriptionPermissions.MODULE_NAME, "DELETE", entity)) {
            //     throw new RuntimeException("无权限删除该标签数据");
            // }
            
            entity.setDeleted(true);
            save(entity);
            // topic_subscriptionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TopicSubscription not found");
        }
    }

    @Override
    public void delete(TopicSubscriptionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TopicSubscriptionResponse convertToResponse(TopicSubscriptionEntity entity) {
        return modelMapper.map(entity, TopicSubscriptionResponse.class);
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
        // for (String topic_subscription : TopicSubscriptionInitData.getAllTopicSubscriptions()) {
        //     TopicSubscriptionRequest topic_subscriptionRequest = TopicSubscriptionRequest.builder()
        //             .uid(Utils.formatUid(orgUid, topic_subscription))
        //             .name(topic_subscription)
        //             .order(0)
        //             .type(TopicSubscriptionTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemTopicSubscription(topic_subscriptionRequest);
        // }
    }

    
    
}
