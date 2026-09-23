/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu.vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.llm_feishu.FeishuDocEntity;
import com.bytedesk.kbase.llm_feishu.FeishuDocRequest;
import com.bytedesk.kbase.llm_feishu.FeishuDocRestService;
import com.bytedesk.kbase.llm_feishu.FeishuDocStatusEnum;
import com.bytedesk.kbase.vector.KbaseVectorStoreResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 飞书文档向量索引服务：
 * - 长文档按段落分块写入（避免整篇长文作为单个向量）
 * - 复用 KbaseVectorStoreResolver.resolveByKbUid
 * - 检索后按 sourceUid 回查实体，过滤已删除/已禁用文档
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class FeishuDocVectorService {

    private static final String DOC_ID_PREFIX = "feishudoc_";

    /** 单个向量文档最大字符数（对齐现有 chunk 数量级） */
    private static final int MAX_CHUNK_CHARS = 1200;

    private final KbaseVectorStoreResolver vectorStoreResolver;

    private final FeishuDocRestService feishuDocRestService;

    /**
     * 分块写入向量索引并回写 docIdList 与状态
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void indexDocVector(FeishuDocEntity entity) {
        try {
            if (!StringUtils.hasText(entity.getKbUid())) {
                throw new IllegalArgumentException("kbUid is required for vectorizing feishu doc uid=" + entity.getUid());
            }

            // 幂等：先删旧文档（不存在的 id 删除为 no-op）
            deleteDocVector(entity);

            String content = entity.getContent() != null ? entity.getContent() : "";
            List<String> chunks = splitContent(content, MAX_CHUNK_CHARS);
            if (chunks.isEmpty()) {
                log.info("飞书文档正文为空，跳过向量索引: uid={}, title={}", entity.getUid(), entity.getTitle());
                feishuDocRestService.updateVectorStatusOnly(entity.getUid(), FeishuDocStatusEnum.NEW.name());
                return;
            }

            List<Document> documents = new ArrayList<>();
            List<String> docIds = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                String docId = DOC_ID_PREFIX + entity.getUid() + "_" + i;
                docIds.add(docId);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("uid", entity.getUid());
                metadata.put("sourceUid", entity.getUid());
                metadata.put("title", entity.getTitle() != null ? entity.getTitle() : "");
                metadata.put(KbaseConst.KBASE_KB_UID, entity.getKbUid());
                metadata.put("categoryUid", entity.getCategoryUid() != null ? entity.getCategoryUid() : "");
                metadata.put("orgUid", entity.getOrgUid() != null ? entity.getOrgUid() : "");
                metadata.put("enabled", Boolean.toString(Boolean.TRUE.equals(entity.getEnabled())));
                metadata.put("sourceType", "FEISHU");
                metadata.put("sourceUrl", entity.getSourceUrl() != null ? entity.getSourceUrl() : "");
                metadata.put("language", "");
                // 首块拼标题，提升标题命中
                String text = (i == 0 && StringUtils.hasText(entity.getTitle())
                        ? entity.getTitle() + "\n\n" + chunks.get(i)
                        : chunks.get(i));
                documents.add(new Document(docId, text, metadata));
            }

            VectorStore vectorStore = vectorStoreResolver.resolveByKbUid(entity.getKbUid());
            vectorStore.add(documents);

            feishuDocRestService.updateDocIdList(entity.getUid(), docIds);
            feishuDocRestService.updateVectorStatusOnly(entity.getUid(), FeishuDocStatusEnum.SUCCESS.name());
        } catch (Exception e) {
            log.error("飞书文档向量索引创建失败: uid={}, error={}", entity.getUid(), e.getMessage(), e);
            feishuDocRestService.updateVectorStatusOnly(entity.getUid(), FeishuDocStatusEnum.ERROR.name());
            throw e instanceof RuntimeException re ? re : new RuntimeException(e);
        }
    }

    /**
     * 删除向量索引（优先 docIdList，兜底当前分块规则生成的 id）
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Boolean deleteDocVector(FeishuDocEntity entity) {
        try {
            if (!StringUtils.hasText(entity.getKbUid())) {
                return Boolean.FALSE;
            }
            List<String> docIds = new ArrayList<>();
            if (entity.getDocIdList() != null && !entity.getDocIdList().isEmpty()) {
                docIds.addAll(entity.getDocIdList());
            } else {
                String content = entity.getContent() != null ? entity.getContent() : "";
                int chunks = splitContent(content, MAX_CHUNK_CHARS).size();
                for (int i = 0; i < chunks; i++) {
                    docIds.add(DOC_ID_PREFIX + entity.getUid() + "_" + i);
                }
            }
            if (docIds.isEmpty()) {
                return Boolean.TRUE;
            }
            vectorStoreResolver.resolveByKbUid(entity.getKbUid()).delete(docIds);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("飞书文档向量索引删除失败: uid={}, error={}", entity.getUid(), e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * 向量相似检索（对齐 searchTextVector：filter enabled+sourceType+kbUid，结果回查实体过滤）
     */
    public List<FeishuDocVectorSearchResult> searchDocVector(String query, String kbUid, String categoryUid,
            String orgUid, Integer limit, String language) {
        if (!StringUtils.hasText(query) || !StringUtils.hasText(kbUid)) {
            return new ArrayList<>();
        }
        try {
            FilterExpressionBuilder b = new FilterExpressionBuilder();
            FilterExpressionBuilder.Op finalOp = b.and(
                    b.eq("enabled", "true"),
                    b.eq("sourceType", "FEISHU"));

            FilterExpressionBuilder.Op kbUidOp = b.eq(KbaseConst.KBASE_KB_UID, kbUid);
            finalOp = b.and(finalOp, kbUidOp);

            if (StringUtils.hasText(categoryUid)) {
                finalOp = b.and(finalOp, b.eq("categoryUid", categoryUid));
            }
            if (StringUtils.hasText(orgUid)) {
                finalOp = b.and(finalOp, b.eq("orgUid", orgUid));
            }
            if (StringUtils.hasText(language)) {
                // P0 未写入语言标记，指定语言过滤时直接返回空，避免跨语言误召回
                return new ArrayList<>();
            }

            Expression expression = finalOp.build();
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .filterExpression(expression)
                    .topK(limit != null && limit > 0 ? limit : 5)
                    .build();

            List<Document> docs = vectorStoreResolver.resolveByKbUid(kbUid).similaritySearch(searchRequest);

            List<FeishuDocVectorSearchResult> results = new ArrayList<>();
            for (Document doc : docs) {
                Map<String, Object> metadata = doc.getMetadata();
                String docUid = (String) metadata.getOrDefault("uid", "");
                String sourceUid = (String) metadata.getOrDefault("sourceUid", docUid);

                // 回查实体：已删除/已禁用/查不到的一律跳过
                Optional<FeishuDocEntity> entityOpt = StringUtils.hasText(sourceUid)
                        ? feishuDocRestService.findByUid(sourceUid)
                        : Optional.empty();
                if (entityOpt.isEmpty() || entityOpt.get().isDeleted()
                        || !Boolean.TRUE.equals(entityOpt.get().getEnabled())) {
                    log.debug("飞书文档已删除/禁用/实体不存在，跳过向量召回: uid={}, sourceUid={}", docUid, sourceUid);
                    continue;
                }

                FeishuDocVector vector = FeishuDocVector.builder()
                        .uid(docUid)
                        .title((String) metadata.getOrDefault("title", ""))
                        .content(doc.getText())
                        .kbUid((String) metadata.getOrDefault(KbaseConst.KBASE_KB_UID,
                                metadata.getOrDefault(KbaseConst.KBASE_KB_UID_LEGACY, "")))
                        .sourceUid(sourceUid)
                        .categoryUid((String) metadata.getOrDefault("categoryUid", ""))
                        .orgUid((String) metadata.getOrDefault("orgUid", ""))
                        .sourceUrl((String) metadata.getOrDefault("sourceUrl", ""))
                        .objType((String) metadata.getOrDefault("objType", ""))
                        .sourceType("FEISHU")
                        .language((String) metadata.getOrDefault("language", ""))
                        .build();

                results.add(FeishuDocVectorSearchResult.builder()
                        .feishuDocVector(vector)
                        .score(doc.getScore() != null ? doc.getScore().floatValue() : 0f)
                        .distance(doc.getScore() != null ? (float) (1.0 - doc.getScore()) : 1f)
                        .build());
            }
            return results;
        } catch (Exception e) {
            log.error("飞书文档向量检索失败: query={}, error={}", query, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 按段落分块：段落累计不超过 maxChars；超长单段硬切
     */
    static List<String> splitContent(String content, int maxChars) {
        List<String> chunks = new ArrayList<>();
        if (!StringUtils.hasText(content)) {
            return chunks;
        }
        StringBuilder buffer = new StringBuilder();
        for (String paragraph : content.split("\n+")) {
            String para = paragraph.trim();
            if (para.isEmpty()) {
                continue;
            }
            if (para.length() > maxChars) {
                if (buffer.length() > 0) {
                    chunks.add(buffer.toString());
                    buffer.setLength(0);
                }
                for (int start = 0; start < para.length(); start += maxChars) {
                    chunks.add(para.substring(start, Math.min(start + maxChars, para.length())));
                }
                continue;
            }
            if (buffer.length() + para.length() + 1 > maxChars) {
                chunks.add(buffer.toString());
                buffer.setLength(0);
            }
            if (buffer.length() > 0) {
                buffer.append('\n');
            }
            buffer.append(para);
        }
        if (buffer.length() > 0) {
            chunks.add(buffer.toString());
        }
        return chunks;
    }

    // ================ 管理端向量索引维护（对齐 FaqVectorService 的管理接口语义） ================

    /**
     * 管理端：更新单条向量索引（indexDocVector 内部幂等先删后建）
     */
    public void updateVectorIndex(FeishuDocRequest request) {
        String uid = requireUid(request);
        FeishuDocEntity entity = feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        indexDocVector(entity);
    }

    /**
     * 管理端：删除单条向量索引并回写状态
     * - 删除成功/无文档：vectorStatus 置为 NEW，清空 docIdList
     * - 删除失败：vectorStatus 置为 ERROR
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Boolean deleteVectorIndexAndSyncStatus(FeishuDocRequest request) {
        String uid = requireUid(request);
        FeishuDocEntity entity = feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        Boolean deleted = deleteDocVector(entity);
        if (Boolean.TRUE.equals(deleted)) {
            feishuDocRestService.updateVectorStatusOnly(uid, FeishuDocStatusEnum.NEW.name());
            feishuDocRestService.updateDocIdList(uid, new ArrayList<>());
        } else {
            feishuDocRestService.updateVectorStatusOnly(uid, FeishuDocStatusEnum.ERROR.name());
        }
        return deleted;
    }

    /**
     * 管理端：同步单条向量索引状态（检查向量库中是否存在该文档并回写）
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FeishuDocEntity syncVectorStatus(FeishuDocRequest request) {
        String uid = requireUid(request);
        FeishuDocEntity entity = feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        boolean exists;
        try {
            exists = existsVectorDocumentByUid(uid, entity.getTitle());
        } catch (Exception e) {
            log.error("同步飞书文档向量状态失败: uid={}, error={}", uid, e.getMessage(), e);
            feishuDocRestService.updateVectorStatusOnly(uid, FeishuDocStatusEnum.ERROR.name());
            return feishuDocRestService.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
        }
        String nextStatus = exists ? FeishuDocStatusEnum.SUCCESS.name() : FeishuDocStatusEnum.NEW.name();
        feishuDocRestService.updateVectorStatusOnly(uid, nextStatus);
        return feishuDocRestService.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("FeishuDoc not found with UID: " + uid));
    }

    /**
     * 管理端：查询向量索引文档详情（分块文档列表，前端“查看索引”弹窗）
     */
    public Map<String, Object> queryVectorByUid(FeishuDocRequest request) {
        String uid = requireUid(request);
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
                expressionBuilder.eq("uid", uid),
                expressionBuilder.eq("sourceType", "FEISHU")).build();

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
                docMap.put("metadata", new HashMap<>(doc.getMetadata()));
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
     * 检查向量库中是否存在该文档的向量（filterExpression uid + sourceType=FEISHU 精确匹配）
     */
    private boolean existsVectorDocumentByUid(String uid, String queryHint) {
        FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
        Expression expression = expressionBuilder.and(
                expressionBuilder.eq("uid", uid),
                expressionBuilder.eq("sourceType", "FEISHU")).build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query((queryHint == null || queryHint.isBlank()) ? "ping" : queryHint)
                .filterExpression(expression)
                .topK(1)
                .build();

        List<Document> docs = resolveStoreByUid(uid).similaritySearch(searchRequest);
        return docs != null && !docs.isEmpty();
    }

    /**
     * 按文档 uid 解析向量存储：优先实体上的 kbUid，缺失时回退默认 store
     */
    private VectorStore resolveStoreByUid(String uid) {
        return feishuDocRestService.findByUid(uid)
                .map(entity -> entity.getKbUid())
                .filter(StringUtils::hasText)
                .map(vectorStoreResolver::resolveByKbUid)
                .orElseGet(vectorStoreResolver::resolveDefault);
    }

    private String requireUid(FeishuDocRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("uid is required");
        }
        return request.getUid();
    }
}
