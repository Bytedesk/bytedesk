/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:40:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:49:19
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
import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.Utils;

@Slf4j
@Service
@AllArgsConstructor
public class AutoReplyFixedRestService extends BaseRestServiceWithExport<AutoReplyFixedEntity, AutoReplyFixedRequest, AutoReplyFixedResponse, AutoReplyFixedExcel> {

    private final AutoReplyFixedRepository autoReplyFixedRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final CategoryRestService categoryRestService;

    private final AuthService authService;

    @Override
    protected Specification<AutoReplyFixedEntity> createSpecification(AutoReplyFixedRequest request) {
        return AutoReplyFixedSpecification.search(request, authService);
    }

    @Override
    protected Page<AutoReplyFixedEntity> executePageQuery(Specification<AutoReplyFixedEntity> spec, Pageable pageable) {
        return autoReplyFixedRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "auto_reply", key="#uid", unless = "#result == null")
    @Override
    public Optional<AutoReplyFixedEntity> findByUid(String uid) {
        return autoReplyFixedRepository.findByUid(uid);
    }

    @Override
    public AutoReplyFixedResponse create(AutoReplyFixedRequest request) {
        // 获取当前用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
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
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected AutoReplyFixedEntity doSave(AutoReplyFixedEntity entity) {
        return autoReplyFixedRepository.save(entity);
    }

    @Override
    public AutoReplyFixedEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AutoReplyFixedEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<AutoReplyFixedEntity> latest = autoReplyFixedRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AutoReplyFixedEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return autoReplyFixedRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<AutoReplyFixedEntity> entities) {
        autoReplyFixedRepository.saveAll(entities);
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
    public AutoReplyFixedResponse convertToResponse(AutoReplyFixedEntity entity) {
        return modelMapper.map(entity, AutoReplyFixedResponse.class);
    }

    @Override
    public AutoReplyFixedExcel convertToExcel(AutoReplyFixedEntity autoReply) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(autoReply.getCategoryUid());
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
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
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
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            autoReply.setCategoryUid(categoryResponse.getUid());
        }
        // 
        autoReply.setKbUid(kbUid);
        autoReply.setOrgUid(orgUid);
        return autoReply;
    }

    public void initData(String orgUid) {
        // 检查是否已经有数据，如果有则不初始化
        if (autoReplyFixedRepository.count() > 0) {
            log.info("AutoReplyFixed data already exists, skipping initialization.");
            return;
        }
        log.info("Initializing AutoReplyFixed data...");

        String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_AUTOREPLY_UID);
        
        // 创建默认分类
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name(AutoReplyFixedInitData.DEFAULT_CATEGORY_NAME)
                .type(CategoryTypeEnum.AUTOREPLY.name())
                .kbUid(kbUid)
                .orgUid(orgUid)
                .build();
        CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
        String categoryUid = categoryResponse.getUid();

        // 从AutoReplyFixedInitData获取默认自动回复数据
        List<AutoReplyFixedInitData.AutoReplyData> defaultDataList = AutoReplyFixedInitData.getDefaultAutoReplyData();
        
        // 创建固定自动回复数据
        List<AutoReplyFixedEntity> autoReplyList = new ArrayList<>();
        
        for (AutoReplyFixedInitData.AutoReplyData data : defaultDataList) {
            AutoReplyFixedEntity autoReply = AutoReplyFixedEntity.builder()
                    .uid(uidUtils.getUid())
                    .content(data.getContent())
                    .type(data.getType())
                    .enabled(data.isEnabled())
                    .categoryUid(categoryUid)
                    .kbUid(kbUid)
                    .orgUid(orgUid)
                    .build();
            autoReplyList.add(autoReply);
        }

        // 保存所有数据
        save(autoReplyList);
    }

    
}
