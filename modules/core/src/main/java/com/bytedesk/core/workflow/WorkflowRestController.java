/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 10:55:10
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/workflow")
@AllArgsConstructor
public class WorkflowRestController extends BaseRestController<WorkflowRequest> {

    private final WorkflowRestService workflowRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Override
    public ResponseEntity<?> queryByUser(WorkflowRequest request) {
        
        Page<WorkflowResponse> workflow = workflowRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Override
    public ResponseEntity<?> create(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Override
    public ResponseEntity<?> update(WorkflowRequest request) {
        
        WorkflowResponse workflow = workflowRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow));
    }

    @Override
    public ResponseEntity<?> delete(WorkflowRequest request) {
        
        workflowRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(WorkflowRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(WorkflowRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}