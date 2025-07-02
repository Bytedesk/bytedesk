/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 09:40:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

@Tag(name = "工作流管理", description = "工作流管理相关接口")
@RestController
@RequestMapping("/api/v1/workflow")
@AllArgsConstructor
public class WorkflowRestController extends BaseRestController<WorkflowRequest> {

    private final WorkflowRestService workflowRestService;

    @Operation(summary = "查询组织下的工作流", description = "根据组织ID查询工作流列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "查询用户下的工作流", description = "根据用户ID查询工作流列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "查询指定工作流", description = "根据UID查询工作流详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "创建工作流", description = "创建新的工作流")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @ActionAnnotation(title = "工作流", action = "新建", description = "create workflow")
    @Override
    public ResponseEntity<?> create(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "更新工作流", description = "更新工作流信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @ActionAnnotation(title = "工作流", action = "更新", description = "update workflow")
    @Override
    public ResponseEntity<?> update(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "删除工作流", description = "删除指定的工作流")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "工作流", action = "删除", description = "delete workflow")
    @Override
    public ResponseEntity<?> delete(WorkflowRequest request) {
        
        workflowRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出工作流", description = "导出工作流数据")
    @ApiResponse(responseCode = "200", description = "导出成功")        
    @ActionAnnotation(title = "工作流", action = "导出", description = "export workflow")
    @Override
    public Object export(WorkflowRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    // 执行工作流
    @Operation(summary = "执行工作流", description = "执行指定的工作流")
    @ApiResponse(responseCode = "200", description = "执行成功")
    @ActionAnnotation(title = "工作流", action = "执行", description = "execute workflow")
    @PostMapping("/execute")
    public ResponseEntity<?> execute(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.execute(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

}