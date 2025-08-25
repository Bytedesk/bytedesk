package com.bytedesk.ai.springai.providers.ragflow;

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
 * RAGFlow Chat Service - 基于 RAGFlow API 的对话服务
 * https://ragflow.io/docs/dev/http_api_reference
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.ragflow.enabled", havingValue = "true", matchIfMissing = false)
public class RagflowChatService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bytedesk.ragflow.api-url:http://localhost:9380}")
    private String apiUrl;

    @Value("${bytedesk.ragflow.api-key}")
    private String apiKey;

    /**
     * 创建聊天完成 - OpenAI 兼容 API
     * 
     * @param chatId 聊天ID
     * @param model 模型名称
     * @param messages 消息列表
     * @param stream 是否流式响应
     * @return API响应
     */
    public String createChatCompletion(String chatId, String model, List<Map<String, Object>> messages, Boolean stream) {
        try {
            String url = apiUrl + "/api/v1/chats_openai/" + chatId + "/chat/completions";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : "model");
            requestBody.put("messages", messages);
            requestBody.put("stream", stream != null ? stream : false);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending chat completion to RAGFlow API: {}", url);
            log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("RAGFlow API response status: {}", response.getStatusCode());
            log.debug("RAGFlow API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error creating chat completion with RAGFlow API", e);
            throw new RuntimeException("Failed to create chat completion with RAGFlow API", e);
        }
    }

    /**
     * 创建代理完成 - OpenAI 兼容 API
     * 
     * @param agentId 代理ID
     * @param model 模型名称
     * @param messages 消息列表
     * @param stream 是否流式响应
     * @return API响应
     */
    public String createAgentCompletion(String agentId, String model, List<Map<String, Object>> messages, Boolean stream) {
        try {
            String url = apiUrl + "/api/v1/agents_openai/" + agentId + "/chat/completions";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null ? model : "model");
            requestBody.put("messages", messages);
            requestBody.put("stream", stream != null ? stream : false);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("Sending agent completion to RAGFlow API: {}", url);
            log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            log.info("RAGFlow API response status: {}", response.getStatusCode());
            log.debug("RAGFlow API response body: {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {
            log.error("Error creating agent completion with RAGFlow API", e);
            throw new RuntimeException("Failed to create agent completion with RAGFlow API", e);
        }
    }

    /**
     * 发送简单对话消息
     * 
     * @param chatId 聊天ID
     * @param content 消息内容
     * @param stream 是否流式响应
     * @return API响应
     */
    public String sendMessage(String chatId, String content, Boolean stream) {
        try {
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", content);
            
            List<Map<String, Object>> messages = List.of(message);
            
            return createChatCompletion(chatId, "model", messages, stream);

        } catch (Exception e) {
            log.error("Error sending message to RAGFlow API", e);
            throw new RuntimeException("Failed to send message to RAGFlow API", e);
        }
    }

    /**
     * 发送代理消息
     * 
     * @param agentId 代理ID
     * @param content 消息内容
     * @param stream 是否流式响应
     * @return API响应
     */
    public String sendAgentMessage(String agentId, String content, Boolean stream) {
        try {
            // 构建消息列表
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", content);
            
            List<Map<String, Object>> messages = List.of(message);
            
            return createAgentCompletion(agentId, "model", messages, stream);

        } catch (Exception e) {
            log.error("Error sending agent message to RAGFlow API", e);
            throw new RuntimeException("Failed to send agent message to RAGFlow API", e);
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
                usageMap.put("prompt_tokens", usage.get("prompt_tokens").asInt());
                usageMap.put("completion_tokens", usage.get("completion_tokens").asInt());
                usageMap.put("total_tokens", usage.get("total_tokens").asInt());
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
            return jsonNode.has("code") && jsonNode.has("message");
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
            JsonNode message = jsonNode.get("message");
            return message != null ? message.asText() : "Unknown error";
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
            JsonNode code = jsonNode.get("code");
            return code != null ? code.asInt() : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
