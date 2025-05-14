/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:16:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 16:19:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class ChunkElasticService {
        
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ChunkRestService chunkRestService;
    
    // update elasticsearch index
    public void updateIndex(ChunkRequest request) {
        Optional<ChunkEntity> chunkOpt = chunkRestService.findByUid(request.getUid());
        if (chunkOpt.isPresent()) {
            ChunkEntity chunk = chunkOpt.get();
            indexChunk(chunk);
        } else {
            throw new IllegalArgumentException("Chunk not found with UID: " + request.getUid());
        }
    }

    

    // update all elasticsearch index
    public void updateAllIndex(ChunkRequest request) {
        List<ChunkEntity> chunkList = chunkRestService.findByKbUid(request.getKbUid());
        chunkList.forEach(chunk -> {
            indexChunk(chunk);
        });
        log.info("Updated elasticsearch index for {} chunks from knowledge base: {}", chunkList.size(), request.getKbUid());
    }

    
    /**
     * 索引Chunk实体到Elasticsearch
     * @param chunk 要索引的Chunk实体
     */
    public void indexChunk(ChunkEntity chunk) {
        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(chunk.getUid(), ChunkElastic.class);
        
        if (exists) {
            log.info("更新已存在的Chunk索引: {}", chunk.getUid());
        } else {
            log.info("为Chunk创建新索引: {}", chunk.getUid());
        }
        
        try {
            // 将ChunkEntity转换为ChunkElastic对象
            ChunkElastic chunkElastic = ChunkEntityElasticConverter.toElastic(chunk);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(chunkElastic);
            
            if (exists) {
                log.info("Chunk索引更新成功: {}", chunk.getUid());
            } else {
                log.info("Chunk索引创建成功: {}", chunk.getUid());
            }

            // 将索引结果保存到数据库中
            chunk.setSuccess();
            chunkRestService.save(chunk);

        } catch (Exception e) {
            log.error("索引Chunk时发生错误: {}, 错误消息: {}", chunk.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除Chunk的索引
     * @param chunkUid 要删除的Chunk的UID
     * @return 是否删除成功
     */
    public boolean deleteChunk(String chunkUid) {
        log.info("从索引中删除Chunk: {}", chunkUid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(chunkUid, ChunkElastic.class);
            if (!exists) {
                log.warn("索引中不存在此Chunk文档: {}", chunkUid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 删除文档
            elasticsearchOperations.delete(chunkUid, ChunkElastic.class);
            log.info("Chunk索引删除成功: {}", chunkUid);
            return true;
        } catch (Exception e) {
            log.error("删除Chunk索引时发生错误: {}, 错误消息: {}", chunkUid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据知识库UID删除所有相关Chunk索引
     * @param kbaseUid 知识库UID
     * @return 是否删除成功
     */
    public boolean deleteByKbaseUid(String kbaseUid) {
        log.info("删除知识库下所有Chunk索引: {}", kbaseUid);
        
        try {
            // 首先创建查询
            Query query = NativeQuery.builder()
                    .withQuery(QueryBuilders.term()
                        .field("kbaseUid")
                        .value(kbaseUid)
                        .build()._toQuery())
                    .build();
                    
            // 创建DeleteQuery对象
            DeleteQuery deleteQuery = DeleteQuery.builder(query).build();
            
            // 执行删除操作并获取响应
            var response = elasticsearchOperations.delete(deleteQuery, ChunkElastic.class);
            
            // 记录删除的文档数量
            log.info("成功删除知识库下的Chunk索引数量: {}", response.getDeleted());
            return true;
        } catch (Exception e) {
            log.error("删除知识库下的Chunk索引时发生错误: {}, 错误消息: {}", kbaseUid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索Chunk内容
     * @param query 查询关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return Chunk搜索结果列表
     */
    public List<ChunkElasticSearchResult> searchChunks(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("搜索Chunks: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 构建查询条件
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 关键词匹配查询
            // 在内容和名称字段中搜索
            MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
                .query(query)
                .fields("content^3", "name^2") // 内容字段权重3，名称字段权重2
                .fuzziness("AUTO")
                .build();
            boolQueryBuilder.must(multiMatchQuery._toQuery());
            
            // 添加过滤条件：启用状态
            boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
            
            // 添加时间过滤
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String nowStr = now.format(formatter);
            
            // 有效期过滤
            DateRangeQuery startDateQuery = new DateRangeQuery.Builder()
                .field("startDate")
                .lte(nowStr)
                .build();
            boolQueryBuilder.filter(QueryBuilders.range().date(startDateQuery).build()._toQuery());
            
            DateRangeQuery endDateQuery = new DateRangeQuery.Builder()
                .field("endDate")
                .gte(nowStr)
                .build();
            boolQueryBuilder.filter(QueryBuilders.range().date(endDateQuery).build()._toQuery());
            
            // 添加可选的过滤条件：知识库、分类、组织
            if (kbUid != null && !kbUid.isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("kbaseUid").value(kbUid).build()._toQuery());
            }
            
            if (categoryUid != null && !categoryUid.isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
            }
            
            if (orgUid != null && !orgUid.trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
            }
            
            // 创建查询对象
            Query searchQuery = NativeQuery.builder()
                    .withQuery(boolQueryBuilder.build()._toQuery())
                    .withMaxResults(10)
                    .build();
            
            // 执行搜索
            SearchHits<ChunkElastic> searchHits = elasticsearchOperations.search(searchQuery, ChunkElastic.class);
            log.info("搜索结果数量: {}", searchHits.getTotalHits());
            
            List<ChunkElasticSearchResult> chunkElasticResultList = new ArrayList<>();
            
            // 处理搜索结果
            for (SearchHit<ChunkElastic> hit : searchHits) {
                ChunkElastic chunkElastic = hit.getContent();
                float score = hit.getScore();
                
                // 创建结果对象
                ChunkElasticSearchResult result = ChunkElasticSearchResult.builder()
                    .chunkElastic(chunkElastic)
                    .score(score)
                    .build();
                
                // 手动添加内容高亮
                String content = chunkElastic.getContent();
                if (content != null && !content.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (content.toLowerCase().contains(query.toLowerCase())) {
                        int startIdx = content.toLowerCase().indexOf(query.toLowerCase());
                        int endIdx = startIdx + query.length();
                        
                        // 为了更好的可读性，提取查询词周围的上下文（前后各100个字符）
                        int contextStart = Math.max(0, startIdx - 100);
                        int contextEnd = Math.min(content.length(), endIdx + 100);
                        String highlightedContent = content.substring(contextStart, startIdx) + 
                                      "<em>" + content.substring(startIdx, endIdx) + "</em>" +
                                      content.substring(endIdx, contextEnd);
                        
                        if (contextStart > 0) {
                            highlightedContent = "..." + highlightedContent;
                        }
                        if (contextEnd < content.length()) {
                            highlightedContent = highlightedContent + "...";
                        }
                        
                        result.setHighlightedContent(highlightedContent);
                    } else {
                        // 如果没有直接匹配，则提取前面一部分内容作为预览
                        result.setHighlightedContent(content.length() > 200 ? content.substring(0, 200) + "..." : content);
                    }
                }
                
                // 手动添加名称高亮
                String name = chunkElastic.getName();
                if (name != null && !name.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (name.toLowerCase().contains(query.toLowerCase())) {
                        int startIdx = name.toLowerCase().indexOf(query.toLowerCase());
                        int endIdx = startIdx + query.length();
                        String highlightedName = name.substring(0, startIdx) + 
                                  "<em>" + name.substring(startIdx, endIdx) + "</em>" +
                                  name.substring(endIdx);
                        result.setHighlightedName(highlightedName);
                    } else {
                        result.setHighlightedName(name);
                    }
                } else if (name != null) {
                    result.setHighlightedName(name);
                }
                
                chunkElasticResultList.add(result);
            }
            
            return chunkElasticResultList;
        } catch (Exception e) {
            log.error("搜索Chunks时发生错误: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 用户在输入过程中，给出输入联想
     * @param request 请求参数
     * @return Chunk搜索结果列表
     */
    public List<ChunkElasticSearchResult> suggestChunks(ChunkRequest request) {
        String query = request.getContent();
        log.info("联想Chunks: {}", query);
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 构建查询条件
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 在内容中查找前缀匹配
            boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
                .field("content")
                .query(query)
                .boost(3.0f)
                .build()._toQuery());
                
            // 在名称中查找前缀匹配
            boolQueryBuilder.should(QueryBuilders.matchPhrasePrefix()
                .field("name")
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
            
            // 添加组织过滤条件
            if (request.getOrgUid() != null && !request.getOrgUid().trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(request.getOrgUid()).build()._toQuery());
            }
            
            // 添加知识库过滤条件
            if (request.getKbUid() != null && !request.getKbUid().trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("kbaseUid").value(request.getKbUid()).build()._toQuery());
            }
            
            // 添加时间过滤
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String nowStr = now.format(formatter);
            
            // 有效期过滤
            DateRangeQuery startDateQuery = new DateRangeQuery.Builder()
                .field("startDate")
                .lte(nowStr)
                .build();
            boolQueryBuilder.filter(QueryBuilders.range().date(startDateQuery).build()._toQuery());
            
            DateRangeQuery endDateQuery = new DateRangeQuery.Builder()
                .field("endDate")
                .gte(nowStr)
                .build();
            boolQueryBuilder.filter(QueryBuilders.range().date(endDateQuery).build()._toQuery());
            
            // 创建查询对象
            Query searchQuery = NativeQuery.builder()
                    .withQuery(boolQueryBuilder.build()._toQuery())
                    .withMaxResults(10) // 联想默认返回10个结果
                    .build();
            
            // 执行搜索
            SearchHits<ChunkElastic> searchHits = elasticsearchOperations.search(searchQuery, ChunkElastic.class);
            
            List<ChunkElasticSearchResult> chunkElasticResultList = new ArrayList<>();
            
            // 处理搜索结果
            for (SearchHit<ChunkElastic> hit : searchHits) {
                ChunkElastic chunkElastic = hit.getContent();
                float score = hit.getScore();
                
                // 创建结果对象
                ChunkElasticSearchResult result = ChunkElasticSearchResult.builder()
                    .chunkElastic(chunkElastic)
                    .score(score)
                    .build();
                
                // 手动添加内容高亮
                String content = chunkElastic.getContent();
                if (content != null && !content.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (content.toLowerCase().contains(query.toLowerCase())) {
                        int startIdx = content.toLowerCase().indexOf(query.toLowerCase());
                        int endIdx = startIdx + query.length();
                        
                        // 为了更好的可读性，提取查询词周围的上下文（前后各50个字符）
                        int contextStart = Math.max(0, startIdx - 50);
                        int contextEnd = Math.min(content.length(), endIdx + 50);
                        String highlightedContent = content.substring(contextStart, startIdx) + 
                                      "<em>" + content.substring(startIdx, endIdx) + "</em>" +
                                      content.substring(endIdx, contextEnd);
                        
                        if (contextStart > 0) {
                            highlightedContent = "..." + highlightedContent;
                        }
                        if (contextEnd < content.length()) {
                            highlightedContent = highlightedContent + "...";
                        }
                        
                        result.setHighlightedContent(highlightedContent);
                    } else {
                        // 如果没有直接匹配，则提取前面一部分内容作为预览
                        result.setHighlightedContent(content.length() > 100 ? content.substring(0, 100) + "..." : content);
                    }
                }
                
                // 手动添加名称高亮
                String name = chunkElastic.getName();
                if (name != null && !name.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (name.toLowerCase().contains(query.toLowerCase())) {
                        int startIdx = name.toLowerCase().indexOf(query.toLowerCase());
                        int endIdx = startIdx + query.length();
                        String highlightedName = name.substring(0, startIdx) + 
                                  "<em>" + name.substring(startIdx, endIdx) + "</em>" +
                                  name.substring(endIdx);
                        result.setHighlightedName(highlightedName);
                    } else {
                        result.setHighlightedName(name);
                    }
                } else if (name != null) {
                    result.setHighlightedName(name);
                }
                
                chunkElasticResultList.add(result);
            }
            
            return chunkElasticResultList;
        } catch (Exception e) {
            log.error("联想Chunks时发生错误: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
