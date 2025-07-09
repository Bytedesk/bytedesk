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
@Tag(name = "文章归档管理", description = "文章归档管理相关接口")
public class ArticleArchiveRestController extends BaseRestController<ArticleArchiveRequest> {

    private final ArticleArchiveRestService article_archiveService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询文章归档", description = "查询组织的文章归档列表")
    @Override
    public ResponseEntity<?> queryByOrg(ArticleArchiveRequest request) {

        Page<ArticleArchiveResponse> page = article_archiveService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据用户查询文章归档", description = "查询用户的文章归档列表")
    @Override
    public ResponseEntity<?> queryByUser(ArticleArchiveRequest request) {
        
        Page<ArticleArchiveResponse> page = article_archiveService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "根据UID查询文章归档", description = "通过UID查询具体的文章归档")
    @Override
    public ResponseEntity<?> queryByUid(ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.queryByUid(request);

        if (article_archive == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "创建文章归档", description = "创建新的文章归档")
    @Override
    public ResponseEntity<?> create(@RequestBody ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.create(request);

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "更新文章归档", description = "更新现有的文章归档")
    @Override
    public ResponseEntity<?> update(@RequestBody ArticleArchiveRequest request) {

        ArticleArchiveResponse article_archive = article_archiveService.update(request);

        return ResponseEntity.ok(JsonResult.success(article_archive));
    }

    @Operation(summary = "删除文章归档", description = "删除指定的文章归档")
    @Override
    public ResponseEntity<?> delete(@RequestBody ArticleArchiveRequest request) {

        article_archiveService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "导出文章归档", description = "导出文章归档数据")
    @Override
    public Object export(ArticleArchiveRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'export'");
    }

    

}
