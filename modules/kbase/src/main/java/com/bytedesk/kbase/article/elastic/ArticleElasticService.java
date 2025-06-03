/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 16:20:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 15:59:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article.elastic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;

import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleRequest;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.article.ArticleStatusEnum;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 全文检索服务
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleElasticService {
        
    private final ElasticsearchOperations elasticsearchOperations;

    private final ArticleRestService articleRestService;

    // update elasticsearch index
    public void updateIndex(ArticleRequest request) {
        Optional<ArticleEntity> articleOpt = articleRestService.findByUid(request.getUid());
        if (articleOpt.isPresent()) {
            ArticleEntity article = articleOpt.get();
            indexArticle(article);
        } else {
            throw new IllegalArgumentException("Article not found with UID: " + request.getUid());
        }
    }

    // update all elasticsearch index
    public void updateAllIndex(ArticleRequest request) {
        List<ArticleEntity> articleList = articleRestService.findByKbUid(request.getKbUid());
        articleList.forEach(article -> {
            indexArticle(article); 
        });
    }
    
    /**
     * 索引文章实体到Elasticsearch
     * @param article 要索引的文章实体
     */
    public void indexArticle(ArticleEntity article) {
        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(article.getUid(), ArticleElastic.class);
        
        if (exists) {
            log.info("更新已存在的文章索引: {}", article.getUid());
        } else {
            log.info("为文章创建新索引: {}", article.getUid());
        }
        
        try {
            // 将ArticleEntity转换为ArticleElastic对象
            ArticleElastic articleElastic = ArticleElastic.fromArticleEntity(article);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(articleElastic);
            
            if (exists) {
                log.info("文章索引更新成功: {}", article.getUid());
            } else {
                log.info("文章索引创建成功: {}", article.getUid());
            }

            // 文章可能有相关属性需要更新，如索引成功标记等
            article.setElasticSuccess();
            articleRestService.save(article);

        } catch (Exception e) {
            log.error("索引文章时发生错误: {}, 错误消息: {}", article.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除文章的索引
     * @param articleUid 要删除的文章的UID
     * @return 是否删除成功
     */
    public Boolean deleteArticle(String articleUid) {
        log.info("从索引中删除文章: {}", articleUid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(articleUid, ArticleElastic.class);
            if (!exists) {
                log.warn("索引中不存在此文章文档: {}", articleUid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 创建Query对象，用于查询要删除的文档
            Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.term().field("uid").value(articleUid).build()._toQuery())
                .build();
            
            // 创建DeleteQuery对象
            DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
            
            // 执行删除 - 返回 ByQueryResponse 而不是 long
            var response = elasticsearchOperations.delete(deleteQuery, ArticleElastic.class);
            
            // 从响应中获取删除的文档数
            long deletedCount = response.getDeleted();
            
            // 检查删除结果
            if (deletedCount > 0) {
                log.info("成功删除文章索引: {}, 删除文档数: {}", articleUid, deletedCount);
                return true;
            } else {
                log.warn("删除文章索引操作没有匹配到文档: {}", articleUid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除文章索引时发生错误: {}, 错误消息: {}", articleUid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 搜索文章内容
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return 带权重的搜索结果列表和元数据
     */
    public List<ArticleElasticSearchResult> searchArticle(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("全文搜索文章: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 关键词查询 - 在title、summary和content字段中搜索
        MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
            .query(query)
            .fields("title^3", "summary^2", "contentMarkdown", "contentHtml", "tagList^1.5") // 字段权重
            .build();
        boolQueryBuilder.must(multiMatchQuery._toQuery());
        
        // 添加过滤条件：发布状态
        boolQueryBuilder.filter(QueryBuilders.term().field("published").value(true).build()._toQuery());
        
        // 添加日期范围过滤 - 将LocalDateTime转换为ISO格式的字符串
        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeStr = currentTime.format(DateTimeFormatter.ISO_DATE_TIME);
        
        // 创建startDate范围查询
        DateRangeQuery startDateQuery = new DateRangeQuery.Builder()
            .field("startDate")
            .lte(currentTimeStr)
            .build();
        boolQueryBuilder.filter(QueryBuilders.range().date(startDateQuery).build()._toQuery());
        
        // 创建endDate范围查询
        DateRangeQuery endDateQuery = new DateRangeQuery.Builder()
            .field("endDate")
            .gte(currentTimeStr)
            .build();
        boolQueryBuilder.filter(QueryBuilders.range().date(endDateQuery).build()._toQuery());
        
        // 添加可选的过滤条件：知识库、分类、组织
        if (kbUid != null && !kbUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery());
        }
        
        if (categoryUid != null && !categoryUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
        }
        
        if (orgUid != null && !orgUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
        }
        
        // 构建最终查询
        Query searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .build();
        
        // 执行搜索 - 使用ArticleElastic类型
        SearchHits<ArticleElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            ArticleElastic.class
        );
        
        // 解析结果 - 返回带权重的ArticleElastic对象
        List<ArticleElasticSearchResult> articleElasticResultList = new ArrayList<>();
        for (SearchHit<ArticleElastic> hit : searchHits) {
            ArticleElastic articleElastic = hit.getContent();
            float score = hit.getScore();
            // 创建结果对象
            ArticleElasticSearchResult result = ArticleElasticSearchResult.builder()
                .articleElastic(articleElastic)
                .score(score)
                .build();
                
            // 添加简单的高亮处理
            String title = articleElastic.getTitle();
            if (title != null && !title.trim().isEmpty() && query != null && !query.isEmpty()) {
                // 在包含查询词的部分手动添加高亮标签
                if (title.toLowerCase().contains(query.toLowerCase())) {
                    int startIdx = title.toLowerCase().indexOf(query.toLowerCase());
                    int endIdx = startIdx + query.length();
                    String highlightedTitle = title.substring(0, startIdx) + 
                                "<em>" + title.substring(startIdx, endIdx) + "</em>" +
                                title.substring(endIdx);
                    result.setHighlightedTitle(highlightedTitle);
                } else {
                    result.setHighlightedTitle(title);
                }
            } else if (title != null) {
                result.setHighlightedTitle(title);
            }

            // 同样处理摘要的高亮
            String summary = articleElastic.getSummary();
            if (summary != null && !summary.trim().isEmpty() && query != null && !query.isEmpty()) {
                if (summary.toLowerCase().contains(query.toLowerCase())) {
                    int startIdx = summary.toLowerCase().indexOf(query.toLowerCase());
                    int endIdx = startIdx + query.length();
                    String highlightedSummary = summary.substring(0, startIdx) + 
                                "<em>" + summary.substring(startIdx, endIdx) + "</em>" +
                                summary.substring(endIdx);
                    result.setHighlightedSummary(highlightedSummary);
                } else {
                    result.setHighlightedSummary(summary);
                }
            } else if (summary != null) {
                result.setHighlightedSummary(summary);
            }
            
            articleElasticResultList.add(result);
        }
 
        return articleElasticResultList;
    }

    /**
     * 搜索相似文章
     * @param uid 文章UID
     * @param kbUid 知识库UID
     * @param limit 限制返回数量
     * @return 相似文章列表
     */
    public List<ArticleElasticSearchResult> findSimilarArticles(String uid, String kbUid, int limit) {
        // 首先获取当前文章
        Optional<ArticleEntity> articleOpt = articleRestService.findByUid(uid);
        if (articleOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        ArticleEntity article = articleOpt.get();
        String title = article.getTitle();
        String summary = article.getSummary();
        
        // 构建多字段查询
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 匹配相似的标题和摘要，但排除当前文章
        MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
            .query(title + " " + summary)
            .fields("title^3", "summary^2", "contentMarkdown", "tagList^1.5")
            .build();
        boolQueryBuilder.should(multiMatchQuery._toQuery());
        
        // 排除当前文章
        boolQueryBuilder.mustNot(QueryBuilders.term().field("uid").value(uid).build()._toQuery());
        
        // 只搜索已发布的文章
        boolQueryBuilder.filter(QueryBuilders.term().field("published").value(true).build()._toQuery());
        
        // 限制在同一知识库内
        if (kbUid != null && !kbUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery());
        }
        
        // 构建最终查询
        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withMaxResults(limit) // 限制结果数量
            .build();
        
        // 执行搜索
        SearchHits<ArticleElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            ArticleElastic.class
        );
        
        // 解析结果
        List<ArticleElasticSearchResult> result = new ArrayList<>();
        for (SearchHit<ArticleElastic> hit : searchHits) {
            ArticleElastic articleElastic = hit.getContent();
            float score = hit.getScore();
            
            result.add(ArticleElasticSearchResult.builder()
                .articleElastic(articleElastic)
                .score(score)
                .build());
        }
        
        return result;
    }
}
