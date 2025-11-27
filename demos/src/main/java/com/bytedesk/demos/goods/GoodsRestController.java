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
package com.bytedesk.demos.goods;

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
@RequestMapping("/api/v1/goods")
@AllArgsConstructor
@Tag(name = "Goods Management", description = "Goods management APIs for organizing and categorizing content with goodss")
@Description("Goods Management Controller - Content goodsging and categorization APIs")
public class GoodsRestController extends BaseRestController<GoodsRequest, GoodsRestService> {

    private final GoodsRestService goodsRestService;

    @ActionAnnotation(title = "Goods", action = "组织查询", description = "query goods by org")
    @Operation(summary = "Query Goodss by Organization", description = "Retrieve goodss for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(GoodsRequest request) {
        
        Page<GoodsResponse> goodss = goodsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(goodss));
    }

    @ActionAnnotation(title = "Goods", action = "用户查询", description = "query goods by user")
    @Operation(summary = "Query Goodss by User", description = "Retrieve goodss for the current user")
    @Override
    public ResponseEntity<?> queryByUser(GoodsRequest request) {
        
        Page<GoodsResponse> goodss = goodsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(goodss));
    }

    @ActionAnnotation(title = "Goods", action = "查询详情", description = "query goods by uid")
    @Operation(summary = "Query Goods by UID", description = "Retrieve a specific goods by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(GoodsRequest request) {
        
        GoodsResponse goods = goodsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(goods));
    }

    @ActionAnnotation(title = "Goods", action = "新建", description = "create goods")
    @Operation(summary = "Create Goods", description = "Create a new goods")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(GoodsRequest request) {
        
        GoodsResponse goods = goodsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(goods));
    }

    @ActionAnnotation(title = "Goods", action = "更新", description = "update goods")
    @Operation(summary = "Update Goods", description = "Update an existing goods")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(GoodsRequest request) {
        
        GoodsResponse goods = goodsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(goods));
    }

    @ActionAnnotation(title = "Goods", action = "删除", description = "delete goods")
    @Operation(summary = "Delete Goods", description = "Delete a goods")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(GoodsRequest request) {
        
        goodsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Goods", action = "导出", description = "export goods")
    @Operation(summary = "Export Goodss", description = "Export goodss to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(GoodsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            goodsRestService,
            GoodsExcel.class,
            "Goods",
            "goods"
        );
    }

    
    
}