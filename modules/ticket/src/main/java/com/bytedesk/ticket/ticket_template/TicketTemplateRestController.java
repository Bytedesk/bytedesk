/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_template;

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
@RequestMapping("/api/v1/ticket/template")
@AllArgsConstructor
@Tag(name = "TicketTemplate Management", description = "TicketTemplate management APIs for organizing and categorizing content with ticketTemplates")
@Description("TicketTemplate Management Controller - Content ticketTemplate management and organization APIs for content stlateging and categorization APIs")
public class TicketTemplateRestController extends BaseRestController<TicketTemplateRequest, TicketTemplateRestService> {

    private final TicketTemplateRestService ticketTemplateRestService;

    @ActionAnnotation(title = "标签", action = "组织查询", description = "query ticketTemplate by org")
    @Operation(summary = "Query TicketTemplates by Organization", description = "Retrieve ticketTemplates for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(TicketTemplateRequest request) {
        
        Page<TicketTemplateResponse> ticketTemplates = ticketTemplateRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ticketTemplates));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query ticketTemplate by user")
    @Operation(summary = "Query TicketTemplates by User", description = "Retrieve ticketTemplates for the current user")
    @Override
    public ResponseEntity<?> queryByUser(TicketTemplateRequest request) {
        
        Page<TicketTemplateResponse> ticketTemplates = ticketTemplateRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ticketTemplates));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query ticketTemplate by uid")
    @Operation(summary = "Query TicketTemplate by UID", description = "Retrieve a specific ticketTemplate by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(TicketTemplateRequest request) {
        
        TicketTemplateResponse ticketTemplate = ticketTemplateRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ticketTemplate));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create ticketTemplate")
    @Operation(summary = "Create TicketTemplate", description = "Create a new ticketTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TicketTemplateRequest request) {
        
        TicketTemplateResponse ticketTemplate = ticketTemplateRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticketTemplate));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update ticketTemplate")
    @Operation(summary = "Update TicketTemplate", description = "Update an existing ticketTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TicketTemplateRequest request) {
        
        TicketTemplateResponse ticketTemplate = ticketTemplateRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticketTemplate));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete ticketTemplate")
    @Operation(summary = "Delete TicketTemplate", description = "Delete a ticketTemplate")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TicketTemplateRequest request) {
        
        ticketTemplateRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export ticketTemplate")
    @Operation(summary = "Export TicketTemplates", description = "Export ticketTemplates to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(TicketTemplateRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ticketTemplateRestService,
            TicketTemplateExcel.class,
            "标签",
            "ticketTemplate"
        );
    }

    
    
}