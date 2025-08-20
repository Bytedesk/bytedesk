/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:52:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_template;

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
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentTemplateRestService extends BaseRestServiceWithExcel<AgentTemplateEntity, AgentTemplateRequest, AgentTemplateResponse, AgentTemplateExcel> {

    private final AgentTemplateRepository agentTemplateRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<AgentTemplateEntity> createSpecification(AgentTemplateRequest request) {
        return AgentTemplateSpecification.search(request);
    }

    @Override
    protected Page<AgentTemplateEntity> executePageQuery(Specification<AgentTemplateEntity> spec, Pageable pageable) {
        return agentTemplateRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "agentTemplate", key = "#uid", unless="#result==null")
    @Override
    public Optional<AgentTemplateEntity> findByUid(String uid) {
        return agentTemplateRepository.findByUid(uid);
    }

    @Cacheable(value = "agentTemplate", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<AgentTemplateEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return agentTemplateRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return agentTemplateRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public AgentTemplateResponse create(AgentTemplateRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<AgentTemplateEntity> agentTemplate = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (agentTemplate.isPresent()) {
                return convertToResponse(agentTemplate.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        AgentTemplateEntity entity = modelMapper.map(request, AgentTemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AgentTemplateEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create agentTemplate failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public AgentTemplateResponse update(AgentTemplateRequest request) {
        Optional<AgentTemplateEntity> optional = agentTemplateRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AgentTemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            AgentTemplateEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update agentTemplate failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("AgentTemplate not found");
        }
    }

    @Override
    protected AgentTemplateEntity doSave(AgentTemplateEntity entity) {
        return agentTemplateRepository.save(entity);
    }

    @Override
    public AgentTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentTemplateEntity entity) {
        try {
            Optional<AgentTemplateEntity> latest = agentTemplateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentTemplateEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return agentTemplateRepository.save(latestEntity);
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
        Optional<AgentTemplateEntity> optional = agentTemplateRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // agentTemplateRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("AgentTemplate not found");
        }
    }

    @Override
    public void delete(AgentTemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentTemplateResponse convertToResponse(AgentTemplateEntity entity) {
        return modelMapper.map(entity, AgentTemplateResponse.class);
    }

    @Override
    public AgentTemplateExcel convertToExcel(AgentTemplateEntity entity) {
        return modelMapper.map(entity, AgentTemplateExcel.class);
    }
    
    public void initAgentTemplates(String orgUid) {
        // log.info("initThreadAgentTemplate");
    }

    
}
