/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 14:29:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-22 09:06:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.faq;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
// import com.bytedesk.core.rbac.user.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FaqEventListener {

    private final FaqService faqService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        String orgUid = organization.getUid();
        log.info("faq - organization created: {}", organization.getName());
        // 
        FaqRequest faqDemo1 = FaqRequest.builder()
                .title(I18Consts.I18N_FAQ_DEMO_TITLE_1)
                .content(I18Consts.I18N_FAQ_DEMO_CONTENT_1)
                .type(MessageTypeEnum.TEXT.getValue())
                .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                .orgUid(orgUid)
                .build();
        faqDemo1.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1);
        faqService.create(faqDemo1);
        //
        FaqRequest faqDemo2 = FaqRequest.builder()
                .title(I18Consts.I18N_FAQ_DEMO_TITLE_2)
                .content(I18Consts.I18N_FAQ_DEMO_CONTENT_2)
                .type(MessageTypeEnum.IMAGE.getValue())
                .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                .orgUid(orgUid)
                .build();
        faqDemo2.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        faqService.create(faqDemo2);
        // 
    }

    
}
