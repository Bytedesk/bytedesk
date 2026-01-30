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
package com.bytedesk.service.channel;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/channel")
@AllArgsConstructor
@Tag(name = "Channel Management", description = "Channel management APIs for organizing and categorizing content with channels")
@Description("Channel Management Controller - Content channelging and categorization APIs")
public class ChannelRestController extends BaseRestController<ChannelRequest, ChannelRestService> {

    private final ChannelRestService channelRestService;

    @ActionAnnotation(title = "Channel", action = "组织查询", description = "query channel by org")
    @Operation(summary = "Query Channels by Organization", description = "Retrieve channels for the current organization")
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ChannelRequest request) {
        
        Page<ChannelResponse> channels = channelRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(channels));
    }

    @ActionAnnotation(title = "Channel", action = "用户查询", description = "query channel by user")
    @Operation(summary = "Query Channels by User", description = "Retrieve channels for the current user")
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_READ)
    @Override
    public ResponseEntity<?> queryByUser(ChannelRequest request) {
        
        Page<ChannelResponse> channels = channelRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(channels));
    }

    @ActionAnnotation(title = "Channel", action = "查询详情", description = "query channel by uid")
    @Operation(summary = "Query Channel by UID", description = "Retrieve a specific channel by its unique identifier")
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_READ)
    @Override
    public ResponseEntity<?> queryByUid(ChannelRequest request) {
        
        ChannelResponse channel = channelRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(channel));
    }

    @ActionAnnotation(title = "Channel", action = "新建", description = "create channel")
    @Operation(summary = "Create Channel", description = "Create a new channel")
    @Override
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_CREATE)
    public ResponseEntity<?> create(ChannelRequest request) {
        
        ChannelResponse channel = channelRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(channel));
    }

    @ActionAnnotation(title = "Channel", action = "更新", description = "update channel")
    @Operation(summary = "Update Channel", description = "Update an existing channel")
    @Override
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_UPDATE)
    public ResponseEntity<?> update(ChannelRequest request) {
        
        ChannelResponse channel = channelRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(channel));
    }

    @ActionAnnotation(title = "Channel", action = "删除", description = "delete channel")
    @Operation(summary = "Delete Channel", description = "Delete a channel")
    @Override
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_DELETE)
    public ResponseEntity<?> delete(ChannelRequest request) {
        
        channelRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Channel", action = "导出", description = "export channel")
    @Operation(summary = "Export Channels", description = "Export channels to Excel format")
    @Override
    @PreAuthorize(ChannelPermissions.HAS_CHANNEL_EXPORT)
    @GetMapping("/export")
    public Object export(ChannelRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            channelRestService,
            ChannelExcel.class,
            "Channel",
            "channel"
        );
    }

    
    
}