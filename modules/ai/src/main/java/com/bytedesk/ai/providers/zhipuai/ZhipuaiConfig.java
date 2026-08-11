package com.bytedesk.ai.providers.zhipuai;

import java.util.concurrent.TimeUnit;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
// import org.springframework.ai.chat.client.ChatClient;        // 已注释：Chat 走 ZhipuaiService 直调 z-ai-sdk
// import org.springframework.ai.chat.prompt.ChatOptions;       // 已注释：Chat 走 ZhipuaiService 直调 z-ai-sdk
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.ai.providers.zhipuai.chat.ZhipuaiChatModel;
// import com.bytedesk.ai.providers.zhipuai.chat.ZhipuaiChatModel;  // 已注释：Chat 走 ZhipuaiService 直调 z-ai-sdk
import com.bytedesk.ai.providers.zhipuai.embedding.ZhipuaiEmbeddingModel;
import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;
// import com.bytedesk.ai.springai.config.ChatClientBuilderFactory;   // 已注释：Chat 走 ZhipuaiService 直调 z-ai-sdk
import com.bytedesk.core.llm.LlmProviderConstants;

import ai.z.openapi.ZhipuAiClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 智谱AI统一配置类
 * 合并了原 ZhipuaiChatConfig（z-ai-sdk ZhipuAiClient Bean）与 Spring AI 适配 Bean。
 * 参考 z-ai-sdk-java: https://github.com/zai-org/z-ai-sdk-java
 *
 * <p>Bean 层次：</p>
 * <ol>
 *   <li><b>zhipuAiClient</b> — 原生 z-ai-sdk {@link ZhipuAiClient}，所有上层能力的基础</li>
 *   <li><b>zhiPuAiEmbeddingModel</b> — 基于 z-ai-sdk 的 Spring AI {@code EmbeddingModel} 适配</li>
 * </ol>
 * <p>以下 Spring AI ChatModel/ChatClient 适配 Bean 已注释，Chat 走 {@code ZhipuaiService} 直调 z-ai-sdk：</p>
 * <ul>
 *   <li><del>{@code bytedeskZhipuaiChatModel}</del></li>
 *   <li><del>{@code bytedeskZhipuaiChatClient}</del></li>
 * </ul>
 */
@Slf4j
@Data
@Configuration
public class ZhipuaiConfig {

    // ────────────────────────────── z-ai-sdk 客户端配置 ──────────────────────────────

    @Value("${spring.ai.zhipuai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.zhipuai.chat.options.model:glm-4.5-flash}")
    private String model;

    @Value("${spring.ai.zhipuai.chat.options.temperature:0.7}")
    private double temperature;

    @Value("${spring.ai.zhipuai.chat.options.top-p:0.9}")
    private double topP;

    @Value("${spring.ai.zhipuai.chat.options.max-tokens:4096}")
    private int maxTokens;

    @Value("${spring.ai.zhipuai.request-timeout:0}")
    private int requestTimeout;

    @Value("${spring.ai.zhipuai.connection-timeout:30}")
    private int connectionTimeout;

    @Value("${spring.ai.zhipuai.read-timeout:10}")
    private int readTimeout;

    @Value("${spring.ai.zhipuai.write-timeout:10}")
    private int writeTimeout;

    @Value("${spring.ai.zhipuai.max-idle-connections:8}")
    private int maxIdleConnections;

    @Value("${spring.ai.zhipuai.keep-alive-duration:1}")
    private int keepAliveDuration;

    // ──────────────────────── z-ai-sdk ZhipuAiClient Bean ──────────────────────────

