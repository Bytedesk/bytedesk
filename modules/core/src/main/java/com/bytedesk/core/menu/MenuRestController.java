/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-22 07:36:34
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

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/menu")
@AllArgsConstructor
public class MenuRestController extends BaseRestController<MenuRequest> {

    private final MenuRestService menuService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(MenuRequest request) {
        
        Page<MenuResponse> menus = menuService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @Override
    public ResponseEntity<?> queryByUser(MenuRequest request) {
        
        Page<MenuResponse> menus = menuService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @Override
    public ResponseEntity<?> create(MenuRequest request) {
        
        MenuResponse menu = menuService.create(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @Override
    public ResponseEntity<?> update(MenuRequest request) {
        
        MenuResponse menu = menuService.update(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @Override
    public ResponseEntity<?> delete(MenuRequest request) {
        
        menuService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}