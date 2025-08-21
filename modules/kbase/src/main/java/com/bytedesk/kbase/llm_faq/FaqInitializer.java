/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 08:43:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.Utils;

@Component
public class FaqInitializer implements SmartInitializingSingleton {

    @Autowired
    private FaqRestService faqRestService;

    @Override
    public void afterSingletonsInstantiated() {
        // init();
    }

    // 迁移到kbaseInitializer
    public void init() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_FAQ_UID); // 替换到LLM_FAQ_UID
        String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID); // 替换到LLM_FAQ_UID, 合并faq和qa
        // 
        faqRestService.importFaqs(orgUid, kbUid);
        // faqService.initRelationFaqs(orgUid, kbUid);
    }
}
