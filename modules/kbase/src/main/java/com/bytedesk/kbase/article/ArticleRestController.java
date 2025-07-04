/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:33:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.article.elastic.ArticleElasticSearchResult;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.article.vector.ArticleVectorService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import com.bytedesk.core.annotation.ActionAnnotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "文章管理", description = "文章管理相关接口")
@RestController
@RequestMapping("/api/v1/article")
@AllArgsConstructor
public class ArticleRestController extends BaseRestController<ArticleRequest> {

    private final ArticleRestService articleRestService;

    private final ArticleElasticService articleElasticService;

    private final ArticleVectorService articleVectorService;

    @Operation(summary = "查询组织下的文章", description = "根据组织ID查询文章列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(ArticleRequest request) {

        Page<ArticleResponse> page = articleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的文章", description = "根据用户ID查询文章列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(ArticleRequest request) {
        
        Page<ArticleResponse> page = articleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定文章", description = "根据UID查询文章详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(ArticleRequest request) {
        
        ArticleResponse article = articleRestService.queryByUid(request);

        if (article == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "创建文章", description = "创建新的文章")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @ActionAnnotation(title = "文章", action = "新建", description = "create article")
    @Override
    public ResponseEntity<?> create(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.initVisitor(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "更新文章", description = "更新文章信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @ActionAnnotation(title = "文章", action = "更新", description = "update article")
    @Override
    public ResponseEntity<?> update(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "删除文章", description = "删除指定的文章")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "文章", action = "删除", description = "delete article")
    @Override
    public ResponseEntity<?> delete(@RequestBody ArticleRequest request) {

        articleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "导出文章", description = "导出文章数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @ActionAnnotation(title = "文章", action = "导出", description = "export article")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(ArticleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            articleRestService,
            ArticleExcel.class,
            "文章",
            "Article"
        );
    }

    @Operation(summary = "更新文章索引", description = "更新文章的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "文章", action = "更新索引", description = "update article index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    @Operation(summary = "更新文章向量索引", description = "更新文章的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "文章", action = "更新向量索引", description = "update article vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ArticleRequest request) {

        articleVectorService.updateVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    @Operation(summary = "更新所有文章索引", description = "更新所有文章的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "文章", action = "更新所有索引", description = "update all article index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    @Operation(summary = "更新所有文章向量索引", description = "更新所有文章的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "文章", action = "更新所有向量索引", description = "update all article vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ArticleRequest request) {

        articleVectorService.updateAllVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
    }

    @Operation(summary = "搜索文章", description = "输入联想搜索文章")
    @ApiResponse(responseCode = "200", description = "搜索成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleElasticSearchResult.class)))
    @GetMapping("/search")
    public ResponseEntity<?> searchElastic(ArticleRequest request) {

        List<ArticleElasticSearchResult> suggestList = articleElasticService.searchArticle(request);

        return ResponseEntity.ok(JsonResult.success(suggestList));
    }
}
