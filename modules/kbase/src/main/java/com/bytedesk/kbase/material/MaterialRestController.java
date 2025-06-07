/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-08 15:12:42
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

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/material")
@AllArgsConstructor
public class MaterialRestController extends BaseRestController<MaterialRequest> {

    private final MaterialRestService materialRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query material by org")
    @Override
    public ResponseEntity<?> queryByOrg(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query material by user")
    @Override
    public ResponseEntity<?> queryByUser(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query material by uid")
    @Override
    public ResponseEntity<?> queryByUid(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(MaterialRequest request) {
        
        materialRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export material")
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