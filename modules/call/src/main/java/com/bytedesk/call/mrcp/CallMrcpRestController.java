/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 15:21:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.mrcp;

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
@RequestMapping("/api/v1/call/mrcp")
@AllArgsConstructor
@Tag(name = "CallMrcp Management", description = "CallMrcp management APIs for organizing and categorizing content with tags")
@Description("CallMrcp Management Controller - Content tagging and categorization APIs")
public class CallMrcpRestController extends BaseRestController<CallMrcpRequest> {

    private final CallMrcpRestService mrcpRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "mrcp", action = "组织查询", description = "query mrcp by org")
    @Operation(summary = "Query CallMrcps by Organization", description = "Retrieve tags for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(CallMrcpRequest request) {
        
        Page<CallMrcpResponse> tags = mrcpRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "mrcp", action = "用户查询", description = "query mrcp by user")
    @Operation(summary = "Query CallMrcps by User", description = "Retrieve tags for the current user")
    @Override
    public ResponseEntity<?> queryByUser(CallMrcpRequest request) {
        
        Page<CallMrcpResponse> tags = mrcpRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "mrcp", action = "查询详情", description = "query mrcp by uid")
    @Operation(summary = "Query CallMrcp by UID", description = "Retrieve a specific mrcp by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(CallMrcpRequest request) {
        
        CallMrcpResponse mrcp = mrcpRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(mrcp));
    }

    @ActionAnnotation(title = "mrcp", action = "新建", description = "create mrcp")
    @Operation(summary = "Create CallMrcp", description = "Create a new mrcp")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(CallMrcpRequest request) {
        
        CallMrcpResponse mrcp = mrcpRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(mrcp));
    }

    @ActionAnnotation(title = "mrcp", action = "更新", description = "update mrcp")
    @Operation(summary = "Update CallMrcp", description = "Update an existing mrcp")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(CallMrcpRequest request) {
        
        CallMrcpResponse mrcp = mrcpRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(mrcp));
    }

    @ActionAnnotation(title = "mrcp", action = "删除", description = "delete mrcp")
    @Operation(summary = "Delete CallMrcp", description = "Delete a mrcp")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(CallMrcpRequest request) {
        
        mrcpRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "mrcp", action = "导出", description = "export mrcp")
    @Operation(summary = "Export CallMrcps", description = "Export tags to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(CallMrcpRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            mrcpRestService,
            CallMrcpExcel.class,
            "mrcp",
            "mrcp"
        );
    }

    
    
}