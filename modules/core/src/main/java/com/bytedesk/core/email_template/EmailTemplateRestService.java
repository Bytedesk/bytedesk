/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-28 10:40:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:17:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_template;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EmailTemplateRestService extends BaseRestService<EmailTemplateEntity, EmailTemplateRequest, EmailTemplateResponse> {

    private final EmailTemplateRepository emailTemplateRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    @Override
    protected Specification<EmailTemplateEntity> createSpecification(EmailTemplateRequest request) {
        return EmailTemplateSpecification.search(request, authService);
    }

    @Override
    protected Page<EmailTemplateEntity> executePageQuery(Specification<EmailTemplateEntity> specification, Pageable pageable) {
        return emailTemplateRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<EmailTemplateEntity> findByUid(String uid) {
        return emailTemplateRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return emailTemplateRepository.existsByUid(uid);
    }

    @Override
    public EmailTemplateResponse create(EmailTemplateRequest request) {
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        EmailTemplateEntity entity = modelMapper.map(request, EmailTemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        EmailTemplateEntity saved = save(entity);
        return convertToResponse(saved);
    }

    @Override
    public EmailTemplateResponse update(EmailTemplateRequest request) {
        Optional<EmailTemplateEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            EmailTemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            EmailTemplateEntity saved = save(entity);
            return convertToResponse(saved);
        }
        throw new RuntimeException("EmailTemplate not found");
    }

    @Override
    protected EmailTemplateEntity doSave(EmailTemplateEntity entity) {
        return emailTemplateRepository.save(entity);
    }

    @Override
    public EmailTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EmailTemplateEntity entity) {
        try {
            Optional<EmailTemplateEntity> latest = emailTemplateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EmailTemplateEntity latestEntity = latest.get();
                latestEntity.setName(entity.getName());
                latestEntity.setContent(entity.getContent());
                return emailTemplateRepository.save(latestEntity);
            } else {
                throw new RuntimeException("无法找到最新的实体数据，uid: " + entity.getUid());
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<EmailTemplateEntity> optional = emailTemplateRepository.findByUid(uid);
        if (optional.isPresent()) {
            EmailTemplateEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException("EmailTemplate not found");
        }
    }

    @Override
    public void delete(EmailTemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public EmailTemplateResponse convertToResponse(EmailTemplateEntity entity) {
        return modelMapper.map(entity, EmailTemplateResponse.class);
    }
}
