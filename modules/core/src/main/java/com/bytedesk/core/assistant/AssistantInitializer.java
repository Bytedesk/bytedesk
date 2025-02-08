/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-08 15:43:57
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

    private AssistantService assistantService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {
        // if (assistantRepository.count() > 0) {
        //     return;
        // }

        // 文件助手
        AssistantRequest fileAssistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_FILE_ASSISTANT)
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultFileAssistantAvatarUrl())
                .description(I18Consts.I18N_FILE_ASSISTANT_DESCRIPTION)
                .build();
        fileAssistantRequest.setUid(BytedeskConsts.DEFAULT_FILE_ASSISTANT_UID);
        fileAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        assistantService.create(fileAssistantRequest);

        // 剪贴助手
        AssistantRequest clipboardAssistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_CLIPBOARD_ASSISTANT)
                .nickname(I18Consts.I18N_CLIPBOARD_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultClipboardAssistantAvatarUrl())
                .description(I18Consts.I18N_CLIPBOARD_ASSISTANT_DESCRIPTION)
                .build();
        clipboardAssistantRequest.setUid(BytedeskConsts.DEFAULT_CLIPBOARD_ASSISTANT_UID);
        clipboardAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        assistantService.create(clipboardAssistantRequest);

        // https://mp.weixin.qq.com/s/nu-2ji9NOszcZ_6SWd469A
        // TODO: 意图改写
        AssistantRequest intentRewriteAssistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_INTENT_REWRITE_ASSISTANT)
                .nickname(I18Consts.I18N_INTENT_REWRITE_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultIntentRewriteAssistantAvatarUrl())
                .description(I18Consts.I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION)
                .build();
        intentRewriteAssistantRequest.setUid(BytedeskConsts.DEFAULT_INTENT_REWRITE_ASSISTANT_UID);
        intentRewriteAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        assistantService.create(intentRewriteAssistantRequest);

        // TODO: 意图识别:意图槽位/意图置信度
        AssistantRequest intentClassificationAssistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_INTENT_CLASSIFICATION_ASSISTANT)
                .nickname(I18Consts.I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultIntentClassificationAssistantAvatarUrl())
                .description(I18Consts.I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION)
                .build();
        intentClassificationAssistantRequest.setUid(BytedeskConsts.DEFAULT_INTENT_CLASSIFICATION_ASSISTANT_UID);
        intentClassificationAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        assistantService.create(intentClassificationAssistantRequest);

        // TODO: 情绪分析
        AssistantRequest emotionAssistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_EMOTION_ASSISTANT)
                .nickname(I18Consts.I18N_EMOTION_ASSISTANT_NAME)
                .avatar(AvatarConsts.getDefaultEmotionAssistantAvatarUrl())
                .description(I18Consts.I18N_EMOTION_ASSISTANT_DESCRIPTION)
                .build();
        emotionAssistantRequest.setUid(BytedeskConsts.DEFAULT_EMOTION_ASSISTANT_UID);
        emotionAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        assistantService.create(emotionAssistantRequest);
    }
    
    
}
