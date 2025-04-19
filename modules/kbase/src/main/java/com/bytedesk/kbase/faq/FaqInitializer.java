/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 15:14:29
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
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.Utils;

// import com.bytedesk.core.constant.BytedeskConsts;
// import com.bytedesk.core.utils.Utils;

@Component
public class FaqInitializer implements SmartInitializingSingleton {

    @Autowired
    private FaqRestService faqService;
    
    // @Autowired
    // private ThreadPoolTaskScheduler taskScheduler;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // init();
        // 使用异步方式延迟初始化FAQ，不阻塞项目启动
        // scheduleFaqInitialization();
    }

    // private void scheduleFaqInitialization() {
    //     taskScheduler.schedule(() -> {
    //         initFaq();
    //     }, java.time.Instant.now().plusSeconds(60));
    // }

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

    // 迁移到kbaseInitializer
    public void init() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_FAQ_UID);
        // 
        faqService.importFaqs(orgUid, kbUid);
        faqService.initRelationFaqs(orgUid, kbUid);
    }
}
