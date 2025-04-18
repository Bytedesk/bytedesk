/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 12:53:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QaInitializer implements SmartInitializingSingleton {

    // private final QaRestService qaService;

    // private final AuthorityRestService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // initQa();
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = QaPermissions.FAQ_PREFIX + permission.name();
        //     if (authorityService.existsByValue(permissionValue)) {
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

    // private void initQa() {
    //     String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
    //     String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_FAQ_UID);
    //     // 
        // qaService.importQas(orgUid, kbUid);
        // qaService.initRelationQas(orgUid, kbUid);
    // }
    

}
