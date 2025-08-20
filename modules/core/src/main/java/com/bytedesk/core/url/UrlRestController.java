/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:24:17
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/url")
@AllArgsConstructor
@Tag(name = "URL Management", description = "URL management APIs for managing system URLs")
public class UrlRestController extends BaseRestController<UrlRequest, UrlRestService> {

    private final UrlRestService urlRestService;

    @Operation(summary = "Query URLs by Organization", description = "Retrieve URLs for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(UrlRequest request) {
        
        Page<UrlResponse> urls = urlRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(urls));
    }

    @Operation(summary = "Query URLs by User", description = "Retrieve URLs for the current user")
    @Override
    public ResponseEntity<?> queryByUser(UrlRequest request) {
        
        Page<UrlResponse> urls = urlRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(urls));
    }

    @Operation(summary = "Create URL", description = "Create a new URL entry")
    @Override
    public ResponseEntity<?> create(UrlRequest request) {
        
        UrlResponse url = urlRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(url));
    }

    @Operation(summary = "Update URL", description = "Update an existing URL entry")
    @Override
    public ResponseEntity<?> update(UrlRequest request) {
        
        UrlResponse url = urlRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(url));
    }

    @Operation(summary = "Delete URL", description = "Delete a URL entry")
    @Override
    public ResponseEntity<?> delete(UrlRequest request) {
        
        urlRestService.delete(request);

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