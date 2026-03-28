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
package com.bytedesk.core.workflow_settings;

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
@RequestMapping("/api/v1/workflow_settings")
@AllArgsConstructor
@Tag(name = "WorkflowSettings Management", description = "WorkflowSettings management APIs for organizing and categorizing content with workflow_settingss")
@Description("WorkflowSettings Management Controller - Content workflow_settingsging and categorization APIs")
public class WorkflowSettingsRestController extends BaseRestController<WorkflowSettingsRequest, WorkflowSettingsRestService> {

    private final WorkflowSettingsRestService workflow_settingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query workflow_settings by org")
    @Operation(summary = "Query WorkflowSettingss by Organization", description = "Retrieve workflow_settingss for the current organization")
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(WorkflowSettingsRequest request) {
        
        Page<WorkflowSettingsResponse> workflow_settingss = workflow_settingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workflow_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query workflow_settings by user")
    @Operation(summary = "Query WorkflowSettingss by User", description = "Retrieve workflow_settingss for the current user")
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(WorkflowSettingsRequest request) {
        
        Page<WorkflowSettingsResponse> workflow_settingss = workflow_settingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workflow_settingss));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query workflow_settings by uid")
    @Operation(summary = "Query WorkflowSettings by UID", description = "Retrieve a specific workflow_settings by its unique identifier")
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(WorkflowSettingsRequest request) {
        
        WorkflowSettingsResponse workflow_settings = workflow_settingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workflow_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create workflow_settings")
    @Operation(summary = "Create WorkflowSettings", description = "Create a new workflow_settings")
    @Override
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody WorkflowSettingsRequest request) {
        
        WorkflowSettingsResponse workflow_settings = workflow_settingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workflow_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update workflow_settings")
    @Operation(summary = "Update WorkflowSettings", description = "Update an existing workflow_settings")
    @Override
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody WorkflowSettingsRequest request) {
        
        WorkflowSettingsResponse workflow_settings = workflow_settingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workflow_settings));
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete workflow_settings")
    @Operation(summary = "Delete WorkflowSettings", description = "Delete a workflow_settings")
    @Override
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody WorkflowSettingsRequest request) {
        
        workflow_settingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_WORKFLOW_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export workflow_settings")
    @Operation(summary = "Export WorkflowSettingss", description = "Export workflow_settingss to Excel format")
    @Override
    @PreAuthorize(WorkflowSettingsPermissions.HAS_WORKFLOW_SETTINGS_EXPORT)
    @GetMapping("/export")
    public Object export(WorkflowSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            workflow_settingsRestService,
            WorkflowSettingsExcel.class,
            "WorkflowSettings",
            "workflow_settings"
        );
    }

    
    
}