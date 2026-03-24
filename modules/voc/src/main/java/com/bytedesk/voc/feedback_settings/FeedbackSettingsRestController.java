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
package com.bytedesk.voc.feedback_settings;

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
@RequestMapping("/api/v1/feedback_settings")
@AllArgsConstructor
@Tag(name = "FeedbackSettings Management", description = "FeedbackSettings management APIs for organizing and categorizing content with feedback_settingss")
@Description("FeedbackSettings Management Controller - Content feedback_settingsging and categorization APIs")
public class FeedbackSettingsRestController extends BaseRestController<FeedbackSettingsRequest, FeedbackSettingsRestService> {

    private final FeedbackSettingsRestService audioFileRestService;

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query feedback_settings by org")
    @Operation(summary = "Query FeedbackSettingss by Organization", description = "Retrieve feedback_settingss for the current organization")
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(FeedbackSettingsRequest request) {
        
        Page<FeedbackSettingsResponse> feedback_settingss = audioFileRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(feedback_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query feedback_settings by user")
    @Operation(summary = "Query FeedbackSettingss by User", description = "Retrieve feedback_settingss for the current user")
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(FeedbackSettingsRequest request) {
        
        Page<FeedbackSettingsResponse> feedback_settingss = audioFileRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(feedback_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query feedback_settings by uid")
    @Operation(summary = "Query FeedbackSettings by UID", description = "Retrieve a specific feedback_settings by its unique identifier")
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(FeedbackSettingsRequest request) {
        
        FeedbackSettingsResponse feedback_settings = audioFileRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(feedback_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create feedback_settings")
    @Operation(summary = "Create FeedbackSettings", description = "Create a new feedback_settings")
    @Override
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody FeedbackSettingsRequest request) {
        
        FeedbackSettingsResponse feedback_settings = audioFileRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(feedback_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update feedback_settings")
    @Operation(summary = "Update FeedbackSettings", description = "Update an existing feedback_settings")
    @Override
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody FeedbackSettingsRequest request) {
        
        FeedbackSettingsResponse feedback_settings = audioFileRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(feedback_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete feedback_settings")
    @Operation(summary = "Delete FeedbackSettings", description = "Delete a feedback_settings")
    @Override
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody FeedbackSettingsRequest request) {
        
        audioFileRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export feedback_settings")
    @Operation(summary = "Export FeedbackSettingss", description = "Export feedback_settingss to Excel format")
    @Override
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(FeedbackSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            audioFileRestService,
            FeedbackSettingsExcel.class,
            "FeedbackSettings",
            "feedback_settings"
        );
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK_SETTINGS, action = I18Consts.I18N_ACTION_PUBLISH, description = "publish feedback_settings")
    @Operation(summary = "Publish FeedbackSettings", description = "Enable and publish a feedback settings template")
    @PreAuthorize(FeedbackSettingsPermissions.HAS_FEEDBACK_SETTINGS_UPDATE)
    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody FeedbackSettingsRequest request) {
        FeedbackSettingsResponse feedback_settings = audioFileRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(feedback_settings));
    }

    
    
}