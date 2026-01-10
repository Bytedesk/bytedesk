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
package com.bytedesk.core.asset;

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
@RequestMapping("/api/v1/asset")
@AllArgsConstructor
@Tag(name = "Asset Management", description = "Asset management APIs for organizing and categorizing content with assets")
@Description("Asset Management Controller - Content assetging and categorization APIs")
public class AssetRestController extends BaseRestController<AssetRequest, AssetRestService> {

    private final AssetRestService assetRestService;

    @ActionAnnotation(title = "Asset", action = "组织查询", description = "query asset by org")
    @Operation(summary = "Query Assets by Organization", description = "Retrieve assets for the current organization")
    @PreAuthorize(AssetPermissions.HAS_ASSET_READ)
    @Override
    public ResponseEntity<?> queryByOrg(AssetRequest request) {
        
        Page<AssetResponse> assets = assetRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(assets));
    }

    @ActionAnnotation(title = "Asset", action = "用户查询", description = "query asset by user")
    @Operation(summary = "Query Assets by User", description = "Retrieve assets for the current user")
    @PreAuthorize(AssetPermissions.HAS_ASSET_READ)
    @Override
    public ResponseEntity<?> queryByUser(AssetRequest request) {
        
        Page<AssetResponse> assets = assetRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(assets));
    }

    @ActionAnnotation(title = "Asset", action = "查询详情", description = "query asset by uid")
    @Operation(summary = "Query Asset by UID", description = "Retrieve a specific asset by its unique identifier")
    @PreAuthorize(AssetPermissions.HAS_ASSET_READ)
    @Override
    public ResponseEntity<?> queryByUid(AssetRequest request) {
        
        AssetResponse asset = assetRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(asset));
    }

    @ActionAnnotation(title = "Asset", action = "新建", description = "create asset")
    @Operation(summary = "Create Asset", description = "Create a new asset")
    @Override
    @PreAuthorize(AssetPermissions.HAS_ASSET_CREATE)
    public ResponseEntity<?> create(AssetRequest request) {
        
        AssetResponse asset = assetRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(asset));
    }

    @ActionAnnotation(title = "Asset", action = "更新", description = "update asset")
    @Operation(summary = "Update Asset", description = "Update an existing asset")
    @Override
    @PreAuthorize(AssetPermissions.HAS_ASSET_UPDATE)
    public ResponseEntity<?> update(AssetRequest request) {
        
        AssetResponse asset = assetRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(asset));
    }

    @ActionAnnotation(title = "Asset", action = "删除", description = "delete asset")
    @Operation(summary = "Delete Asset", description = "Delete a asset")
    @Override
    @PreAuthorize(AssetPermissions.HAS_ASSET_DELETE)
    public ResponseEntity<?> delete(AssetRequest request) {
        
        assetRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Asset", action = "导出", description = "export asset")
    @Operation(summary = "Export Assets", description = "Export assets to Excel format")
    @Override
    @PreAuthorize(AssetPermissions.HAS_ASSET_EXPORT)
    @GetMapping("/export")
    public Object export(AssetRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            assetRestService,
            AssetExcel.class,
            "Asset",
            "asset"
        );
    }

    
    
}