/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 15:30:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_token;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/visitor/token")
@AllArgsConstructor
@Tag(name = "VisitorToken Management", description = "VisitorToken management APIs")
public class VisitorTokenRestController extends BaseRestController<VisitorTokenRequest, VisitorTokenRestService> {

    private final VisitorTokenRestService tokenRestService;

    @Override
    public ResponseEntity<?> queryByOrg(VisitorTokenRequest request) {
        
        Page<VisitorTokenResponse> page = tokenRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(VisitorTokenRequest request) {
        
        Page<VisitorTokenResponse> page = tokenRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUid(VisitorTokenRequest request) {

        VisitorTokenResponse response = tokenRestService.queryByUid(request);
        // 
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> create(VisitorTokenRequest request) {
        
        VisitorTokenResponse response = tokenRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(VisitorTokenRequest request) {
        
        VisitorTokenResponse response = tokenRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(VisitorTokenRequest request) {
        
        tokenRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/generate")
    @ActionAnnotation(title = "用户", action = "generate_token", description = "Generate Access VisitorToken")
    public ResponseEntity<?> generateAccessVisitorToken(@RequestBody VisitorTokenRequest request) {

        String accessVisitorToken = tokenRestService.generateAccessVisitorToken(request);

        return ResponseEntity.ok(JsonResult.success("success", accessVisitorToken));
    }

    
    
}
