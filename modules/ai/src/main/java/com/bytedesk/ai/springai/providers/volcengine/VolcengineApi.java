/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-11 15:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 15:11:41
 * @Description: 火山引擎API适配器，提供工厂方法创建正确的API实例
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.volcengine;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 火山引擎API工具类，用于创建适合火山引擎的OpenAiApi实例
 */
public class VolcengineApi {

    // 私有构造函数，防止实例化
    private VolcengineApi() {}

    /**
     * 创建火山引擎API实例
     * @param baseUrl 基础URL，例如：https://ark.cn-beijing.volces.com/api/v3
     * @param apiKey API密钥
     * @return OpenAiApi实例，配置为使用火山引擎的路径
     */
    public static OpenAiApi create(String baseUrl, String apiKey) {
        // 火山引擎的API路径没有v1前缀
        String chatCompletionsPath = "/chat/completions";
        String embeddingsPath = "/embeddings"; // 假设火山引擎也支持embeddings
        
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .completionsPath(chatCompletionsPath)
                .embeddingsPath(embeddingsPath)
                .headers(headers)
                .build();
    }
    
    /**
     * 创建火山引擎API实例，带更多自定义配置选项
     */
    public static OpenAiApi create(String baseUrl, String apiKey, 
            RestClient.Builder restClientBuilder, 
            WebClient.Builder webClientBuilder, 
            ResponseErrorHandler responseErrorHandler) {
        
        // 火山引擎的API路径没有v1前缀
        String chatCompletionsPath = "/chat/completions";
        String embeddingsPath = "/embeddings"; // 假设火山引擎也支持embeddings
        
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .completionsPath(chatCompletionsPath)
                .embeddingsPath(embeddingsPath)
                .headers(headers)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
    }
}
