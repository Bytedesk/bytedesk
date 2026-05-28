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
package com.bytedesk.core.report;

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
@RequestMapping("/api/v1/report")
@AllArgsConstructor
@Tag(name = "Report Management", description = "Report management APIs for organizing and categorizing content with reports")
@Description("Report Management Controller - Content reportging and categorization APIs")
public class ReportRestController extends BaseRestController<ReportRequest, ReportRestService> {

    private final ReportRestService reportRestService;

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query report by org")
    @Operation(summary = "Query Reports by Organization", description = "Retrieve reports for the current organization")
    @PreAuthorize(ReportPermissions.HAS_REPORT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ReportRequest request) {
        
        Page<ReportResponse> reports = reportRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(reports));
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query report by user")
    @Operation(summary = "Query Reports by User", description = "Retrieve reports for the current user")
    @PreAuthorize(ReportPermissions.HAS_REPORT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ReportRequest request) {
        
        Page<ReportResponse> reports = reportRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(reports));
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query report by uid")
    @Operation(summary = "Query Report by UID", description = "Retrieve a specific report by its unique identifier")
    @PreAuthorize(ReportPermissions.HAS_REPORT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ReportRequest request) {
        
        ReportResponse report = reportRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(report));
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_CREATE, description = "create report")
    @Operation(summary = "Create Report", description = "Create a new report")
    @Override
    @PreAuthorize(ReportPermissions.HAS_REPORT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ReportRequest request) {
        
        ReportResponse report = reportRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(report));
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_UPDATE, description = "update report")
    @Operation(summary = "Update Report", description = "Update an existing report")
    @Override
    @PreAuthorize(ReportPermissions.HAS_REPORT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ReportRequest request) {
        
        ReportResponse report = reportRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(report));
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_DELETE, description = "delete report")
    @Operation(summary = "Delete Report", description = "Delete a report")
    @Override
    @PreAuthorize(ReportPermissions.HAS_REPORT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ReportRequest request) {
        
        reportRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_REPORT, action = I18Consts.I18N_ACTION_EXPORT, description = "export report")
    @Operation(summary = "Export Reports", description = "Export reports to Excel format")
    @Override
    @PreAuthorize(ReportPermissions.HAS_REPORT_EXPORT)
    @GetMapping("/export")
    public Object export(ReportRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            reportRestService,
            ReportExcel.class,
            "Report",
            "report"
        );
    }

    
    
}