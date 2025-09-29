/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-29 10:31:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/settings")
@AllArgsConstructor
@Tag(name = "Settings Management", description = "Settings management APIs for organizing and categorizing content with settings")
@Description("Settings Management Controller - Content settings and categorization APIs")
public class SettingsRestController extends BaseRestController<SettingsRequest, SettingsRestService> {

    private final SettingsRestService settingsRestService;

    @ActionAnnotation(title = "Settings", action = "组织查询", description = "query settings by org")
    @Operation(summary = "Query Settings by Organization", description = "Retrieve settings for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(SettingsRequest request) {
        
        Page<SettingsResponse> settings = settingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(settings));
    }

    @ActionAnnotation(title = "Settings", action = "用户查询", description = "query settings by user")
    @Operation(summary = "Query Settings by User", description = "Retrieve settings for the current user")
    @Override
    public ResponseEntity<?> queryByUser(SettingsRequest request) {
        
        Page<SettingsResponse> settings = settingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(settings));
    }

    @ActionAnnotation(title = "Settings", action = "查询详情", description = "query settings by uid")
    @Operation(summary = "Query Settings by UID", description = "Retrieve a specific settings by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(SettingsRequest request) {
        
        SettingsResponse settings = settingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(settings));
    }

    @ActionAnnotation(title = "Settings", action = "新建", description = "create settings")
    @Operation(summary = "Create Settings", description = "Create a new settings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(SettingsRequest request) {
        
        SettingsResponse settings = settingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(settings));
    }

    @ActionAnnotation(title = "Settings", action = "更新", description = "update settings")
    @Operation(summary = "Update Settings", description = "Update an existing settings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(SettingsRequest request) {
        
        SettingsResponse settings = settingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(settings));
    }

    @ActionAnnotation(title = "Settings", action = "删除", description = "delete settings")
    @Operation(summary = "Delete Settings", description = "Delete a settings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(SettingsRequest request) {
        
        settingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Settings", action = "导出", description = "export settings")
    @Operation(summary = "Export Settings", description = "Export settings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(SettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            settingsRestService,
            SettingsExcel.class,
            "Settings",
            "settings"
        );
    }

    
    
}