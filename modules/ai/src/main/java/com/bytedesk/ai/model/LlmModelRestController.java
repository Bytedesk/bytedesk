/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:20:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:26:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "LLM模型管理", description = "LLM模型管理相关接口")
@RestController
@RequestMapping("/api/v1/model")
@AllArgsConstructor
@Description("LLM Model Controller - Large Language Model management and configuration APIs")
public class LlmModelRestController extends BaseRestController<LlmModelRequest, LlmModelRestService> {

    private final LlmModelRestService llmModelRestService;

    @Operation(summary = "查询组织下的LLM模型", description = "根据组织ID查询LLM模型列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "查询用户下的LLM模型", description = "根据用户ID查询LLM模型列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "查询指定LLM模型", description = "根据UID查询LLM模型详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "创建LLM模型", description = "创建新的LLM模型")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> create(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "更新LLM模型", description = "更新LLM模型信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> update(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "删除LLM模型", description = "删除指定的LLM模型")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(LlmModelRequest request) {
        
        llmModelRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出LLM模型", description = "导出LLM模型数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(LlmModelRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
}
