/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role Management", description = "Role management APIs for managing user roles and permissions")
@Description("Role Management Controller - Role and permission management APIs")
public class RoleRestController extends BaseRestController<RoleRequest, RoleRestService> {

    private final RoleRestService roleRestService;

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query role by org")
    @Operation(summary = "Query Roles by Organization", description = "Retrieve roles for the current organization")
    @PreAuthorize(RolePermissions.HAS_ROLE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(RoleRequest request) {
        
        Page<RoleResponse> roles = roleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query role by user")
    @Operation(summary = "Query Roles by User", description = "Retrieve roles for the current user")
    @PreAuthorize(RolePermissions.HAS_ROLE_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(RoleRequest request) {

        Page<RoleResponse> roles = roleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query role by uid")
    @Operation(summary = "Query Role by UID", description = "Retrieve a specific role by its unique identifier")
    @PreAuthorize(RolePermissions.HAS_ROLE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(RoleRequest request) {
        
        RoleResponse role = roleRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(role));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_CREATE, description = "create role")
    @Operation(summary = "Create Role", description = "Create a new role")
    @PreAuthorize(RolePermissions.HAS_ROLE_CREATE)
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RoleRequest request) {

        RoleResponse role = roleRestService.create(request);
        
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_UPDATE, description = "update role")
    @Operation(summary = "Update Role", description = "Update an existing role")
    @PreAuthorize(RolePermissions.HAS_ROLE_UPDATE)
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody RoleRequest request) {

        RoleResponse role = roleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(role));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_DELETE, description = "delete role")
    @Operation(summary = "Delete Role", description = "Delete a role")
    @PreAuthorize(RolePermissions.HAS_ROLE_DELETE)
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody RoleRequest request) {

        roleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_EXPORT, description = "export role")
    @Operation(summary = "Export Roles", description = "Export roles to Excel format")
    @PreAuthorize(RolePermissions.HAS_ROLE_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(RoleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            roleRestService,
            RoleExcel.class,
            "Role",
            "role"
        );
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_AUTHORITIES_RESET_LEVEL, description = "reset role authorities by level")
    @Operation(summary = "Reset role authorities for a level", description = "Reapply canonical permissions for the specified level on a role")
    @PreAuthorize(RolePermissions.HAS_ROLE_UPDATE)
    @PostMapping("/authorities/reset")
    public ResponseEntity<?> resetAuthorities(@RequestBody RoleRequest request) {

        RoleResponse role = roleRestService.resetAuthorities(request);
        
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_AUTHORITIES_ADD, description = "add role authorities")
    @Operation(summary = "Add role authorities", description = "Add authorities to a role without updating the whole role")
    @PreAuthorize(RolePermissions.HAS_ROLE_UPDATE)
    @PostMapping("/authorities/add")
    public ResponseEntity<?> addAuthorities(@RequestBody RoleRequest request) {

        RoleResponse role = roleRestService.addAuthorities(request);

        return ResponseEntity.ok(JsonResult.success(role));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROLE, action = I18Consts.I18N_ACTION_AUTHORITIES_REMOVE, description = "remove role authorities")
    @Operation(summary = "Remove role authorities", description = "Remove authorities from a role without updating the whole role")
    @PreAuthorize(RolePermissions.HAS_ROLE_UPDATE)
    @PostMapping("/authorities/remove")
    public ResponseEntity<?> removeAuthorities(@RequestBody RoleRequest request) {

        RoleResponse role = roleRestService.removeAuthorities(request);

        return ResponseEntity.ok(JsonResult.success(role));
    }

}