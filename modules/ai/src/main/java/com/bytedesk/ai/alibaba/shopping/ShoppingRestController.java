/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:39:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.shopping;

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
@RequestMapping("/api/v1/shopping")
@AllArgsConstructor
@Tag(name = "Shopping Management", description = "Shopping management APIs for organizing and categorizing content with shoppings")
@Description("Shopping Management Controller - Content shopping and categorization APIs")
public class ShoppingRestController extends BaseRestController<ShoppingRequest> {

    private final ShoppingRestService shoppingRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query shopping by org")
    @Operation(summary = "Query Shoppings by Organization", description = "Retrieve shoppings for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ShoppingRequest request) {
        
        Page<ShoppingResponse> shoppings = shoppingRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(shoppings));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query shopping by user")
    @Operation(summary = "Query Shoppings by User", description = "Retrieve shoppings for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ShoppingRequest request) {
        
        Page<ShoppingResponse> shoppings = shoppingRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(shoppings));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query shopping by uid")
    @Operation(summary = "Query Shopping by UID", description = "Retrieve a specific shopping by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ShoppingRequest request) {
        
        ShoppingResponse shopping = shoppingRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(shopping));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create shopping")
    @Operation(summary = "Create Shopping", description = "Create a new shopping")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ShoppingRequest request) {
        
        ShoppingResponse shopping = shoppingRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(shopping));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update shopping")
    @Operation(summary = "Update Shopping", description = "Update an existing shopping")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ShoppingRequest request) {
        
        ShoppingResponse shopping = shoppingRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(shopping));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete shopping")
    @Operation(summary = "Delete Shopping", description = "Delete a shopping")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ShoppingRequest request) {
        
        shoppingRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export shopping")
    @Operation(summary = "Export Shoppings", description = "Export shoppings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(ShoppingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            shoppingRestService,
            ShoppingExcel.class,
            "标签",
            "shopping"
        );
    }

    
    
}