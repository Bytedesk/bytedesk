package com.bytedesk.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
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
 * ChatModel信息查询服务
 * 提供查看所有ChatModel和Primary ChatModel的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatModelInfoService {

    private final ApplicationContext applicationContext;
    
    @Value("${spring.ai.model.chat:none}")
    private String primaryChatProvider;

    // ZhiPuAI ChatModel
    @Autowired(required = false)
    @Qualifier("bytedeskZhipuaiChatModel")
    private ChatModel zhipuaiChatModel;

    // Ollama ChatModel
    @Autowired(required = false)
    @Qualifier("bytedeskOllamaChatModel")
    private ChatModel ollamaChatModel;

    // Dashscope ChatModel
    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeChatModel")
    private ChatModel dashscopeChatModel;

    // Deepseek ChatModel
    @Autowired(required = false)
    @Qualifier("deepseekChatModel")
    private ChatModel deepseekChatModel;

    // Baidu ChatModel
    @Autowired(required = false)
    @Qualifier("baiduChatModel")
    private ChatModel baiduChatModel;

    // Tencent ChatModel
    @Autowired(required = false)
    @Qualifier("tencentChatModel")
    private ChatModel tencentChatModel;

    // Volcengine ChatModel
    @Autowired(required = false)
    @Qualifier("volcengineChatModel")
    private ChatModel volcengineChatModel;

    // OpenAI ChatModel
    @Autowired(required = false)
    @Qualifier("openaiChatModel")
    private ChatModel openaiChatModel;

    // OpenRouter ChatModel
    @Autowired(required = false)
    @Qualifier("openrouterChatModel")
    private ChatModel openrouterChatModel;

    // SiliconFlow ChatModel
    @Autowired(required = false)
    @Qualifier("siliconFlowChatModel")
    private ChatModel siliconflowChatModel;

    // Gitee ChatModel
    @Autowired(required = false)
    @Qualifier("giteeChatModel")
    private ChatModel giteeChatModel;

    /**
     * 测试指定的ChatModel
     */
    public Map<String, Object> testChatModel(String provider) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ChatModel model = getChatModelByProvider(provider);
            if (model == null) {
                result.put("status", "Not Available");
                result.put("error", "ChatModel for provider '" + provider + "' is not available");
                return result;
            }

            log.info("Testing {} chat model", provider);
            String testMessage = "Hello, this is a test message. Please respond with 'OK' if you can see this.";
            Prompt prompt = new Prompt(new UserMessage(testMessage));
            
            var response = model.call(prompt);
            String responseText = response.getResult().getOutput().getText();
            
            result.put("status", "Success");
            result.put("provider", provider);
            result.put("testMessage", testMessage);
            result.put("response", responseText);
            result.put("responseLength", responseText.length());
            result.put("modelClass", model.getClass().getSimpleName());
            
        } catch (Exception e) {
            log.error("Error testing {} chat model: {}", provider, e.getMessage(), e);
            result.put("status", "Error");
            result.put("provider", provider);
            result.put("error", e.getMessage());
        }
        
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试所有可用的ChatModel
     */
    public Map<String, Object> testAllChatModels() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> testResults = new HashMap<>();
        
        String[] providers = {"zhipuai", "ollama", "dashscope", "deepseek", "baidu", "tencent", "volcengine", "openai", "openrouter", "siliconflow", "gitee"};
        
        for (String provider : providers) {
            testResults.put(provider, testChatModel(provider));
        }
        
        result.put("testResults", testResults);
        result.put("totalProviders", providers.length);
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 获取所有ChatModel信息
     */
    public Map<String, Object> getAllChatModelsInfo() {
        log.info("Getting all ChatModel information");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> chatModels = new ArrayList<>();
        
        try {
            // 获取当前配置的Primary ChatModel
            ChatModel primaryModel = getPrimaryChatModel();
            
            // 检查并添加各个ChatModel
            addChatModelInfoIfAvailable(chatModels, "zhipuai", zhipuaiChatModel, "zhipuai".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "ollama", ollamaChatModel, "ollama".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "dashscope", dashscopeChatModel, "dashscope".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "deepseek", deepseekChatModel, "deepseek".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "baidu", baiduChatModel, "baidu".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "tencent", tencentChatModel, "tencent".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "volcengine", volcengineChatModel, "volcengine".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "openai", openaiChatModel, "openai".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "openrouter", openrouterChatModel, "openrouter".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "siliconflow", siliconflowChatModel, "siliconflow".equals(primaryChatProvider));
            addChatModelInfoIfAvailable(chatModels, "gitee", giteeChatModel, "gitee".equals(primaryChatProvider));
            
            // 添加Primary ChatModel信息
            if (primaryModel != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryChatProvider);
                primaryInfo.put("className", primaryModel.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryModel.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 测试Primary ChatModel
                try {
                    String testMessage = "Hello";
                    Prompt prompt = new Prompt(new UserMessage(testMessage));
                    var response = primaryModel.call(prompt);
                    String responseText = response.getResult().getOutput().getText();
                    primaryInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary chat model failed to respond: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryModel", primaryInfo);
            } else {
                log.warn("No primary chat model found for provider: {}", primaryChatProvider);
            }
            
            result.put("chatModels", chatModels);
            result.put("totalCount", chatModels.size());
            result.put("primaryProvider", primaryChatProvider);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting chat models info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取Primary ChatModel信息
     */
    public Map<String, Object> getPrimaryChatModelInfo() {
        log.info("Getting Primary ChatModel information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            ChatModel primaryModel = getPrimaryChatModel();
            
            if (primaryModel != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryChatProvider);
                primaryInfo.put("className", primaryModel.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryModel.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 测试Primary ChatModel
                try {
                    String testMessage = "Hello";
                    Prompt prompt = new Prompt(new UserMessage(testMessage));
                    var response = primaryModel.call(prompt);
                    String responseText = response.getResult().getOutput().getText();
                    primaryInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary chat model failed to respond: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryModel", primaryInfo);
            } else {
                result.put("error", "No primary chat model configured");
                result.put("status", "Not Configured");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting primary chat model info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取RAG使用的ChatModel信息
     */
    public Map<String, Object> getRagChatModelInfo() {
        log.info("Getting RAG ChatModel information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取RAG使用的ChatModel
            ChatModel ragModel = getPrimaryChatModel();
            
            if (ragModel != null) {
                Map<String, Object> modelInfo = new HashMap<>();
                modelInfo.put("provider", primaryChatProvider);
                modelInfo.put("className", ragModel.getClass().getSimpleName());
                modelInfo.put("fullClassName", ragModel.getClass().getName());
                modelInfo.put("usedBy", "RAG");
                
                // 测试ChatModel
                try {
                    String testMessage = "Hello";
                    Prompt prompt = new Prompt(new UserMessage(testMessage));
                    var response = ragModel.call(prompt);
                    String responseText = response.getResult().getOutput().getText();
                    modelInfo.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
                    modelInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("RAG chat model failed to respond: {}", e.getMessage());
                    modelInfo.put("status", "Error");
                    modelInfo.put("error", e.getMessage());
                }
                
                result.put("ragModel", modelInfo);
            } else {
                result.put("error", "No chat model available for RAG");
                result.put("status", "Not Available");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting RAG chat model info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 添加ChatModel信息（如果可用）
     */
    private void addChatModelInfoIfAvailable(List<Map<String, Object>> chatModels, String provider, ChatModel model, boolean isPrimary) {
        if (model != null) {
            try {
                Map<String, Object> info = getChatModelInfo(provider, model, isPrimary);
                chatModels.add(info);
            } catch (Exception e) {
                log.warn("ChatModel for provider {} is available but failed to get info: {}", provider, e.getMessage());
                // 添加一个错误状态的 ChatModel 信息
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("provider", provider);
                errorInfo.put("className", model.getClass().getSimpleName());
                errorInfo.put("fullClassName", model.getClass().getName());
                errorInfo.put("isPrimary", isPrimary);
                errorInfo.put("enabled", true);
                errorInfo.put("description", getProviderDescription(provider));
                errorInfo.put("modelType", "Cloud Chat Model");
                errorInfo.put("status", "Error");
                errorInfo.put("error", e.getMessage());
                chatModels.add(errorInfo);
            }
        } else {
            log.debug("ChatModel for provider {} is not available", provider);
        }
    }

    /**
     * 获取单个ChatModel的详细信息
     */
    private Map<String, Object> getChatModelInfo(String provider, ChatModel model, boolean isPrimary) {
        Map<String, Object> info = new HashMap<>();
        info.put("provider", provider);
        info.put("className", model.getClass().getSimpleName());
        info.put("fullClassName", model.getClass().getName());
        info.put("isPrimary", isPrimary);
        info.put("enabled", true);
        info.put("description", getProviderDescription(provider));
        info.put("modelType", getProviderModelType(provider));
        
        // 测试ChatModel
        try {
            log.debug("Testing chat model for provider: {}", provider);
            String testMessage = "Hello";
            Prompt prompt = new Prompt(new UserMessage(testMessage));
            var response = model.call(prompt);
            String responseText = response.getResult().getOutput().getText();
            info.put("testResponse", responseText.substring(0, Math.min(100, responseText.length())) + "...");
            info.put("status", "Active");
            log.debug("Successfully tested chat model for provider: {}", provider);
        } catch (Exception e) {
            log.warn("Failed to test chat model for provider {}: {}", provider, e.getMessage());
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
                return "智谱AI云端对话模型";
            case "ollama":
                return "本地Ollama对话模型";
            case "dashscope":
                return "阿里云Dashscope对话模型";
            case "deepseek":
                return "DeepSeek云端对话模型";
            case "baidu":
                return "百度文心一言对话模型";
            case "tencent":
                return "腾讯混元对话模型";
            case "volcengine":
                return "火山引擎对话模型";
            case "openai":
                return "OpenAI对话模型";
            case "openrouter":
                return "OpenRouter对话模型";
            case "siliconflow":
                return "SiliconFlow对话模型";
            case "gitee":
                return "Gitee对话模型";
            default:
                return "未知对话模型";
        }
    }

    /**
     * 获取提供商模型类型
     */
    private String getProviderModelType(String provider) {
        switch (provider) {
            case "ollama":
                return "Local Chat Model";
            default:
                return "Cloud Chat Model";
        }
    }

    /**
     * 根据提供商获取ChatModel
     */
    private ChatModel getChatModelByProvider(String provider) {
        switch (provider) {
            case "zhipuai":
                return zhipuaiChatModel;
            case "ollama":
                return ollamaChatModel;
            case "dashscope":
                return dashscopeChatModel;
            case "deepseek":
                return deepseekChatModel;
            case "baidu":
                return baiduChatModel;
            case "tencent":
                return tencentChatModel;
            case "volcengine":
                return volcengineChatModel;
            case "openai":
                return openaiChatModel;
            case "openrouter":
                return openrouterChatModel;
            case "siliconflow":
                return siliconflowChatModel;
            case "gitee":
                return giteeChatModel;
            default:
                return null;
        }
    }

    /**
     * 获取Primary ChatModel
     */
    private ChatModel getPrimaryChatModel() {
        try {
            return applicationContext.getBean(ChatModel.class);
        } catch (Exception e) {
            log.warn("Could not get primary chat model: {}", e.getMessage());
            return null;
        }
    }
} 