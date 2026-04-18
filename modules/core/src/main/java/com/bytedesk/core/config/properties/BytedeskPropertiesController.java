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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    public BytedeskPropertiesController(Environment environment) {
        this.environment = environment;
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
    
}