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
package com.bytedesk.crm.tender;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/tender")
@AllArgsConstructor
@Tag(name = "Tender Management", description = "Tender management APIs for organizing and categorizing content with tenders")
@Description("Tender Management Controller - Content tenderging and categorization APIs")
public class TenderRestController extends BaseRestController<TenderRequest, TenderRestService> {

    private final TenderRestService tenderRestService;

    @ActionAnnotation(title = "Tender", action = "组织查询", description = "query tender by org")
    @Operation(summary = "Query Tenders by Organization", description = "Retrieve tenders for the current organization")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    public ResponseEntity<?> queryByOrg(TenderRequest request) {
        
        Page<TenderResponse> tenders = tenderRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tenders));
    }

    @ActionAnnotation(title = "Tender", action = "用户查询", description = "query tender by user")
    @Operation(summary = "Query Tenders by User", description = "Retrieve tenders for the current user")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    public ResponseEntity<?> queryByUser(TenderRequest request) {
        
        Page<TenderResponse> tenders = tenderRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tenders));
    }

    @ActionAnnotation(title = "Tender", action = "查询详情", description = "query tender by uid")
    @Operation(summary = "Query Tender by UID", description = "Retrieve a specific tender by its unique identifier")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    public ResponseEntity<?> queryByUid(TenderRequest request) {
        
        TenderResponse tender = tenderRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = "Tender", action = "新建", description = "create tender")
    @Operation(summary = "Create Tender", description = "Create a new tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_CREATE)
    public ResponseEntity<?> create(TenderRequest request) {
        
        TenderResponse tender = tenderRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = "Tender", action = "更新", description = "update tender")
    @Operation(summary = "Update Tender", description = "Update an existing tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_UPDATE)
    public ResponseEntity<?> update(TenderRequest request) {
        
        TenderResponse tender = tenderRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = "Tender", action = "删除", description = "delete tender")
    @Operation(summary = "Delete Tender", description = "Delete a tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_DELETE)
    public ResponseEntity<?> delete(TenderRequest request) {
        
        tenderRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Tender", action = "导出", description = "export tender")
    @Operation(summary = "Export Tenders", description = "Export tenders to Excel format")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_EXPORT)
    @GetMapping("/export")
    public Object export(TenderRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tenderRestService,
            TenderExcel.class,
            "Tender",
            "tender"
        );
    }

    
    
}