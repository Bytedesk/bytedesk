/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

/**
 * 注册 PUSH_ANDROID_* 平台级权限（对称 APNS_PUSH_*）。
 * 推送记录为系统生成，无需初始化默认数据。
 */
@Component
@AllArgsConstructor
public class PushAndroidInitializer implements SmartInitializingSingleton {

    // private final PushAndroidRestService pushAndroidRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = PushAndroidPermissions.PUSH_ANDROID_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

}
