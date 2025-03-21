/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:43:37
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

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FaqInitializer implements SmartInitializingSingleton {

    private final FaqRestService faqService;

    // private final AuthorityRestService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initFaq();
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = FaqPermissions.FAQ_PREFIX + permission.name();
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

    private void initFaq() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_FAQ_UID);
        // 
        faqService.importFaqs(orgUid, kbUid);
        faqService.initRelationFaqs(orgUid, kbUid);
    }
    

}
