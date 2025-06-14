/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 16:08:46
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

@RestController
@RequestMapping("/api/v1/article")
@AllArgsConstructor
public class ArticleRestController extends BaseRestController<ArticleRequest> {

    private final ArticleRestService articleRestService;

    private final ArticleElasticService articleElasticService;

    private final ArticleVectorService articleVectorService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(ArticleRequest request) {

        Page<ArticleResponse> page = articleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(ArticleRequest request) {
        
        Page<ArticleResponse> page = articleRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(ArticleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // query detail
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @GetMapping("/query/detail")
    public ResponseEntity<?> queryDetail(ArticleRequest request) {

        ArticleResponse article = articleRestService.queryDetail(request);
        if (article == null) {
            return ResponseEntity.ok(JsonResult.error("article not found"));
        }

        return ResponseEntity.ok(JsonResult.success(article));
    }

    // @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    // @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    // @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(@RequestBody ArticleRequest request) {

        articleRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

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

    // update elasticsearch index
    @ActionAnnotation(title = "文章", action = "更新索引", description = "update article index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = "文章", action = "更新向量索引", description = "update article vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ArticleRequest request) {

        articleVectorService.updateVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // update all elasticsearch index
    @ActionAnnotation(title = "文章", action = "更新所有索引", description = "update all article index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ArticleRequest request) {

        articleElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    // update all elasticsearch vector index
    @ActionAnnotation(title = "文章", action = "更新所有向量索引", description = "update all article vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ArticleRequest request) {

        articleVectorService.updateAllVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
    }

    // 输入联想搜索faq
    @GetMapping("/search")
    public ResponseEntity<?> searchElastic(ArticleRequest request) {

        List<ArticleElasticSearchResult> suggestList = articleElasticService.searchArticle(request);

        return ResponseEntity.ok(JsonResult.success(suggestList));
    }
}
