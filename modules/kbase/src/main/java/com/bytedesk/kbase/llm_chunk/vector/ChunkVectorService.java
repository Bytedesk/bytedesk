/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:55:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 13:25:19
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_chunk.ChunkEntity;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Chunk向量检索服务
 * 用于处理Chunk的向量存储和相似度搜索
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
// @ConditionalOnBean(org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore.class)
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ChunkVectorService {
    
    private final ElasticsearchVectorStore vectorStore;
    
    private final ChunkRestService chunkRestService;

    public Map<String, Object> queryVectorByUid(ChunkRequest request) {
        String uid = request.getUid();
        if (!StringUtils.hasText(uid)) {
            throw new RuntimeException("uid is required");
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "CHUNK")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query("ping")
                .filterExpression(expression)
                .topK(10)
                .build();

        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        List<Map<String, Object>> docMaps = new ArrayList<>();
        if (docs != null) {
            for (Document doc : docs) {
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("id", doc.getId());
                docMap.put("content", doc.getText());
                Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
                if (!metadata.containsKey(KbaseConst.KBASE_KB_UID)
                        && metadata.containsKey(KbaseConst.KBASE_KB_UID_LEGACY)) {
                    metadata.put(KbaseConst.KBASE_KB_UID, metadata.get(KbaseConst.KBASE_KB_UID_LEGACY));
                    metadata.remove(KbaseConst.KBASE_KB_UID_LEGACY);
                }
                docMap.put("metadata", metadata);
                docMaps.add(docMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("exists", docs != null && !docs.isEmpty());
        result.put("total", docMaps.size());
        result.put("docs", docMaps);
        if (docs == null || docs.isEmpty()) {
            result.put("message", "未查询到相关向量化信息");
        }
        return result;
    }
    
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
                String tags = (chunk.getTagList() == null || chunk.getTagList().isEmpty())
                    ? ""
                    : String.join(",", chunk.getTagList());
            
            // 元数据
            Map<String, Object> metadata = new java.util.HashMap<>();
            metadata.put("uid", chunk.getUid());
            metadata.put("name", chunk.getName());
            metadata.put(KbaseConst.KBASE_KB_UID, chunk.getKbase() != null ? chunk.getKbase().getUid() : "");
            metadata.put("categoryUid", chunk.getCategoryUid() != null ? chunk.getCategoryUid() : "");
            metadata.put("orgUid", chunk.getOrgUid());
            metadata.put("enabled", Boolean.toString(chunk.getEnabled()));
            metadata.put("tags", tags);
            metadata.put("type", chunk.getType());
            // 用于向量检索侧按数据源类型过滤（ALL/FAQ/TEXT/CHUNK/WEBPAGE）
            metadata.put("sourceType", "CHUNK");
            metadata.put("docId", chunk.getDocId() != null ? chunk.getDocId() : "");
            metadata.put("fileUid", chunk.getFile() != null ? chunk.getFile().getUid() : "");
            metadata.put("fileName", chunk.getFile() != null ? chunk.getFile().getFileName() : "");
            metadata.put("fileUrl", chunk.getFile() != null ? chunk.getFile().getFileUrl() : "");
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            checkAndDeleteExistingDoc(id);
            
            // 添加新文档
            vectorStore.add(List.of(document));
            
            // 3. 仅更新向量状态（避免 detached entity 的版本冲突）
            chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.SUCCESS.name());
            chunkRestService.evictChunkCacheAllEntries();
            
            log.info("Chunk向量索引成功: {}", chunk.getName());
        } catch (Exception e) {
            log.error("Chunk向量索引失败: {}, 错误: {}", chunk.getName(), e.getMessage());
            
            // 仅更新向量状态（避免 detached entity 的版本冲突）
            chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.ERROR.name());
            chunkRestService.evictChunkCacheAllEntries();
            
            throw e;
        }
    }
    
    /**
     * 更新chunk的向量索引
     * @param request chunk请求对象
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateVectorIndex(ChunkRequest request) {
        log.info("更新chunk向量索引: {}", request.getName());
        
        try {
            // 获取Chunk实体
            Optional<ChunkEntity> chunkOpt = chunkRestService.findByUidNoCache(request.getUid());
            
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
                chunkRestService.evictChunkCacheAllEntries();
                
                // 重新索引
                ChunkEntity latestChunk = chunkRestService.findByUidNoCache(chunk.getUid())
                        .orElseThrow(() -> new RuntimeException("Chunk not found with UID: " + chunk.getUid()));
                indexChunkVector(latestChunk);
            } else {
                log.info("Chunk内容无变化，不更新向量索引: {}", chunk.getName());
            }
        } catch (Exception e) {
            log.error("更新Chunk向量索引失败: {}, 错误: {}", request.getName(), e.getMessage());
            throw e;
        }
    }
    
    // update all elasticsearch vector index
    public Map<String, Object> updateAllVectorIndex(ChunkRequest request) {
        String kbUid = request.getKbUid();
        if (!StringUtils.hasText(kbUid)) {
            throw new RuntimeException("kbUid is required");
        }

        List<ChunkEntity> chunkList = chunkRestService.findByKbUidNoCache(kbUid);

        int total = chunkList.size();
        int successCount = 0;
        int errorCount = 0;

        for (ChunkEntity chunk : chunkList) {
            try {
                // 标记处理中（便于前端观察状态变化）
                chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.PROCESSING.name());

                // 逐条重新索引（内部会根据结果写入 SUCCESS / ERROR）
                ChunkEntity latestChunk = chunkRestService.findByUidNoCache(chunk.getUid())
                        .orElse(chunk);
                indexChunkVector(latestChunk);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                log.warn("批量更新Chunk向量索引失败: uid={}, name={}, error={}", chunk.getUid(), chunk.getName(), e.getMessage());
                // indexChunkVector 内部已尽力更新 ERROR，这里不再抛出，继续处理其它 chunk
            }
        }

        chunkRestService.evictChunkCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("total", total);
        result.put("success", successCount);
        result.put("error", errorCount);

        log.info("Updated vector index for {} chunks from knowledge base: {}, success={}, error={}", total, kbUid, successCount, errorCount);
        return result;
    }

    /**
     * 同步Chunk向量索引状态到数据库
     */
    public ChunkEntity syncVectorStatus(ChunkRequest request) {
        Optional<ChunkEntity> chunkOpt = chunkRestService.findByUidNoCache(request.getUid());
        if (chunkOpt.isEmpty()) {
            throw new RuntimeException("Chunk not found with UID: " + request.getUid());
        }

        ChunkEntity chunk = chunkOpt.get();

        boolean exists;
        try {
            exists = existsVectorDocumentByUid(chunk.getUid(), chunk.getName());
        } catch (Exception e) {
            log.error("同步Chunk向量状态失败: uid={}, error={}", chunk.getUid(), e.getMessage(), e);
            chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.ERROR.name());
            chunkRestService.evictChunkCacheAllEntries();
            return chunkRestService.findByUidNoCache(chunk.getUid())
                    .orElseThrow(() -> new RuntimeException("Chunk not found with UID: " + chunk.getUid()));
        }

        String nextStatus = exists ? ChunkStatusEnum.SUCCESS.name() : ChunkStatusEnum.NEW.name();
        chunkRestService.updateVectorStatusOnly(chunk.getUid(), nextStatus);
        chunkRestService.evictChunkCacheAllEntries();
        return chunkRestService.findByUidNoCache(chunk.getUid())
                .orElseThrow(() -> new RuntimeException("Chunk not found with UID: " + chunk.getUid()));
    }

    /**
     * 根据知识库kbUid批量同步Chunk向量索引状态到数据库
     */
    public Map<String, Object> syncVectorStatusByKbUid(ChunkRequest request) {
        String kbUid = request.getKbUid();
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<ChunkEntity> chunkList = chunkRestService.findByKbUidNoCache(kbUid);

        int successCount = 0;
        int newCount = 0;
        int errorCount = 0;

        for (ChunkEntity chunk : chunkList) {
            try {
                boolean exists = existsVectorDocumentByUid(chunk.getUid(), chunk.getName());
                if (exists) {
                    chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.SUCCESS.name());
                    successCount++;
                } else {
                    chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.NEW.name());
                    newCount++;
                }
            } catch (Exception e) {
                chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.ERROR.name());
                errorCount++;
            }
        }

        chunkRestService.evictChunkCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("total", chunkList.size());
        result.put("success", successCount);
        result.put("new", newCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 按知识库kbUid批量删除Chunk向量索引，并同步更新数据库状态
     */
    public Map<String, Object> deleteAllVectorIndexByKbUidAndSyncStatus(ChunkRequest request) {
        String kbUid = request.getKbUid();
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<ChunkEntity> chunkList = chunkRestService.findByKbUidNoCache(kbUid);
        int total = chunkList.size();

        List<String> docIdsToDelete = new ArrayList<>();
        for (ChunkEntity chunk : chunkList) {
            docIdsToDelete.add("chunk_" + chunk.getUid());
        }

        int successCount = 0;
        int errorCount = 0;

        try {
            if (!docIdsToDelete.isEmpty()) {
                vectorStore.delete(docIdsToDelete);
            }
            for (ChunkEntity chunk : chunkList) {
                chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.NEW.name());
            }
            successCount = total;
        } catch (Exception e) {
            log.warn("批量删除Chunk向量索引失败，将回退逐条删除: kbUid={}, error={}", kbUid, e.getMessage());
            for (ChunkEntity chunk : chunkList) {
                try {
                    vectorStore.delete(List.of("chunk_" + chunk.getUid()));
                    chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.NEW.name());
                    successCount++;
                } catch (Exception ex) {
                    chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.ERROR.name());
                    errorCount++;
                }
            }
        }

        chunkRestService.evictChunkCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("total", total);
        result.put("success", successCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 删除Chunk向量索引，并同步更新数据库状态
     */
    public Boolean deleteVectorIndexAndSyncStatus(ChunkRequest request) {
        Optional<ChunkEntity> chunkOpt = chunkRestService.findByUidNoCache(request.getUid());
        if (chunkOpt.isEmpty()) {
            throw new RuntimeException("Chunk not found with UID: " + request.getUid());
        }

        ChunkEntity chunk = chunkOpt.get();

        Boolean deleted;
        try {
            vectorStore.delete(List.of("chunk_" + chunk.getUid()));
            deleted = true;
        } catch (Exception e) {
            log.warn("删除Chunk向量索引失败: uid={}, error={}", chunk.getUid(), e.getMessage());
            deleted = false;
        }

        if (Boolean.TRUE.equals(deleted)) {
            chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.NEW.name());
        } else {
            chunkRestService.updateVectorStatusOnly(chunk.getUid(), ChunkStatusEnum.ERROR.name());
        }

        chunkRestService.evictChunkCacheAllEntries();
        return deleted;
    }

    private boolean existsVectorDocumentByUid(String uid, String queryHint) {
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "CHUNK")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query((queryHint == null || queryHint.isBlank()) ? "ping" : queryHint)
                .filterExpression(expression)
                .topK(1)
                .build();

        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        return docs != null && !docs.isEmpty();
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

            // 强制限定数据源类型，避免跨类型（FAQ/TEXT/WEBPAGE）文档误召回
            FilterExpressionBuilder.Op sourceTypeOp = expressionBuilder.eq("sourceType", "CHUNK");
            
            // 添加可选的过滤条件
            FilterExpressionBuilder.Op finalOp = expressionBuilder.and(enabledOp, sourceTypeOp);
            
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
                String kbaseUid = (String) metadata.getOrDefault(
                    KbaseConst.KBASE_KB_UID,
                    metadata.getOrDefault(KbaseConst.KBASE_KB_UID_LEGACY, ""));
                String catUid = (String) metadata.getOrDefault("categoryUid", "");
                String organization = (String) metadata.getOrDefault("orgUid", "");
                String document = (String) metadata.getOrDefault("docId", "");
                String file = (String) metadata.getOrDefault("fileUid", "");
                String fileName = (String) metadata.getOrDefault("fileName", "");
                String fileUrl = (String) metadata.getOrDefault("fileUrl", "");
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
                    .fileName(fileName)
                    .fileUrl(fileUrl)
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
