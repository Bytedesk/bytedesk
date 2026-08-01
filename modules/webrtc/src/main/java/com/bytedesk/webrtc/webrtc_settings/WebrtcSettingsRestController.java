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
package com.bytedesk.webrtc.webrtc_settings;

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
@RequestMapping("/api/v1/webrtc_settings")
@AllArgsConstructor
@Tag(name = "WebrtcSettings Management", description = "WebrtcSettings management APIs for organizing and categorizing content with webrtc_settingss")
@Description("WebrtcSettings Management Controller - Content webrtc_settingsging and categorization APIs")
public class WebrtcSettingsRestController extends BaseRestController<WebrtcSettingsRequest, WebrtcSettingsRestService> {

    private final WebrtcSettingsRestService webrtcSettingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query webrtc_settings by org")
    @Operation(summary = "Query WebrtcSettingss by Organization", description = "Retrieve webrtc_settingss for the current organization")
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(WebrtcSettingsRequest request) {
        
        Page<WebrtcSettingsResponse> webrtc_settingss = webrtcSettingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webrtc_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query webrtc_settings by user")
    @Operation(summary = "Query WebrtcSettingss by User", description = "Retrieve webrtc_settingss for the current user")
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(WebrtcSettingsRequest request) {
        
        Page<WebrtcSettingsResponse> webrtc_settingss = webrtcSettingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(webrtc_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query webrtc_settings by uid")
    @Operation(summary = "Query WebrtcSettings by UID", description = "Retrieve a specific webrtc_settings by its unique identifier")
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(WebrtcSettingsRequest request) {
        
        WebrtcSettingsResponse webrtc_settings = webrtcSettingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(webrtc_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create webrtc_settings")
    @Operation(summary = "Create WebrtcSettings", description = "Create a new webrtc_settings")
    @Override
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WebrtcSettingsRequest request) {
        
        WebrtcSettingsResponse webrtc_settings = webrtcSettingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webrtc_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update webrtc_settings")
    @Operation(summary = "Update WebrtcSettings", description = "Update an existing webrtc_settings")
    @Override
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody WebrtcSettingsRequest request) {
        
        WebrtcSettingsResponse webrtc_settings = webrtcSettingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webrtc_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete webrtc_settings")
    @Operation(summary = "Delete WebrtcSettings", description = "Delete a webrtc_settings")
    @Override
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody WebrtcSettingsRequest request) {
        
        webrtcSettingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBRTC_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export webrtc_settings")
    @Operation(summary = "Export WebrtcSettingss", description = "Export webrtc_settingss to Excel format")
    @Override
    @PreAuthorize(WebrtcSettingsPermissions.HAS_WEBRTC_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(WebrtcSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            webrtcSettingsRestService,
            WebrtcSettingsExcel.class,
            "WebrtcSettings",
            "webrtc_settings"
        );
    }

    
    
}