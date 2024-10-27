/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:18:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooService extends BaseService<TabooEntity, TabooRequest, TabooResponse> {

    private final TabooRepository tabooRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    @Override
    public Page<TabooResponse> queryByOrg(TabooRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<TabooEntity> specification = TabooSpecification.search(request);
        
        Page<TabooEntity> page = tabooRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TabooResponse> queryByUser(TabooRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<TabooEntity> findByUid(String uid) {
        return tabooRepository.findByUid(uid);
    }

    @Override
    public TabooResponse create(TabooRequest request) {
        
        TabooEntity taboo = modelMapper.map(request, TabooEntity.class);
        taboo.setUid(uidUtils.getCacheSerialUid());

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

    @Override
    public TabooEntity save(TabooEntity entity) {
        try {
            return tabooRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void save(List<TabooEntity> entities) {
        tabooRepository.saveAll(entities);
    }

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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TabooEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TabooResponse convertToResponse(TabooEntity entity) {
        return modelMapper.map(entity, TabooResponse.class);
    }

    public TabooExcel convertToExcel(TabooResponse response) {
        return modelMapper.map(response, TabooExcel.class);
    }

    public TabooEntity convertExcelToTaboo(TabooExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Taboo.class); // String categoryUid,
        TabooEntity taboo = TabooEntity.builder().build();
        taboo.setUid(uidUtils.getCacheSerialUid());
        taboo.setContent(excel.getContent());
        // 
        // taboo.setCategoryUid(categoryUid);
         Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            taboo.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_TABOO);
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            taboo.setCategoryUid(categoryResponse.getUid());
        }
        // 
        taboo.setKbUid(kbUid);
        taboo.setOrgUid(orgUid);

        return taboo;
    }
    
}
