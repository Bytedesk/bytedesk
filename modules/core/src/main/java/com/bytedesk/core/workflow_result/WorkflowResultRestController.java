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
package com.bytedesk.core.workflow_result;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/workflow/result")
@AllArgsConstructor
public class WorkflowResultRestController extends BaseRestController<WorkflowResultRequest> {

    private final WorkflowResultRestService workflowResultRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query tag by org")
    @Override
    public ResponseEntity<?> queryByOrg(WorkflowResultRequest request) {
        
        Page<WorkflowResultResponse> tags = workflowResultRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query tag by user")
    @Override
    public ResponseEntity<?> queryByUser(WorkflowResultRequest request) {
        
        Page<WorkflowResultResponse> tags = workflowResultRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query tag by uid")
    @Override
    public ResponseEntity<?> queryByUid(WorkflowResultRequest request) {
        
        WorkflowResultResponse tag = workflowResultRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(WorkflowResultRequest request) {
        
        WorkflowResultResponse tag = workflowResultRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(WorkflowResultRequest request) {
        
        WorkflowResultResponse tag = workflowResultRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(WorkflowResultRequest request) {
        
        workflowResultRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(WorkflowResultRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflowResultRestService,
            WorkflowResultExcel.class,
            "标签",
            "tag"
        );
    }

    
    
}