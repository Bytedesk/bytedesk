/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 12:54:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bytedesk.core.utils.Utils;

@Slf4j
@Component
@AllArgsConstructor
public class FaqEventListener {
    
    private final FaqRestService faqService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("faq - organization created: {}", organization.getName());
        //
        String faqDemoUid1 = Utils.formatUid(orgUid, FaqConsts.FAQ_DEMO_UID_1);
        FaqRequest faqDemo1 = FaqRequest.builder()
                .question(I18Consts.I18N_FAQ_DEMO_QUESTION_1)
                .answer(I18Consts.I18N_FAQ_DEMO_ANSWER_1)
                .type(MessageTypeEnum.TEXT.name())
                .categoryUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_1))
                .build();
        faqDemo1.setUid(faqDemoUid1);       
        faqDemo1.setOrgUid(orgUid);
        faqService.create(faqDemo1);
        //
        String faqDemoUid2 = Utils.formatUid(orgUid, FaqConsts.FAQ_DEMO_UID_2);
        FaqRequest faqDemo2 = FaqRequest.builder()
                .question(I18Consts.I18N_FAQ_DEMO_QUESTION_2)
                .answer(I18Consts.I18N_FAQ_DEMO_ANSWER_2)
                .type(MessageTypeEnum.IMAGE.name())
                .categoryUid(Utils.formatUid(orgUid, I18Consts.I18N_FAQ_CATEGORY_DEMO_2))
                .build();
        faqDemo2.setUid(faqDemoUid2);
        faqDemo2.setOrgUid(orgUid);
        faqService.create(faqDemo2);
    }

    // @EventListener
    // public void onMessageCreateEvent(MessageCreateEvent event) {
    // Message message = event.getMessage();
    // log.info("message faq create event: {}", message.getContent());
    // }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        //
        if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())
                || message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
            log.info("message faq update event: {}", message);
            //
            FaqMessageExtra extra = JSON.parseObject(message.getExtra(), FaqMessageExtra.class);
            log.info("faq rate extra faqUid {}, rate {}", extra.getFaqUid(), extra.getRate());
            //
            if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())) {
                faqService.upVote(extra.getFaqUid());
            } else if (message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
                faqService.downVote(extra.getFaqUid());
            }
        }
    }
}
