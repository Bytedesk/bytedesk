package com.bytedesk.ai.springai.ollama;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.ps.ModelsProcessResponse;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.LibraryModelDetail;
import io.github.ollama4j.models.response.LibraryModelTag;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
// import io.github.ollama4j.types.OllamaModelType;


// https://ollama4j.github.io/ollama4j/apis-model-management/list-library-models
@Service
public class Ollama4jService {

    @Autowired
    @Qualifier("ollama4jApi")
    private OllamaAPI ollama4jApi;

    // 检查Ollama4j是否可用
    public boolean isOllama4jReachable() {
        try {
            return ollama4jApi.ping();
        } catch (Exception e) {
            // e.printStackTrace();
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
            // e.printStackTrace();
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
            // e.printStackTrace();
        } 
        throw new RuntimeException("Ollama4j get models error.");
    }

    //  a list of running models and details about each model currently loaded into memory.
    // 获取正在运行的模型
    public ModelsProcessResponse getPs() {
        try {
            return ollama4jApi.ps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get ps error.");
    }

    // This API Fetches the tags associated with a specific model from Ollama library.
    public LibraryModelDetail getLibraryModelDetails(LibraryModel model) {
        try {
            return ollama4jApi.getLibraryModelDetails(model);
        } catch (Exception e) {
            // e.printStackTrace();
        } 
        throw new RuntimeException("Ollama4j get model details error.");
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/get-model-details
     * @{OllamaModelType}
     * @param OllamaModelType
     * @return
     */
    public ModelDetail getModelDetails(String ollamaModelType) {
        try {
            return ollama4jApi.getModelDetails(ollamaModelType);
        } catch (Exception e) {
            // e.printStackTrace();
        } 
        throw new RuntimeException("Ollama4j get model details error.");
    }

    public LibraryModelTag getModelTag(String model, String tag) {
        try {
            return ollama4jApi.findModelTagFromLibrary(model, tag);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j get model tag error.");
    }

    // 拉取远程模型
    public void pullModel(LibraryModelTag libraryModelTag) {
        try {
            ollama4jApi.pullModel(libraryModelTag);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j pull model error.");
    }

    /**
     * https://ollama4j.github.io/ollama4j/apis-model-management/pull-model
     * ollamaAPI.pullModel(OllamaModelType.LLAMA2);
     * @{OllamaModelType}
     */
    public void pullModel(String ollamaModelType) {
        try {
            // OllamaModelType.LLAMA2
            ollama4jApi.pullModel(ollamaModelType);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j pull model error.");
    }

    // https://ollama4j.github.io/ollama4j/apis-model-management/delete-model
    public void deleteModel(String model) {
        try {
            ollama4jApi.deleteModel(model, true);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        throw new RuntimeException("Ollama4j delete model error.");
    }
    
    
}
