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
package com.bytedesk.service.workgroup_routing;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/workgroup/routing")
@AllArgsConstructor
@Tag(name = "WorkgroupRouting Management", description = "WorkgroupRouting management APIs for organizing and categorizing content with workgroup_routings")
@Description("WorkgroupRouting Management Controller - Content workgroup_routingging and categorization APIs")
public class WorkgroupRoutingRestController extends BaseRestController<WorkgroupRoutingRequest, WorkgroupRoutingRestService> {

    private final WorkgroupRoutingRestService agentNextRestService;

    private final WorkgroupRestService workgroupRestService;

    private final WorkgroupRoutingService workgroupRoutingService;

    @ActionAnnotation(title = "WorkgroupRouting", action = "组织查询", description = "query workgroup_routing by org")
    @Operation(summary = "Query WorkgroupRoutings by Organization", description = "Retrieve workgroup_routings for the current organization")
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_READ)
    @Override
    public ResponseEntity<?> queryByOrg(WorkgroupRoutingRequest request) {
        
        Page<WorkgroupRoutingResponse> workgroup_routings = agentNextRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(workgroup_routings));
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "用户查询", description = "query workgroup_routing by user")
    @Operation(summary = "Query WorkgroupRoutings by User", description = "Retrieve workgroup_routings for the current user")
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_READ)
    @Override
    public ResponseEntity<?> queryByUser(WorkgroupRoutingRequest request) {
        
        Page<WorkgroupRoutingResponse> workgroup_routings = agentNextRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(workgroup_routings));
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "查询详情", description = "query workgroup_routing by uid")
    @Operation(summary = "Query WorkgroupRouting by UID", description = "Retrieve a specific workgroup_routing by its unique identifier")
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_READ)
    @Override
    public ResponseEntity<?> queryByUid(WorkgroupRoutingRequest request) {
        
        WorkgroupRoutingResponse workgroup_routing = agentNextRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(workgroup_routing));
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "新建", description = "create workgroup_routing")
    @Operation(summary = "Create WorkgroupRouting", description = "Create a new workgroup_routing")
    @Override
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_CREATE)
    public ResponseEntity<?> create(WorkgroupRoutingRequest request) {
        
        WorkgroupRoutingResponse workgroup_routing = agentNextRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(workgroup_routing));
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "更新", description = "update workgroup_routing")
    @Operation(summary = "Update WorkgroupRouting", description = "Update an existing workgroup_routing")
    @Override
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_UPDATE)
    public ResponseEntity<?> update(WorkgroupRoutingRequest request) {
        
        WorkgroupRoutingResponse workgroup_routing = agentNextRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(workgroup_routing));
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "删除", description = "delete workgroup_routing")
    @Operation(summary = "Delete WorkgroupRouting", description = "Delete a workgroup_routing")
    @Override
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_DELETE)
    public ResponseEntity<?> delete(WorkgroupRoutingRequest request) {
        
        agentNextRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "WorkgroupRouting", action = "导出", description = "export workgroup_routing")
    @Operation(summary = "Export WorkgroupRoutings", description = "Export workgroup_routings to Excel format")
    @Override
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_EXPORT)
    @GetMapping("/export")
    public Object export(WorkgroupRoutingRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            agentNextRestService,
            WorkgroupRoutingExcel.class,
            "WorkgroupRouting",
            "workgroup_routing"
        );
    }

    @GetMapping("/state")
    @Operation(summary = "Get Workgroup Routing State", description = "Get current routing mode, available agents and precomputed next agent for a workgroup")
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_READ)
    public ResponseEntity<?> state(@RequestParam("workgroupUid") String workgroupUid) {
        WorkgroupEntity workgroup = workgroupRestService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + workgroupUid));
        WorkgroupRoutingStateResponse state = workgroupRoutingService.getRoutingState(workgroup);
        return ResponseEntity.ok(JsonResult.success(state));
    }

    @PostMapping("/state/refresh")
    @Operation(summary = "Refresh Workgroup Routing State", description = "Manually recompute and persist routing state (nextAgent) for a workgroup, then return the latest state")
    @PreAuthorize(WorkgroupRoutingPermissions.HAS_WORKGROUP_ROUTING_READ)
    public ResponseEntity<?> refreshState(@RequestParam("workgroupUid") String workgroupUid) {
        WorkgroupEntity workgroup = workgroupRestService.findByUid(workgroupUid)
                .orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + workgroupUid));
        // 同步刷新：用于前端手动触发立即生效
        workgroupRoutingService.refreshRoutingState(workgroup);
        WorkgroupRoutingStateResponse state = workgroupRoutingService.getRoutingState(workgroup);
        return ResponseEntity.ok(JsonResult.success(state));
    }
    
    
}