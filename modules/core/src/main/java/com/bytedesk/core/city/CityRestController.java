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
package com.bytedesk.core.city;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api/v1/city")
@AllArgsConstructor
@Tag(name = "City Management", description = "City management APIs for organizing and categorizing content with citys")
@Description("City Management Controller - Content cityging and categorization APIs")
public class CityRestController extends BaseRestController<CityRequest, CityRestService> {

    private final CityRestService cityRestService;

    @ActionAnnotation(title = "City", action = "组织查询", description = "query city by org")
    @Operation(summary = "Query Citys by Organization", description = "Retrieve citys for the current organization")
    @PreAuthorize(CityPermissions.HAS_CITY_READ)
    @Override
    public ResponseEntity<?> queryByOrg(CityRequest request) {
        
        Page<CityResponse> citys = cityRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(citys));
    }

    @ActionAnnotation(title = "City", action = "用户查询", description = "query city by user")
    @Operation(summary = "Query Citys by User", description = "Retrieve citys for the current user")
    @PreAuthorize(CityPermissions.HAS_CITY_READ)
    @Override
    public ResponseEntity<?> queryByUser(CityRequest request) {
        
        Page<CityResponse> citys = cityRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(citys));
    }

    @ActionAnnotation(title = "City", action = "查询详情", description = "query city by uid")
    @Operation(summary = "Query City by UID", description = "Retrieve a specific city by its unique identifier")
    @PreAuthorize(CityPermissions.HAS_CITY_READ)
    @Override
    public ResponseEntity<?> queryByUid(CityRequest request) {
        
        CityResponse city = cityRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(city));
    }

    @ActionAnnotation(title = "City", action = "新建", description = "create city")
    @Operation(summary = "Create City", description = "Create a new city")
    @Override
    @PreAuthorize(CityPermissions.HAS_CITY_CREATE)
    public ResponseEntity<?> create(CityRequest request) {
        
        CityResponse city = cityRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(city));
    }

    @ActionAnnotation(title = "City", action = "更新", description = "update city")
    @Operation(summary = "Update City", description = "Update an existing city")
    @Override
    @PreAuthorize(CityPermissions.HAS_CITY_UPDATE)
    public ResponseEntity<?> update(CityRequest request) {
        
        CityResponse city = cityRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(city));
    }

    @ActionAnnotation(title = "City", action = "删除", description = "delete city")
    @Operation(summary = "Delete City", description = "Delete a city")
    @Override
    @PreAuthorize(CityPermissions.HAS_CITY_DELETE)
    public ResponseEntity<?> delete(CityRequest request) {
        
        cityRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "City", action = "导出", description = "export city")
    @Operation(summary = "Export Citys", description = "Export citys to Excel format")
    @Override
    @PreAuthorize(CityPermissions.HAS_CITY_EXPORT)
    @GetMapping("/export")
    public Object export(CityRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            cityRestService,
            CityExcel.class,
            "City",
            "city"
        );
    }

    @ActionAnnotation(title = "City", action = "一键重置", description = "reset all cities")
    @Operation(summary = "Reset Citys", description = "Delete all city records (admin only).")
    @PreAuthorize(CityPermissions.HAS_CITY_DELETE)
    @PostMapping("/reset")
    public ResponseEntity<?> reset(CityRequest request) {
        long deletedCount = cityRestService.resetAll();
        return ResponseEntity.ok(JsonResult.success(I18Consts.I18N_CITY_RESET_SUCCESS, deletedCount));
    }

    @ActionAnnotation(title = "City", action = "一键初始化", description = "init cities from sql")
    @Operation(summary = "Init Citys", description = "Initialize city data from SQL script in background.")
    @PreAuthorize(CityPermissions.HAS_CITY_CREATE)
    @PostMapping("/init")
    public ResponseEntity<?> init(CityRequest request) {
        boolean started = cityRestService.initIfEmptyAsync();
        if (!started) {
            return ResponseEntity.ok(JsonResult.success(I18Consts.I18N_CITY_INIT_SKIPPED, false));
        }
        return ResponseEntity.ok(JsonResult.success(I18Consts.I18N_CITY_INIT_SCHEDULED, true));
    }

    
    
}