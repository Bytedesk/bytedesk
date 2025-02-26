/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 12:55:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityService;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FaqInitializer implements SmartInitializingSingleton {

    private final FaqRestService faqService;

    private final AuthorityService authorityService;

    private final CategoryRestService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initFaqCategory();
        initFaq();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = FaqPermissions.FAQ_PREFIX + permission.name();
            if (authorityService.existsByValue(permissionValue)) {
                continue;
            }
            AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .build();
            authRequest.setUid(permissionValue.toLowerCase());
            authorityService.create(authRequest);
        }
    }

    private void initFaqCategory() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String faqCategoryDemoUid = Utils.formatUid(orgUid, FaqConsts.FAQ_CATEGORY_DEMO_UID_1);
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .build();
        categoryFaqDemoRequest1.setType(CategoryTypeEnum.FAQ.name());
        categoryFaqDemoRequest1.setUid(faqCategoryDemoUid);
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest1);
        //
        String faqCategoryDemoUid2 = Utils.formatUid(orgUid, FaqConsts.FAQ_CATEGORY_DEMO_UID_2);
        CategoryRequest categoryFaqDemoRequest2 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .build();
        categoryFaqDemoRequest2.setType(CategoryTypeEnum.FAQ.name());
        categoryFaqDemoRequest2.setUid(faqCategoryDemoUid2);
        categoryFaqDemoRequest2.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest2);
    }

    // @PostConstruct
    private void initFaq() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String faqDemoUid = Utils.formatUid(orgUid, FaqConsts.FAQ_DEMO_UID_1);
        if (!faqService.existsByUid(faqDemoUid)) {
            FaqRequest faqDemo1 = FaqRequest.builder()
                    .question(I18Consts.I18N_FAQ_DEMO_QUESTION_1)
                    .answer(I18Consts.I18N_FAQ_DEMO_ANSWER_1)
                    .type(MessageTypeEnum.TEXT.name())
                    .categoryUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_1))
                    .build();
            faqDemo1.setUid(faqDemoUid);
            faqDemo1.setOrgUid(orgUid);
            faqService.create(faqDemo1);
        }
        //
        String faqDemoUid2 = Utils.formatUid(orgUid, FaqConsts.FAQ_DEMO_UID_2);
        if (!faqService.existsByUid(faqDemoUid2)) {
            FaqRequest faqDemo2 = FaqRequest.builder()
                    .question(I18Consts.I18N_FAQ_DEMO_QUESTION_2)
                    .answer(I18Consts.I18N_FAQ_DEMO_ANSWER_2)
                    .type(MessageTypeEnum.IMAGE.name())
                    .categoryUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_2))
                    .build();
            faqDemo2.setUid(faqDemoUid2);
            faqDemo2.setOrgUid(orgUid);
            faqService.create(faqDemo2);
        }
    }

}
