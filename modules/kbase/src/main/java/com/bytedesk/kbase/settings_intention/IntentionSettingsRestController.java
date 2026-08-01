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
@Tag(name = "Intention Settings Management", description = "Intention settings management APIs")
public class IntentionSettingsRestController extends BaseRestController<IntentionSettingsRequest, IntentionSettingsRestService> {

    private final IntentionSettingsRestService intentionSettingsService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Intention Settings by Organization", description = "Retrieve intention settings list for the organization")
    @Override
    public ResponseEntity<?> queryByOrg(IntentionSettingsRequest request) {
        
        Page<IntentionSettingsResponse> intentionSettings = intentionSettingsService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "Query Intention Settings by User", description = "Retrieve intention settings list for the user")
    @Override
    public ResponseEntity<?> queryByUser(IntentionSettingsRequest request) {
        
        Page<IntentionSettingsResponse> intentionSettings = intentionSettingsService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "Create Intention Settings", description = "Create new intention settings")
    @Override
    public ResponseEntity<?> create(IntentionSettingsRequest request) {
        
        IntentionSettingsResponse intentionSettings = intentionSettingsService.create(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "Update Intention Settings", description = "Update existing intention settings")
    @Override
    public ResponseEntity<?> update(IntentionSettingsRequest request) {
        
        IntentionSettingsResponse intentionSettings = intentionSettingsService.update(request);

        return ResponseEntity.ok(JsonResult.success(intentionSettings));
    }

    @Operation(summary = "Delete Intention Settings", description = "Delete the specified intention settings")
    @Override
    public ResponseEntity<?> delete(IntentionSettingsRequest request) {
        
        intentionSettingsService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Intention Settings", description = "Export intention settings data")
    @Override
    public Object export(IntentionSettingsRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Intention Settings by UID", description = "Retrieve intention settings details by UID")
    @Override
    public ResponseEntity<?> queryByUid(IntentionSettingsRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}