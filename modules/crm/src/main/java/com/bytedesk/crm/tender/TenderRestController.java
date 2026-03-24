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
@RequestMapping("/api/v1/tender")
@AllArgsConstructor
@Tag(name = "Tender Management", description = "Tender management APIs for organizing and categorizing content with tenders")
@Description("Tender Management Controller - Content tenderging and categorization APIs")
public class TenderRestController extends BaseRestController<TenderRequest, TenderRestService> {

    private final TenderRestService tenderRestService;

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tender by org")
    @Operation(summary = "Query Tenders by Organization", description = "Retrieve tenders for the current organization")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(TenderRequest request) {
        
        Page<TenderResponse> tenders = tenderRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tenders));
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tender by user")
    @Operation(summary = "Query Tenders by User", description = "Retrieve tenders for the current user")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(TenderRequest request) {
        
        Page<TenderResponse> tenders = tenderRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tenders));
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tender by uid")
    @Operation(summary = "Query Tender by UID", description = "Retrieve a specific tender by its unique identifier")
    @PreAuthorize(TenderPermissions.HAS_TENDER_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TenderRequest request) {
        
        TenderResponse tender = tenderRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_CREATE, description = "create tender")
    @Operation(summary = "Create Tender", description = "Create a new tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TenderRequest request) {
        
        TenderResponse tender = tenderRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_UPDATE, description = "update tender")
    @Operation(summary = "Update Tender", description = "Update an existing tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TenderRequest request) {
        
        TenderResponse tender = tenderRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tender));
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_DELETE, description = "delete tender")
    @Operation(summary = "Delete Tender", description = "Delete a tender")
    @Override
    @PreAuthorize(TenderPermissions.HAS_TENDER_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TenderRequest request) {
        
        tenderRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TENDER, action = I18Consts.I18N_ACTION_EXPORT, description = "export tender")
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
            "招标",
            "tender"
        );
    }

    
    
}