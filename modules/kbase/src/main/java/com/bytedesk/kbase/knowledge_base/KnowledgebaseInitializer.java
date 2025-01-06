/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 10:47:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class KnowledgebaseInitializer implements SmartInitializingSingleton {

    private final KnowledgebaseRepository knowledgebaseRepository;

    private final KnowledgebaseService knowledgebaseService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {
        
        if (knowledgebaseRepository.count() > 0) {
            return;
        }
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        KnowledgebaseRequest kownledgebaseRequestQuickReplyPlatform = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_PLATFORM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .level(LevelEnum.PLATFORM.name())
                .build();
        kownledgebaseRequestQuickReplyPlatform.setUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID);
        kownledgebaseRequestQuickReplyPlatform.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        // 方便超级管理员加载，避免重新写一个接口拉取
        kownledgebaseRequestQuickReplyPlatform.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestQuickReplyPlatform);
        
        //
        KnowledgebaseRequest kownledgebaseRequestHelpdoc = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_HELPCENTER_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestHelpdoc.setUid(BytedeskConsts.DEFAULT_KB_HELPCENTER_UID);
        kownledgebaseRequestHelpdoc.setType(KnowledgebaseTypeEnum.HELPCENTER.name());
        kownledgebaseRequestHelpdoc.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestHelpdoc);
        //
        KnowledgebaseRequest kownledgebaseRequestLlm = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_LLM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestLlm.setType(KnowledgebaseTypeEnum.LLM.name());
        kownledgebaseRequestLlm.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestLlm);
        //
        KnowledgebaseRequest kownledgebaseRequestKeyword = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_KEYWORD_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestKeyword.setType(KnowledgebaseTypeEnum.KEYWORD.name());
        kownledgebaseRequestKeyword.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestKeyword);
        // 
        KnowledgebaseRequest kownledgebaseRequestFaq = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_FAQ_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestFaq.setType(KnowledgebaseTypeEnum.FAQ.name());
        kownledgebaseRequestFaq.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestFaq);
        //
        KnowledgebaseRequest kownledgebaseRequestAutoReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_AUTOREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestAutoReply.setType(KnowledgebaseTypeEnum.AUTOREPLY.name());
        kownledgebaseRequestAutoReply.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestAutoReply);
        //
        KnowledgebaseRequest kownledgebaseRequestQuickReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_QUICKREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        kownledgebaseRequestQuickReply.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestQuickReply);
        //
        KnowledgebaseRequest kownledgebaseRequestTaboo = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_TABOO_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestTaboo.setType(KnowledgebaseTypeEnum.TABOO.name());
        kownledgebaseRequestTaboo.setOrgUid(orgUid);
        knowledgebaseService.create(kownledgebaseRequestTaboo);
        // 
        
    }
    
}
