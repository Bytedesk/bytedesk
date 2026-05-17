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
package com.bytedesk.core.push.apns_p12;

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
@RequestMapping("/api/v1/apns_p12")
@AllArgsConstructor
@Tag(name = "ApnsP12 Management", description = "ApnsP12 management APIs for organizing and categorizing content with apns_p12s")
@Description("ApnsP12 Management Controller - Content apns_p12ging and categorization APIs")
public class ApnsP12RestController extends BaseRestController<ApnsP12Request, ApnsP12RestService> {

    private final ApnsP12RestService apnsP12RestService;

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query apns_p12 by org")
    @Operation(summary = "Query ApnsP12s by Organization", description = "Retrieve apns_p12s for the current organization")
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ApnsP12Request request) {
        
        Page<ApnsP12Response> apns_p12s = apnsP12RestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apns_p12s));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query apns_p12 by user")
    @Operation(summary = "Query ApnsP12s by User", description = "Retrieve apns_p12s for the current user")
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ApnsP12Request request) {
        
        Page<ApnsP12Response> apns_p12s = apnsP12RestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apns_p12s));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query apns_p12 by uid")
    @Operation(summary = "Query ApnsP12 by UID", description = "Retrieve a specific apns_p12 by its unique identifier")
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ApnsP12Request request) {
        
        ApnsP12Response apns_p12 = apnsP12RestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(apns_p12));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_CREATE, description = "create apns_p12")
    @Operation(summary = "Create ApnsP12", description = "Create a new apns_p12")
    @Override
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ApnsP12Request request) {
        
        ApnsP12Response apns_p12 = apnsP12RestService.create(request);

        return ResponseEntity.ok(JsonResult.success(apns_p12));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_UPDATE, description = "update apns_p12")
    @Operation(summary = "Update ApnsP12", description = "Update an existing apns_p12")
    @Override
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ApnsP12Request request) {
        
        ApnsP12Response apns_p12 = apnsP12RestService.update(request);

        return ResponseEntity.ok(JsonResult.success(apns_p12));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_DELETE, description = "delete apns_p12")
    @Operation(summary = "Delete ApnsP12", description = "Delete a apns_p12")
    @Override
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ApnsP12Request request) {
        
        apnsP12RestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_P12, action = I18Consts.I18N_ACTION_EXPORT, description = "export apns_p12")
    @Operation(summary = "Export ApnsP12s", description = "Export apns_p12s to Excel format")
    @Override
    @PreAuthorize(ApnsP12Permissions.HAS_APNS_P12_EXPORT)
    @GetMapping("/export")
    public Object export(ApnsP12Request request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            apnsP12RestService,
            ApnsP12Excel.class,
            "ApnsP12",
            "apns_p12"
        );
    }

    
    
}