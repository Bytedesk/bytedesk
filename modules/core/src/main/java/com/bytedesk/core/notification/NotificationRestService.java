/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 17:46:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationRestService extends BaseRestServiceWithExport<NotificationEntity, NotificationRequest, NotificationResponse, NotificationExcel> {

    private final NotificationRepository notificationRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<NotificationEntity> createSpecification(NotificationRequest request) {
        return NotificationSpecification.search(request, authService);
    }

    @Override
    protected Page<NotificationEntity> executePageQuery(Specification<NotificationEntity> spec, Pageable pageable) {
        return notificationRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "notification", key = "#uid", unless="#result==null")
    @Override
    public Optional<NotificationEntity> findByUid(String uid) {
        return notificationRepository.findByUid(uid);
    }

    @Cacheable(value = "notification", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<NotificationEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return notificationRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return notificationRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public NotificationResponse create(NotificationRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<NotificationEntity> notification = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (notification.isPresent()) {
                return convertToResponse(notification.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        NotificationEntity entity = modelMapper.map(request, NotificationEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        NotificationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create notification failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public NotificationResponse update(NotificationRequest request) {
        Optional<NotificationEntity> optional = notificationRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            NotificationEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            NotificationEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update notification failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Notification not found");
        }
    }

    @Override
    protected NotificationEntity doSave(NotificationEntity entity) {
        return notificationRepository.save(entity);
    }

    @Override
    public NotificationEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, NotificationEntity entity) {
        try {
            Optional<NotificationEntity> latest = notificationRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                NotificationEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return notificationRepository.save(latestEntity);
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
        Optional<NotificationEntity> optional = notificationRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // notificationRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Notification not found");
        }
    }

    @Override
    public void delete(NotificationRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public NotificationResponse convertToResponse(NotificationEntity entity) {
        return modelMapper.map(entity, NotificationResponse.class);
    }

    @Override
    public NotificationExcel convertToExcel(NotificationEntity entity) {
        return modelMapper.map(entity, NotificationExcel.class);
    }
    
    public void initNotifications(String orgUid) {
        // log.info("initThreadNotification");
        for (String notification : NotificationInitData.getAllNotifications()) {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .uid(Utils.formatUid(orgUid, notification))
                    .name(notification)
                    .type(NotificationTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(notificationRequest);
        }
    }

    
    
}
