/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 23:13:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.form;

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
@RequestMapping("/api/v1/form")
@AllArgsConstructor
public class FormRestController extends BaseRestController<FormRequest> {

    private final FormRestService formService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(FormRequest request) {
        
        Page<FormResponse> form = formService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @Override
    public ResponseEntity<?> queryByUser(FormRequest request) {
        
        Page<FormResponse> form = formService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(form));
    }

    @Override
    public ResponseEntity<?> create(FormRequest request) {
        
        FormResponse ticket_process = formService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticket_process));
    }

    @Override
    public ResponseEntity<?> update(FormRequest request) {
        
        FormResponse ticket_process = formService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticket_process));
    }

    @Override
    public ResponseEntity<?> delete(FormRequest request) {
        
        formService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(FormRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }
    
}