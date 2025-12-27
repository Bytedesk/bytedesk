/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:40:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 16:59:00
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadInitializer implements SmartInitializingSingleton {

    // private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = ThreadPermissions.THREAD_PREFIX + permission.name();
        //     authorityRestService.createForPlatform(permissionValue);
        // }
    }

    


}
