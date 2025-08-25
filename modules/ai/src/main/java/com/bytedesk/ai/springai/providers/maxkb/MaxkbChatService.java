/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 07:21:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-23 07:22:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.maxkb;

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
import java.util.List;
import java.util.Map;

/**
 * MaxKB Chat Service - 基于 MaxKB OpenAI 兼容 API 的对话服务
 * https://maxkb.cn/docs/v1/dev_manual/APIKey_chat/#1-openai-api
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.maxkb.enabled", havingValue = "true", matchIfMissing = false)
public class MaxkbChatService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bytedesk.maxkb.api-url:https://maxkb.fit2cloud.com}")
    private String apiUrl;

    @Value("${bytedesk.maxkb.api-key}")
    private String apiKey;

    @Value("${bytedesk.maxkb.default-model:gpt-3.5-turbo}")
    private String defaultModel;

    @Value("${bytedesk.maxkb.default-stream:false}")
    private Boolean defaultStream;

    /**
     * 创建聊天完成 - OpenAI 兼容 API
     * 
     * @param applicationId 应用ID
     * @param model 模型名称
     * @param messages 消息列表
     * @param stream 是否流式响应
     * @return API响应
     */
    public String createChatCompletion(String applicationId, String model, List<Map<String, Object>> messages, Boolean stream) {
        try {
            String url = apiUrl + "/api/application/" + applicationId + "/chat/completions";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : defaultModel);
            requestBody.put("messages", messages);
            if (stream != null) {
                requestBody.put("stream", stream);
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending chat completion to MaxKB API: {}", url);
            log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("MaxKB API response status: {}", response.getStatusCode());
            log.debug("MaxKB API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error creating chat completion with MaxKB API", e);
            throw new RuntimeException("Failed to create chat completion with MaxKB API", e);
        }
    }

    /**
     * 发送简单对话消息
     * 
     * @param applicationId 应用ID
     * @param content 消息内容
     * @param stream 是否流式响应
     * @return API响应
     */
    public String sendMessage(String applicationId, String content, Boolean stream) {
        try {
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", content);
            
            List<Map<String, Object>> messages = List.of(message);
            
            return createChatCompletion(applicationId, defaultModel, messages, stream);

        } catch (Exception e) {
            log.error("Error sending message to MaxKB API", e);
            throw new RuntimeException("Failed to send message to MaxKB API", e);
        }
    }

    /**
     * 发送带系统角色的对话消息
     * 
     * @param applicationId 应用ID
     * @param systemRole 系统角色描述
     * @param content 用户消息内容
     * @param stream 是否流式响应
     * @return API响应
     */
    public String sendMessageWithSystemRole(String applicationId, String systemRole, String content, Boolean stream) {
        try {
            // 构建消息列表
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", systemRole);
            systemMessage.put("content", "MaxKB 是什么？");
            
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", content);
            
            List<Map<String, Object>> messages = List.of(systemMessage, userMessage);
            
            return createChatCompletion(applicationId, defaultModel, messages, stream);

        } catch (Exception e) {
            log.error("Error sending message with system role to MaxKB API", e);
            throw new RuntimeException("Failed to send message with system role to MaxKB API", e);
        }
    }

    /**
     * 解析响应中的消息内容（非流式）
     * 
     * @param response API响应
     * @return 消息内容
     */
    public String extractMessageContent(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    return content != null ? content.asText() : "";
                }
            }
            return "";
        } catch (Exception e) {
            log.error("Error extracting message content from response", e);
            return "";
        }
    }

    /**
     * 解析响应中的完成原因
     * 
     * @param response API响应
     * @return 完成原因
     */
    public String extractFinishReason(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode finishReason = firstChoice.get("finish_reason");
                return finishReason != null ? finishReason.asText() : "";
            }
            return "";
        } catch (Exception e) {
            log.error("Error extracting finish reason from response", e);
            return "";
        }
    }

    /**
     * 解析响应中的使用情况
     * 
     * @param response API响应
     * @return 使用情况
     */
    public Map<String, Object> extractUsage(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode usage = jsonNode.get("usage");
            if (usage != null) {
                Map<String, Object> usageMap = new HashMap<>();
                if (usage.has("prompt_tokens")) {
                    usageMap.put("prompt_tokens", usage.get("prompt_tokens").asInt());
                }
                if (usage.has("completion_tokens")) {
                    usageMap.put("completion_tokens", usage.get("completion_tokens").asInt());
                }
                if (usage.has("total_tokens")) {
                    usageMap.put("total_tokens", usage.get("total_tokens").asInt());
                }
                return usageMap;
            }
            return new HashMap<>();
        } catch (Exception e) {
            log.error("Error extracting usage from response", e);
            return new HashMap<>();
        }
    }

    /**
     * 检查响应是否出错
     * 
     * @param response API响应
     * @return 是否出错
     */
    public boolean isError(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.has("error") || (jsonNode.has("code") && jsonNode.has("message"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取错误信息
     * 
     * @param response API响应
     * @return 错误信息
     */
    public String getErrorMessage(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // 检查标准的错误格式
            if (jsonNode.has("error")) {
                JsonNode error = jsonNode.get("error");
                if (error.has("message")) {
                    return error.get("message").asText();
                }
                return error.asText();
            }
            
            // 检查自定义错误格式
            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }
            
            return "Unknown error";
        } catch (Exception e) {
            return "Failed to parse error message";
        }
    }

    /**
     * 获取错误代码
     * 
     * @param response API响应
     * @return 错误代码
     */
    public int getErrorCode(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // 检查标准的错误格式
            if (jsonNode.has("error")) {
                JsonNode error = jsonNode.get("error");
                if (error.has("code")) {
                    return error.get("code").asInt();
                }
            }
            
            // 检查自定义错误格式
            if (jsonNode.has("code")) {
                return jsonNode.get("code").asInt();
            }
            
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 解析模型信息
     * 
     * @param response API响应
     * @return 模型名称
     */
    public String extractModel(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode model = jsonNode.get("model");
            return model != null ? model.asText() : "";
        } catch (Exception e) {
            log.error("Error extracting model from response", e);
            return "";
        }
    }

    /**
     * 解析响应ID
     * 
     * @param response API响应
     * @return 响应ID
     */
    public String extractId(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode id = jsonNode.get("id");
            return id != null ? id.asText() : "";
        } catch (Exception e) {
            log.error("Error extracting id from response", e);
            return "";
        }
    }

    /**
     * 解析对象类型
     * 
     * @param response API响应
     * @return 对象类型
     */
    public String extractObject(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode object = jsonNode.get("object");
            return object != null ? object.asText() : "";
        } catch (Exception e) {
            log.error("Error extracting object from response", e);
            return "";
        }
    }

    /**
     * 解析创建时间戳
     * 
     * @param response API响应
     * @return 创建时间戳
     */
    public Long extractCreated(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode created = jsonNode.get("created");
            return created != null ? created.asLong() : null;
        } catch (Exception e) {
            log.error("Error extracting created timestamp from response", e);
            return null;
        }
    }
}
