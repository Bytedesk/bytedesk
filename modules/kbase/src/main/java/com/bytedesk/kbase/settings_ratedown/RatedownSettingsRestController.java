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

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/ratedown/setting")
@AllArgsConstructor
public class RatedownSettingsRestController extends BaseRestController<RatedownSettingsRequest> {

    private final RatedownSettingsRestService ratedownSettingService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Override
    public ResponseEntity<?> queryByUser(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Override
    public ResponseEntity<?> create(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Override
    public ResponseEntity<?> update(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Override
    public ResponseEntity<?> delete(RatedownSettingsRequest request) {
        
        ratedownSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(RatedownSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(RatedownSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}