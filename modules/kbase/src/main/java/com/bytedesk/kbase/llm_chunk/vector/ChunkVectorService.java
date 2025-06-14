/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:55:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 11:05:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

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
                "enabled", Boolean.toString(chunk.getEnabled()),
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
            Optional<ChunkEntity> chunkOpt = chunkRestService.findByUid(request.getUid());
            
            if (!chunkOpt.isPresent()) {
                log.error("找不到要更新的chunk: {}", request.getUid());
                return;
            }
            
            ChunkEntity chunk = chunkOpt.get();
            
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
    
    // update all elasticsearch vector index
    public void updateAllVectorIndex(ChunkRequest request) {
        List<ChunkEntity> chunkList = chunkRestService.findByKbUid(request.getKbUid());
        chunkList.forEach(chunk -> {
            // TODO: Implement vector indexing logic here
            log.info("Vector index functionality not implemented yet for Chunk: {}", chunk.getUid());
        });
        log.info("Vector indexing requested for {} chunks from knowledge base: {}", chunkList.size(), request.getKbUid());
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
     * @param kbUid 知识库UID (可选)
     * @param categoryUid 分类UID (可选)
     * @param orgUid 组织UID (可选)
     * @param limit 返回结果数量限制
     * @return 搜索结果列表
     */
    public List<ChunkVectorSearchResult> searchChunkVector(String query, String kbUid, String categoryUid, String orgUid, int limit) {
        // 默认相似度阈值设为0.0，表示返回所有结果
        return searchChunkVector(query, kbUid, categoryUid, orgUid, limit, 0.0);
    }
    
    /**
     * 向量相似度搜索（带相似度阈值参数）
     * @param query 查询文本
     * @param kbUid 知识库UID (可选)
     * @param categoryUid 分类UID (可选)
     * @param orgUid 组织UID (可选)
     * @param limit 返回结果数量限制
     * @param similarity 相似度阈值 (0-1)
     * @return 搜索结果列表
     */
    public List<ChunkVectorSearchResult> searchChunkVector(String query, String kbUid, String categoryUid, String orgUid, int limit, double similarity) {
        log.info("向量搜索chunk: query={}, kbUid={}, categoryUid={}, orgUid={}, limit={}, similarity={}", query, kbUid, categoryUid, orgUid, limit, similarity);
        
        List<ChunkVectorSearchResult> results = new ArrayList<>();
        
        try {
            // 创建过滤表达式构建器
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            
            // 构建查询条件
            FilterExpressionBuilder.Op enabledOp = expressionBuilder.eq("enabled", "true");
            
            // 添加可选的过滤条件
            FilterExpressionBuilder.Op finalOp = enabledOp;
            
            if (kbUid != null && !kbUid.isEmpty()) {
                FilterExpressionBuilder.Op kbUidOp = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid);
                finalOp = expressionBuilder.and(finalOp, kbUidOp);
            }
            
            if (categoryUid != null && !categoryUid.isEmpty()) {
                FilterExpressionBuilder.Op categoryUidOp = expressionBuilder.eq("categoryUid", categoryUid);
                finalOp = expressionBuilder.and(finalOp, categoryUidOp);
            }
            
            if (orgUid != null && !orgUid.isEmpty()) {
                FilterExpressionBuilder.Op orgUidOp = expressionBuilder.eq("orgUid", orgUid);
                finalOp = expressionBuilder.and(finalOp, orgUidOp);
            }
            
            // 构建最终的过滤表达式
            Expression expression = finalOp.build();
            
            // 构建搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .filterExpression(expression)
                    .topK(limit) // 限制返回的结果数量
                    .similarityThreshold((float) similarity) // 设置相似度阈值
                    .build();
            
            // 执行相似度搜索
            List<Document> documents = vectorStore.similaritySearch(searchRequest);
            
            // 处理结果
            for (Document doc : documents) {
                Map<String, Object> metadata = doc.getMetadata();
                
                // 从元数据中提取值，使用getOrDefault保证安全获取
                String uid = (String) metadata.getOrDefault("uid", "");
                String name = (String) metadata.getOrDefault("name", "");
                String content = doc.getText();
                String type = (String) metadata.getOrDefault("type", "");
                String kbaseUid = (String) metadata.getOrDefault(KbaseConst.KBASE_KB_UID, "");
                String catUid = (String) metadata.getOrDefault("categoryUid", "");
                String organization = (String) metadata.getOrDefault("orgUid", "");
                String document = (String) metadata.getOrDefault("docId", "");
                String file = (String) metadata.getOrDefault("fileUid", "");
                boolean enabled = Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true"));
                
                // 处理标签
                List<String> tagsList = new ArrayList<>();
                String tagsStr = (String) metadata.getOrDefault("tags", "");
                if (tagsStr != null && !tagsStr.isEmpty()) {
                    tagsList = List.of(tagsStr.split(","));
                }
                
                // 创建ChunkVector对象
                ChunkVector chunkVector = ChunkVector.builder()
                    .uid(uid)
                    .name(name)
                    .content(content)
                    .type(type)
                    .kbUid(kbaseUid)
                    .categoryUid(catUid)
                    .orgUid(organization)
                    .docId(document)
                    .fileUid(file)
                    .enabled(enabled)
                    .tagList(tagsList)
                    .build();
                
                // 创建搜索结果对象
                ChunkVectorSearchResult result = ChunkVectorSearchResult.builder()
                    .chunkVector(chunkVector)
                    .score(doc.getScore())
                    // 以下是新增字段，初始化为默认值，可后续根据需要设置
                    .highlightedContent(content) // 默认使用原始内容，可稍后应用高亮处理
                    .highlightedName(name) // 默认使用原始名称，可稍后应用高亮处理
                    .distance((float) (1.0 - doc.getScore())) // 将相似度转换为距离
                    .build();
                
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
            // 使用过滤表达式查询已存在的文档
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op idOp = expressionBuilder.eq("id", id);
            Expression expression = idOp.build();
            
            // 构建搜索请求，只查询是否存在，不关心内容
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("")
                    .filterExpression(expression)
                    .build();
            
            List<Document> existingDocs = vectorStore.similaritySearch(searchRequest);
            
            // 如果文档存在，则删除
            if (existingDocs != null && !existingDocs.isEmpty()) {
                log.info("删除已存在的Chunk向量文档: {}", id);
                vectorStore.delete(List.of(id));
            }
        } catch (Exception e) {
            log.warn("检查文档存在性时出错: {}, 错误: {}", id, e.getMessage());
            // 安全起见，尝试直接删除
            try {
                vectorStore.delete(List.of(id));
            } catch (Exception ex) {
                log.warn("删除可能存在的文档时出错: {}", ex.getMessage());
            }
        }
    }
}
