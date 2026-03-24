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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/material")
@AllArgsConstructor
@Tag(name = "Material Management", description = "Material management APIs")
public class MaterialRestController extends BaseRestController<MaterialRequest, MaterialRestService> {

    private final MaterialRestService materialRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query material by org")
    @Operation(summary = "Query Materials by Organization", description = "Retrieve material list for the organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query material by user")
    @Operation(summary = "Query Materials by User", description = "Retrieve material list for the user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(MaterialRequest request) {
        
        Page<MaterialResponse> materials = materialRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(materials));
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query material by uid")
    @Operation(summary = "Query Material by UID", description = "Retrieve material details by UID")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MaterialRequest request) {
        
        MaterialResponse material = materialRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_CREATE, description = "create material")
    @Operation(summary = "Create Material", description = "Create a new material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MaterialRequest request) {
        
        MaterialResponse material = materialRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_UPDATE, description = "update material")
    @Operation(summary = "Update Material", description = "Update the existing material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MaterialRequest request) {
        
        MaterialResponse material = materialRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(material));
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_DELETE, description = "delete material")
    @Operation(summary = "Delete Material", description = "Delete the specified material")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MaterialRequest request) {
        
        materialRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_MATERIAL, action = I18Consts.I18N_ACTION_EXPORT, description = "export material")
    @Operation(summary = "Export Materials", description = "Export material data")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MaterialRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            materialRestService,
            MaterialExcel.class,
            "素材",
            "material"
        );
    }

    
    
}