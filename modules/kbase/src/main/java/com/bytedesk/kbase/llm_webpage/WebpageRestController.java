/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-18 10:12:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/webpage")
@AllArgsConstructor
public class WebpageRestController extends BaseRestController<WebpageRequest, WebpageRestService> {

    private final WebpageRestService webpageRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WebpageRequest request) {
        
        Page<WebpageResponse> webpages = webpageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webpages));
    }

    @Override
    public ResponseEntity<?> queryByUser(WebpageRequest request) {
        
        Page<WebpageResponse> webpages = webpageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(webpages));
    }

    @Override
    public ResponseEntity<?> create(WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
    public ResponseEntity<?> update(WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
    public ResponseEntity<?> delete(WebpageRequest request) {
        
        webpageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody WebpageRequest request) {

        webpageRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable webpage
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody WebpageRequest request) {

        WebpageResponse webpage = webpageRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
    public Object export(WebpageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            webpageRestService,
            WebpageExcel.class,
            "知识库网站",
            "webpage"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(WebpageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}