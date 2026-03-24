/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-20 13:19:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-25 11:10:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_message;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.message.MessageRequest;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.rbac.role.RolePermissions;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Visitor Message Management", description = "Visitor message management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor/message")
public class VisitorMessageRestController extends BaseRestController<MessageRequest, VisitorMessageRestService> {
    
    private final VisitorMessageRestService messageRestService;

    @Operation(summary = "Query Visitor Messages by Organization", description = "Retrieve visitor message list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Visitor Messages by User", description = "Retrieve visitor message list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByUser(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Query Visitor Message by UID", description = "Retrieve visitor message details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @GetMapping("/query/uid")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create Visitor Message", description = "Create a new visitor message")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @PostMapping("/create")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> create(@RequestBody MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Operation(summary = "Update Visitor Message", description = "Update visitor message information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @PostMapping("/update")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> update(@RequestBody MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Operation(summary = "Delete Visitor Message", description = "Delete the specified visitor message")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> delete(@RequestBody MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Operation(summary = "Export Visitor Messages", description = "Export visitor message data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @GetMapping("/export")
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public Object export(MessageRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}
