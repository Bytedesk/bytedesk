/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-11 14:31:46
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

        // 
        // TODO: query扩充，用户填写一个问题，系统根据问题生成多个类似不同问法，用LLM生成一批同义句
        // AssistantRequest queryExpansionAssistantRequest = AssistantRequest.builder()
        //         .topic(TopicUtils.TOPIC_QUERY_EXPANSION_ASSISTANT)
        //         .nickname(I18Consts.I18N_QUERY_EXPANSION_ASSISTANT_NAME)
        //         .avatar(AvatarConsts.getDefaultQueryExpansionAssistantAvatarUrl())
        //         .description(I18Consts.I18N_QUERY_EXPANSION_ASSISTANT_DESCRIPTION)
        //         .build();
        // queryExpansionAssistantRequest.setUid(BytedeskConsts.DEFAULT_QUERY_EXPANSION_ASSISTANT_UID);
        // queryExpansionAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        // assistantService.create(queryExpansionAssistantRequest);

        // https://mp.weixin.qq.com/s/nu-2ji9NOszcZ_6SWd469A
        // TODO: 意图改写
        // AssistantRequest intentRewriteAssistantRequest = AssistantRequest.builder()
        //         .topic(TopicUtils.TOPIC_INTENT_REWRITE_ASSISTANT)
        //         .nickname(I18Consts.I18N_INTENT_REWRITE_ASSISTANT_NAME)
        //         .avatar(AvatarConsts.getDefaultIntentRewriteAssistantAvatarUrl())
        //         .description(I18Consts.I18N_INTENT_REWRITE_ASSISTANT_DESCRIPTION)
        //         .build();
        // intentRewriteAssistantRequest.setUid(BytedeskConsts.DEFAULT_INTENT_REWRITE_ASSISTANT_UID);
        // intentRewriteAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        // assistantService.create(intentRewriteAssistantRequest);

        /**
         * https://mp.weixin.qq.com/s/nu-2ji9NOszcZ_6SWd469A
         prompt = """
                # 角色
                你是一位意图样本生成专家，擅长根据给定的模板生成意图及对应的槽位信息。你能够准确地解析用户输入，并将其转化为结构化的意图和槽位数据。

                ## 技能
                ### 技能1：解析用户指令
                - **任务**：根据用户提供的自然语言指令，识别出用户的意图。

                ### 技能2：生成结构化意图和槽位信息
                - 意图分类：video_search,music_search,information_search
                - 槽位分类：
                -- information_search: classification,video_category,video_name,video_season
                -- music_search: music_search,music_singer,music_tag,music_release_time
                -- video_search: video_actor,video_name,video_episode
                - **任务**：将解析出的用户意图转换为结构化的JSON格式。
                - 确保每个意图都有相应的槽位信息，不要自行编造槽位。
                - 槽位信息应包括所有必要的细节，如演员、剧名、集数、歌手、音乐标签、发布时间等。

                ### 技能3：在线搜索
                - 如果遇到关于电影情节的描述，可以调用搜索引擎获取到电影名、演员等信息称补充到actor,name等槽位中

                ### 输出示例
                - 以JSON格式输出，例如：
                -"这周杭州的天气如何明天北京有雨吗"：{'infor_search':{'extra_info':['这周杭州的天气如何明天北京有雨吗']}}
                -"我一直在追赵丽颖的楚乔传我看到第二十集了它已经更新了吗我可以看下一集吗"：{'video_search':{'video_actor':['赵丽颖'],'video_name':['楚乔传'],'video_episode':['第21集'],'extra_info':['我一直在追赵丽颖的楚乔传我看到第二十集了它已经更新了吗我可以看下一集吗']}}

                ## 限制
                - 只处理与意图生成相关的任务。
                - 生成的意图和槽位信息必须准确且完整。
                - 在解析过程中，确保理解用户的意图并正确映射到相应的服务类型
                - 如果遇到未知的服务类型或槽位信息，可以通过调用搜索工具进行补充和确认。
                - 直接输出Json，不要输出其他思考过程
                生成后的用于知识库的数据集格式如下：

                [{
                "instruction":"播放一首七仔的歌曲我想听他的经典老歌最好是70年代的音乐风格",
                "output":"{'music_search':{'singer':['张学友'],'music_tag':['经典老歌'],'release_time':['70年代']}}"
                }]
         */
        // TODO: 意图识别:意图槽位/意图置信度
        // AssistantRequest intentClassificationAssistantRequest = AssistantRequest.builder()
        //         .topic(TopicUtils.TOPIC_INTENT_CLASSIFICATION_ASSISTANT)
        //         .nickname(I18Consts.I18N_INTENT_CLASSIFICATION_ASSISTANT_NAME)
        //         .avatar(AvatarConsts.getDefaultIntentClassificationAssistantAvatarUrl())
        //         .description(I18Consts.I18N_INTENT_CLASSIFICATION_ASSISTANT_DESCRIPTION)
        //         .build();
        // intentClassificationAssistantRequest.setUid(BytedeskConsts.DEFAULT_INTENT_CLASSIFICATION_ASSISTANT_UID);
        // intentClassificationAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        // assistantService.create(intentClassificationAssistantRequest);

        // TODO: 情绪分析
        // AssistantRequest emotionAssistantRequest = AssistantRequest.builder()
        //         .topic(TopicUtils.TOPIC_EMOTION_ASSISTANT)
        //         .nickname(I18Consts.I18N_EMOTION_ASSISTANT_NAME)
        //         .avatar(AvatarConsts.getDefaultEmotionAssistantAvatarUrl())
        //         .description(I18Consts.I18N_EMOTION_ASSISTANT_DESCRIPTION)
        //         .build();
        // emotionAssistantRequest.setUid(BytedeskConsts.DEFAULT_EMOTION_ASSISTANT_UID);
        // emotionAssistantRequest.setLevel(LevelEnum.PLATFORM.name());
        // assistantService.create(emotionAssistantRequest);

        

    }
    
    
}
