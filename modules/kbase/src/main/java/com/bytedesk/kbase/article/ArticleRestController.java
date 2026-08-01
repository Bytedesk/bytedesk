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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.article.elastic.ArticleElasticSearchResult;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.article.vector.ArticleVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import com.bytedesk.core.annotation.ActionAnnotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;

@Tag(name = "Article Management", description = "Article management APIs")
@RestController
@RequestMapping("/api/v1/article")
@Description("Article Management Controller - Knowledge base article content management APIs")
public class ArticleRestController extends BaseRestController<ArticleRequest, ArticleRestService> {

    private final ArticleRestService articleRestService;

    private final ArticleElasticService articleElasticService;

    private final ArticleVectorService articleVectorService;

    public ArticleRestController(ArticleRestService articleRestService, ArticleElasticService articleElasticService,
            ObjectProvider<ArticleVectorService> articleVectorServiceProvider) {
        this.articleVectorService = articleVectorServiceProvider.getIfAvailable();
        this.articleRestService = articleRestService;
        this.articleElasticService = articleElasticService;
    }

    @Operation(summary = "Query Articles by Organization", description = "Query the list of articles by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_READ)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query article by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ArticleRequest request) {

        Page<ArticleResponse> page = articleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Articles by User", description = "Query the list of articles by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_READ)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query article by user")
    @Override
    @GetMapping({ "/query", "/query/user" })
    public ResponseEntity<?> queryByUser(ArticleRequest request) {
        
        Page<ArticleResponse> page = articleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Article by UID", description = "Query article details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_READ)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query article by uid")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ArticleRequest request) {
        
        ArticleResponse article = articleRestService.queryByUid(request);

        if (article == null) {
            return ResponseEntity.ok(JsonResult.error("not found"));
        }

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "Create Article", description = "Create a new article")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_CREATE, description = "create article")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "Update Article", description = "Update article information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE, description = "update article")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "Crawl Article Content", description = "Crawl webpage content by source URL for article editing")
    @ApiResponse(responseCode = "200", description = "Crawl successful",
        content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ArticleResponse.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE, description = "crawl article content")
    @PostMapping("/crawl")
    public ResponseEntity<?> crawlContent(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.crawlContent(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Operation(summary = "Delete Article", description = "Delete the specified article")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_DELETE, description = "delete article")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ArticleRequest request) {

        articleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "Export Articles", description = "Export article data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_EXPORT, description = "export article")
    @GetMapping("/export")
    @Override
    public Object export(ArticleRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            articleRestService,
            ArticleExcel.class,
            "Article",
            "Article"
        );
    }

    @Operation(summary = "Update Article Index", description = "Update the Elasticsearch index for the article")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE_INDEX, description = "update article index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    @Operation(summary = "Update Article Vector Index", description = "Update the vector index for the article")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE_VECTOR_INDEX, description = "update article vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ArticleRequest request) {

        if (articleVectorService != null) {
            articleVectorService.updateVectorIndex(request);
            return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
        } else {
            return ResponseEntity.ok(JsonResult.error("Vector service is not available"));
        }
    }

    @Operation(summary = "Update All Article Indexes", description = "Update Elasticsearch indexes for all articles")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE_ALL_INDEX, description = "update all article index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    @Operation(summary = "Update All Article Vector Indexes", description = "Update vector indexes for all articles")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_UPDATE_ALL_VECTOR_INDEX, description = "update all article vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ArticleRequest request) {

        if (articleVectorService != null) {
            articleVectorService.updateAllVectorIndex(request);
            return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
        } else {
            return ResponseEntity.ok(JsonResult.error("Vector service is not available"));
        }
    }

    @Operation(summary = "Search Articles", description = "Search articles with suggestion input")
    @ApiResponse(responseCode = "200", description = "Search successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ArticleElasticSearchResult.class)))
    @PreAuthorize(ArticlePermissions.HAS_ARTICLE_READ)
    @ActionAnnotation(title = I18Consts.I18N_ARTICLE, action = I18Consts.I18N_ACTION_SEARCH, description = "search article")
    @GetMapping("/search")
    public ResponseEntity<?> searchElastic(ArticleRequest request) {

        List<ArticleElasticSearchResult> suggestList = articleElasticService.searchArticle(request);

        return ResponseEntity.ok(JsonResult.success(suggestList));
    }
}
