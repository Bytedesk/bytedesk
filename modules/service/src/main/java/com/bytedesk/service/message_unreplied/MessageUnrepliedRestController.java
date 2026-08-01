/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-25 11:09:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unreplied;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Unreplied Message Management", description = "Unreplied message management APIs")
@RestController
@RequestMapping("/api/v1/message/unreplied")
@AllArgsConstructor
public class MessageUnrepliedRestController extends BaseRestController<MessageUnrepliedRequest, MessageUnrepliedRestService> {

    private final MessageUnrepliedRestService messageUnrepliedService;

    @Operation(summary = "Query Unreplied Messages by Organization", description = "Retrieve unreplied message list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @GetMapping("/query/org")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_READ)
    @Override
    public ResponseEntity<?> queryByOrg(MessageUnrepliedRequest request) {
        
        Page<MessageUnrepliedResponse> message_unreplieds = messageUnrepliedService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplieds));
    }

    @Operation(summary = "Query Unreplied Messages by User", description = "Retrieve unreplied message list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_READ)
    @Override
    public ResponseEntity<?> queryByUser(MessageUnrepliedRequest request) {
        
        Page<MessageUnrepliedResponse> message_unreplieds = messageUnrepliedService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplieds));
    }

    @Operation(summary = "Create Unreplied Message", description = "Create a new unreplied message")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @PostMapping("/create")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody MessageUnrepliedRequest request) {
        
        MessageUnrepliedResponse message_unreplied = messageUnrepliedService.create(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplied));
    }

    @Operation(summary = "Update Unreplied Message", description = "Update unreplied message information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @PostMapping("/update")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody MessageUnrepliedRequest request) {
        
        MessageUnrepliedResponse message_unreplied = messageUnrepliedService.update(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplied));
    }

    @Operation(summary = "Delete Unreplied Message", description = "Delete the specified unreplied message")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody MessageUnrepliedRequest request) {
        
        messageUnrepliedService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Unreplied Messages", description = "Export unreplied message data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @GetMapping("/export")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_EXPORT)
    @Override
    public Object export(MessageUnrepliedRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "Query Unreplied Message by UID", description = "Retrieve unreplied message details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @GetMapping("/query/uid")
    @PreAuthorize(MessageUnrepliedPermissions.HAS_MESSAGE_UNANSWERED_READ)
    @Override
    public ResponseEntity<?> queryByUid(MessageUnrepliedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}