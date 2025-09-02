/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 22:17:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 22:48:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openrouter;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * OpenRouter REST 服务
 * 提供 OpenRouter API 的管理功能
 */
@Slf4j
@Service
public class SpringAIOpenrouterRestService {

    private static final String DEFAULT_OPENROUTER_API_URL = "https://openrouter.ai/api/v1";

    /**
     * 获取可用模型列表
     * 直接调用 OpenRouter API 获取真实的模型列表
     * 注意：此接口可以匿名访问，无需 API Key
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getModels(OpenrouterRequest request) {
        try {
            String apiUrl = StringUtils.hasText(request.getApiUrl())
                    ? request.getApiUrl()
                    : DEFAULT_OPENROUTER_API_URL;

            String modelsUrl = apiUrl + "/models";

            // 创建HTTP请求头（无需认证）
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送HTTP GET请求（匿名访问）
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    modelsUrl,
                    HttpMethod.GET,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 解析JSON响应
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<Map<String, Object>>() {
                        });

                // OpenRouter API 返回的结构是 {"data": [...]}
                Object dataObj = responseMap.get("data");
                if (dataObj instanceof List) {
                    return (List<Map<String, Object>>) dataObj;
                }

                log.warn("Unexpected response structure from OpenRouter models API");
                return List.of();
            } else {
                log.warn("Failed to get models from OpenRouter API. Status: {}", response.getStatusCode());
                return List.of();
            }

        } catch (Exception e) {
            log.error("Failed to get OpenRouter models: {}", e.getMessage(), e);
            // 作为备用方案，返回一些常用模型
            return List.of();
        }
    }

    /**
     * 获取结构化的模型列表
     * 返回 OpenrouterModel 对象列表，便于强类型使用
     */
    public List<OpenrouterModel> getModelsStructured(OpenrouterRequest request) {
        try {
            List<Map<String, Object>> rawModels = getModels(request);
            return rawModels.stream()
                    .map(OpenrouterModel::fromMap)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get OpenRouter structured models", e);
            return List.of();
        }

    }
}
