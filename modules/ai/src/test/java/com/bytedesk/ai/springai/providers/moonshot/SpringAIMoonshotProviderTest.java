package com.bytedesk.ai.springai.providers.moonshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.service.BaseModuleAIServiceProvider;
import com.bytedesk.ai.springai.providers.moonshot.api.MoonshotChatOptions;
import com.bytedesk.core.llm.LlmProviderConstants;

class SpringAIMoonshotProviderTest {

    private static <T extends ChatOptions> T duplicate(ChatOptions options) {
        return options.copy();
    }

    @Test
    void shouldUseKimiK26AsDefaultMoonshotModel() {
        SpringAIMoonshotChatConfig config = new SpringAIMoonshotChatConfig();
        ReflectionTestUtils.setField(config, "baseUrl", "https://api.moonshot.cn");
        ReflectionTestUtils.setField(config, "apiKey", "sk-test");
        ReflectionTestUtils.setField(config, "model", SpringAIMoonshotService.DEFAULT_MOONSHOT_MODEL);
        ReflectionTestUtils.setField(config, "temperature", 0.7D);

        MoonshotChatOptions options = config.moonshotChatOptions();

        assertEquals(SpringAIMoonshotService.DEFAULT_MOONSHOT_MODEL, options.getModel());
        assertEquals(1.0D, options.getTemperature());
    }

    @Test
    void shouldOmitDeprecatedMaxTokensAndClampDynamicSamplingOptions() {
        SpringAIMoonshotService service = new SpringAIMoonshotService();
        RobotLlm llm = RobotLlm.builder()
                .textModel("kimi-k2.6")
                .maxTokens(4096)
            .temperature(0.2D)
                .topP(-0.2D)
                .enableThinking(false)
                .build();

        MoonshotChatOptions options = ReflectionTestUtils.invokeMethod(service, "createDynamicOptions", llm);

        assertEquals("kimi-k2.6", options.getModel());
        assertEquals(1.0D, options.getTemperature());
        assertEquals(0.95D, options.getTopP());
        assertNull(options.getMaxTokens());
        assertEquals(4096, options.getMaxCompletionTokens());
        assertEquals(MoonshotChatOptions.Thinking.DISABLED, options.getThinking().type());
    }

    @Test
    void shouldCopyMoonshotOptionsThroughChatOptionsContract() {
        MoonshotChatOptions options = MoonshotChatOptions.builder()
                .model("kimi-k2.6")
                .maxCompletionTokens(2048)
                .temperature(1.0D)
                .thinking(new MoonshotChatOptions.Thinking(MoonshotChatOptions.Thinking.DISABLED))
                .build();

        MoonshotChatOptions copy = duplicate(options);

        assertNotSame(options, copy);
        assertEquals("kimi-k2.6", copy.getModel());
        assertEquals(2048, copy.getMaxCompletionTokens());
        assertEquals(1.0D, copy.getTemperature());
        assertEquals(MoonshotChatOptions.Thinking.DISABLED, copy.getThinking().type());
    }

    @Test
    void shouldRegisterMoonshotServiceInBaseProviderRegistry() {
        BaseModuleAIServiceProvider provider = new BaseModuleAIServiceProvider(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new SpringAIMoonshotService()),
                Optional.empty());

        provider.registerServices();

        assertTrue(provider.getSupportedProviders().contains(LlmProviderConstants.MOONSHOT));
        assertTrue(provider.getService(LlmProviderConstants.MOONSHOT) instanceof SpringAIMoonshotService);
    }
}