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
package com.bytedesk.ai.workflow_node;

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
@RequestMapping("/api/v1/workflow/node")
@AllArgsConstructor
@Tag(name = "WorkflowNode Management", description = "WorkflowNode management APIs for organizing and categorizing content with workflow_nodes")
@Description("WorkflowNode Management Controller - Content workflow_node and categorization APIs")
public class WorkflowNodeRestController extends BaseRestController<WorkflowNodeRequest, WorkflowNodeRestService> {

    private final WorkflowNodeRestService workflow_nodeRestService;

    @ActionAnnotation(title = "Workflow Node", action = "组织查询", description = "query workflow_node by org")
    @Operation(summary = "Query WorkflowNodes by Organization", description = "Retrieve workflow_nodes for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(WorkflowNodeRequest request) {
        
        Page<WorkflowNodeResponse> workflow_nodes = workflow_nodeRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow_nodes));
    }

    @ActionAnnotation(title = "Workflow Node", action = "用户查询", description = "query workflow_node by user")
    @Operation(summary = "Query WorkflowNodes by User", description = "Retrieve workflow_nodes for the current user")
    @Override
    public ResponseEntity<?> queryByUser(WorkflowNodeRequest request) {
        
        Page<WorkflowNodeResponse> workflow_nodes = workflow_nodeRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow_nodes));
    }

    @ActionAnnotation(title = "Workflow Node", action = "查询详情", description = "query workflow_node by uid")
    @Operation(summary = "Query WorkflowNode by UID", description = "Retrieve a specific workflow_node by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(WorkflowNodeRequest request) {
        
        WorkflowNodeResponse workflow_node = workflow_nodeRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workflow_node));
    }

    @ActionAnnotation(title = "Workflow Node", action = "新建", description = "create workflow_node")
    @Operation(summary = "Create WorkflowNode", description = "Create a new workflow_node")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(WorkflowNodeRequest request) {
        
        WorkflowNodeResponse workflow_node = workflow_nodeRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow_node));
    }

    @ActionAnnotation(title = "Workflow Node", action = "更新", description = "update workflow_node")
    @Operation(summary = "Update WorkflowNode", description = "Update an existing workflow_node")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(WorkflowNodeRequest request) {
        
        WorkflowNodeResponse workflow_node = workflow_nodeRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow_node));
    }

    @ActionAnnotation(title = "Workflow Node", action = "删除", description = "delete workflow_node")
    @Operation(summary = "Delete WorkflowNode", description = "Delete a workflow_node")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(WorkflowNodeRequest request) {
        
        workflow_nodeRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Workflow Node", action = "导出", description = "export workflow_node")
    @Operation(summary = "Export WorkflowNodes", description = "Export workflow_nodes to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(WorkflowNodeRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflow_nodeRestService,
            WorkflowNodeExcel.class,
            "Workflow Node",
            "workflow_node"
        );
    }

    
    
}