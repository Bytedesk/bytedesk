/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 07:07:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/robot/message")
@AllArgsConstructor
public class RobotMessageRestController extends BaseRestController<RobotMessageRequest> {

    @Override
    public ResponseEntity<?> queryByOrg(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public ResponseEntity<?> queryByUser(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public ResponseEntity<?> queryByUid(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ResponseEntity<?> create(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(RobotMessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(RobotMessageRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }
    
}
