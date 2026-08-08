package com.bytedesk.ai.providers.dashscope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;

import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.bytedesk.ai.providers.dashscope.chat.DashScopeChatModel;
import com.bytedesk.ai.providers.dashscope.chat.DashScopeChatOptions;
import com.bytedesk.ai.service.ChatTokenUsage;
import com.bytedesk.ai.service.TokenUsageHelper;

class BytedeskDashScopeChatModelTest {

    @Test
    void normalizesCompatibleDashScopeBaseUrlToNativeRoot() {
    assertEquals(
            "https://dashscope.aliyuncs.com/api/v1",
        DashScopeBaseUrlSupport.normalize("https://dashscope.aliyuncs.com/compatible-mode/v1"));
    assertEquals(
            "https://dashscope.aliyuncs.com/api/v1",
        DashScopeBaseUrlSupport.normalize("https://dashscope.aliyuncs.com/v1/chat/completions"));
    assertEquals(
            "https://proxy.example.com/dashscope",
        DashScopeBaseUrlSupport.normalize("https://proxy.example.com/dashscope/"));
        assertEquals(
            "https://dashscope.aliyuncs.com/api/v1",
            DashScopeBaseUrlSupport.normalize("https://dashscope.aliyuncs.com"));
    }

    @Test
    void mapsDashScopeUsageToSpringAiMetadataAndTokenUsageHelperKeys() throws Exception {
        GenerationOutput output = new GenerationOutput();
        output.setText("hello");
        output.setFinishReason("stop");

        GenerationUsage usage = GenerationUsage.builder()
                .inputTokens(11)
                .outputTokens(7)
                .totalTokens(18)
                .build();

        GenerationResult result = newGenerationResult();
        result.setRequestId("req-1");
        result.setOutput(output);
        result.setUsage(usage);
        result.setStatusCode(200);
        result.setCode("ok");
        result.setMessage("success");

        DashScopeChatModel model = new DashScopeChatModel(
                "https://dashscope.aliyuncs.com",
                "test-api-key",
                DashScopeChatOptions.builder().model("qwen-test").build());

        ChatResponse response = toChatResponse(model, result);

        assertEquals("hello", response.getResult().getOutput().getText());
        assertEquals(Integer.valueOf(11), response.getMetadata().getUsage().getPromptTokens());
        assertEquals(Integer.valueOf(7), response.getMetadata().getUsage().getCompletionTokens());
        assertEquals(Integer.valueOf(18), response.getMetadata().getUsage().getTotalTokens());
        assertEquals(Integer.valueOf(11), response.getMetadata().get("prompt_tokens"));
        assertEquals(Integer.valueOf(7), response.getMetadata().get("completion_tokens"));
        assertEquals(Integer.valueOf(18), response.getMetadata().get("total_tokens"));

        ChatTokenUsage extracted = new TokenUsageHelper(null).extractTokenUsage(response);
        assertEquals(Long.valueOf(11), extracted.getPromptTokens());
        assertEquals(Long.valueOf(7), extracted.getCompletionTokens());
        assertEquals(Long.valueOf(18), extracted.getTotalTokens());
    }

    private static GenerationResult newGenerationResult() throws Exception {
        Constructor<GenerationResult> constructor = GenerationResult.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static ChatResponse toChatResponse(DashScopeChatModel model, GenerationResult result)
            throws Exception {
        Method method = DashScopeChatModel.class.getDeclaredMethod("toChatResponse", GenerationResult.class);
        method.setAccessible(true);
        return (ChatResponse) method.invoke(model, result);
    }
}