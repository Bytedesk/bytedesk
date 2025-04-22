/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 15:26:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 16:47:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;

import com.bytedesk.kbase.llm.qa.QaEntity;

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
public class SpringAIFullTextService {
    
    private static final String QA_INDEX = "qa_index";
    
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    
    /**
     * 索引QA实体到Elasticsearch
     * @param qa 要索引的QA实体
     */
    public void indexQa(QaEntity qa) {
        log.info("为QA创建索引: {}", qa.getUid());
        
        // 获取知识库UID，安全处理可能的空值
        String kbUid = "";
        if (qa.getKbaseEntity() != null) {
            kbUid = qa.getKbaseEntity().getUid();
        }
        
        // 处理分类UID，避免可能的空指针
        String categoryUid = qa.getCategoryUid() != null ? qa.getCategoryUid() : "";
        
        // 安全处理集合，确保不为null
        List<String> questionList = qa.getQuestionList() != null ? qa.getQuestionList() : new ArrayList<>();
        List<String> tagList = qa.getTagList() != null ? qa.getTagList() : new ArrayList<>();
        
        // 创建索引文档 - 使用安全的值构建文档
        // 避免复杂对象可能导致的序列化问题
        Map<String, Object> document = Map.of(
            "uid", qa.getUid() != null ? qa.getUid() : "",
            "question", qa.getQuestion() != null ? qa.getQuestion() : "",
            "answer", qa.getAnswer() != null ? qa.getAnswer() : "",
            "questionList", questionList,
            "tagList", tagList,
            "orgUid", qa.getOrgUid() != null ? qa.getOrgUid() : "",
            "kbUid", kbUid,
            "categoryUid", categoryUid,
            "enabled", qa.isEnabled(),
            "createdAt", qa.getCreatedAt() != null ? qa.getCreatedAt().toString() : "",
            "updatedAt", qa.getUpdatedAt() != null ? qa.getUpdatedAt().toString() : ""
        );
        
        try {
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(document, IndexCoordinates.of(QA_INDEX));
            log.info("QA索引成功: {}", qa.getUid());
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
        
        elasticsearchOperations.delete(deleteQuery, Map.class, IndexCoordinates.of(QA_INDEX));
        log.info("成功删除QA索引: {}", qaUid);
    }
    
    /**
     * 搜索QA内容
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return 搜索结果列表
     */
    public List<String> searchQa(String query, String kbUid, String categoryUid, String orgUid) {
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
        
        // 执行搜索 - 使用原始的通配符类型，避免类型不匹配问题
        SearchHits<?> searchHits = elasticsearchOperations.search(
            searchQuery, 
            Map.class, 
            IndexCoordinates.of(QA_INDEX)
        );
        
        // 解析结果 - 使用安全类型转换
        List<String> qaUidList = new ArrayList<>();
        for (SearchHit<?> hit : searchHits) {
            Object content = hit.getContent();
            if (content instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> hitMap = (Map<String, Object>) content;
                Object uidObj = hitMap.get("uid");
                if (uidObj instanceof String) {
                    qaUidList.add((String) uidObj);
                }
            }
        }
        
        log.info("搜索到 {} 个QA结果", qaUidList.size());
        return qaUidList;
    }
}