    /**
     * 原生 z-ai-sdk 客户端 Bean。
     * 参考 SDK 源码 {@code ZhipuAiClient.Builder} / {@code AbstractBuilder}：
     * <ul>
     *   <li>{@code .ofZHIPU()} — 使用智谱官方端点</li>
     *   <li>{@code .enableTokenCache()} — 启用 JWT 令牌缓存</li>
     *   <li>{@code .networkConfig(request, connect, read, write, unit)} — 超时配置</li>
     *   <li>{@code .connectionPool(maxIdle, keepAlive, unit)} — 连接池配置</li>
     * </ul>
     */
    @Bean("zhipuAiClient")
    @ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ZhipuAiClient zhipuAiClient() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Zhipuai API key is not configured");
            return null;
        }

        log.info("Initializing ZhipuAiClient with model: {}", model);

        try {
            int minimumRequestTimeout = connectionTimeout + readTimeout + writeTimeout + 5;
            int effectiveRequestTimeout = requestTimeout > 0
                ? Math.max(requestTimeout, minimumRequestTimeout)
                : minimumRequestTimeout;

            if (requestTimeout > 0 && requestTimeout < minimumRequestTimeout) {
                log.warn(
                    "Configured spring.ai.zhipuai.request-timeout={}s is lower than the minimum safe timeout {}s; using {}s instead",
                    requestTimeout,
                    minimumRequestTimeout,
                    effectiveRequestTimeout);
            }

            return ZhipuAiClient.builder()
                .ofZHIPU()
                    .apiKey(apiKey)
                .enableTokenCache()
                .networkConfig(
                    effectiveRequestTimeout,
                    connectionTimeout,
                    readTimeout,
                    writeTimeout,
                    TimeUnit.SECONDS)
                .connectionPool(
                    maxIdleConnections,
                    keepAliveDuration,
                    TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            // zai-sdk 会在 setApiKey 时做格式校验；无效 key 不应阻塞应用启动
            log.warn("Failed to initialize ZhipuAiClient due to invalid api key, ZhipuAI features will be disabled: {}",
                    e.getMessage());
            return null;
        } catch (LinkageError e) {
            log.warn(
                    "Failed to initialize ZhipuAiClient due to SDK/runtime incompatibility, ZhipuAI features will be disabled: {}",
                    e.getMessage());
            return null;
        }
    }

    // ──────────── Spring AI ChatModel/ChatClient 适配 Bean（已注释）─────────────
    // 说明：Chat 功能走 ZhipuaiService 直调 z-ai-sdk，无需 Spring AI ChatModel/ChatClient 适配层。
    // 如需恢复（例如用于 ChatClient 统一编排），取消以下注释并恢复对应 import。

    @Bean("bytedeskZhipuaiChatModel")
    @ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
    ZhipuaiChatModel bytedeskZhipuaiChatModel(
            ObjectProvider<ZhipuAiClient> clientProvider) {
        ZhipuAiClient client = clientProvider.getIfAvailable();
        if (client == null) {
            return null;
        }
        ChatOptions options = ChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .topP(topP)
                .maxTokens(maxTokens)
                .build();
        return new ZhipuaiChatModel(client, options);
    }

    @Bean("bytedeskZhipuaiChatClient")
    @ConditionalOnProperty(prefix = "spring.ai.zhipuai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
    ChatClient bytedeskZhipuaiChatClient(
            ObjectProvider<ZhipuaiChatModel> chatModelProvider,
            ChatClientBuilderFactory chatClientBuilderFactory) {
        ZhipuaiChatModel chatModel = chatModelProvider.getIfAvailable();
        if (chatModel == null) {
            return null;
        }
        return chatClientBuilderFactory.builder(chatModel).build();
    }

    // ─────────────────── Spring AI EmbeddingModel 适配 Bean ─────────────────────────

    @Bean("zhiPuAiEmbeddingModel")
    @ConditionalOnProperty(name = "spring.ai.model.embedding", havingValue = LlmProviderConstants.ZHIPUAI, matchIfMissing = false)
    @ConditionalOnProperty(prefix = "spring.ai.zhipuai.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
    EmbeddingModel zhiPuAiEmbeddingModel(
            ObjectProvider<ZhipuAiClient> clientProvider,
            org.springframework.core.env.Environment environment) {
        ZhipuAiClient client = clientProvider.getIfAvailable();
        if (client == null) {
            return null;
        }
        String embeddingModel = environment.getProperty("spring.ai.zhipuai.embedding.options.model", "embedding-2");
        Integer dimensions = environment.getProperty("spring.ai.zhipuai.embedding.options.dimensions", Integer.class);
        return new ZhipuaiEmbeddingModel(client, embeddingModel, dimensions);
    }
}