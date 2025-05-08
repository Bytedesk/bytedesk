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

@RestController
@RequestMapping("/api/v1/channel/app")
@AllArgsConstructor
public class ChannelAppRestController extends BaseRestController<ChannelAppRequest> {

    private final ChannelAppRestService appRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "应用", action = "组织查询", description = "query app by org")
    @Override
    public ResponseEntity<?> queryByOrg(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = appRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @ActionAnnotation(title = "应用", action = "用户查询", description = "query app by user")
    @Override
    public ResponseEntity<?> queryByUser(ChannelAppRequest request) {
        
        Page<ChannelAppResponse> apps = appRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @ActionAnnotation(title = "应用", action = "查询详情", description = "query app by uid")
    @Override
    public ResponseEntity<?> queryByUid(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "应用", action = "新建", description = "create app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "应用", action = "更新", description = "update app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(ChannelAppRequest request) {
        
        ChannelAppResponse app = appRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "应用", action = "删除", description = "delete app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(ChannelAppRequest request) {
        
        appRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "应用", action = "导出", description = "export app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
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