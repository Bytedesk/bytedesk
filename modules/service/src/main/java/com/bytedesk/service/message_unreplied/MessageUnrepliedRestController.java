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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "未回复消息管理", description = "未回复消息管理相关接口")
@RestController
@RequestMapping("/api/v1/message/unreplied")
@AllArgsConstructor
public class MessageUnrepliedRestController extends BaseRestController<MessageUnrepliedRequest, MessageUnrepliedRestService> {

    private final MessageUnrepliedRestService messageUnrepliedService;

    @Operation(summary = "查询组织下的未回复消息", description = "根据组织ID查询未回复消息列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(MessageUnrepliedRequest request) {
        
        Page<MessageUnrepliedResponse> message_unreplieds = messageUnrepliedService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplieds));
    }

    @Operation(summary = "查询用户下的未回复消息", description = "根据用户ID查询未回复消息列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(MessageUnrepliedRequest request) {
        
        Page<MessageUnrepliedResponse> message_unreplieds = messageUnrepliedService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplieds));
    }

    @Operation(summary = "创建未回复消息", description = "创建新的未回复消息")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @Override
    public ResponseEntity<?> create(MessageUnrepliedRequest request) {
        
        MessageUnrepliedResponse message_unreplied = messageUnrepliedService.create(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplied));
    }

    @Operation(summary = "更新未回复消息", description = "更新未回复消息信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @Override
    public ResponseEntity<?> update(MessageUnrepliedRequest request) {
        
        MessageUnrepliedResponse message_unreplied = messageUnrepliedService.update(request);

        return ResponseEntity.ok(JsonResult.success(message_unreplied));
    }

    @Operation(summary = "删除未回复消息", description = "删除指定的未回复消息")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(MessageUnrepliedRequest request) {
        
        messageUnrepliedService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出未回复消息", description = "导出未回复消息数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(MessageUnrepliedRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Operation(summary = "查询指定未回复消息", description = "根据UID查询未回复消息详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageUnrepliedResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(MessageUnrepliedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}