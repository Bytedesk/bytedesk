/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:37:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/robot_message")
@AllArgsConstructor
public class RobotMessageRestController extends BaseRestController<RobotMessageRequest> {

    private final RobotMessageRestService robot_messageService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(RobotMessageRequest request) {
        
        Page<RobotMessageResponse> robot_messages = robot_messageService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(robot_messages));
    }

    @Override
    public ResponseEntity<?> queryByUser(RobotMessageRequest request) {
        
        Page<RobotMessageResponse> robot_messages = robot_messageService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(robot_messages));
    }

    @Override
    public ResponseEntity<?> create(RobotMessageRequest request) {
        
        RobotMessageResponse robot_message = robot_messageService.create(request);

        return ResponseEntity.ok(JsonResult.success(robot_message));
    }

    @Override
    public ResponseEntity<?> update(RobotMessageRequest request) {
        
        RobotMessageResponse robot_message = robot_messageService.update(request);

        return ResponseEntity.ok(JsonResult.success(robot_message));
    }

    @Override
    public ResponseEntity<?> delete(RobotMessageRequest request) {
        
        robot_messageService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(RobotMessageRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}