/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:38:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.url;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/url")
@AllArgsConstructor
public class UrlRestController extends BaseRestController<UrlRequest> {

    private final UrlRestService urlService;

    @Override
    public ResponseEntity<?> queryByOrg(UrlRequest request) {
        
        Page<UrlResponse> urls = urlService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(urls));
    }

    @Override
    public ResponseEntity<?> queryByUser(UrlRequest request) {
        
        Page<UrlResponse> urls = urlService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(urls));
    }

    @Override
    public ResponseEntity<?> create(UrlRequest request) {
        
        UrlResponse url = urlService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(url));
    }

    @Override
    public ResponseEntity<?> update(UrlRequest request) {
        
        UrlResponse url = urlService.update(request);

        return ResponseEntity.ok(JsonResult.success(url));
    }

    @Override
    public ResponseEntity<?> delete(UrlRequest request) {
        
        urlService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Override
    public Object export(UrlRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    @Override
    public ResponseEntity<?> queryByUid(UrlRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}