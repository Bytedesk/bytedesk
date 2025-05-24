/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-20 13:19:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-27 14:50:22
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "访客消息管理", description = "访客消息管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor/message")
public class VisitorMessageRestController extends BaseRestController<MessageRequest> {
    
    private final VisitorMessageRestService messageRestService;

    @Operation(summary = "查询组织下的访客消息", description = "根据组织ID查询访客消息列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的访客消息", description = "根据用户ID查询访客消息列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(MessageRequest request) {
        
        Page<MessageResponse> page = messageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/topic")
    public ResponseEntity<?> queryByTopic(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "查询指定访客消息", description = "根据UID查询访客消息详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(MessageRequest request) {

        Page<MessageResponse> response = messageRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "创建访客消息", description = "创建新的访客消息")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @Override
    public ResponseEntity<?> create(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Operation(summary = "更新访客消息", description = "更新访客消息信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageResponse.class)))
    @Override
    public ResponseEntity<?> update(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Operation(summary = "删除访客消息", description = "删除指定的访客消息")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(MessageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Operation(summary = "导出访客消息", description = "导出访客消息数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(MessageRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
    
}
