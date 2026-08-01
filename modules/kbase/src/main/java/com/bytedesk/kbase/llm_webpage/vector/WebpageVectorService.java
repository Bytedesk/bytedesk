/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-31 09:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 09:30:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage.vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_webpage.WebpageEntity;
import com.bytedesk.kbase.llm_webpage.WebpageRequest;
import com.bytedesk.kbase.llm_webpage.WebpageRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
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
 * 网页向量检索服务
 * 用于处理网页的向量存储和相似度搜索
 * 
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
// @ConditionalOnBean(org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore.class)
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WebpageVectorService {

    private final KbaseVectorStoreResolver vectorStoreResolver;

    private final WebpageRestService webpageRestService;

    private final KbaseTranslationRepository kbaseTranslationRepository;

    private final com.bytedesk.kbase.llm_embedding.LlmEmbeddingRestService llmEmbeddingRestService;

    public Map<String, Object> queryVectorByUid(WebpageRequest request) {
        String uid = request.getUid();
        if (!StringUtils.hasText(uid)) {
            throw new RuntimeException("uid is required");
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "WEBPAGE")).build();

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
     * 将网页内容添加到向量存储中
     * 
     * @param webpage 网页实体
     */
    @Transactional
    public void indexWebpageVector(WebpageEntity webpage) {
        log.info("开始向量索引网页: {}, ID: {}", webpage.getTitle(), webpage.getUid());
        long startMs = System.currentTimeMillis();

        // 在处理前先获取最新的网页实体
        Optional<WebpageEntity> currentWebpageOpt = webpageRestService.findByUidWithKbaseNoCache(webpage.getUid());
        if (!currentWebpageOpt.isPresent()) {
            log.error("网页实体不存在，无法创建向量索引: {}", webpage.getUid());
            throw new RuntimeException("网页实体不存在: " + webpage.getUid());
        }

        WebpageEntity currentWebpage = currentWebpageOpt.get();
        log.info("获取到最新网页实体，当前向量状态: {}, ID: {}", currentWebpage.getVectorStatus(), currentWebpage.getUid());

        try {
            // 1. 为标题和内容创建文档（带有元数据）
            String id = "webpage_" + currentWebpage.getUid();
            String title = currentWebpage.getTitle() != null ? currentWebpage.getTitle() : "";
            String content = currentWebpage.getContent() != null ? currentWebpage.getContent() : "";
            String combinedContent = title + "\n" + content;

            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", currentWebpage.getTagList());

            // 元数据
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("uid", currentWebpage.getUid());
                metadata.put("sourceUid", currentWebpage.getUid());
                metadata.put("title", title);
                metadata.put("url", currentWebpage.getUrl() != null ? currentWebpage.getUrl() : "");
                metadata.put(KbaseConst.KBASE_KB_UID, currentWebpage.getKbase() != null ? currentWebpage.getKbase().getUid() : "");
                metadata.put("categoryUid", currentWebpage.getCategoryUid() != null ? currentWebpage.getCategoryUid() : "");
                metadata.put("orgUid", currentWebpage.getOrgUid());
                metadata.put("enabled", Boolean.toString(currentWebpage.getEnabled()));
                metadata.put("tags", tags);
                metadata.put("language", currentWebpage.getKbase() != null && StringUtils.hasText(currentWebpage.getKbase().getSourceLanguage()) ? currentWebpage.getKbase().getSourceLanguage().trim().toUpperCase() : "");
                metadata.put("sourceLanguage", currentWebpage.getKbase() != null && StringUtils.hasText(currentWebpage.getKbase().getSourceLanguage()) ? currentWebpage.getKbase().getSourceLanguage().trim().toUpperCase() : "");
                metadata.put("translated", Boolean.FALSE.toString());
                metadata.put("sourceType", "WEBPAGE");

            // 创建文档
            Document document = new Document(id, combinedContent, metadata);

            // 添加新文档到向量存储
            log.info("向向量存储添加文档: {}", id);
            resolveStoreByWebpage(currentWebpage).add(List.of(document));
            log.info("已成功添加文档到向量存储: {}", id);
            reindexTranslatedWebpageVectors(currentWebpage);

            // 3. 更新网页实体中的文档ID列表
            List<String> docIdList = currentWebpage.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            } else if (docIdList.contains(id)) {
                // 如果已包含该ID，先移除再添加以确保唯一性
                docIdList.remove(id);
                log.info("从文档ID列表中移除重复ID: {}", id);
            }

            // 添加文档ID并更新状态
            docIdList.add(id);
            // 仅更新向量相关字段，避免保存整条实体导致 kbase 关联被置空
            webpageRestService.updateDocIdListOnly(currentWebpage.getUid(), docIdList);
            webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.SUCCESS.name());
            webpageRestService.evictWebpageCacheAllEntries();

            // 记录向量化成功
            long costMs = System.currentTimeMillis() - startMs;
            try {
                KbaseVectorStoreResolver.EmbeddingInfo embeddingInfo = vectorStoreResolver.getEmbeddingInfo(currentWebpage.getKbase());
                String embProvider = embeddingInfo != null ? embeddingInfo.provider() : null;
                String embModel = embeddingInfo != null ? embeddingInfo.model() : null;
                Integer embDimensions = embeddingInfo != null ? embeddingInfo.dimensions() : null;
                llmEmbeddingRestService.recordEmbedding("WEBPAGE", currentWebpage.getUid(), currentWebpage.getOrgUid(),
                        embProvider, embModel, embDimensions, combinedContent, "SUCCESS", null, costMs);
            } catch (Exception recEx) {
                log.warn("记录WEBPAGE向量化历史失败: uid={}, error={}", currentWebpage.getUid(), recEx.getMessage());
            }

        } catch (Exception e) {
            log.error("网页向量索引失败: {}, 错误: {}", currentWebpage.getTitle(), e.getMessage(), e);

            try {
                webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.ERROR.name());
                webpageRestService.evictWebpageCacheAllEntries();
            } catch (Exception saveEx) {
                log.error("更新网页向量索引状态失败: {}, 错误: {}", currentWebpage.getUid(), saveEx.getMessage());
            }

            // 记录向量化失败
            try {
                long failCostMs = System.currentTimeMillis() - startMs;
                KbaseVectorStoreResolver.EmbeddingInfo embeddingInfo = vectorStoreResolver.getEmbeddingInfo(currentWebpage.getKbase());
                String embProvider = embeddingInfo != null ? embeddingInfo.provider() : null;
                String embModel = embeddingInfo != null ? embeddingInfo.model() : null;
                Integer embDimensions = embeddingInfo != null ? embeddingInfo.dimensions() : null;
                String errContent = (currentWebpage.getTitle() != null ? currentWebpage.getTitle() : "")
                        + "\n" + (currentWebpage.getContent() != null ? currentWebpage.getContent() : "");
                llmEmbeddingRestService.recordEmbedding("WEBPAGE", currentWebpage.getUid(), currentWebpage.getOrgUid(),
                        embProvider, embModel, embDimensions, errContent, "ERROR", e.getMessage(), failCostMs);
            } catch (Exception recEx) {
                log.warn("记录WEBPAGE向量化失败历史失败: uid={}, error={}", currentWebpage.getUid(), recEx.getMessage());
            }

            throw new RuntimeException("创建向量索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新网页的向量索引
     * 
     * @param request 网页请求对象
     */
    public void updateVectorIndex(WebpageRequest request) {
        Optional<WebpageEntity> webpageOpt = webpageRestService.findByUidWithKbaseNoCache(request.getUid());
        if (webpageOpt.isPresent()) {
            WebpageEntity webpage = webpageOpt.get();
            // 删除旧的向量索引
            deleteWebpageVector(webpage);
            // 创建新的向量索引
            indexWebpageVector(webpage);
        } else {
            log.warn("未找到要更新向量索引的网页: {}", request.getUid());
        }
    }

    /**
     * 更新所有网页的向量索引
     * 
     * @param request 网页请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(WebpageRequest request) {
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && !StringUtils.hasText(request.getKbUid())) {
            throw new RuntimeException("kbUid is required");
        }

        List<WebpageEntity> webpageList = superUser
                ? webpageRestService.findAllNotDeletedNoCache()
                : webpageRestService.findByKbUid(request.getKbUid());
        webpageList.forEach(webpage -> {
            try {
                // 删除旧的向量索引
                deleteWebpageVector(webpage);
                // 创建新的向量索引
                indexWebpageVector(webpage);
            } catch (Exception e) {
                log.error("更新网页向量索引失败: {}, 错误: {}", webpage.getTitle(), e.getMessage());
            }
        });
        log.info("Updated vector index for {} webpages, superUser={}, kbUid={}", webpageList.size(), superUser, request.getKbUid());
    }

    /**
     * 同步Webpage向量索引状态到数据库
     */
    public WebpageEntity syncVectorStatus(WebpageRequest request) {
        Optional<WebpageEntity> webpageOpt = webpageRestService.findByUidNoCache(request.getUid());
        if (webpageOpt.isEmpty()) {
            throw new RuntimeException("Webpage not found with UID: " + request.getUid());
        }

        WebpageEntity webpage = webpageOpt.get();

        boolean exists;
        try {
            exists = existsVectorDocumentByUid(webpage.getUid(), webpage.getTitle());
        } catch (Exception e) {
            log.error("同步Webpage向量状态失败: uid={}, error={}", webpage.getUid(), e.getMessage(), e);
            webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.ERROR.name());
            webpageRestService.evictWebpageCacheAllEntries();
            return webpageRestService.findByUidNoCache(webpage.getUid())
                    .orElseThrow(() -> new RuntimeException("Webpage not found with UID: " + webpage.getUid()));
        }

        String nextStatus = exists ? ChunkStatusEnum.SUCCESS.name() : ChunkStatusEnum.NEW.name();
        webpageRestService.updateVectorStatusOnly(webpage.getUid(), nextStatus);
        webpageRestService.evictWebpageCacheAllEntries();
        return webpageRestService.findByUidNoCache(webpage.getUid())
                .orElseThrow(() -> new RuntimeException("Webpage not found with UID: " + webpage.getUid()));
    }

    /**
     * 根据知识库kbUid批量同步Webpage向量索引状态到数据库
     */
    public Map<String, Object> syncVectorStatusByKbUid(WebpageRequest request) {
        String kbUid = request.getKbUid();
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && (kbUid == null || kbUid.isBlank())) {
            throw new RuntimeException("kbUid is required");
        }

        List<WebpageEntity> webpageList = superUser
                ? webpageRestService.findAllNotDeletedNoCache()
                : webpageRestService.findByKbUidNoCache(kbUid);

        int successCount = 0;
        int newCount = 0;
        int errorCount = 0;

        for (WebpageEntity webpage : webpageList) {
            try {
                boolean exists = existsVectorDocumentByUid(webpage.getUid(), webpage.getTitle());
                if (exists) {
                    webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.SUCCESS.name());
                    successCount++;
                } else {
                    webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
                    newCount++;
                }
            } catch (Exception e) {
                webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.ERROR.name());
                errorCount++;
            }
        }

        webpageRestService.evictWebpageCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("superUser", superUser);
        result.put("total", webpageList.size());
        result.put("success", successCount);
        result.put("new", newCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 按知识库kbUid批量删除Webpage向量索引，并同步更新数据库状态
     */
    public Map<String, Object> deleteAllVectorIndexByKbUidAndSyncStatus(WebpageRequest request) {
        String kbUid = request.getKbUid();
        boolean superUser = Boolean.TRUE.equals(request.getSuperUser());
        if (!superUser && (kbUid == null || kbUid.isBlank())) {
            throw new RuntimeException("kbUid is required");
        }

        List<WebpageEntity> webpageList = superUser
                ? webpageRestService.findAllNotDeletedNoCache()
                : webpageRestService.findByKbUidNoCache(kbUid);
        int total = webpageList.size();

        List<String> docIdsToDelete = new ArrayList<>();
        for (WebpageEntity webpage : webpageList) {
            List<String> sanitizedDocIds = sanitizeDocIds(webpage.getDocIdList());
            if (sanitizedDocIds.isEmpty()) {
                docIdsToDelete.add(buildDefaultVectorDocId(webpage.getUid()));
            } else {
                docIdsToDelete.addAll(sanitizedDocIds);
            }
        }

        int successCount = 0;
        int errorCount = 0;
        VectorStore vectorStore = resolveStoreByKbUid(kbUid);

        try {
            List<String> sanitizedToDelete = sanitizeDocIds(docIdsToDelete);
            if (!superUser && !sanitizedToDelete.isEmpty()) {
                vectorStore.delete(sanitizedToDelete);
                for (WebpageEntity webpage : webpageList) {
                    webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
                    webpageRestService.updateDocIdListOnly(webpage.getUid(), new ArrayList<>());
                }
                successCount = total;
            }
        } catch (Exception e) {
            log.warn("批量删除Webpage向量索引失败，将回退逐条删除: kbUid={}, error={}", kbUid, e.getMessage());
        }

        if (superUser || successCount != total) {
            successCount = 0;
            errorCount = 0;
            for (WebpageEntity webpage : webpageList) {
                try {
                    WebpageEntity currentWebpage = webpageRestService.findByUidWithKbaseNoCache(webpage.getUid()).orElse(webpage);
                    if (currentWebpage.getDocIdList() == null || currentWebpage.getDocIdList().isEmpty()) {
                        resolveStoreByWebpage(currentWebpage).delete(List.of(buildDefaultVectorDocId(currentWebpage.getUid())));
                        webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.NEW.name());
                        webpageRestService.updateDocIdListOnly(currentWebpage.getUid(), new ArrayList<>());
                        successCount++;
                        continue;
                    }

                    Boolean deleted = deleteWebpageVector(currentWebpage);
                    if (Boolean.TRUE.equals(deleted)) {
                        webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.NEW.name());
                        webpageRestService.updateDocIdListOnly(currentWebpage.getUid(), new ArrayList<>());
                        successCount++;
                    } else {
                        webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.ERROR.name());
                        errorCount++;
                    }
                } catch (Exception ex) {
                    webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.ERROR.name());
                    errorCount++;
                }
            }
        }

        webpageRestService.evictWebpageCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("superUser", superUser);
        result.put("total", total);
        result.put("success", successCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 删除Webpage向量索引，并同步更新数据库状态
     */
    public Boolean deleteVectorIndexAndSyncStatus(WebpageRequest request) {
        Optional<WebpageEntity> webpageOpt = webpageRestService.findByUidNoCache(request.getUid());
        if (webpageOpt.isEmpty()) {
            throw new RuntimeException("Webpage not found with UID: " + request.getUid());
        }

        WebpageEntity webpage = webpageOpt.get();
        VectorStore vectorStore = resolveStoreByWebpage(webpage);

        if (webpage.getDocIdList() == null || webpage.getDocIdList().isEmpty()) {
            try {
                vectorStore.delete(List.of(buildDefaultVectorDocId(webpage.getUid())));
            } catch (Exception e) {
                log.warn("按默认docId删除向量索引失败（将继续走常规删除逻辑）: uid={}, error={}", webpage.getUid(), e.getMessage());
            }
        }

        Boolean deleted = deleteWebpageVector(webpage);
        if (Boolean.TRUE.equals(deleted)) {
            webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
            webpageRestService.updateDocIdListOnly(webpage.getUid(), new ArrayList<>());
        } else {
            webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.ERROR.name());
        }

        webpageRestService.evictWebpageCacheAllEntries();
        return deleted;
    }

    private boolean existsVectorDocumentByUid(String uid, String queryHint) {
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "WEBPAGE")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query((queryHint == null || queryHint.isBlank()) ? "ping" : queryHint)
                .filterExpression(expression)
                .topK(1)
                .build();

        List<Document> docs = resolveStoreByUid(uid).similaritySearch(searchRequest);
        return docs != null && !docs.isEmpty();
    }

    /**
     * 从向量存储中删除网页向量文档
     * 修改为不更新实体的方式，只进行向量删除操作，避免实体并发修改冲突
     * 
     * @param webpage 网页实体
     * @return 删除成功返回true，否则返回false
     */
    @Transactional(readOnly = true) // 只读事务，因为我们不会修改实体
    public Boolean deleteWebpageVector(WebpageEntity webpage) {
        log.info("从向量索引中删除网页: {}, ID: {}", webpage.getTitle(), webpage.getUid());
        try {
            VectorStore vectorStore = resolveStoreByWebpage(webpage);
            // 获取网页文档ID列表
            List<String> docIdList = sanitizeDocIds(webpage.getDocIdList());
            if (docIdList.isEmpty()) {
                String fallbackId = buildDefaultVectorDocId(webpage.getUid());
                if (!StringUtils.hasText(fallbackId)) {
                    log.info("网页没有有效的向量文档ID，无需删除: {}", webpage.getUid());
                    return true;
                }
                try {
                    log.info("网页文档ID列表为空/脏数据，尝试按默认docId删除: {}", fallbackId);
                    vectorStore.delete(List.of(fallbackId));
                } catch (Exception e) {
                    log.warn("按默认docId删除向量索引失败（将忽略并返回成功）: uid={}, error={}", webpage.getUid(), e.getMessage());
                }
                return true;
            }

            // 从向量存储中删除所有相关文档
            log.info("删除网页向量文档, 数量: {}, IDs: {}", docIdList.size(), docIdList);
            vectorStore.delete(docIdList);

            // 不再在此方法中更新实体状态，避免乐观锁冲突
            // 状态更新将在indexWebpageVector方法中完成

            log.info("成功从向量存储中删除网页文档: {}", webpage.getUid());
            return true;
        } catch (Exception e) {
            log.error("删除网页向量索引失败: {}, 错误: {}", webpage.getTitle(), e.getMessage(), e);
            return false;
        }
    }

    private static String buildDefaultVectorDocId(String uid) {
        if (!StringUtils.hasText(uid)) {
            return "";
        }
        return "webpage_" + uid;
    }

    private static List<String> sanitizeDocIds(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return new ArrayList<>();
        }

        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String raw : docIds) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            String trimmed = raw.trim();
            if (StringUtils.hasText(trimmed)) {
                unique.add(trimmed);
            }
        }
        return new ArrayList<>(unique);
    }

    /**
     * 在向量存储中进行语义搜索
     * 
     * @param query       搜索关键词
     * @param kbUid       知识库UID（可选）
     * @param categoryUid 分类UID（可选）
     * @param orgUid      组织UID（可选）
     * @param limit       返回结果数量限制
     * @return 相似度搜索结果列表
     */
    public List<WebpageVectorSearchResult> searchWebpageVector(String query, String kbUid, String categoryUid, String orgUid,
            int limit) {
        return searchWebpageVector(query, kbUid, categoryUid, orgUid, limit, null);
        }

        public List<WebpageVectorSearchResult> searchWebpageVector(String query, String kbUid, String categoryUid, String orgUid,
            int limit, String language) {
        log.info("网页向量搜索: query={}, kbUid={}, categoryUid={}, orgUid={}, limit={}", query, kbUid, categoryUid, orgUid, limit);

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 创建过滤表达式构建器
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();

        // 构建查询条件
        FilterExpressionBuilder.Op enabledOp = expressionBuilder.eq("enabled", "true");

        // 强制限定数据源类型，避免跨类型（FAQ/TEXT/CHUNK）文档误召回
        FilterExpressionBuilder.Op sourceTypeOp = expressionBuilder.eq("sourceType", "WEBPAGE");

        // 添加可选的过滤条件
        FilterExpressionBuilder.Op finalOp = expressionBuilder.and(enabledOp, sourceTypeOp);

        // 添加可选的过滤条件：知识库、分类、组织
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
        List<Document> similarDocuments = resolveStoreByKbUid(kbUid).similaritySearch(searchRequest);

        // 解析结果
        List<WebpageVectorSearchResult> resultList = new ArrayList<>();

        for (Document doc : similarDocuments) {
            // 从文档中提取元数据
            Map<String, Object> metadata = doc.getMetadata();
            String uid = (String) metadata.getOrDefault("uid", "");

            // 1. 通过UID查找对应的网页实体，以便获取完整信息
            // 注意：findByUid 走缓存时可能出现 kbase 关联丢失（导致 kbUid 为空），从而 fallback 到简化 metadata 并丢掉 content。
            // 这里改为直接查询包含 kbase 关联的实体，确保 answer/content 可用。
            Optional<WebpageEntity> webpageEntityOpt = webpageRestService.findByUidWithKbaseNoCache(uid);

            if (webpageEntityOpt.isPresent()) {
                WebpageEntity webpageEntity = webpageEntityOpt.get();

                // 2. 将WebpageEntity转换为WebpageVector
                WebpageVector webpageVector;
                try {
                    webpageVector = WebpageVector.fromWebpageEntity(webpageEntity);
                } catch (IllegalArgumentException ex) {
                    // 兼容历史脏数据：entity 可能缺 kbUid 等关键字段，但向量文档 metadata 中通常包含 kbUid
                    log.warn("WebpageVectorService search fallback to document metadata, uid={}, err={}", uid,
                            ex.getMessage());
                    webpageVector = createSimpleWebpageVectorFromDocument(doc);
                }

                // 3. 创建搜索结果对象
                WebpageVectorSearchResult result = WebpageVectorSearchResult.builder()
                        .webpageVector(webpageVector)
                        .score(doc.getScore().floatValue())
                        .highlightedTitle(webpageVector.getTitle()) // 如果需要高亮可以在这里处理
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 将相似度转换为距离，距离 = 1 - 相似度
                        .build();

                resultList.add(result);
            } else {
                // 如果找不到对应的网页实体，尝试从文档元数据构建一个简化的WebpageVector
                WebpageVector simpleWebpageVector = createSimpleWebpageVectorFromDocument(doc);

                WebpageVectorSearchResult result = WebpageVectorSearchResult.builder()
                        .webpageVector(simpleWebpageVector)
                        .score(doc.getScore().floatValue())
                        .highlightedTitle((String) metadata.getOrDefault("title", ""))
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 同上
                        .build();

                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 从文档创建简化版的WebpageVector对象
     * 用于在找不到对应的网页实体时提供基础信息
     * 
     * @param doc 文档对象
     * @return 简化的WebpageVector对象
     */
    private WebpageVector createSimpleWebpageVectorFromDocument(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();
        
        String kbUid = (String) metadata.getOrDefault(
                KbaseConst.KBASE_KB_UID,
                metadata.getOrDefault(KbaseConst.KBASE_KB_UID_LEGACY, ""));

        return WebpageVector.builder()
            .uid((String) metadata.getOrDefault("uid", ""))
            .title((String) metadata.getOrDefault("title", ""))
            .url((String) metadata.getOrDefault("url", ""))
            // 向量库 Document 的 text 是 title + "\n" + content（索引时写入），用于兜底补齐 answer。
            .content(doc.getText())
            .kbUid(kbUid)
            .sourceUid((String) metadata.getOrDefault("sourceUid", metadata.getOrDefault("uid", "")))
            .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
            .orgUid((String) metadata.getOrDefault("orgUid", ""))
            .enabled(Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true")))
                .language((String) metadata.getOrDefault("language", ""))
                .sourceLanguage((String) metadata.getOrDefault("sourceLanguage", ""))
                .translated(Boolean.parseBoolean((String) metadata.getOrDefault("translated", "false")))
            .build();
    }

    private VectorStore resolveStoreByUid(String uid) {
        return webpageRestService.findByUidWithKbaseNoCache(uid)
                .map(this::resolveStoreByWebpage)
                .orElseGet(vectorStoreResolver::resolveDefault);
    }

    private VectorStore resolveStoreByWebpage(WebpageEntity webpage) {
        if (webpage != null && webpage.getKbase() != null) {
            return vectorStoreResolver.resolveByKbase(webpage.getKbase());
        }
        return vectorStoreResolver.resolveDefault();
    }

    private VectorStore resolveStoreByKbUid(String kbUid) {
        if (StringUtils.hasText(kbUid)) {
            return vectorStoreResolver.resolveByKbUid(kbUid);
        }
        return vectorStoreResolver.resolveDefault();
    }

    private void reindexTranslatedWebpageVectors(WebpageEntity webpage) {
        deleteTranslatedWebpageVectors(webpage);

        String kbUid = webpage.getKbase() != null ? webpage.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid)) {
            return;
        }

        List<KbaseTranslationEntity> translations = kbaseTranslationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndDeletedFalse(
                        kbUid,
                        webpage.getUid(),
                        KbaseTranslationSourceTypeEnum.WEBPAGE.name());

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

            WebpageVector translatedVector = WebpageVector.fromTranslation(webpage, translation);
            if (!StringUtils.hasText(translatedVector.getTitle()) && !StringUtils.hasText(translatedVector.getContent())) {
                continue;
            }

            String docId = "webpage_translation_" + translation.getUid();
            String combinedContent = (translatedVector.getTitle() != null ? translatedVector.getTitle() : "")
                    + "\n"
                    + (translatedVector.getContent() != null ? translatedVector.getContent() : "");

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("uid", translation.getUid());
            metadata.put("sourceUid", webpage.getUid());
            metadata.put("title", translatedVector.getTitle() != null ? translatedVector.getTitle() : "");
            metadata.put("url", translatedVector.getUrl() != null ? translatedVector.getUrl() : "");
            metadata.put(KbaseConst.KBASE_KB_UID, kbUid);
            metadata.put("categoryUid", webpage.getCategoryUid() != null ? webpage.getCategoryUid() : "");
            metadata.put("orgUid", webpage.getOrgUid());
            metadata.put("enabled", Boolean.toString(translatedVector.getEnabled()));
            metadata.put("tags", translatedVector.getTagList() == null ? "" : String.join(",", translatedVector.getTagList()));
            metadata.put("language", translatedVector.getLanguage() != null ? translatedVector.getLanguage() : "");
            metadata.put("sourceLanguage", translatedVector.getSourceLanguage() != null ? translatedVector.getSourceLanguage() : "");
            metadata.put("translated", Boolean.TRUE.toString());
            metadata.put("sourceType", "WEBPAGE");

            documents.add(new Document(docId, combinedContent, metadata));
        }

        if (!documents.isEmpty()) {
            resolveStoreByWebpage(webpage).add(documents);
        }
    }

    private void deleteTranslatedWebpageVectors(WebpageEntity webpage) {
        if (webpage == null || webpage.getKbase() == null || !StringUtils.hasText(webpage.getUid())) {
            return;
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        FilterExpressionBuilder.Op finalOp = expressionBuilder.and(
                expressionBuilder.eq("sourceType", "WEBPAGE"),
                expressionBuilder.eq("sourceUid", webpage.getUid()));
        finalOp = expressionBuilder.and(finalOp, expressionBuilder.eq("translated", "true"));
        Expression expression = finalOp.build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query("ping")
                .filterExpression(expression)
                .topK(200)
                .build();

        List<Document> existingDocs = resolveStoreByWebpage(webpage).similaritySearch(searchRequest);
        if (existingDocs == null || existingDocs.isEmpty()) {
            return;
        }

        List<String> docIds = existingDocs.stream()
                .map(Document::getId)
                .filter(StringUtils::hasText)
                .toList();
        if (!docIds.isEmpty()) {
            resolveStoreByWebpage(webpage).delete(docIds);
        }
    }
}
