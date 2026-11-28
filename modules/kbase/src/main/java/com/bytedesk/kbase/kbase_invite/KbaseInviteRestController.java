/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-08 17:12:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase_invite;

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
@RequestMapping("/api/v1/kbase/invite")
@AllArgsConstructor
public class KbaseInviteRestController extends BaseRestController<KbaseInviteRequest, KbaseInviteRestService> {

    private final KbaseInviteRestService tagService;

    @Operation(summary = "Query Kbase Invites by Organization", description = "Retrieve kbase invites for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query kbase invite")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(KbaseInviteRequest request) {
        
        Page<KbaseInviteResponse> tags = tagService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Operation(summary = "Query Kbase Invites by User", description = "Retrieve kbase invites for the current user")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query kbase invite")
    @Override
    public ResponseEntity<?> queryByUser(KbaseInviteRequest request) {
        
        Page<KbaseInviteResponse> tags = tagService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @Operation(summary = "Create Kbase Invites", description = "Create a new kbase invite entry")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_CREATE, description = "create kbase invite")
    @Override
    public ResponseEntity<?> create(KbaseInviteRequest request) {
        
        KbaseInviteResponse tag = tagService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Operation(summary = "Update Kbase Invites", description = "Update an existing kbase invite entry")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_UPDATE, description = "update kbase invite")
    @Override
    public ResponseEntity<?> update(KbaseInviteRequest request) {
        
        KbaseInviteResponse tag = tagService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @Operation(summary = "Delete Kbase Invites", description = "Delete a kbase invite entry")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_DELETE, description = "delete kbase invite")
    @Override
    public ResponseEntity<?> delete(KbaseInviteRequest request) {
        
        tagService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Kbase Invites", description = "Export kbase invites to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_EXPORT, description = "export kbase invite")
    @Override
    public Object export(KbaseInviteRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Kbase Invites by UID", description = "Retrieve a specific kbase invite entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_KBASE_INVITE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query kbase invite")
    @Override
    public ResponseEntity<?> queryByUid(KbaseInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}