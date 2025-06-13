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
package com.bytedesk.core.group_invite;

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
public class GroupInviteRestService extends BaseRestService<GroupInviteEntity, GroupInviteRequest, GroupInviteResponse> {

    private final GroupInviteRepository group_inviteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<GroupInviteResponse> queryByOrg(GroupInviteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GroupInviteEntity> spec = GroupInviteSpecification.search(request);
        Page<GroupInviteEntity> page = group_inviteRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<GroupInviteResponse> queryByUser(GroupInviteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "group_invite", key = "#uid", unless="#result==null")
    @Override
    public Optional<GroupInviteEntity> findByUid(String uid) {
        return group_inviteRepository.findByUid(uid);
    }

    @Override
    public GroupInviteResponse create(GroupInviteRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        
        GroupInviteEntity entity = modelMapper.map(request, GroupInviteEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        entity.setOrgUid(user.getOrgUid());

        GroupInviteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create group_invite failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public GroupInviteResponse update(GroupInviteRequest request) {
        Optional<GroupInviteEntity> optional = group_inviteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            GroupInviteEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            GroupInviteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update group_invite failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("GroupInvite not found");
        }
    }
    
    @Override
    public GroupInviteEntity save(GroupInviteEntity entity) {
        log.info("Attempting to save group_invite: {}", entity.getName());
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected GroupInviteEntity doSave(GroupInviteEntity entity) {
        return group_inviteRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<GroupInviteEntity> optional = group_inviteRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // group_inviteRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("GroupInvite not found");
        }
    }

    @Override
    public void delete(GroupInviteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public GroupInviteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GroupInviteEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<GroupInviteEntity> latest = group_inviteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                GroupInviteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return group_inviteRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GroupInviteResponse convertToResponse(GroupInviteEntity entity) {
        return modelMapper.map(entity, GroupInviteResponse.class);
    }
    
}
