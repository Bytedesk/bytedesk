/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:44:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notification;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.department.DepartmentRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationRestService extends BaseRestService<NotificationEntity, NotificationRequest, NotificationResponse> {

    private NotificationRepository notificationRepository;

    private UserRepository userRepository;

    private DepartmentRestService departmentRestService;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    @Override
    protected Specification<NotificationEntity> createSpecification(NotificationRequest request) {
        return NotificationSpecification.search(request, authService);
    }

    @Override
    protected Page<NotificationEntity> executePageQuery(Specification<NotificationEntity> spec, Pageable pageable) {
        return notificationRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "notification", key = "#uid", unless = "#result == null")
    @Override
    public Optional<NotificationEntity> findByUid(String uid) {
        return notificationRepository.findByUid(uid);
    }

    @Cacheable(value = "notification", key = "#messageUid", unless = "#result == null")
    public Optional<NotificationEntity> findByExtraContains(String messageUid) {
        return notificationRepository.findByExtraContains(messageUid);
    }

    @Cacheable(value = "notification", key = "#status + #messageUid", unless = "#result == null")
    public Optional<NotificationEntity> findByStatusAndExtraContains(String status, String messageUid) {
        return notificationRepository.findByStatusAndExtraContains(status, messageUid);
    }

    @Override
    public NotificationResponse create(NotificationRequest request) {
        NotificationEntity entity = modelMapper.map(request, NotificationEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setType(NotificationTypeEnum.fromValue(entity.getType()).name());
        NotificationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public NotificationResponse update(NotificationRequest request) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }

        NotificationEntity entity = notificationRepository.findByUidAndUserUidAndDeletedFalse(request.getUid(), user.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND));

        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getTitle())) {
            entity.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            entity.setContent(request.getContent());
        }
        entity.setType(NotificationTypeEnum.fromValue(entity.getType()).name());

        NotificationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    public long countUnread() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return 0L;
        }
        return notificationRepository.countByUserUidAndStatusAndDeletedFalse(user.getUid(), NotificationStatusEnum.UNREAD.name());
    }

    public NotificationResponse markRead(String uid) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        NotificationEntity entity = notificationRepository.findByUidAndUserUidAndDeletedFalse(uid, user.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND));
        entity.setStatus(NotificationStatusEnum.READ.name());
        NotificationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    public long markAllRead() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return 0L;
        }

        List<NotificationEntity> unreadNotifications = notificationRepository.findByUserUidAndStatusAndDeletedFalse(user.getUid(), NotificationStatusEnum.UNREAD.name());
        unreadNotifications.forEach(notification -> notification.setStatus(NotificationStatusEnum.READ.name()));
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    public NotificationResponse queryCurrentUserNotification(String uid) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
        NotificationEntity entity = notificationRepository.findByUidAndUserUidAndDeletedFalse(uid, user.getUid())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND));
        return convertToResponse(entity);
    }

    public NotificationResponse acceptTransfer(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse rejectTransfer(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse cancelTransfer(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse timeOutTransfer(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse acceptInvite(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse rejectInvite(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse cancelInvite(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse timeOutInvite(String messageUid) {
        return updateByMessageUid(messageUid, NotificationStatusEnum.READ.name());
    }

    public NotificationResponse updateByMessageUid(String messageUid, String status) {
        Optional<NotificationEntity> entity = notificationRepository.findByExtraContains(messageUid);
        if (entity.isPresent()) {
            NotificationEntity notificationEntity = entity.get();
            notificationEntity.setStatus(status);
            NotificationEntity savedEntity = save(notificationEntity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        return null;
    }

    @Override
    public NotificationEntity save(NotificationEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected NotificationEntity doSave(NotificationEntity entity) {
        return notificationRepository.save(entity);
    }

    @Override
    public NotificationEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NotificationEntity entity) {
        try {
            Optional<NotificationEntity> latest = notificationRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                NotificationEntity latestEntity = latest.get();
                return notificationRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<NotificationEntity> entity = notificationRepository.findByUid(uid);
        if (entity.isPresent()) {
            entity.get().setDeleted(true);
            save(entity.get());
        }
    }

    @Override
    public void delete(NotificationRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public NotificationResponse convertToResponse(NotificationEntity entity) {
        NotificationResponse response = modelMapper.map(entity, NotificationResponse.class);

        if (StringUtils.hasText(entity.getUserUid())) {
            userRepository.findByUid(entity.getUserUid()).ifPresent(user -> {
                response.setUsername(user.getUsername());
                response.setUserNickname(user.getNickname());
                response.setUserAvatar(user.getAvatar());
            });
        }

        if (StringUtils.hasText(entity.getDeptUid())) {
            departmentRestService.findByUid(entity.getDeptUid())
                    .ifPresent(department -> response.setDeptName(department.getName()));
        }

        if (StringUtils.hasText(entity.getCreatorUid())) {
            userRepository.findByUid(entity.getCreatorUid()).ifPresent(user -> {
                response.setCreatorUid(user.getUid());
                response.setCreatorUsername(user.getUsername());
                response.setCreatorNickname(user.getNickname());
            });
        }

        return response;
    }
    
}
