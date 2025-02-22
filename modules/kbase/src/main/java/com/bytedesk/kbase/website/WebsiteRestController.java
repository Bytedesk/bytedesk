/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 14:20:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.website;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/website")
@AllArgsConstructor
public class WebsiteRestController extends BaseRestController<WebsiteRequest> {

    private final WebsiteRestService websiteService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Override
    public ResponseEntity<?> queryByUser(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Override
    public ResponseEntity<?> create(WebsiteRequest request) {
        
        WebsiteResponse website = websiteService.create(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @Override
    public ResponseEntity<?> update(WebsiteRequest request) {
        
        WebsiteResponse website = websiteService.update(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @Override
    public ResponseEntity<?> delete(WebsiteRequest request) {
        
        websiteService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}