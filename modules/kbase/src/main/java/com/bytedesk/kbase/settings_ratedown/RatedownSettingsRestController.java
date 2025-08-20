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
@Tag(name = "降级设置管理", description = "降级设置管理相关接口")
public class RatedownSettingsRestController extends BaseRestController<RatedownSettingsRequest, RatedownSettingsRestService> {

    private final RatedownSettingsRestService ratedownSettingService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询降级设置", description = "管理员查询组织的降级设置列表")
    @Override
    public ResponseEntity<?> queryByOrg(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Operation(summary = "根据用户查询降级设置", description = "查询用户的降级设置列表")
    @Override
    public ResponseEntity<?> queryByUser(RatedownSettingsRequest request) {
        
        Page<RatedownSettingsResponse> ratedownSettings = ratedownSettingService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSettings));
    }

    @Operation(summary = "创建降级设置", description = "创建新的降级设置")
    @Override
    public ResponseEntity<?> create(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.create(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Operation(summary = "更新降级设置", description = "更新现有的降级设置")
    @Override
    public ResponseEntity<?> update(RatedownSettingsRequest request) {
        
        RatedownSettingsResponse ratedownSetting = ratedownSettingService.update(request);

        return ResponseEntity.ok(JsonResult.success(ratedownSetting));
    }

    @Operation(summary = "删除降级设置", description = "删除指定的降级设置")
    @Override
    public ResponseEntity<?> delete(RatedownSettingsRequest request) {
        
        ratedownSettingService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出降级设置", description = "导出降级设置数据")
    @Override
    public Object export(RatedownSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "根据UID查询降级设置", description = "通过UID查询具体的降级设置")
    @Override
    public ResponseEntity<?> queryByUid(RatedownSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}