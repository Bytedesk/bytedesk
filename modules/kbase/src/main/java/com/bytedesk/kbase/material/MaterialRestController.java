/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 23:31:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.material;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/material")
@AllArgsConstructor
@Tag(name = "素材管理", description = "素材管理相关接口")
public class MaterialRestController extends BaseRestController<MaterialRequest> {

    private final MaterialRestService materialRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query material by org")
    @Operation(summary = "根据组织查询素材", description = "查询组织的素材列表")
    @Override
    public ResponseEntity<?> queryByOrg(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query material by user")
    @Operation(summary = "根据用户查询素材", description = "查询用户的素材列表")
    @Override
    public ResponseEntity<?> queryByUser(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query material by uid")
    @Operation(summary = "根据UID查询素材", description = "通过UID查询具体的素材")
    @Override
    public ResponseEntity<?> queryByUid(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create material")
    @Operation(summary = "创建素材", description = "创建新的素材")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update material")
    @Operation(summary = "更新素材", description = "更新现有的素材")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete material")
    @Operation(summary = "删除素材", description = "删除指定的素材")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(MaterialRequest request) {
        
        materialRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export material")
    @Operation(summary = "导出素材", description = "导出素材数据")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MaterialRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            materialRestService,
            MaterialExcel.class,
            "标签",
            "material"
        );
    }

    
    
}