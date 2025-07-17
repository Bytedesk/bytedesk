/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 11:35:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/token")
@AllArgsConstructor
@Tag(name = "Token Management", description = "Token management APIs")
public class TokenRestController extends BaseRestController<TokenRequest> {

    private final TokenRestService tokenRestService;

    @Override
    public ResponseEntity<?> queryByOrg(TokenRequest request) {
        
        Page<TokenResponse> page = tokenRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(TokenRequest request) {
        
        Page<TokenResponse> page = tokenRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(TokenRequest request) {

        TokenResponse response = tokenRestService.queryByUid(request);
        // 
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(TokenRequest request) {
        
        TokenResponse response = tokenRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(TokenRequest request) {
        
        TokenResponse response = tokenRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(TokenRequest request) {
        
        tokenRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(TokenRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @PostMapping("/generate")
    @ActionAnnotation(title = "用户", action = "generate_token", description = "Generate Access Token")
    public ResponseEntity<?> generateAccessToken(@RequestBody TokenRequest request) {

        String accessToken = tokenRestService.generateAccessToken(request);

        return ResponseEntity.ok(JsonResult.success("success", accessToken));
    }

    
    
}
