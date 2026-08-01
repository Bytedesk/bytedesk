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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "Form Management", description = "Form management APIs")
@RestController
@RequestMapping("/api/v1/form")
@AllArgsConstructor
@Description("Form Management Controller - Form creation and management APIs for customer service")
public class FormRestController extends BaseRestController<FormRequest, FormRestService> {

    private final FormRestService formRestService;

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg form")
    @Operation(summary = "Query Forms by Organization", description = "Retrieve form list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(FormRequest request) {
        
        Page<FormResponse> form = formRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser form")
    @Operation(summary = "Query Forms by User", description = "Retrieve form list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(FormRequest request) {
        
        Page<FormResponse> form = formRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_READ)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid form")
    @Operation(summary = "Query Form by UID", description = "Retrieve form details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(FormRequest request) {
        
        FormResponse form = formRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_CREATE, description = "create form")
    @Operation(summary = "Create Form", description = "Create a new form")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody FormRequest request) {
        
        FormResponse form = formRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_UPDATE, description = "update form")
    @Operation(summary = "Update Form", description = "Update form information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FormResponse.class)))
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody FormRequest request) {
        
        FormResponse form = formRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @PreAuthorize(FormPermissions.HAS_FORM_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_DELETE, description = "delete form")
    @Operation(summary = "Delete Form", description = "Delete the specified form")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody FormRequest request) {
        
        formRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(FormPermissions.HAS_FORM_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_FORM, action = I18Consts.I18N_ACTION_EXPORT, description = "export form")
    @Operation(summary = "Export Forms", description = "Export form data")
    @ApiResponse(responseCode = "200", description = "Export successful")
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