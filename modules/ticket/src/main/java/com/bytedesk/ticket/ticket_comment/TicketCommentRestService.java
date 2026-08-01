/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_comment;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketCommentRestService extends BaseRestServiceWithExport<TicketCommentEntity, TicketCommentRequest, TicketCommentResponse, TicketCommentExcel> {

    private final TicketCommentRepository ticket_commentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final TicketAttachmentRepository attachmentRepository;

    private final UploadRestService uploadRestService;
    
    @Override
    public Page<TicketCommentEntity> queryByOrgEntity(TicketCommentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketCommentEntity> specs = TicketCommentSpecification.search(request, authService);
        return ticket_commentRepository.findAll(specs, pageable);
    }

    @Override
    public Page<TicketCommentResponse> queryByOrg(TicketCommentRequest request) {
        Page<TicketCommentEntity> ticket_commentPage = queryByOrgEntity(request);
        return ticket_commentPage.map(this::convertToResponse);
    }

    @Override
    public Page<TicketCommentResponse> queryByUser(TicketCommentRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    /**
     * 按工单 uid 分页查询评论/备注，按创建时间倒序
     */
    public Page<TicketCommentResponse> queryByTicketUid(TicketCommentRequest request) {
        Pageable pageable = request.getPageable();
        Page<TicketCommentEntity> page = ticket_commentRepository
                .findByTicketUidAndDeletedFalseOrderByCreatedAtDesc(request.getTicketUid(), pageable);
        return page.map(this::convertToResponse);
    }

    @Cacheable(value = "ticket_comment", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketCommentEntity> findByUid(String uid) {
        return ticket_commentRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return ticket_commentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketCommentResponse create(TicketCommentRequest request) {
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            // 构建用户快照
            if (!StringUtils.hasText(request.getUser())) {
                request.setUser("{\"uid\":\"" + user.getUid() + "\",\"nickname\":\"" 
                        + (user.getNickname() != null ? user.getNickname() : "") + "\",\"avatar\":\""
                        + (user.getAvatar() != null ? user.getAvatar() : "") + "\"}");
            }
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 权限检查
        if (!permissionService.canCreateAtLevel(TicketCommentPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        TicketCommentEntity entity = modelMapper.map(request, TicketCommentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        
        TicketCommentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }

        // 处理附件
        Set<TicketAttachmentEntity> attachments = new HashSet<>();
        if (request.getUploadUids() != null && !request.getUploadUids().isEmpty()) {
            for (String uploadUid : request.getUploadUids()) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setOrgUid(savedEntity.getOrgUid());
                    attachment.setComment(savedEntity);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    attachments.add(attachment);
                }
            }
        }
        savedEntity.setAttachments(attachments);
        
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TicketCommentResponse update(TicketCommentRequest request) {
        Optional<TicketCommentEntity> optional = ticket_commentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketCommentEntity entity = optional.get();
            
            if (!permissionService.hasEntityPermission(TicketCommentPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            // 仅允许更新 content
            if (StringUtils.hasText(request.getContent())) {
                entity.setContent(request.getContent());
            }
            
            TicketCommentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected TicketCommentEntity doSave(TicketCommentEntity entity) {
        return ticket_commentRepository.save(entity);
    }

    @Override
    public TicketCommentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketCommentEntity entity) {
        try {
            Optional<TicketCommentEntity> latest = ticket_commentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketCommentEntity latestEntity = latest.get();
                latestEntity.setContent(entity.getContent());
                return ticket_commentRepository.save(latestEntity);
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
        Optional<TicketCommentEntity> optional = ticket_commentRepository.findByUid(uid);
        if (optional.isPresent()) {
            TicketCommentEntity entity = optional.get();
            
            if (!permissionService.hasEntityPermission(TicketCommentPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(TicketCommentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TicketCommentResponse convertToResponse(TicketCommentEntity entity) {
        return modelMapper.map(entity, TicketCommentResponse.class);
    }

    @Override
    public TicketCommentExcel convertToExcel(TicketCommentEntity entity) {
        return modelMapper.map(entity, TicketCommentExcel.class);
    }

    @Override
    protected Specification<TicketCommentEntity> createSpecification(TicketCommentRequest request) {
        return TicketCommentSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketCommentEntity> executePageQuery(Specification<TicketCommentEntity> spec, Pageable pageable) {
        return ticket_commentRepository.findAll(spec, pageable);
    }
    
    public void initTicketComments(String orgUid) {
        // 工单评论无需预设种子数据
    }

}
