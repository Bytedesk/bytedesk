/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 13:19:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:47:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.ollama;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.github.ollama4j.models.ps.ModelsProcessResponse;
import io.github.ollama4j.models.response.LibraryModel;
import io.github.ollama4j.models.response.LibraryModelDetail;
import io.github.ollama4j.models.response.LibraryModelTag;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/ollama4j")
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = false)
public class Ollama4jRestController {

    @Autowired
    private Ollama4jService ollama4jService;

    // http://127.0.0.1:9003/api/v1/ollama4j/ping
    @GetMapping("/ping")
    public ResponseEntity<JsonResult<Boolean>> ping() {

        boolean isReachable = ollama4jService.isOllama4jReachable();

        return ResponseEntity.ok(JsonResult.success(isReachable));
    }

    // 本地已经下载的模型列表
    // http://127.0.0.1:9003/api/v1/ollama4j/local-models
    @GetMapping("/local-models")
    public ResponseEntity<JsonResult<List<Model>>> getLocalModels() {

        List<Model> models = ollama4jService.getLocalModels();

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // 远程模型列表
    // http://127.0.0.1:9003/api/v1/ollama4j/models
    @GetMapping("/models")
    public ResponseEntity<JsonResult<List<LibraryModel>>> getModels() {

        List<LibraryModel> models = ollama4jService.getModels();

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // 当前运行的模型
    // http://127.0.0.1:9003/api/v1/ollama4j/ps
    @GetMapping("/ps")
    public ResponseEntity<JsonResult<ModelsProcessResponse>> processModels() {

        ModelsProcessResponse models = ollama4jService.getPs();

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/library/models/{model}/details
    @GetMapping("/library/models/{model}/details")
    public ResponseEntity<JsonResult<LibraryModelDetail>> getLibraryModelDetails(@PathVariable String model) {

        LibraryModel libraryModel = new LibraryModel();
        libraryModel.setName(model);
        LibraryModelDetail modelDetail = ollama4jService.getLibraryModelDetails(libraryModel);

        return ResponseEntity.ok(JsonResult.success(modelDetail));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/models/{model}/details
    @GetMapping("/models/{model}/details")
    public ResponseEntity<JsonResult<ModelDetail>> getModelDetails(@PathVariable String model) {

        ModelDetail modelDetail = ollama4jService.getModelDetails(model);

        return ResponseEntity.ok(JsonResult.success(modelDetail));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/models/{model}/tags/{tag}
    @GetMapping("/models/{model}/tags/{tag}")
    public ResponseEntity<JsonResult<LibraryModelTag>> getModelTag(@PathVariable String model, @PathVariable String tag) {

        LibraryModelTag modelTag = ollama4jService.getModelTag(model, tag);

        return ResponseEntity.ok(JsonResult.success(modelTag));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/models/pull
    @PostMapping("/models/pull")
    public ResponseEntity<?> pullModel(@RequestBody OllamaRequest request) {

        ollama4jService.pullModel(request.getModel());

        return ResponseEntity.ok(JsonResult.success());
    }

    // 删除模型    
    // http://127.0.0.1:9003/api/v1/ollama4j/models/delete
    @PostMapping("/models/delete")
    public ResponseEntity<?> deleteModel(@RequestBody OllamaRequest request) {

        ollama4jService.deleteModel(request.getModel());

        return ResponseEntity.ok(JsonResult.success());
    }

    // 检查嵌入模型是否已经存在
    // http://127.0.0.1:9003/api/v1/ollama4j/embedding-model/exists
    @GetMapping("/embedding-model/exists")
    public ResponseEntity<?> isEmbeddingModelExists(String model) {
        // String modelName = ollama4jService.getEmbeddingModelName();
        boolean exists = ollama4jService.isModelExists(model);
        log.info("Embedding model exists: {}, {}", model, exists);
        // 
        return ResponseEntity.ok(JsonResult.success(exists));
    }
}