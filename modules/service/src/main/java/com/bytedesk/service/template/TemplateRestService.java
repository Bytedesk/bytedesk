/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-29 08:27:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.template;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
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
public class TemplateRestService extends BaseRestServiceWithExcel<TemplateEntity, TemplateRequest, TemplateResponse, TemplateExcel> {

    private final TemplateRepository templateRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TemplateEntity> queryByOrgEntity(TemplateRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TemplateEntity> spec = TemplateSpecification.search(request);
        return templateRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TemplateResponse> queryByOrg(TemplateRequest request) {
        Page<TemplateEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TemplateResponse> queryByUser(TemplateRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public TemplateResponse queryByUid(TemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "template", key = "#uid", unless="#result==null")
    @Override
    public Optional<TemplateEntity> findByUid(String uid) {
        return templateRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return templateRepository.existsByUid(uid);
    }

    @Override
    public TemplateResponse create(TemplateRequest request) {
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
        TemplateEntity entity = modelMapper.map(request, TemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TemplateEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create template failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TemplateResponse update(TemplateRequest request) {
        Optional<TemplateEntity> optional = templateRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TemplateEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update template failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Template not found");
        }
    }

    @Override
    protected TemplateEntity doSave(TemplateEntity entity) {
        return templateRepository.save(entity);
    }

    @Override
    public TemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TemplateEntity entity) {
        try {
            Optional<TemplateEntity> latest = templateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TemplateEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return templateRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TemplateEntity> optional = templateRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // templateRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Template not found");
        }
    }

    @Override
    public void delete(TemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TemplateResponse convertToResponse(TemplateEntity entity) {
        return modelMapper.map(entity, TemplateResponse.class);
    }

    @Override
    public TemplateExcel convertToExcel(TemplateEntity entity) {
        return modelMapper.map(entity, TemplateExcel.class);
    }
    
    public void initTemplates(String orgUid) {
        // log.info("initThreadTemplate");
        // for (String template : TemplateInitData.getAllTemplates()) {
        //     TemplateRequest templateRequest = TemplateRequest.builder()
        //             .uid(Utils.formatUid(orgUid, template))
        //             .name(template)
        //             .order(0)
        //             .type(TemplateTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(templateRequest);
        // }
    }
    
}
