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
package com.bytedesk.kbase.settings_service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/service/setting")
@AllArgsConstructor
public class ServiceSettingsRestController extends BaseRestController<ServiceSettingsRequest> {

    private final ServiceSettingsRestService serviceSettingService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @Override
    public ResponseEntity<?> queryByUser(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @Override
    public ResponseEntity<?> create(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSetting = serviceSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(serviceSetting));
    }

    @Override
    public ResponseEntity<?> update(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSetting = serviceSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(serviceSetting));
    }

    @Override
    public ResponseEntity<?> delete(ServiceSettingsRequest request) {
        
        serviceSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(ServiceSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(ServiceSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}