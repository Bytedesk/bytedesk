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
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/invite/setting")
@AllArgsConstructor
public class InviteSettingsRestController extends BaseRestController<InviteSettingsRequest, InviteSettingsRestService> {

    private final InviteSettingsRestService inviteSettingService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(InviteSettingsRequest request) {
        
        Page<InviteSettingsResponse> inviteSettings = inviteSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(inviteSettings));
    }

    @Override
    public ResponseEntity<?> queryByUser(InviteSettingsRequest request) {
        
        Page<InviteSettingsResponse> inviteSettings = inviteSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(inviteSettings));
    }

    @Override
    public ResponseEntity<?> create(InviteSettingsRequest request) {
        
        InviteSettingsResponse inviteSetting = inviteSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(inviteSetting));
    }

    @Override
    public ResponseEntity<?> update(InviteSettingsRequest request) {
        
        InviteSettingsResponse inviteSetting = inviteSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(inviteSetting));
    }

    @Override
    public ResponseEntity<?> delete(InviteSettingsRequest request) {
        
        inviteSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(InviteSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(InviteSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}