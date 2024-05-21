/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-18 18:19:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService extends BaseService<Category, CategoryRequest, CategoryResponse> {
    
    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;
    
    public List<CategoryResponse> findByNullParent(String platform) {
        // 一级分类
        List<Category> firstCategoriesList = categoryRepository.findByParentAndPlatformOrderByOrderNoAsc(null, platform);

        Iterator<Category> iterator = firstCategoriesList.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            // 二级分类
            List<Category> secondCategoriesSet = categoryRepository.findByParentAndPlatformOrderByOrderNoAsc(category, platform);
            if (secondCategoriesSet != null && !secondCategoriesSet.isEmpty()) {
                category.setChildren(secondCategoriesSet);
            }
        }

        return convertToResponseList(firstCategoriesList);
    }
    
    @Override
    public Page<CategoryResponse> queryByOrg(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<CategoryResponse> queryByUser(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Category> findByUid(String uid) {
        return categoryRepository.findByUid(uid);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public CategoryResponse update(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Category save(Category entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public void deleteByUid(CategoryRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(Category entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Category entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public CategoryResponse convertToResponse(Category entity) {
        CategoryResponse response = modelMapper.map(entity, CategoryResponse.class);

        log.info("{} children length {}", entity.getName(), entity.getChildren().size());

        response.setChildren(convertToResponseList(entity.getChildren()));
        return response;
    }

    public List<CategoryResponse> convertToResponseList(List<Category> list) {
        return list.stream().map(city -> convertToResponse(city)).collect(Collectors.toList());
    }

    // public Set<CategoryResponse> convertToResponseSet(Set<Category> set) {
    //     return set.stream().map(city -> convertToResponse(city)).collect(Collectors.toSet());
    // }

    // 
    public Boolean existsByPlatform(String platform) {
        return categoryRepository.existsByPlatform(platform);
    }
    

}
