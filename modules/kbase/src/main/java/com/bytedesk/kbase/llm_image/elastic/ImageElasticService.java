/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 15:16:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 13:20:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image.elastic;

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
import com.bytedesk.kbase.llm_image.ImageEntity;
import com.bytedesk.kbase.llm_image.ImageRequest;
import com.bytedesk.kbase.llm_image.ImageRestService;

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
public class ImageElasticService {
        
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ImageRestService imageRestService;
    
    // update elasticsearch index
    public void updateIndex(ImageRequest request) {
        Optional<ImageEntity> imageOpt = imageRestService.findByUid(request.getUid());
        if (imageOpt.isPresent()) {
            ImageEntity image = imageOpt.get();
            indexImage(image);
        } else {
            throw new IllegalArgumentException("Image not found with UID: " + request.getUid());
        }
    }

    // update all elasticsearch index
    public void updateAllIndex(ImageRequest request) {
        List<ImageEntity> imageList = imageRestService.findByKbUid(request.getKbUid());
        imageList.forEach(image -> {
            indexImage(image);
        });
        log.info("Updated elasticsearch index for {} images from knowledge base: {}", imageList.size(), request.getKbUid());
    }
    
    /**
     * 索引Image实体到Elasticsearch
     * @param image 要索引的Image实体
     */
    public void indexImage(ImageEntity image) {
        // 首先检查索引是否存在，如果不存在则创建
        boolean indexExists = elasticsearchOperations.indexOps(ImageElastic.class).exists();
        if (!indexExists) {
            log.info("索引不存在: {}，正在创建...", ImageElastic.class.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName());
            // 创建索引
            boolean created = elasticsearchOperations.indexOps(ImageElastic.class).create();
            // 创建映射
            boolean mapped = elasticsearchOperations.indexOps(ImageElastic.class).putMapping();
            log.info("索引创建结果: {}, 映射创建结果: {}", created, mapped);
            
            if (!(created && mapped)) {
                log.error("索引创建失败，无法继续索引文档: {}", image.getUid());
                return;
            }
        }

        // 检查文档是否已存在
        boolean exists = elasticsearchOperations.exists(image.getUid(), ImageElastic.class);
            
            if (exists) {
                log.info("更新已存在的Image索引: {}", image.getUid());
            } else {
                log.info("为Image创建新索引: {}", image.getUid());
            }
        
        try {
            // 将ImageEntity转换为ImageElastic对象
            ImageElastic imageElastic = ImageElastic.fromEntity(image);
            
            // 将文档索引到Elasticsearch
            elasticsearchOperations.save(imageElastic);
            
            if (exists) {
                log.info("Image索引更新成功: {}", image.getUid());
            } else {
                log.info("Image索引创建成功: {}", image.getUid());
            }

            // 将索引结果保存到数据库中
            image.setElasticSuccess();
            imageRestService.save(image);

        } catch (Exception e) {
            log.error("索引Image时发生错误: {}, 错误消息: {}", image.getUid(), e.getMessage(), e);
        }
    }
    
