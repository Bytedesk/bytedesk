/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:45:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FormResultRestService extends BaseRestServiceWithExport<FormResultEntity, FormResultRequest, FormResultResponse, FormResultExcel> {

    private final FormResultRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<FormResultEntity> createSpecification(FormResultRequest request) {
        return FormResultSpecification.search(request);
    }

    @Override
    protected Page<FormResultEntity> executePageQuery(Specification<FormResultEntity> spec, Pageable pageable) {
        return tagRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FormResultEntity> queryByOrgEntity(FormResultRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FormResultEntity> spec = FormResultSpecification.search(request);
        return tagRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FormResultResponse> queryByOrg(FormResultRequest request) {
        Page<FormResultEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FormResultResponse> queryByUser(FormResultRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public FormResultResponse queryByUid(FormResultRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<FormResultEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<FormResultEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tagRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tagRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public FormResultResponse create(FormResultRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<FormResultEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tag.isPresent()) {
                return convertToResponse(tag.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        FormResultEntity entity = modelMapper.map(request, FormResultEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        FormResultEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public FormResultResponse update(FormResultRequest request) {
        Optional<FormResultEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FormResultEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FormResultEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("FormResult not found");
        }
    }

    @Override
    protected FormResultEntity doSave(FormResultEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public FormResultEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FormResultEntity entity) {
        try {
            Optional<FormResultEntity> latest = tagRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FormResultEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tagRepository.save(latestEntity);
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
        Optional<FormResultEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("FormResult not found");
        }
    }

    @Override
    public void delete(FormResultRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FormResultResponse convertToResponse(FormResultEntity entity) {
        return modelMapper.map(entity, FormResultResponse.class);
    }

    @Override
    public FormResultExcel convertToExcel(FormResultEntity entity) {
        return modelMapper.map(entity, FormResultExcel.class);
    }
    
}
