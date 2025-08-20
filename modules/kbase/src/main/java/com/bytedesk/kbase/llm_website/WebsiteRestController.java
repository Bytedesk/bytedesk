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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/website")
@AllArgsConstructor
@Tag(name = "网站管理", description = "网站管理相关接口")
public class WebsiteRestController extends BaseRestController<WebsiteRequest, WebsiteRestService> {

    private final WebsiteRestService websiteRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询网站", description = "查询组织的网站列表")
    @Override
    public ResponseEntity<?> queryByOrg(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Operation(summary = "根据用户查询网站", description = "查询用户的网站列表")
    @Override
    public ResponseEntity<?> queryByUser(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @ActionAnnotation(title = "知识库网站", action = "新建", description = "create website")
    @Operation(summary = "创建网站", description = "创建新的网站")
    @Override
    public ResponseEntity<?> create(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "更新", description = "update website")
    @Operation(summary = "更新网站", description = "更新现有的网站")
    @Override
    public ResponseEntity<?> update(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "删除", description = "delete website")
    @Operation(summary = "删除网站", description = "删除指定的网站")
    @Override
    public ResponseEntity<?> delete(WebsiteRequest request) {
        
        websiteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    @Operation(summary = "删除所有网站", description = "删除所有网站")
    public ResponseEntity<?> deleteAll(@RequestBody WebsiteRequest request) {

        websiteRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable website
    @PostMapping("/enable")
    @Operation(summary = "启用/禁用网站", description = "启用或禁用网站")
    public ResponseEntity<?> enable(@RequestBody WebsiteRequest request) {

        WebsiteResponse website = websiteRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "导出", description = "export website")
    @Operation(summary = "导出网站", description = "导出网站数据")
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

    @Operation(summary = "根据UID查询网站", description = "通过UID查询具体的网站")
    @Override
    public ResponseEntity<?> queryByUid(WebsiteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}