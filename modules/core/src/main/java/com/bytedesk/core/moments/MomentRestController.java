/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:39:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.moment;

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
import io.swagger.v3.oas.annotations.moments.Moment;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/moment")
@AllArgsConstructor
@Moment(name = "Moment Management", description = "Moment management APIs for organizing and categorizing content with moments")
public class MomentRestController extends BaseRestController<MomentRequest> {

    private final MomentRestService momentRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query moment by org")
    @Operation(summary = "Query Moments by Organization", description = "Retrieve moments for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(MomentRequest request) {
        
        Page<MomentResponse> moments = momentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(moments));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query moment by user")
    @Operation(summary = "Query Moments by User", description = "Retrieve moments for the current user")
    @Override
    public ResponseEntity<?> queryByUser(MomentRequest request) {
        
        Page<MomentResponse> moments = momentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(moments));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query moment by uid")
    @Operation(summary = "Query Moment by UID", description = "Retrieve a specific moment by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(MomentRequest request) {
        
        MomentResponse moment = momentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create moment")
    @Operation(summary = "Create Moment", description = "Create a new moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(MomentRequest request) {
        
        MomentResponse moment = momentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update moment")
    @Operation(summary = "Update Moment", description = "Update an existing moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(MomentRequest request) {
        
        MomentResponse moment = momentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete moment")
    @Operation(summary = "Delete Moment", description = "Delete a moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(MomentRequest request) {
        
        momentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export moment")
    @Operation(summary = "Export Moments", description = "Export moments to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MomentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            momentRestService,
            MomentExcel.class,
            "标签",
            "moment"
        );
    }

    
    
}