/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 22:27:51
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
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.kbase.auto_reply.fixed.AutoReplyFixedInitializer;
import com.bytedesk.kbase.auto_reply.keyword.AutoReplyKeywordInitializer;
import com.bytedesk.kbase.faq.FaqInitializer;
import com.bytedesk.kbase.file.FileInitializer;
import com.bytedesk.kbase.llm_chunk.ChunkInitializer;
import com.bytedesk.kbase.llm_text.TextInitializer;
import com.bytedesk.kbase.quick_reply.QuickReplyInitializer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KbaseInitializer implements SmartInitializingSingleton {

    private final KbaseRestService kbaseRestService;

    private final AuthorityRestService authorityRestService;

    private final FaqInitializer faqInitializer;

    private final QuickReplyInitializer quickReplyInitializer;

    private final TextInitializer textInitializer;

    private final FileInitializer fileInitializer;

    private final ChunkInitializer chunkInitializer;

    private final AutoReplyFixedInitializer autoReplyFixedInitializer;

    private final AutoReplyKeywordInitializer autoReplyKeywordInitializer;

    @Override
    public void afterSingletonsInstantiated() {
        log.info("KbaseInitializer initialization started...");

        // 初始化权限
        initPermissions();
        // 初始化知识库
        initKbase();
        // 初始化FAQ
        faqInitializer.init();
        // 初始化快捷回复
        quickReplyInitializer.init();
        
        // 初始化固定自动回复
        autoReplyFixedInitializer.init();
        // 初始化关键词自动回复
        autoReplyKeywordInitializer.init();

        // 初始化文本
        textInitializer.init();
        // 初始化文件
        fileInitializer.init();
        // 初始化分片
        chunkInitializer.init();
    }

    public void initKbase() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // 初始化知识库
        kbaseRestService.initKbase(orgUid);
    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = KbasePermissions.KBASE_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
    
}
