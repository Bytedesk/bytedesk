/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:02:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.module;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/module")
@AllArgsConstructor
public class ModuleRestController extends BaseRestController<ModuleRequest, ModuleRestService> {

    private final ModuleRestService moduleRestService;

    @Operation(summary = "Query Modules by Organization", description = "Retrieve modules for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query module")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ModuleRequest request) {
        
        Page<ModuleResponse> modules = moduleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(modules));
    }

    @Operation(summary = "Query Modules by User", description = "Retrieve modules for the current user")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query module")
    @Override
    public ResponseEntity<?> queryByUser(ModuleRequest request) {
        
        Page<ModuleResponse> modules = moduleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(modules));
    }

    @Operation(summary = "Create Modules", description = "Create a new module entry")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_CREATE, description = "create module")
    @Override
    public ResponseEntity<?> create(ModuleRequest request) {
        
        ModuleResponse module = moduleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(module));
    }

    @Operation(summary = "Update Modules", description = "Update an existing module entry")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_UPDATE, description = "update module")
    @Override
    public ResponseEntity<?> update(ModuleRequest request) {
        
        ModuleResponse module = moduleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(module));
    }

    @Operation(summary = "Delete Modules", description = "Delete a module entry")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_DELETE, description = "delete module")
    @Override
    public ResponseEntity<?> delete(ModuleRequest request) {
        
        moduleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Modules", description = "Export modules to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_EXPORT, description = "export module")
    @Override
    public Object export(ModuleRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Modules by UID", description = "Retrieve a specific module entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_MODULE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query module")
    @Override
    public ResponseEntity<?> queryByUid(ModuleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}