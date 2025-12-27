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
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "工作组管理", description = "工作组管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/workgroup")
@Description("Workgroup Management Controller - Workgroup and team management APIs")
public class WorkgroupRestController extends BaseRestController<WorkgroupRequest, WorkgroupRestService> {

    private final WorkgroupRestService workgroupRestService;

    @ActionAnnotation(title = "工作组", action = "组织查询", description = "query workgroup by org")
    @Operation(summary = "查询组织下的工作组", description = "根据组织ID查询工作组列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ)
    @Override
    public ResponseEntity<?> queryByOrg(WorkgroupRequest request) {

        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @ActionAnnotation(title = "工作组", action = "用户查询", description = "query workgroup by user")
    @Operation(summary = "查询用户下的工作组", description = "根据用户ID查询工作组列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ)
    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRequest request) {
        
        Page<WorkgroupResponse> workgroups = workgroupRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workgroups));
    }

    @ActionAnnotation(title = "工作组", action = "查询详情", description = "query workgroup by uid")
    @Operation(summary = "查询指定工作组", description = "根据UID查询工作组详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_READ)
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupRequest request) {
        
        WorkgroupResponse workgroup = workgroupRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "工作组", action = "新建", description = "create workgroup")
    @Operation(summary = "创建工作组", description = "创建新的工作组")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "工作组", action = "更新", description = "update workgroup")
    @Operation(summary = "更新工作组", description = "更新工作组信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.update(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "工作组", action = "updateAvatar", description = "update workgroup avatar")
    @Operation(summary = "更新工作组头像", description = "更新工作组的头像")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @PostMapping("/update/avatar")
    public ResponseEntity<?> updateAvatar(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateAvatar(request);

        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "工作组", action = "updateStatus", description = "update workgroup status")
    @Operation(summary = "更新工作组状态", description = "更新工作组的状态")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkgroupResponse.class)))
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_UPDATE)
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody WorkgroupRequest request) {

        WorkgroupResponse workgroup = workgroupRestService.updateStatus(request);
        //
        return ResponseEntity.ok(JsonResult.success(workgroup));
    }

    @ActionAnnotation(title = "工作组", action = "删除", description = "delete workgroup")
    @Operation(summary = "删除工作组", description = "删除指定的工作组")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(WorkgroupPermissions.HAS_WORKGROUP_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody WorkgroupRequest request) {

        workgroupRestService.deleteByUid(request.getUid());
        //
        return ResponseEntity.ok(JsonResult.success(request));
    }

    @ActionAnnotation(title = "工作组", action = "导出", description = "export workgroup")
    @Operation(summary = "导出工作组", description = "导出工作组数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
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
