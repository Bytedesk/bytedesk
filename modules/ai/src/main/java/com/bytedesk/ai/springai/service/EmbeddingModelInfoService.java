package com.bytedesk.ai.springai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmbeddingModel信息查询服务
 * 提供查看所有EmbeddingModel和Primary EmbeddingModel的服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingModelInfoService {

    private final ApplicationContext applicationContext;
    
    @Value("${spring.ai.model.embedding:none}")
    private String primaryEmbeddingProvider;

    @Autowired(required = false)
    private ZhiPuAiEmbeddingModel zhipuaiEmbeddingModel;

    @Autowired(required = false)
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    @Autowired(required = false)
    private DashScopeEmbeddingModel dashscopeEmbeddingModel;

    /**
     * 测试不同的 embedding 模型
     */
    public Map<String, Object> testEmbeddingModels() {
        Map<String, Object> result = new HashMap<>();
        
        if (dashscopeEmbeddingModel == null) {
            result.put("error", "DashScopeEmbeddingModel is not available");
            return result;
        }
        
        String[] testModels = {"text-embedding-v1", "text-embedding-v2", "text-embedding-v3"};
        Map<String, Object> modelResults = new HashMap<>();
        
        for (String model : testModels) {
            try {
                log.info("Testing DashScope embedding model: {}", model);
                // 这里需要动态设置模型，但 DashScopeEmbeddingModel 可能不支持动态切换
         
                    Map<String, Object> modelResult = new HashMap<>();
                    modelResult.put("status", "Not Configured");
                    modelResult.put("note", "This model is not currently configured");
                    modelResults.put(model, modelResult);

            } catch (Exception e) {
                Map<String, Object> modelResult = new HashMap<>();
                modelResult.put("status", "Error");
                modelResult.put("error", e.getMessage());
                modelResults.put(model, modelResult);
            }
        }
        
        result.put("modelResults", modelResults);
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 获取所有EmbeddingModel信息
     */
    public Map<String, Object> getAllEmbeddingModelsInfo() {
        log.info("Getting all EmbeddingModel information");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> embeddingModels = new ArrayList<>();
        
        try {
            // 获取当前配置的Primary EmbeddingModel
            EmbeddingModel primaryModel = getPrimaryEmbeddingModel();
            
            // 检查并添加Zhipuai EmbeddingModel
            if (zhipuaiEmbeddingModel != null) {
                Map<String, Object> zhipuaiInfo = getEmbeddingModelInfo("zhipuai", zhipuaiEmbeddingModel, 
                    "zhipuai".equals(primaryEmbeddingProvider));
                embeddingModels.add(zhipuaiInfo);
            } else {
                log.debug("Zhipuai EmbeddingModel is not available");
            }
            
            // 检查并添加Ollama EmbeddingModel
            if (ollamaEmbeddingModel != null) {
                Map<String, Object> ollamaInfo = getEmbeddingModelInfo("ollama", ollamaEmbeddingModel, 
                    "ollama".equals(primaryEmbeddingProvider));
                embeddingModels.add(ollamaInfo);
            } else {
                log.debug("Ollama EmbeddingModel is not available");
            }
            
            // 检查并添加Dashscope EmbeddingModel
            if (dashscopeEmbeddingModel != null) {
                try {
                    Map<String, Object> dashscopeInfo = getEmbeddingModelInfo("dashscope", dashscopeEmbeddingModel, 
                        "dashscope".equals(primaryEmbeddingProvider));
                    embeddingModels.add(dashscopeInfo);
                } catch (Exception e) {
                    log.warn("Dashscope EmbeddingModel is available but failed to get info: {}", e.getMessage());
                    // 添加一个错误状态的 Dashscope 信息
                    Map<String, Object> dashscopeErrorInfo = new HashMap<>();
                    dashscopeErrorInfo.put("provider", "dashscope");
                    dashscopeErrorInfo.put("className", dashscopeEmbeddingModel.getClass().getSimpleName());
                    dashscopeErrorInfo.put("fullClassName", dashscopeEmbeddingModel.getClass().getName());
                    dashscopeErrorInfo.put("isPrimary", "dashscope".equals(primaryEmbeddingProvider));
                    dashscopeErrorInfo.put("enabled", true);
                    dashscopeErrorInfo.put("description", "阿里云Dashscope Embedding模型");
                    dashscopeErrorInfo.put("modelType", "Cloud Embedding Model");
                    dashscopeErrorInfo.put("status", "Error");
                    dashscopeErrorInfo.put("error", e.getMessage());
                    embeddingModels.add(dashscopeErrorInfo);
                }
            } else {
                log.debug("Dashscope EmbeddingModel is not available");
            }
            
            // 添加Primary EmbeddingModel信息
            if (primaryModel != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryEmbeddingProvider);
                primaryInfo.put("className", primaryModel.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryModel.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 获取维度信息
                try {
                    var testEmbedding = primaryModel.embed("test");
                    primaryInfo.put("dimensions", testEmbedding.length);
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary embedding model failed to embed test text: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryModel", primaryInfo);
            } else {
                log.warn("No primary embedding model found for provider: {}", primaryEmbeddingProvider);
            }
            
            result.put("embeddingModels", embeddingModels);
            result.put("totalCount", embeddingModels.size());
            result.put("primaryProvider", primaryEmbeddingProvider);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting embedding models info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取Primary EmbeddingModel信息
     */
    public Map<String, Object> getPrimaryEmbeddingModelInfo() {
        log.info("Getting Primary EmbeddingModel information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            EmbeddingModel primaryModel = getPrimaryEmbeddingModel();
            
            if (primaryModel != null) {
                Map<String, Object> primaryInfo = new HashMap<>();
                primaryInfo.put("provider", primaryEmbeddingProvider);
                primaryInfo.put("className", primaryModel.getClass().getSimpleName());
                primaryInfo.put("fullClassName", primaryModel.getClass().getName());
                primaryInfo.put("isPrimary", true);
                
                // 获取维度信息
                try {
                    var testEmbedding = primaryModel.embed("test");
                    primaryInfo.put("dimensions", testEmbedding.length);
                    primaryInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("Primary embedding model failed to embed test text: {}", e.getMessage());
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryModel", primaryInfo);
            } else {
                result.put("error", "No primary embedding model configured");
                result.put("status", "Not Configured");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting primary embedding model info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取VectorStore使用的EmbeddingModel信息
     */
    public Map<String, Object> getVectorStoreEmbeddingModelInfo() {
        log.info("Getting VectorStore EmbeddingModel information");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取VectorStore使用的EmbeddingModel
            EmbeddingModel vectorStoreModel = getPrimaryEmbeddingModel();
            
            if (vectorStoreModel != null) {
                Map<String, Object> modelInfo = new HashMap<>();
                modelInfo.put("provider", primaryEmbeddingProvider);
                modelInfo.put("className", vectorStoreModel.getClass().getSimpleName());
                modelInfo.put("fullClassName", vectorStoreModel.getClass().getName());
                modelInfo.put("usedBy", "VectorStore");
                
                // 获取维度信息
                try {
                    var testEmbedding = vectorStoreModel.embed("test");
                    modelInfo.put("dimensions", testEmbedding.length);
                    modelInfo.put("status", "Active");
                } catch (Exception e) {
                    log.warn("VectorStore embedding model failed to embed test text: {}", e.getMessage());
                    modelInfo.put("status", "Error");
                    modelInfo.put("error", e.getMessage());
                }
                
                result.put("vectorStoreModel", modelInfo);
            } else {
                result.put("error", "No embedding model available for VectorStore");
                result.put("status", "Not Available");
            }
            
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting vector store embedding model info: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            result.put("status", "Error");
        }
        
        return result;
    }

    /**
     * 获取单个EmbeddingModel的详细信息
     */
    private Map<String, Object> getEmbeddingModelInfo(String provider, EmbeddingModel model, boolean isPrimary) {
        Map<String, Object> info = new HashMap<>();
        info.put("provider", provider);
        info.put("className", model.getClass().getSimpleName());
        info.put("fullClassName", model.getClass().getName());
        info.put("isPrimary", isPrimary);
        info.put("enabled", true);
        
        // 根据提供商设置描述信息
        switch (provider) {
            case "zhipuai":
                info.put("description", "智谱AI云端Embedding模型");
                info.put("modelType", "Cloud Embedding Model");
                break;
            case "ollama":
                info.put("description", "本地Ollama Embedding模型");
                info.put("modelType", "Local Embedding Model");
                break;
            case "dashscope":
                info.put("description", "阿里云Dashscope Embedding模型");
                info.put("modelType", "Cloud Embedding Model");
                break;
            default:
                info.put("description", "未知Embedding模型");
                info.put("modelType", "Unknown Embedding Model");
        }
        
        // 获取维度信息
        try {
            log.debug("Testing embedding for provider: {}", provider);
            var testEmbedding = model.embed("test");
            info.put("dimensions", testEmbedding.length);
            info.put("status", "Active");
            log.debug("Successfully tested embedding for provider: {}, dimensions: {}", provider, testEmbedding.length);
        } catch (Exception e) {
            log.warn("Failed to test embedding for provider {}: {}", provider, e.getMessage());
            info.put("status", "Error");
            info.put("error", e.getMessage());
        }
        
        return info;
    }

    /**
     * 获取Primary EmbeddingModel
     */
    private EmbeddingModel getPrimaryEmbeddingModel() {
        try {
            return applicationContext.getBean(EmbeddingModel.class);
        } catch (Exception e) {
            log.warn("Could not get primary embedding model: {}", e.getMessage());
            return null;
        }
    }

} 