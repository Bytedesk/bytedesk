/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:32:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.ivr;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/call/ivr")
@AllArgsConstructor
@Tag(name = "CallIvr Management", description = "CallIvr management APIs for organizing and categorizing content with tags")
@Description("CallIvr Management Controller - Content tagging and categorization APIs")
public class CallIvrRestController extends BaseRestController<CallIvrRequest, CallIvrRestService> {

    private final CallIvrRestService ivrRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "ivr", action = "组织查询", description = "query ivr by org")
    @Operation(summary = "Query CallIvrs by Organization", description = "Retrieve tags for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(CallIvrRequest request) {
        
        Page<CallIvrResponse> tags = ivrRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "ivr", action = "用户查询", description = "query ivr by user")
    @Operation(summary = "Query CallIvrs by User", description = "Retrieve tags for the current user")
    @Override
    public ResponseEntity<?> queryByUser(CallIvrRequest request) {
        
        Page<CallIvrResponse> tags = ivrRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "ivr", action = "查询详情", description = "query ivr by uid")
    @Operation(summary = "Query CallIvr by UID", description = "Retrieve a specific ivr by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(CallIvrRequest request) {
        
        CallIvrResponse ivr = ivrRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ivr));
    }

    @ActionAnnotation(title = "ivr", action = "新建", description = "create ivr")
    @Operation(summary = "Create CallIvr", description = "Create a new ivr")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(CallIvrRequest request) {
        
        CallIvrResponse ivr = ivrRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ivr));
    }

    @ActionAnnotation(title = "ivr", action = "更新", description = "update ivr")
    @Operation(summary = "Update CallIvr", description = "Update an existing ivr")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(CallIvrRequest request) {
        
        CallIvrResponse ivr = ivrRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ivr));
    }

    @ActionAnnotation(title = "ivr", action = "删除", description = "delete ivr")
    @Operation(summary = "Delete CallIvr", description = "Delete a ivr")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(CallIvrRequest request) {
        
        ivrRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "ivr", action = "导出", description = "export ivr")
    @Operation(summary = "Export CallIvrs", description = "Export tags to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(CallIvrRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ivrRestService,
            CallIvrExcel.class,
            "ivr",
            "ivr"
        );
    }

    
    
}