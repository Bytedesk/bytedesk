/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-28 21:31:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 22:05:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.elastic;

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
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRequest;
import com.bytedesk.kbase.faq.FaqRestService;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 全文检索服务
 * 
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FaqElasticService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final FaqRestService faqRestService;

    // update elasticsearch index
    public void updateIndex(FaqRequest request) {
        Optional<FaqEntity> faqOpt = faqRestService.findByUid(request.getUid());
        if (faqOpt.isPresent()) {
            FaqEntity faq = faqOpt.get();
            indexFaq(faq);
        } else {
            throw new IllegalArgumentException("FAQ not found with UID: " + request.getUid());
        }
    }

    // update all elasticsearch index
    public void updateAllIndex(FaqRequest request) {
        List<FaqEntity> faqList = faqRestService.findByKbUid(request.getKbUid());
        faqList.forEach(faq -> {
            indexFaq(faq);
        });
    }

    /**
     * 索引FAQ实体到Elasticsearch
     * 
     * @param faq 要索引的FAQ实体
     */
    public void indexFaq(FaqEntity faq) {
        log.info("索引FAQ: uid={}, question={}", faq.getUid(), faq.getQuestion());

        try {
            // 首先检查索引是否存在，如果不存在则创建
            boolean indexExists = elasticsearchOperations.indexOps(FaqElastic.class).exists();
            if (!indexExists) {
                log.info("索引不存在: {}，正在创建...", FaqElastic.class
                        .getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName());
                // 创建索引
                boolean created = elasticsearchOperations.indexOps(FaqElastic.class).create();
                // 创建映射
                boolean mapped = elasticsearchOperations.indexOps(FaqElastic.class).putMapping();
                log.info("索引创建结果: {}, 映射创建结果: {}", created, mapped);

                if (!(created && mapped)) {
                    log.error("索引创建失败，无法继续索引文档: {}", faq.getUid());
                    return;
                }
            }
            
            // 转换为Elasticsearch文档
            FaqElastic faqElastic = FaqElastic.fromFaqEntity(faq);

            // 保存到Elasticsearch
            elasticsearchOperations.save(faqElastic);

            log.info("FAQ索引成功: uid={}", faq.getUid());
        } catch (Exception e) {
            log.error("FAQ索引失败: uid={}, error={}", faq.getUid(), e.getMessage(), e);
        }
    }

    /**
     * 从Elasticsearch中删除FAQ的索引
     * 
     * @param faqUid 要删除的FAQ的UID
     * @return 是否删除成功
     */
    public Boolean deleteFaq(String faqUid) {
        log.info("从索引中删除FAQ: {}", faqUid);

        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(faqUid, FaqElastic.class);
            if (!exists) {
                log.warn("索引中不存在此FAQ文档: {}", faqUid);
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
                log.info("成功删除FAQ索引: {}, 删除文档数: {}", faqUid, deletedCount);
                return true;
            } else {
                log.warn("删除FAQ索引操作没有匹配到文档: {}", faqUid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除FAQ索引时发生错误: {}, 错误消息: {}", faqUid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索FAQ内容 - 私有辅助方法，处理两种搜索场景的共同逻辑
     * 
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @param isSuggest 是否为输入联想模式
     * @param maxResults 最大结果数，为null则不限制
     * @return 搜索结果列表
     */
    private List<FaqElasticSearchResult> searchFaqInternal(
            String query, 
            String kbUid, 
            String categoryUid, 
            String orgUid, 
            boolean isSuggest,
            Integer maxResults) {
        
        if (!StringUtils.hasText(query)) {
            log.info("查询为空，直接返回空结果");
            return new ArrayList<>();
        }
        
        try {
            // 首先检查索引是否存在
            boolean indexExists = elasticsearchOperations.indexOps(FaqElastic.class).exists();
            if (!indexExists) {
                log.warn("索引不存在: {}，请先创建索引", FaqElastic.class
                        .getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName());
                return new ArrayList<>();
            }

            // 构建查询条件
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 根据搜索模式构建不同的查询
            try {
                if (isSuggest) {
                    // 输入联想模式 - 主要匹配问题字段
                    boolQueryBuilder.should(QueryBuilders.match()
                            .field("question")
                            .query(query)
                            .build()._toQuery());
                    
                    // 设置最小匹配数
                    boolQueryBuilder.minimumShouldMatch("1");
                    
                    log.debug("添加question字段的match查询: {}", query);
                } else {
                    // 完整搜索模式 - 在多个字段中搜索
                    MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
                            .query(query)
                            .fields("question^3", "answer", "similarQuestions^2", "tagList^1.5") // 字段权重
                            .build();
                    boolQueryBuilder.must(multiMatchQuery._toQuery());
                    
                    // 添加过滤条件：启用状态
                    boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
                }
            } catch (Exception e) {
                log.warn("添加查询条件时出错: {}", e.getMessage());
            }
            
            // 添加可选的过滤条件：知识库、分类、组织
            try {
                if (StringUtils.hasText(kbUid)) {
                    boolQueryBuilder.filter(QueryBuilders.term().field("kbUid").value(kbUid).build()._toQuery());
                    log.debug("添加kbUid过滤条件: {}", kbUid);
                }
                
                if (StringUtils.hasText(categoryUid) && !isSuggest) {
                    boolQueryBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
                    log.debug("添加categoryUid过滤条件: {}", categoryUid);
                }
                
                if (StringUtils.hasText(orgUid)) {
                    boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
                    log.debug("添加orgUid过滤条件: {}", orgUid);
                }
            } catch (Exception e) {
                log.warn("添加过滤条件时出错: {}", e.getMessage());
            }

            // 构建最终查询
            var searchQueryBuilder = NativeQuery.builder()
                    .withQuery(boolQueryBuilder.build()._toQuery());
            
            // 如果指定了最大结果数，则添加限制
            if (maxResults != null && maxResults > 0) {
                searchQueryBuilder.withMaxResults(maxResults);
            }
            
            NativeQuery searchQuery = searchQueryBuilder.build();
            log.debug("构建查询成功: {}", searchQuery);

            // 执行搜索
            SearchHits<FaqElastic> searchHits;
            try {
                searchHits = elasticsearchOperations.search(searchQuery, FaqElastic.class);
                log.debug("搜索完成，命中数: {}", searchHits.getTotalHits());
            } catch (Exception e) {
                log.error("执行Elasticsearch搜索失败: {}", e.getMessage(), e);
                if (e.getCause() != null) {
                    log.error("根本原因: {}", e.getCause().getMessage());
                }
                return new ArrayList<>();
            }

            // 处理结果
            List<FaqElasticSearchResult> resultList = new ArrayList<>();
            for (SearchHit<FaqElastic> hit : searchHits) {
                try {
                    FaqElastic faqElastic = hit.getContent();
                    if (faqElastic == null) {
                        log.warn("搜索结果包含空内容");
                        continue;
                    }
                    
                    float score = hit.getScore();
                    
                    // 创建结果对象
                    FaqElasticSearchResult result = FaqElasticSearchResult.builder()
                            .faqElastic(faqElastic)
                            .score(score)
                            .build();
                    
                    // 如果是输入联想模式，添加高亮
                    if (isSuggest) {
                        addHighlight(result, query, faqElastic.getQuestion());
                    }
                    
                    resultList.add(result);
                } catch (Exception e) {
                    log.warn("处理搜索结果时出错: {}", e.getMessage());
                }
            }
            
            return resultList;
        } catch (Exception e) {
            log.error("FAQ搜索失败: query={}, 错误类型={}, 错误消息={}", 
                    query, e.getClass().getName(), e.getMessage(), e);
            
            // 返回空列表而不是抛出异常，避免影响整个服务
            return new ArrayList<>();
        }
    }
    
    /**
     * 为搜索结果添加高亮
     */
    private void addHighlight(FaqElasticSearchResult result, String query, String question) {
        if (question != null && !question.trim().isEmpty() && query != null && !query.isEmpty()) {
            try {
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
            } catch (Exception e) {
                log.warn("处理高亮时出错: {}", e.getMessage());
                result.setHighlightedQuestion(question);
            }
        } else if (question != null) {
            result.setHighlightedQuestion(question);
        }
    }

    /**
     * 搜索FAQ内容
     * 
     * @param query       搜索关键词
     * @param kbUid       知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid      组织UID（可选）
     * @return 带权重的搜索结果列表和元数据
     */
    public List<FaqElasticSearchResult> searchFaq(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("全文搜索FAQ: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        return searchFaqInternal(query, kbUid, categoryUid, orgUid, false, null);
    }

    /**
     * 用户在输入过程中，给出输入联想
     */
    public List<FaqElasticSearchResult> suggestFaq(FaqRequest request) {
        log.info("FAQ输入联想: query={}, kbUid={}, categoryUid={}, orgUid={}",
                request.getQuestion(), request.getKbUid(), request.getCategoryUid(), request.getOrgUid());

        String query = request.getQuestion();
        String kbUid = request.getKbUid();
        String orgUid = request.getOrgUid();
        String categoryUid = request.getCategoryUid();
        
        List<FaqElasticSearchResult> results = searchFaqInternal(query, kbUid, categoryUid, orgUid, true, 10);
        log.info("FAQ输入联想成功，返回结果数: {}", results.size());
        return results;
    }

}
