/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:08:22
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "表单管理", description = "表单管理相关接口")
@RestController
@RequestMapping("/api/v1/form")
@AllArgsConstructor
@Description("Form Management Controller - Form creation and management APIs for customer service")
public class FormRestController extends BaseRestController<FormRequest, FormRestService> {

    private final FormRestService formRestService;

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = "表单管理", action = "查询组织表单", description = "queryByOrg form")
    @Operation(summary = "查询组织下的表单", description = "根据组织ID查询表单列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(FormRequest request) {
        
        Page<FormResponse> form = formRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = "表单管理", action = "查询用户表单", description = "queryByUser form")
    @Operation(summary = "查询用户下的表单", description = "根据用户ID查询表单列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(FormRequest request) {
        
        Page<FormResponse> form = formRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = "表单管理", action = "查询表单详情", description = "queryByUid form")
    @Operation(summary = "查询指定表单", description = "根据UID查询表单详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(FormRequest request) {
        
        FormResponse form = formRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_CREATE)
    @ActionAnnotation(title = "表单管理", action = "创建表单", description = "create form")
    @Operation(summary = "创建表单", description = "创建新的表单")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> create(FormRequest request) {
        
        FormResponse form = formRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_UPDATE)
    @ActionAnnotation(title = "表单管理", action = "更新表单", description = "update form")
    @Operation(summary = "更新表单", description = "更新表单信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @Override
    public ResponseEntity<?> update(FormRequest request) {
        
        FormResponse form = formRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_DELETE)
    @ActionAnnotation(title = "表单管理", action = "删除表单", description = "delete form")
    @Operation(summary = "删除表单", description = "删除指定的表单")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(FormRequest request) {
        
        formRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(FormPermissions.HAS_FORM_EXPORT)
    @ActionAnnotation(title = "表单管理", action = "导出表单", description = "export form")
    @Operation(summary = "导出表单", description = "导出表单数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(FormRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            formRestService,
            FormExcel.class,
            "表单",
            "form"
        );
    }

    
    
}