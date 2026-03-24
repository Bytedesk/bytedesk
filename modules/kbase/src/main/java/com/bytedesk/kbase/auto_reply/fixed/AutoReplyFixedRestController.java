/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:39:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:45:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Fixed Auto Reply Management", description = "Fixed auto reply management APIs")
@RestController
@RequestMapping("/api/v1/autoreply/fixed")
@AllArgsConstructor
public class AutoReplyFixedRestController extends BaseRestController<AutoReplyFixedRequest, AutoReplyFixedRestService> {

    private final AutoReplyFixedRestService autoReplyService;

    @Operation(summary = "Query Fixed Auto Replies by Organization", description = "Query the list of fixed auto replies by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyFixedResponse.class)))
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_READ)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query autoReplyFixed by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Fixed Auto Replies by User", description = "Query the list of fixed auto replies by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyFixedResponse.class)))
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_READ)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query autoReplyFixed by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(AutoReplyFixedRequest request) {
        
        Page<AutoReplyFixedResponse> page = autoReplyService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Fixed Auto Reply", description = "Create a new fixed auto reply")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyFixedResponse.class)))
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_CREATE, description = "create autoReplyFixed")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Fixed Auto Reply", description = "Update fixed auto reply information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyFixedResponse.class)))
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_UPDATE, description = "update autoReplyFixed")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Fixed Auto Reply", description = "Delete the specified fixed auto reply")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_DELETE, description = "delete autoReplyFixed")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AutoReplyFixedRequest request) {
        
        autoReplyService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
    @Operation(summary = "Enable Fixed Auto Reply", description = "Enable or disable fixed auto replies")
    @ApiResponse(responseCode = "200", description = "Operation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyFixedResponse.class)))
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_ENABLE, description = "enable autoReplyFixed")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AutoReplyFixedRequest request) {
        
        AutoReplyFixedResponse response = autoReplyService.enable(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }
    
    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @Operation(summary = "Export Fixed Auto Replies", description = "Export fixed auto reply data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(AutoReplyFixedPermissions.HAS_AUTO_REPLY_FIXED_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_FIXED, action = I18Consts.I18N_ACTION_EXPORT, description = "export autoReplyFixed")
    @GetMapping("/export")
    public Object export(AutoReplyFixedRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            autoReplyService,
            AutoReplyFixedExcel.class,
            "AutoReplyFixed",
            "auto_reply-fixed"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(AutoReplyFixedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
