/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.organization_config;

/**
 * 组织级配置受控 key 清单
 *
 * 扩展方式：新增组织级配置项 = 在此枚举加一条（含 key/类型/分组/展示名/说明/排序），
 * 后端 /query 自动合成完整清单，前端表单按元数据自动渲染，无需改表、无需新接口。
 *
 * 默认回退来源为系统级静态配置（properties / docker compose 环境变量，如
 * spring.ai.dashscope.audio.transcription.api-key）。组织未配置时行为与现状一致。
 */
public enum OrganizationConfigKeyEnum {

    // ============ 语音识别（AI_ASR） ============
    AI_ASR_PROVIDER(
            OrganizationConfigConsts.KEY_AI_ASR_PROVIDER,
            "STRING",
            "AI_ASR",
            "ASR 厂商",
            "语音识别服务厂商，当前支持 dashscope（阿里云百炼）",
            1),
    AI_ASR_MODEL(
            OrganizationConfigConsts.KEY_AI_ASR_MODEL,
            "STRING",
            "AI_ASR",
            "ASR 模型",
            "语音识别模型，如 paraformer-v2 / fun-asr / paraformer-realtime-v2",
            2),
    AI_ASR_API_KEY(
            OrganizationConfigConsts.KEY_AI_ASR_API_KEY,
            "STRING",
            "AI_ASR",
            "ASR API Key",
            "组织级语音识别 apiKey，不填则使用系统级配置（spring.ai.dashscope.audio.transcription.api-key）",
            3),
    AI_ASR_LANGUAGE(
            OrganizationConfigConsts.KEY_AI_ASR_LANGUAGE,
            "STRING",
            "AI_ASR",
            "ASR 识别语言",
            "默认识别语言，如 zh / en / auto",
            4),

    // ============ 语音合成（AI_TTS） ============
    AI_TTS_PROVIDER(
            OrganizationConfigConsts.KEY_AI_TTS_PROVIDER,
            "STRING",
            "AI_TTS",
            "TTS 厂商",
            "语音合成服务厂商，当前支持 dashscope（阿里云百炼）",
            1),
    AI_TTS_MODEL(
            OrganizationConfigConsts.KEY_AI_TTS_MODEL,
            "STRING",
            "AI_TTS",
            "TTS 模型",
            "语音合成模型，如 cosyvoice-v2 / qwen-tts / qwen-audio-tts",
            2),
    AI_TTS_VOICE(
            OrganizationConfigConsts.KEY_AI_TTS_VOICE,
            "STRING",
            "AI_TTS",
            "TTS 音色",
            "默认音色，如 longan_v2 / longxiaochun，见阿里云百炼音色列表",
            3),
    AI_TTS_API_KEY(
            OrganizationConfigConsts.KEY_AI_TTS_API_KEY,
            "STRING",
            "AI_TTS",
            "TTS API Key",
            "组织级语音合成 apiKey，不填则使用系统级配置（spring.ai.dashscope.audio.synthesis.api-key）",
            4),
    AI_TTS_LANGUAGE(
            OrganizationConfigConsts.KEY_AI_TTS_LANGUAGE,
            "STRING",
            "AI_TTS",
            "TTS 合成语言",
            "默认合成语言，如 zh-CN / en-US",
            5),

    // ============ 图文识别（AI_OCR） ============
    AI_OCR_PROVIDER(
            OrganizationConfigConsts.KEY_AI_OCR_PROVIDER,
            "STRING",
            "AI_OCR",
            "OCR 厂商",
            "图文识别服务厂商，当前支持 dashscope（阿里云百炼）",
            1),
    AI_OCR_MODEL(
            OrganizationConfigConsts.KEY_AI_OCR_MODEL,
            "STRING",
            "AI_OCR",
            "OCR 模型",
            "图文识别模型，如 qwen-vl-ocr-latest",
            2),
    AI_OCR_API_KEY(
            OrganizationConfigConsts.KEY_AI_OCR_API_KEY,
            "STRING",
            "AI_OCR",
            "OCR API Key",
            "组织级图文识别 apiKey，不填则使用系统级配置（spring.ai.dashscope.api-key）",
            3),
    ;

    private final String key;
    private final String valueType;
    private final String group;
    private final String displayName;
    private final String description;
    private final int sortOrder;

    OrganizationConfigKeyEnum(String key, String valueType, String group,
            String displayName, String description, int sortOrder) {
        this.key = key;
        this.valueType = valueType;
        this.group = group;
        this.displayName = displayName;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public String getKey() {
        return key;
    }

    public String getValueType() {
        return valueType;
    }

    public String getGroup() {
        return group;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public static OrganizationConfigKeyEnum fromKey(String key) {
        if (key == null) {
            return null;
        }
        for (OrganizationConfigKeyEnum keyEnum : OrganizationConfigKeyEnum.values()) {
            if (keyEnum.getKey().equals(key)) {
                return keyEnum;
            }
        }
        return null;
    }
}
