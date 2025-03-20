/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:56:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class KbaseInitializer implements SmartInitializingSingleton {

    private final KbaseRestService kbaseService;

    private final QuickReplyRestService quickReplyRestService;

    private final AuthorityRestService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        initKbase();
        initPermissions();
    }

    public void initKbase() {
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // 初始化知识库
        kbaseService.initKbase(orgUid);
        // 初始化快捷回复分类
        quickReplyRestService.initQuickReplyCategory(orgUid);
        // 初始化快捷回复
        quickReplyRestService.initQuickReply(orgUid);
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = KbasePermissions.KBASE_PREFIX + permission.name();
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
    
}
