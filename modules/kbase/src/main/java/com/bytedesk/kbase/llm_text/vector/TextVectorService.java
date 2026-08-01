/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:45:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 13:24:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text.vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_text.TextRequest;
import com.bytedesk.kbase.llm_text.TextRestService;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;
import com.bytedesk.kbase.translation.KbaseTranslationRepository;
import com.bytedesk.kbase.translation.KbaseTranslationSourceTypeEnum;
import com.bytedesk.kbase.translation.KbaseTranslationStatusEnum;
import com.bytedesk.kbase.vector.KbaseVectorStoreResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Text向量检索服务
 * 用于处理Text的向量存储和相似度搜索
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
// @ConditionalOnBean(org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore.class)
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class TextVectorService {
    
    private final KbaseVectorStoreResolver vectorStoreResolver;
    
    private final TextRestService textRestService;

    private final KbaseTranslationRepository kbaseTranslationRepository;

    private final com.bytedesk.kbase.llm_embedding.LlmEmbeddingRestService llmEmbeddingRestService;

    public Map<String, Object> queryVectorByUid(TextRequest request) {
        String uid = request.getUid();
        if (!StringUtils.hasText(uid)) {
            throw new RuntimeException("uid is required");
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "TEXT")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query("ping")
                .filterExpression(expression)
                .topK(10)
                .build();

        List<Document> docs = resolveStoreByUid(uid).similaritySearch(searchRequest);
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
     * 将文本内容添加到向量存储中
     * @param text 文本实体
     */
    @Transactional
    public void indexTextVector(TextEntity text) {
        log.info("向量索引文本: {}", text.getTitle());
        long startMs = System.currentTimeMillis();
        
        try {
            // 1. 为标题和内容创建文档（带有元数据）
            String id = "text_" + text.getUid();
            // 将标题和内容合并，以便一起索引，确保标题具有更大的权重
            String content = text.getTitle() + "\n\n" + text.getContent();
            
            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", text.getTagList());
            
            // 元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("uid", text.getUid());
            metadata.put("sourceUid", text.getUid());
            metadata.put("title", text.getTitle());
            metadata.put(KbaseConst.KBASE_KB_UID, text.getKbase() != null ? text.getKbase().getUid() : "");
            metadata.put("categoryUid", text.getCategoryUid() != null ? text.getCategoryUid() : "");
            metadata.put("orgUid", text.getOrgUid());
            metadata.put("enabled", Boolean.toString(text.getEnabled()));
            metadata.put("tags", tags);
            metadata.put("type", text.getType());
            metadata.put("language", text.getKbase() != null && StringUtils.hasText(text.getKbase().getSourceLanguage()) ? text.getKbase().getSourceLanguage().trim().toUpperCase() : "");
            metadata.put("sourceLanguage", text.getKbase() != null && StringUtils.hasText(text.getKbase().getSourceLanguage()) ? text.getKbase().getSourceLanguage().trim().toUpperCase() : "");
            metadata.put("translated", Boolean.FALSE.toString());
            metadata.put("sourceType", "TEXT");
            
            // 创建文档
            Document document = new Document(id, content, metadata);
            
            // 2. 添加到向量存储
            // 检查是否已存在该文档ID
            VectorStore vectorStore = vectorStoreResolver.resolveByKbase(text.getKbase());
            checkAndDeleteExistingDoc(vectorStore, id);
            
            // 添加新文档
            vectorStore.add(List.of(document));
            reindexTranslatedTextVectors(text);
            
            // 3. 更新Text实体中的文档ID列表
            List<String> docIdList = text.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            }
            if (!docIdList.contains(id)) {
                docIdList.add(id);
                
                // 使用原生SQL直接更新字段，避免触发JPA @PostUpdate监听器
                // 和防止因updateVectorStatusOnly先更新了版本号导致的版本冲突
                textRestService.updateDocIdListOnly(text.getUid(), docIdList);
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.SUCCESS.name());
            }

            // 记录向量化成功
            long costMs = System.currentTimeMillis() - startMs;
            try {
                KbaseVectorStoreResolver.EmbeddingInfo embeddingInfo = vectorStoreResolver.getEmbeddingInfo(text.getKbase());
                String embProvider = embeddingInfo != null ? embeddingInfo.provider() : null;
                String embModel = embeddingInfo != null ? embeddingInfo.model() : null;
                Integer embDimensions = embeddingInfo != null ? embeddingInfo.dimensions() : null;
                llmEmbeddingRestService.recordEmbedding("TEXT", text.getUid(), text.getOrgUid(),
                        embProvider, embModel, embDimensions, content, "SUCCESS", null, costMs);
            } catch (Exception recEx) {
                log.warn("记录TEXT向量化历史失败: uid={}, error={}", text.getUid(), recEx.getMessage());
            }

            log.info("文本向量索引成功: {}", text.getTitle());
        } catch (Exception e) {
            log.error("文本向量索引失败: {}, 错误: {}", text.getTitle(), e.getMessage());

            // 记录向量化失败
            try {
                long failCostMs = System.currentTimeMillis() - startMs;
                KbaseVectorStoreResolver.EmbeddingInfo embeddingInfo = vectorStoreResolver.getEmbeddingInfo(text.getKbase());
                String embProvider = embeddingInfo != null ? embeddingInfo.provider() : null;
                String embModel = embeddingInfo != null ? embeddingInfo.model() : null;
                Integer embDimensions = embeddingInfo != null ? embeddingInfo.dimensions() : null;
                String errContent = text.getTitle() + "\n\n" + (text.getContent() != null ? text.getContent() : "");
                llmEmbeddingRestService.recordEmbedding("TEXT", text.getUid(), text.getOrgUid(),
                        embProvider, embModel, embDimensions, errContent, "ERROR", e.getMessage(), failCostMs);
            } catch (Exception recEx) {
                log.warn("记录TEXT向量化失败历史失败: uid={}, error={}", text.getUid(), recEx.getMessage());
            }

            // 设置向量索引状态为失败（使用原生SQL避免版本冲突）
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
            
            throw e;
        }
    }
    
    /**
     * 更新文本的向量索引
     * @param request 文本请求对象
     */
    public void updateVectorIndex(TextRequest request) {
        Optional<TextEntity> textOpt = textRestService.findByUidNoCache(request.getUid());
        if (textOpt.isPresent()) {
            TextEntity text = textOpt.get();
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.PROCESSING.name());
            try {
                // 删除旧的向量索引
                deleteTextVector(text);
                // 创建新的向量索引
                indexTextVector(text);
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.SUCCESS.name());
            } catch (Exception e) {
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
                throw e;
            } finally {
                textRestService.evictTextCacheAllEntries();
            }
        } else {
            log.warn("未找到要更新向量索引的文本: {}", request.getUid());
        }
    }
    
    /**
     * 更新所有文本的向量索引
     * @param request 文本请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(TextRequest request) {
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && !StringUtils.hasText(request.getKbUid())) {
            throw new RuntimeException("kbUid is required");
        }

        List<TextEntity> textList = superUser
                ? textRestService.findAllNotDeletedNoCache()
                : textRestService.findByKbUidNoCache(request.getKbUid());
        textList.forEach(text -> {
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.PROCESSING.name());
            try {
                // 删除旧的向量索引
                deleteTextVector(text);
                // 创建新的向量索引
                indexTextVector(text);
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.SUCCESS.name());
            } catch (Exception e) {
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
                log.error("更新文本向量索引失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            }
        });
        textRestService.evictTextCacheAllEntries();
        log.info("Updated vector index for {} texts, superUser={}, kbUid={}", textList.size(), superUser, request.getKbUid());
    }

    /**
     * 同步Text向量索引状态到数据库
     * - 如果向量存储中存在该uid文档：vectorStatus 置为 SUCCESS
     * - 否则：vectorStatus 置为 NEW
     */
    public TextEntity syncVectorStatus(TextRequest request) {
        Optional<TextEntity> textOpt = textRestService.findByUidNoCache(request.getUid());
        if (textOpt.isEmpty()) {
            throw new RuntimeException("Text not found with UID: " + request.getUid());
        }

        TextEntity text = textOpt.get();

        boolean exists;
        try {
            exists = existsVectorDocumentByUid(text.getUid(), text.getTitle());
        } catch (Exception e) {
            log.error("同步Text向量状态失败: uid={}, error={}", text.getUid(), e.getMessage(), e);
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
            textRestService.evictTextCacheAllEntries();
            return textRestService.findByUidNoCache(text.getUid())
                    .orElseThrow(() -> new RuntimeException("Text not found with UID: " + text.getUid()));
        }

        String nextStatus = exists ? ChunkStatusEnum.SUCCESS.name() : ChunkStatusEnum.NEW.name();
        textRestService.updateVectorStatusOnly(text.getUid(), nextStatus);
        textRestService.evictTextCacheAllEntries();
        return textRestService.findByUidNoCache(text.getUid())
                .orElseThrow(() -> new RuntimeException("Text not found with UID: " + text.getUid()));
    }

    /**
     * 根据知识库kbUid批量同步Text向量索引状态到数据库
     */
    public Map<String, Object> syncVectorStatusByKbUid(TextRequest request) {
        String kbUid = request.getKbUid();
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && (kbUid == null || kbUid.isBlank())) {
            throw new RuntimeException("kbUid is required");
        }

        List<TextEntity> textList = superUser
                ? textRestService.findAllNotDeletedNoCache()
                : textRestService.findByKbUidNoCache(kbUid);

        int successCount = 0;
        int newCount = 0;
        int errorCount = 0;

        for (TextEntity text : textList) {
            try {
                boolean exists = existsVectorDocumentByUid(text.getUid(), text.getTitle());
                if (exists) {
                    textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.SUCCESS.name());
                    successCount++;
                } else {
                    textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.NEW.name());
                    newCount++;
                }
            } catch (Exception e) {
                textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
                errorCount++;
            }
        }

        textRestService.evictTextCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("superUser", superUser);
        result.put("total", textList.size());
        result.put("success", successCount);
        result.put("new", newCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 按知识库kbUid批量删除Text向量索引，并同步更新数据库状态
     */
    public Map<String, Object> deleteAllVectorIndexByKbUidAndSyncStatus(TextRequest request) {
        String kbUid = request.getKbUid();
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && (kbUid == null || kbUid.isBlank())) {
            throw new RuntimeException("kbUid is required");
        }

        List<TextEntity> textList = superUser
                ? textRestService.findAllNotDeletedNoCache()
                : textRestService.findByKbUidNoCache(kbUid);
        int total = textList.size();

        List<String> docIdsToDelete = new ArrayList<>();
        for (TextEntity text : textList) {
            List<String> docIdList = text.getDocIdList();
            if (docIdList == null || docIdList.isEmpty()) {
                docIdsToDelete.add("text_" + text.getUid());
            } else {
                docIdsToDelete.addAll(docIdList);
            }
        }

        int successCount = 0;
        int errorCount = 0;

        try {
            if (!superUser && !docIdsToDelete.isEmpty()) {
                vectorStoreResolver.resolveByKbUid(kbUid).delete(docIdsToDelete);
                for (TextEntity text : textList) {
                    textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.NEW.name());
                    textRestService.updateDocIdListOnly(text.getUid(), new ArrayList<>());
                }
                successCount = total;
            }
        } catch (Exception e) {
            log.warn("批量删除Text向量索引失败，将回退逐条删除: kbUid={}, error={}", kbUid, e.getMessage());
        }

        if (superUser || successCount != total) {
            successCount = 0;
            errorCount = 0;
            for (TextEntity text : textList) {
                try {
                    TextEntity currentText = textRestService.findByUidWithKbaseNoCache(text.getUid()).orElse(text);
                    if (currentText.getDocIdList() == null || currentText.getDocIdList().isEmpty()) {
                        vectorStoreResolver.resolveByKbase(currentText.getKbase()).delete(List.of("text_" + currentText.getUid()));
                        textRestService.updateVectorStatusOnly(currentText.getUid(), ChunkStatusEnum.NEW.name());
                        textRestService.updateDocIdListOnly(currentText.getUid(), new ArrayList<>());
                        successCount++;
                        continue;
                    }

                    Boolean deleted = deleteTextVector(currentText);
                    if (Boolean.TRUE.equals(deleted)) {
                        textRestService.updateVectorStatusOnly(currentText.getUid(), ChunkStatusEnum.NEW.name());
                        textRestService.updateDocIdListOnly(currentText.getUid(), new ArrayList<>());
                        successCount++;
                    } else {
                        textRestService.updateVectorStatusOnly(currentText.getUid(), ChunkStatusEnum.ERROR.name());
                        errorCount++;
                    }
                } catch (Exception ex) {
                    textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
                    errorCount++;
                }
            }
        }

        textRestService.evictTextCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("superUser", superUser);
        result.put("total", total);
        result.put("success", successCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 删除Text向量索引，并同步更新数据库状态
     */
    public Boolean deleteVectorIndexAndSyncStatus(TextRequest request) {
        Optional<TextEntity> textOpt = textRestService.findByUidNoCache(request.getUid());
        if (textOpt.isEmpty()) {
            throw new RuntimeException("Text not found with UID: " + request.getUid());
        }

        TextEntity text = textOpt.get();

        if (text.getDocIdList() == null || text.getDocIdList().isEmpty()) {
            try {
                vectorStoreResolver.resolveByKbase(text.getKbase()).delete(List.of("text_" + text.getUid()));
            } catch (Exception e) {
                log.warn("按默认docId删除向量索引失败（将继续走常规删除逻辑）: uid={}, error={}", text.getUid(), e.getMessage());
            }
        }

        Boolean deleted = deleteTextVector(text);
        if (Boolean.TRUE.equals(deleted)) {
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.NEW.name());
            textRestService.updateDocIdListOnly(text.getUid(), new ArrayList<>());
        } else {
            textRestService.updateVectorStatusOnly(text.getUid(), ChunkStatusEnum.ERROR.name());
        }

        textRestService.evictTextCacheAllEntries();
        return deleted;
    }

    private boolean existsVectorDocumentByUid(String uid, String queryHint) {
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "TEXT")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query((queryHint == null || queryHint.isBlank()) ? "ping" : queryHint)
                .filterExpression(expression)
                .topK(1)
                .build();

        List<Document> docs = resolveStoreByUid(uid).similaritySearch(searchRequest);
        return docs != null && !docs.isEmpty();
    }
    
    /**
     * 从向量存储中删除文本
     * @param text 要删除的文本实体
     * @return 是否删除成功
     */
    public Boolean deleteTextVector(TextEntity text) {
        log.info("从向量索引中删除文本: {}", text.getTitle());
        
        try {
            // 获取文档ID列表
            List<String> docIdList = text.getDocIdList();
            if (docIdList != null && !docIdList.isEmpty()) {
                // 删除所有关联的向量文档
                vectorStoreResolver.resolveByKbase(text.getKbase()).delete(docIdList);
                
                // 清空文档ID列表并更新状态
                text.setDocIdList(new ArrayList<>());
                text.setVectorStatus(ChunkStatusEnum.NEW.name());
                textRestService.save(text);
                
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("从向量存储中删除文本失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            return false;
        }
    }
    
    /**
     * 在向量存储中进行语义搜索
     * @param query 搜索关键词
     * @param kbUid 知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid 组织UID（可选）
     * @param limit 返回结果数量限制
     * @return 相似度搜索结果列表
     */
    public List<TextVectorSearchResult> searchTextVector(String query, String kbUid, String categoryUid, String orgUid, int limit) {
        return searchTextVector(query, kbUid, categoryUid, orgUid, limit, null);
        }

        public List<TextVectorSearchResult> searchTextVector(String query, String kbUid, String categoryUid, String orgUid,
            int limit, String language) {
        log.info("向量搜索文本: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        // 创建过滤表达式构建器
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        
        // 构建查询条件
        FilterExpressionBuilder.Op enabledOp = expressionBuilder.eq("enabled", "true");

        // 强制限定数据源类型，避免跨类型（FAQ/CHUNK/WEBPAGE）文档误召回
        FilterExpressionBuilder.Op sourceTypeOp = expressionBuilder.eq("sourceType", "TEXT");
        
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

        if (StringUtils.hasText(language)) {
            FilterExpressionBuilder.Op languageOp = expressionBuilder.eq("language", language.trim().toUpperCase());
            finalOp = expressionBuilder.and(finalOp, languageOp);
        }
        
        // 构建最终的过滤表达式
        Expression expression = finalOp.build();
        
        // 构建搜索请求
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .filterExpression(expression)
                .topK(limit) // 限制返回的结果数量
                .build();
        
        // 执行相似度搜索
        List<Document> similarDocuments = vectorStoreResolver.resolveByKbUid(kbUid).similaritySearch(searchRequest);
        
        // 解析结果
        List<TextVectorSearchResult> resultList = new ArrayList<>();
        
        for (Document doc : similarDocuments) {
            // 从文档元数据构建 TextVector 对象
            Map<String, Object> metadata = doc.getMetadata();
            String docUid = (String) metadata.getOrDefault("uid", "");
            String docTitle = (String) metadata.getOrDefault("title", "");
            String docContent = doc.getText();
            String docType = (String) metadata.getOrDefault("type", "");
                String docKbUid = (String) metadata.getOrDefault(
                    KbaseConst.KBASE_KB_UID,
                    metadata.getOrDefault(KbaseConst.KBASE_KB_UID_LEGACY, ""));
            String docCategoryUid = (String) metadata.getOrDefault("categoryUid", "");
            String docOrgUid = (String) metadata.getOrDefault("orgUid", "");
            
            // 从标签字符串还原为列表
            String tagsStr = (String) metadata.getOrDefault("tags", "");
            List<String> tagList = new ArrayList<>();
            if (tagsStr != null && !tagsStr.isEmpty()) {
                tagList = List.of(tagsStr.split(","));
            }
            
            // 构建 TextVector 对象
            TextVector textVector = TextVector.builder()
                .uid(docUid)
                .title(docTitle)
                .content(docContent)
                .type(docType)
                .kbUid(docKbUid)
                .sourceUid((String) metadata.getOrDefault("sourceUid", docUid))
                .categoryUid(docCategoryUid)
                .orgUid(docOrgUid)
                .language((String) metadata.getOrDefault("language", ""))
                .sourceLanguage((String) metadata.getOrDefault("sourceLanguage", ""))
                .translated(Boolean.parseBoolean((String) metadata.getOrDefault("translated", "false")))
                .tagList(tagList)
                .build();
            
            // 构建搜索结果对象
            TextVectorSearchResult result = TextVectorSearchResult.builder()
                .textVector(textVector)
                .score(doc.getScore().floatValue()) // 获取相似度分数
                .distance((float)(1.0 - doc.getScore())) // 根据相似度计算距离（简化实现）
                .highlightedTitle(docTitle) // 基础实现，实际应用中可能需要真正的高亮处理
                .highlightedContent(docContent) // 基础实现，实际应用中可能需要真正的高亮处理
                .build();
            
            resultList.add(result);
        }
        
        return resultList;
    }
    
    /**
     * 检查并删除已存在的文档
     * @param docId 文档ID
     */
    private void checkAndDeleteExistingDoc(VectorStore vectorStore, String docId) {
        try {
            // 使用过滤表达式查询已存在的文档
            FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op idOp = expressionBuilder.eq("id", docId);
            Expression expression = idOp.build();
            
            // 构建搜索请求，只查询是否存在，不关心内容
            SearchRequest searchRequest = SearchRequest.builder()
                    .query("")
                    .filterExpression(expression)
                    .build();
            
            List<Document> existingDocs = vectorStore.similaritySearch(searchRequest);
            
            // 如果文档存在，则删除
            if (existingDocs != null && !existingDocs.isEmpty()) {
                log.info("删除已存在的文本向量文档: {}", docId);
                vectorStore.delete(List.of(docId));
            }
        } catch (Exception e) {
            log.warn("检查文档存在性时出错: {}, 错误: {}", docId, e.getMessage());
            // 安全起见，尝试直接删除
            try {
                vectorStore.delete(List.of(docId));
            } catch (Exception ex) {
                log.warn("删除可能存在的文档时出错: {}", ex.getMessage());
            }
        }
    }

    private VectorStore resolveStoreByUid(String uid) {
        return textRestService.findByUidNoCache(uid)
                .map(TextEntity::getKbase)
                .map(vectorStoreResolver::resolveByKbase)
                .orElseGet(vectorStoreResolver::resolveDefault);
    }

    private void reindexTranslatedTextVectors(TextEntity text) {
        deleteTranslatedTextVectors(text);

        String kbUid = text.getKbase() != null ? text.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid)) {
            return;
        }

        List<KbaseTranslationEntity> translations = kbaseTranslationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndDeletedFalse(
                        kbUid,
                        text.getUid(),
                        KbaseTranslationSourceTypeEnum.TEXT.name());

        List<Document> documents = new ArrayList<>();
        for (KbaseTranslationEntity translation : translations) {
            if (!Boolean.TRUE.equals(translation.getEnabled())) {
                continue;
            }
            if (!KbaseTranslationStatusEnum.SUCCESS.name().equals(translation.getTranslateStatus())) {
                continue;
            }
            if (!StringUtils.hasText(translation.getTargetLanguage())) {
                continue;
            }

            TextVector translatedVector = TextVector.fromTranslation(text, translation);
            if (!StringUtils.hasText(translatedVector.getTitle()) && !StringUtils.hasText(translatedVector.getContent())) {
                continue;
            }

            String docId = "text_translation_" + translation.getUid();
            String content = (translatedVector.getTitle() != null ? translatedVector.getTitle() : "")
                    + "\n\n"
                    + (translatedVector.getContent() != null ? translatedVector.getContent() : "");

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("uid", translation.getUid());
            metadata.put("sourceUid", text.getUid());
            metadata.put("title", translatedVector.getTitle());
            metadata.put(KbaseConst.KBASE_KB_UID, kbUid);
            metadata.put("categoryUid", text.getCategoryUid() != null ? text.getCategoryUid() : "");
            metadata.put("orgUid", text.getOrgUid());
            metadata.put("enabled", Boolean.toString(translatedVector.getEnabled()));
            metadata.put("tags", translatedVector.getTagList() == null ? "" : String.join(",", translatedVector.getTagList()));
            metadata.put("type", translatedVector.getType());
            metadata.put("language", translatedVector.getLanguage() != null ? translatedVector.getLanguage() : "");
            metadata.put("sourceLanguage", translatedVector.getSourceLanguage() != null ? translatedVector.getSourceLanguage() : "");
            metadata.put("translated", Boolean.TRUE.toString());
            metadata.put("sourceType", "TEXT");

            documents.add(new Document(docId, content, metadata));
        }

        if (!documents.isEmpty()) {
            vectorStoreResolver.resolveByKbase(text.getKbase()).add(documents);
        }
    }

    private void deleteTranslatedTextVectors(TextEntity text) {
        if (text.getKbase() == null || !StringUtils.hasText(text.getUid())) {
            return;
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        FilterExpressionBuilder.Op finalOp = expressionBuilder.and(
                expressionBuilder.eq("sourceType", "TEXT"),
                expressionBuilder.eq("sourceUid", text.getUid()));
        finalOp = expressionBuilder.and(finalOp, expressionBuilder.eq("translated", "true"));
        Expression expression = finalOp.build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query("ping")
                .filterExpression(expression)
                .topK(200)
                .build();

        List<Document> existingDocs = vectorStoreResolver.resolveByKbase(text.getKbase()).similaritySearch(searchRequest);
        if (existingDocs == null || existingDocs.isEmpty()) {
            return;
        }

        List<String> docIds = existingDocs.stream()
                .map(Document::getId)
                .filter(StringUtils::hasText)
                .toList();
        if (!docIds.isEmpty()) {
            vectorStoreResolver.resolveByKbase(text.getKbase()).delete(docIds);
        }
    }
}
