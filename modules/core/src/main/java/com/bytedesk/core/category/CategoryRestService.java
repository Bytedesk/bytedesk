/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 16:57:59
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class CategoryRestService extends BaseRestService<CategoryEntity, CategoryRequest, CategoryResponse> {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    public List<CategoryResponse> findByNullParent(String platform) {
        // 一级分类
        List<CategoryEntity> firstCategoriesList = categoryRepository.findByParentAndPlatformAndDeletedOrderByOrderAsc(null,
                platform, false);

        Iterator<CategoryEntity> iterator = firstCategoriesList.iterator();
        while (iterator.hasNext()) {
            CategoryEntity category = iterator.next();
            // 二级分类
            List<CategoryEntity> secondCategoriesSet = categoryRepository.findByParentAndPlatformAndDeletedOrderByOrderAsc(category,
                    platform, false);
            if (secondCategoriesSet != null && !secondCategoriesSet.isEmpty()) {
                category.setChildren(secondCategoriesSet);
            }
        }
        return convertToResponseList(firstCategoriesList);
    }

    @Override
    public Page<CategoryResponse> queryByOrg(CategoryRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CategoryEntity> specs = CategorySpecification.search(request);
        Page<CategoryEntity> page = categoryRepository.findAll(specs, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<CategoryResponse> queryByUser(CategoryRequest request) {
        UserEntity authUser = authService.getUser();
        if (authUser == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(authUser.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "category", key = "#uid", unless = "#result == null")
    @Override
    public Optional<CategoryEntity> findByUid(String uid) {
        return categoryRepository.findByUid(uid);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(String name, String type, String orgUid,
           String level, String platform) {
        return categoryRepository.findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeletedFalse(name, type, orgUid, level, platform);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndLevelAndPlatform(String name, String type, String level,
            String platform) {
        return categoryRepository.findByNameAndTypeAndLevelAndPlatformAndDeletedFalse(name, type, level, platform);
    }

    public Optional<CategoryEntity> findByNameAndKbUid(String name, String kbUid) {
        return categoryRepository.findByNameAndKbUidAndDeletedFalse(name, kbUid);
    }

    public List<CategoryEntity> findByKbUid(String kbUid) {
        return categoryRepository.findByKbUidAndDeletedFalse(kbUid);
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
            return convertToResponse(findByUid(request.getUid()).get());
        }
        //
        CategoryEntity category = modelMapper.map(request, CategoryEntity.class);

        // 生成uid
        if (!StringUtils.hasText(request.getUid())) {
            category.setUid(uidUtils.getUid());
        }
        // 平台
        if (StringUtils.hasText(request.getPlatform())) {
            category.setPlatform(request.getPlatform());
        }
        // 父类
        if (StringUtils.hasText(request.getParentUid())) {
            Optional<CategoryEntity> parentCategory = findByUid(request.getParentUid());
            if (parentCategory.isPresent()) {
                CategoryEntity parent = parentCategory.get();
                parent.getChildren().add(category);
                if (parent.getId() == null) {
                    save(parent);
                }
                category.setParent(parent);
            }
        }
        //
        CategoryEntity newCategory = save(category);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }
        // 
        return convertToResponse(newCategory);
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
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected CategoryEntity doSave(CategoryEntity entity) {
        // 保存父级
        if (entity.getParent() != null) {
            CategoryEntity parent = entity.getParent();
            if (parent.getId() == null) {
                CategoryEntity newParent = categoryRepository.save(parent);
                entity.setParent(newParent);
            }
        }
        return categoryRepository.save(entity);
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
    public CategoryEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CategoryEntity entity) {
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

    @Override
    public CategoryResponse queryByUid(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    public void initCategories(String orgUid) {
        // log.info("initThreadCategory");
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String category : CategoryInitData.getAllCategories()) {
            // log.info("initThreadCategory: {}", category);
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .uid(Utils.formatUid(orgUid, category))
                    .name(category)
                    .order(0)
                    .type(CategoryTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(categoryRequest);
        }
    }

}
