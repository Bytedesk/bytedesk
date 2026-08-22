/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-07 21:24:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 00:03:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.properties;

import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.LicenseValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/config/bytedesk", produces = "application/json;charset=UTF-8")
@Tag(name = "Configuration Properties", description = "Configuration properties management APIs for system settings")
public class BytedeskPropertiesController {

    private static final String FALLBACK_TTS_PROVIDER = "dashscope";
    private static final String FALLBACK_TTS_MODEL = "cosyvoice-v3-flash";
    private static final String FALLBACK_TTS_VOICE = "longanhuan";
    private static final String FALLBACK_TTS_LANGUAGE = "zh-CN";
    private static final String FALLBACK_TTS_AUDIO_FORMAT = "mp3";
    private static final String FALLBACK_ASR_PROVIDER = "dashscope";
    private static final String FALLBACK_ASR_MODEL = "paraformer-v2";
    private static final String FALLBACK_ASR_REALTIME_MODEL = "paraformer-realtime-v2";
    private static final String FALLBACK_ASR_SOURCE_FORMAT = "mp3";
    private static final long FALLBACK_ASR_TIMEOUT_MS = 180_000L;
    private static final long FALLBACK_ASR_POLL_INTERVAL_MS = 1_500L;

    private final Environment environment;
    private final StringRedisTemplate stringRedisTemplate;

