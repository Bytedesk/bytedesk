/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 16:45:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

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

@Tag(name = "Keyword Auto Reply Management", description = "Keyword auto reply management APIs")
@RestController
@RequestMapping("/api/v1/autoreply/keyword")
@AllArgsConstructor
public class AutoReplyKeywordRestController extends BaseRestController<AutoReplyKeywordRequest, AutoReplyKeywordRestService> {

    private final AutoReplyKeywordRestService keywordRestService;

    @Operation(summary = "Query Keyword Auto Replies by Organization", description = "Query the list of keyword auto replies by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_READ)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query autoReplyKeyword by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Keyword Auto Replies by User", description = "Query the list of keyword auto replies by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_READ)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query autoReplyKeyword by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(AutoReplyKeywordRequest request) {
        
        Page<AutoReplyKeywordResponse> page = keywordRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Keyword Auto Reply", description = "Create a new keyword auto reply")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_CREATE, description = "create autoReplyKeyword")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Keyword Auto Reply", description = "Update keyword auto reply information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_UPDATE, description = "update autoReplyKeyword")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Keyword Auto Reply", description = "Delete the specified keyword auto reply")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_DELETE, description = "delete autoReplyKeyword")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody AutoReplyKeywordRequest request) {

        keywordRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success(request.getUid()));
    }

    @Operation(summary = "Enable Keyword Auto Reply", description = "Enable or disable keyword auto replies")
    @ApiResponse(responseCode = "200", description = "Operation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = AutoReplyKeywordResponse.class)))
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_ENABLE, description = "enable autoReplyKeyword")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody AutoReplyKeywordRequest request) {
        
        AutoReplyKeywordResponse response = keywordRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Export Keyword Auto Replies", description = "Export keyword auto reply data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(AutoReplyKeywordPermissions.HAS_AUTO_REPLY_KEYWORD_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_AUTO_REPLY_KEYWORD, action = I18Consts.I18N_ACTION_EXPORT, description = "export autoReplyKeyword")
    @GetMapping("/export")
    public Object export(AutoReplyKeywordRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            keywordRestService,
            AutoReplyKeywordExcel.class,
            "Keyword",
            "auto_reply-keyword"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(AutoReplyKeywordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }


    
}
