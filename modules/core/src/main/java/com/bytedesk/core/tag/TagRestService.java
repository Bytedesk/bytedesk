/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-13 21:03:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.retry.annotation.Recover;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TagRestService extends BaseRestService<TagEntity, TagRequest, TagResponse> {

    private final TagRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TagResponse> queryByOrg(TagRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TagEntity> spec = TagSpecification.search(request);
        Page<TagEntity> page = tagRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TagResponse> queryByUser(TagRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<TagEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return tagRepository.existsByUid(uid);
    }

    @Override
    public TagResponse create(TagRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TagEntity entity = modelMapper.map(request, TagEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TagEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TagResponse update(TagRequest request) {
        Optional<TagEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TagEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TagEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Tag not found");
        }
    }

    @Override
    public TagEntity save(TagEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected TagEntity doSave(TagEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public TagEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TagEntity entity) {
        try {
            Optional<TagEntity> latest = tagRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TagEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setOrder(entity.getOrder());
                latestEntity.setDeleted(entity.isDeleted());
                return tagRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 重试失败后的回调方法
     */
    @Recover
    public TagEntity recover(Exception e, TagEntity entity) {
        log.error("Failed to save tag after 3 attempts: {}", entity.getName(), e);
        // 可以在这里添加告警通知
        throw new RuntimeException("Failed to save tag after retries: " + e.getMessage());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TagEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Tag not found");
        }
    }

    @Override
    public void delete(TagRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TagResponse convertToResponse(TagEntity entity) {
        return modelMapper.map(entity, TagResponse.class);
    }

    @Override
    public TagResponse queryByUid(TagRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
    
}
