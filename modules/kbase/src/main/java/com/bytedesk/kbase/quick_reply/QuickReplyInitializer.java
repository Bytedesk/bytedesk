/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 11:06:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QuickReplyInitializer implements SmartInitializingSingleton {

    private final QuickReplyService quickReplyService;

    private final QuickReplyRepository quickReplyRepository;

    private final CategoryService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {
        if (quickReplyRepository.count() > 0) {
            return;
        }

        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        Optional<CategoryEntity> categoryContact = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryContact.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_CONTACT_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_CONTACT_CONTENT)
                    .categoryUid(categoryContact.get().getUid())
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .level(LevelEnum.PLATFORM.name())
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            quickReplyService.create(quickReplyRequest);
        }
        //
        Optional<CategoryEntity> categoryThanks = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryThanks.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_THANKS_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_THANKS_CONTENT)
                    .categoryUid(categoryThanks.get().getUid())
                    .level(LevelEnum.PLATFORM.name())
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            quickReplyService.create(quickReplyRequest);
        }
        //
        Optional<CategoryEntity> categoryWelcome = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryWelcome.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_WELCOME_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_WELCOME_CONTENT)
                    .categoryUid(categoryWelcome.get().getUid())
                    .level(LevelEnum.PLATFORM.name())
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            quickReplyService.create(quickReplyRequest);
        }

        Optional<CategoryEntity> categoryBye = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryBye.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_BYE_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_BYE_CONTENT)
                    .categoryUid(categoryBye.get().getUid())
                    .level(LevelEnum.PLATFORM.name())
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            quickReplyService.create(quickReplyRequest);
        }
    }
    
}
