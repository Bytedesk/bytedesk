/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 08:30:41
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Description;

@RestController
@RequestMapping("/api/v1/service/setting")
@AllArgsConstructor
@Tag(name = "服务设置管理", description = "服务设置管理相关接口")
@Description("Service Settings Controller - Knowledge base service configuration management APIs")
public class ServiceSettingsRestController extends BaseRestController<ServiceSettingsRequest> {

    private final ServiceSettingsRestService serviceSettingService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询服务设置", description = "查询组织的服务设置列表")
    @Override
    public ResponseEntity<?> queryByOrg(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @Operation(summary = "根据用户查询服务设置", description = "查询用户的服务设置列表")
    @Override
    public ResponseEntity<?> queryByUser(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @Operation(summary = "创建服务设置", description = "创建新的服务设置")
    @Override
    public ResponseEntity<?> create(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSetting = serviceSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(serviceSetting));
    }

    @Operation(summary = "更新服务设置", description = "更新现有的服务设置")
    @Override
    public ResponseEntity<?> update(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSetting = serviceSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(serviceSetting));
    }

    @Operation(summary = "删除服务设置", description = "删除指定的服务设置")
    @Override
    public ResponseEntity<?> delete(ServiceSettingsRequest request) {
        
        serviceSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出服务设置", description = "导出服务设置数据")
    @Override
    public Object export(ServiceSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "根据UID查询服务设置", description = "通过UID查询具体的服务设置")
    @Override
    public ResponseEntity<?> queryByUid(ServiceSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}