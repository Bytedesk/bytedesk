/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 18:13:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ticket/process")
@AllArgsConstructor
public class TicketProcessRestController extends BaseRestController<TicketProcessRequest> {

    private final TicketProcessRestService processService;
    
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(TicketProcessRequest request) {
        
        Page<TicketProcessResponse> process = processService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> queryByUser(TicketProcessRequest request) {
        
        Page<TicketProcessResponse> process = processService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> create(TicketProcessRequest request) {
        
        TicketProcessResponse process = processService.create(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> update(TicketProcessRequest request) {
        
        TicketProcessResponse process = processService.update(request);

        return ResponseEntity.ok(JsonResult.success(process));
    }

    @Override
    public ResponseEntity<?> delete(TicketProcessRequest request) {
        
        processService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}