/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:17:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@AllArgsConstructor
@RequestMapping("/api/v1/category")
@Tag(name = "Category Management", description = "Category management APIs for organizing and classifying content")
@Description("Category Management Controller - Content categorization and organization APIs")
public class CategoryRestController extends BaseRestController<CategoryRequest, CategoryRestService> {
    
    private final CategoryRestService categoryRestService;
    
    @ActionAnnotation(title = "Category", action = "组织查询", description = "query category by org")
    @Operation(summary = "Query Categories by Organization", description = "Retrieve categories for the current organization")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_READ)
    @Override
    public ResponseEntity<?> queryByOrg(CategoryRequest request) {
        
        Page<CategoryResponse> page = categoryRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "Category", action = "用户查询", description = "query category by user")
    @Operation(summary = "Query Categories by User", description = "Retrieve categories for the current user")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_READ)
    @Override
    public ResponseEntity<?> queryByUser(CategoryRequest request) {
        
        Page<CategoryResponse> page = categoryRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = "Category", action = "查询详情", description = "query category by uid")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_READ)
    @Override
    public ResponseEntity<?> queryByUid(CategoryRequest request) {
        
        CategoryResponse response = categoryRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "Category", action = "新建", description = "create category")
    @Operation(summary = "Create Category", description = "Create a new category")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody CategoryRequest request) {
        
        CategoryResponse response = categoryRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "Category", action = "更新", description = "update category")
    @Operation(summary = "Update Category", description = "Update an existing category")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody CategoryRequest request) {
        
        CategoryResponse response = categoryRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @ActionAnnotation(title = "Category", action = "删除", description = "delete category")
    @Operation(summary = "Delete Category", description = "Delete a category")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody CategoryRequest request) {
        
        categoryRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @ActionAnnotation(title = "Category", action = "导出", description = "export category")
    @PreAuthorize(CategoryPermissions.HAS_CATEGORY_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(CategoryRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

}
