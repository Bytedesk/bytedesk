/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_edge;

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
@RequestMapping("/api/v1/workflow/edge")
@AllArgsConstructor
@Tag(name = "WorkflowEdge Management", description = "WorkflowEdge management APIs for organizing and categorizing content with workflow_edges")
@Description("WorkflowEdge Management Controller - Content workflow_edge and categorization APIs")
public class WorkflowEdgeRestController extends BaseRestController<WorkflowEdgeRequest, WorkflowEdgeRestService> {

    private final WorkflowEdgeRestService workflow_edgeRestService;

    @ActionAnnotation(title = "Workflow Edge", action = "组织查询", description = "query workflow_edge by org")
    @Operation(summary = "Query WorkflowEdges by Organization", description = "Retrieve workflow_edges for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(WorkflowEdgeRequest request) {
        
        Page<WorkflowEdgeResponse> workflow_edges = workflow_edgeRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow_edges));
    }

    @ActionAnnotation(title = "Workflow Edge", action = "用户查询", description = "query workflow_edge by user")
    @Operation(summary = "Query WorkflowEdges by User", description = "Retrieve workflow_edges for the current user")
    @Override
    public ResponseEntity<?> queryByUser(WorkflowEdgeRequest request) {
        
        Page<WorkflowEdgeResponse> workflow_edges = workflow_edgeRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow_edges));
    }

    @ActionAnnotation(title = "Workflow Edge", action = "查询详情", description = "query workflow_edge by uid")
    @Operation(summary = "Query WorkflowEdge by UID", description = "Retrieve a specific workflow_edge by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(WorkflowEdgeRequest request) {
        
        WorkflowEdgeResponse workflow_edge = workflow_edgeRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workflow_edge));
    }

    @ActionAnnotation(title = "Workflow Edge", action = "新建", description = "create workflow_edge")
    @Operation(summary = "Create WorkflowEdge", description = "Create a new workflow_edge")
    @Override
    // @PreAuthorize(WorkflowEdgePermissions.HAS_WORKFLOW_EDGE_CREATE)
    public ResponseEntity<?> create(WorkflowEdgeRequest request) {
        
        WorkflowEdgeResponse workflow_edge = workflow_edgeRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow_edge));
    }

    @ActionAnnotation(title = "Workflow Edge", action = "更新", description = "update workflow_edge")
    @Operation(summary = "Update WorkflowEdge", description = "Update an existing workflow_edge")
    @Override
    // @PreAuthorize(WorkflowEdgePermissions.HAS_WORKFLOW_EDGE_UPDATE)
    public ResponseEntity<?> update(WorkflowEdgeRequest request) {
        
        WorkflowEdgeResponse workflow_edge = workflow_edgeRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow_edge));
    }

    @ActionAnnotation(title = "Workflow Edge", action = "删除", description = "delete workflow_edge")
    @Operation(summary = "Delete WorkflowEdge", description = "Delete a workflow_edge")
    @Override
    // @PreAuthorize(WorkflowEdgePermissions.HAS_WORKFLOW_EDGE_DELETE)
    public ResponseEntity<?> delete(WorkflowEdgeRequest request) {
        
        workflow_edgeRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Workflow Edge", action = "导出", description = "export workflow_edge")
    @Operation(summary = "Export WorkflowEdges", description = "Export workflow_edges to Excel format")
    @Override
    // @PreAuthorize(WorkflowEdgePermissions.HAS_WORKFLOW_EDGE_EXPORT)
    @GetMapping("/export")
    public Object export(WorkflowEdgeRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflow_edgeRestService,
            WorkflowEdgeExcel.class,
            "Workflow Edge",
            "workflow_edge"
        );
    }

    
    
}