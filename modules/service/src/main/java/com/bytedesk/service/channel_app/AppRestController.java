/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-08 12:38:02
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
public class AppRestController extends BaseRestController<AppRequest> {

    private final AppRestService appRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "渠道", action = "查询组织", description = "query app by org")
    @Override
    public ResponseEntity<?> queryByOrg(AppRequest request) {
        
        Page<AppResponse> apps = appRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @ActionAnnotation(title = "渠道", action = "查询用户", description = "query app by user")
    @Override
    public ResponseEntity<?> queryByUser(AppRequest request) {
        
        Page<AppResponse> apps = appRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(apps));
    }

    @ActionAnnotation(title = "渠道", action = "查询详情", description = "query app by uid")
    @Override
    public ResponseEntity<?> queryByUid(AppRequest request) {
        
        AppResponse app = appRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "渠道", action = "新建", description = "create app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(AppRequest request) {
        
        AppResponse app = appRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "渠道", action = "更新", description = "update app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(AppRequest request) {
        
        AppResponse app = appRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(app));
    }

    @ActionAnnotation(title = "渠道", action = "删除", description = "delete app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(AppRequest request) {
        
        appRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "渠道", action = "导出", description = "export app")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(AppRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            appRestService,
            AppExcel.class,
            "渠道",
            "app"
        );
    }

    
    
}