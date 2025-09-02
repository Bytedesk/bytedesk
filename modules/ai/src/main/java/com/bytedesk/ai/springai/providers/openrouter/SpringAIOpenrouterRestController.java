/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-02 22:17:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 22:48:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.openrouter;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OpenRouter REST API 控制器
 * 提供 OpenRouter 相关的管理和配置接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/openrouter")
@RequiredArgsConstructor
public class SpringAIOpenrouterRestController {

    private final SpringAIOpenrouterRestService openrouterService;

    /**
     * 获取结构化模型列表
     * GET http://127.0.0.1:9003/api/v1/openrouter/models/structured
     * 注意：此接口可以匿名访问，无需 API Key，返回强类型的 OpenrouterModel 对象
     */
    @GetMapping("/models")
    public ResponseEntity<JsonResult<List<OpenrouterModel>>> getModelsStructured(OpenrouterRequest request) {
        log.info("Getting OpenRouter structured models list (anonymous access)");
        
        try {
            List<OpenrouterModel> models = openrouterService.getModelsStructured(request);
            return ResponseEntity.ok(JsonResult.success(models));
        } catch (Exception e) {
            log.error("Failed to get OpenRouter structured models", e);
            return ResponseEntity.ok(new JsonResult<List<OpenrouterModel>>().setCode(500).setMessage(e.getMessage()).setData(List.of()));
        }
    }




}
