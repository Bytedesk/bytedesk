/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
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
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/menu")
@AllArgsConstructor
@Tag(name = "Menu Management", description = "Menu management APIs for organizing and categorizing content with menus")
@Description("Menu Management Controller - Content menuging and categorization APIs")
public class MenuRestController extends BaseRestController<MenuRequest, MenuRestService> {

    private final MenuRestService menuRestService;

    @ActionAnnotation(title = "Menu", action = "组织查询", description = "query menu by org")
    @Operation(summary = "Query Menus by Organization", description = "Retrieve menus for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(MenuRequest request) {
        
        Page<MenuResponse> menus = menuRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @ActionAnnotation(title = "Menu", action = "用户查询", description = "query menu by user")
    @Operation(summary = "Query Menus by User", description = "Retrieve menus for the current user")
    @Override
    public ResponseEntity<?> queryByUser(MenuRequest request) {
        
        Page<MenuResponse> menus = menuRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(menus));
    }

    @ActionAnnotation(title = "Menu", action = "查询详情", description = "query menu by uid")
    @Operation(summary = "Query Menu by UID", description = "Retrieve a specific menu by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(MenuRequest request) {
        
        MenuResponse menu = menuRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @ActionAnnotation(title = "Menu", action = "新建", description = "create menu")
    @Operation(summary = "Create Menu", description = "Create a new menu")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(MenuRequest request) {
        
        MenuResponse menu = menuRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @ActionAnnotation(title = "Menu", action = "更新", description = "update menu")
    @Operation(summary = "Update Menu", description = "Update an existing menu")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(MenuRequest request) {
        
        MenuResponse menu = menuRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(menu));
    }

    @ActionAnnotation(title = "Menu", action = "删除", description = "delete menu")
    @Operation(summary = "Delete Menu", description = "Delete a menu")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(MenuRequest request) {
        
        menuRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Menu", action = "导出", description = "export menu")
    @Operation(summary = "Export Menus", description = "Export menus to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MenuRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            menuRestService,
            MenuExcel.class,
            "Menu",
            "menu"
        );
    }

    
    
}