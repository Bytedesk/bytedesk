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
package com.bytedesk.core.organization_settings;

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
@RequestMapping("/api/v1/organization/settings")
@AllArgsConstructor
@Tag(name = "OrganizationSettings Management", description = "OrganizationSettings management APIs for organizing and categorizing content with organization_settingss")
@Description("OrganizationSettings Management Controller - Content organization_settingsging and categorization APIs")
public class OrganizationSettingsRestController extends BaseRestController<OrganizationSettingsRequest, OrganizationSettingsRestService> {

    private final OrganizationSettingsRestService organization_settingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query organization_settings by org")
    @Operation(summary = "Query OrganizationSettingss by Organization", description = "Retrieve organization_settingss for the current organization")
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(OrganizationSettingsRequest request) {
        
        Page<OrganizationSettingsResponse> organization_settingss = organization_settingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(organization_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query organization_settings by user")
    @Operation(summary = "Query OrganizationSettingss by User", description = "Retrieve organization_settingss for the current user")
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(OrganizationSettingsRequest request) {
        
        Page<OrganizationSettingsResponse> organization_settingss = organization_settingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(organization_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query organization_settings by uid")
    @Operation(summary = "Query OrganizationSettings by UID", description = "Retrieve a specific organization_settings by its unique identifier")
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(OrganizationSettingsRequest request) {
        
        OrganizationSettingsResponse organization_settings = organization_settingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(organization_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create organization_settings")
    @Operation(summary = "Create OrganizationSettings", description = "Create a new organization_settings")
    @Override
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody OrganizationSettingsRequest request) {
        
        OrganizationSettingsResponse organization_settings = organization_settingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(organization_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update organization_settings")
    @Operation(summary = "Update OrganizationSettings", description = "Update an existing organization_settings")
    @Override
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody OrganizationSettingsRequest request) {
        
        OrganizationSettingsResponse organization_settings = organization_settingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(organization_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete organization_settings")
    @Operation(summary = "Delete OrganizationSettings", description = "Delete a organization_settings")
    @Override
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrganizationSettingsRequest request) {
        
        organization_settingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_ORGANIZATION_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export organization_settings")
    @Operation(summary = "Export OrganizationSettingss", description = "Export organization_settingss to Excel format")
    @Override
    @PreAuthorize(OrganizationSettingsPermissions.HAS_ORGANIZATION_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(OrganizationSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            organization_settingsRestService,
            OrganizationSettingsExcel.class,
            "OrganizationSettings",
            "organization_settings"
        );
    }

    
    
}