    public BytedeskPropertiesController(Environment environment,
                                         StringRedisTemplate stringRedisTemplate) {
        this.environment = environment;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // http://127.0.0.1:9003/config/bytedesk/properties
    @ApiRateLimiter(value = 1, timeout = 1)
    @Operation(summary = "Get Bytedesk Properties", description = "Retrieve Bytedesk system configuration properties")
    @GetMapping(value = "/properties", produces = "application/json;charset=UTF-8")
    public ResponseEntity<JsonResult<?>> getBytedeskProperties() {

        BytedeskPropertiesResponse bytedeskPropertiesResponse = ConvertUtils.convertToBytedeskPropertiesResponse(BytedeskProperties.getInstance());
        if (bytedeskPropertiesResponse.getCustom() == null) {
            bytedeskPropertiesResponse.setCustom(new BytedeskPropertiesResponse.Custom());
        }
        bytedeskPropertiesResponse.getCustom().setBndEnabled(
                environment.getProperty("bytedesk.custom.bnd.enabled", Boolean.class, false));
        if (bytedeskPropertiesResponse.getService() == null) {
            bytedeskPropertiesResponse.setService(new BytedeskPropertiesResponse.Service());
        }
        bytedeskPropertiesResponse.getService().setAgentSeatEnabled(
            environment.getProperty("bytedesk.service.agent-seat-enabled", Boolean.class, false));

        // 仅下发服务端验签后的明文 license 摘要（edition/expiryDate/valid/userType 等），供前端门控直接读取，无需验签/解密。
        String licenseKey = BytedeskProperties.getInstance().getLicenseKey();
        if (StringUtils.hasText(licenseKey)) {
            String cacheKey = BytedeskConsts.LICENSE_VALID_CACHE_PREFIX + BytedeskProperties.getInstance().getOriginalAppkey();
            String cachedResult = stringRedisTemplate.opsForValue().get(cacheKey);

            // 即使 Redis 缓存为 "false"，仍需重新验证许可证。
            // 原因：Redis 缓存可能来自上一次运行实例的失败记录（30 分钟有效），
            // 而当前实例启动时验签可能已通过，缓存不应阻断前端获取正确的许可证摘要。
            boolean cacheSaysInvalid = "false".equalsIgnoreCase(cachedResult);

            LicenseValidator.LicenseInfo licenseInfo = BytedeskProperties.getInstance().validateLicense();
            BytedeskPropertiesResponse.License plainLicense = new BytedeskPropertiesResponse.License();
            if (licenseInfo != null && licenseInfo.isValid()) {
                // 验签通过：覆盖可能存在的旧缓存（包括将 "false" 刷新为 "true"）
                if (!"true".equalsIgnoreCase(cachedResult)) {
                    stringRedisTemplate.opsForValue().set(cacheKey, "true",
                            java.time.Duration.ofHours(1));
                }
                if (cacheSaysInvalid) {
                    log.info("License re-validated successfully, stale cache cleared: edition={}, expiry={}",
                            licenseInfo.getEdition(), licenseInfo.getExpiryDate());
                }

                // 明文摘要：签名有效；valid 需叠加过期判断（与后端拦截器语义一致）
                plainLicense.setEdition(licenseInfo.getEdition());
                plainLicense.setExpiryDate(licenseInfo.getExpiryDate());
                plainLicense.setUserType(licenseInfo.getUserType());
                plainLicense.setValid(!licenseInfo.isExpired());
                // 展示字段：被授权人/描述/授权IP/授权域名（许可证载荷中已存在，随明文摘要同步下发）
                // uid：新格式 licenseKey 载荷末尾携带，旧格式许可证为空字符串（前端据此判定兼容）
                plainLicense.setUid(licenseInfo.getUid() != null ? licenseInfo.getUid() : "");
                plainLicense.setName(licenseInfo.getName() != null ? licenseInfo.getName() : "");
                plainLicense.setDescription(licenseInfo.getDescription() != null ? licenseInfo.getDescription() : "");
                plainLicense.setServerIps(splitCsvToList(licenseInfo.getServerIps()));
                plainLicense.setServerDomains(splitCsvToList(licenseInfo.getServerDomains()));
            } else {
                stringRedisTemplate.opsForValue().set(cacheKey, "false",
                        java.time.Duration.ofMinutes(30));
                log.warn("License validation FAILED, marking as invalid");
                // 不下发 licenseKey（包括哨兵值），前端依据明文摘要 valid=false 回退社区版
                plainLicense.setValid(false);
            }
            bytedeskPropertiesResponse.setLicense(plainLicense);
        }

        BytedeskPropertiesResponse.Ai ai = new BytedeskPropertiesResponse.Ai();

        // spring.ai.* 负责供应商/模型能力配置；bytedesk.ai.* 负责系统表单和执行接口共享的默认值。
        BytedeskPropertiesResponse.Tts tts = new BytedeskPropertiesResponse.Tts();
        tts.setProvider(environment.getProperty("spring.ai.model.audio", FALLBACK_TTS_PROVIDER));
        tts.setModel(environment.getProperty("spring.ai.dashscope.audio.synthesis.options.model", FALLBACK_TTS_MODEL));
        tts.setVoice(environment.getProperty("spring.ai.dashscope.audio.synthesis.options.voice", FALLBACK_TTS_VOICE));
        tts.setLanguage(environment.getProperty("bytedesk.ai.tts.language", FALLBACK_TTS_LANGUAGE));
        tts.setAudioFormat(environment.getProperty("bytedesk.ai.tts.audio-format", FALLBACK_TTS_AUDIO_FORMAT));
        ai.setTts(tts);

        BytedeskPropertiesResponse.Asr asr = new BytedeskPropertiesResponse.Asr();
        asr.setProvider(environment.getProperty("spring.ai.model.audio", FALLBACK_ASR_PROVIDER));
        asr.setModel(environment.getProperty("spring.ai.dashscope.audio.transcription.options.model", FALLBACK_ASR_MODEL));
        asr.setRealtimeModel(environment.getProperty("spring.ai.dashscope.audio.transcription.realtime-model", FALLBACK_ASR_REALTIME_MODEL));
        asr.setSourceFormat(environment.getProperty("bytedesk.ai.asr.source-format", FALLBACK_ASR_SOURCE_FORMAT));
        asr.setTimeoutMs(environment.getProperty("bytedesk.ai.asr.timeout-ms", Long.class, FALLBACK_ASR_TIMEOUT_MS));
        asr.setPollIntervalMs(environment.getProperty("bytedesk.ai.asr.poll-interval-ms", Long.class, FALLBACK_ASR_POLL_INTERVAL_MS));
        ai.setAsr(asr);

        bytedeskPropertiesResponse.setAi(ai);
        
        return ResponseEntity.ok(JsonResult.success(bytedeskPropertiesResponse));
    }

    /**
     * 逗号分隔字符串 → 去空白后的列表（null/空白返回空列表）。
     */
    private static java.util.List<String> splitCsvToList(String csv) {
        java.util.List<String> result = new java.util.ArrayList<>();
        if (csv == null || csv.isBlank()) {
            return result;
        }
        for (String item : csv.split(",")) {
            String value = item == null ? "" : item.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result;
    }
    
}