/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 22:28:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AutoReplyFixedInitializer implements SmartInitializingSingleton {

    private final AutoReplyFixedRestService autoReplyFixedRestService;

    @Override
    public void afterSingletonsInstantiated() {
        // init();
    }

    // 迁移到kbaseInitializer
    public void init() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_AUTOREPLY_FIXED_UID);
        log.info("初始化固定自动回复数据...");
        // 初始化固定自动回复数据
        autoReplyFixedRestService.initData(orgUid);
    }
    
}
