/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 10:17:02
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationRestService extends BaseRestService<NotificationEntity, NotificationRequest, NotificationResponse> {

    private NotificationRepository noticeRepository;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    @Override
    public Page<NotificationResponse> queryByOrg(NotificationRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<NotificationEntity> specification = NotificationSpecification.search(request);

        Page<NotificationEntity> page = noticeRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<NotificationResponse> queryByUser(NotificationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "notice", key = "#uid", unless = "#result == null")
    @Override
    public Optional<NotificationEntity> findByUid(String uid) {
        return noticeRepository.findByUid(uid);
    }

    @Override
    public NotificationResponse create(NotificationRequest request) {
        
        NotificationEntity entity = modelMapper.map(request, NotificationEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        NotificationEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create notice failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public NotificationResponse update(NotificationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public NotificationEntity save(NotificationEntity entity) {
        try {
            return noticeRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<NotificationEntity> entity = noticeRepository.findByUid(uid);
        if (entity.isPresent()) {
            // noticeRepository.delete(entity.get());
            entity.get().setDeleted(true);
            save(entity.get());
        }
    }

    @Override
    public void delete(NotificationRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NotificationEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public NotificationResponse convertToResponse(NotificationEntity entity) {
        return modelMapper.map(entity, NotificationResponse.class);
    }
    
}
