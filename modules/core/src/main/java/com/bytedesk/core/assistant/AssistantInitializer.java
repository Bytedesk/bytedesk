/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 13:09:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.topic.TopicUtils;

// import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@Order(1)
@AllArgsConstructor
public class AssistantInitializer implements SmartInitializingSingleton {

    // private AssistantRepository assistantRepository;

    private AssistantRestService assistantService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {

        // 文件助手
        AssistantRequest fileAssistantRequest = AssistantRequest.builder()
                .uid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID)
                .topic(TopicUtils.TOPIC_FILE_ASSISTANT)
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultFileAssistantAvatarUrl())
                .description(I18Consts.I18N_FILE_ASSISTANT_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .build();
        assistantService.create(fileAssistantRequest);

        // 排队助手
        AssistantRequest queueAssistantRequest = AssistantRequest.builder()
                .uid(BytedeskConsts.DEFAULT_QUEUE_ASSISTANT_UID)
                .topic(TopicUtils.TOPIC_QUEUE_ASSISTANT)
                .nickname(I18Consts.I18N_QUEUE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultQueueAssistantAvatarUrl())
                .description(I18Consts.I18N_QUEUE_ASSISTANT_DESCRIPTION)
                .level(LevelEnum.PLATFORM.name())
                .build();
        assistantService.create(queueAssistantRequest);

        // 剪贴助手
        // AssistantRequest clipboardAssistantRequest = AssistantRequest.builder()
        //         .topic(TopicUtils.TOPIC_CLIPBOARD_ASSISTANT)
        //         .nickname(I18Consts.I18N_CLIPBOARD_ASSISTANT_NAME)
        //         .avatar(AvatarConsts.getDefaultClipboardAssistantAvatarUrl())
        //         .description(I18Consts.I18N_CLIPBOARD_ASSISTANT_DESCRIPTION)
        //         .build();
        // clipboardAssistantRequest.setUid(BytedeskConsts.DEFAULT_CLIPBOARD_ASSISTANT_UID);
        // clipboardAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        // assistantService.create(clipboardAssistantRequest);

    }
    
    
}
