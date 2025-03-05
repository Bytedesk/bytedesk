/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:37:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
public class RoleRestController extends BaseRestController<RoleRequest> {

    private final RoleRestService roleService;

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @GetMapping("/query/super")
    public ResponseEntity<?> queryBySuper(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryBySuper(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @Override
    public ResponseEntity<?> queryByUser(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @Override
    public ResponseEntity<?> create(RoleRequest request) {
        RoleResponse role = roleService.create(request);
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @Override
    public ResponseEntity<?> update(RoleRequest request) {
        RoleResponse role = roleService.update(request);
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @Override
    public ResponseEntity<?> delete(RoleRequest request) {
        roleService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(RoleRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(RoleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
}