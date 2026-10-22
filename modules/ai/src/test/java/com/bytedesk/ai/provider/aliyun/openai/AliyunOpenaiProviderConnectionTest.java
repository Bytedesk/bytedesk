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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.ai.llm_provider.LlmProviderEntity;
import com.bytedesk.ai.llm_provider.LlmProviderRestService;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.llm.LlmProviderConstants;

/**
 * 连接解析规则测试（规划 4.2/4.3，验收矩阵“租户与凭据”/“默认关闭”）。
 */
@ExtendWith(MockitoExtension.class)
class AliyunOpenaiProviderConnectionTest {

    @Mock
    private LlmProviderRestService llmProviderRestService;

    private AliyunOpenaiProviderConnection connection;

    @BeforeEach
    void setUp() {
        connection = new AliyunOpenaiProviderConnection(llmProviderRestService);
        ReflectionTestUtils.setField(connection, "chatEnabled", true);
        ReflectionTestUtils.setField(connection, "globalApiKey", "");
        ReflectionTestUtils.setField(connection, "globalBaseUrl", "");
    }

    private LlmProviderEntity provider(String level, String type, boolean enabled, String apiKey) {
        LlmProviderEntity entity = new LlmProviderEntity();
        entity.setUid("provider-uid-1");
        entity.setType(type);
        entity.setLevel(level);
        entity.setEnabled(enabled);
        entity.setApiKey(apiKey);
        entity.setBaseUrl("");
        return entity;
    }

    @Test
    void disabledChatRejectsBeforeAnyLookup() {
        ReflectionTestUtils.setField(connection, "chatEnabled", false);
        RobotLlm llm = RobotLlm.builder().textProvider(LlmProviderConstants.ALIYUN_OPENAI)
                .textProviderUid("provider-uid-1").textModel("qwen3.8-flash").build();

        assertThatThrownBy(() -> connection.requireProvider(llm))
                .isInstanceOf(AliyunOpenaiProviderException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void missingUidRejected() {
        RobotLlm llm = RobotLlm.builder().textProvider(LlmProviderConstants.ALIYUN_OPENAI).build();

        assertThatThrownBy(() -> connection.requireProvider(llm))
                .isInstanceOf(AliyunOpenaiProviderException.class)
                .hasMessageContaining("textProviderUid");
    }

    @Test
    void typeMismatchRejected() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.DASHSCOPE, true, "key-1");
        when(llmProviderRestService.findByUid("provider-uid-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> connection.requireProviderByUid("provider-uid-1"))
                .isInstanceOf(AliyunOpenaiProviderException.class)
                .hasMessageContaining("type mismatch");
    }

    @Test
    void disabledProviderRejected() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.ALIYUN_OPENAI, false,
                "key-1");
        when(llmProviderRestService.findByUid("provider-uid-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> connection.requireProviderByUid("provider-uid-1"))
                .isInstanceOf(AliyunOpenaiProviderException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void orgProviderDoesNotInheritGlobalKey() {
        LlmProviderEntity entity = provider(LevelEnum.ORGANIZATION.name(), LlmProviderConstants.ALIYUN_OPENAI, true, "");
        ReflectionTestUtils.setField(connection, "globalApiKey", "global-key");

        assertThat(connection.resolveApiKey(entity)).isNull();
    }

    @Test
    void platformProviderFallsBackToGlobalKey() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.ALIYUN_OPENAI, true, "");
        ReflectionTestUtils.setField(connection, "globalApiKey", "global-key");

        assertThat(connection.resolveApiKey(entity)).isEqualTo("global-key");
    }

    @Test
    void placeholderKeyTreatedAsMissing() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.ALIYUN_OPENAI, true,
                "sk-xxx");
        ReflectionTestUtils.setField(connection, "globalApiKey", "");

        assertThat(connection.resolveApiKey(entity)).isNull();
    }

    @Test
    void providerOwnKeyWins() {
        LlmProviderEntity entity = provider(LevelEnum.ORGANIZATION.name(), LlmProviderConstants.ALIYUN_OPENAI, true,
                "org-key");
        ReflectionTestUtils.setField(connection, "globalApiKey", "global-key");

        assertThat(connection.resolveApiKey(entity)).isEqualTo("org-key");
    }

    @Test
    void blankBaseUrlUsesCompatibleDefault() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.ALIYUN_OPENAI, true,
                "key-1");

        assertThat(connection.resolveBaseUrl(entity))
                .isEqualTo("https://dashscope.aliyuncs.com/compatible-mode/v1");
    }

    @Test
    void explicitBaseUrlNotRewritten() {
        LlmProviderEntity entity = provider(LevelEnum.PLATFORM.name(), LlmProviderConstants.ALIYUN_OPENAI, true,
                "key-1");
        entity.setBaseUrl("https://ws-demo.cn-beijing.maas.aliyuncs.com/compatible-mode/v1");

        assertThat(connection.resolveBaseUrl(entity))
                .isEqualTo("https://ws-demo.cn-beijing.maas.aliyuncs.com/compatible-mode/v1");
    }

    @Test
    void unknownProviderUidRejectedWithoutFallback() {
        when(llmProviderRestService.findByUid(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> connection.requireProviderByUid("missing-uid"))
                .isInstanceOf(AliyunOpenaiProviderException.class)
                .hasMessageContaining("not found");
    }

}
