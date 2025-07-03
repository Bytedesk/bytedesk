/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:54:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply.elastic;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
// import com.bytedesk.kbase.quick_reply.QuickReplyRestService;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 快捷回复-全文检索服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickReplyElasticService {

    private final ElasticsearchOperations elasticsearchOperations;
    // private final QuickReplyRestService quickReplyRestService;

    /**
     * 更新索引
     */
    public void updateIndex(QuickReplyEntity quickReply) {
        try {
            // 检查文档是否已存在
            boolean exists = elasticsearchOperations.exists(quickReply.getUid(), QuickReplyElastic.class);
            
            if (exists) {
                log.info("更新已存在的快捷回复索引: {}", quickReply.getUid());
            } else {
                log.info("为快捷回复创建新索引: {}", quickReply.getUid());
            }
            
            // 将QuickReplyEntity转换为QuickReplyElastic对象
            QuickReplyElastic quickReplyElastic = convertToElastic(quickReply);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(quickReplyElastic);
            
            if (exists) {
                log.info("快捷回复索引更新成功: {}", quickReply.getUid());
            } else {
                log.info("快捷回复索引创建成功: {}", quickReply.getUid());
            }

        } catch (Exception e) {
            log.error("索引快捷回复时发生错误: {}, 错误消息: {}", quickReply.getUid(), e.getMessage(), e);
        }
    }

    /**
     * 从Elasticsearch中删除索引
     */
    public Boolean deleteIndex(String uid) {
        log.info("从索引中删除快捷回复: {}", uid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(uid, QuickReplyElastic.class);
            if (!exists) {
                log.warn("索引中不存在此快捷回复文档: {}", uid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 创建Query对象，用于查询要删除的文档
            Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.term().field("uid").value(uid).build()._toQuery())
                .build();
            
            // 创建DeleteQuery对象
            DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
            
            // 执行删除
            var response = elasticsearchOperations.delete(deleteQuery, QuickReplyElastic.class);
            
            // 从响应中获取删除的文档数
            long deletedCount = response.getDeleted();
            
            // 检查删除结果
            if (deletedCount > 0) {
                log.info("成功删除快捷回复索引: {}, 删除文档数: {}", uid, deletedCount);
                return true;
            } else {
                log.warn("删除快捷回复索引操作没有匹配到文档: {}", uid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除快捷回复索引时发生错误: {}, 错误消息: {}", uid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 全文检索
     */
    public Page<QuickReplyElasticSearchResult> search(String keyword, String orgUid, String agentUid, Pageable pageable) {
        log.info("全文搜索快捷回复: keyword={}, orgUid={}, agentUid={}", keyword, orgUid, agentUid);
        
        // 构建查询条件
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        
        // 关键词查询 - 在title和content字段中搜索
        if (StringUtils.hasText(keyword)) {
            MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
                .query(keyword)
                .fields("title^3", "content", "tagList^1.5") // 字段权重
                .build();
            boolQueryBuilder.must(multiMatchQuery._toQuery());
        }
        
        // 添加过滤条件：启用状态
        boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
        
        // 添加日期范围过滤 - 将ZonedDateTime转换为ISO格式的字符串
        ZonedDateTime currentTime = ZonedDateTime.now();
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
        
        // 构建最终查询
        Query searchQuery = NativeQuery.builder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withPageable(pageable)
            .build();
        
        // 执行搜索
        SearchHits<QuickReplyElastic> searchHits = elasticsearchOperations.search(searchQuery, QuickReplyElastic.class);
        
        // 转换结果
        List<QuickReplyElasticSearchResult> results = searchHits.getSearchHits().stream()
                .map(this::convertToSearchResult)
                .toList();

        // 构建分页结果
        return new org.springframework.data.domain.PageImpl<>(
                results,
                pageable,
                searchHits.getTotalHits());
    }

    /**
     * 根据uid查询
     */
    public Optional<QuickReplyElastic> findByUid(String uid) {
        return Optional.ofNullable(elasticsearchOperations.get(uid, QuickReplyElastic.class));
    }

    /**
     * 转换为ES实体
     */
    private QuickReplyElastic convertToElastic(QuickReplyEntity quickReply) {
        return QuickReplyElastic.builder()
                .uid(quickReply.getUid())
                .title(quickReply.getTitle())
                .content(quickReply.getContent())
                .shortCut(quickReply.getShortCut())
                .tagList(quickReply.getTagList())
                .enabled(quickReply.getEnabled())
                .type(quickReply.getType())
                .clickCount(quickReply.getClickCount())
                .startDate(quickReply.getStartDate() != null ? quickReply.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .endDate(quickReply.getEndDate() != null ? quickReply.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .categoryUid(quickReply.getCategoryUid())
                .kbUid(quickReply.getKbUid())
                .agentUid(quickReply.getAgentUid())
                .orgUid(quickReply.getOrgUid())
                .platform(quickReply.getPlatform())
                .level(quickReply.getLevel())
                .build();
    }

    /**
     * 转换为搜索结果
     */
    private QuickReplyElasticSearchResult convertToSearchResult(SearchHit<QuickReplyElastic> searchHit) {
        QuickReplyElastic quickReplyElastic = searchHit.getContent();
        return QuickReplyElasticSearchResult.builder()
                .uid(quickReplyElastic.getUid())
                .title(quickReplyElastic.getTitle())
                .content(quickReplyElastic.getContent())
                .type(quickReplyElastic.getType())
                .categoryUid(quickReplyElastic.getCategoryUid())
                .kbUid(quickReplyElastic.getKbUid())
                .agentUid(quickReplyElastic.getAgentUid())
                .score(searchHit.getScore())
                .build();
    }
} 