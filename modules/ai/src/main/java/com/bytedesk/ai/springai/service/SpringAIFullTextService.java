/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 15:26:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 15:50:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.kbase.llm.qa.QaEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 全文检索服务
 * @author jackning
 */
@Slf4j
@Service
public class SpringAIFullTextService {
    
    private static final String QA_INDEX = "bytedesk_qa";
    
    @Autowired
    private RestHighLevelClient elasticsearchClient;
    
    /**
     * 初始化索引
     */
    public void initIndex() {
        try {
            // 检查索引是否存在
            boolean exists = elasticsearchClient.indices().exists(new GetIndexRequest(QA_INDEX), RequestOptions.DEFAULT);
            if (!exists) {
                // 创建索引
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(QA_INDEX);
                // 设置分片和复制
                createIndexRequest.settings(Settings.builder()
                        .put("index.number_of_shards", 3)
                        .put("index.number_of_replicas", 1)
                        .build());
                
                // 设置映射
                Map<String, Object> properties = new HashMap<>();
                
                // 问题字段 - 标准分词器
                Map<String, Object> question = new HashMap<>();
                question.put("type", "text");
                question.put("analyzer", "standard");
                question.put("search_analyzer", "standard");
                
                // 问题列表 - 标准分词器
                Map<String, Object> questionList = new HashMap<>();
                questionList.put("type", "text");
                questionList.put("analyzer", "standard");
                questionList.put("search_analyzer", "standard");
                
                // 答案 - 标准分词器
                Map<String, Object> answer = new HashMap<>();
                answer.put("type", "text");
                answer.put("analyzer", "standard");
                answer.put("search_analyzer", "standard");
                
                // 其他字段
                Map<String, Object> uid = new HashMap<>();
                uid.put("type", "keyword");
                
                Map<String, Object> kbUid = new HashMap<>();
                kbUid.put("type", "keyword");
                
                Map<String, Object> categoryUid = new HashMap<>();
                categoryUid.put("type", "keyword");
                
                Map<String, Object> orgUid = new HashMap<>();
                orgUid.put("type", "keyword");
                
                Map<String, Object> userUid = new HashMap<>();
                userUid.put("type", "keyword");
                
                Map<String, Object> enabled = new HashMap<>();
                enabled.put("type", "boolean");
                
                Map<String, Object> tagList = new HashMap<>();
                tagList.put("type", "keyword");
                
                Map<String, Object> startDate = new HashMap<>();
                startDate.put("type", "date");
                startDate.put("format", "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis");
                
                Map<String, Object> endDate = new HashMap<>();
                endDate.put("type", "date");
                endDate.put("format", "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis");
                
                Map<String, Object> createdAt = new HashMap<>();
                createdAt.put("type", "date");
                createdAt.put("format", "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis");
                
                // 添加所有属性
                properties.put("uid", uid);
                properties.put("question", question);
                properties.put("questionList", questionList);
                properties.put("answer", answer);
                properties.put("kbUid", kbUid);
                properties.put("categoryUid", categoryUid);
                properties.put("orgUid", orgUid);
                properties.put("userUid", userUid);
                properties.put("enabled", enabled);
                properties.put("tagList", tagList);
                properties.put("startDate", startDate);
                properties.put("endDate", endDate);
                properties.put("createdAt", createdAt);
                
                Map<String, Object> mapping = new HashMap<>();
                mapping.put("properties", properties);
                
                createIndexRequest.mapping(mapping);
                
                // 创建索引
                elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                log.info("创建索引 {} 成功", QA_INDEX);
            } else {
                log.info("索引 {} 已存在", QA_INDEX);
            }
        } catch (IOException e) {
            log.error("初始化索引失败: {}", e.getMessage());
        }
    }
    
    /**
     * 删除索引
     */
    public void deleteIndex() {
        try {
            boolean exists = elasticsearchClient.indices().exists(new GetIndexRequest(QA_INDEX), RequestOptions.DEFAULT);
            if (exists) {
                DeleteIndexRequest request = new DeleteIndexRequest(QA_INDEX);
                elasticsearchClient.indices().delete(request, RequestOptions.DEFAULT);
                log.info("索引 {} 已删除", QA_INDEX);
            }
        } catch (IOException e) {
            log.error("删除索引失败: {}", e.getMessage());
        }
    }
    
    /**
     * 索引单个QA实体
     */
    public void indexQa(QaEntity qa) {
        try {
            Map<String, Object> jsonMap = entityToMap(qa);
            
            IndexRequest indexRequest = new IndexRequest(QA_INDEX)
                    .id(qa.getUid())
                    .source(jsonMap);
            
            elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("成功索引QA: {}", qa.getQuestion());
        } catch (IOException e) {
            log.error("索引QA失败: {}", e.getMessage());
        }
    }
    
