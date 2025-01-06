/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 16:03:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class CategoryRestService extends BaseRestService<CategoryEntity, CategoryRequest, CategoryResponse> {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public List<CategoryResponse> findByNullParent(String platform) {
        // 一级分类
        List<CategoryEntity> firstCategoriesList = categoryRepository.findByParentAndPlatformAndDeletedOrderByOrderNoAsc(null,
                platform, false);

        Iterator<CategoryEntity> iterator = firstCategoriesList.iterator();
        while (iterator.hasNext()) {
            CategoryEntity category = iterator.next();
            // 二级分类
            List<CategoryEntity> secondCategoriesSet = categoryRepository.findByParentAndPlatformAndDeletedOrderByOrderNoAsc(category,
                    platform, false);
            if (secondCategoriesSet != null && !secondCategoriesSet.isEmpty()) {
                category.setChildren(secondCategoriesSet);
            }
        }
        return convertToResponseList(firstCategoriesList);
    }

    @Override
    public Page<CategoryResponse> queryByOrg(CategoryRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<CategoryEntity> specs = CategorySpecification.search(request);
        Page<CategoryEntity> page = categoryRepository.findAll(specs, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CategoryResponse> queryByUser(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<CategoryEntity> findByUid(String uid) {
        return categoryRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return categoryRepository.existsByUid(uid);
    }

    public Boolean existsByNameAndOrgUidAndDeletedFalse(String name, String orgUid) {
        return categoryRepository.existsByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {

        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return null;
        }

        CategoryEntity category = modelMapper.map(request, CategoryEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            category.setUid(uidUtils.getUid());
        }
        if (StringUtils.hasText(request.getPlatform())) {
            category.setPlatform(request.getPlatform());
        }
        //
        CategoryEntity newCategory = save(category);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }
        // 
        return convertToResponse(newCategory);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(String name, String type, String orgUid,
           String level, String platform) {
        return categoryRepository.findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeletedFalse(name, type, orgUid, level, platform);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndLevelAndPlatform(String name, String type, LevelEnum level,
            PlatformEnum platform) {
        return categoryRepository.findByNameAndTypeAndLevelAndPlatformAndDeletedFalse(name, type, level.name(), platform.name());
    }

    public Optional<CategoryEntity> findByNameAndKbUid(String name, String kbUid) {
        return categoryRepository.findByNameAndKbUidAndDeletedFalse(name, kbUid);
    }

    public List<CategoryEntity> findByKbUid(String kbUid) {
        return categoryRepository.findByKbUidAndDeletedFalse(kbUid);
    }

    @Override
    public CategoryResponse update(CategoryRequest request) {
        Optional<CategoryEntity> category = findByUid(request.getUid());
        if (!category.isPresent()) {
            throw new RuntimeException("category not found");
        }
        CategoryEntity entity = category.get();
        // modelMapper.map(request, entity);
        entity.setName(request.getName());
        // entity.setIcon(request.getIcon());
        entity.setType(request.getType());
        // entity.setPlatform(request.getPlatform());
        // TODO: children
        CategoryEntity newCategory = save(entity);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }

        return convertToResponse(newCategory);
    }

    @Override
    public CategoryEntity save(CategoryEntity entity) {
        try {
            return categoryRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<CategoryEntity> category = findByUid(uid);
        if (category.isPresent()) {
            category.get().setDeleted(true);
            save(category.get());
        }
    }

    @Override
    public void delete(CategoryRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CategoryEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public CategoryResponse convertToResponse(CategoryEntity entity) {
        CategoryResponse response = modelMapper.map(entity, CategoryResponse.class);
        // log.info("{} children length {}", entity.getName(),
        // entity.getChildren().size());
        // response.setChildren(convertToResponseList(entity.getChildren()));
        return response;
    }

    public List<CategoryResponse> convertToResponseList(List<CategoryEntity> list) {
        return list.stream().map(city -> convertToResponse(city)).collect(Collectors.toList());
    }


}
