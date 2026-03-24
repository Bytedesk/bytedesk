/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:30:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_ratedown;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ratedown/setting")
@AllArgsConstructor
@Tag(name = "Fallback Settings Management", description = "Fallback settings management APIs")
public class RatedownSettingsRestController extends BaseRestController<RatedownSettingsRequest, RatedownSettingsRestService> {

    private final RatedownSettingsRestService ratedownSettingService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Fallback Settings by Organization", description = "Allow administrators to query the organization's fallback settings list")
    @Override
    public ResponseEntity<?> queryByOrg(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Operation(summary = "Query Fallback Settings by User", description = "Query the user's fallback settings list")
    @Override
    public ResponseEntity<?> queryByUser(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Operation(summary = "Create Fallback Settings", description = "Create new fallback settings")
    @Override
    public ResponseEntity<?> create(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Operation(summary = "Update Fallback Settings", description = "Update the existing fallback settings")
    @Override
    public ResponseEntity<?> update(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Operation(summary = "Delete Fallback Settings", description = "Delete the specified fallback settings")
    @Override
    public ResponseEntity<?> delete(RatedownSettingsRequest request) {
        
        ratedownSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Fallback Settings", description = "Export fallback settings data")
    @Override
    public Object export(RatedownSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Fallback Settings by UID", description = "Query the specific fallback settings by UID")
    @Override
    public ResponseEntity<?> queryByUid(RatedownSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}