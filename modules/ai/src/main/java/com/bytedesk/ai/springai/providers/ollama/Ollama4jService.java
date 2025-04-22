package com.bytedesk.ai.springai.providers.ollama;

import java.util.List;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.ps.ModelsProcessResponse;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.LibraryModelDetail;
import io.github.ollama4j.models.response.LibraryModelTag;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import lombok.extern.slf4j.Slf4j;

// https://ollama4j.github.io/ollama4j/apis-model-management/list-library-models
@Slf4j
@Service
public class Ollama4jService {

    @Autowired
    @Qualifier("ollama4jApi")
    private OllamaAPI ollama4jApi;

    @Autowired
    @Qualifier("bytedeskOllamaApi")
    private OllamaApi bytedeskOllamaApi;

    @Value("${spring.ai.ollama.embedding.options.model:bge-m3:latest}")
    private String ollamaEmbeddingOptionsModel;

    // 初始化方法，检查并拉取必要的嵌入模型
    // @PostConstruct
    // public void init() {
    //     ensureEmbeddingModelExists(ollamaEmbeddingOptionsModel);
    // }

    /**
     * 检查并确保嵌入模型存在，如果不存在则尝试拉取
     * 通过配置文件设置拉取策略
     * spring.ai.ollama.init.pull-model-strategy=when_missing
     * 
     * @param modelName 模型名称
     */
    // private void ensureEmbeddingModelExists(String modelName) {
    //     try {
    //         log.info("检查嵌入模型是否存在: {}", modelName);
    //         // 使用showModel方法替代不存在的getModelInfo方法
    //         bytedeskOllamaApi.showModel(new OllamaApi.ShowModelRequest(modelName));
    //         log.info("嵌入模型已存在: {}", modelName);
    //     } catch (HttpClientErrorException e) {
    //         if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
    //             log.warn("嵌入模型不存在: {}，尝试拉取模型...", modelName);
    //             try {
    //                 // 模型不存在，尝试拉取
    //                 bytedeskOllamaApi.pullModel(new OllamaApi.PullModelRequest(modelName));
    //                 log.info("成功拉取嵌入模型: {}", modelName);
    //             } catch (Exception pullEx) {
    //                 log.error("拉取嵌入模型失败: {}, 错误: {}", modelName, pullEx.getMessage(), pullEx);
    //             }
    //         } else {
    //             log.error("检查嵌入模型时发生错误: {}, 状态码: {}", modelName, e.getStatusCode(), e);
    //         }
    //     } catch (Exception e) {
    //         log.error("检查嵌入模型时发生未知错误: {}, 错误: {}", modelName, e.getMessage(), e);
    //     }
    // }

    /**
     * 检查模型是否存在
     * 
     * @param modelName 模型名称
     * @return 如果模型存在返回true，否则返回false
     */
    public boolean isModelExists(String modelName) {
        try {
            bytedeskOllamaApi.showModel(new OllamaApi.ShowModelRequest(modelName));
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("检查模型是否存在时发生错误: {}, 状态码: {}", modelName, e.getStatusCode(), e);
            throw e;
        } catch (Exception e) {
            log.error("检查模型是否存在时发生未知错误: {}, 错误: {}", modelName, e.getMessage(), e);
            throw new RuntimeException("检查模型是否存在时出错", e);
        }
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
    public boolean isOllama4jReachable() {
        try {
            return ollama4jApi.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j is not running.");
    }

    // 本地模型列表
    // https://ollama4j.github.io/ollama4j/apis-model-management/list-models
    // 拉取本地的模型
    public List<Model> getLocalModels() {
        try {
            return ollama4jApi.listModels();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get local models error.");
    }

    // 远程模型列表
    // https://ollama4j.github.io/ollama4j/apis-model-management/list-library-models
    // 拉取所有的模型
    public List<LibraryModel> getModels() {
        try {
            return ollama4jApi.listModelsFromLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get models error.");
    }

    // a list of running models and details about each model currently loaded into
    // memory.
    // 获取正在运行的模型
    public ModelsProcessResponse getPs() {
        try {
            return ollama4jApi.ps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get ps error.");
    }

    // This API Fetches the tags associated with a specific model from Ollama
    // library.
    public LibraryModelDetail getLibraryModelDetails(LibraryModel model) {
        try {
            return ollama4jApi.getLibraryModelDetails(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get model details error.");
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/get-model-details
     * 
     * @{OllamaModelType}
     * @param OllamaModelType
     * @return
     */
    public ModelDetail getModelDetails(String ollamaModelType) {
        try {
            return ollama4jApi.getModelDetails(ollamaModelType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get model details error.");
    }

    public LibraryModelTag getModelTag(String model, String tag) {
        try {
            return ollama4jApi.findModelTagFromLibrary(model, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get model tag error.");
    }

    // 拉取远程模型
    public void pullModel(LibraryModelTag libraryModelTag) {
        try {
            ollama4jApi.pullModel(libraryModelTag);
            return; // 成功时返回
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ollama4j pull model error: " + e.getMessage());
        }
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/pull-model
     * ollamaAPI.pullModel(OllamaModelType.LLAMA2);
     * 
     * @{OllamaModelType}
     */
    public void pullModel(String ollamaModelType) {
        try {
            // OllamaModelType.LLAMA2
            ollama4jApi.pullModel(ollamaModelType);
            return; // 成功时返回
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ollama4j pull model error: " + e.getMessage());
        }
    }

    // https://ollama4j.github.io/ollama4j/apis-model-management/delete-model
    public void deleteModel(String model) {
        try {
            ollama4jApi.deleteModel(model, true);
            return; // 成功时返回
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ollama4j delete model error: " + e.getMessage());
        }
    }

}
