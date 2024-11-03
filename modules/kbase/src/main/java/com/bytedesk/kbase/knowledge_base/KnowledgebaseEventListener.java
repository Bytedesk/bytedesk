/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-30 09:55:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
// import com.bytedesk.vip_kbase.knowledge_base.KnowledgebaseStaticService;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KnowledgebaseEventListener {

        private final KnowledgebaseService knowledgebaseService;

        @EventListener
        public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
                OrganizationEntity organization = (OrganizationEntity) event.getSource();
                String orgUid = organization.getUid();
                log.info("onOrganizationCreateEvent: orgUid {}", orgUid);
                //
                KnowledgebaseRequest kownledgebaseRequestHelpdoc = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_HELPCENTER_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
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
                KnowledgebaseRequest kownledgebaseRequeqestFaq = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_FAQ_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqestFaq.setType(KnowledgebaseTypeEnum.FAQ.name());
                kownledgebaseRequeqestFaq.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqestFaq);
                //
                KnowledgebaseRequest kownledgebaseRequeqestAutoReply = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_AUTOREPLY_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqestAutoReply.setType(KnowledgebaseTypeEnum.AUTOREPLY.name());
                kownledgebaseRequeqestAutoReply.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqestAutoReply);
                //
                KnowledgebaseRequest kownledgebaseRequeqestQuickReply = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_QUICKREPLY_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqestQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
                kownledgebaseRequeqestQuickReply.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqestQuickReply);
                //
                KnowledgebaseRequest kownledgebaseRequestTaboo = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_TABOO_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequestTaboo.setType(KnowledgebaseTypeEnum.TABOO.name());
                kownledgebaseRequestTaboo.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequestTaboo);
        }


}
