/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:35:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "表单管理", description = "表单管理相关接口")
@RestController
@RequestMapping("/api/v1/form")
@AllArgsConstructor
public class FormRestController extends BaseRestController<FormRequest> {

    private final FormRestService formService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "查询组织下的表单", description = "根据组织ID查询表单列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(FormRequest request) {
        
        Page<FormResponse> form = formService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @Operation(summary = "查询用户下的表单", description = "根据用户ID查询表单列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(FormRequest request) {
        
        Page<FormResponse> form = formService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @Operation(summary = "创建表单", description = "创建新的表单")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> create(FormRequest request) {
        
        FormResponse ticket_process = formService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticket_process));
    }

    @Operation(summary = "更新表单", description = "更新表单信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> update(FormRequest request) {
        
        FormResponse ticket_process = formService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticket_process));
    }

    @Operation(summary = "删除表单", description = "删除指定的表单")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(FormRequest request) {
        
        formService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出表单", description = "导出表单数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(FormRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "查询指定表单", description = "根据UID查询表单详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(FormRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}