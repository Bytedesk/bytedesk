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
package com.bytedesk.core.sms;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/sms")
@AllArgsConstructor
@Tag(name = "Sms Management", description = "Sms management APIs for organizing and categorizing content with smss")
@Description("Sms Management Controller - Content smsging and categorization APIs")
public class SmsRestController extends BaseRestController<SmsRequest, SmsRestService> {

    private final SmsRestService smsRestService;

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query sms by org")
    @Operation(summary = "Query Smss by Organization", description = "Retrieve smss for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(SmsRequest request) {
        
        Page<SmsResponse> smss = smsRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(smss));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query sms by user")
    @Operation(summary = "Query Smss by User", description = "Retrieve smss for the current user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(SmsRequest request) {
        
        Page<SmsResponse> smss = smsRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(smss));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query sms by uid")
    @Operation(summary = "Query Sms by UID", description = "Retrieve a specific sms by its unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(SmsRequest request) {
        
        SmsResponse sms = smsRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(sms));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_CREATE, description = "create sms")
    @Operation(summary = "Create Sms", description = "Create a new sms")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SmsRequest request) {
        
        SmsResponse sms = smsRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(sms));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_UPDATE, description = "update sms")
    @Operation(summary = "Update Sms", description = "Update an existing sms")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody SmsRequest request) {
        
        SmsResponse sms = smsRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(sms));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_DELETE, description = "delete sms")
    @Operation(summary = "Delete Sms", description = "Delete a sms")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody SmsRequest request) {
        
        smsRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS, action = I18Consts.I18N_ACTION_EXPORT, description = "export sms")
    @Operation(summary = "Export Smss", description = "Export smss to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(SmsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            smsRestService,
            SmsExcel.class,
            "Sms",
            "sms"
        );
    }

    
    
}