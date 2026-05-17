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
package com.bytedesk.core.push.apns_push;

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
@RequestMapping("/api/v1/apns_push")
@AllArgsConstructor
@Tag(name = "ApnsPush Management", description = "ApnsPush management APIs for organizing and categorizing content with apns_pushs")
@Description("ApnsPush Management Controller - Content apns_pushging and categorization APIs")
public class ApnsPushRestController extends BaseRestController<ApnsPushRequest, ApnsPushRestService> {

    private final ApnsPushRestService apnsPushRestService;

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query apns_push by org")
    @Operation(summary = "Query ApnsPushs by Organization", description = "Retrieve apns_pushs for the current organization")
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ApnsPushRequest request) {
        
        Page<ApnsPushResponse> apns_pushs = apnsPushRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apns_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query apns_push by user")
    @Operation(summary = "Query ApnsPushs by User", description = "Retrieve apns_pushs for the current user")
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ApnsPushRequest request) {
        
        Page<ApnsPushResponse> apns_pushs = apnsPushRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apns_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query apns_push by uid")
    @Operation(summary = "Query ApnsPush by UID", description = "Retrieve a specific apns_push by its unique identifier")
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ApnsPushRequest request) {
        
        ApnsPushResponse apns_push = apnsPushRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(apns_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_CREATE, description = "create apns_push")
    @Operation(summary = "Create ApnsPush", description = "Create a new apns_push")
    @Override
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ApnsPushRequest request) {
        
        ApnsPushResponse apns_push = apnsPushRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(apns_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_UPDATE, description = "update apns_push")
    @Operation(summary = "Update ApnsPush", description = "Update an existing apns_push")
    @Override
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ApnsPushRequest request) {
        
        ApnsPushResponse apns_push = apnsPushRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(apns_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_DELETE, description = "delete apns_push")
    @Operation(summary = "Delete ApnsPush", description = "Delete a apns_push")
    @Override
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ApnsPushRequest request) {
        
        apnsPushRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_PUSH, action = I18Consts.I18N_ACTION_EXPORT, description = "export apns_push")
    @Operation(summary = "Export ApnsPushs", description = "Export apns_pushs to Excel format")
    @Override
    @PreAuthorize(ApnsPushPermissions.HAS_APNS_PUSH_EXPORT)
    @GetMapping("/export")
    public Object export(ApnsPushRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            apnsPushRestService,
            ApnsPushExcel.class,
            "ApnsPush",
            "apns_push"
        );
    }

    
    
}