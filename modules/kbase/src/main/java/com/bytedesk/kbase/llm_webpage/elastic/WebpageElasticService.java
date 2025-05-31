/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 09:25:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 09:25:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.elastic;

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

import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRequest;
import com.bytedesk.kbase.llm_webpage.WebpageRestService;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Elasticsearch 全文检索服务（网页搜索）
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebpageElasticService {

    private final ElasticsearchOperations elasticsearchOperations;
    
    private final WebpageRestService webpageRestService;

    /**
     * 索引网页实体到Elasticsearch
     * @param webpage 要索引的网页实体
     */
    public void indexWebpage(WebpageEntity webpage) {
        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(webpage.getUid(), WebpageElastic.class);
        
        if (exists) {
            log.info("更新已存在的网页索引: {}", webpage.getUid());
        } else {
            log.info("为网页创建新索引: {}", webpage.getUid());
        }
        
        try {
            // 将WebpageEntity转换为WebpageElastic对象
            WebpageElastic webpageElastic = WebpageElastic.fromWebpageEntity(webpage);
            
            // 保存到Elasticsearch
            elasticsearchOperations.save(webpageElastic);
            
            log.info("成功索引网页: {}", webpage.getTitle());
        } catch (Exception e) {
            log.error("索引网页时发生错误: {}, 错误消息: {}", webpage.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除网页索引
     * @param webpageUid 网页UID
     */
    public void deleteWebpageIndex(String webpageUid) {
        log.info("从ES删除网页索引: {}", webpageUid);
        try {
            elasticsearchOperations.delete(webpageUid, WebpageElastic.class);
            log.info("成功删除网页索引: {}", webpageUid);
        } catch (Exception e) {
            log.error("删除网页索引时发生错误: {}, 错误消息: {}", webpageUid, e.getMessage(), e);
        }
    }
    
    /**
     * 批量删除指定知识库的所有网页索引
     * @param kbUid 知识库UID
     */
    public void deleteWebpageIndexByKbUid(String kbUid) {
        log.info("批量删除知识库下所有网页索引: {}", kbUid);
        try {
            // 创建删除查询
            DeleteQuery deleteQuery = DeleteQuery.builder()
                .withQuery(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery())
                .build();
            
            // 执行删除
            elasticsearchOperations.delete(deleteQuery, WebpageElastic.class);
            
            log.info("成功批量删除知识库下所有网页索引: {}", kbUid);
        } catch (Exception e) {
            log.error("批量删除知识库下所有网页索引时发生错误: {}, 错误消息: {}", kbUid, e.getMessage(), e);
        }
    }
    
    /**
     * 全文搜索网页
     * @param query 搜索查询
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return 搜索结果列表
     */
    public List<WebpageElasticSearchResult> searchWebpage(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("网页全文搜索: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 构建布尔查询
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 添加多字段匹配查询（标题、内容、描述、标签）
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
            .fields("title^3", "description^2", "content", "tagList")
            .query(query)
            .fuzziness("AUTO")
            .build();
        
        boolQueryBuilder.must(multiMatchQuery._toQuery());
        
        // 添加过滤条件：启用状态
        boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
        
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
        
        // 执行搜索
        SearchHits<WebpageElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            WebpageElastic.class
        );
        
        // 解析结果 - 返回带权重的WebpageElastic对象
        List<WebpageElasticSearchResult> webpageElasticResultList = new ArrayList<>();
        for (SearchHit<WebpageElastic> hit : searchHits) {
            WebpageElastic webpageElastic = hit.getContent();
            float score = hit.getScore();
            
            // 创建结果对象
            WebpageElasticSearchResult result = WebpageElasticSearchResult.builder()
                .webpageElastic(webpageElastic)
                .score(score)
                .build();
                
            // 设置基本高亮（简单实现，可根据需要进一步优化）
            applySimpleHighlighting(result, query);
            
            webpageElasticResultList.add(result);
        }
        
        return webpageElasticResultList;
    }
    
    /**
     * 网页输入联想
     * @param request 网页请求参数
     * @return 联想结果列表
     */
    public List<WebpageElasticSearchResult> suggestWebpage(WebpageRequest request) {
        log.info("网页输入联想: query={}, kbUid={}, categoryUid={}, orgUid={}", 
            request.getTitle(), request.getKbUid(), request.getCategoryUid(), request.getOrgUid());
        
        String query = request.getTitle();
        String kbUid = request.getKbUid();
        String categoryUid = request.getCategoryUid();
        String orgUid = request.getOrgUid();
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 添加前缀匹配查询，主要针对标题字段
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("title")
            .query(query)
            .boost(3.0f)
            .build()._toQuery());
            
        // 在描述中查找
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("description")
            .query(query)
            .boost(2.0f)
            .build()._toQuery());
            
        // 在内容中查找
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("content")
            .query(query)
            .boost(1.0f)
            .build()._toQuery());
        
        // 在标签中查找
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("tagList")
            .query(query)
            .boost(1.0f)
            .build()._toQuery());
        
        // 添加过滤条件：启用状态
        boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
        
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
        
        // 使用NativeQuery
        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withFields("title", "description", "tagList") // 指定需要返回的字段
            .withMaxResults(10) // 限制结果数量
            .build();
        
        // 执行搜索
        SearchHits<WebpageElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            WebpageElastic.class
        );
        
        // 处理结果，返回WebpageElasticSearchResult对象列表
        List<WebpageElasticSearchResult> webpageElasticResultList = new ArrayList<>();
        for (SearchHit<WebpageElastic> hit : searchHits) {
            WebpageElastic webpageElastic = hit.getContent();
            float score = hit.getScore();
            
            // 创建结果对象
            WebpageElasticSearchResult result = WebpageElasticSearchResult.builder()
                .webpageElastic(webpageElastic)
                .score(score)
                .build();
            
            // 手动添加高亮
            applySimpleHighlighting(result, query);
            
            webpageElasticResultList.add(result);
        }
        
        return webpageElasticResultList;
    }
    
    /**
     * 简单地为搜索结果添加高亮标记
     * @param result 搜索结果
     * @param query 搜索查询
     */
    private void applySimpleHighlighting(WebpageElasticSearchResult result, String query) {
        WebpageElastic webpageElastic = result.getWebpageElastic();
        
        // 高亮标题
        String title = webpageElastic.getTitle();
        if (title != null && !title.trim().isEmpty() && query != null && !query.isEmpty()) {
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
        
        // 高亮描述
        String description = webpageElastic.getDescription();
        if (description != null && !description.trim().isEmpty() && query != null && !query.isEmpty()) {
            if (description.toLowerCase().contains(query.toLowerCase())) {
                int startIdx = description.toLowerCase().indexOf(query.toLowerCase());
                int endIdx = startIdx + query.length();
                String highlightedDescription = description.substring(0, startIdx) + 
                              "<em>" + description.substring(startIdx, endIdx) + "</em>" +
                              description.substring(endIdx);
                result.setHighlightedDescription(highlightedDescription);
            } else {
                result.setHighlightedDescription(description);
            }
        } else if (description != null) {
            result.setHighlightedDescription(description);
        }
        
        // 高亮内容（仅展示部分内容）
        String content = webpageElastic.getContent();
        if (content != null && !content.trim().isEmpty() && query != null && !query.isEmpty()) {
            if (content.toLowerCase().contains(query.toLowerCase())) {
                int startIdx = content.toLowerCase().indexOf(query.toLowerCase());
                int previewStart = Math.max(0, startIdx - 50);
                int previewEnd = Math.min(content.length(), startIdx + query.length() + 50);
                
                String preview = (previewStart > 0 ? "..." : "") + 
                                content.substring(previewStart, startIdx) + 
                                "<em>" + content.substring(startIdx, startIdx + query.length()) + "</em>" + 
                                content.substring(startIdx + query.length(), previewEnd) + 
                                (previewEnd < content.length() ? "..." : "");
                
                result.setHighlightedContent(preview);
            } else {
                // 没有匹配时显示内容前100个字符作为摘要
                int previewLength = Math.min(100, content.length());
                String preview = content.substring(0, previewLength) + (content.length() > previewLength ? "..." : "");
                result.setHighlightedContent(preview);
            }
        }
    }
}
