/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 21:17:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group_notice;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GroupNoticeRestService extends BaseRestService<GroupNoticeEntity, GroupNoticeRequest, GroupNoticeResponse> {

    private final GroupNoticeRepository group_noticeRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<GroupNoticeResponse> queryByOrg(GroupNoticeRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GroupNoticeEntity> spec = GroupNoticeSpecification.search(request);
        Page<GroupNoticeEntity> page = group_noticeRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<GroupNoticeResponse> queryByUser(GroupNoticeRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "group_notice", key = "#uid", unless="#result==null")
    @Override
    public Optional<GroupNoticeEntity> findByUid(String uid) {
        return group_noticeRepository.findByUid(uid);
    }

    @Override
    public GroupNoticeResponse create(GroupNoticeRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        
        GroupNoticeEntity entity = modelMapper.map(request, GroupNoticeEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        entity.setOrgUid(user.getOrgUid());

        GroupNoticeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create group_notice failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public GroupNoticeResponse update(GroupNoticeRequest request) {
        Optional<GroupNoticeEntity> optional = group_noticeRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            GroupNoticeEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            GroupNoticeEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update group_notice failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("GroupNotice not found");
        }
    }
    
    @Override
    public GroupNoticeEntity save(GroupNoticeEntity entity) {
        log.info("Attempting to save group_notice: {}", entity.getName());
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected GroupNoticeEntity doSave(GroupNoticeEntity entity) {
        return group_noticeRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<GroupNoticeEntity> optional = group_noticeRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // group_noticeRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("GroupNotice not found");
        }
    }

    @Override
    public void delete(GroupNoticeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public GroupNoticeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GroupNoticeEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<GroupNoticeEntity> latest = group_noticeRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                GroupNoticeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return group_noticeRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GroupNoticeResponse convertToResponse(GroupNoticeEntity entity) {
        return modelMapper.map(entity, GroupNoticeResponse.class);
    }
    
}
