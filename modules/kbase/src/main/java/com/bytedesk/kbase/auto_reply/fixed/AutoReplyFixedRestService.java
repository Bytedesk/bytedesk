/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:42:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AutoReplyFixedRestService extends BaseRestServiceWithExcel<AutoReplyFixedEntity, AutoReplyFixedRequest, AutoReplyFixedResponse, AutoReplyFixedExcel> {

    private final AutoReplyFixedRepository autoReplyRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final CategoryRestService categoryService;

    private final AuthService authService;

    @Override
    public Page<AutoReplyFixedEntity> queryByOrgEntity(AutoReplyFixedRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AutoReplyFixedEntity> specification = AutoReplyFixedSpecification.search(request);
        return autoReplyRepository.findAll(specification, pageable);
    }

    @Override
    public Page<AutoReplyFixedResponse> queryByOrg(AutoReplyFixedRequest request) {
        Page<AutoReplyFixedEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AutoReplyFixedResponse> queryByUser(AutoReplyFixedRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "auto_reply", key="#uid", unless = "#result == null")
    @Override
    public Optional<AutoReplyFixedEntity> findByUid(String uid) {
        return autoReplyRepository.findByUid(uid);
    }

    @Override
    public AutoReplyFixedResponse create(AutoReplyFixedRequest request) {
        // 获取当前用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        AutoReplyFixedEntity autoReply = modelMapper.map(request, AutoReplyFixedEntity.class);
        autoReply.setUid(uidUtils.getUid());

        AutoReplyFixedEntity savedAutoReplyFixed = save(autoReply);
        if (savedAutoReplyFixed == null) {
            throw new RuntimeException("AutoReplyFixed create failed");
        }
        // 
        return convertToResponse(savedAutoReplyFixed);
    }

    @Override
    public AutoReplyFixedResponse update(AutoReplyFixedRequest request) {
        
        Optional<AutoReplyFixedEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            AutoReplyFixedEntity autoReply = optional.get();
            autoReply.setContent(request.getContent());
            autoReply.setCategoryUid(request.getCategoryUid());
            autoReply.setKbUid(request.getKbUid());
            // 
            AutoReplyFixedEntity savedAutoReplyFixed = save(autoReply);
            if (savedAutoReplyFixed == null) {
                throw new RuntimeException("AutoReplyFixed create failed");
            }
            //
            return convertToResponse(savedAutoReplyFixed);
        } else {
            throw new RuntimeException("AutoReplyFixed not found");
        }
    }

    @Override
    public AutoReplyFixedEntity save(AutoReplyFixedEntity entity) {
        try {
            return autoReplyRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void save(List<AutoReplyFixedEntity> entities) {
        autoReplyRepository.saveAll(entities);
    }

    // 启用/禁用固定自动回复
    public AutoReplyFixedResponse enable(AutoReplyFixedRequest request) {
        Optional<AutoReplyFixedEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            AutoReplyFixedEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            AutoReplyFixedEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update AutoReplyFixed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("auto_reply_fixed not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AutoReplyFixedEntity> autoReplyOptional = findByUid(uid);
        if (autoReplyOptional.isPresent()) {
            autoReplyOptional.get().setDeleted(true);
            save(autoReplyOptional.get());
        }
    }

    @Override
    public void delete(AutoReplyFixedRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public AutoReplyFixedEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AutoReplyFixedEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public AutoReplyFixedResponse convertToResponse(AutoReplyFixedEntity entity) {
        return modelMapper.map(entity, AutoReplyFixedResponse.class);
    }

    @Override
    public AutoReplyFixedExcel convertToExcel(AutoReplyFixedEntity autoReply) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryService.findByUid(autoReply.getCategoryUid());
        // 
        AutoReplyFixedExcel excel = modelMapper.map(autoReply, AutoReplyFixedExcel.class);
        if (categoryOptional.isPresent()) {
            excel.setCategory(categoryOptional.get().getName());
        }
        return excel;
    }

    // String categoryUid,
    public AutoReplyFixedEntity convertExcelToAutoReplyFixed(AutoReplyFixedExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, AutoReplyFixed.class);
        AutoReplyFixedEntity autoReply = AutoReplyFixedEntity.builder().build();
        autoReply.setUid(uidUtils.getCacheSerialUid());
        autoReply.setContent(excel.getContent());
        // 
        // autoReply.setType(MessageTypeEnum.TEXT); // TODO: 根据实际类型设置
        autoReply.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        // 
        // autoReply.setCategoryUid(categoryUid);
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            autoReply.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.AUTOREPLY.name());
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            autoReply.setCategoryUid(categoryResponse.getUid());
        }
        // 
        autoReply.setKbUid(kbUid);
        autoReply.setOrgUid(orgUid);

        return autoReply;
    }

    
}
