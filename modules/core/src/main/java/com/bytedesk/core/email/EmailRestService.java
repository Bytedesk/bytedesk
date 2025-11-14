/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 18:16:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EmailRestService extends BaseRestServiceWithExport<EmailEntity, EmailRequest, EmailResponse, EmailExcel> {

    private final EmailRepository emailRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<EmailEntity> createSpecification(EmailRequest request) {
        return EmailSpecification.search(request, authService);
    }

    @Override
    protected Page<EmailEntity> executePageQuery(Specification<EmailEntity> spec, Pageable pageable) {
        return emailRepository.findAll(spec, pageable);
    }

    @Override
    public EmailResponse queryByUid(EmailRequest request) {
        Optional<EmailEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException("Email not found");
        }
    }

    @Cacheable(value = "email", key = "#uid", unless="#result==null")
    @Override
    public Optional<EmailEntity> findByUid(String uid) {
        return emailRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return emailRepository.existsByUid(uid);
    }

    @Override
    public EmailResponse create(EmailRequest request) {
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
        EmailEntity entity = modelMapper.map(request, EmailEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        EmailEntity savedEntity = save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public EmailResponse update(EmailRequest request) {
        Optional<EmailEntity> optional = emailRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            EmailEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            EmailEntity savedEntity = save(entity);
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Email not found");
        }
    }

    @Override
    protected EmailEntity doSave(EmailEntity entity) {
        return emailRepository.save(entity);
    }

    @Override
    public EmailEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EmailEntity entity) {
        try {
            Optional<EmailEntity> latest = emailRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EmailEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return emailRepository.save(latestEntity);
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
        Optional<EmailEntity> optional = emailRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // emailRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Email not found");
        }
    }

    @Override
    public void delete(EmailRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public EmailResponse convertToResponse(EmailEntity entity) {
        return modelMapper.map(entity, EmailResponse.class);
    }

    @Override
    public EmailExcel convertToExcel(EmailEntity entity) {
        return modelMapper.map(entity, EmailExcel.class);
    }
    
}
