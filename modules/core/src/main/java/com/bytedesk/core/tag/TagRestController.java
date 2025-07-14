/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:39:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/tag")
@AllArgsConstructor
@Tag(name = "Tag Management", description = "Tag management APIs for organizing and categorizing content with tags")
@Description("Tag Management Controller - Content tagging and categorization APIs")
public class TagRestController extends BaseRestController<TagRequest> {

    private final TagRestService tagRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @ActionAnnotation(title = "标签", action = "组织查询", description = "query tag by org")
    @Operation(summary = "Query Tags by Organization", description = "Retrieve tags for the current organization")
    @Override
    public ResponseEntity<?> queryByOrg(TagRequest request) {
        
        Page<TagResponse> tags = tagRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "用户查询", description = "query tag by user")
    @Operation(summary = "Query Tags by User", description = "Retrieve tags for the current user")
    @Override
    public ResponseEntity<?> queryByUser(TagRequest request) {
        
        Page<TagResponse> tags = tagRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = "标签", action = "查询详情", description = "query tag by uid")
    @Operation(summary = "Query Tag by UID", description = "Retrieve a specific tag by its unique identifier")
    @Override
    public ResponseEntity<?> queryByUid(TagRequest request) {
        
        TagResponse tag = tagRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "新建", description = "create tag")
    @Operation(summary = "Create Tag", description = "Create a new tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    public ResponseEntity<?> create(TagRequest request) {
        
        TagResponse tag = tagRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "更新", description = "update tag")
    @Operation(summary = "Update Tag", description = "Update an existing tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    public ResponseEntity<?> update(TagRequest request) {
        
        TagResponse tag = tagRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = "标签", action = "删除", description = "delete tag")
    @Operation(summary = "Delete Tag", description = "Delete a tag")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    public ResponseEntity<?> delete(TagRequest request) {
        
        tagRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "标签", action = "导出", description = "export tag")
    @Operation(summary = "Export Tags", description = "Export tags to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(TagRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagRestService,
            TagExcel.class,
            "标签",
            "tag"
        );
    }

    
    
}