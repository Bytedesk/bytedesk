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
package com.bytedesk.kbase.qa;

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
@RequestMapping("/api/v1/qa")
@AllArgsConstructor
public class QaRestController extends BaseRestController<QaRequest> {

    private final QaRestService qaService;

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(QaRequest request) {
        
        Page<QaResponse> qas = qaService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(qas));
    }

    @Override
    public ResponseEntity<?> queryByUser(QaRequest request) {
        
        Page<QaResponse> qas = qaService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(qas));
    }

    @Override
    public ResponseEntity<?> create(QaRequest request) {
        
        QaResponse qa = qaService.create(request);

        return ResponseEntity.ok(JsonResult.success(qa));
    }

    @Override
    public ResponseEntity<?> update(QaRequest request) {
        
        QaResponse qa = qaService.update(request);

        return ResponseEntity.ok(JsonResult.success(qa));
    }

    @Override
    public ResponseEntity<?> delete(QaRequest request) {
        
        qaService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}