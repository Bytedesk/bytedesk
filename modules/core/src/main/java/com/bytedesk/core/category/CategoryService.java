/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-24 09:36:10
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class CategoryService extends BaseService<Category, CategoryRequest, CategoryResponse> {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public List<CategoryResponse> findByNullParent(PlatformEnum platform) {
        // 一级分类
        List<Category> firstCategoriesList = categoryRepository.findByParentAndPlatformOrderByOrderNoAsc(null,
                platform);

        Iterator<Category> iterator = firstCategoriesList.iterator();
        while (iterator.hasNext()) {
            Category category = iterator.next();
            // 二级分类
            List<Category> secondCategoriesSet = categoryRepository.findByParentAndPlatformOrderByOrderNoAsc(category,
                    platform);
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

        Specification<Category> specs = CategorySpecification.search(request);

        Page<Category> page = categoryRepository.findAll(specs, pageable);

        return page.map(this::convertToResponse);
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

        Category category = modelMapper.map(request, Category.class);
        if (!StringUtils.hasText(request.getUid())) {
            category.setUid(uidUtils.getDefaultSerialUid());
        }
        category.setPlatform(PlatformEnum.fromValue(request.getPlatform()));

        Category newCategory = save(category);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }

        return convertToResponse(newCategory);
    }

    public Optional<Category> findByNameAndTypeAndOrgUidAndPlatform(String name, String type, String orgUid,
            PlatformEnum platform) {
        return categoryRepository.findByNameAndTypeAndOrgUidAndPlatform(name, type, orgUid, platform);
    }
    
    public Optional<Category> findByNameAndTypeAndLevelAndPlatform(String name, String type, LevelEnum level,
            PlatformEnum platform) {
        return categoryRepository.findByNameAndTypeAndLevelAndPlatform(name, type, level, platform);
    }

    @Override
    public CategoryResponse update(CategoryRequest request) {
        Optional<Category> category = findByUid(request.getUid());
        if (!category.isPresent()) {
            throw new RuntimeException("category not found");
        }

        Category entity = category.get();
        // modelMapper.map(request, entity);
        entity.setName(request.getName());
        entity.setIcon(request.getIcon());
        entity.setType(request.getType());
        // entity.setPlatform(request.getPlatform());

        // TODO: children

        Category newCategory = save(entity);
        if (newCategory == null) {
            throw new RuntimeException("category save error");
        }

        return convertToResponse(newCategory);
    }

    @Override
    public Category save(Category entity) {
        try {
            return categoryRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Category> category = findByUid(uid);
        if (category.isPresent()) {
            category.get().setDeleted(true);
            save(category.get());
        }
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
        // log.info("{} children length {}", entity.getName(), entity.getChildren().size());
        // response.setChildren(convertToResponseList(entity.getChildren()));
        return response;
    }

    public List<CategoryResponse> convertToResponseList(List<Category> list) {
        return list.stream().map(city -> convertToResponse(city)).collect(Collectors.toList());
    }

    // 
    public void initData() {
        if (categoryRepository.count() > 0) {
            return;
        }

        // init quick reply categories
        CategoryRequest categoryContact = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT)
                .orderNo(0)
                .level(LevelEnum.PLATFORM)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryContact.setType(CategoryConsts.CATEGORY_TYPE_QUICK_REPLY);
        create(categoryContact);

        CategoryRequest categoryThanks = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS)
                .orderNo(1)
                .level(LevelEnum.PLATFORM)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryThanks.setType(CategoryConsts.CATEGORY_TYPE_QUICK_REPLY);
        create(categoryThanks);

        CategoryRequest categoryWelcome = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME)
                .orderNo(2)
                // .orgUid(orgUid)
                .level(LevelEnum.PLATFORM)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                .build();
        categoryWelcome.setType(CategoryConsts.CATEGORY_TYPE_QUICK_REPLY);
        create(categoryWelcome);

        CategoryRequest categoryBye = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE)
                .orderNo(3)
                // .orgUid(orgUid)
                .level(LevelEnum.PLATFORM)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                .build();
        categoryBye.setType(CategoryConsts.CATEGORY_TYPE_QUICK_REPLY);
        create(categoryBye);

        //
        String orgUid = UserConsts.DEFAULT_ORGANIZATION_UID;
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGNIZATION)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest1.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest1.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        create(categoryFaqDemoRequest1);
        //
        CategoryRequest categoryFaqDemoRequest2 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orderNo(0)
                .level(LevelEnum.ORGNIZATION)
                .platform(BdConstants.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest2.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest2.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        create(categoryFaqDemoRequest2);
    }


}
