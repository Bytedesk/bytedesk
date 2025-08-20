/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 22:53:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.menu;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/menu")
@AllArgsConstructor
@Tag(name = "Menu Management", description = "Menu management APIs for organizing application navigation and permissions")
@Description("Menu Management Controller - Application menu and navigation management APIs")
public class MenuRestController extends BaseRestController<MenuRequest, MenuRestService> {

    private final MenuRestService menuService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Menus by Organization", description = "Retrieve menus for the current organization (Admin only)")
    @Override
    public ResponseEntity<?> queryByOrg(MenuRequest request) {
        
        Page<MenuResponse> menus = menuService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @Operation(summary = "Query Menus by User", description = "Retrieve menus for the current user")
    @Override
    public ResponseEntity<?> queryByUser(MenuRequest request) {
        
        Page<MenuResponse> menus = menuService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @Operation(summary = "Create Menu", description = "Create a new menu item")
    @Override
    public ResponseEntity<?> create(MenuRequest request) {
        
        MenuResponse menu = menuService.create(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @Operation(summary = "Update Menu", description = "Update an existing menu item")
    @Override
    public ResponseEntity<?> update(MenuRequest request) {
        
        MenuResponse menu = menuService.update(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @Operation(summary = "Delete Menu", description = "Delete a menu item")
    @Override
    public ResponseEntity<?> delete(MenuRequest request) {
        
        menuService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(MenuRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(MenuRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}