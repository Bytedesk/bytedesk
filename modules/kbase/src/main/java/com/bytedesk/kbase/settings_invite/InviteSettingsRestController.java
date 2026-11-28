/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:37:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_invite;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/invite/setting")
@AllArgsConstructor
public class InviteSettingsRestController extends BaseRestController<InviteSettingsRequest, InviteSettingsRestService> {

    private final InviteSettingsRestService inviteSettingService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Invite Settings by Organization", description = "Retrieve invite settings for the current organization")
    @Override
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query settings")
    public ResponseEntity<?> queryByOrg(InviteSettingsRequest request) {
        
        Page<InviteSettingsResponse> inviteSettings = inviteSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(inviteSettings));
    }

    @Operation(summary = "Query Invite Settings by User", description = "Retrieve invite settings for the current user")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query settings")
    @Override
    public ResponseEntity<?> queryByUser(InviteSettingsRequest request) {
        
        Page<InviteSettingsResponse> inviteSettings = inviteSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(inviteSettings));
    }

    @Operation(summary = "Create Invite Settings", description = "Create a new invite setting entry")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create settings")
    @Override
    public ResponseEntity<?> create(InviteSettingsRequest request) {
        
        InviteSettingsResponse inviteSetting = inviteSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(inviteSetting));
    }

    @Operation(summary = "Update Invite Settings", description = "Update an existing invite setting entry")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update settings")
    @Override
    public ResponseEntity<?> update(InviteSettingsRequest request) {
        
        InviteSettingsResponse inviteSetting = inviteSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(inviteSetting));
    }

    @Operation(summary = "Delete Invite Settings", description = "Delete a invite setting entry")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete settings")
    @Override
    public ResponseEntity<?> delete(InviteSettingsRequest request) {
        
        inviteSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Invite Settings", description = "Export invite settings to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export settings")
    @Override
    public Object export(InviteSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Invite Settings by UID", description = "Retrieve a specific invite setting entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query settings")
    @Override
    public ResponseEntity<?> queryByUid(InviteSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}