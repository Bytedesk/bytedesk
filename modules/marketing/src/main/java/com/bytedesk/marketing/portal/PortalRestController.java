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
package com.bytedesk.marketing.portal;

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
import com.bytedesk.core.annotation.Idempotent;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/portal")
@AllArgsConstructor
@Tag(name = "Portal Management", description = "Portal management APIs for organizing and categorizing content with portals")
@Description("Portal Management Controller - Content portalging and categorization APIs")
public class PortalRestController extends BaseRestController<PortalRequest, PortalRestService> {

    private final PortalRestService portalRestService;

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query portal by org")
    @Operation(summary = "Query Portals by Organization", description = "Retrieve portals for the current organization")
    @PreAuthorize(PortalPermissions.HAS_PORTAL_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(PortalRequest request) {
        
        Page<PortalResponse> portals = portalRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(portals));
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query portal by user")
    @Operation(summary = "Query Portals by User", description = "Retrieve portals for the current user")
    @PreAuthorize(PortalPermissions.HAS_PORTAL_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(PortalRequest request) {
        
        Page<PortalResponse> portals = portalRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(portals));
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query portal by uid")
    @Operation(summary = "Query Portal by UID", description = "Retrieve a specific portal by its unique identifier")
    @PreAuthorize(PortalPermissions.HAS_PORTAL_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(PortalRequest request) {
        
        PortalResponse portal = portalRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(portal));
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_CREATE, description = "create portal")
    @Operation(summary = "Create Portal", description = "Create a new portal")
    @Override
    @PreAuthorize(PortalPermissions.HAS_PORTAL_CREATE)
    @Idempotent(ttlSeconds = 120)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PortalRequest request) {
        
        PortalResponse portal = portalRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(portal));
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_UPDATE, description = "update portal")
    @Operation(summary = "Update Portal", description = "Update an existing portal")
    @Override
    @PreAuthorize(PortalPermissions.HAS_PORTAL_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PortalRequest request) {
        
        PortalResponse portal = portalRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(portal));
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_DELETE, description = "delete portal")
    @Operation(summary = "Delete Portal", description = "Delete a portal")
    @Override
    @PreAuthorize(PortalPermissions.HAS_PORTAL_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody PortalRequest request) {
        
        portalRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PORTAL, action = I18Consts.I18N_ACTION_EXPORT, description = "export portal")
    @Operation(summary = "Export Portals", description = "Export portals to Excel format")
    @Override
    @PreAuthorize(PortalPermissions.HAS_PORTAL_EXPORT)
    @GetMapping("/export")
    public Object export(PortalRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            portalRestService,
            PortalExcel.class,
            "Portal",
            "portal"
        );
    }
    
}