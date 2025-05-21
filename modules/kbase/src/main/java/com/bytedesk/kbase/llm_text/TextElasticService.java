/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 17:56:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 16:23:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
public class TextElasticService {
    
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private TextRestService textRestService;
    
    // update elasticsearch index
    public void updateIndex(TextRequest request) {
        Optional<TextEntity> textOpt = textRestService.findByUid(request.getUid());
        if (textOpt.isPresent()) {
            TextEntity text = textOpt.get();
            indexText(text);
        } else {
            throw new IllegalArgumentException("Text not found with UID: " + request.getUid());
        }
    }

    // update all elasticsearch index
    public void updateAllIndex(TextRequest request) {
        List<TextEntity> textList = textRestService.findByKbUid(request.getKbUid());
        textList.forEach(text -> {
            indexText(text);
        });
        log.info("Updated elasticsearch index for {} texts from knowledge base: {}", textList.size(), request.getKbUid());
    }

    
    /**
     * 索引Text实体到Elasticsearch
     * @param text 要索引的Text实体
     */
    public void indexText(TextEntity text) {
        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(text.getUid(), TextElastic.class);
        
        if (exists) {
            log.info("更新已存在的Text索引: {}", text.getUid());
        } else {
            log.info("为Text创建新索引: {}", text.getUid());
        }
        
        try {
            // 将TextEntity转换为TextElastic对象
            TextElastic textElastic = TextEntityElasticConverter.toElastic(text);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(textElastic);
            
            if (exists) {
                log.info("Text索引更新成功: {}", text.getUid());
            } else {
                log.info("Text索引创建成功: {}", text.getUid());
            }

            // 将索引结果保存到数据库中
            text.setSuccess();
            textRestService.save(text);

        } catch (Exception e) {
            log.error("索引Text时发生错误: {}, 错误消息: {}", text.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除Text的索引
     * @param textUid 要删除的Text的UID
     * @return 是否删除成功
     */
    public Boolean deleteText(String textUid) {
        log.info("从索引中删除Text: {}", textUid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(textUid, TextElastic.class);
            if (!exists) {
                log.warn("索引中不存在此Text文档: {}", textUid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 删除文档
            elasticsearchOperations.delete(textUid, TextElastic.class);
            log.info("Text索引删除成功: {}", textUid);
            return true;
        } catch (Exception e) {
            log.error("删除Text索引时发生错误: {}, 错误消息: {}", textUid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据知识库UID删除所有相关Text索引
     * @param kbaseUid 知识库UID
     * @return 是否删除成功
     */
    public Boolean deleteByKbaseUid(String kbaseUid) {
        log.info("删除知识库下所有Text索引: {}", kbaseUid);
        
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
            var response = elasticsearchOperations.delete(deleteQuery, TextElastic.class);
            
            // 记录删除的文档数量
            log.info("成功删除知识库下的Text索引数量: {}", response.getDeleted());
            return true;
        } catch (Exception e) {
            log.error("删除知识库下的Text索引时发生错误: {}, 错误消息: {}", kbaseUid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索Text内容
     * @param query 查询关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return Text搜索结果列表
     */
    public List<TextElasticSearchResult> searchTexts(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("搜索Texts: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 转义正则表达式特殊字符
            query = Pattern.quote(query);

            // 构建查询条件
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 关键词匹配查询
            // 在内容和名称字段中搜索
            MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
                .query(query)
                .fields("content^3", "name^2")
                .fuzziness("AUTO")
                .build();
            boolQueryBuilder.must(multiMatchQuery._toQuery());
            
            // 添加过滤条件：启用状态
            boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
            
            // 添加时间过滤
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String nowStr = now.format(formatter);
            
            // 有效期过滤 - 开始日期
            DateRangeQuery startDateQuery = new DateRangeQuery.Builder()
                .field("startDate")
                .lte(nowStr)
                .build();
            boolQueryBuilder.filter(QueryBuilders.range().date(startDateQuery).build()._toQuery());
            
            // 有效期过滤 - 结束日期
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
            
            // 构建查询
            NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQueryBuilder.build()._toQuery())
                .withMaxResults(10)
                .build();
            
            // 执行搜索
            SearchHits<TextElastic> searchHits = 
                elasticsearchOperations.search(searchQuery, TextElastic.class);
            log.info("搜索结果数量: {}", searchHits.getTotalHits());
            
            List<TextElasticSearchResult> results = new ArrayList<>();
            
            // 处理搜索结果
            for (SearchHit<TextElastic> hit : searchHits) {
                TextElastic textElastic = hit.getContent();
                float score = hit.getScore();
                
                // 创建结果对象
                TextElasticSearchResult result = TextElasticSearchResult.builder()
                    .textElastic(textElastic)
                    .score(score)
                    .build();
                
                // 手动添加内容高亮
                String content = textElastic.getContent();
                if (content != null && !content.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (content.toLowerCase().contains(query.toLowerCase())) {
                        int index = content.toLowerCase().indexOf(query.toLowerCase());
                        int start = Math.max(0, index - 50);
                        int end = Math.min(content.length(), index + query.length() + 50);
                        String snippet = content.substring(start, end);
                        
                        // 添加高亮标记 - 对特殊正则表达式字符进行转义
                        String queryEscaped = Pattern.quote(query);
                        String highlighted = snippet.replaceAll(
                            "(?i)" + queryEscaped,
                            "<em>" + query + "</em>"
                        );
                        
                        result.setHighlightedContent(highlighted);
                    } else {
                        // 如果没有直接匹配，使用前100个字符
                        result.setHighlightedContent(content.length() > 100 ? 
                            content.substring(0, 100) + "..." : content);
                    }
                }
                
                // 手动添加名称高亮
                String title = textElastic.getTitle();
                if (title != null && !title.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (title.toLowerCase().contains(query.toLowerCase())) {
                        String queryEscaped = Pattern.quote(query);
                        String highlighted = title.replaceAll(
                            "(?i)" + queryEscaped,
                            "<em>" + query + "</em>"
                        );
                        result.setHighlightedName(highlighted);
                    } else {
                        result.setHighlightedName(title);
                    }
                } else if (title != null) {
                    result.setHighlightedName(title);
                }
                
                results.add(result);
            }
            
            log.info("搜索Text完成，找到{}条结果", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("搜索Text时发生错误: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 用户在输入过程中，给出输入联想
     * @param request 请求参数
     * @return Text搜索结果列表
     */
    public List<TextElasticSearchResult> suggestTexts(TextRequest request) {
        String query = request.getContent();
        log.info("联想Texts: {}", query);
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 转义正则表达式特殊字符
            query = Pattern.quote(query);

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
            SearchHits<TextElastic> searchHits = 
                elasticsearchOperations.search(searchQuery, TextElastic.class);
            
            List<TextElasticSearchResult> results = new ArrayList<>();
            
            // 处理搜索结果
            for (SearchHit<TextElastic> hit : searchHits) {
                TextElastic textElastic = hit.getContent();
                float score = hit.getScore();
                
                // 创建结果对象
                TextElasticSearchResult result = TextElasticSearchResult.builder()
                    .textElastic(textElastic)
                    .score(score)
                    .build();
                
                // 手动添加内容高亮
                String content = textElastic.getContent();
                if (content != null && !content.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (content.toLowerCase().contains(query.toLowerCase())) {
                        int index = content.toLowerCase().indexOf(query.toLowerCase());
                        int start = Math.max(0, index - 50);
                        int end = Math.min(content.length(), index + query.length() + 50);
                        String snippet = content.substring(start, end);
                        
                        // 添加高亮标记 - 对特殊正则表达式字符进行转义
                        String queryEscaped = Pattern.quote(query);
                        String highlighted = snippet.replaceAll(
                            "(?i)" + queryEscaped,
                            "<em>" + query + "</em>"
                        );
                        
                        result.setHighlightedContent(highlighted);
                    } else {
                        // 如果没有直接匹配，使用前100个字符
                        result.setHighlightedContent(content.length() > 100 ? 
                            content.substring(0, 100) + "..." : content);
                    }
                }
                
                // 手动添加名称高亮
                String title = textElastic.getTitle();
                if (title != null && !title.trim().isEmpty() && query != null && !query.isEmpty()) {
                    // 在包含查询词的部分手动添加高亮标签
                    if (title.toLowerCase().contains(query.toLowerCase())) {
                        String queryEscaped = Pattern.quote(query);
                        String highlighted = title.replaceAll(
                            "(?i)" + queryEscaped,
                            "<em>" + query + "</em>"
                        );
                        result.setHighlightedName(highlighted);
                    } else {
                        result.setHighlightedName(title);
                    }
                } else if (title != null) {
                    result.setHighlightedName(title);
                }
                
                results.add(result);
            }
            
            log.info("Text联想完成，找到{}条结果", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Text联想时发生错误: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
