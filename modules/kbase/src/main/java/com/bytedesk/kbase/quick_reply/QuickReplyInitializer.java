/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 16:15:36
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

import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
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

    private final CategoryRestService categoryService;

    @Override
    public void afterSingletonsInstantiated() {
        initQuickReplyCategory();
        initQuickReply();
    }

    // 快捷回复
    // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
    // init quick reply categories
    private void initQuickReplyCategory() {
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String quickReplyCategoryDemoUid1 = orgUid + QuickReplyConsts.QUICK_REPLY_CATEGORY_DEMO_UID_1;
        // 快捷回复-询问联系方式
        CategoryRequest categoryContact = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT)
                .orderNo(0)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryContact.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryContact.setUid(quickReplyCategoryDemoUid1);
        // 此处设置orgUid方便超级管理员加载
        categoryContact.setOrgUid(orgUid);
        categoryService.create(categoryContact);

        // 快捷回复-感谢
        String quickReplyCategoryDemoUid2 = orgUid + QuickReplyConsts.QUICK_REPLY_CATEGORY_DEMO_UID_2;
        CategoryRequest categoryThanks = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS)
                .orderNo(1)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryThanks.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryThanks.setUid(quickReplyCategoryDemoUid2);
        // 此处设置orgUid方便超级管理员加载
        categoryThanks.setOrgUid(orgUid);
        categoryService.create(categoryThanks);

        // 快捷回复-问候
        String quickReplyCategoryDemoUid3 = orgUid + QuickReplyConsts.QUICK_REPLY_CATEGORY_DEMO_UID_3;
        CategoryRequest categoryWelcome = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME)
                .orderNo(2)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryWelcome.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryWelcome.setUid(quickReplyCategoryDemoUid3);
        // 此处设置orgUid方便超级管理员加载
        categoryWelcome.setOrgUid(orgUid);
        categoryService.create(categoryWelcome);

        // 快捷回复-告别
        String quickReplyCategoryDemoUid4 = orgUid + QuickReplyConsts.QUICK_REPLY_CATEGORY_DEMO_UID_4;
        CategoryRequest categoryBye = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE)
                .orderNo(3)
                .level(LevelEnum.PLATFORM.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                .build();
        categoryBye.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryBye.setUid(quickReplyCategoryDemoUid4);
        // 此处设置orgUid方便超级管理员加载
        categoryBye.setOrgUid(orgUid);
        categoryService.create(categoryBye);
    }

    // @PostConstruct
    public void initQuickReply() {
        // 
        if (quickReplyRepository.count() > 0) {
            return;
        }

        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        Optional<CategoryEntity> categoryContact = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT,
                CategoryTypeEnum.QUICKREPLY.name(),
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
                CategoryTypeEnum.QUICKREPLY.name(),
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
                CategoryTypeEnum.QUICKREPLY.name(),
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
                CategoryTypeEnum.QUICKREPLY.name(),
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
