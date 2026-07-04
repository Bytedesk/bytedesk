/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:25:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Workgroup Management", description = "Workgroup management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
@Description("Workgroup Management Controller - Workgroup and team management APIs")
public class WorkgroupRestController extends BaseRestController<WorkgroupRequest, WorkgroupRestService> {

    private final WorkgroupRestService workgroupRestService;

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query workgroup by org")
    @Operation(summary = "Query Workgroups by Organization", description = "Retrieve workgroup list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(WorkgroupRequest request) {

        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_QUERY_USER, description = "query workgroup by user")
    @Operation(summary = "Query Workgroups by User", description = "Retrieve workgroup list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRequest request) {
        
        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query workgroup by uid")
    @Operation(summary = "Query Workgroup by UID", description = "Retrieve workgroup details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ_OR_TICKET_READ)
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupRequest request) {
        
        WorkgroupResponse workgroup = workgroupRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_CREATE, description = "create workgroup")
    @Operation(summary = "Create Workgroup", description = "Create a new workgroup")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_UPDATE, description = "update workgroup")
    @Operation(summary = "Update Workgroup", description = "Update workgroup information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_PREFIX + "action.update.avatar", description = "update workgroup avatar")
    @Operation(summary = "Update Workgroup Avatar", description = "Update the workgroup avatar")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_UPDATE_STATUS, description = "update workgroup status")
    @Operation(summary = "Update Workgroup Status", description = "Update the workgroup status")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_DELETE, description = "delete workgroup")
    @Operation(summary = "Delete Workgroup", description = "Delete the specified workgroup")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest request) {

        workgroupRestService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @ActionAnnotation(title = I18Consts.I18N_PREFIX + "workgroup.management", action = I18Consts.I18N_ACTION_EXPORT, description = "export workgroup")
    @Operation(summary = "Export Workgroups", description = "Export workgroup data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(WorkgroupRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workgroupRestService,
            WorkgroupExcel.class,
            "工作组",
            "workgroup"
        );
    }

}
