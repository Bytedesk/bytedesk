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
package com.bytedesk.call.gateway;

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
@RequestMapping("/api/v1/gateway")
@AllArgsConstructor
@Tag(name = "Gateway Management", description = "Gateway management APIs for organizing and categorizing content with gateways")
@Description("Gateway Management Controller - Content gatewayging and categorization APIs")
public class GatewayRestController extends BaseRestController<GatewayRequest, GatewayRestService> {

    private final GatewayRestService gatewayRestService;

    @ActionAnnotation(title = "Gateway", action = "组织查询", description = "query gateway by org")
    @Operation(summary = "Query Gateways by Organization", description = "Retrieve gateways for the current organization")
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_READ)
    @Override
    public ResponseEntity<?> queryByOrg(GatewayRequest request) {
        
        Page<GatewayResponse> gateways = gatewayRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(gateways));
    }

    @ActionAnnotation(title = "Gateway", action = "用户查询", description = "query gateway by user")
    @Operation(summary = "Query Gateways by User", description = "Retrieve gateways for the current user")
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_READ)
    @Override
    public ResponseEntity<?> queryByUser(GatewayRequest request) {
        
        Page<GatewayResponse> gateways = gatewayRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(gateways));
    }

    @ActionAnnotation(title = "Gateway", action = "查询详情", description = "query gateway by uid")
    @Operation(summary = "Query Gateway by UID", description = "Retrieve a specific gateway by its unique identifier")
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_READ)
    @Override
    public ResponseEntity<?> queryByUid(GatewayRequest request) {
        
        GatewayResponse gateway = gatewayRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    @ActionAnnotation(title = "Gateway", action = "新建", description = "create gateway")
    @Operation(summary = "Create Gateway", description = "Create a new gateway")
    @Override
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_CREATE)
    public ResponseEntity<?> create(GatewayRequest request) {
        
        GatewayResponse gateway = gatewayRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    @ActionAnnotation(title = "Gateway", action = "更新", description = "update gateway")
    @Operation(summary = "Update Gateway", description = "Update an existing gateway")
    @Override
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_UPDATE)
    public ResponseEntity<?> update(GatewayRequest request) {
        
        GatewayResponse gateway = gatewayRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(gateway));
    }

    @ActionAnnotation(title = "Gateway", action = "删除", description = "delete gateway")
    @Operation(summary = "Delete Gateway", description = "Delete a gateway")
    @Override
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_DELETE)
    public ResponseEntity<?> delete(GatewayRequest request) {
        
        gatewayRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Gateway", action = "导出", description = "export gateway")
    @Operation(summary = "Export Gateways", description = "Export gateways to Excel format")
    @Override
    @PreAuthorize(GatewayPermissions.HAS_GATEWAY_EXPORT)
    @GetMapping("/export")
    public Object export(GatewayRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            gatewayRestService,
            GatewayExcel.class,
            "Gateway",
            "gateway"
        );
    }

    
    
}