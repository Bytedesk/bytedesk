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

@Tag(name = "LLM Model Management", description = "LLM model management APIs")
@RestController
@RequestMapping("/api/v1/model")
@AllArgsConstructor
@Description("LLM Model Controller - Large Language Model management and configuration APIs")
public class LlmModelRestController extends BaseRestController<LlmModelRequest, LlmModelRestService> {

    private final LlmModelRestService llmModelRestService;

    @Operation(summary = "Query LLM Models by Organization", description = "Retrieve LLM model list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Query LLM Models by User", description = "Retrieve LLM model list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(LlmModelRequest request) {
        
        Page<LlmModelResponse> result = llmModelRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Query LLM Model by UID", description = "Retrieve LLM model details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Create LLM Model", description = "Create a new LLM model")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> create(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Update LLM Model", description = "Update LLM model information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmModelResponse.class)))
    @Override
    public ResponseEntity<?> update(LlmModelRequest request) {
        
        LlmModelResponse result = llmModelRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Delete LLM Model", description = "Delete the specified LLM model")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @Override
    public ResponseEntity<?> delete(LlmModelRequest request) {
        
        llmModelRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export LLM Models", description = "Export LLM model data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @Override
    public Object export(LlmModelRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
}
