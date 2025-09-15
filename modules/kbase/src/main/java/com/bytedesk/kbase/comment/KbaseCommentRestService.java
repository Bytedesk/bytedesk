/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:50:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.comment;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbaseCommentRestService extends BaseRestService<KbaseCommentEntity, KbaseCommentRequest, KbaseCommentResponse> {

    private final KbaseCommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<KbaseCommentEntity> createSpecification(KbaseCommentRequest request) {
        return KbaseCommentSpecification.search(request, authService);
    }

    @Override
    protected Page<KbaseCommentEntity> executePageQuery(Specification<KbaseCommentEntity> spec, Pageable pageable) {
        return commentRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<KbaseCommentEntity> findByUid(String uid) {
        return commentRepository.findByUid(uid);
    }

    @Override
    public KbaseCommentResponse create(KbaseCommentRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        
        KbaseCommentEntity entity = modelMapper.map(request, KbaseCommentEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
        entity.setUser(JSON.toJSONString(userProtobuf));
        entity.setOrgUid(user.getOrgUid());

        KbaseCommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create comment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public KbaseCommentResponse update(KbaseCommentRequest request) {
        Optional<KbaseCommentEntity> optional = commentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            KbaseCommentEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            KbaseCommentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update comment failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("KbaseComment not found");
        }
    }

    @Override
    public KbaseCommentEntity save(KbaseCommentEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @CachePut(value = "comment", key = "#entity.uid")
    @Override
    protected KbaseCommentEntity doSave(KbaseCommentEntity entity) {
        return commentRepository.save(entity);
    }

    @CacheEvict(value = "comment", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<KbaseCommentEntity> optional = commentRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // commentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("KbaseComment not found");
        }
    }

    @Override
    public void delete(KbaseCommentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public KbaseCommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, KbaseCommentEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<KbaseCommentEntity> latest = commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                KbaseCommentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return commentRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public KbaseCommentResponse convertToResponse(KbaseCommentEntity entity) {
        return modelMapper.map(entity, KbaseCommentResponse.class);
    }

    
    
}
