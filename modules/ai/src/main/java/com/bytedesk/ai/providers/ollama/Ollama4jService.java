package com.bytedesk.ai.providers.ollama;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.ps.ModelProcessesResult;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import lombok.extern.slf4j.Slf4j;

// https://ollama4j.github.io/ollama4j/apis-model-management/list-library-models
@Slf4j
@Service
public class Ollama4jService {

    @Value("${spring.ai.ollama.embedding.model:bge-m3:latest}")
    private String ollamaEmbeddingOptionsModel;

    @Value("${spring.ai.ollama.request-timeout-seconds:120}")
    private Integer ollamaRequestTimeoutSeconds;

    /**
     * 根据请求中的 apiUrl 创建 Ollama 实例
     * 
     * @param request 包含 apiUrl 的请求对象
     * @return Ollama 实例
     */
    public Ollama createOllamaAPI(OllamaRequest request) {
        String apiUrl = request.getBaseUrl();
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("apiUrl is required in OllamaRequest");
        }
        
        Ollama ollamaAPI = new Ollama(apiUrl);
        ollamaAPI.setRequestTimeoutSeconds(ollamaRequestTimeoutSeconds);
        return ollamaAPI;
    }

    /**
     * 获取当前配置的嵌入模型名称
     * 
     * @return 嵌入模型名称
     */
    public String getEmbeddingModelName() {
        return ollamaEmbeddingOptionsModel;
    }

    // 检查Ollama4j是否可用
    public Boolean isOllama4jReachable(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            return ollamaAPI.ping();
        } catch (Exception e) {
            log.warn("Ollama4j is not running. Error: {}", e.getMessage());
        }
        return false;
    }

    // 本地模型列表
    // https://ollama4j.github.io/ollama4j/apis-model-management/list-models
    // 拉取本地的模型
    public List<Model> getLocalModels(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            return ollamaAPI.listModels();
        } catch (Exception e) {
            log.warn("Ollama4j get local models error: {}", e.getMessage());
        }
        return List.of();
    }

    // 远程模型列表（ollama4j 1.1.7 已移除 Library API，返回本地模型列表作为替代）
    public List<Model> getModels(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            return ollamaAPI.listModels();
        } catch (Exception e) {
            log.warn("Ollama4j get models error: {}", e.getMessage());
        }
        return List.of();
    }

    // a list of running models and details about each model currently loaded into
    // memory.
    // 获取正在运行的模型
    public ModelProcessesResult getPs(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            return ollamaAPI.ps();
        } catch (Exception e) {
            log.warn("Ollama4j get ps error: {}", e.getMessage());
        }
        return null;
    }

    // ollama4j 1.1.7 已移除 Library Model 详情 API
    // This API Fetches the tags associated with a specific model from Ollama library.
    public String getLibraryModelDetails(OllamaRequest request) {
        log.warn("Library model details API removed in ollama4j 1.1.7");
        return "Library model details API removed in ollama4j 1.1.7";
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/get-model-details
     * 
     * @param request
     * @return
     */
    public ModelDetail getModelDetails(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            return ollamaAPI.getModelDetails(request.getModel());
        } catch (Exception e) {
            log.warn("Ollama4j get model details error: {}", e.getMessage());
        }
        return null;
    }

    // ollama4j 1.1.7 已移除 Model Tag API
    public String getModelTag(OllamaRequest request) {
        log.warn("Model tag API removed in ollama4j 1.1.7");
        return "Model tag API removed in ollama4j 1.1.7";
    }

    // 拉取远程模型
    // ollama4j 1.1.7 已移除 LibraryModelTag 参数，仅支持 modelName 字符串
    public void pullModel(OllamaRequest request, String modelTag) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            ollamaAPI.pullModel(modelTag);
            return; // 成功时返回
        } catch (Exception e) {
            log.warn("Ollama4j pull model error: {}", e.getMessage());
        }
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/pull-model
     * ollamaAPI.pullModel(OllamaModelType.LLAMA2);
     * 
     * @{OllamaModelType}
     */
    public void pullModel(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            // OllamaModelType.LLAMA2
            ollamaAPI.pullModel(request.getModel());
            return; // 成功时返回
        } catch (Exception e) {
            log.warn("Ollama4j pull model error: {}", e.getMessage());
        }
    }

    // https://ollama4j.github.io/ollama4j/apis-model-management/delete-model
    public void deleteModel(OllamaRequest request) {
        try {
            Ollama ollamaAPI = createOllamaAPI(request);
            ollamaAPI.deleteModel(request.getModel(), true);
            return; // 成功时返回
        } catch (Exception e) {
            log.warn("Ollama4j delete model error: {}", e.getMessage());
        }
    }

}
