/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 22:34:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

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
public class NoticeRestService extends BaseRestService<NoticeEntity, NoticeRequest, NoticeResponse> {

    private NoticeRepository noticeRepository;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    @Override
    public Page<NoticeResponse> queryByOrg(NoticeRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<NoticeEntity> specification = NoticeSpecification.search(request);

        Page<NoticeEntity> page = noticeRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<NoticeResponse> queryByUser(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "notice", key = "#uid", unless = "#result == null")
    @Override
    public Optional<NoticeEntity> findByUid(String uid) {
        return noticeRepository.findByUid(uid);
    }

    @Override
    public NoticeResponse create(NoticeRequest request) {
        
        NoticeEntity entity = modelMapper.map(request, NoticeEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        NoticeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create notice failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public NoticeResponse update(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public NoticeEntity save(NoticeEntity entity) {
        try {
            return noticeRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<NoticeEntity> entity = noticeRepository.findByUid(uid);
        if (entity.isPresent()) {
            // noticeRepository.delete(entity.get());
            entity.get().setDeleted(true);
            save(entity.get());
        }
    }

    @Override
    public void delete(NoticeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NoticeEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public NoticeResponse convertToResponse(NoticeEntity entity) {
        return modelMapper.map(entity, NoticeResponse.class);
    }

    @Override
    public NoticeResponse queryByUid(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
