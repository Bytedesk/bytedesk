/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.crm.product;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/product")
@AllArgsConstructor
@Tag(name = "Product Management", description = "Product management APIs for organizing and categorizing content with products")
@Description("Product Management Controller - Content productging and categorization APIs")
public class ProductRestController extends BaseRestController<ProductRequest, ProductRestService> {

    private final ProductRestService productRestService;

    @ActionAnnotation(title = "Product", action = "组织查询", description = "query product by org")
    @Operation(summary = "Query Products by Organization", description = "Retrieve products for the current organization")
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ProductRequest request) {
        
        Page<ProductResponse> products = productRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(products));
    }

    @ActionAnnotation(title = "Product", action = "用户查询", description = "query product by user")
    @Operation(summary = "Query Products by User", description = "Retrieve products for the current user")
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_READ)
    @Override
    public ResponseEntity<?> queryByUser(ProductRequest request) {
        
        Page<ProductResponse> products = productRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(products));
    }

    @ActionAnnotation(title = "Product", action = "查询详情", description = "query product by uid")
    @Operation(summary = "Query Product by UID", description = "Retrieve a specific product by its unique identifier")
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_READ)
    @Override
    public ResponseEntity<?> queryByUid(ProductRequest request) {
        
        ProductResponse product = productRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(product));
    }

    @ActionAnnotation(title = "Product", action = "新建", description = "create product")
    @Operation(summary = "Create Product", description = "Create a new product")
    @Override
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_CREATE)
    public ResponseEntity<?> create(ProductRequest request) {
        
        ProductResponse product = productRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(product));
    }

    @ActionAnnotation(title = "Product", action = "更新", description = "update product")
    @Operation(summary = "Update Product", description = "Update an existing product")
    @Override
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_UPDATE)
    public ResponseEntity<?> update(ProductRequest request) {
        
        ProductResponse product = productRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(product));
    }

    @ActionAnnotation(title = "Product", action = "删除", description = "delete product")
    @Operation(summary = "Delete Product", description = "Delete a product")
    @Override
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_DELETE)
    public ResponseEntity<?> delete(ProductRequest request) {
        
        productRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Product", action = "导出", description = "export product")
    @Operation(summary = "Export Products", description = "Export products to Excel format")
    @Override
    @PreAuthorize(ProductPermissions.HAS_PRODUCT_EXPORT)
    @GetMapping("/export")
    public Object export(ProductRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            productRestService,
            ProductExcel.class,
            "Product",
            "product"
        );
    }

    
    
}