    /**
     * 批量索引QA实体
     */
    public void bulkIndexQas(List<QaEntity> qaList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            
            for (QaEntity qa : qaList) {
                Map<String, Object> jsonMap = entityToMap(qa);
                
                IndexRequest indexRequest = new IndexRequest(QA_INDEX)
                        .id(qa.getUid())
                        .source(jsonMap);
                
                bulkRequest.add(indexRequest);
            }
            
            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("批量索引部分失败: {}", bulkResponse.buildFailureMessage());
            } else {
                log.info("批量索引 {} 个QA成功", qaList.size());
            }
        } catch (IOException e) {
            log.error("批量索引失败: {}", e.getMessage());
        }
    }
    
    /**
     * 从索引中删除QA实体
     */
    public void deleteQa(String qaUid) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(QA_INDEX, qaUid);
            elasticsearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("成功从索引中删除QA: {}", qaUid);
        } catch (IOException e) {
            log.error("删除QA索引失败: {}", e.getMessage());
        }
    }
    
    /**
     * 批量删除QA实体
     */
    public void bulkDeleteQas(List<String> qaUids) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            
            for (String qaUid : qaUids) {
                DeleteRequest deleteRequest = new DeleteRequest(QA_INDEX, qaUid);
                bulkRequest.add(deleteRequest);
            }
            
            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("批量删除部分失败: {}", bulkResponse.buildFailureMessage());
            } else {
                log.info("批量删除 {} 个QA成功", qaUids.size());
            }
        } catch (IOException e) {
            log.error("批量删除失败: {}", e.getMessage());
        }
    }
    
    /**
     * 搜索QA
     * @param keyword 搜索关键词
     * @param kbUid 知识库UID (可选)
     * @param categoryUid 分类UID (可选)
     * @param orgUid 组织UID (可选)
     * @param tagList 标签列表 (可选)
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果列表
     */
    public List<QaEntity> searchQas(String keyword, String kbUid, String categoryUid, 
                                    String orgUid, List<String> tagList,
                                    int page, int size) {
        try {
            SearchRequest searchRequest = new SearchRequest(QA_INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            
            // 使用布尔查询构建复杂查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            
            // 关键词搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 多字段搜索，给予问题字段更高的权重
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "question^3", "questionList^2", "answer")
                        .fuzziness("AUTO"));  // 启用模糊匹配，处理拼写错误
            } else {
                // 如果没有关键词，匹配所有文档
                boolQuery.must(QueryBuilders.matchAllQuery());
            }
            
            // 过滤条件 - 知识库
            if (kbUid != null && !kbUid.trim().isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("kbUid", kbUid));
            }
            
            // 过滤条件 - 分类
            if (categoryUid != null && !categoryUid.trim().isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("categoryUid", categoryUid));
            }
            
            // 过滤条件 - 组织
            if (orgUid != null && !orgUid.trim().isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("orgUid", orgUid));
            }
            
            // 过滤条件 - 标签
            if (tagList != null && !tagList.isEmpty()) {
                for (String tag : tagList) {
                    boolQuery.filter(QueryBuilders.termQuery("tagList", tag));
                }
            }
            
            // 只显示启用的QA
            boolQuery.filter(QueryBuilders.termQuery("enabled", true));
            
            searchSourceBuilder.query(boolQuery);
            
            // 分页
            searchSourceBuilder.from((page - 1) * size);
            searchSourceBuilder.size(size);
            
            // 排序 - 按创建时间降序
            searchSourceBuilder.sort(SortBuilders.fieldSort("createdAt").order(SortOrder.DESC));
            
            searchRequest.source(searchSourceBuilder);
            
            SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            
            List<QaEntity> results = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                QaEntity qa = JSON.parseObject(hit.getSourceAsString(), QaEntity.class);
                results.add(qa);
            }
            
            log.info("搜索结果数: {}", results.size());
            return results;
            
        } catch (IOException e) {
            log.error("搜索QA失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 将QaEntity转换为Map用于索引
     */
    private Map<String, Object> entityToMap(QaEntity qa) {
        Map<String, Object> map = new HashMap<>();
        
        // 基本字段
        map.put("uid", qa.getUid());
        map.put("question", qa.getQuestion());
        map.put("answer", qa.getAnswer());
        
        // 处理问题列表
        if (qa.getQuestionList() != null && !qa.getQuestionList().isEmpty()) {
            map.put("questionList", qa.getQuestionList());
        }
        
        // 标签列表
        if (qa.getTagList() != null && !qa.getTagList().isEmpty()) {
            map.put("tagList", qa.getTagList());
        }
        
        // 其他元数据
        if (qa.getKbaseEntity() != null) {
            map.put("kbUid", qa.getKbaseEntity().getUid());
        }
        
        map.put("categoryUid", qa.getCategoryUid());
        map.put("orgUid", qa.getOrgUid());
        map.put("userUid", qa.getUserUid());
        map.put("enabled", qa.isEnabled());
        
        // 日期
        if (qa.getStartDate() != null) {
            map.put("startDate", qa.getStartDate().toString());
        }
        
        if (qa.getEndDate() != null) {
            map.put("endDate", qa.getEndDate().toString());
        }
        
        if (qa.getCreatedAt() != null) {
            map.put("createdAt", qa.getCreatedAt().toString());
        }
        
        return map;
    }
}
