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
package com.bytedesk.core.push.apns_token;

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
@RequestMapping("/api/v1/apns_token")
@AllArgsConstructor
@Tag(name = "ApnsToken Management", description = "ApnsToken management APIs for organizing and categorizing content with apns_tokens")
@Description("ApnsToken Management Controller - Content apns_tokenging and categorization APIs")
public class ApnsTokenRestController extends BaseRestController<ApnsTokenRequest, ApnsTokenRestService> {

    private final ApnsTokenRestService apnsTokenRestService;

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query apns_token by org")
    @Operation(summary = "Query ApnsTokens by Organization", description = "Retrieve apns_tokens for the current organization")
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ApnsTokenRequest request) {
        
        Page<ApnsTokenResponse> apns_tokens = apnsTokenRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apns_tokens));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query apns_token by user")
    @Operation(summary = "Query ApnsTokens by User", description = "Retrieve apns_tokens for the current user")
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ApnsTokenRequest request) {
        
        Page<ApnsTokenResponse> apns_tokens = apnsTokenRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apns_tokens));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query apns_token by uid")
    @Operation(summary = "Query ApnsToken by UID", description = "Retrieve a specific apns_token by its unique identifier")
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ApnsTokenRequest request) {
        
        ApnsTokenResponse apns_token = apnsTokenRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(apns_token));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_CREATE, description = "create apns_token")
    @Operation(summary = "Create ApnsToken", description = "Create a new apns_token")
    @Override
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ApnsTokenRequest request) {
        
        ApnsTokenResponse apns_token = apnsTokenRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(apns_token));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_CREATE, description = "register current user apns token")
    @Operation(summary = "Register Current User APNs Token", description = "Create or update the APNs token bound to the current authenticated user. When p12Uid is omitted, the backend will try to resolve it from bundleId and orgUid; if both sandbox and production certificates exist for the same bundleId, the client must also provide environment.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ApnsTokenRequest request) {

        ApnsTokenResponse apnsToken = apnsTokenRestService.registerCurrentUserToken(request);

        return ResponseEntity.ok(JsonResult.success(apnsToken));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_DELETE, description = "unregister current user apns token")
    @Operation(summary = "Unregister Current User APNs Token", description = "Mark the current authenticated user's APNs token as deleted")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unregister")
    public ResponseEntity<?> unregister(@RequestBody ApnsTokenRequest request) {

        apnsTokenRestService.unregisterCurrentUserToken(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_QUERY_USER, description = "check whether current user ios app already registered apns token")
    @Operation(summary = "Check Current User APNs Token Registration", description = "Check whether the current authenticated user has already registered an APNs token for the specified iOS app. The request can provide token directly, or provide p12Uid, or provide bundleId plus orgUid and optional environment for resolution.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/is/registered")
    public ResponseEntity<?> isRegistered(ApnsTokenRequest request) {

        Boolean registered = apnsTokenRestService.isCurrentUserTokenRegistered(request);

        return ResponseEntity.ok(JsonResult.success(registered));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_UPDATE, description = "update apns_token")
    @Operation(summary = "Update ApnsToken", description = "Update an existing apns_token")
    @Override
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ApnsTokenRequest request) {
        
        ApnsTokenResponse apns_token = apnsTokenRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(apns_token));
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_DELETE, description = "delete apns_token")
    @Operation(summary = "Delete ApnsToken", description = "Delete a apns_token")
    @Override
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ApnsTokenRequest request) {
        
        apnsTokenRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_APNS_TOKEN, action = I18Consts.I18N_ACTION_EXPORT, description = "export apns_token")
    @Operation(summary = "Export ApnsTokens", description = "Export apns_tokens to Excel format")
    @Override
    @PreAuthorize(ApnsTokenPermissions.HAS_APNS_TOKEN_EXPORT)
    @GetMapping("/export")
    public Object export(ApnsTokenRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            apnsTokenRestService,
            ApnsTokenExcel.class,
            "ApnsToken",
            "apns_token"
        );
    }
    
    
}