/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:40:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 09:07:46
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

import com.bytedesk.core.constant.BytedeskConsts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadInitializer implements SmartInitializingSingleton {

    private final ThreadRestService threadRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // 创建默认的工单分类
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        threadRestService.initThreadCategory(orgUid);
        // threadRestService.initThreadTag(orgUid);
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = ThreadPermissions.THREAD_PREFIX + permission.name();
        //     if (authorityService.existsByValue(permissionValue)) {
        //         // log.info("Thread authority {} already exists", permissionValue);
        //         continue;
        //     }
        //     AuthorityRequest authRequest = AuthorityRequest.builder()
        //             .name(I18Consts.I18N_PREFIX + permissionValue)
        //             .value(permissionValue)
        //             .description("Permission for " + permissionValue)
        //             .build();
        //     authRequest.setUid(permissionValue.toLowerCase());
        //     authorityService.create(authRequest);
        // }
    }

    

    


}
