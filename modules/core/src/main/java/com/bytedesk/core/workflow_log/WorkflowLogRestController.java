/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:24:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_log;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/v1/workflow/log")
@AllArgsConstructor
public class WorkflowLogRestController extends BaseRestController<WorkflowLogRequest, WorkflowLogRestService> {

    private final WorkflowLogRestService workflowResultRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query workflow log by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(WorkflowLogRequest request) {
        
        Page<WorkflowLogResponse> tags = workflowResultRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query workflow log by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(WorkflowLogRequest request) {
        
        Page<WorkflowLogResponse> tags = workflowResultRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query workflow log by uid")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(WorkflowLogRequest request) {
        
        WorkflowLogResponse tag = workflowResultRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_CREATE, description = "create workflow log")
    @Override
    // @PreAuthorize(WorkflowLogPermissions.HAS_WORKFLOW_LOG_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WorkflowLogRequest request) {
        
        WorkflowLogResponse tag = workflowResultRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_UPDATE, description = "update workflow log")
    @Override
    // @PreAuthorize(WorkflowLogPermissions.HAS_WORKFLOW_LOG_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody WorkflowLogRequest request) {
        
        WorkflowLogResponse tag = workflowResultRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_DELETE, description = "delete workflow log")
    @Override
    // @PreAuthorize(WorkflowLogPermissions.HAS_WORKFLOW_LOG_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody WorkflowLogRequest request) {
        
        workflowResultRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_LOG, action = I18Consts.I18N_ACTION_EXPORT, description = "export workflow log")
    @Override
    // @PreAuthorize(WorkflowLogPermissions.HAS_WORKFLOW_LOG_EXPORT)
    @GetMapping("/export")
    public Object export(WorkflowLogRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflowResultRestService,
            WorkflowLogExcel.class,
            "Workflow Log",
            "workflow_log"
        );
    }

    
    
}