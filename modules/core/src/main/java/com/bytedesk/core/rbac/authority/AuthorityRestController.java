/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-24 09:50:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:37:02
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

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/authority")
@AllArgsConstructor
@Tag(name = "Authority Management", description = "Authority management APIs")
public class AuthorityRestController extends BaseRestController<AuthorityRequest> {

    private final AuthorityRestService authorityService;

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

    @Override
    public ResponseEntity<?> queryByUid(AuthorityRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_CREATE)
    @Override
    public ResponseEntity<?> create(AuthorityRequest request) {

        AuthorityResponse authority = authorityService.initVisitor(request);

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

    @Override
    public Object export(AuthorityRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    
}