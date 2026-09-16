/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.sms_provider;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/sms_provider")
@AllArgsConstructor
@Tag(name = "SmsProvider Management", description = "SmsProvider management APIs for organizing and categorizing content with sms_providers")
@Description("SmsProvider Management Controller - Content sms_providerging and categorization APIs")
public class SmsProviderRestController extends BaseRestController<SmsProviderRequest, SmsProviderRestService> {

    private final SmsProviderRestService sms_providerRestService;

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query sms_provider by org")
    @Operation(summary = "Query SmsProviders by Organization", description = "Retrieve sms_providers for the current organization")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_READ)
    public ResponseEntity<?> queryByOrg(SmsProviderRequest request) {
        
        Page<SmsProviderResponse> sms_providers = sms_providerRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(sms_providers));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query sms_provider by user")
    @Operation(summary = "Query SmsProviders by User", description = "Retrieve sms_providers for the current user")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_READ)
    public ResponseEntity<?> queryByUser(SmsProviderRequest request) {
        
        Page<SmsProviderResponse> sms_providers = sms_providerRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(sms_providers));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query sms_provider by uid")
    @Operation(summary = "Query SmsProvider by UID", description = "Retrieve a specific sms_provider by its unique identifier")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_READ)
    public ResponseEntity<?> queryByUid(SmsProviderRequest request) {
        
        SmsProviderResponse sms_provider = sms_providerRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(sms_provider));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_CREATE, description = "create sms_provider")
    @Operation(summary = "Create SmsProvider", description = "Create a new sms_provider")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_CREATE)
    public ResponseEntity<?> create(SmsProviderRequest request) {
        
        SmsProviderResponse sms_provider = sms_providerRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(sms_provider));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_UPDATE, description = "update sms_provider")
    @Operation(summary = "Update SmsProvider", description = "Update an existing sms_provider")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_UPDATE)
    public ResponseEntity<?> update(SmsProviderRequest request) {
        
        SmsProviderResponse sms_provider = sms_providerRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(sms_provider));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_DELETE, description = "delete sms_provider")
    @Operation(summary = "Delete SmsProvider", description = "Delete a sms_provider")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_DELETE)
    public ResponseEntity<?> delete(SmsProviderRequest request) {
        
        sms_providerRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PROVIDER, action = I18Consts.I18N_ACTION_EXPORT, description = "export sms_provider")
    @Operation(summary = "Export SmsProviders", description = "Export sms_providers to Excel format")
    @Override
    @PreAuthorize(SmsProviderPermissions.HAS_SMS_PROVIDER_EXPORT)
    @GetMapping("/export")
    public Object export(SmsProviderRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            sms_providerRestService,
            SmsProviderExcel.class,
            "SmsProvider",
            "sms_provider"
        );
    }

    
    
}