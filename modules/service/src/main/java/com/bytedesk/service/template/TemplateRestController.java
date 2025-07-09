/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 00:02:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.template;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/template")
@AllArgsConstructor
@Tag(name = "模板管理", description = "模板管理相关接口")
public class TemplateRestController extends BaseRestController<TemplateRequest> {

    private final TemplateRestService templateRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "模板", action = "组织查询", description = "query template by org")
    @Operation(summary = "根据组织查询模板", description = "查询组织的模板列表")
    @Override
    public ResponseEntity<?> queryByOrg(TemplateRequest request) {
        
        Page<TemplateResponse> templates = templateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @ActionAnnotation(title = "模板", action = "用户查询", description = "query template by user")
    @Operation(summary = "根据用户查询模板", description = "查询用户的模板列表")
    @Override
    public ResponseEntity<?> queryByUser(TemplateRequest request) {
        
        Page<TemplateResponse> templates = templateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @ActionAnnotation(title = "模板", action = "查询详情", description = "query template by uid")
    @Operation(summary = "根据UID查询模板", description = "通过UID查询具体的模板")
    @Override
    public ResponseEntity<?> queryByUid(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "新建", description = "create template")
    @Operation(summary = "创建模板", description = "创建新的模板")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "更新", description = "update template")
    @Operation(summary = "更新模板", description = "更新现有的模板")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "删除", description = "delete template")
    @Operation(summary = "删除模板", description = "删除指定的模板")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TemplateRequest request) {
        
        templateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "模板", action = "导出", description = "export template")
    @Operation(summary = "导出模板", description = "导出模板数据")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(TemplateRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            templateRestService,
            TemplateExcel.class,
            "模板",
            "template"
        );
    }

    
    
}