/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.blog;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/blog")
@AllArgsConstructor
@Tag(name = "Blog Management", description = "Blog management APIs for organizing and categorizing content with blogs")
@Description("Blog Management Controller - Content blogging and categorization APIs")
public class BlogRestController extends BaseRestController<BlogRequest, BlogRestService> {

    private final BlogRestService blogRestService;

    @ActionAnnotation(title = "Blog", action = "组织查询", description = "query blog by org")
    @Operation(summary = "Query Blogs by Organization", description = "Retrieve blogs for the current organization")
    @PreAuthorize(BlogPermissions.HAS_BLOG_READ)
    @Override
    public ResponseEntity<?> queryByOrg(BlogRequest request) {
        
        Page<BlogResponse> blogs = blogRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(blogs));
    }

    @ActionAnnotation(title = "Blog", action = "用户查询", description = "query blog by user")
    @Operation(summary = "Query Blogs by User", description = "Retrieve blogs for the current user")
    @PreAuthorize(BlogPermissions.HAS_BLOG_READ)
    @Override
    public ResponseEntity<?> queryByUser(BlogRequest request) {
        
        Page<BlogResponse> blogs = blogRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(blogs));
    }

    @ActionAnnotation(title = "Blog", action = "查询详情", description = "query blog by uid")
    @Operation(summary = "Query Blog by UID", description = "Retrieve a specific blog by its unique identifier")
    @PreAuthorize(BlogPermissions.HAS_BLOG_READ)
    @Override
    public ResponseEntity<?> queryByUid(BlogRequest request) {
        
        BlogResponse blog = blogRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(blog));
    }

    @ActionAnnotation(title = "Blog", action = "新建", description = "create blog")
    @Operation(summary = "Create Blog", description = "Create a new blog")
    @Override
    @PreAuthorize(BlogPermissions.HAS_BLOG_CREATE)
    public ResponseEntity<?> create(BlogRequest request) {
        
        BlogResponse blog = blogRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(blog));
    }

    @ActionAnnotation(title = "Blog", action = "更新", description = "update blog")
    @Operation(summary = "Update Blog", description = "Update an existing blog")
    @Override
    @PreAuthorize(BlogPermissions.HAS_BLOG_UPDATE)
    public ResponseEntity<?> update(BlogRequest request) {
        
        BlogResponse blog = blogRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(blog));
    }

    @ActionAnnotation(title = "Blog", action = "删除", description = "delete blog")
    @Operation(summary = "Delete Blog", description = "Delete a blog")
    @Override
    @PreAuthorize(BlogPermissions.HAS_BLOG_DELETE)
    public ResponseEntity<?> delete(BlogRequest request) {
        
        blogRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Blog", action = "导出", description = "export blog")
    @Operation(summary = "Export Blogs", description = "Export blogs to Excel format")
    @Override
    @PreAuthorize(BlogPermissions.HAS_BLOG_EXPORT)
    @GetMapping("/export")
    public Object export(BlogRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            blogRestService,
            BlogExcel.class,
            "Blog",
            "blog"
        );
    }

    
    
}