/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 22:47:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FaqInitializer implements SmartInitializingSingleton {

    private final FaqService faqService;

    private final AuthorityService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {

        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = FaqPermissions.FAQ_PREFIX + permission.name();
            if (authorityService.existsByValue(permissionValue)) {
                continue;
            }
            AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .build();
            authRequest.setUid(permissionValue.toLowerCase());
            authorityService.create(authRequest);
        }

        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        if (!faqService.existsByUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1)) {
            FaqRequest faqDemo1 = FaqRequest.builder()
                    .title(I18Consts.I18N_FAQ_DEMO_TITLE_1)
                    .content(I18Consts.I18N_FAQ_DEMO_CONTENT_1)
                    .type(MessageTypeEnum.TEXT.name())
                    .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_1)
                    .build();
            faqDemo1.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1);
            faqDemo1.setOrgUid(orgUid);
            faqService.create(faqDemo1);
        }
        //
        if (!faqService.existsByUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2)) {
            FaqRequest faqDemo2 = FaqRequest.builder()
                    .title(I18Consts.I18N_FAQ_DEMO_TITLE_2)
                    .content(I18Consts.I18N_FAQ_DEMO_CONTENT_2)
                    .type(MessageTypeEnum.IMAGE.name())
                    .categoryUid(orgUid + I18Consts.I18N_FAQ_CATEGORY_DEMO_2)
                    .build();
            faqDemo2.setUid(orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
            faqDemo2.setOrgUid(orgUid);
            faqService.create(faqDemo2);
        }
    }

}
