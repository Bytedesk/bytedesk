package com.bytedesk.ai.provider.volcengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

import com.volcengine.ark.runtime.models.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionStreamOptions;
import com.volcengine.ark.runtime.models.chat.Thinking;
import com.volcengine.ark.runtime.models.chat.ThinkingMode;

/**
 * 方舟官方 SDK（com.volcengine:ark-runtime）迁移验证：
 * https://docs.volcengine.com/docs/ark/migrate-ark-runtime-sdk
 */
class VolcengineArkSdkMigrationTest {

    private static final Path MAIN_SOURCE_DIR = Path.of("src/main/java/com/bytedesk/ai/provider/volcengine");

    @Test
    void volcengineMainSourcesShouldNotReferenceOpenAiCompatibleClient() throws IOException {
        try (Stream<Path> paths = Files.walk(MAIN_SOURCE_DIR)) {
            List<String> legacyReferences = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .flatMap(this::readLinesSafely)
                    .filter(line -> line.contains("org.springframework.ai.openai.OpenAiChatModel")
                            || line.contains("org.springframework.ai.openai.OpenAiChatOptions"))
                    .collect(Collectors.toList());

            assertTrue(legacyReferences.isEmpty(), () -> "Legacy OpenAI-compatible references still exist: "
                    + legacyReferences);
        }
    }

    @Test
    void normalizesLegacyApiV3BaseUrlToSdkRoot() {
        // 历史 OpenAI 兼容配置带 /api/v3 后缀，SDK 内部自行拼接 /api/v3/*
        assertEquals("https://ark.cn-beijing.volces.com",
                SpringAIVolcengineChatConfig.normalizeBaseUrl("https://ark.cn-beijing.volces.com/api/v3"));
        assertEquals("https://ark.cn-beijing.volces.com",
                SpringAIVolcengineChatConfig.normalizeBaseUrl("https://ark.cn-beijing.volces.com/api/v3/"));
        assertEquals("https://ark.cn-beijing.volces.com",
                SpringAIVolcengineChatConfig.normalizeBaseUrl("https://ark.cn-beijing.volces.com"));
        assertEquals("https://ark.cn-beijing.volces.com",
                SpringAIVolcengineChatConfig.normalizeBaseUrl(null));
        assertEquals("https://ark.example.com",
                SpringAIVolcengineChatConfig.normalizeBaseUrl("https://ark.example.com/"));
    }

    @Test
    void buildsSyncAndStreamRequestsWithoutApiKeyLeak() {
        // ArkService 由 config/service 持有凭据；ChatModel 只组装请求体（不发调用）
        ArkChatModel model = new ArkChatModel(dummyArkApi(), ArkChatOptions.builder()
                .model("doubao-1-5-pro-32k-250115")
                .temperature(0.7)
                .topP(0.9)
                .maxTokens(1024)
                .build());

        Prompt prompt = new Prompt(List.of(
                new SystemMessage("角色：资深客服专家"),
                new UserMessage("你好")));

        ChatCompletionRequest syncRequest = invokeCreateRequest(model, prompt, false);
        assertEquals("doubao-1-5-pro-32k-250115", syncRequest.getModel());
        assertNull(syncRequest.getStream());
        assertNull(syncRequest.getStreamOptions());
        assertEquals(2, syncRequest.getMessages().size());
        assertEquals("system", roleOf(syncRequest.getMessages().get(0)));
        assertEquals("user", roleOf(syncRequest.getMessages().get(1)));

        ChatCompletionRequest streamRequest = invokeCreateRequest(model, prompt, true);
        assertEquals(Boolean.TRUE, streamRequest.getStream());
        ChatCompletionStreamOptions streamOptions = streamRequest.getStreamOptions();
        assertNotNull(streamOptions);
        assertEquals(Boolean.TRUE, streamOptions.getIncludeUsage());
    }

    @Test
    void buildsThinkingRequestOnlyWhenExplicitlyEnabled() {
        ArkChatModel model = new ArkChatModel(dummyArkApi(), ArkChatOptions.builder()
                .model("doubao-seed-1-6")
                .build());

        // 未显式设置 thinking：不透传（非思考模型设置会导致 400）
        Prompt prompt = new Prompt(new UserMessage("hi"));
        ChatCompletionRequest withoutThinking = invokeCreateRequest(model, prompt, false);
        assertNull(withoutThinking.getThinking());

        // 显式开启：thinking.type=enabled
        Prompt thinkingPrompt = new Prompt(List.of(new UserMessage("hi")),
                ArkChatOptions.builder().model("doubao-seed-1-6").enableThinking(true).build());
        ChatCompletionRequest withThinking = invokeCreateRequest(model, thinkingPrompt, false);
        Thinking thinking = withThinking.getThinking();
        assertNotNull(thinking);
        assertEquals(ThinkingMode.ENABLED, thinking.getType());
    }

    @Test
    void arkOptionsShouldSatisfyToolCallingContract() {
        ArkChatOptions options = ArkChatOptions.builder()
                .model("doubao-1-5-pro-32k-250115")
                .temperature(0.7)
                .topP(0.9)
                .maxTokens(512)
                .enableThinking(false)
                .toolChoice("auto")
                .build();

        assertTrue(options instanceof ToolCallingChatOptions);
        assertEquals("doubao-1-5-pro-32k-250115", options.getModel());
        assertEquals(0.7, options.getTemperature());
        assertEquals(0.9, options.getTopP());
        assertEquals(512, options.getMaxTokens());
        assertEquals(Boolean.FALSE, options.getEnableThinking());
        assertEquals("auto", options.getToolChoice());
        assertNotNull(options.getToolCallbacks());
        assertNotNull(options.getToolNames());
        assertNotNull(options.getToolContext());

        // mutate 保字段
        ArkChatOptions mutated = options.mutate().build();
        assertEquals(options.getModel(), mutated.getModel());
        assertEquals(options.getEnableThinking(), mutated.getEnableThinking());
        assertEquals(options.getToolChoice(), mutated.getToolChoice());
    }

    private String roleOf(ChatCompletionRequestMessage message) {
        return message.getRole() != null ? message.getRole().toString() : null;
    }

    /** 仅用于满足构造器非空校验的 ArkApi 实例（测试中不发起真实调用）。 */
    private com.volcengine.ark.runtime.service.ArkApi dummyArkApi() {
        return ArkServiceFactory.createApi("test-api-key", "https://ark.cn-beijing.volces.com");
    }

    /** 通过反射调用私有 createRequest（ArkService 为 null 时仅组装请求体，不发起调用）。 */
    private ChatCompletionRequest invokeCreateRequest(ArkChatModel model, Prompt prompt, boolean stream) {
        try {
            java.lang.reflect.Method method = ArkChatModel.class
                    .getDeclaredMethod("createRequest", Prompt.class, boolean.class);
            method.setAccessible(true);
            return (ChatCompletionRequest) method.invoke(model, prompt, stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke createRequest", e);
        }
    }

    private Stream<String> readLinesSafely(Path path) {
        try {
            return Files.readAllLines(path).stream().map(line -> path.getFileName() + ": " + line.trim());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read " + path, exception);
        }
    }
}
