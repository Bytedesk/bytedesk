/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 17:03:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-30 09:31:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.base.LlmProviderConfigDefault;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "LLM提供商管理", description = "LLM提供商管理相关接口")
@RestController
@RequestMapping("/api/v1/provider")
@AllArgsConstructor
public class LlmProviderRestController extends BaseRestController<LlmProviderRequest> {

    private final LlmProviderRestService llmProviderRestService;

    @Operation(summary = "查询组织下的LLM提供商", description = "根据组织ID查询LLM提供商列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderResponse.class)))
    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(LlmProviderRequest request) {
        
        Page<LlmProviderResponse> page = llmProviderRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的LLM提供商", description = "根据用户ID查询LLM提供商列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(LlmProviderRequest request) {
        
        Page<LlmProviderResponse> page = llmProviderRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "创建LLM提供商", description = "创建新的LLM提供商")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderResponse.class)))
    @Override
    public ResponseEntity<?> create(LlmProviderRequest request) {
        
        LlmProviderResponse response = llmProviderRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "更新LLM提供商", description = "更新LLM提供商信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderResponse.class)))
    @Override
    public ResponseEntity<?> update(LlmProviderRequest request) {
        
        LlmProviderResponse response = llmProviderRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "删除LLM提供商", description = "删除指定的LLM提供商")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(LlmProviderRequest request) {
        
        llmProviderRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出LLM提供商", description = "导出LLM提供商数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(LlmProviderRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "查询指定LLM提供商", description = "根据UID查询LLM提供商详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(LlmProviderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Operation(summary = "获取LLM提供商默认配置", description = "获取LLM提供商的默认配置信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = LlmProviderConfigDefault.class)))
    @GetMapping("/config/default")
    public ResponseEntity<?> getLlmProviderConfigDefault() {
        LlmProviderConfigDefault response = llmProviderRestService.getLlmProviderConfigDefault();
        return ResponseEntity.ok(JsonResult.success(response));
    }
}
