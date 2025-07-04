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

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/template")
@AllArgsConstructor
public class TemplateRestController extends BaseRestController<TemplateRequest> {

    private final TemplateRestService templateRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "模板", action = "组织查询", description = "query template by org")
    @Override
    public ResponseEntity<?> queryByOrg(TemplateRequest request) {
        
        Page<TemplateResponse> templates = templateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @ActionAnnotation(title = "模板", action = "用户查询", description = "query template by user")
    @Override
    public ResponseEntity<?> queryByUser(TemplateRequest request) {
        
        Page<TemplateResponse> templates = templateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(templates));
    }

    @ActionAnnotation(title = "模板", action = "查询详情", description = "query template by uid")
    @Override
    public ResponseEntity<?> queryByUid(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "新建", description = "create template")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "更新", description = "update template")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TemplateRequest request) {
        
        TemplateResponse template = templateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(template));
    }

    @ActionAnnotation(title = "模板", action = "删除", description = "delete template")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TemplateRequest request) {
        
        templateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "模板", action = "导出", description = "export template")
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