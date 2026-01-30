/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 14:20:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 13:20:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRequest;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.FaqStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FAQ向量检索服务
 * 用于处理FAQ的向量存储和相似度搜索
 * 
 * @author jackning
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class FaqVectorService {

    private final ElasticsearchVectorStore vectorStore;

    private final FaqRestService faqRestService;

    private final KbaseRestService kbaseRestService;

    public Map<String, Object> queryVectorByUid(FaqRequest request) {
        String uid = request.getUid();
        if (!StringUtils.hasText(uid)) {
            throw new RuntimeException("uid is required");
        }

        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "FAQ")).build();

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

    private FaqEntity ensureFaqHasKbase(FaqEntity faq, String kbUidHint) {
        if (faq.getKbase() != null && StringUtils.hasText(faq.getKbase().getUid())) {
            return faq;
        }

        if (!StringUtils.hasText(kbUidHint)) {
            return faq;
        }

        var kbaseOpt = kbaseRestService.findByUid(kbUidHint);
        if (kbaseOpt.isEmpty()) {
            return faq;
        }

        // 只更新关联，避免 save 整实体覆盖 elasticStatus 等字段
        faqRestService.updateKbaseOnly(faq.getUid(), kbaseOpt.get());
        faq.setKbase(kbaseOpt.get());
        faqRestService.evictFaqCacheAllEntries();
        return faq;
    }

    /**
     * 同步FAQ向量索引状态到数据库
     * - 如果向量存储中存在该uid文档：vectorStatus 置为 SUCCESS
     * - 否则：vectorStatus 置为 NEW
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FaqEntity syncVectorStatus(FaqRequest request) {
        Optional<FaqEntity> faqOpt = faqRestService.findByUidNoCache(request.getUid());
        if (faqOpt.isEmpty()) {
            throw new RuntimeException("FAQ not found with UID: " + request.getUid());
        }

        FaqEntity faq = faqOpt.get();

        boolean exists;
        try {
            exists = existsVectorDocumentByUid(faq.getUid(), faq.getQuestion());
        } catch (Exception e) {
            log.error("同步FAQ向量状态失败: uid={}, error={}", faq.getUid(), e.getMessage(), e);
            faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.ERROR.name());
            faqRestService.evictFaqCacheAllEntries();
            return faqRestService.findByUidNoCache(faq.getUid())
                    .orElseThrow(() -> new RuntimeException("FAQ not found with UID: " + faq.getUid()));
        }

        String nextStatus = exists ? FaqStatusEnum.SUCCESS.name() : FaqStatusEnum.NEW.name();
        faqRestService.updateVectorStatusOnly(faq.getUid(), nextStatus);
        faqRestService.evictFaqCacheAllEntries();
        return faqRestService.findByUidNoCache(faq.getUid())
                .orElseThrow(() -> new RuntimeException("FAQ not found with UID: " + faq.getUid()));
    }

    /**
     * 根据知识库kbUid批量同步FAQ向量索引状态到数据库
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> syncVectorStatusByKbUid(FaqRequest request) {
        String kbUid = request.getKbUid();
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<FaqEntity> faqList = faqRestService.findByKbUidNoCache(kbUid);

        int successCount = 0;
        int newCount = 0;
        int errorCount = 0;

        for (FaqEntity faq : faqList) {
            try {
                boolean exists = existsVectorDocumentByUid(faq.getUid(), faq.getQuestion());
                if (exists) {
                    faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.SUCCESS.name());
                    successCount++;
                } else {
                    faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.NEW.name());
                    newCount++;
                }
            } catch (Exception e) {
                faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.ERROR.name());
                errorCount++;
            }
        }

        faqRestService.evictFaqCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("total", faqList.size());
        result.put("success", successCount);
        result.put("new", newCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 按知识库kbUid批量删除FAQ向量索引，并同步更新数据库状态
     * - 删除成功/或无文档：vectorStatus 置为 NEW，清空 docIdList
     * - 删除失败：vectorStatus 置为 ERROR
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> deleteAllVectorIndexByKbUidAndSyncStatus(FaqRequest request) {
        String kbUid = request.getKbUid();
        if (kbUid == null || kbUid.isBlank()) {
            throw new RuntimeException("kbUid is required");
        }

        List<FaqEntity> faqList = faqRestService.findByKbUidNoCache(kbUid);
        int total = faqList.size();

        // 先尝试批量删除（更快）；失败时再回退到逐条删除以尽可能完成
        List<String> docIdsToDelete = new ArrayList<>();
        for (FaqEntity faq : faqList) {
            List<String> docIdList = faq.getDocIdList();
            if (docIdList == null || docIdList.isEmpty()) {
                docIdsToDelete.add("faq_" + faq.getUid());
            } else {
                docIdsToDelete.addAll(docIdList);
            }
        }

        int successCount = 0;
        int errorCount = 0;

        try {
            if (!docIdsToDelete.isEmpty()) {
                vectorStore.delete(docIdsToDelete);
            }
            for (FaqEntity faq : faqList) {
                faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.NEW.name());
                faqRestService.updateDocIdListOnly(faq.getUid(), new ArrayList<>());
            }
            successCount = total;
        } catch (Exception e) {
            log.warn("批量删除向量索引失败，将回退逐条删除: kbUid={}, error={}", kbUid, e.getMessage());
            for (FaqEntity faq : faqList) {
                try {
                    // 兼容：docIdList 为空时也尝试默认 docId
                    if (faq.getDocIdList() == null || faq.getDocIdList().isEmpty()) {
                        vectorStore.delete(List.of("faq_" + faq.getUid()));
                        faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.NEW.name());
                        faqRestService.updateDocIdListOnly(faq.getUid(), new ArrayList<>());
                        successCount++;
                        continue;
                    }

                    Boolean deleted = deleteFaqVector(faq);
                    if (Boolean.TRUE.equals(deleted)) {
                        faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.NEW.name());
                        faqRestService.updateDocIdListOnly(faq.getUid(), new ArrayList<>());
                        successCount++;
                    } else {
                        faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.ERROR.name());
                        errorCount++;
                    }
                } catch (Exception ex) {
                    faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.ERROR.name());
                    errorCount++;
                }
            }
        }

        faqRestService.evictFaqCacheAllEntries();

        Map<String, Object> result = new HashMap<>();
        result.put("kbUid", kbUid);
        result.put("total", total);
        result.put("success", successCount);
        result.put("error", errorCount);
        return result;
    }

    /**
     * 删除FAQ向量索引，并同步更新数据库状态
     * - 删除成功/或无文档：vectorStatus 置为 NEW，清空 docIdList
     * - 删除失败：vectorStatus 置为 ERROR
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Boolean deleteVectorIndexAndSyncStatus(FaqRequest request) {
        Optional<FaqEntity> faqOpt = faqRestService.findByUidNoCache(request.getUid());
        if (faqOpt.isEmpty()) {
            throw new RuntimeException("FAQ not found with UID: " + request.getUid());
        }

        FaqEntity faq = faqOpt.get();

        // 兼容历史数据：docIdList 为空时，按默认规则尝试删除 faq_<uid>
        if (faq.getDocIdList() == null || faq.getDocIdList().isEmpty()) {
            try {
                String defaultDocId = "faq_" + faq.getUid();
                vectorStore.delete(List.of(defaultDocId));
            } catch (Exception e) {
                log.warn("按默认docId删除向量索引失败（将继续走常规删除逻辑）: uid={}, error={}", faq.getUid(), e.getMessage());
            }
        }

        Boolean deleted = deleteFaqVector(faq);
        if (Boolean.TRUE.equals(deleted)) {
            faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.NEW.name());
            faqRestService.updateDocIdListOnly(faq.getUid(), new ArrayList<>());
        } else {
            faqRestService.updateVectorStatusOnly(faq.getUid(), FaqStatusEnum.ERROR.name());
        }

        faqRestService.evictFaqCacheAllEntries();
        return deleted;
    }

    private boolean existsVectorDocumentByUid(String uid, String queryHint) {
        // 通过 filterExpression 精确过滤 uid，再用任意query触发检索
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
            expressionBuilder.eq("uid", uid),
            expressionBuilder.eq("sourceType", "FAQ")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query((queryHint == null || queryHint.isBlank()) ? "ping" : queryHint)
                .filterExpression(expression)
                .topK(1)
                .build();

        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        return docs != null && !docs.isEmpty();
    }
    
    /**
     * 将FAQ内容添加到向量存储中
     * 
     * @param faq FAQ实体
     */
    /**
     * 将FAQ内容添加到向量存储中
     * 添加版本检查，以更好地处理乐观锁冲突
     * 
     * @param faq FAQ实体
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void indexFaqVector(FaqEntity faq) {
        log.info("开始向量索引FAQ: {}, ID: {}", faq.getQuestion(), faq.getUid());

        // 尝试从入参携带的 kbase 取 kbUid 作为兜底（防止 DB 中关联缺失或保存时出现乐观锁冲突）
        final String kbUidHint = (faq.getKbase() != null) ? faq.getKbase().getUid() : null;

        // 在处理前先获取最新的FAQ实体
        Optional<FaqEntity> currentFaqOpt = faqRestService.findByUidNoCache(faq.getUid());
        if (!currentFaqOpt.isPresent()) {
            log.error("FAQ实体不存在，无法创建向量索引: {}", faq.getUid());
            throw new RuntimeException("FAQ实体不存在: " + faq.getUid());
        }

        FaqEntity currentFaq = currentFaqOpt.get();
        log.info("获取到最新FAQ实体，当前向量状态: {}, ID: {}", currentFaq.getVectorStatus(), currentFaq.getUid());

        // kbUid 必填：避免写入向量存储时 kb_uid 为空
        String kbUid = (currentFaq.getKbase() != null) ? currentFaq.getKbase().getUid() : null;
        if (!StringUtils.hasText(kbUid) && StringUtils.hasText(kbUidHint)) {
            // 优先使用提示 kbUid，尽量补齐数据库关联；补齐失败也不影响本次向量索引。
            try {
                currentFaq = ensureFaqHasKbase(currentFaq, kbUidHint);
            } catch (Exception e) {
                log.warn("补齐FAQ.kbase失败（将继续使用kbUidHint写入向量存储）: uid={}, kbUidHint={}, error={}",
                        currentFaq.getUid(), kbUidHint, e.getMessage());
            }
            kbUid = (currentFaq.getKbase() != null) ? currentFaq.getKbase().getUid() : kbUidHint;
        }

        if (!StringUtils.hasText(kbUid)) {
            try {
                faqRestService.updateVectorStatusOnly(currentFaq.getUid(), FaqStatusEnum.ERROR.name());
                faqRestService.evictFaqCacheAllEntries();
            } catch (Exception saveEx) {
                log.error("设置向量状态为ERROR失败: uid={}, error={}", currentFaq.getUid(), saveEx.getMessage());
            }
            throw new RuntimeException("kbUid is required (faq.kbase is null), uid=" + currentFaq.getUid());
        }

        // 标记为处理中，避免重建期间仍显示为SUCCESS
        try {
            faqRestService.updateVectorStatusOnly(currentFaq.getUid(), FaqStatusEnum.PROCESSING.name());
            faqRestService.evictFaqCacheAllEntries();
        } catch (Exception e) {
            log.warn("设置向量状态为处理中失败（将继续尝试索引）: uid={}, error={}", currentFaq.getUid(), e.getMessage());
        }

        try {
            // 1. 为问题和答案创建文档（带有元数据）
            String id = "faq_" + currentFaq.getUid();
            String content = currentFaq.getQuestion() + "\n" + currentFaq.getAnswer();

            // 处理标签，将其转化为字符串便于索引
            String tags = String.join(",", currentFaq.getTagList());

            // 元数据
            Map<String, Object> metadata = Map.of(
                    "uid", currentFaq.getUid(),
                    "question", currentFaq.getQuestion(),
                    "kbUid", kbUid,
                    "categoryUid", currentFaq.getCategoryUid() != null ? currentFaq.getCategoryUid() : "",
                    "orgUid", currentFaq.getOrgUid(),
                    "enabled", Boolean.toString(currentFaq.getEnabled()),
                    "tags", tags,
                    // 用于向量检索侧按数据源类型过滤（ALL/FAQ/TEXT/CHUNK/WEBPAGE）
                    "sourceType", "FAQ");

            // 创建文档
            Document document = new Document(id, content, metadata);

            // 添加新文档到向量存储
            log.info("向向量存储添加文档: {}", id);
            vectorStore.add(List.of(document));
            log.info("已成功添加文档到向量存储: {}", id);

            // 3. 更新FAQ实体中的文档ID列表
            List<String> docIdList = currentFaq.getDocIdList();
            if (docIdList == null) {
                docIdList = new ArrayList<>();
            } else if (docIdList.contains(id)) {
                // 如果已包含该ID，先移除再添加以确保唯一性
                docIdList.remove(id);
                log.info("从文档ID列表中移除重复ID: {}", id);
            }

            // 添加文档ID并更新状态
            docIdList.add(id);
            currentFaq.setDocIdList(docIdList);
            faqRestService.updateDocIdListOnly(currentFaq.getUid(), docIdList);
            faqRestService.updateVectorStatusOnly(currentFaq.getUid(), FaqStatusEnum.SUCCESS.name());
            faqRestService.evictFaqCacheAllEntries();

        } catch (Exception e) {
            log.error("FAQ向量索引失败: {}, 错误: {}", currentFaq.getQuestion(), e.getMessage(), e);

            // 设置向量索引状态为失败
            try {
                log.info("保存FAQ实体更新，设置向量索引状态为失败: {}", currentFaq.getUid());
                faqRestService.updateVectorStatusOnly(currentFaq.getUid(), FaqStatusEnum.ERROR.name());
                faqRestService.evictFaqCacheAllEntries();
            } catch (Exception saveEx) {
                log.error("更新FAQ向量索引状态失败: {}, 错误: {}", currentFaq.getUid(), saveEx.getMessage());
            }

            throw new RuntimeException("创建向量索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新FAQ的向量索引
     * 
     * @param request FAQ请求对象
     */
    public void updateVectorIndex(FaqRequest request) {
        Optional<FaqEntity> faqOpt = faqRestService.findByUidNoCache(request.getUid());
        if (faqOpt.isPresent()) {
            FaqEntity faq = ensureFaqHasKbase(faqOpt.get(), request.getKbUid());
            if (faq.getKbase() == null || !StringUtils.hasText(faq.getKbase().getUid())) {
                throw new RuntimeException("kbUid is required");
            }
            // 删除旧的向量索引
            deleteFaqVector(faq);
            // 创建新的向量索引
            indexFaqVector(faq);
        } else {
            log.warn("未找到要更新向量索引的FAQ: {}", request.getUid());
        }
    }

    /**
     * 更新所有FAQ的向量索引
     * 
     * @param request FAQ请求对象，包含知识库ID
     */
    public void updateAllVectorIndex(FaqRequest request) {
        if (!StringUtils.hasText(request.getKbUid())) {
            throw new RuntimeException("kbUid is required");
        }

        List<FaqEntity> faqList = faqRestService.findByKbUidNoCache(request.getKbUid());
        faqList.forEach(faq -> {
            try {
                FaqEntity fixed = ensureFaqHasKbase(faq, request.getKbUid());
                if (fixed.getKbase() == null || !StringUtils.hasText(fixed.getKbase().getUid())) {
                    throw new RuntimeException("kbUid is required");
                }
                // 删除旧的向量索引
                deleteFaqVector(fixed);
                // 创建新的向量索引
                indexFaqVector(fixed);
            } catch (Exception e) {
                log.error("更新FAQ向量索引失败: {}, 错误: {}", faq.getQuestion(), e.getMessage());
            }
        });
    }

    /**
     * 从向量存储中删除FAQ向量文档
     * 修改为不更新实体的方式，只进行向量删除操作，避免实体并发修改冲突
     * 
     * @param faq FAQ实体
     * @return 删除成功返回true，否则返回false
     */
    @Transactional(readOnly = true) // 只读事务，因为我们不会修改实体
    public Boolean deleteFaqVector(FaqEntity faq) {
        log.info("从向量索引中删除FAQ: {}, ID: {}", faq.getQuestion(), faq.getUid());
        try {
            // 获取FAQ文档ID列表
            List<String> docIdList = faq.getDocIdList();
            if (docIdList == null || docIdList.isEmpty()) {
                log.info("FAQ没有关联的向量文档，无需删除: {}", faq.getUid());
                return true;
            }

            // 从向量存储中删除所有相关文档
            log.info("删除FAQ向量文档, 数量: {}, IDs: {}", docIdList.size(), docIdList);
            vectorStore.delete(docIdList);

            // 不再在此方法中更新实体状态，避免乐观锁冲突
            // 状态更新将在indexFaqVector方法中完成

            log.info("成功从向量存储中删除FAQ文档: {}", faq.getUid());
            return true;
        } catch (Exception e) {
            log.error("删除FAQ向量索引失败: {}, 错误: {}", faq.getQuestion(), e.getMessage(), e);
            return false;
        }
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
    public List<FaqVectorSearchResult> searchFaqVector(String query, String kbUid, String categoryUid, String orgUid,
            int limit) {
        log.info("向量搜索FAQ: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);

        // 创建过滤表达式构建器
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();

        // 构建查询条件
        FilterExpressionBuilder.Op enabledOp = expressionBuilder.eq("enabled", "true");

        // 强制限定数据源类型，避免跨类型（TEXT/CHUNK/WEBPAGE）文档误召回
        FilterExpressionBuilder.Op sourceTypeOp = expressionBuilder.eq("sourceType", "FAQ");

        // 添加可选的过滤条件
        FilterExpressionBuilder.Op finalOp = expressionBuilder.and(enabledOp, sourceTypeOp);

        if (kbUid != null && !kbUid.isEmpty()) {
            FilterExpressionBuilder.Op kbUidOp = expressionBuilder.eq("kbUid", kbUid);
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
        List<FaqVectorSearchResult> resultList = new ArrayList<>();

        for (Document doc : similarDocuments) {
            // 从文档中提取元数据
            Map<String, Object> metadata = doc.getMetadata();
            String uid = (String) metadata.getOrDefault("uid", "");

            // 1. 通过UID查找对应的FAQ实体，以便获取完整信息
            Optional<FaqEntity> faqEntityOpt = faqRestService.findByUid(uid);
            if (faqEntityOpt.isPresent()) {
                FaqEntity faqEntity = faqEntityOpt.get();

                // 2. 将FaqEntity转换为FaqVector
                FaqVector faqVector;
                try {
                    faqVector = FaqVector.fromFaqEntity(faqEntity);
                } catch (IllegalArgumentException ex) {
                    // 兼容历史脏数据：entity 可能缺 kbUid，但向量文档 metadata 中通常包含 kbUid
                    log.warn("FaqVectorService search fallback to document metadata, uid={}, err={}", uid,
                            ex.getMessage());
                    faqVector = createSimpleFaqVectorFromDocument(doc);
                }

                // 3. 创建搜索结果对象
                FaqVectorSearchResult result = FaqVectorSearchResult.builder()
                        .faqVector(faqVector)
                        .score(doc.getScore().floatValue())
                        .highlightedQuestion(faqVector.getQuestion()) // 如果需要高亮可以在这里处理
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 将相似度转换为距离，距离 = 1 - 相似度
                        .build();

                resultList.add(result);
            } else {
                // 如果找不到对应的FAQ实体，尝试从文档元数据构建一个简化的FaqVector
                FaqVector simpleFaqVector = createSimpleFaqVectorFromDocument(doc);

                FaqVectorSearchResult result = FaqVectorSearchResult.builder()
                        .faqVector(simpleFaqVector)
                        .score(doc.getScore().floatValue())
                        .highlightedQuestion((String) metadata.getOrDefault("question", ""))
                        .distance((float) (1.0 - doc.getScore().doubleValue())) // 同上
                        .build();

                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 从文档创建简化版的FaqVector对象
     * 用于在找不到对应的FAQ实体时提供基础信息
     * 
     * @param doc 文档对象
     * @return 简化的FaqVector对象
     */
    private FaqVector createSimpleFaqVectorFromDocument(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();

        // 提取元数据
        String uid = (String) metadata.getOrDefault("uid", "");
        String question = (String) metadata.getOrDefault("question", "");
        String content = doc.getText();
        String answer = "";

        // 从内容中提取答案（通常问题和答案用换行符分隔）
        if (content != null && content.contains("\n")) {
            String[] parts = content.split("\n", 2);
            answer = parts.length > 1 ? parts[1] : "";
        }

        // 处理标签
        String tagsStr = (String) metadata.getOrDefault("tags", "");
        List<String> tagList = new ArrayList<>();
        if (tagsStr != null && !tagsStr.isEmpty()) {
            tagList = List.of(tagsStr.split(","));
        }

        // 创建FaqVector对象
        return FaqVector.builder()
                .uid(uid)
                .question(question)
                .answer(answer)
                .similarQuestions(new ArrayList<>()) // 空列表，因为文档中可能没有这个信息
                .tagList(tagList)
                .orgUid((String) metadata.getOrDefault("orgUid", ""))
                .kbUid((String) metadata.getOrDefault(
                    KbaseConst.KBASE_KB_UID,
                    metadata.getOrDefault(KbaseConst.KBASE_KB_UID_LEGACY, "")))
                .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
                .enabled(Boolean.parseBoolean((String) metadata.getOrDefault("enabled", "true")))
                .build();
    }


}
