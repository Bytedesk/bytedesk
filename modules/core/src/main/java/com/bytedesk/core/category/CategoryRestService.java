/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-20 15:41:16
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;
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

    @Override
    protected Specification<CategoryEntity> createSpecification(CategoryRequest request) {
        return CategorySpecification.search(request, authService);
    }

    @Override
    protected Page<CategoryEntity> executePageQuery(Specification<CategoryEntity> spec, Pageable pageable) {
        return categoryRepository.findAll(spec, pageable);
    }

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
        // 父类：若不存在则创建占位父分类
        if (StringUtils.hasText(request.getParentUid())) {
            String parentUid = request.getParentUid();
            Optional<CategoryEntity> parentOpt = findByUid(parentUid);
            CategoryEntity parent = parentOpt.orElseGet(() ->
                CategoryEntity.builder()
                    .uid(parentUid)
                    .name(parentUid) // 占位名称使用 uid，后续可通过更新完善
                    .type(category.getType())
                    .platform(category.getPlatform())
                    .orgUid(category.getOrgUid())
                    .level(category.getLevel())
                    .kbUid(category.getKbUid())
                    .order(0)
                    .build()
            );
            // 维护双向关联
            category.setParent(parent);
            if (!parent.getChildren().contains(category)) {
                parent.getChildren().add(category);
            }
        }

        // 子类：根据 childrenUids 创建缺失子类并建立父子关系
        if (request.getChildrenUids() != null && !request.getChildrenUids().isEmpty()) {
            for (String childUid : request.getChildrenUids()) {
                if (!StringUtils.hasText(childUid)) {
                    continue;
                }
                // 避免自引用
                if (childUid.equals(category.getUid())) {
                    continue;
                }
                Optional<CategoryEntity> childOpt = findByUid(childUid);
                CategoryEntity child = childOpt.orElseGet(() ->
                    CategoryEntity.builder()
                        .uid(childUid)
                        .name(childUid) // 占位名称使用 uid
                        .type(category.getType())
                        .platform(category.getPlatform())
                        .orgUid(category.getOrgUid())
                        .level(category.getLevel())
                        .kbUid(category.getKbUid())
                        .order(0)
                        .build()
                );
                // 维护双向关联
                child.setParent(category);
                if (!category.getChildren().contains(child)) {
                    category.getChildren().add(child);
                }
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
        // 更新父类：若传入 parentUid，则关联到该父类；不存在时自动创建
        if (StringUtils.hasText(request.getParentUid())) {
            String parentUid = request.getParentUid();
            // 若父类发生变化或当前无父类
            if (entity.getParent() == null || !parentUid.equals(entity.getParent().getUid())) {
                Optional<CategoryEntity> parentOpt = findByUid(parentUid);
                CategoryEntity parent = parentOpt.orElseGet(() ->
                    CategoryEntity.builder()
                        .uid(parentUid)
                        .name(parentUid)
                        .type(entity.getType())
                        .platform(StringUtils.hasText(request.getPlatform()) ? request.getPlatform() : entity.getPlatform())
                        .orgUid(StringUtils.hasText(request.getOrgUid()) ? request.getOrgUid() : entity.getOrgUid())
                        .level(StringUtils.hasText(request.getLevel()) ? request.getLevel() : entity.getLevel())
                        .kbUid(StringUtils.hasText(request.getKbUid()) ? request.getKbUid() : entity.getKbUid())
                        .order(0)
                        .build()
                );
                // 解除旧父子关系
                if (entity.getParent() != null) {
                    entity.getParent().getChildren().remove(entity);
                }
                // 维护新父子关系
                entity.setParent(parent);
                if (!parent.getChildren().contains(entity)) {
                    parent.getChildren().add(entity);
                }
            }
        }

        // 更新子类：仅添加/关联传入的 childrenUids，不移除未包含的子类（避免意外删除）
        if (request.getChildrenUids() != null && !request.getChildrenUids().isEmpty()) {
            for (String childUid : request.getChildrenUids()) {
                if (!StringUtils.hasText(childUid) || childUid.equals(entity.getUid())) {
                    continue;
                }
                Optional<CategoryEntity> childOpt = findByUid(childUid);
                CategoryEntity child = childOpt.orElseGet(() ->
                    CategoryEntity.builder()
                        .uid(childUid)
                        .name(childUid)
                        .type(entity.getType())
                        .platform(StringUtils.hasText(request.getPlatform()) ? request.getPlatform() : entity.getPlatform())
                        .orgUid(StringUtils.hasText(request.getOrgUid()) ? request.getOrgUid() : entity.getOrgUid())
                        .level(StringUtils.hasText(request.getLevel()) ? request.getLevel() : entity.getLevel())
                        .kbUid(StringUtils.hasText(request.getKbUid()) ? request.getKbUid() : entity.getKbUid())
                        .order(0)
                        .build()
                );
                child.setParent(entity);
                if (!entity.getChildren().contains(child)) {
                    entity.getChildren().add(child);
                }
            }
        }
        CategoryEntity newCategory = save(entity);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }

        return convertToResponse(newCategory);
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
        return convertToResponseRecursive(entity, new HashSet<>());
    }

    private CategoryResponse convertToResponseRecursive(CategoryEntity entity, Set<String> visited) {
        CategoryResponse response = modelMapper.map(entity, CategoryResponse.class);
        // 设置父级 uid
        response.setParentUid(entity.getParent() != null ? entity.getParent().getUid() : null);
        // 防止循环引用
        if (StringUtils.hasText(entity.getUid())) {
            visited.add(entity.getUid());
        }
        // 映射子级（按 order 升序，其次按名称）
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            Comparator<CategoryEntity> byOrder = Comparator.comparingInt(c -> c.getOrder() == null ? Integer.MAX_VALUE : c.getOrder());
            List<CategoryResponse> childResponses = entity.getChildren().stream()
                    .filter(Objects::nonNull)
                    .filter(c -> c.getUid() == null || !visited.contains(c.getUid()))
                    .sorted(byOrder.thenComparing(c -> StringUtils.hasText(c.getName()) ? c.getName() : ""))
                    .map(c -> convertToResponseRecursive(c, visited))
                    .collect(Collectors.toList());
            if (!childResponses.isEmpty()) {
                response.setChildren(childResponses);
            }
        }
        return response;
    }

    public List<CategoryResponse> convertToResponseList(List<CategoryEntity> list) {
        return list.stream().map(city -> convertToResponse(city)).collect(Collectors.toList());
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
