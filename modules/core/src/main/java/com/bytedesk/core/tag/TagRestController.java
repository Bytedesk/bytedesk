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
package com.bytedesk.core.tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
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
public class TagRestController extends BaseRestController<TagRequest, TagRestService> {

    private final TagRestService tagRestService;

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query tag by org")
    @Operation(summary = "Query Tags by Organization", description = "Retrieve tags for the current organization")
    @PreAuthorize(TagPermissions.HAS_TAG_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(TagRequest request) {
        
        Page<TagResponse> tags = tagRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query tag by user")
    @Operation(summary = "Query Tags by User", description = "Retrieve tags for the current user")
    @PreAuthorize(TagPermissions.HAS_TAG_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(TagRequest request) {
        
        Page<TagResponse> tags = tagRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(tags));
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query tag by uid")
    @Operation(summary = "Query Tag by UID", description = "Retrieve a specific tag by its unique identifier")
    @PreAuthorize(TagPermissions.HAS_TAG_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TagRequest request) {
        
        TagResponse tag = tagRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_CREATE, description = "create tag")
    @Operation(summary = "Create Tag", description = "Create a new tag")
    @Override
    @PreAuthorize(TagPermissions.HAS_TAG_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TagRequest request) {
        
        TagResponse tag = tagRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_UPDATE, description = "update tag")
    @Operation(summary = "Update Tag", description = "Update an existing tag")
    @Override
    @PreAuthorize(TagPermissions.HAS_TAG_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TagRequest request) {
        
        TagResponse tag = tagRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(tag));
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_DELETE, description = "delete tag")
    @Operation(summary = "Delete Tag", description = "Delete a tag")
    @Override
    @PreAuthorize(TagPermissions.HAS_TAG_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TagRequest request) {
        
        tagRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TAG, action = I18Consts.I18N_ACTION_EXPORT, description = "export tag")
    @Operation(summary = "Export Tags", description = "Export tags to Excel format")
    @Override
    @PreAuthorize(TagPermissions.HAS_TAG_EXPORT)
    @GetMapping("/export")
    public Object export(TagRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            tagRestService,
            TagExcel.class,
            "Tag",
            "tag"
        );
    }

    
    
}