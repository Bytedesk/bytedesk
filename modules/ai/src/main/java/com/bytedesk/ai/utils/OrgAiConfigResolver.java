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
package com.bytedesk.ai.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.organization_config.OrganizationConfigConsts;
import com.bytedesk.core.organization_config.OrganizationConfigRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 组织级 AI 配置解析器（规划 §10.3）。
 *
 * 读取优先级：组织覆盖值（bytedesk_core_organization_config）> 系统级静态配置
 * （properties / 环境变量，与执行服务 @Value 注入的回退链保持一致）。
 *
 * 定位在 modules/ai：依赖 modules/core 可直接读 OrganizationConfigRestService（带 Redis 缓存）；
 * 静态回退 key 在社区版同样存在；enterprise/ai 执行服务（Asr/Tts/Ocr）通过注入本类消费。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrgAiConfigResolver {

    private final OrganizationConfigRestService organizationConfigRestService;

    private final Environment environment;

    // ============ ASR（语音识别） ============

    /**
     * 组织级 ASR apiKey：组织覆盖 > spring.ai.dashscope.audio.transcription.api-key
     * > spring.ai.dashscope.api-key > DASHSCOPE_API_KEY
     */
    public String resolveAsrApiKey(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_ASR_API_KEY,
                "spring.ai.dashscope.audio.transcription.api-key",
                "spring.ai.dashscope.api-key",
                "DASHSCOPE_API_KEY");
    }

    /** 组织级 ASR 模型（默认 paraformer-v2） */
    public String resolveAsrModel(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_ASR_MODEL,
                "spring.ai.dashscope.audio.transcription.options.model");
    }

    /** 组织级 ASR 语言（默认 zh） */
    public String resolveAsrLanguage(String orgUid) {
        String value = resolve(orgUid, OrganizationConfigConsts.KEY_AI_ASR_LANGUAGE);
        return StringUtils.hasText(value) ? value : "zh";
    }

    // ============ TTS（语音合成） ============

    /**
     * 组织级 TTS apiKey：组织覆盖 > spring.ai.dashscope.audio.synthesis.api-key
     * > spring.ai.dashscope.api-key > DASHSCOPE_API_KEY
     */
    public String resolveTtsApiKey(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_TTS_API_KEY,
                "spring.ai.dashscope.audio.synthesis.api-key",
                "spring.ai.dashscope.api-key",
                "DASHSCOPE_API_KEY");
    }

    /** 组织级 TTS 模型 */
    public String resolveTtsModel(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_TTS_MODEL,
                "spring.ai.dashscope.audio.synthesis.options.model");
    }

    /** 组织级 TTS 音色 */
    public String resolveTtsVoice(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_TTS_VOICE,
                "spring.ai.dashscope.audio.synthesis.options.voice");
    }

    // ============ OCR（图文识别） ============

    /**
     * 组织级 OCR apiKey：组织覆盖 > spring.ai.dashscope.api-key > DASHSCOPE_API_KEY
     */
    public String resolveOcrApiKey(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_OCR_API_KEY,
                "spring.ai.dashscope.api-key",
                "DASHSCOPE_API_KEY");
    }

    /** 组织级 OCR 模型 */
    public String resolveOcrModel(String orgUid) {
        return resolve(orgUid, OrganizationConfigConsts.KEY_AI_OCR_MODEL);
    }

    // ============ 通用解析 ============

    /**
     * 解析单个配置：组织覆盖 > 静态回退链（依次尝试多个 property key）。
     * 组织配置读取失败（缓存/DB 异常）时静默回退静态值，不阻断业务链路。
     */
    private String resolve(String orgUid, String orgKey, String... fallbackPropertyKeys) {
        if (StringUtils.hasText(orgUid) && StringUtils.hasText(orgKey)) {
            try {
                String override = organizationConfigRestService.getEffectiveValue(orgUid, orgKey);
                if (StringUtils.hasText(override)) {
                    return override;
                }
            } catch (Exception e) {
                log.warn("Failed to resolve organization config, orgUid={}, key={}, fallback to system default: {}",
                        orgUid, orgKey, e.getMessage());
            }
        }
        if (fallbackPropertyKeys == null) {
            return null;
        }
        for (String key : fallbackPropertyKeys) {
            String value = environment.getProperty(key);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
