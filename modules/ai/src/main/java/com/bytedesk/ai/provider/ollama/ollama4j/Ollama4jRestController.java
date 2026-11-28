/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-11 13:19:23
 * @LastEditors: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.ollama.ollama4j;

import java.util.List;

// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ai.provider.ollama.OllamaRequest;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import io.github.ollama4j.models.ps.ModelProcessesResult;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.ModelDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/v1/ollama4j")
@RequiredArgsConstructor
public class Ollama4jRestController {

    private final Ollama4jService ollama4jService;

    // http://127.0.0.1:9003/api/v1/ollama4j/ping
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Ping Ollama Service", description = "Check whether the Ollama service is reachable")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_CHECK_SERVICE_REACHABLE, description = "check service reachable ollama")
    @GetMapping("/ping")
    public ResponseEntity<JsonResult<Boolean>> ping(OllamaRequest request) {

        boolean isReachable = ollama4jService.isOllama4jReachable(request);

        return ResponseEntity.ok(JsonResult.success(isReachable));
    }

    // 本地已经下载的模型列表
    // http://127.0.0.1:9003/api/v1/ollama4j/local-models
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Get Local Models", description = "Retrieve models downloaded locally")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/local-models")
    public ResponseEntity<JsonResult<List<Model>>> getLocalModels(OllamaRequest request) {

        List<Model> models = ollama4jService.getLocalModels(request);

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // 远程模型列表（ollama4j 1.1.7 已移除 Library API，此接口不再可用）
    // http://127.0.0.1:9003/api/v1/ollama4j/models
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Get Available Models", description = "Retrieve the list of available models")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/models")
    public ResponseEntity<JsonResult<List<Model>>> getModels(OllamaRequest request) {

        List<Model> models = ollama4jService.getModels(request);

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // 当前运行的模型
    // http://127.0.0.1:9003/api/v1/ollama4j/ps
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "List Running Models", description = "List models currently loaded in Ollama memory")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/ps")
    public ResponseEntity<JsonResult<ModelProcessesResult>> processModelsResponse(OllamaRequest request) {

        ModelProcessesResult models = ollama4jService.getPs(request);

        return ResponseEntity.ok(JsonResult.success(models));
    }

    // ollama4j 1.1.7 已移除 Library Model 详情 API
    // http://127.0.0.1:9003/api/v1/ollama4j/library/models/{model}/details
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Get Library Model Details", description = "Retrieve details of a model in the library")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/library/models/details")
    public ResponseEntity<JsonResult<?>> getLibraryModelDetails(OllamaRequest request) {
        
        return ResponseEntity.ok(JsonResult.success("Library model details API removed in ollama4j 1.1.7"));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/models/details
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Get Model Details", description = "Retrieve details of a specific model")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/models/details")
    public ResponseEntity<JsonResult<ModelDetail>> getModelDetails(OllamaRequest request) {

        ModelDetail modelDetail = ollama4jService.getModelDetails(request);

        return ResponseEntity.ok(JsonResult.success(modelDetail));
    }

    // ollama4j 1.1.7 已移除 Model Tag API
    // http://127.0.0.1:9003/api/v1/ollama4j/models/{model}/tags/{tag}
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_READ)
    @Operation(summary = "Get Model Tags", description = "Retrieve tags of a specific model")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ollama")
    @GetMapping("/models/tags")
    public ResponseEntity<JsonResult<?>> getModelTag(OllamaRequest request) {

        return ResponseEntity.ok(JsonResult.success("Model tag API removed in ollama4j 1.1.7"));
    }

    // http://127.0.0.1:9003/api/v1/ollama4j/models/pull
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_UPDATE)
    @Operation(summary = "Pull Model", description = "Download a model from the registry")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_UPDATE, description = "update ollama")
    @PostMapping("/models/pull")
    public ResponseEntity<?> pullModel(@RequestBody OllamaRequest request) {

        ollama4jService.pullModel(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // 删除模型    
    // http://127.0.0.1:9003/api/v1/ollama4j/models/delete
    @PreAuthorize(Ollama4jPermissions.HAS_OLLAMA4J_DELETE)
    @Operation(summary = "Delete Model", description = "Delete a local model")
    @ActionAnnotation(title = I18Consts.I18N_OLLAMA, action = I18Consts.I18N_ACTION_DELETE, description = "delete ollama")
    @PostMapping("/models/delete")
    public ResponseEntity<?> deleteModel(@RequestBody OllamaRequest request) {

        ollama4jService.deleteModel(request);

        return ResponseEntity.ok(JsonResult.success());
    }

}