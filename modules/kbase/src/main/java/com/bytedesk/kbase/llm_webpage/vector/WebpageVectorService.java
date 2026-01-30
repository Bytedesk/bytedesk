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
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
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

    private final ElasticsearchVectorStore vectorStore;

    private final WebpageRestService webpageRestService;

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
     * 将网页内容添加到向量存储中
     * 
     * @param webpage 网页实体
     */
    @Transactional
    public void indexWebpageVector(WebpageEntity webpage) {
        log.info("开始向量索引网页: {}, ID: {}", webpage.getTitle(), webpage.getUid());

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
            Map<String, Object> metadata = Map.of(
                    "uid", currentWebpage.getUid(),
                    "title", title,
                    "url", currentWebpage.getUrl() != null ? currentWebpage.getUrl() : "",
                    KbaseConst.KBASE_KB_UID, currentWebpage.getKbase() != null ? currentWebpage.getKbase().getUid() : "",
                    "categoryUid", currentWebpage.getCategoryUid() != null ? currentWebpage.getCategoryUid() : "",
                    "orgUid", currentWebpage.getOrgUid(),
                    "enabled", Boolean.toString(currentWebpage.getEnabled()),
                    "tags", tags,
                    // 用于向量检索侧按数据源类型过滤（ALL/FAQ/TEXT/CHUNK/WEBPAGE）
                    "sourceType", "WEBPAGE");

            // 创建文档
            Document document = new Document(id, combinedContent, metadata);

            // 添加新文档到向量存储
            log.info("向向量存储添加文档: {}", id);
            vectorStore.add(List.of(document));
            log.info("已成功添加文档到向量存储: {}", id);

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

        } catch (Exception e) {
            log.error("网页向量索引失败: {}, 错误: {}", currentWebpage.getTitle(), e.getMessage(), e);

            try {
                webpageRestService.updateVectorStatusOnly(currentWebpage.getUid(), ChunkStatusEnum.ERROR.name());
                webpageRestService.evictWebpageCacheAllEntries();
            } catch (Exception saveEx) {
                log.error("更新网页向量索引状态失败: {}, 错误: {}", currentWebpage.getUid(), saveEx.getMessage());
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
        List<WebpageEntity> webpageList = webpageRestService.findByKbUid(request.getKbUid());
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
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<WebpageEntity> webpageList = webpageRestService.findByKbUidNoCache(kbUid);

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
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<WebpageEntity> webpageList = webpageRestService.findByKbUidNoCache(kbUid);
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

        try {
            List<String> sanitizedToDelete = sanitizeDocIds(docIdsToDelete);
            if (!sanitizedToDelete.isEmpty()) {
                vectorStore.delete(sanitizedToDelete);
            }
            for (WebpageEntity webpage : webpageList) {
                webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
                webpageRestService.updateDocIdListOnly(webpage.getUid(), new ArrayList<>());
            }
            successCount = total;
        } catch (Exception e) {
            log.warn("批量删除Webpage向量索引失败，将回退逐条删除: kbUid={}, error={}", kbUid, e.getMessage());
            for (WebpageEntity webpage : webpageList) {
                try {
                    if (webpage.getDocIdList() == null || webpage.getDocIdList().isEmpty()) {
                        vectorStore.delete(List.of(buildDefaultVectorDocId(webpage.getUid())));
                        webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
                        webpageRestService.updateDocIdListOnly(webpage.getUid(), new ArrayList<>());
                        successCount++;
                        continue;
                    }

                    Boolean deleted = deleteWebpageVector(webpage);
                    if (Boolean.TRUE.equals(deleted)) {
                        webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.NEW.name());
                        webpageRestService.updateDocIdListOnly(webpage.getUid(), new ArrayList<>());
                        successCount++;
                    } else {
                        webpageRestService.updateVectorStatusOnly(webpage.getUid(), ChunkStatusEnum.ERROR.name());
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

        List<Document> docs = vectorStore.similaritySearch(searchRequest);
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

        // 构建最终的过滤表达式
        Expression expression = finalOp.build();

        // 构建搜索请求
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .filterExpression(expression)
                .topK(limit) // 限制返回的结果数量
                .build();

        // 执行相似度搜索
        List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

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
            .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
            .orgUid((String) metadata.getOrDefault("orgUid", ""))
            .enabled(Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true")))
            .build();
    }
}
