/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:22:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-24 18:33:36
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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class CategoryService extends BaseService<CategoryEntity, CategoryRequest, CategoryResponse> {

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

    @Override
    public CategoryResponse create(CategoryRequest request) {

        CategoryEntity category = modelMapper.map(request, CategoryEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            category.setUid(uidUtils.getDefaultSerialUid());
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

    public Optional<CategoryEntity> findByNameAndTypeAndOrgUidAndPlatform(String name, String type, String orgUid,
            String platform) {
        return categoryRepository.findByNameAndTypeAndOrgUidAndPlatformAndDeleted(name, type, orgUid, platform, false);
    }

    public Optional<CategoryEntity> findByNameAndTypeAndLevelAndPlatform(String name, String type, LevelEnum level,
            PlatformEnum platform) {
        return categoryRepository.findByNameAndTypeAndLevelAndPlatformAndDeleted(name, type, level.name(), platform.name(), false);
    }

    public Optional<CategoryEntity> findByNameAndKbUid(String name, String kbUid) {
        return categoryRepository.findByNameAndKbUidAndDeleted(name, kbUid, false);
    }

    public List<CategoryEntity> findByKbUid(String kbUid) {
        return categoryRepository.findByKbUidAndDeleted(kbUid, false);
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

    //
    public void initData() {

        if (categoryRepository.count() > 0) {
            return;
        }

        //
        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        // init quick reply categories
        CategoryRequest categoryContact = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT)
                .orderNo(0)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_UID)
                .build();
        categoryContact.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryContact.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        create(categoryContact);

        CategoryRequest categoryThanks = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS)
                .orderNo(1)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_UID)
                .build();
        categoryThanks.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryThanks.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        create(categoryThanks);

        CategoryRequest categoryWelcome = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME)
                .orderNo(2)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_UID)
                .build();
        categoryWelcome.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryWelcome.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        create(categoryWelcome);

        CategoryRequest categoryBye = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE)
                .orderNo(3)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_UID)
                .build();
        categoryBye.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryBye.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        create(categoryBye);

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .build();
        categoryFaqDemoRequest1.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest1.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        create(categoryFaqDemoRequest1);
        //
        CategoryRequest categoryFaqDemoRequest2 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .build();
        categoryFaqDemoRequest2.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest2.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2);
        categoryFaqDemoRequest2.setOrgUid(orgUid);
        create(categoryFaqDemoRequest2);
    }

}
