/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:25:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.moment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/moment")
@AllArgsConstructor
@Tag(name = "Moment Management", description = "Moment management APIs for organizing and categorizing content with moments")
public class MomentRestController extends BaseRestController<MomentRequest, MomentRestService> {

    private final MomentRestService momentRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query moment by org")
    @Operation(summary = "Query Moments by Organization", description = "Retrieve moments for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MomentRequest request) {
        
        Page<MomentResponse> moments = momentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(moments));
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query moment by user")
    @Operation(summary = "Query Moments by User", description = "Retrieve moments for the current user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(MomentRequest request) {
        
        Page<MomentResponse> moments = momentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(moments));
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query moment by uid")
    @Operation(summary = "Query Moment by UID", description = "Retrieve a specific moment by its unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(MomentRequest request) {
        
        MomentResponse moment = momentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_CREATE, description = "create moment")
    @Operation(summary = "Create Moment", description = "Create a new moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MomentRequest request) {
        
        MomentResponse moment = momentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update moment")
    @Operation(summary = "Update Moment", description = "Update an existing moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MomentRequest request) {
        
        MomentResponse moment = momentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(moment));
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete moment")
    @Operation(summary = "Delete Moment", description = "Delete a moment")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MomentRequest request) {
        
        momentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_MOMENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export moment")
    @Operation(summary = "Export Moments", description = "Export moments to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(MomentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            momentRestService,
            MomentExcel.class,
            "Moment",
            "moment"
        );
    }

    
    
}