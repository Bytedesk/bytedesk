package com.bytedesk.ai.springai.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
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
    @Qualifier("bytedeskZhipuaiEmbeddingModel")
    private EmbeddingModel zhipuaiEmbeddingModel;

    @Autowired(required = false)
    @Qualifier("bytedeskOllamaEmbeddingModel")
    private EmbeddingModel ollamaEmbeddingModel;

    @Autowired(required = false)
    @Qualifier("bytedeskDashscopeEmbeddingModel")
    private EmbeddingModel dashscopeEmbeddingModel;

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
            }
            
            // 检查并添加Ollama EmbeddingModel
            if (ollamaEmbeddingModel != null) {
                Map<String, Object> ollamaInfo = getEmbeddingModelInfo("ollama", ollamaEmbeddingModel, 
                    "ollama".equals(primaryEmbeddingProvider));
                embeddingModels.add(ollamaInfo);
            }
            
            // 检查并添加Dashscope EmbeddingModel
            if (dashscopeEmbeddingModel != null) {
                Map<String, Object> dashscopeInfo = getEmbeddingModelInfo("dashscope", dashscopeEmbeddingModel, 
                    "dashscope".equals(primaryEmbeddingProvider));
                embeddingModels.add(dashscopeInfo);
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
                    primaryInfo.put("status", "Error");
                    primaryInfo.put("error", e.getMessage());
                }
                
                result.put("primaryModel", primaryInfo);
            }
            
            result.put("embeddingModels", embeddingModels);
            result.put("totalCount", embeddingModels.size());
            result.put("primaryProvider", primaryEmbeddingProvider);
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Error getting embedding models info: {}", e.getMessage());
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
            log.error("Error getting primary embedding model info: {}", e.getMessage());
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
            log.error("Error getting vector store embedding model info: {}", e.getMessage());
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
            var testEmbedding = model.embed("test");
            info.put("dimensions", testEmbedding.length);
            info.put("status", "Active");
        } catch (Exception e) {
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