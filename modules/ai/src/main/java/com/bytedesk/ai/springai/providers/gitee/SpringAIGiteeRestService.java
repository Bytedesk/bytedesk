/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 23:15:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 23:15:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.gitee;

import java.net.URI;
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
 * Gitee AI REST 服务
 * 提供 Gitee AI API 的管理功能
 */
@Slf4j
@Service
public class SpringAIGiteeRestService {

    private static final String DEFAULT_GITEE_API_URL = "https://ai.gitee.com/v1";
    private static final String ALLOWED_GITEE_HOST = "ai.gitee.com";

    /**
     * 获取可用模型列表
     * 直接调用 Gitee AI API 获取真实的模型列表
     * 注意：此接口可以匿名访问，无需 API Key
     * 
     * https://ai.gitee.com/v1/models
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getModels(GiteeRequest request) {
        try {
            String apiUrl = resolveSafeApiUrl(request);

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

                // Gitee AI API 返回的结构是 {"object": "list", "data": [...]}
                Object dataObj = responseMap.get("data");
                if (dataObj instanceof List) {
                    return (List<Map<String, Object>>) dataObj;
                }

                log.warn("Unexpected response structure from Gitee AI models API");
                return List.of();
            } else {
                log.warn("Failed to get models from Gitee AI API. Status: {}", response.getStatusCode());
                return List.of();
            }

        } catch (Exception e) {
            log.error("Failed to get Gitee AI models: {}", e.getMessage(), e);
            // 作为备用方案，返回空列表
            return List.of();
        }
    }

    private String resolveSafeApiUrl(GiteeRequest request) {
        if (!StringUtils.hasText(request.getApiUrl())) {
            return DEFAULT_GITEE_API_URL;
        }

        try {
            URI uri = URI.create(request.getApiUrl().trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (!"https".equalsIgnoreCase(scheme)) {
                log.warn("Rejected Gitee apiUrl due to non-https scheme: {}", request.getApiUrl());
                return DEFAULT_GITEE_API_URL;
            }

            if (host == null || !ALLOWED_GITEE_HOST.equalsIgnoreCase(host)) {
                log.warn("Rejected Gitee apiUrl due to disallowed host: {}", request.getApiUrl());
                return DEFAULT_GITEE_API_URL;
            }

            return request.getApiUrl().trim().replaceAll("/+$", "");
        } catch (Exception ex) {
            log.warn("Rejected Gitee apiUrl due to parse error: {}", request.getApiUrl());
            return DEFAULT_GITEE_API_URL;
        }
    }

    /**
     * 获取结构化的模型列表
     * 返回 GiteeModel 对象列表，便于强类型使用
     */
    public List<GiteeModel> getModelsStructured(GiteeRequest request) {
        try {
            List<Map<String, Object>> rawModels = getModels(request);
            return rawModels.stream()
                    .map(GiteeModel::fromMap)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get Gitee AI structured models", e);
            return List.of();
        }
    }
}
