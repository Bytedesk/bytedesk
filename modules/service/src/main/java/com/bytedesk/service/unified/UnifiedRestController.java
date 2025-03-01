/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-01 18:40:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.unified;

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
@RequestMapping("/api/v1/unified")
@AllArgsConstructor
public class UnifiedRestController extends BaseRestController<UnifiedRequest> {

    private final UnifiedRestService unifiedService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(UnifiedRequest request) {
        
        Page<UnifiedResponse> unifieds = unifiedService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(unifieds));
    }

    @Override
    public ResponseEntity<?> queryByUser(UnifiedRequest request) {
        
        Page<UnifiedResponse> unifieds = unifiedService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(unifieds));
    }

    @Override
    public ResponseEntity<?> create(UnifiedRequest request) {
        
        UnifiedResponse unified = unifiedService.create(request);

        return ResponseEntity.ok(JsonResult.success(unified));
    }

    @Override
    public ResponseEntity<?> update(UnifiedRequest request) {
        
        UnifiedResponse unified = unifiedService.update(request);

        return ResponseEntity.ok(JsonResult.success(unified));
    }

    @Override
    public ResponseEntity<?> delete(UnifiedRequest request) {
        
        unifiedService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}