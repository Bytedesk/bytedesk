/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.comment;

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
public class CommentRestService extends BaseRestServiceWithExport<CommentEntity, CommentRequest, CommentResponse, CommentExcel> {

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<CommentEntity> createSpecification(CommentRequest request) {
        return CommentSpecification.search(request, authService);
    }

    @Override
    protected Page<CommentEntity> executePageQuery(Specification<CommentEntity> spec, Pageable pageable) {
        return commentRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<CommentEntity> findByUid(String uid) {
        return commentRepository.findByUid(uid);
    }

    @Cacheable(value = "comment", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CommentEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return commentRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return commentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CommentResponse create(CommentRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CommentEntity> comment = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (comment.isPresent()) {
                return convertToResponse(comment.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        CommentEntity entity = modelMapper.map(request, CommentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create comment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
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
    protected CommentEntity doSave(CommentEntity entity) {
        return commentRepository.save(entity);
    }

    @Override
    public CommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CommentEntity entity) {
        try {
            Optional<CommentEntity> latest = commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CommentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return commentRepository.save(latestEntity);
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
    public CommentResponse convertToResponse(CommentEntity entity) {
        return modelMapper.map(entity, CommentResponse.class);
    }

    @Override
    public CommentExcel convertToExcel(CommentEntity entity) {
        return modelMapper.map(entity, CommentExcel.class);
    }
    
    public void initComments(String orgUid) {
        // log.info("initThreadComment");
        for (String comment : CommentInitData.getAllComments()) {
            CommentRequest commentRequest = CommentRequest.builder()
                    .uid(Utils.formatUid(orgUid, comment))
                    .name(comment)
                    .order(0)
                    .type(CommentTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(commentRequest);
        }
    }

    
    
}
