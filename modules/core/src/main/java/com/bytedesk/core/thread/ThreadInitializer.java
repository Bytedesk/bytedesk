/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:40:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 09:00:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.tag.TagRequest;
import com.bytedesk.core.tag.TagRestService;
import com.bytedesk.core.tag.TagTypeEnum;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityService;

    private final CategoryRestService categoryService;

    private final TagRestService tagRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initThreadCategory();
        initThreadTag();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = ThreadPermissions.THREAD_PREFIX + permission.name();
            if (authorityService.existsByValue(permissionValue)) {
                // log.info("Thread authority {} already exists", permissionValue);
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

    private void initThreadCategory() {
        log.info("initThreadCategory");
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String category : ThreadCategories.getAllCategories()) {
            // log.info("initThreadCategory: {}", category);
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(category)
                    .order(0)
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.THREAD.name());
            categoryRequest.setUid(Utils.formatUid(orgUid, category));
            categoryRequest.setOrgUid(orgUid);
            categoryService.create(categoryRequest);
        }
    }


    private void initThreadTag() {
        log.info("initThreadTag");
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String tag : ThreadTags.getAllTags()) {
            // log.info("initThreadCategory: {}", category);
            TagRequest tagRequest = TagRequest.builder()
                    .name(tag)
                    .order(0)
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .build();
            tagRequest.setType(TagTypeEnum.THREAD.name());
            tagRequest.setUid(Utils.formatUid(orgUid, tag));
            tagRequest.setOrgUid(orgUid);
            tagRestService.create(tagRequest);
        }
    }
}
