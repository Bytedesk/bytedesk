/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 14:49:30
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role Management", description = "Role management APIs")
public class RoleRestController extends BaseRestController<RoleRequest, RoleRestService> {

    private final RoleRestService roleService;

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    // @PreAuthorize("hasAuthority('ROLE_READ')") // 创建成员时依赖，暂时放开
    @Override
    public ResponseEntity<?> queryByOrg(RoleRequest request) {
        
        Page<RoleResponse> roles = roleService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(roles));
    }

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(RoleRequest request) {

        Page<RoleResponse> roles = roleService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(roles));
    }

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(RoleRequest request) {
        
        RoleResponse role = roleService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(role));
    }

    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Override
    public ResponseEntity<?> create(RoleRequest request) {

        RoleResponse role = roleService.create(request);
        
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @Override
    public ResponseEntity<?> update(RoleRequest request) {
        RoleResponse role = roleService.update(request);
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Override
    public ResponseEntity<?> delete(RoleRequest request) {
        roleService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize("hasAuthority('ROLE_EXPORT')")
    @Override
    public Object export(RoleRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
}