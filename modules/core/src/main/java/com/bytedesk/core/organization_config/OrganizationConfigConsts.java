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
 * 组织级配置常量
 */
public class OrganizationConfigConsts {

    /**
     * Redis 缓存名（TTL 在 RedisCacheConfig 中注册）
     */
    public static final String CACHE_NAME_ORGANIZATION_CONFIG = "organization_config";

    // ============ 受控 key（与 OrganizationConfigKeyEnum 保持一致） ============
    // 分组 AI_ASR：语音识别
    public static final String KEY_AI_ASR_PROVIDER = "ai.asr.provider";
    public static final String KEY_AI_ASR_MODEL = "ai.asr.model";
    public static final String KEY_AI_ASR_API_KEY = "ai.asr.apiKey";
    public static final String KEY_AI_ASR_LANGUAGE = "ai.asr.language";

    // 分组 AI_TTS：语音合成
    public static final String KEY_AI_TTS_PROVIDER = "ai.tts.provider";
    public static final String KEY_AI_TTS_MODEL = "ai.tts.model";
    public static final String KEY_AI_TTS_VOICE = "ai.tts.voice";
    public static final String KEY_AI_TTS_API_KEY = "ai.tts.apiKey";
    public static final String KEY_AI_TTS_LANGUAGE = "ai.tts.language";

    // 分组 AI_OCR：图文识别
    public static final String KEY_AI_OCR_PROVIDER = "ai.ocr.provider";
    public static final String KEY_AI_OCR_MODEL = "ai.ocr.model";
    public static final String KEY_AI_OCR_API_KEY = "ai.ocr.apiKey";

    private OrganizationConfigConsts() {
    }
}
