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
package com.bytedesk.ai.embedding_settings;

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
@RequestMapping("/api/v1/embedding_settings")
@AllArgsConstructor
@Tag(name = "EmbeddingSettings Management", description = "EmbeddingSettings management APIs for organizing and categorizing content with embedding_settingss")
@Description("EmbeddingSettings Management Controller - Content embedding_settingsging and categorization APIs")
public class EmbeddingSettingsRestController extends BaseRestController<EmbeddingSettingsRequest, EmbeddingSettingsRestService> {

    private final EmbeddingSettingsRestService embedding_settingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query embedding_settings by org")
    @Operation(summary = "Query EmbeddingSettingss by Organization", description = "Retrieve embedding_settingss for the current organization")
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(EmbeddingSettingsRequest request) {
        
        Page<EmbeddingSettingsResponse> embedding_settingss = embedding_settingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(embedding_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query embedding_settings by user")
    @Operation(summary = "Query EmbeddingSettingss by User", description = "Retrieve embedding_settingss for the current user")
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(EmbeddingSettingsRequest request) {
        
        Page<EmbeddingSettingsResponse> embedding_settingss = embedding_settingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(embedding_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query embedding_settings by uid")
    @Operation(summary = "Query EmbeddingSettings by UID", description = "Retrieve a specific embedding_settings by its unique identifier")
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(EmbeddingSettingsRequest request) {
        
        EmbeddingSettingsResponse embedding_settings = embedding_settingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(embedding_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create embedding_settings")
    @Operation(summary = "Create EmbeddingSettings", description = "Create a new embedding_settings")
    @Override
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EmbeddingSettingsRequest request) {
        
        EmbeddingSettingsResponse embedding_settings = embedding_settingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(embedding_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update embedding_settings")
    @Operation(summary = "Update EmbeddingSettings", description = "Update an existing embedding_settings")
    @Override
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody EmbeddingSettingsRequest request) {
        
        EmbeddingSettingsResponse embedding_settings = embedding_settingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(embedding_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete embedding_settings")
    @Operation(summary = "Delete EmbeddingSettings", description = "Delete a embedding_settings")
    @Override
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody EmbeddingSettingsRequest request) {
        
        embedding_settingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_EMBEDDING_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export embedding_settings")
    @Operation(summary = "Export EmbeddingSettingss", description = "Export embedding_settingss to Excel format")
    @Override
    @PreAuthorize(EmbeddingSettingsPermissions.HAS_EMBEDDING_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(EmbeddingSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            embedding_settingsRestService,
            EmbeddingSettingsExcel.class,
            "EmbeddingSettings",
            "embedding_settings"
        );
    }

    
    
}