/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:34:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 13:34:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TicketInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityService;

    private final CategoryRestService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initTicketCategory();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TicketPermissions.TICKET_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }

    private void initTicketCategory() {
        log.info("initTicketCategory");
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String category : TicketCategories.getAllCategories()) {
            // log.info("initTicketCategory: {}", category);

            // if (TicketCategories.isParentCategory(category)) { // 父类
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(category)
                    .order(0)
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.TICKET.name());
            categoryRequest.setUid(Utils.formatUid(orgUid, category));
            categoryRequest.setOrgUid(orgUid);
            categoryService.create(categoryRequest);
            // } else { // 子类
            // String parentCategory = TicketCategories.getParentCategory(category);
            // CategoryRequest categoryRequest = CategoryRequest.builder()
            // .parentUid(orgUid + parentCategory)
            // .name(category)
            // .order(0)
            // .level(LevelEnum.ORGANIZATION.name())
            // .platform(BytedeskConsts.PLATFORM_BYTEDESK)
            // .build();
            // categoryRequest.setType(CategoryTypeEnum.TICKET.name());
            // categoryRequest.setUid(orgUid + category);
            // categoryRequest.setOrgUid(orgUid);
            // categoryService.create(categoryRequest);
            // }
        }
    }

}
