/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:40:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 22:56:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/action")
@Tag(name = "Action Log Management", description = "Action log management APIs for tracking user activities and system events")
public class ActionRestController extends BaseRestController<ActionRequest, ActionRestService> {
    
    private final ActionRestService actionRestService;

    @Operation(summary = "Query Action Logs by Organization", description = "Retrieve action logs for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ActionRequest request) {

        Page<ActionResponse> page = actionRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Action Logs by User", description = "Retrieve action logs for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ActionRequest request) {
        
        Page<ActionResponse> page = actionRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Action Log by UID", description = "Retrieve a specific action log by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ActionRequest request) {
        
        ActionResponse action = actionRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Create Action Log", description = "Create a new action log entry")
    @Override
    public ResponseEntity<?> create(@RequestBody ActionRequest request) {
        
        ActionResponse action = actionRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Update Action Log", description = "Update an existing action log entry")
    @Override
    public ResponseEntity<?> update(@RequestBody ActionRequest request) {
        
        ActionResponse action = actionRestService.update(request);  

        return ResponseEntity.ok(JsonResult.success(action));
    }

    @Operation(summary = "Delete Action Log", description = "Delete an action log entry")
    @Override
    public ResponseEntity<?> delete(@RequestBody ActionRequest request) {
        
        actionRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    // @ActionAnnotation(title = "action", action = "导出", description = "export action")
    @Operation(summary = "Export Action Logs", description = "Export action logs to Excel format")
    @GetMapping("/export")
    public Object export(ActionRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            actionRestService,
            ActionExcel.class,
            "日志",
            "actions"
        );
    }

    

    
}
