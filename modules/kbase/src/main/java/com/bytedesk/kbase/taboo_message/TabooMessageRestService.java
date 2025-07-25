/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 11:29:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo_message;

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
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooMessageRestService extends BaseRestServiceWithExcel<TabooMessageEntity, TabooMessageRequest, TabooMessageResponse, TabooMessageExcel> {

    private final TabooMessageRepository tabooMessageRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TabooMessageEntity> queryByOrgEntity(TabooMessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TabooMessageEntity> specification = TabooMessageSpecification.search(request);
        return tabooMessageRepository.findAll(specification, pageable);
    }

    @Override
    public Page<TabooMessageResponse> queryByOrg(TabooMessageRequest request) {
        Page<TabooMessageEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TabooMessageResponse> queryByUser(TabooMessageRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "taboo_message", key="#uid", unless = "#result == null")
    @Override
    public Optional<TabooMessageEntity> findByUid(String uid) {
        return tabooMessageRepository.findByUid(uid);
    }

    @Override
    public TabooMessageResponse create(TabooMessageRequest request) {
        // 
        TabooMessageEntity taboo_message = modelMapper.map(request, TabooMessageEntity.class);
        taboo_message.setUid(uidUtils.getUid());
        // categoryUid
        // Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
        // if (categoryOptional.isPresent()) {
        //     taboo_message.setCategoryUid(categoryOptional.get().getUid());
        // }
        // 
        TabooMessageEntity savedTabooMessage = save(taboo_message);
        if (savedTabooMessage == null) {
            throw new RuntimeException("create taboo_message failed");
        }
        return convertToResponse(savedTabooMessage);
    }

    @Override
    public TabooMessageResponse update(TabooMessageRequest request) {
        
        Optional<TabooMessageEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TabooMessageEntity taboo_message = optional.get();
            taboo_message.setContent(request.getContent());
            // categoryUid
            // Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
            // if (categoryOptional.isPresent()) {
            //     taboo_message.setCategoryUid(categoryOptional.get().getUid());
            // }
            // 
            TabooMessageEntity savedTabooMessage = save(taboo_message);
            if (savedTabooMessage == null) {
                throw new RuntimeException("create taboo_message failed");
            }
            return convertToResponse(savedTabooMessage);
        } else {
            throw new RuntimeException("update taboo_message failed");
        }
    }

    @Override
    public TabooMessageEntity save(TabooMessageEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    protected TabooMessageEntity doSave(TabooMessageEntity entity) {
        return tabooMessageRepository.save(entity);
    }

    @Override
    public TabooMessageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TabooMessageEntity entity) {
        try {
            Optional<TabooMessageEntity> latest = tabooMessageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TabooMessageEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return tabooMessageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<TabooMessageEntity> entities) {
        tabooMessageRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TabooMessageEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            TabooMessageEntity taboo_message = optional.get();
            taboo_message.setDeleted(true);
            save(taboo_message);
        }
    }

    @Override
    public void delete(TabooMessageRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public TabooMessageResponse convertToResponse(TabooMessageEntity entity) {
        return modelMapper.map(entity, TabooMessageResponse.class);
    }

    @Override
    public TabooMessageExcel convertToExcel(TabooMessageEntity response) {
        // categoryUid
        // Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(response.getCategoryUid());
        // TabooMessageExcel taboo_messageExcel = modelMapper.map(response, TabooMessageExcel.class);
        // taboo_messageExcel.setCategory(categoryOptional.get().getName());
        // return taboo_messageExcel;
        return null;
    }

    public TabooMessageEntity convertExcelToTabooMessage(TabooMessageExcel excel, String kbUid, String orgUid) {
        return null;
        // return modelMapper.map(excel, TabooMessage.class); // String categoryUid,
        // TabooMessageEntity taboo_message = TabooMessageEntity.builder().build();
        // taboo_message.setUid(uidUtils.getUid());
        // taboo_message.setContent(excel.getContent());
        // // 
        // // taboo_message.setCategoryUid(categoryUid);
        //  Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        // if (categoryOptional.isPresent()) {
        //     taboo_message.setCategoryUid(categoryOptional.get().getUid());
        // } else {
        //     // create category
        //     CategoryRequest categoryRequest = CategoryRequest.builder()
        //             .name(excel.getCategory())
        //             .kbUid(kbUid)
        //             .build();
        //     categoryRequest.setType(CategoryTypeEnum.TABOO.name());
        //     categoryRequest.setOrgUid(orgUid);
        //     // 
        //     CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
        //     taboo_message.setCategoryUid(categoryResponse.getUid());
        // }
        // // 
        // taboo_message.setKbUid(kbUid);
        // taboo_message.setOrgUid(orgUid);

        // return taboo_message;
    }
    
}
