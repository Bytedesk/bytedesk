package com.bytedesk.ai.provider.volcengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import com.volcengine.ark.runtime.exception.ArkHttpException;
import com.volcengine.ark.runtime.models.chat.ChatCompletionMessageContent;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestMessageType;
import com.volcengine.ark.runtime.models.chat.ChatCompletionRequestUserMessage;
import com.volcengine.ark.runtime.models.chat.ChatCompletionStreamResponse;
import com.volcengine.ark.runtime.service.ArkApi;

/**
 * 流式链路回归验证：ArkServiceFactory.streamChatCompletion 全链路
 * （Retrofit/okHttp → ArkSseCallback SSE 解析 → ChatCompletionStreamResponse）。
 *
 * <p>背景：SDK 官方 {@code ResponseBodyCallback} 静态初始化回调
 * {@code ArkService.defaultObjectMapper()}，后者引用 Jackson 2.21 已移除的
 * {@code PropertyNamingStrategy.SNAKE_CASE}，首次流式订阅即抛
 * {@code NoSuchFieldError}。本用例走真实 HTTP 流式响应，若回归必失败。</p>
 */
class ArkServiceFactoryStreamTest {

    private HttpServer server;

    /** 由测试 HTTP 服务端捕获的请求头，用于断言拦截器链行为。 */
    private final java.util.Map<String, String> capturedHeaders = new java.util.concurrent.ConcurrentHashMap<>();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldStreamSseChunksWithoutTriggeringArkServiceStaticInit() {
        // 每个数据块后需空行（SSE 事件分隔），[DONE] 结束流
        String sseBody = """
                data: {"id":"chat-1","object":"chat.completion.chunk","created":1730000000,"model":"doubao-1-5-pro-32k","choices":[{"index":0,"delta":{"role":"assistant","content":"你好"},"finish_reason":null}]}

                data: {"id":"chat-1","object":"chat.completion.chunk","created":1730000000,"model":"doubao-1-5-pro-32k","choices":[{"index":0,"delta":{"content":"，世界"},"finish_reason":"stop"}]}

                data: [DONE]

                """;
        serve(sseBody, "text/event-stream; charset=utf-8", 200);

        ArkApi api = ArkServiceFactory.createApi("test-api-key", baseUrl());
        ChatCompletionRequest request = request("你好");

        List<ChatCompletionStreamResponse> chunks = ArkServiceFactory
                .streamChatCompletion(api, request)
                .toList()
                .blockingGet();

        assertEquals(2, chunks.size());
        String content = chunks.stream()
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .collect(Collectors.joining());
        assertEquals("你好，世界", content);
        assertTrue(chunks.get(0).getId().startsWith("chat-1"));

        // 拦截器链验证：ArkRequestIdInterceptor 生成请求 ID + User-Agent；
        // AuthenticationInterceptor 注入 API Key（此前 NoClassDefFoundError 会直接失败）
        String clientRequestId = capturedHeaders.getOrDefault("x-client-request-id", "");
        assertTrue(clientRequestId.length() >= 20, "X-Client-Request-Id should be generated: " + clientRequestId);
        assertTrue(clientRequestId.chars().allMatch(c -> Character.isLetterOrDigit(c)));
        assertTrue(capturedHeaders.getOrDefault("user-agent", "").startsWith("ark-runtime-java/"));
        assertEquals("Bearer test-api-key", capturedHeaders.get("authorization"));
    }

    @Test
    void shouldTranslateNonSuccessResponseToArkHttpException() {
        String errorBody = "{\"error\":{\"code\":\"InvalidApiKey\",\"message\":\"invalid api key\","
                + "\"param\":\"\",\"type\":\"invalid_request_error\"}}";
        serve(errorBody, "application/json", 401);

        ArkApi api = ArkServiceFactory.createApi("bad-key", baseUrl());

        ArkServiceFactory.streamChatCompletion(api, request("hi"))
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertError(throwable -> {
                    ArkHttpException exception = assertInstanceOf(ArkHttpException.class, throwable);
                    assertEquals(401, exception.statusCode);
                    return exception.getMessage().contains("invalid api key");
                });
    }

    private void serve(String body, String contentType, int status) {
        server.createContext("/", exchange -> {
            exchange.getRequestHeaders().forEach((name, values) ->
                    capturedHeaders.put(name.toLowerCase(), values.get(0)));
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
    }

    private String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private ChatCompletionRequest request(String userText) {
        return ChatCompletionRequest.builder()
                .model("doubao-1-5-pro-32k")
                .messages(List.of(ChatCompletionRequestUserMessage.builder()
                        .role(ChatCompletionRequestMessageType.USER)
                        .content(ChatCompletionMessageContent.ofString(userText))
                        .build()))
                .build();
    }
}
