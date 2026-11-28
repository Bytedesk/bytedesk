/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-24 09:50:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:23:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/authority")
@AllArgsConstructor
@Tag(name = "Authority Management", description = "Authority management APIs")
public class AuthorityRestController extends BaseRestController<AuthorityRequest, AuthorityRestService> {

    private final AuthorityRestService authorityRestService;

    // @PreAuthorize(AuthorityPermissions.HAS_AUTHORITY_READ)
    @Operation(summary = "Query Authentication by Organization", description = "Retrieve authentication for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query authority")
    @Override
    public ResponseEntity<?> queryByOrg(AuthorityRequest request) {

        Page<AuthorityResponse> authorities = authorityRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(authorities));
    }

    @Operation(summary = "Query Authentication by User", description = "Retrieve authentication for the current user")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query authority")
    @Override
    public ResponseEntity<?> queryByUser(AuthorityRequest request) {

        Page<AuthorityResponse> authorities = authorityRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(authorities));
    }

    @Operation(summary = "Query Authentication by UID", description = "Retrieve a specific authentication entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query authority")
    @Override
    public ResponseEntity<?> queryByUid(AuthorityRequest request) {
        
        AuthorityResponse authority = authorityRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(authority));
    }

    // 都是自动创建，不需要此接口
    // @Override
    // @PreAuthorize(RolePermissions.ROLE_SUPER)
    // public ResponseEntity<?> create(AuthorityRequest request) {

    //     AuthorityResponse authority = authorityRestService.create(request);

    //     return ResponseEntity.ok(JsonResult.success(authority));
    @Operation(summary = "Update Authentication", description = "Update an existing authentication entry")
    // }

    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_UPDATE, description = "update authority")
    @Override
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> update(AuthorityRequest request) {

        AuthorityResponse authority = authorityRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(authority));
    }

    @Operation(summary = "Delete Authentication", description = "Delete a authentication entry")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_DELETE, description = "delete authority")
    @Override
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> delete(AuthorityRequest request) {

        authorityRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Authentication", description = "Export authentication to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_EXPORT, description = "export authority")
    @Override
    public Object export(AuthorityRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Reset Authority Levels", description = "Reset authority levels to defaults")
    @ActionAnnotation(title = I18Consts.I18N_AUTHORITY, action = I18Consts.I18N_ACTION_AUTHORITIES_RESET_LEVEL, description = "reset authority levels authority")
    @PostMapping("/reset/level")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> resetAuthorityLevels(@RequestBody(required = false) AuthorityRequest request) {

        Map<String, Integer> result = authorityRestService.resetAuthorityLevels();

        return ResponseEntity.ok(JsonResult.success(result));
    }

}