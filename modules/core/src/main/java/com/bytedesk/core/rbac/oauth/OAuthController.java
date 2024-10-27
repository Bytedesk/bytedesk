/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:50:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-10 23:53:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.oauth;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth/api/v1")
@RequiredArgsConstructor
public class OAuthController extends BaseController<OAuthRequest> {

    private final OAuthService oauthService;

    @Override
    public ResponseEntity<?> queryByOrg(OAuthRequest request) {
        
        Page<OAuthResponse> oauths = oauthService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(oauths));
    }

    @Override
    public ResponseEntity<?> queryByUser(OAuthRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(OAuthRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ResponseEntity<?> update(OAuthRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ResponseEntity<?> delete(OAuthRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    
}
