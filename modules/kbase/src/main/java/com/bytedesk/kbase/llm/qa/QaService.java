/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 15:26:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 09:48:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

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
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 全文检索服务
 * @author jackning
 */
@Service
@Slf4j
public class QaService {
        
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private QaRestService qaRestService;
    
    /**
     * 索引QA实体到Elasticsearch
     * @param qa 要索引的QA实体
     */
    public void indexQa(QaEntity qa) {
        log.info("为QA创建索引: {}", qa.getUid());
        
        try {
            // 将QaEntity转换为QaElastic对象
            QaElastic qaElastic = QaElastic.fromQaEntity(qa);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(qaElastic);
            log.info("QA索引成功: {}", qa.getUid());

            // 将索引结果保存到数据库中
            qa.setSuccess();
            qaRestService.save(qa);

        } catch (Exception e) {
            log.error("索引QA时发生错误: {}, 错误消息: {}", qa.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除QA的索引
     * @param qaUid 要删除的QA的UID
     */
    public void deleteQa(String qaUid) {
        log.info("从索引中删除QA: {}", qaUid);
        
        // 首先创建一个Query对象，用于查询要删除的文档
        Query query = NativeQuery.builder()
            .withQuery(QueryBuilders.term().field("uid").value(qaUid).build()._toQuery())
            .build();
        
        // 然后创建DeleteQuery对象，将Query作为参数传入
        DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
        
        elasticsearchOperations.delete(deleteQuery, QaElastic.class);
        log.info("成功删除QA索引: {}", qaUid);
    }
    
    /**
     * 搜索QA内容
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return 带权重的搜索结果列表和元数据
     */
    public List<QaElasticSearchResult> searchQa(String query, String kbUid, String categoryUid, String orgUid) {
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
        
        // 执行搜索 - 使用QaElastic类型
        SearchHits<QaElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            QaElastic.class
        );
        
        // 解析结果 - 返回带权重的QaElastic对象
        List<QaElasticSearchResult> qaElasticResultList = new ArrayList<>();
        for (SearchHit<QaElastic> hit : searchHits) {
            QaElastic qaElastic = hit.getContent();
            float score = hit.getScore();
            // 创建结果对象
            QaElasticSearchResult result = QaElasticSearchResult.builder()
                .qaElastic(qaElastic)
                .score(score)
                .build();
            qaElasticResultList.add(result);
        }
 
        return qaElasticResultList;
    }

    // 用户在输入过程中，给出输入联想
    public List<QaElasticSearchResult> suggestQa(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("QA输入联想: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
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
        
        // 使用NativeQuery而不是直接处理高亮
        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withFields("question", "questionList", "tagList") // 指定需要返回的字段
            .withMaxResults(10) // 限制结果数量
            .build();
        
        // 执行搜索，暂时不使用高亮功能
        SearchHits<QaElastic> searchHits = elasticsearchOperations.search(
            searchQuery, 
            QaElastic.class
        );
        
        // 处理结果，返回QaElasticSearchResult对象列表，并保留高亮功能
        List<QaElasticSearchResult> qaElasticResultList = new ArrayList<>();
        for (SearchHit<QaElastic> hit : searchHits) {
            QaElastic qaElastic = hit.getContent();
            float score = hit.getScore();
            
            // 创建结果对象
            QaElasticSearchResult result = QaElasticSearchResult.builder()
                .qaElastic(qaElastic)
                .score(score)
                .build();
            
            // 手动添加高亮
            String question = qaElastic.getQuestion();
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
            
            qaElasticResultList.add(result);
        }
        
        return qaElasticResultList;
    }

}
