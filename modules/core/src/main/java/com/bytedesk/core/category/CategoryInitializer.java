/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 10:46:55
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

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CategoryInitializer implements SmartInitializingSingleton {

    private final CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {

        if (categoryRepository.count() > 0) {
            return;
        }

        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        // init quick reply categories
        CategoryRequest categoryContact = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT)
                .orderNo(0)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryContact.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryContact.setOrgUid(orgUid);
        categoryService.create(categoryContact);

        CategoryRequest categoryThanks = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS)
                .orderNo(1)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryThanks.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryThanks.setOrgUid(orgUid);
        categoryService.create(categoryThanks);

        CategoryRequest categoryWelcome = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME)
                .orderNo(2)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryWelcome.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryWelcome.setOrgUid(orgUid);
        categoryService.create(categoryWelcome);

        CategoryRequest categoryBye = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE)
                .orderNo(3)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryBye.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
        // 此处设置orgUid方便超级管理员加载
        categoryBye.setOrgUid(orgUid);
        categoryService.create(categoryBye);

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .build();
        categoryFaqDemoRequest1.setType(CategoryConsts.CATEGORY_TYPE_FAQ);
        categoryFaqDemoRequest1.setUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest1);
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
        categoryService.create(categoryFaqDemoRequest2);
    }
    
}
