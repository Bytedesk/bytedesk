/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-10-21 09:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-10-21 09:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.provider.aliyun.openai;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmProviderConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云百炼 OpenAI 兼容模式连接解析。
 *
 * <p>与 SiliconFlow 等兼容服务的“静默回退默认模型”不同：解析失败直接抛出
 * {@link AliyunOpenaiProviderException}，由调用方走明确错误路径，
 * 不再用全局默认模型掩盖配置错误（规划 B4）。</p>
 *
 * <p>凭据规则：优先 provider 自身 apiKey；仅平台级（PLATFORM）provider 允许回退
 * 全局 spring.ai.aliyun_openai.api-key（其可引用同地域 dashscope Key）；
 * 组织级记录缺 Key 即阻断，不自动继承平台/其他供应商凭据。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOpenaiProviderConnection {

    public static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    private static final String PLACEHOLDER_KEY = "sk-xxx";

    private final LlmProviderRestService llmProviderRestService;

    @Value("${spring.ai.aliyun_openai.api-key:}")
    private String globalApiKey;

    @Value("${spring.ai.aliyun_openai.base-url:}")
    private String globalBaseUrl;

    @Value("${spring.ai.aliyun_openai.chat.enabled:false}")
    private boolean chatEnabled;

    /**
     * 解析机器人配置对应的 provider 并校验开关/类型/启用状态。
     */
    public LlmProviderEntity requireProvider(RobotLlm llm) {
        if (llm == null || !StringUtils.hasText(llm.getTextProviderUid())) {
            throw new AliyunOpenaiProviderException("RobotLlm or textProviderUid is empty");
        }
        return requireProviderByUid(llm.getTextProviderUid());
    }

    /**
     * 按已解析的 provider UID 校验（结构化调用等入口使用）。
     */
    public LlmProviderEntity requireProviderByUid(String providerUid) {
        if (!chatEnabled) {
            throw new AliyunOpenaiProviderException(
                    "aliyun_openai chat is disabled (spring.ai.aliyun_openai.chat.enabled=false)");
        }
        if (!StringUtils.hasText(providerUid)) {
            throw new AliyunOpenaiProviderException("providerUid is empty");
        }
        Optional<LlmProviderEntity> optional = llmProviderRestService.findByUid(providerUid);
        if (optional.isEmpty()) {
            throw new AliyunOpenaiProviderException("LlmProvider not found: " + providerUid);
        }
        LlmProviderEntity provider = optional.get();
        if (provider.isDeleted()) {
            throw new AliyunOpenaiProviderException("LlmProvider deleted: " + providerUid);
        }
        if (!LlmProviderConstants.ALIYUN_OPENAI.equals(provider.getType())) {
            throw new AliyunOpenaiProviderException(
                    "LlmProvider type mismatch: expected aliyun_openai but was " + provider.getType());
        }
        if (Boolean.FALSE.equals(provider.getEnabled())) {
            throw new AliyunOpenaiProviderException("LlmProvider disabled: " + providerUid);
        }
        return provider;
    }

    /**
     * 解析 API Key：组织级只认自身配置；平台级允许全局回退。
     */
    public String resolveApiKey(LlmProviderEntity provider) {
        String key = provider.getApiKey();
        if (StringUtils.hasText(key) && !PLACEHOLDER_KEY.equalsIgnoreCase(key)) {
            return key;
        }
        if (LevelEnum.PLATFORM.name().equals(provider.getLevel())
                && StringUtils.hasText(globalApiKey) && !PLACEHOLDER_KEY.equalsIgnoreCase(globalApiKey)) {
            log.info("Platform aliyun_openai provider {} apiKey empty, fallback to global config", provider.getUid());
            return globalApiKey;
        }
        return null;
    }

    /**
     * 解析 base URL：仅空值回退默认兼容端点，不改写显式配置。
     */
    public String resolveBaseUrl(LlmProviderEntity provider) {
        String url = provider.getBaseUrl();
        if (StringUtils.hasText(url)) {
            return url;
        }
        if (StringUtils.hasText(globalBaseUrl)) {
            return globalBaseUrl;
        }
        return DEFAULT_BASE_URL;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

}
