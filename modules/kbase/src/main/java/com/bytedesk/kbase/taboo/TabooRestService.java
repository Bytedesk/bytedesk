/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:25:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.Optional;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooRestService extends BaseRestServiceWithExport<TabooEntity, TabooRequest, TabooResponse, TabooExcel> {

    private final TabooRepository tabooRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    @Override
    protected Specification<TabooEntity> createSpecification(TabooRequest request) {
        return TabooSpecification.search(request, authService);
    }

    @Override
    protected Page<TabooEntity> executePageQuery(Specification<TabooEntity> spec, Pageable pageable) {
        return tabooRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "taboo", key="#uid", unless = "#result == null")
    @Override
    public Optional<TabooEntity> findByUid(String uid) {
        return tabooRepository.findByUid(uid);
    }

    @Override
    public TabooResponse create(TabooRequest request) {
        // 
        TabooEntity taboo = modelMapper.map(request, TabooEntity.class);
        taboo.setUid(uidUtils.getUid());
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
        if (categoryOptional.isPresent()) {
            taboo.setCategoryUid(categoryOptional.get().getUid());
        }
        // 
        TabooEntity savedTaboo = save(taboo);
        if (savedTaboo == null) {
            throw new RuntimeException("create taboo failed");
        }
        return convertToResponse(savedTaboo);
    }

    @Override
    public TabooResponse update(TabooRequest request) {
        
        Optional<TabooEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TabooEntity taboo = optional.get();
            taboo.setContent(request.getContent());
            taboo.setReply(request.getReply());
            taboo.setSynonymList(request.getSynonymList());
            taboo.setTagList(request.getTagList());
            // categoryUid
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
            if (categoryOptional.isPresent()) {
                taboo.setCategoryUid(categoryOptional.get().getUid());
            }
            // 
            TabooEntity savedTaboo = save(taboo);
            if (savedTaboo == null) {
                throw new RuntimeException("create taboo failed");
            }
            return convertToResponse(savedTaboo);
        } else {
            throw new RuntimeException("update taboo failed");
        }
    }

    @CachePut(value = "taboo", key = "#entity.uid")
    protected TabooEntity doSave(TabooEntity entity) {
        return tabooRepository.save(entity);
    }

    @Override
    public TabooEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TabooEntity entity) {
        try {
            Optional<TabooEntity> latest = tabooRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TabooEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return tabooRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<TabooEntity> entities) {
        tabooRepository.saveAll(entities);
    }

    // 启用/禁用敏感词
    public TabooResponse enable(TabooRequest request) {
        Optional<TabooEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TabooEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            TabooEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update Taboo");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("taboo not found");
        }
    }

    @CacheEvict(value = "taboo", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<TabooEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            TabooEntity taboo = optional.get();
            taboo.setDeleted(true);
            save(taboo);
        }
    }

    @Override
    public void delete(TabooRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public TabooResponse convertToResponse(TabooEntity entity) {
        return modelMapper.map(entity, TabooResponse.class);
    }

    @Override
    public TabooExcel convertToExcel(TabooEntity response) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(response.getCategoryUid());
        TabooExcel tabooExcel = modelMapper.map(response, TabooExcel.class);
        tabooExcel.setCategory(categoryOptional.get().getName());
        return tabooExcel;
    }

    public TabooEntity convertExcelToTaboo(TabooExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Taboo.class); // String categoryUid,
        TabooEntity taboo = TabooEntity.builder().build();
        taboo.setUid(uidUtils.getUid());
        taboo.setContent(excel.getContent());
        // 
        // taboo.setCategoryUid(categoryUid);
         Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            taboo.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.TABOO.name());
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            taboo.setCategoryUid(categoryResponse.getUid());
        }
        // 
        taboo.setKbUid(kbUid);
        taboo.setOrgUid(orgUid);

        return taboo;
    }
    
}
