/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:50:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-07 10:32:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.oauth2;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2RestController extends BaseRestController<OAuth2Request> {

    private final OAuth2RestService oauthRestService;

    @Override
    public ResponseEntity<?> queryByOrg(OAuth2Request request) {
        
        Page<OAuth2Response> oauth2s = oauthRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(oauth2s));
    }

    @Override
    public ResponseEntity<?> queryByUser(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public Object export(OAuth2Request request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
