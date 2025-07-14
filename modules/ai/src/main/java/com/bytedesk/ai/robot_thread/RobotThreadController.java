/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:34:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_thread;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/robot/thread")
@AllArgsConstructor
@Description("Robot Thread Controller - Visitor robot thread management APIs")
public class RobotThreadController extends BaseRestController<RobotThreadRequest> {

    private RobotThreadService visitorThreadService;
 
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(RobotThreadRequest request) {
        
        Page<RobotThreadResponse> page = visitorThreadService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(RobotThreadRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(RobotThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
