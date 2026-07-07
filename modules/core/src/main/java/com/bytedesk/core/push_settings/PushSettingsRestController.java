/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/push_settings")
@AllArgsConstructor
@Tag(name = "PushSettings Management", description = "PushSettings management APIs for organizing and categorizing content with push_settingss")
@Description("PushSettings Management Controller - Content push_settingsging and categorization APIs")
public class PushSettingsRestController extends BaseRestController<PushSettingsRequest, PushSettingsRestService> {

    private final PushSettingsRestService push_settingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query push_settings by org")
    @Operation(summary = "Query PushSettingss by Organization", description = "Retrieve push_settingss for the current organization")
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(PushSettingsRequest request) {
        
        Page<PushSettingsResponse> push_settingss = push_settingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(push_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query push_settings by user")
    @Operation(summary = "Query PushSettingss by User", description = "Retrieve push_settingss for the current user")
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(PushSettingsRequest request) {
        
        Page<PushSettingsResponse> push_settingss = push_settingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(push_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query push_settings by uid")
    @Operation(summary = "Query PushSettings by UID", description = "Retrieve a specific push_settings by its unique identifier")
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(PushSettingsRequest request) {
        
        PushSettingsResponse push_settings = push_settingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(push_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create push_settings")
    @Operation(summary = "Create PushSettings", description = "Create a new push_settings")
    @Override
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PushSettingsRequest request) {
        
        PushSettingsResponse push_settings = push_settingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(push_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update push_settings")
    @Operation(summary = "Update PushSettings", description = "Update an existing push_settings")
    @Override
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PushSettingsRequest request) {
        
        PushSettingsResponse push_settings = push_settingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(push_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete push_settings")
    @Operation(summary = "Delete PushSettings", description = "Delete a push_settings")
    @Override
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody PushSettingsRequest request) {
        
        push_settingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PUSH_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export push_settings")
    @Operation(summary = "Export PushSettingss", description = "Export push_settingss to Excel format")
    @Override
    @PreAuthorize(PushSettingsPermissions.HAS_PUSH_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(PushSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            push_settingsRestService,
            PushSettingsExcel.class,
            "PushSettings",
            "push_settings"
        );
    }

    
    
}