/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 16:18:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_template;

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
public class TicketTemplateRestService extends BaseRestServiceWithExport<TicketTemplateEntity, TicketTemplateRequest, TicketTemplateResponse, TicketTemplateExcel> {

    private final TicketTemplateRepository ticketTemplateRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TicketTemplateEntity> createSpecification(TicketTemplateRequest request) {
        return TicketTemplateSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketTemplateEntity> executePageQuery(Specification<TicketTemplateEntity> spec, Pageable pageable) {
        return ticketTemplateRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "ticketTemplate", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketTemplateEntity> findByUid(String uid) {
        return ticketTemplateRepository.findByUid(uid);
    }

    @Cacheable(value = "ticketTemplate", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TicketTemplateEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return ticketTemplateRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ticketTemplateRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketTemplateResponse create(TicketTemplateRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TicketTemplateEntity> ticketTemplate = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (ticketTemplate.isPresent()) {
                return convertToResponse(ticketTemplate.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TicketTemplateEntity entity = modelMapper.map(request, TicketTemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TicketTemplateEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ticketTemplate failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TicketTemplateResponse update(TicketTemplateRequest request) {
        Optional<TicketTemplateEntity> optional = ticketTemplateRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketTemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TicketTemplateEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ticketTemplate failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TicketTemplate not found");
        }
    }

    @Override
    protected TicketTemplateEntity doSave(TicketTemplateEntity entity) {
        return ticketTemplateRepository.save(entity);
    }

    @Override
    public TicketTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketTemplateEntity entity) {
        try {
            Optional<TicketTemplateEntity> latest = ticketTemplateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketTemplateEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ticketTemplateRepository.save(latestEntity);
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
        Optional<TicketTemplateEntity> optional = ticketTemplateRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ticketTemplateRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TicketTemplate not found");
        }
    }

    @Override
    public void delete(TicketTemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TicketTemplateResponse convertToResponse(TicketTemplateEntity entity) {
        return modelMapper.map(entity, TicketTemplateResponse.class);
    }

    @Override
    public TicketTemplateExcel convertToExcel(TicketTemplateEntity entity) {
        return modelMapper.map(entity, TicketTemplateExcel.class);
    }
    
    public void initTicketTemplates(String orgUid) {
        // log.info("initThreadTicketTemplate");
    }

    
    
}
