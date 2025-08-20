/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:37:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention;

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

@RestController
@RequestMapping("/api/v1/intention/settings")
@AllArgsConstructor
@Tag(name = "意图设置管理", description = "意图设置管理相关接口")
public class IntentionSettingsRestController extends BaseRestController<IntentionSettingsRequest, IntentionSettingsRestService> {

    private final IntentionSettingsRestService intentionSettingsService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询意图设置", description = "查询组织的意图设置列表")
    @Override
    public ResponseEntity<?> queryByOrg(IntentionSettingsRequest request) {
        
        Page<IntentionSettingsResponse> intentionSettings = intentionSettingsService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "根据用户查询意图设置", description = "查询用户的意图设置列表")
    @Override
    public ResponseEntity<?> queryByUser(IntentionSettingsRequest request) {
        
        Page<IntentionSettingsResponse> intentionSettings = intentionSettingsService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "创建意图设置", description = "创建新的意图设置")
    @Override
    public ResponseEntity<?> create(IntentionSettingsRequest request) {
        
        IntentionSettingsResponse intentionSettings = intentionSettingsService.create(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "更新意图设置", description = "更新现有的意图设置")
    @Override
    public ResponseEntity<?> update(IntentionSettingsRequest request) {
        
        IntentionSettingsResponse intentionSettings = intentionSettingsService.update(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "删除意图设置", description = "删除指定的意图设置")
    @Override
    public ResponseEntity<?> delete(IntentionSettingsRequest request) {
        
        intentionSettingsService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出意图设置", description = "导出意图设置数据")
    @Override
    public Object export(IntentionSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "根据UID查询意图设置", description = "通过UID查询具体的意图设置")
    @Override
    public ResponseEntity<?> queryByUid(IntentionSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}