/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 13:28:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

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
import org.springframework.data.domain.PageImpl;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryRestService extends BaseRestService<CategoryEntity, CategoryRequest, CategoryResponse> {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    public Page<CategoryResponse> queryByOrg(CategoryRequest request) {
        log.info("queryByOrg started - pageNumber: {}, pageSize: {}", 
                request.getPageable().getPageNumber(), request.getPageable().getPageSize());
        
        Pageable pageable = request.getPageable();
        
        // 使用专门的根分类查询 Specification
        Specification<CategoryEntity> rootOnlySpec = CategorySpecification.search(request, authService);

        // 第一步：获取所有符合条件的根节点实体
        List<CategoryEntity> allRootEntities = categoryRepository.findAll(rootOnlySpec);
        log.info("Step 1: Found {} entities from database", allRootEntities.size());
        
        // 打印所有原始实体的详细信息
        for (CategoryEntity entity : allRootEntities) {
            String parentInfo = entity.getParent() != null ? 
                    "parent: " + entity.getParent().getUid() : "parent: null";
            log.info("Step 1: Entity uid: {}, name: {}, {}", 
                    entity.getUid(), entity.getName(), parentInfo);
        }
        
        List<String> allUids = allRootEntities.stream()
                .filter(Objects::nonNull)
                .map(CategoryEntity::getUid)
                .collect(Collectors.toList());
        log.info("Step 1: All UIDs from database: {}", allUids);
        
        // 第二步：手动去重并分页
        List<String> uniqueUids = allRootEntities.stream()
                .filter(Objects::nonNull)
                .filter(entity -> StringUtils.hasText(entity.getUid()))
                .map(CategoryEntity::getUid)
                .distinct()
                .collect(Collectors.toList());
        log.debug("Step 2: Unique UIDs after deduplication: {}", uniqueUids);
        
        // 第三步：手动分页处理
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min((pageNumber + 1) * pageSize, uniqueUids.size());
        log.debug("Step 3: Pagination - startIndex: {}, endIndex: {}, total: {}", 
                startIndex, endIndex, uniqueUids.size());
        
        List<CategoryResponse> content;
        if (startIndex >= uniqueUids.size()) {
            content = List.of();
            log.debug("Step 3: No content - startIndex >= uniqueUids.size()");
        } else {
            List<String> pagedUids = uniqueUids.subList(startIndex, endIndex);
            log.debug("Step 3: Paged UIDs: {}", pagedUids);
            
            List<CategoryEntity> entities = pagedUids.stream()
                    .map(this::findByUid)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(entity -> entity.getParent() == null) // 再次确保只处理根分类
                    .collect(Collectors.toList());
            log.debug("Step 3: Found {} root entities after filtering", entities.size());
            
            content = entities.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            log.debug("Step 3: Converted to {} responses", content.size());
            
            // 打印最终响应的 uid 列表
            List<String> responseUids = content.stream()
                    .map(CategoryResponse::getUid)
                    .collect(Collectors.toList());
            log.debug("Step 3: Final response UIDs: {}", responseUids);
        }

        log.debug("queryByOrg completed - returning {} items", content.size());
        return new PageImpl<>(content, pageable, uniqueUids.size());
    }

    @Cacheable(value = "category", key = "#uid", unless = "#result == null")
    @Override
    public Optional<CategoryEntity> findByUid(String uid) {
        return categoryRepository.findByUid(uid);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(String name, String type,
            String orgUid, String level, String platform) {
        return categoryRepository.findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeletedFalse(name, type, orgUid,
                level, platform);
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
        // 
        UserEntity user = authService.getCurrentUser();
        if (user != null) {
            category.setUserUid(user.getUid());
        }
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
            if (!parentOpt.isPresent()) {
                throw new RuntimeException("parent category not found: " + parentUid);
            }
            CategoryEntity parent = parentOpt.get();
            // 维护双向关联
            category.setParent(parent);
            if (parent.getChildren() != null && !parent.getChildren().contains(category)) {
                parent.getChildren().add(category);
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
        // 更新父类：若传入 parentUid，则关联到该父类；不存在则报错，不创建占位
        if (StringUtils.hasText(request.getParentUid())) {
            String parentUid = request.getParentUid();
            // 若父类发生变化或当前无父类
            if (entity.getParent() == null || !parentUid.equals(entity.getParent().getUid())) {
                Optional<CategoryEntity> parentOpt = findByUid(parentUid);
                if (!parentOpt.isPresent()) {
                    throw new RuntimeException("parent category not found: " + parentUid);
                }
                CategoryEntity parent = parentOpt.get();
                // 解除旧父子关系
                if (entity.getParent() != null) {
                    entity.getParent().getChildren().remove(entity);
                }
                // 维护新父子关系
                entity.setParent(parent);
                if (parent.getChildren() != null && !parent.getChildren().contains(entity)) {
                    parent.getChildren().add(entity);
                }
            }
        }
        //
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
    public CategoryEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            CategoryEntity entity) {
        try {
            // 重试机制：重新加载实体并应用更改，然后再次尝试保存
            Optional<CategoryEntity> freshEntityOpt = findByUid(entity.getUid());
            if (freshEntityOpt.isPresent()) {
                CategoryEntity freshEntity = freshEntityOpt.get();
                // 这里可以根据需要合并更改
                freshEntity.setName(entity.getName());
                freshEntity.setType(entity.getType());
                // freshEntity.setOrder(entity.getOrder());
                freshEntity.setKbUid(entity.getKbUid());
                // 其他字段...
                return save(freshEntity);
            } else {
                throw new RuntimeException("Entity not found for UID: " + entity.getUid());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle optimistic locking failure", ex);
        }
    }

    @Override
    public CategoryResponse convertToResponse(CategoryEntity entity) {
        if (entity == null) {
            log.debug("convertToResponse: entity is null");
            return null;
        }
        log.debug("convertToResponse: Processing entity with uid: {}, name: {}", 
                entity.getUid(), entity.getName());
        
        // 直接转换当前实体，不再强制返回根节点
        // 这样可以避免子分类导致重复返回父分类的问题
        CategoryResponse response = convertToResponseRecursive(entity, new HashSet<>());
        log.debug("convertToResponse: Returning response with uid: {}, name: {}", 
                response.getUid(), response.getName());
        return response;
    }

    private CategoryResponse convertToResponseRecursive(CategoryEntity entity, Set<String> visited) {
        CategoryResponse response = modelMapper.map(entity, CategoryResponse.class);
        // 设置父级 uid
        response.setParentUid(entity.getParent() != null ? entity.getParent().getUid() : null);
        // 防止循环引用
        if (StringUtils.hasText(entity.getUid())) {
            visited.add(entity.getUid());
        }
        // 显示子级（按 order 升序，其次按名称）
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            Comparator<CategoryEntity> byOrder = Comparator
                    .comparingInt(c -> c.getOrder() == null ? Integer.MAX_VALUE : c.getOrder());
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

    @Override
    protected Specification<CategoryEntity> createSpecification(CategoryRequest request) {
        return CategorySpecification.search(request, authService);
    }

    @Override
    protected Page<CategoryEntity> executePageQuery(Specification<CategoryEntity> spec, Pageable pageable) {
        return categoryRepository.findAll(spec, pageable);
    }

    public void initCategories(String orgUid) {
        // log.debug("initThreadCategory");
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String category : CategoryInitData.getAllCategories()) {
            // log.debug("initThreadCategory: {}", category);
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
