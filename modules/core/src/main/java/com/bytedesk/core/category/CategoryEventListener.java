/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-21 14:28:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 12:58:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryEventListener {

    private final CategoryRestService categoryService;

    @Order(2)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("faq - organization created: {}", organization.getName());
        //
        CategoryRequest categoryFaqDemoRequest1 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest1.setType(CategoryTypeEnum.FAQ.name());
        categoryFaqDemoRequest1.setUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_1));
        categoryFaqDemoRequest1.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest1);
        //
        CategoryRequest categoryFaqDemoRequest2 = CategoryRequest.builder()
                .name(I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orderNo(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                // .orgUid(orgUid)
                .build();
        categoryFaqDemoRequest2.setType(CategoryTypeEnum.FAQ.name());
        categoryFaqDemoRequest2.setUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_2));
        categoryFaqDemoRequest2.setOrgUid(orgUid);
        categoryService.create(categoryFaqDemoRequest2);

    }

    
}
