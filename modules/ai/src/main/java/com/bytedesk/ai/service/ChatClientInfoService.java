package com.bytedesk.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatClient信息查询服务
 * 提供查看所有ChatClient和Primary ChatClient的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatClientInfoService {

    private final ApplicationContext applicationContext;
    
    @Value("${spring.ai.model.chat:none}")
    private String primaryChatProvider;

    // ZhiPuAI ChatClient
    @Autowired(required = false)
    @Qualifier("bytedeskZhipuaiChatClient")
    private ChatClient zhipuaiChatClient;

    // Ollama ChatClient
    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatClient")
    private ChatClient ollamaChatClient;

    // Dashscope ChatClient
    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatClient")
    private ChatClient dashscopeChatClient;

    // Deepseek ChatClient
    @Autowired(required = false)
    @Qualifier("deepseekChatClient")
    private ChatClient deepseekChatClient;

    // Baidu ChatClient
    @Autowired(required = false)
    @Qualifier("baiduChatClient")
    private ChatClient baiduChatClient;

    // Tencent ChatClient
    @Autowired(required = false)
    @Qualifier("tencentChatClient")
    private ChatClient tencentChatClient;

    // Volcengine ChatClient
    @Autowired(required = false)
    @Qualifier("volcengineChatClient")
    private ChatClient volcengineChatClient;

    // OpenAI ChatClient
    @Autowired(required = false)
    @Qualifier("openaiChatClient")
    private ChatClient openaiChatClient;

    // OpenRouter ChatClient
    @Autowired(required = false)
    @Qualifier("openrouterChatClient")
    private ChatClient openrouterChatClient;

    // SiliconFlow ChatClient
    @Autowired(required = false)
    @Qualifier("siliconFlowChatClient")
    private ChatClient siliconflowChatClient;

    // Gitee ChatClient
    @Autowired(required = false)
    @Qualifier("giteeChatClient")
    private ChatClient giteeChatClient;

    /**
     * 测试指定的ChatClient
     */
    public Map<String, Object> testChatClient(String provider) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ChatClient client = getChatClientByProvider(provider);
            if (client == null) {
                result.put("status", "Not Available");
                result.put("error", "ChatClient for provider '" + provider + "' is not available");
                return result;
            }

            log.info("Testing {} chat client", provider);
            String testMessage = "Hello, this is a test message. Please respond with 'OK' if you can see this.";
            
            var response = client.prompt()
                    .user(testMessage)
                    .call()
                    .chatResponse();
            
            String responseText = response.getResult().getOutput().getText();
            
            result.put("status", "Success");
            result.put("provider", provider);
            result.put("testMessage", testMessage);
            result.put("response", responseText);
            result.put("responseLength", responseText.length());
            result.put("clientClass", client.getClass().getSimpleName());
            
        } catch (Exception e) {
            log.error("Error testing {} chat client: {}", provider, e.getMessage(), e);
            result.put("status", "Error");
            result.put("provider", provider);
            result.put("error", e.getMessage());
        }
        
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试所有可用的ChatClient
     */
    public Map<String, Object> testAllChatClients() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> testResults = new HashMap<>();
        
        String[] providers = {"zhipuai", "ollama", "dashscope", "deepseek", "baidu", "tencent", "volcengine", "openai", "openrouter", "siliconflow", "gitee"};
        
        for (String provider : providers) {
            testResults.put(provider, testChatClient(provider));
        }
        
        result.put("testResults", testResults);
        result.put("totalProviders", providers.length);
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 获取所有ChatClient信息
     */
    public Map<String, Object> getAllChatClientsInfo() {
        log.info("Getting all ChatClient information");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> chatClients = new ArrayList<>();
        
        try {
            // 获取当前配置的Primary ChatClient
            ChatClient primaryClient = getPrimaryChatClient();
            
            // 检查并添加各个ChatClient
            addChatClientInfoIfAvailable(chatClients, "zhipuai", zhipuaiChatClient, "zhipuai".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "ollama", ollamaChatClient, "ollama".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "dashscope", dashscopeChatClient, "dashscope".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "deepseek", deepseekChatClient, "deepseek".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "baidu", baiduChatClient, "baidu".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "tencent", tencentChatClient, "tencent".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "volcengine", volcengineChatClient, "volcengine".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "openai", openaiChatClient, "openai".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "openrouter", openrouterChatClient, "openrouter".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "siliconflow", siliconflowChatClient, "siliconflow".equals(primaryChatProvider));
            addChatClientInfoIfAvailable(chatClients, "gitee", giteeChatClient, "gitee".equals(primaryChatProvider));
            
            // 添加Primary ChatClient信息
            if (primaryClient != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryChatProvider);
                primaryInfo.put("className", primaryClient.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryClient.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 测试Primary ChatClient
                try {
                    String testMessage = "Hello";
                    var response = primaryClient.prompt()
                            .user(testMessage)
                            .call()
                            .chatResponse();
                    String responseText = response.getResult().getOutput().getText();
                    primaryInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary chat client failed to respond: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryClient", primaryInfo);
            } else {
                log.warn("No primary chat client found for provider: {}", primaryChatProvider);
            }
            
            result.put("chatClients", chatClients);
            result.put("totalCount", chatClients.size());
            result.put("primaryProvider", primaryChatProvider);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting chat clients info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取Primary ChatClient信息
     */
    public Map<String, Object> getPrimaryChatClientInfo() {
        log.info("Getting Primary ChatClient information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            ChatClient primaryClient = getPrimaryChatClient();
            
            if (primaryClient != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryChatProvider);
                primaryInfo.put("className", primaryClient.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryClient.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 测试Primary ChatClient
                try {
                    String testMessage = "Hello";
                    var response = primaryClient.prompt()
                            .user(testMessage)
                            .call()
                            .chatResponse();
                    String responseText = response.getResult().getOutput().getText();
                    primaryInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary chat client failed to respond: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryClient", primaryInfo);
            } else {
                result.put("error", "No primary chat client configured");
                result.put("status", "Not Configured");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting primary chat client info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取RAG使用的ChatClient信息
     */
    public Map<String, Object> getRagChatClientInfo() {
        log.info("Getting RAG ChatClient information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取RAG使用的ChatClient
            ChatClient ragClient = getPrimaryChatClient();
            
            if (ragClient != null) {
                Map<String, Object> clientInfo = new HashMap<>();
                clientInfo.put("provider", primaryChatProvider);
                clientInfo.put("className", ragClient.getClass().getSimpleName());
                clientInfo.put("fullClassName", ragClient.getClass().getName());
                clientInfo.put("usedBy", "RAG");
                
                // 测试ChatClient
                try {
                    String testMessage = "Hello";
                    var response = ragClient.prompt()
                            .user(testMessage)
                            .call()
                            .chatResponse();
                    String responseText = response.getResult().getOutput().getText();
                    clientInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    clientInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("RAG chat client failed to respond: {}", e.getMessage());
                    clientInfo.put("status", "Error");
                    clientInfo.put("error", e.getMessage());
                }
                
                result.put("ragClient", clientInfo);
            } else {
                result.put("error", "No chat client available for RAG");
                result.put("status", "Not Available");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting RAG chat client info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 添加ChatClient信息（如果可用）
     */
    private void addChatClientInfoIfAvailable(List<Map<String, Object>> chatClients, String provider, ChatClient client, boolean isPrimary) {
        if (client != null) {
            try {
                Map<String, Object> info = getChatClientInfo(provider, client, isPrimary);
                chatClients.add(info);
            } catch (Exception e) {
                log.warn("ChatClient for provider {} is available but failed to get info: {}", provider, e.getMessage());
                // 添加一个错误状态的 ChatClient 信息
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("provider", provider);
                errorInfo.put("className", client.getClass().getSimpleName());
                errorInfo.put("fullClassName", client.getClass().getName());
                errorInfo.put("isPrimary", isPrimary);
                errorInfo.put("enabled", true);
                errorInfo.put("description", getProviderDescription(provider));
                errorInfo.put("clientType", "Cloud Chat Client");
                errorInfo.put("status", "Error");
                errorInfo.put("error", e.getMessage());
                chatClients.add(errorInfo);
            }
        } else {
            log.debug("ChatClient for provider {} is not available", provider);
        }
    }

    /**
     * 获取单个ChatClient的详细信息
     */
    private Map<String, Object> getChatClientInfo(String provider, ChatClient client, boolean isPrimary) {
        Map<String, Object> info = new HashMap<>();
        info.put("provider", provider);
        info.put("className", client.getClass().getSimpleName());
        info.put("fullClassName", client.getClass().getName());
        info.put("isPrimary", isPrimary);
        info.put("enabled", true);
        info.put("description", getProviderDescription(provider));
        info.put("clientType", getProviderClientType(provider));
        
        // 测试ChatClient
        try {
            log.debug("Testing chat client for provider: {}", provider);
            String testMessage = "Hello";
            var response = client.prompt()
                    .user(testMessage)
                    .call()
                    .chatResponse();
            String responseText = response.getResult().getOutput().getText();
            info.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
            info.put("status", "Active");
            log.debug("Successfully tested chat client for provider: {}", provider);
        } catch (Exception e) {
            log.warn("Failed to test chat client for provider {}: {}", provider, e.getMessage());
            info.put("status", "Error");
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    /**
     * 获取提供商描述
     */
    private String getProviderDescription(String provider) {
        switch (provider) {
            case "zhipuai":
                return "智谱AI云端对话客户端";
            case "ollama":
                return "本地Ollama对话客户端";
            case "dashscope":
                return "阿里云Dashscope对话客户端";
            case "deepseek":
                return "DeepSeek云端对话客户端";
            case "baidu":
                return "百度文心一言对话客户端";
            case "tencent":
                return "腾讯混元对话客户端";
            case "volcengine":
                return "火山引擎对话客户端";
            case "openai":
                return "OpenAI对话客户端";
            case "openrouter":
                return "OpenRouter对话客户端";
            case "siliconflow":
                return "SiliconFlow对话客户端";
            case "gitee":
                return "Gitee对话客户端";
            default:
                return "未知对话客户端";
        }
    }

    /**
     * 获取提供商客户端类型
     */
    private String getProviderClientType(String provider) {
        switch (provider) {
            case "ollama":
                return "Local Chat Client";
            default:
                return "Cloud Chat Client";
        }
    }

    /**
     * 根据提供商获取ChatClient
     */
    public ChatClient getChatClientByProvider(String provider) {
        switch (provider) {
            case "zhipuai":
                return zhipuaiChatClient;
            case "ollama":
                return ollamaChatClient;
            case "dashscope":
                return dashscopeChatClient;
            case "deepseek":
                return deepseekChatClient;
            case "baidu":
                return baiduChatClient;
            case "tencent":
                return tencentChatClient;
            case "volcengine":
                return volcengineChatClient;
            case "openai":
                return openaiChatClient;
            case "openrouter":
                return openrouterChatClient;
            case "siliconflow":
                return siliconflowChatClient;
            case "gitee":
                return giteeChatClient;
            default:
                return null;
        }
    }

    /**
     * 获取Primary ChatClient
     */
    public ChatClient getPrimaryChatClient() {
        try {
            return applicationContext.getBean(ChatClient.class);
        } catch (Exception e) {
            log.warn("Could not get primary chat client: {}", e.getMessage());
            return null;
        }
    }
} 