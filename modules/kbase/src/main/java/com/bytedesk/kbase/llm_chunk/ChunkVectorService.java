/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:55:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:55:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bytedesk.kbase.config.KbaseConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Chunk向量检索服务
 * 用于处理Chunk的向量存储和相似度搜索
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChunkVectorService {
    
    private final ElasticsearchVectorStore vectorStore;
    
    private final ChunkRestService chunkRestService;
    
    @Qualifier("bytedeskOllamaEmbeddingModel") 
    private final EmbeddingModel embeddingModel;
    
    /**
     * 将chunk内容添加到向量存储中
     * @param chunk Chunk实体
     */
    @Transactional
    public void indexChunkVector(ChunkEntity chunk) {
        log.info("向量索引chunk: {}", chunk.getName());
        
        try {
            // 1. 为内容创建文档（带有元数据）
            String id = "chunk_" + chunk.getUid();
            String content = chunk.getContent();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", chunk.getTagList());
            
            // 元数据
            Map<String, Object> metadata = Map.of(
                "uid", chunk.getUid(),
                "name", chunk.getName(),
                KbaseConst.KBASE_KB_UID, chunk.getKbase() != null ? chunk.getKbase().getUid() : "",
                "categoryUid", chunk.getCategoryUid() != null ? chunk.getCategoryUid() : "",
                "orgUid", chunk.getOrgUid(),
                "enabled", Boolean.toString(chunk.isEnabled()),
                "tags", tags,
                "type", chunk.getType(),
                "docId", chunk.getDocId() != null ? chunk.getDocId() : "",
                "fileUid", chunk.getFile() != null ? chunk.getFile().getUid() : ""
            );
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            checkAndDeleteExistingDoc(id);
            
            // 添加新文档
            vectorStore.add(List.of(document));
            
            // 3. 更新Chunk实体的状态
            // 设置向量索引状态为成功
            chunk.setVectorStatus(ChunkStatusEnum.SUCCESS.name());
            
            // 更新Chunk实体
            chunkRestService.save(chunk);
            
            log.info("Chunk向量索引成功: {}", chunk.getName());
        } catch (Exception e) {
            log.error("Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
            
            // 设置向量索引状态为失败
            chunk.setVectorStatus(ChunkStatusEnum.ERROR.name());
            chunkRestService.save(chunk);
            
            throw e;
        }
    }
    
    /**
     * 更新chunk的向量索引
     * @param request chunk请求对象
     */
    @Transactional
    public void updateVectorIndex(ChunkRequest request) {
        log.info("更新chunk向量索引: {}", request.getName());
        
        try {
            // 获取Chunk实体
            ChunkEntity chunk = chunkRestService.findByUid(request.getUid());
            
            if (chunk == null) {
                log.error("找不到要更新的chunk: {}", request.getUid());
                return;
            }
            
            // 检查是否有内容变化
            if (chunk.hasChanged(request)) {
                log.info("Chunk内容有变化，更新向量索引: {}", chunk.getName());
                
                // 更新Chunk实体
                // 名称
                if (request.getName() != null) {
                    chunk.setName(request.getName());
                }
                // 内容
                if (request.getContent() != null) {
                    chunk.setContent(request.getContent());
                }
                // 标签
                if (request.getTagList() != null) {
                    chunk.setTagList(request.getTagList());
                }
                // 启用状态
                chunk.setEnabled(request.getEnabled());
                // 日期
                if (request.getStartDate() != null) {
                    chunk.setStartDate(request.getStartDate());
                }
                if (request.getEndDate() != null) {
                    chunk.setEndDate(request.getEndDate());
                }
                
                // 重置向量状态
                chunk.setVectorStatus(ChunkStatusEnum.NEW.name());
                
                // 保存更新后的实体
                chunkRestService.save(chunk);
                
                // 重新索引
                indexChunkVector(chunk);
            } else {
                log.info("Chunk内容无变化，不更新向量索引: {}", chunk.getName());
            }
        } catch (Exception e) {
            log.error("更新Chunk向量索引失败: {}, 错误: {}", request.getName(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * 删除chunk的向量索引
     * @param chunk Chunk实体
     */
    @Transactional
    public void deleteChunkVector(ChunkEntity chunk) {
        log.info("删除chunk向量索引: {}", chunk.getName());
        
        try {
            // 删除向量文档
            String id = "chunk_" + chunk.getUid();
            vectorStore.delete(List.of(id));
            
            log.info("Chunk向量索引删除成功: {}", chunk.getName());
        } catch (Exception e) {
            log.error("删除Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * 向量相似度搜索
     * @param query 查询文本
     * @param limit 返回结果限制数量
     * @param kbUid 知识库UID (可选)
     * @param categoryUid 分类UID (可选)
     * @param similarity 相似度阈值 (0-1)
     * @return 搜索结果列表
     */
    public List<ChunkVectorSearchResult> searchChunkVector(String query, int limit, String kbUid, String categoryUid, double similarity) {
        log.info("向量搜索chunk: query={}, kbUid={}, categoryUid={}", query, kbUid, categoryUid);
        
        List<ChunkVectorSearchResult> results = new ArrayList<>();
        
        try {
            // 构建过滤条件
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder()
                .eq("enabled", "true");
                
            // 如果指定了知识库，添加过滤条件
            if (kbUid != null && !kbUid.isEmpty()) {
                filterBuilder = filterBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid);
            }
            
            // 如果指定了分类，添加过滤条件
            if (categoryUid != null && !categoryUid.isEmpty()) {
                filterBuilder = filterBuilder.eq("categoryUid", categoryUid);
            }
            
            Expression filter = filterBuilder.build();
            
            // 创建搜索请求
            SearchRequest searchRequest = SearchRequest.defaults()
                .withQuery(query)
                .withTopK(limit)
                .withSimilarityThreshold((float) similarity)
                .withFilter(filter);
            
            // 执行搜索
            List<Document> documents = vectorStore.search(searchRequest);
            
            // 处理结果
            for (Document doc : documents) {
                Map<String, Object> metadata = doc.getMetadata();
                String content = doc.getContent();
                
                // 创建搜索结果对象
                ChunkVectorSearchResult result = ChunkVectorSearchResult.builder()
                    .uid((String) metadata.get("uid"))
                    .name((String) metadata.get("name"))
                    .content(content)
                    .score(doc.getScore())
                    .kbUid((String) metadata.get(KbaseConst.KBASE_KB_UID))
                    .categoryUid((String) metadata.get("categoryUid"))
                    .orgUid((String) metadata.get("orgUid"))
                    .type((String) metadata.get("type"))
                    .docId((String) metadata.get("docId"))
                    .fileUid((String) metadata.get("fileUid"))
                    .build();
                
                // 处理标签
                String tagString = (String) metadata.get("tags");
                if (tagString != null && !tagString.isEmpty()) {
                    result.setTagList(List.of(tagString.split(",")));
                }
                
                results.add(result);
            }
            
            log.info("Chunk向量搜索成功，找到{}个结果", results.size());
        } catch (Exception e) {
            log.error("Chunk向量搜索失败: {}", e.getMessage());
            // 不抛出异常，返回空列表
        }
        
        return results;
    }
    
    /**
     * 检查并删除已存在的文档
     * @param id 文档ID
     */
    private void checkAndDeleteExistingDoc(String id) {
        try {
            // 查找并删除之前的文档
            vectorStore.delete(List.of(id));
            log.debug("已删除旧向量文档: {}", id);
        } catch (Exception e) {
            log.debug("文档不存在或删除失败: {}, 错误: {}", id, e.getMessage());
            // 忽略错误，继续执行
        }
    }
}
