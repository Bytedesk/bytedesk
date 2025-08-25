/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:12:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:17:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Dify Chat Service - 基于 Dify API 的对话服务
 * https://docs.dify.ai/zh-hans/guides/application-publishing/developing-with-apis
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.dify.enabled", havingValue = "true", matchIfMissing = false)
public class DifyChatService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bytedesk.dify.api-url:https://api.dify.ai}")
    private String apiUrl;

    @Value("${bytedesk.dify.api-key}")
    private String apiKey;

    /**
     * 发送对话消息 - chat-messages API
     * 
     * @param query 用户查询
     * @param conversationId 对话ID，为空时创建新对话
     * @param user 用户标识
     * @param inputs 输入变量
     * @param responseMode 响应模式：streaming 或 blocking
     * @return API响应
     */
    public String sendChatMessage(String query, String conversationId, String user, 
                                 Map<String, Object> inputs, String responseMode) {
        try {
            String url = apiUrl + "/v1/chat-messages";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", responseMode != null ? responseMode : "blocking");
            requestBody.put("user", user);
            
            if (conversationId != null && !conversationId.isEmpty()) {
                requestBody.put("conversation_id", conversationId);
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending chat message to Dify API: {}", url);
            log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error sending chat message to Dify API", e);
            throw new RuntimeException("Failed to send chat message to Dify API", e);
        }
    }

    /**
     * 发送文本补全消息 - completion-messages API
     * 
     * @param inputs 输入变量
     * @param user 用户标识
     * @param responseMode 响应模式：streaming 或 blocking
     * @return API响应
     */
    public String sendCompletionMessage(Map<String, Object> inputs, String user, String responseMode) {
        try {
            String url = apiUrl + "/v1/completion-messages";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", responseMode != null ? responseMode : "blocking");
            requestBody.put("user", user);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending completion message to Dify API: {}", url);
            log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error sending completion message to Dify API", e);
            throw new RuntimeException("Failed to send completion message to Dify API", e);
        }
    }

    /**
     * 获取对话历史
     * 
     * @param conversationId 对话ID
     * @param user 用户标识
     * @param firstId 第一条消息ID（用于分页）
     * @param limit 限制数量
     * @return API响应
     */
    public String getConversationMessages(String conversationId, String user, String firstId, Integer limit) {
        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl + "/v1/messages");
            urlBuilder.append("?conversation_id=").append(conversationId);
            urlBuilder.append("&user=").append(user);
            
            if (firstId != null && !firstId.isEmpty()) {
                urlBuilder.append("&first_id=").append(firstId);
            }
            if (limit != null) {
                urlBuilder.append("&limit=").append(limit);
            }

            String url = urlBuilder.toString();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("Getting conversation messages from Dify API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error getting conversation messages from Dify API", e);
            throw new RuntimeException("Failed to get conversation messages from Dify API", e);
        }
    }

    /**
     * 获取对话列表
     * 
     * @param user 用户标识
     * @param lastId 最后一个对话ID（用于分页）
     * @param limit 限制数量
     * @param pinned 是否只返回置顶对话
     * @return API响应
     */
    public String getConversations(String user, String lastId, Integer limit, Boolean pinned) {
        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl + "/v1/conversations");
            urlBuilder.append("?user=").append(user);
            
            if (lastId != null && !lastId.isEmpty()) {
                urlBuilder.append("&last_id=").append(lastId);
            }
            if (limit != null) {
                urlBuilder.append("&limit=").append(limit);
            }
            if (pinned != null) {
                urlBuilder.append("&pinned=").append(pinned);
            }

            String url = urlBuilder.toString();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("Getting conversations from Dify API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error getting conversations from Dify API", e);
            throw new RuntimeException("Failed to get conversations from Dify API", e);
        }
    }

    /**
     * 删除对话
     * 
     * @param conversationId 对话ID
     * @param user 用户标识
     * @return API响应
     */
    public String deleteConversation(String conversationId, String user) {
        try {
            String url = apiUrl + "/v1/conversations/" + conversationId;
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user", user);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Deleting conversation from Dify API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.DELETE, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error deleting conversation from Dify API", e);
            throw new RuntimeException("Failed to delete conversation from Dify API", e);
        }
    }

    /**
     * 重命名对话
     * 
     * @param conversationId 对话ID
     * @param name 新名称
     * @param user 用户标识
     * @return API响应
     */
    public String renameConversation(String conversationId, String name, String user) {
        try {
            String url = apiUrl + "/v1/conversations/" + conversationId + "/name";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("user", user);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Renaming conversation in Dify API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("Dify API response status: {}", response.getStatusCode());
            log.debug("Dify API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error renaming conversation in Dify API", e);
            throw new RuntimeException("Failed to rename conversation in Dify API", e);
        }
    }

    /**
     * 解析响应中的 conversation_id
     * 
     * @param response API响应
     * @return conversation_id
     */
    public String extractConversationId(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("conversation_id").asText();
        } catch (Exception e) {
            log.error("Error extracting conversation_id from response", e);
            return null;
        }
    }

    /**
     * 解析响应中的消息内容
     * 
     * @param response API响应
     * @return 消息内容
     */
    public String extractMessageContent(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("answer").asText();
        } catch (Exception e) {
            log.error("Error extracting message content from response", e);
            return null;
        }
    }
}