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
package com.bytedesk.service.agent_seat;

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
@RequestMapping("/api/v1/agent/seat")
@AllArgsConstructor
@Tag(name = "AgentSeat Management", description = "AgentSeat management APIs for organizing and categorizing content with agent_seats")
@Description("AgentSeat Management Controller - Content agent_seatging and categorization APIs")
public class AgentSeatRestController extends BaseRestController<AgentSeatRequest, AgentSeatRestService> {

    private final AgentSeatRestService agentSeatRestService;

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query agent_seat by org")
    @Operation(summary = "Query AgentSeats by Organization", description = "Retrieve agent_seats for the current organization")
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(AgentSeatRequest request) {
        
        Page<AgentSeatResponse> agent_seats = agentSeatRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(agent_seats));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query agent_seat by user")
    @Operation(summary = "Query AgentSeats by User", description = "Retrieve agent_seats for the current user")
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(AgentSeatRequest request) {
        
        Page<AgentSeatResponse> agent_seats = agentSeatRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(agent_seats));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query agent_seat by uid")
    @Operation(summary = "Query AgentSeat by UID", description = "Retrieve a specific agent_seat by its unique identifier")
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(AgentSeatRequest request) {
        
        AgentSeatResponse agent_seat = agentSeatRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(agent_seat));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_CREATE, description = "create agent_seat")
    @Operation(summary = "Create AgentSeat", description = "Create a new agent_seat")
    @Override
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AgentSeatRequest request) {
        
        AgentSeatResponse agent_seat = agentSeatRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(agent_seat));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_UPDATE, description = "update agent_seat")
    @Operation(summary = "Update AgentSeat", description = "Update an existing agent_seat")
    @Override
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AgentSeatRequest request) {
        
        AgentSeatResponse agent_seat = agentSeatRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(agent_seat));
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_DELETE, description = "delete agent_seat")
    @Operation(summary = "Delete AgentSeat", description = "Delete a agent_seat")
    @Override
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AgentSeatRequest request) {
        
        agentSeatRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_AGENT_SEAT, action = I18Consts.I18N_ACTION_EXPORT, description = "export agent_seat")
    @Operation(summary = "Export AgentSeats", description = "Export agent_seats to Excel format")
    @Override
    @PreAuthorize(AgentSeatPermissions.HAS_AGENT_SEAT_EXPORT)
    @GetMapping("/export")
    public Object export(AgentSeatRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentSeatRestService,
            AgentSeatExcel.class,
            "AgentSeat",
            "agent_seat"
        );
    }

    
    
}