    /**
     * 从Elasticsearch中删除Image的索引
     * @param imageUid 要删除的Image的UID
     * @return 是否删除成功
     */
    public Boolean deleteImage(String imageUid) {
        log.info("从索引中删除Image: {}", imageUid);
        
        try {
            // 首先检查文档是否存在
            boolean exists = elasticsearchOperations.exists(imageUid, ImageElastic.class);
            if (!exists) {
                log.warn("索引中不存在此Image文档: {}", imageUid);
                return true; // 文档不存在也视为删除成功
            }
            
            // 删除文档
            elasticsearchOperations.delete(imageUid, ImageElastic.class);
            log.info("Image索引删除成功: {}", imageUid);
            return true;
        } catch (Exception e) {
            log.error("删除Image索引时发生错误: {}, 错误消息: {}", imageUid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据知识库UID删除所有相关Image索引
     * @param kbaseUid 知识库UID
     * @return 是否删除成功
     */
    public Boolean deleteByKbaseUid(String kbaseUid) {
        log.info("删除知识库下所有Image索引: {}", kbaseUid);
        
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
            var response = elasticsearchOperations.delete(deleteQuery, ImageElastic.class);
            
            // 记录删除的文档数量
            log.info("成功删除知识库下的Image索引数量: {}", response.getDeleted());
            return true;
        } catch (Exception e) {
            log.error("删除知识库下的Image索引时发生错误: {}, 错误消息: {}", kbaseUid, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 搜索Image内容
     * @param query 查询关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @return Image搜索结果列表
     */
    public List<ImageElasticSearchResult> searchImages(String query, String kbUid, String categoryUid, String orgUid) {
        log.info("搜索Images: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        return searchImagesInternal(query, kbUid, categoryUid, orgUid, false, 10);
    }

    /**
     * 用户在输入过程中，给出输入联想
     * @param request 请求参数
     * @return Image搜索结果列表
     */
    public List<ImageElasticSearchResult> suggestImages(ImageRequest request) {
        String query = request.getContent();
        String kbUid = request.getKbUid();
        String orgUid = request.getOrgUid();
        
        log.info("联想Images: query={}, kbUid={}, orgUid={}", query, kbUid, orgUid);
        
        List<ImageElasticSearchResult> results = searchImagesInternal(query, kbUid, null, orgUid, true, 10);
        log.info("Image联想结果数: {}", results.size());
        return results;
    }
    
    /**
     * 搜索Image内容 - 私有辅助方法，处理搜索和联想的共同逻辑
     * 
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @param isSuggest 是否为输入联想模式
     * @param maxResults 最大结果数，为null则不限制
     * @return 搜索结果列表
     */
    private List<ImageElasticSearchResult> searchImagesInternal(
            String query, 
            String kbUid, 
            String categoryUid, 
            String orgUid, 
            boolean isSuggest,
            Integer maxResults) {
        
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 首先检查索引是否存在
            boolean indexExists = elasticsearchOperations.indexOps(ImageElastic.class).exists();
            if (!indexExists) {
                log.warn("索引不存在: {}，请先创建索引", ImageElastic.class.getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName());
                return new ArrayList<>();
            }
            
            // 构建查询条件
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 根据搜索模式构建不同的查询
            if (isSuggest) {
                // 联想模式 - 使用前缀匹配
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
            } else {
                // 完整搜索模式 - 使用多字段匹配
                MultiMatchQuery multiMatchQuery = QueryBuilders.multiMatch()
                    .query(query)
                    .fields("content^3", "name^2") // 内容字段权重3，名称字段权重2
                    .fuzziness("AUTO")
                    .build();
                boolQueryBuilder.must(multiMatchQuery._toQuery());
            }
            
            // 添加过滤条件：启用状态
            boolQueryBuilder.filter(QueryBuilders.term().field("enabled").value(true).build()._toQuery());
            
            // 添加时间过滤
            // ZonedDateTime now = BdDateUtils.now();
            // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            // String nowStr = now.format(formatter);
            
            // // 有效期过滤
            // DateRangeQuery startDateQuery = new DateRangeQuery.Builder()
            //     .field("startDate")
            //     .lte(nowStr)
            //     .build();
            // boolQueryBuilder.filter(QueryBuilders.range().date(startDateQuery).build()._toQuery());
            
            // DateRangeQuery endDateQuery = new DateRangeQuery.Builder()
            //     .field("endDate")
            //     .gte(nowStr)
            //     .build();
            // boolQueryBuilder.filter(QueryBuilders.range().date(endDateQuery).build()._toQuery());
            
            // 添加可选的过滤条件：知识库、分类、组织
            if (kbUid != null && !kbUid.trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("kbaseUid").value(kbUid).build()._toQuery());
            }
            
            if (categoryUid != null && !categoryUid.trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("categoryUid").value(categoryUid).build()._toQuery());
            }
            
            if (orgUid != null && !orgUid.trim().isEmpty()) {
                boolQueryBuilder.filter(QueryBuilders.term().field("orgUid").value(orgUid).build()._toQuery());
            }
            
            // 构建最终查询
            var searchQueryBuilder = NativeQuery.builder()
                    .withQuery(boolQueryBuilder.build()._toQuery());
            
            // 如果指定了最大结果数，则添加限制
            if (maxResults != null && maxResults > 0) {
                searchQueryBuilder.withMaxResults(maxResults);
            }
            
            Query searchQuery = searchQueryBuilder.build();
            
            // 执行搜索
            SearchHits<ImageElastic> searchHits = elasticsearchOperations.search(searchQuery, ImageElastic.class);
            log.debug("搜索完成，命中数: {}", searchHits.getTotalHits());
            
            List<ImageElasticSearchResult> resultList = new ArrayList<>();
            
            // 处理搜索结果
            for (SearchHit<ImageElastic> hit : searchHits) {
                ImageElastic imageElastic = hit.getContent();
                float score = hit.getScore();
                
                // 创建结果对象
                ImageElasticSearchResult result = ImageElasticSearchResult.builder()
                    .imageElastic(imageElastic)
                    .score(score)
                    .build();
                
                // 处理高亮
                applyHighlighting(result, query, imageElastic);
                
                resultList.add(result);
            }
            
            return resultList;
        } catch (Exception e) {
            log.error("Image搜索失败: query={}, 错误类型={}, 错误消息={}", 
                query, e.getClass().getName(), e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 为搜索结果添加高亮
     */
    private void applyHighlighting(ImageElasticSearchResult result, String query, ImageElastic imageElastic) {
        // 手动添加内容高亮
        String content = imageElastic.getContent();
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
        String name = imageElastic.getName();
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
    }
}
