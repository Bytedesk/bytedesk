/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-17 14:24:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 14:30:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.rag;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.config.properties.BytedeskProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * EmbeddingModel信息查询控制器
 * 提供查看所有EmbeddingModel和Primary EmbeddingModel的接口
 */
@Slf4j
@RestController
@RequestMapping("/spring/ai/api/v1/embedding-models")
@RequiredArgsConstructor
@ConditionalOnBean(EmbeddingModel.class)
public class EmbeddingModelInfoController {

    private final EmbeddingModelInfoService embeddingModelInfoService;
    private final BytedeskProperties bytedeskProperties;

    /**
     * 获取所有EmbeddingModel信息
     * GET http://127.0.0.1:9003/spring/ai/api/v1/embedding-models/info
     */
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
     * GET /api/v1/embedding-models/primary
     */
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
     * GET /api/v1/embedding-models/vectorstore
     */
    @GetMapping("/vectorstore")
    public ResponseEntity<JsonResult<?>> getVectorStoreEmbeddingModelInfo() {
        if (!bytedeskProperties.getDebug()) {
            return ResponseEntity.ok(JsonResult.error("Service is not available"));
        }
        log.info("Getting VectorStore EmbeddingModel information");
        Map<String, Object> result = embeddingModelInfoService.getVectorStoreEmbeddingModelInfo();
        return ResponseEntity.ok(JsonResult.success(result));
    }
} 