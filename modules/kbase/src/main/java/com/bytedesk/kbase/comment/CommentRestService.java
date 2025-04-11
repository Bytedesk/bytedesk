/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 12:41:20
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentRestService extends BaseRestService<CommentEntity, CommentRequest, CommentResponse> {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CommentResponse> queryByOrg(CommentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CommentEntity> spec = CommentSpecification.search(request);
        Page<CommentEntity> page = commentRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CommentResponse> queryByUser(CommentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<CommentEntity> findByUid(String uid) {
        return commentRepository.findByUid(uid);
    }

    @Override
    public CommentResponse create(CommentRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        
        CommentEntity entity = modelMapper.map(request, CommentEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
        entity.setUser(JSON.toJSONString(userProtobuf));
        entity.setOrgUid(user.getOrgUid());

        CommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create comment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public CommentResponse update(CommentRequest request) {
        Optional<CommentEntity> optional = commentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CommentEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            CommentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update comment failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Comment not found");
        }
    }

    @Override
    public CommentEntity save(CommentEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected CommentEntity doSave(CommentEntity entity) {
        return commentRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CommentEntity> optional = commentRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // commentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Comment not found");
        }
    }

    @Override
    public void delete(CommentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CommentEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<CommentEntity> latest = commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CommentEntity latestEntity = latest.get();
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
    public CommentResponse convertToResponse(CommentEntity entity) {
        return modelMapper.map(entity, CommentResponse.class);
    }
    
}
