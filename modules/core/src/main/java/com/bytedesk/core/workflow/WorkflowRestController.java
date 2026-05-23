/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-25 16:39:24
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Workflow Management", description = "Workflow management APIs for process automation and task management")
@Description("Workflow Management Controller - Business process automation and workflow management APIs")
@RestController
@RequestMapping("/api/v1/workflow")
@AllArgsConstructor
public class WorkflowRestController extends BaseRestController<WorkflowRequest, WorkflowRestService> {

    private final WorkflowRestService workflowRestService;
    
    private final WorkflowService workflowService;

    @Operation(summary = "Query Workflows by Organization", description = "Retrieve workflow list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Query Workflows by User", description = "Retrieve workflow list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Query Workflow by UID", description = "Retrieve workflow details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Query IVR Demo Template Options", description = "Retrieve IVR demo template names and descriptions from server-side defaults")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = WorkflowTemplateOptionResponse.class)))
    @GetMapping("/template/ivr/demo")
    public ResponseEntity<?> queryIvrDemoTemplateOptions() {

        return ResponseEntity.ok(JsonResult.success(workflowRestService.queryIvrDemoTemplateOptions()));
    }

    @Operation(summary = "Create Workflow", description = "Create a new workflow")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_CREATE, description = "create workflow")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Update Workflow", description = "Update workflow information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = WorkflowResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_UPDATE, description = "update workflow")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Delete Workflow", description = "Delete the specified workflow")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_DELETE, description = "delete workflow")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody WorkflowRequest request) {
        
        workflowRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Reset Workflow", description = "Reset the specified demo workflow to server-side built-in schema")
    @ApiResponse(responseCode = "200", description = "Reset successfully",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = WorkflowResponse.class)))
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_RESET, description = "reset workflow")
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody WorkflowRequest request) {

        WorkflowResponse workflow = workflowRestService.reset(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Operation(summary = "Export Workflows", description = "Export workflow data")
    @ApiResponse(responseCode = "200", description = "Export successful")        
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_EXPORT, description = "export workflow")
    @Override
    @GetMapping("/export")
    public Object export(WorkflowRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflowRestService,
            WorkflowExcel.class,
            "工作流",
            "workflow"
        );
    }

    // 执行工作流
    @Operation(summary = "Execute Workflow", description = "Execute the specified workflow")
    @ApiResponse(responseCode = "200", description = "Executed successfully")
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute workflow")
    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowService.execute(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

}