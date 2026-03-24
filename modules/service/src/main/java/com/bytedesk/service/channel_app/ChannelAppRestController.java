/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:18:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.channel_app;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Channel App Management", description = "Channel app management APIs")
@RestController
@RequestMapping("/api/v1/channel/app")
@AllArgsConstructor
public class ChannelAppRestController extends BaseRestController<ChannelAppRequest, ChannelAppRestService> {

    private final ChannelAppRestService channelRestService;

    @Operation(summary = "Query Channel Apps by Organization", description = "Retrieve channel app list by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @GetMapping("/query/org")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_READ)
    @Override
    public ResponseEntity<?> queryByOrg(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = channelRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @Operation(summary = "Query Channel Apps by User", description = "Retrieve channel app list by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @GetMapping({ "/query", "/query/user" })
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_READ)
    @Override
    public ResponseEntity<?> queryByUser(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = channelRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @Operation(summary = "Query Channel App by UID", description = "Retrieve channel app details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @GetMapping("/query/uid")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_READ)
    @Override
    public ResponseEntity<?> queryByUid(ChannelAppRequest request) {
        
        ChannelAppResponse app = channelRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "Create Channel App", description = "Create a new channel app")
    @ApiResponse(responseCode = "200", description = "Created successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @PostMapping("/create")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_CREATE)
    @Override
    public ResponseEntity<?> create(@RequestBody ChannelAppRequest request) {
        
        ChannelAppResponse app = channelRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "Update Channel App", description = "Update channel app information")
    @ApiResponse(responseCode = "200", description = "Updated successfully",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @PostMapping("/update")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_UPDATE)
    @Override
    public ResponseEntity<?> update(@RequestBody ChannelAppRequest request) {
        
        ChannelAppResponse app = channelRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "Delete Channel App", description = "Delete the specified channel app")
    @ApiResponse(responseCode = "200", description = "Deleted successfully")
    @PostMapping("/delete")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_DELETE)
    @Override
    public ResponseEntity<?> delete(@RequestBody ChannelAppRequest request) {
        
        channelRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Channel Apps", description = "Export channel app data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @Override
    @GetMapping("/export")
    @PreAuthorize(ChannelAppPermissions.HAS_CHANNEL_APP_EXPORT)
    public Object export(ChannelAppRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            channelRestService,
            ChannelAppExcel.class,
            "应用",
            "app"
        );
    }

    
    
}