/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 17:39:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing_queue;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "路由规则管理", description = "路由规则管理相关接口")
@RestController
@RequestMapping("/api/v1/routing/queue")
@AllArgsConstructor
public class RoutingQueueRestController extends BaseRestController<RoutingQueueRequest, RoutingQueueRestService> {

    private final RoutingQueueRestService routingRuleRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "查询组织下的路由规则", description = "根据组织ID查询路由规则列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RoutingQueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(RoutingQueueRequest request) {
        
        Page<RoutingQueueResponse> rules = routingRuleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(rules));
    }

    @Operation(summary = "查询用户下的路由规则", description = "根据用户ID查询路由规则列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RoutingQueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(RoutingQueueRequest request) {
        
        Page<RoutingQueueResponse> rules = routingRuleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(rules));
    }

    @Operation(summary = "查询指定路由规则", description = "根据UID查询路由规则详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RoutingQueueResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(RoutingQueueRequest request) {
        
        RoutingQueueResponse rule = routingRuleRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(rule));
    }

    @Operation(summary = "创建路由规则", description = "创建新的路由规则")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RoutingQueueResponse.class)))
    @Override
    // @PreAuthorize("hasAuthority('rule_CREATE')")
    public ResponseEntity<?> create(RoutingQueueRequest request) {
        
        RoutingQueueResponse rule = routingRuleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(rule));
    }

    @Operation(summary = "更新路由规则", description = "更新路由规则信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = RoutingQueueResponse.class)))
    @Override
    // @PreAuthorize("hasAuthority('rule_UPDATE')")
    public ResponseEntity<?> update(RoutingQueueRequest request) {
        
        RoutingQueueResponse rule = routingRuleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(rule));
    }

    @Operation(summary = "删除路由规则", description = "删除指定的路由规则")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    // @PreAuthorize("hasAuthority('rule_DELETE')")
    public ResponseEntity<?> delete(RoutingQueueRequest request) {
        
        routingRuleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出路由规则", description = "导出路由规则数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    // @PreAuthorize("hasAuthority('rule_EXPORT')")
    @GetMapping("/export")
    public Object export(RoutingQueueRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            routingRuleRestService,
            RoutingQueueExcel.class,
            "路由",
            "rule"
        );
    }

    
    
}