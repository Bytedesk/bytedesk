/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 18:04:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.vector;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.quick_reply.QuickReplyEntity;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 快捷回复-向量检索服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickReplyVectorService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final QuickReplyRestService quickReplyRestService;

    /**
     * 更新向量索引
     */
    public void updateVectorIndex(QuickReplyEntity quickReply, float[] embedding) {
        try {
            // 检查文档是否已存在
            boolean exists = elasticsearchOperations.exists(quickReply.getUid(), QuickReplyVector.class);
            
            if (exists) {
                log.info("更新已存在的快捷回复向量索引: {}", quickReply.getUid());
            } else {
                log.info("为快捷回复创建新向量索引: {}", quickReply.getUid());
            }
            
            // 将QuickReplyEntity转换为QuickReplyVector对象
            QuickReplyVector quickReplyVector = QuickReplyVector.fromQuickReplyEntity(quickReply);
            quickReplyVector.setEmbedding(embedding);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(quickReplyVector);
            
            if (exists) {
                log.info("快捷回复向量索引更新成功: {}", quickReply.getUid());
            } else {
                log.info("快捷回复向量索引创建成功: {}", quickReply.getUid());
            }

        } catch (Exception e) {
            log.error("索引快捷回复向量时发生错误: {}, 错误消息: {}", quickReply.getUid(), e.getMessage(), e);
        }
    }

    /**
     * 新增重载方法，自动向量化（mock 示例）
     */
    public void updateVectorIndex(QuickReplyEntity quickReply) {
        // 生成一个 mock embedding，例如全零向量
        float[] embedding = new float[ 1536 ];
        for (int i = 0; i < embedding.length; i++) {
             embedding[i] = 0.01f * i; // 模拟递增向量
        }
        updateVectorIndex(quickReply, embedding);
    }

    /**
     * 从Elasticsearch中删除向量索引
     */
    public Boolean deleteVectorIndex(String uid) {
        log.info("从向量索引中删除快捷回复: {}", uid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(uid, QuickReplyVector.class);
            if (!exists) {
                log.warn("向量索引中不存在此快捷回复文档: {}", uid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 创建Query对象，用于查询要删除的文档
            Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.term().field("uid").value(uid).build()._toQuery())
                .build();
            
            // 创建DeleteQuery对象
            DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
            
            // 执行删除
            var response = elasticsearchOperations.delete(deleteQuery, QuickReplyVector.class);
            
            // 从响应中获取删除的文档数
            long deletedCount = response.getDeleted();
            
            // 检查删除结果
            if (deletedCount > 0) {
                log.info("成功删除快捷回复向量索引: {}, 删除文档数: {}", uid, deletedCount);
                return true;
            } else {
                log.warn("删除快捷回复向量索引操作没有匹配到文档: {}", uid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除快捷回复向量索引时发生错误: {}, 错误消息: {}", uid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向量检索
     */
    public Page<QuickReplyVectorSearchResult> search(float[] queryVector, String orgUid, String agentUid, Pageable pageable) {
        log.info("向量搜索快捷回复: orgUid={}, agentUid={}", orgUid, agentUid);
        
        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
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
        
        // 添加组织过滤条件
        if (StringUtils.hasText(orgUid)) {
            boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
        }
        
        // 添加客服过滤条件
        if (StringUtils.hasText(agentUid)) {
            boolQueryBuilder.filter(QueryBuilders.term().field("agentUid").value(agentUid).build()._toQuery());
        }
        
        // 构建向量查询
        List<Float> vectorList = new ArrayList<>();
        for (float value : queryVector) {
            vectorList.add(value);
        }
        
        // 构建KNN查询
        QueryVariant knnQuery = QueryBuilders.knn()
            .field("embedding")
            .queryVector(vectorList)
            .k(10)
            .numCandidates(100)
            .build();
        
        // 将KNN查询添加到bool查询中
        boolQueryBuilder.should(List.of(knnQuery._toQuery()));
        
        // 构建最终查询
        Query searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withPageable(pageable)
            .build();
        
        // 执行搜索
        SearchHits<QuickReplyVector> searchHits = elasticsearchOperations.search(searchQuery, QuickReplyVector.class);
        
        // 转换结果
        List<QuickReplyVectorSearchResult> results = searchHits.getSearchHits().stream()
                .map(this::convertToSearchResult)
                .toList();

        // 构建分页结果
        return new org.springframework.data.domain.PageImpl<>(
                results,
                pageable,
                searchHits.getTotalHits());
    }

    /**
     * 转换为搜索结果
     */
    private QuickReplyVectorSearchResult convertToSearchResult(SearchHit<QuickReplyVector> searchHit) {
        QuickReplyVector quickReplyVector = searchHit.getContent();
        return QuickReplyVectorSearchResult.builder()
                .uid(quickReplyVector.getUid())
                .title(quickReplyVector.getTitle())
                .content(quickReplyVector.getContent())
                .type(quickReplyVector.getType())
                .categoryUid(quickReplyVector.getCategoryUid())
                .kbUid(quickReplyVector.getKbUid())
                .agentUid(quickReplyVector.getAgentUid())
                .score(searchHit.getScore())
                .build();
    }
} 