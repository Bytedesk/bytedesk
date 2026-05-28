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
package com.bytedesk.core.push.sms_push;

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
@RequestMapping("/api/v1/sms_push")
@AllArgsConstructor
@Tag(name = "SmsPush Management", description = "SmsPush management APIs for organizing and categorizing content with sms_pushs")
@Description("SmsPush Management Controller - Content sms_pushging and categorization APIs")
public class SmsPushRestController extends BaseRestController<SmsPushRequest, SmsPushRestService> {

    private final SmsPushRestService sms_pushRestService;

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query sms_push by org")
    @Operation(summary = "Query SmsPushs by Organization", description = "Retrieve sms_pushs for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(SmsPushRequest request) {
        
        Page<SmsPushResponse> sms_pushs = sms_pushRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(sms_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query sms_push by user")
    @Operation(summary = "Query SmsPushs by User", description = "Retrieve sms_pushs for the current user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(SmsPushRequest request) {
        
        Page<SmsPushResponse> sms_pushs = sms_pushRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(sms_pushs));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query sms_push by uid")
    @Operation(summary = "Query SmsPush by UID", description = "Retrieve a specific sms_push by its unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(SmsPushRequest request) {
        
        SmsPushResponse sms_push = sms_pushRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(sms_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_CREATE, description = "create sms_push")
    @Operation(summary = "Create SmsPush", description = "Create a new sms_push")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SmsPushRequest request) {
        
        SmsPushResponse sms_push = sms_pushRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(sms_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_UPDATE, description = "update sms_push")
    @Operation(summary = "Update SmsPush", description = "Update an existing sms_push")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody SmsPushRequest request) {
        
        SmsPushResponse sms_push = sms_pushRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(sms_push));
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_DELETE, description = "delete sms_push")
    @Operation(summary = "Delete SmsPush", description = "Delete a sms_push")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody SmsPushRequest request) {
        
        sms_pushRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_SMS_PUSH, action = I18Consts.I18N_ACTION_EXPORT, description = "export sms_push")
    @Operation(summary = "Export SmsPushs", description = "Export sms_pushs to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(SmsPushRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            sms_pushRestService,
            SmsPushExcel.class,
            "SmsPush",
            "sms_push"
        );
    }

    
    
}