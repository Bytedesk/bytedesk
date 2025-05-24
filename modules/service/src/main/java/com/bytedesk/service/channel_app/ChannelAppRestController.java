/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-08 15:12:15
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "渠道应用管理", description = "渠道应用管理相关接口")
@RestController
@RequestMapping("/api/v1/channel/app")
@AllArgsConstructor
public class ChannelAppRestController extends BaseRestController<ChannelAppRequest> {

    private final ChannelAppRestService appRestService;

    @Operation(summary = "查询组织下的渠道应用", description = "根据组织ID查询渠道应用列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = appRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @Operation(summary = "查询用户下的渠道应用", description = "根据用户ID查询渠道应用列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = appRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @Operation(summary = "查询指定渠道应用", description = "根据UID查询渠道应用详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "创建渠道应用", description = "创建新的渠道应用")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @Override
    public ResponseEntity<?> create(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "更新渠道应用", description = "更新渠道应用信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ChannelAppResponse.class)))
    @Override
    public ResponseEntity<?> update(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @Operation(summary = "删除渠道应用", description = "删除指定的渠道应用")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(ChannelAppRequest request) {
        
        appRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "导出渠道应用", description = "导出渠道应用数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    @GetMapping("/export")
    public Object export(ChannelAppRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            appRestService,
            ChannelAppExcel.class,
            "应用",
            "app"
        );
    }

    
    
}