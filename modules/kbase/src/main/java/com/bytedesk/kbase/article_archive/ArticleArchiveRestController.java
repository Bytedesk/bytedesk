/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-26 17:59:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/article_archive")
@AllArgsConstructor
@Tag(name = "Article Archive Management", description = "Article archive management APIs")
public class ArticleArchiveRestController extends BaseRestController<ArticleArchiveRequest, ArticleArchiveRestService> {

    private final ArticleArchiveRestService article_archiveService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Article Archives by Organization", description = "Retrieve article archive list for the organization")
    @Override
    public ResponseEntity<?> queryByOrg(ArticleArchiveRequest request) {

        Page<ArticleArchiveResponse> page = article_archiveService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Article Archives by User", description = "Retrieve article archive list for the user")
    @Override
    public ResponseEntity<?> queryByUser(ArticleArchiveRequest request) {
        
        Page<ArticleArchiveResponse> page = article_archiveService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Article Archive by UID", description = "Retrieve article archive details by UID")
    @Override
    public ResponseEntity<?> queryByUid(ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.queryByUid(request);

        if (article_archive == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "Create Article Archive", description = "Create a new article archive")
    @Override
    public ResponseEntity<?> create(@RequestBody ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.create(request);

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "Update Article Archive", description = "Update the existing article archive")
    @Override
    public ResponseEntity<?> update(@RequestBody ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.update(request);

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "Delete Article Archive", description = "Delete the specified article archive")
    @Override
    public ResponseEntity<?> delete(@RequestBody ArticleArchiveRequest request) {

        article_archiveService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "Export Article Archives", description = "Export article archive data")
    @Override
    public Object export(ArticleArchiveRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

}
