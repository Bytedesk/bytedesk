/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 17:57:08
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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooMessageRestService extends BaseRestService<TabooMessageEntity, TabooMessageRequest, TabooMessageResponse> {

    private final TabooMessageRepository taboo_messageRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    private final AuthService authService;

    @Override
    public Page<TabooMessageResponse> queryByOrg(TabooMessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TabooMessageEntity> specification = TabooMessageSpecification.search(request);
        Page<TabooMessageEntity> page = taboo_messageRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TabooMessageResponse> queryByUser(TabooMessageRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "taboo_message", key="#uid", unless = "#result == null")
    @Override
    public Optional<TabooMessageEntity> findByUid(String uid) {
        return taboo_messageRepository.findByUid(uid);
    }

    @Override
    public TabooMessageResponse create(TabooMessageRequest request) {
        // 
        TabooMessageEntity taboo_message = modelMapper.map(request, TabooMessageEntity.class);
        taboo_message.setUid(uidUtils.getUid());
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
        if (categoryOptional.isPresent()) {
            taboo_message.setCategoryUid(categoryOptional.get().getUid());
        }
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
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
            if (categoryOptional.isPresent()) {
                taboo_message.setCategoryUid(categoryOptional.get().getUid());
            }
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
            return taboo_messageRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void save(List<TabooMessageEntity> entities) {
        taboo_messageRepository.saveAll(entities);
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TabooMessageEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TabooMessageResponse convertToResponse(TabooMessageEntity entity) {
        return modelMapper.map(entity, TabooMessageResponse.class);
    }

    public Page<TabooMessageEntity> queryByOrgEntity(TabooMessageRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TabooMessageEntity> specification = TabooMessageSpecification.search(request);
        return taboo_messageRepository.findAll(specification, pageable);
    }

    public TabooMessageExcel convertToExcel(TabooMessageEntity response) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(response.getCategoryUid());
        TabooMessageExcel taboo_messageExcel = modelMapper.map(response, TabooMessageExcel.class);
        taboo_messageExcel.setCategory(categoryOptional.get().getName());
        return taboo_messageExcel;
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
