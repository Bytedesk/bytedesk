/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-24 09:50:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 12:40:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/authority")
@AllArgsConstructor
public class AuthorityRestController extends BaseRestController<AuthorityRequest> {

    private final AuthorityService authorityService;

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_READ)
    @Override
    public ResponseEntity<?> queryByOrg(AuthorityRequest request) {
        Page<AuthorityResponse> authorities = authorityService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(authorities));
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_READ)
    @Override
    public ResponseEntity<?> queryByUser(AuthorityRequest request) {
        Page<AuthorityResponse> authorities = authorityService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(authorities));
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_CREATE)
    @Override
    public ResponseEntity<?> create(AuthorityRequest request) {
        AuthorityResponse authority = authorityService.create(request);
        return ResponseEntity.ok(JsonResult.success(authority));
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_UPDATE)
    @Override
    public ResponseEntity<?> update(AuthorityRequest request) {
        AuthorityResponse authority = authorityService.update(request);
        return ResponseEntity.ok(JsonResult.success(authority));
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_DELETE)
    @Override
    public ResponseEntity<?> delete(AuthorityRequest request) {
        authorityService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}