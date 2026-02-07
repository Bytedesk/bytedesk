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
package com.bytedesk.service.routing_pool;

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
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/routing/pool")
@AllArgsConstructor
@Tag(name = "RoutingPool Management", description = "RoutingPool management APIs for organizing and categorizing content with routing_pools")
@Description("RoutingPool Management Controller - Content routing_poolging and categorization APIs")
public class RoutingPoolRestController extends BaseRestController<RoutingPoolRequest, RoutingPoolRestService> {

    private final RoutingPoolRestService routingPoolRestService;

    @ActionAnnotation(title = "RoutingPool", action = "组织查询", description = "query routing_pool by org")
    @Operation(summary = "Query RoutingPools by Organization", description = "Retrieve routing_pools for the current organization")
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(RoutingPoolRequest request) {
        
        Page<RoutingPoolResponse> routing_pools = routingPoolRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(routing_pools));
    }

    @ActionAnnotation(title = "RoutingPool", action = "用户查询", description = "query routing_pool by user")
    @Operation(summary = "Query RoutingPools by User", description = "Retrieve routing_pools for the current user")
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_READ)
    @Override
    public ResponseEntity<?> queryByUser(RoutingPoolRequest request) {
        
        Page<RoutingPoolResponse> routing_pools = routingPoolRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(routing_pools));
    }

    @ActionAnnotation(title = "RoutingPool", action = "查询详情", description = "query routing_pool by uid")
    @Operation(summary = "Query RoutingPool by UID", description = "Retrieve a specific routing_pool by its unique identifier")
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_READ)
    @Override
    public ResponseEntity<?> queryByUid(RoutingPoolRequest request) {
        
        RoutingPoolResponse routing_pool = routingPoolRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(routing_pool));
    }

    @ActionAnnotation(title = "RoutingPool", action = "新建", description = "create routing_pool")
    @Operation(summary = "Create RoutingPool", description = "Create a new routing_pool")
    @Override
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_CREATE)
    public ResponseEntity<?> create(RoutingPoolRequest request) {
        
        RoutingPoolResponse routing_pool = routingPoolRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(routing_pool));
    }

    @ActionAnnotation(title = "RoutingPool", action = "更新", description = "update routing_pool")
    @Operation(summary = "Update RoutingPool", description = "Update an existing routing_pool")
    @Override
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_UPDATE)
    public ResponseEntity<?> update(RoutingPoolRequest request) {
        
        RoutingPoolResponse routing_pool = routingPoolRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(routing_pool));
    }

    @ActionAnnotation(title = "RoutingPool", action = "删除", description = "delete routing_pool")
    @Operation(summary = "Delete RoutingPool", description = "Delete a routing_pool")
    @Override
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_DELETE)
    public ResponseEntity<?> delete(RoutingPoolRequest request) {
        
        routingPoolRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "RoutingPool", action = "导出", description = "export routing_pool")
    @Operation(summary = "Export RoutingPools", description = "Export routing_pools to Excel format")
    @Override
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_EXPORT)
    @GetMapping("/export")
    public Object export(RoutingPoolRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            routingPoolRestService,
            RoutingPoolExcel.class,
            "RoutingPool",
            "routing_pool"
        );
    }

    @ActionAnnotation(title = "RoutingPool", action = "accept", description = "accept manual routing pool thread")
    @Operation(summary = "Accept RoutingPool Thread", description = "Claim a manual routing pool item and accept the related thread")
    @PreAuthorize(RoutingPoolPermissions.HAS_ROUTING_POOL_READ)
    @PostMapping("/accept")
    public ResponseEntity<?> accept(@RequestBody RoutingPoolRequest request) {
        ThreadResponseSimple accepted = routingPoolRestService.acceptManualThread(request);
        return ResponseEntity.ok(JsonResult.success(accepted));
    }

    
    
}