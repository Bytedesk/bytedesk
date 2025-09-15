/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:13:51
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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VocCommentRestService extends BaseRestServiceWithExport<VocCommentEntity, VocCommentRequest, VocCommentResponse, VocCommentExcel> {

    private final VocCommentRepository commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<VocCommentEntity> createSpecification(VocCommentRequest request) {
        return VocCommentSpecification.search(request, authService);
    }

    @Override
    protected Page<VocCommentEntity> executePageQuery(Specification<VocCommentEntity> spec, Pageable pageable) {
        return commentRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<VocCommentEntity> findByUid(String uid) {
        return commentRepository.findByUid(uid);
    }

    // @Cacheable(value = "comment", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<VocCommentEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return commentRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return commentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public VocCommentResponse create(VocCommentRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<VocCommentEntity> comment = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (comment.isPresent()) {
        //         return convertToResponse(comment.get());
        //     }
        // }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        VocCommentEntity entity = modelMapper.map(request, VocCommentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        VocCommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create comment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public VocCommentResponse update(VocCommentRequest request) {
        Optional<VocCommentEntity> optional = commentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            VocCommentEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            VocCommentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update comment failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("VocComment not found");
        }
    }

    @Override
    protected VocCommentEntity doSave(VocCommentEntity entity) {
        return commentRepository.save(entity);
    }

    @Override
    public VocCommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, VocCommentEntity entity) {
        try {
            Optional<VocCommentEntity> latest = commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                VocCommentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
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
        Optional<VocCommentEntity> optional = commentRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // commentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("VocComment not found");
        }
    }

    @Override
    public void delete(VocCommentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public VocCommentResponse convertToResponse(VocCommentEntity entity) {
        return modelMapper.map(entity, VocCommentResponse.class);
    }

    @Override
    public VocCommentExcel convertToExcel(VocCommentEntity entity) {
        return modelMapper.map(entity, VocCommentExcel.class);
    }
    
    public void initVocComments(String orgUid) {
        // log.info("initThreadVocComment");
        // for (String comment : VocCommentInitData.getAllVocComments()) {
        //     VocCommentRequest commentRequest = VocCommentRequest.builder()
        //             .uid(Utils.formatUid(orgUid, comment))
        //             // .name(comment)
        //             .type(VocCommentTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(commentRequest);
        // }
    }

    
    
}
