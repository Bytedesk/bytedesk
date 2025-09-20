/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:02:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.service_settings;

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
@RequestMapping("/api/v1/service/settings")
@AllArgsConstructor
@Tag(name = "ServiceSettings Management", description = "ServiceSettings management APIs for organizing and categorizing content with serviceSettings")
@Description("ServiceSettings Management Controller - Content service settings and categorization APIs")
public class ServiceSettingsRestController extends BaseRestController<ServiceSettingsRequest, ServiceSettingsRestService> {

    private final ServiceSettingsRestService serviceSettingsRestService;

    @ActionAnnotation(title = "Service Settings", action = "组织查询", description = "query serviceSettings by org")
    @Operation(summary = "Query ServiceSettings by Organization", description = "Retrieve service settings for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @ActionAnnotation(title = "Service Settings", action = "用户查询", description = "query serviceSettings by user")
    @Operation(summary = "Query serviceSettings by User", description = "Retrieve serviceSettings for the current user")
    @Override
    public ResponseEntity<?> queryByUser(ServiceSettingsRequest request) {
        
        Page<ServiceSettingsResponse> serviceSettings = serviceSettingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @ActionAnnotation(title = "Service Settings", action = "查询详情", description = "query serviceSettings by uid")
    @Operation(summary = "Query ServiceSettings by UID", description = "Retrieve a specific serviceSettings by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSettings = serviceSettingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @ActionAnnotation(title = "Service Settings", action = "新建", description = "create serviceSettings")
    @Operation(summary = "Create ServiceSettings", description = "Create a new serviceSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSettings = serviceSettingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @ActionAnnotation(title = "Service Settings", action = "更新", description = "update serviceSettings")
    @Operation(summary = "Update ServiceSettings", description = "Update an existing serviceSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ServiceSettingsRequest request) {
        
        ServiceSettingsResponse serviceSettings = serviceSettingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(serviceSettings));
    }

    @ActionAnnotation(title = "Service Settings", action = "删除", description = "delete serviceSettings")
    @Operation(summary = "Delete ServiceSettings", description = "Delete a serviceSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ServiceSettingsRequest request) {
        
        serviceSettingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Service Settings", action = "导出", description = "export serviceSettings")
    @Operation(summary = "Export serviceSettings", description = "Export serviceSettings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(ServiceSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            serviceSettingsRestService,
            ServiceSettingsExcel.class,
            "Service Settings",
            "serviceSettings"
        );
    }

    
    
}