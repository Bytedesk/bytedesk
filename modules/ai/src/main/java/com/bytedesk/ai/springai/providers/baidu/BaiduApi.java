/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-11 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 13:04:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.baidu;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 百度智能云API配置
 */
public class BaiduApi {
    
    // 私有构造函数，防止实例化
    private BaiduApi() {}
    
    /**
     * 创建适用于百度智能云的OpenAiApi实例
     * 
     * @param baseUrl 百度智能云API的基础URL
     * @param apiKey API密钥
     * @return 配置好的OpenAiApi实例
     */
    public static OpenAiApi create(String baseUrl, String apiKey) {
        // 百度API路径直接是/chat/completions，不需要v1前缀
        String chatCompletionsPath = "/chat/completions";
        String embeddingsPath = "/embeddings"; // 假设百度API也支持embeddings
        
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
     * 创建百度智能云API实例，带更多自定义配置选项
     */
    public static OpenAiApi create(String baseUrl, String apiKey, 
            RestClient.Builder restClientBuilder, 
            WebClient.Builder webClientBuilder, 
            ResponseErrorHandler responseErrorHandler) {
        
        // 百度API路径直接是/chat/completions，不需要v1前缀
        String chatCompletionsPath = "/chat/completions";
        String embeddingsPath = "/embeddings"; // 假设百度API也支持embeddings
        
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
