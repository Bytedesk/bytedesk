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
package com.bytedesk.kbase.llm_website;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.annotation.ActionAnnotation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/website")
@AllArgsConstructor
public class WebsiteRestController extends BaseRestController<WebsiteRequest> {

    private final WebsiteRestService websiteRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Override
    public ResponseEntity<?> queryByUser(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @ActionAnnotation(title = "知识库网站", action = "新建", description = "create website")
    @Override
    public ResponseEntity<?> create(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "更新", description = "update website")
    @Override
    public ResponseEntity<?> update(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "删除", description = "delete website")
    @Override
    public ResponseEntity<?> delete(WebsiteRequest request) {
        
        websiteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody WebsiteRequest request) {

        websiteRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable website
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody WebsiteRequest request) {

        WebsiteResponse website = websiteRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "导出", description = "export website")
    @Override
    public Object export(WebsiteRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            websiteRestService,
            WebsiteExcel.class,
            "知识库网站",
            "website"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(WebsiteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}