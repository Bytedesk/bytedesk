/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 23:17:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

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
import org.springframework.context.annotation.Description;

@Tag(name = "表单结果管理", description = "表单结果管理相关接口")
@RestController
@RequestMapping("/api/v1/form/result")
@AllArgsConstructor
@Description("Form Result Controller - Form submission result collection and management APIs")
public class FormResultRestController extends BaseRestController<FormResultRequest> {

    private final FormResultRestService tagRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query tag by org")
    @Operation(summary = "Query Form Results by Organization", description = "Retrieve form results for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(FormResultRequest request) {
        
        Page<FormResultResponse> tags = tagRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query tag by user")
    @Operation(summary = "Query Form Results by User", description = "Retrieve form results for the current user")
    @Override
    public ResponseEntity<?> queryByUser(FormResultRequest request) {
        
        Page<FormResultResponse> tags = tagRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query tag by uid")
    @Operation(summary = "Query Form Result by UID", description = "Retrieve a specific form result by UID")
    @Override
    public ResponseEntity<?> queryByUid(FormResultRequest request) {
        
        FormResultResponse tag = tagRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create tag")
    @Operation(summary = "Create Form Result", description = "Create a new form result entry")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(FormResultRequest request) {
        
        FormResultResponse tag = tagRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update tag")
    @Operation(summary = "Update Form Result", description = "Update an existing form result entry")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(FormResultRequest request) {
        
        FormResultResponse tag = tagRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete tag")
    @Operation(summary = "Delete Form Result", description = "Delete a form result entry")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(FormResultRequest request) {
        
        tagRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export tag")
    @Operation(summary = "Export Form Results", description = "Export form results to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(FormResultRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagRestService,
            FormResultExcel.class,
            "标签",
            "tag"
        );
    }

    
    
}