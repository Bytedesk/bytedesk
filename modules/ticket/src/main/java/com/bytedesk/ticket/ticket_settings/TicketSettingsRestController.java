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
package com.bytedesk.ticket.ticket_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/v1/ticket/settings")
@AllArgsConstructor
@Tag(name = "TicketSettings Management", description = "TicketSettings management APIs for organizing and categorizing content with ticketSettings")
@Description("TicketSettings Management Controller - Content ticket settings and categorization APIs")
public class TicketSettingsRestController extends BaseRestController<TicketSettingsRequest, TicketSettingsRestService> {

    private final TicketSettingsRestService ticketSettingsRestService;

    @ActionAnnotation(title = "Ticket Settings", action = "组织查询", description = "query ticketSettings by org")
    @Operation(summary = "Query TicketSettings by Organization", description = "Retrieve ticket settings for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(TicketSettingsRequest request) {
        
        Page<TicketSettingsResponse> ticketSettings = ticketSettingsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "用户查询", description = "query ticketSettings by user")
    @Operation(summary = "Query ticketSettings by User", description = "Retrieve ticketSettings for the current user")
    @Override
    public ResponseEntity<?> queryByUser(TicketSettingsRequest request) {
        
        Page<TicketSettingsResponse> ticketSettings = ticketSettingsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "查询详情", description = "query ticketSettings by uid")
    @Operation(summary = "Query TicketSettings by UID", description = "Retrieve a specific ticketSettings by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "新建", description = "create ticketSettings")
    @Operation(summary = "Create TicketSettings", description = "Create a new ticketSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "更新", description = "update ticketSettings")
    @Operation(summary = "Update TicketSettings", description = "Update an existing ticketSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TicketSettingsRequest request) {
        
        TicketSettingsResponse ticketSettings = ticketSettingsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticketSettings));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "删除", description = "delete ticketSettings")
    @Operation(summary = "Delete TicketSettings", description = "Delete a ticketSettings")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TicketSettingsRequest request) {
        
        ticketSettingsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Ticket Settings", action = "导出", description = "export ticketSettings")
    @Operation(summary = "Export ticketSettings", description = "Export ticketSettings to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(TicketSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ticketSettingsRestService,
            TicketSettingsExcel.class,
            "Ticket Settings",
            "ticketSettings"
        );
    }

    @ActionAnnotation(title = "Ticket Settings", action = "按工作组查询", description = "通过 orgUid+workgroupUid 获取工单设置，若不存在返回默认模板")
    @Operation(summary = "Get TicketSettings by workgroup", description = "Get settings by orgUid and workgroupUid; returns defaults if missing")
    @GetMapping("/by-workgroup")
    public ResponseEntity<?> getByWorkgroup(TicketSettingsRequest request) {
        if (request == null || request.getOrgUid() == null || request.getWorkgroupUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("orgUid and workgroupUid are required"));
        }
        TicketSettingsResponse resp = ticketSettingsRestService.getOrDefaultByWorkgroup(request.getOrgUid(), request.getWorkgroupUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "按工作组保存", description = "保存或更新指定工作组的工单设置")
    @Operation(summary = "Save TicketSettings by workgroup", description = "Upsert settings by orgUid+workgroupUid")
    @PostMapping("/by-workgroup")
    public ResponseEntity<?> saveByWorkgroup(@RequestBody TicketSettingsRequest request) {
        if (request == null || request.getOrgUid() == null || request.getWorkgroupUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("orgUid and workgroupUid are required"));
        }
        TicketSettingsResponse resp = ticketSettingsRestService.saveByWorkgroup(request);
        return ResponseEntity.ok(JsonResult.success(resp));
    }

    @ActionAnnotation(title = "Ticket Settings", action = "发布", description = "发布当前工作组的工单草稿配置")
    @Operation(summary = "Publish TicketSettings", description = "Publish draft settings to active for given TicketSettings uid")
    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody TicketSettingsRequest request) {
        if (request == null || request.getUid() == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("uid is required"));
        }
        TicketSettingsResponse resp = ticketSettingsRestService.publish(request.getUid());
        return ResponseEntity.ok(JsonResult.success(resp));
    }
    
    
}