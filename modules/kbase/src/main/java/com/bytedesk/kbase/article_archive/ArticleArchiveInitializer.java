/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 09:12:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ArticleArchiveInitializer implements SmartInitializingSingleton {

    // private final AuthorityRestService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        // init();
    }

    // private void init() {
    //     for (PermissionEnum permission : PermissionEnum.values()) {
    //         String permissionValue = ArticleArchivePermissions.ARTICLE_ARCHIVE_PREFIX + permission.name();
    //         if (authorityService.existsByValue(permissionValue)) {
    //             continue;
    //         }
    //         AuthorityRequest authRequest = AuthorityRequest.builder()
    //                 .name(I18Consts.I18N_PREFIX + permissionValue)
    //                 .value(permissionValue)
    //                 .description("Permission for " + permissionValue)
    //                 .build();
    //         authRequest.setUid(permissionValue.toLowerCase());
    //         authorityService.create(authRequest);
    //     }
    // }

}
