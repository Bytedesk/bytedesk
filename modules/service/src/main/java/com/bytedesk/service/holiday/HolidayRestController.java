/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-25 11:09:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Holiday Management", description = "Holiday management APIs")
@RestController
@RequestMapping("/api/v1/holiday")
@AllArgsConstructor
public class HolidayRestController extends BaseRestController<HolidayRequest, HolidayRestService> {

    private final HolidayRestService holidayService;

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_READ)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg holiday")
    @Operation(summary = "Query Holidays by Organization", description = "Retrieve holiday list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = HolidayResponse.class)))
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(HolidayRequest request) {
        
        Page<HolidayResponse> holidays = holidayService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(holidays));
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_READ)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser holiday")
    @Operation(summary = "Query Holidays by User", description = "Retrieve holiday list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = HolidayResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(HolidayRequest request) {
        
        Page<HolidayResponse> holidays = holidayService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(holidays));
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_CREATE, description = "create holiday")
    @Operation(summary = "Create Holiday", description = "Create a new holiday")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = HolidayResponse.class)))
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody HolidayRequest request) {
        
        HolidayResponse holiday = holidayService.create(request);

        return ResponseEntity.ok(JsonResult.success(holiday));
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_UPDATE, description = "update holiday")
    @Operation(summary = "Update Holiday", description = "Update holiday information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = HolidayResponse.class)))
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody HolidayRequest request) {
        
        HolidayResponse holiday = holidayService.update(request);

        return ResponseEntity.ok(JsonResult.success(holiday));
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_DELETE, description = "delete holiday")
    @Operation(summary = "Delete Holiday", description = "Delete the specified holiday")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody HolidayRequest request) {
        
        holidayService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_INIT, description = "init holiday defaults")
    @Operation(summary = "Initialize Holiday Defaults", description = "Initialize default holiday data for the current organization")
    @ApiResponse(responseCode = "200", description = "Initialized successfully")
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody HolidayRequest request) {
        
        holidayService.initOrganizationHolidays(request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success());
    }

    @GetMapping("/export")
    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_EXPORT, description = "export holiday")
    @Operation(summary = "Export Holidays", description = "Export holiday data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @Override
    public Object export(HolidayRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            holidayService,
            HolidayExcel.class,
            "节假日",
            "holiday"
        );
    }

    @PreAuthorize(HolidayPermissions.HAS_HOLIDAY_READ)
    @ActionAnnotation(title = I18Consts.I18N_HOLIDAY, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid holiday")
    @Operation(summary = "Query Holiday by UID", description = "Retrieve holiday details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = HolidayResponse.class)))
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(HolidayRequest request) {
        
        HolidayResponse holiday = holidayService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(holiday));
    }
    
}