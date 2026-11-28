/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 14:24:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 14:17:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.ai.service.EmbeddingModelInfoService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
/**
 * EmbeddingModel信息查询控制器
 * 提供查看所有EmbeddingModel和Primary EmbeddingModel的接口
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/embedding-models")
@RequiredArgsConstructor
public class EmbeddingModelInfoController {

    private final EmbeddingModelInfoService embeddingModelInfoService;
    private final BytedeskProperties bytedeskProperties;

    /**
     * 获取所有EmbeddingModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/embedding-models/info
     */
    @Operation(summary = "Get All Embedding Models Info", description = "Retrieve configuration info of all embedding models")
    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_MODEL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query embedding model")
    @GetMapping("/info")
    public ResponseEntity<JsonResult<?>> getAllEmbeddingModelsInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting all EmbeddingModel information");
        Map<String, Object> result = embeddingModelInfoService.getAllEmbeddingModelsInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 获取Primary EmbeddingModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/embedding-models/primary
     */
    @Operation(summary = "Get Primary Embedding Model Info", description = "Retrieve configuration info of the primary embedding model")
    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_MODEL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query embedding model")
    @GetMapping("/primary")
    public ResponseEntity<JsonResult<?>> getPrimaryEmbeddingModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting Primary EmbeddingModel information");
        Map<String, Object> result = embeddingModelInfoService.getPrimaryEmbeddingModelInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 获取VectorStore使用的EmbeddingModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/embedding-models/vectorstore
     */
    @Operation(summary = "Get Vector Store Embedding Model Info", description = "Retrieve configuration info of the vector store embedding model")
    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_MODEL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query embedding model")
    @GetMapping("/vectorstore")
    public ResponseEntity<JsonResult<?>> getVectorStoreEmbeddingModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting VectorStore EmbeddingModel information");
        Map<String, Object> result = embeddingModelInfoService.getVectorStoreEmbeddingModelInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * 测试 DashScope embedding 模型
     * GET http://127.0.0.1:9003/spring/ai/api/v1/embedding-models/test-dashscope-models
     */
    @Operation(summary = "Test DashScope Models", description = "Test connectivity of DashScope embedding models")
    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_MODEL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query embedding model")
    @GetMapping("/test-dashscope-models")
    public ResponseEntity<JsonResult<?>> testDashScopeModels() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Testing DashScope embedding models");
        
        Map<String, Object> result = embeddingModelInfoService.testEmbeddingModels();
        return ResponseEntity.ok(JsonResult.success(result));
    }
} 