/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-28 21:31:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-28 21:33:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 全文检索服务
 * @author jackning
 */
@Service
@Slf4j
public class FaqService {
        
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private FaqRestService faqRestService;
    
    /**
     * 索引QA实体到Elasticsearch
     * @param faq 要索引的QA实体
     */
    public void indexFaq(FaqEntity faq) {
        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(faq.getUid(), FaqElastic.class);
        
        if (exists) {
            log.info("更新已存在的QA索引: {}", faq.getUid());
        } else {
            log.info("为QA创建新索引: {}", faq.getUid());
        }
        
        try {
            // 将FaqEntity转换为FaqElastic对象
            FaqElastic faqElastic = FaqElastic.fromFaqEntity(faq);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(faqElastic);
            
            if (exists) {
                log.info("QA索引更新成功: {}", faq.getUid());
            } else {
                log.info("QA索引创建成功: {}", faq.getUid());
            }

            // 将索引结果保存到数据库中
            faq.setSuccess();
            faqRestService.save(faq);

        } catch (Exception e) {
            log.error("索引QA时发生错误: {}, 错误消息: {}", faq.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除QA的索引
     * @param faqUid 要删除的QA的UID
     * @return 是否删除成功
     */
    public boolean deleteFaq(String faqUid) {
        log.info("从索引中删除QA: {}", faqUid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(faqUid, FaqElastic.class);
            if (!exists) {
                log.warn("索引中不存在此QA文档: {}", faqUid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 创建Query对象，用于查询要删除的文档
            Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.term().field("uid").value(faqUid).build()._toQuery())
                .build();
            
            // 创建DeleteQuery对象
            DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
            
            // 执行删除 - 返回 ByQueryResponse 而不是 long
            var response = elasticsearchOperations.delete(deleteQuery, FaqElastic.class);
            
            // 从响应中获取删除的文档数
            long deletedCount = response.getDeleted();
            
            // 检查删除结果
            if (deletedCount > 0) {
                log.info("成功删除QA索引: {}, 删除文档数: {}", faqUid, deletedCount);
                return true;
            } else {
                log.warn("删除QA索引操作没有匹配到文档: {}", faqUid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除QA索引时发生错误: {}, 错误消息: {}", faqUid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 搜索QA内容
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return 带权重的搜索结果列表和元数据
     */
    public List<FaqElasticSearchResult> searchFaq(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("全文搜索QA: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 关键词查询 - 在question和answer字段中搜索
        MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
            .query(query)
            .fields("question^3", "answer", "questionList^2", "tagList^1.5") // 字段权重
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
        
        // 执行搜索 - 使用FaqElastic类型
        SearchHits<FaqElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            FaqElastic.class
        );
        
        // 解析结果 - 返回带权重的FaqElastic对象
        List<FaqElasticSearchResult> faqElasticResultList = new ArrayList<>();
        for (SearchHit<FaqElastic> hit : searchHits) {
            FaqElastic faqElastic = hit.getContent();
            float score = hit.getScore();
            // 创建结果对象
            FaqElasticSearchResult result = FaqElasticSearchResult.builder()
                .faqElastic(faqElastic)
                .score(score)
                .build();
            faqElasticResultList.add(result);
        }
 
        return faqElasticResultList;
    }

    // 用户在输入过程中，给出输入联想
    public List<FaqElasticSearchResult> suggestFaq(FaqRequest request) {
        log.info("QA输入联想: query={}, kbUid={}, categoryUid={}, orgUid={}", 
            request.getQuestion(), request.getKbUid(), request.getCategoryUid(), request.getOrgUid());
        
        String query = request.getQuestion();
        // String kbUid = request.getKbUid();
        String categoryUid = request.getCategoryUid();
        String orgUid = request.getOrgUid();
        // 
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 添加前缀匹配查询，主要针对问题字段
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("question")
            .query(query)
            .boost(3.0f)
            .build()._toQuery());
            
        // 也在问题列表中查找
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("questionList")
            .query(query)
            .boost(2.0f)
            .build()._toQuery());
            
        // 在标签中查找
        boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
            .field("tagList")
            .query(query)
            .boost(1.0f)
            .build()._toQuery());
        
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
        // if (kbUid != null && !kbUid.isEmpty()) {
        //     boolQueryBuilder.filter(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery());
        // }
        
        if (categoryUid != null && !categoryUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
        }
        
        if (orgUid != null && !orgUid.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
        }
        
        // 使用NativeQuery而不是直接处理高亮
        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withFields("question", "questionList", "tagList") // 指定需要返回的字段
            .withMaxResults(10) // 限制结果数量
            .build();
        
        // 执行搜索，暂时不使用高亮功能
        SearchHits<FaqElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            FaqElastic.class
        );
        
        // 处理结果，返回FaqElasticSearchResult对象列表，并保留高亮功能
        List<FaqElasticSearchResult> faqElasticResultList = new ArrayList<>();
        for (SearchHit<FaqElastic> hit : searchHits) {
            FaqElastic faqElastic = hit.getContent();
            float score = hit.getScore();
            
            // 创建结果对象
            FaqElasticSearchResult result = FaqElasticSearchResult.builder()
                .faqElastic(faqElastic)
                .score(score)
                .build();
            
            // 手动添加高亮
            String question = faqElastic.getQuestion();
            if (question != null && !question.trim().isEmpty() && query != null && !query.isEmpty()) {
                // 在包含查询词的部分手动添加高亮标签
                if (question.toLowerCase().contains(query.toLowerCase())) {
                    int startIdx = question.toLowerCase().indexOf(query.toLowerCase());
                    int endIdx = startIdx + query.length();
                    String highlightedQuestion = question.substring(0, startIdx) + 
                                  "<em>" + question.substring(startIdx, endIdx) + "</em>" +
                                  question.substring(endIdx);
                    result.setHighlightedQuestion(highlightedQuestion);
                } else {
                    result.setHighlightedQuestion(question);
                }
            } else if (question != null) {
                result.setHighlightedQuestion(question);
            }
            
            faqElasticResultList.add(result);
        }
        
        return faqElasticResultList;
    }

}
