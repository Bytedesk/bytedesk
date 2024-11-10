/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 16:04:55
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
        }


}
