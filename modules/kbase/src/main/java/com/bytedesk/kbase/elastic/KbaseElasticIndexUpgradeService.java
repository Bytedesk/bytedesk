/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-09 17:12:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-09 17:12:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import co.elastic.clients.transport.rest5_client.low_level.Request;
import co.elastic.clients.transport.rest5_client.low_level.ResponseException;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.article.ArticleRequest;
import com.bytedesk.kbase.article.elastic.ArticleElastic;
import com.bytedesk.kbase.article.elastic.ArticleElasticService;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElastic;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_faq.FaqRequest;
import com.bytedesk.kbase.llm_faq.elastic.FaqElastic;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_text.TextRequest;
import com.bytedesk.kbase.llm_text.elastic.TextElastic;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_webpage.WebpageRequest;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElastic;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;
import com.bytedesk.kbase.quick_reply.elastic.QuickReplyElastic;
import com.bytedesk.kbase.quick_reply.elastic.QuickReplyElasticService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KbaseElasticIndexUpgradeService {

    private static final String IK_INDEX_ANALYZER = "ik_max_word";

    private static final String IK_SEARCH_ANALYZER = "ik_smart";

    private final ElasticsearchOperations elasticsearchOperations;

    private final ObjectProvider<Rest5Client> restClientProvider;

    private final KbaseRestService kbaseRestService;

    private final ArticleElasticService articleElasticService;

    private final FaqElasticService faqElasticService;

    private final TextElasticService textElasticService;

    private final ChunkElasticService chunkElasticService;

    private final WebpageElasticService webpageElasticService;

    private final QuickReplyElasticService quickReplyElasticService;

    private final QuickReplyRestService quickReplyRestService;

    public Map<String, Object> checkAndUpgradeIkIndexes() {
        Map<String, Object> analyzerCheck = checkIkAnalyzerAvailability();
        if (!Boolean.TRUE.equals(analyzerCheck.get("available"))) {
            Map<String, Object> skippedResult = new LinkedHashMap<>();
            skippedResult.put("needsUpgrade", false);
            skippedResult.put("upgradedCount", 0);
            skippedResult.put("kbaseCount", 0);
            skippedResult.put("indexes", List.of());
            skippedResult.put("skipped", true);
            skippedResult.put("skipReason", analyzerCheck.get("reason"));
            skippedResult.put("analyzerAvailable", false);
            log.warn("Skip KBase Elasticsearch IK upgrade: {}", analyzerCheck.get("reason"));
            return skippedResult;
        }

        List<KbaseEntity> kbaseList = kbaseRestService.findAllNotDeleted();
        List<Map<String, Object>> indexResults = new ArrayList<>();

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("article")
                .documentClass(ArticleElastic.class)
                .textFields(List.of("title", "summary", "contentMarkdown", "contentHtml"))
                .reindex(reindexByKbase(kbaseList, kb -> {
                    ArticleRequest request = ArticleRequest.builder().kbUid(kb.getUid()).build();
                    articleElasticService.updateAllIndex(request);
                }))
                .build()));

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("faq")
                .documentClass(FaqElastic.class)
                .textFields(List.of("question", "answer", "similarQuestions"))
                .keywordFields(List.of("language", "sourceUid", "sourceLanguage", "sourceType"))
                .booleanFields(List.of("translated"))
                .reindex(reindexByKbase(kbaseList, kb -> {
                    FaqRequest request = FaqRequest.builder().kbUid(kb.getUid()).build();
                    faqElasticService.updateAllIndex(request);
                }))
                .build()));

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("text")
                .documentClass(TextElastic.class)
                .textFields(List.of("title", "content"))
                .keywordFields(List.of("language", "sourceUid", "sourceLanguage", "sourceType"))
                .booleanFields(List.of("translated"))
                .reindex(reindexByKbase(kbaseList, kb -> {
                    TextRequest request = TextRequest.builder().kbUid(kb.getUid()).build();
                    textElasticService.updateAllIndex(request);
                }))
                .build()));

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("chunk")
                .documentClass(ChunkElastic.class)
                .textFields(List.of("name", "content"))
                .keywordFields(List.of("language", "sourceUid", "sourceLanguage", "sourceType"))
                .booleanFields(List.of("translated"))
                .reindex(reindexByKbase(kbaseList, kb -> {
                    ChunkRequest request = ChunkRequest.builder().kbUid(kb.getUid()).build();
                    chunkElasticService.updateAllIndex(request);
                }))
                .build()));

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("webpage")
                .documentClass(WebpageElastic.class)
                .textFields(List.of("title", "description", "content"))
                .keywordFields(List.of("url", "language", "sourceUid", "sourceLanguage", "sourceType"))
                .booleanFields(List.of("translated"))
                .reindex(reindexByKbase(kbaseList, kb -> {
                    WebpageRequest request = WebpageRequest.builder().kbUid(kb.getUid()).build();
                    webpageElasticService.updateAllIndex(request);
                }))
                .build()));

        indexResults.add(checkUpgradeAndReindex(IndexSpec.builder()
                .name("quickReply")
                .documentClass(QuickReplyElastic.class)
                .textFields(List.of("title", "content"))
                .reindex(() -> quickReplyRestService.findAllNotDeleted().forEach(quickReplyElasticService::updateIndex))
                .build()));

        long upgradedCount = indexResults.stream()
                .filter(result -> Boolean.TRUE.equals(result.get("upgraded")))
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("needsUpgrade", upgradedCount > 0);
        result.put("upgradedCount", upgradedCount);
        result.put("kbaseCount", kbaseList.size());
        result.put("indexes", indexResults);
        result.put("skipped", false);
        result.put("analyzerAvailable", true);
        return result;
    }

    private Map<String, Object> checkIkAnalyzerAvailability() {
        Rest5Client restClient = restClientProvider.getIfAvailable();
        if (restClient == null) {
            return Map.of(
                    "available", false,
                    "reason", "RestClient is not available, cannot verify IK analyzers");
        }

        String[] analyzers = { IK_INDEX_ANALYZER, IK_SEARCH_ANALYZER };
        for (String analyzer : analyzers) {
            String failureReason = verifyAnalyzer(restClient, analyzer);
            if (StringUtils.hasText(failureReason)) {
                return Map.of(
                        "available", false,
                        "reason", failureReason);
            }
        }

        return Map.of("available", true);
    }

    private String verifyAnalyzer(Rest5Client restClient, String analyzer) {
        try {
            Request request = new Request("POST", "/_analyze");
            request.setJsonEntity("{\"analyzer\":\"" + analyzer + "\",\"text\":\"bytedesk ik check\"}");
            restClient.performRequest(request);
            return null;
        } catch (ResponseException ex) {
            int statusCode = ex.getResponse().getStatusCode();
            return "IK analyzer unavailable: " + analyzer + ", status=" + statusCode + ", error=" + ex.getMessage();
        } catch (IOException ex) {
            return "Failed to verify IK analyzer " + analyzer + ": " + ex.getMessage();
        }
    }

    private Runnable reindexByKbase(List<KbaseEntity> kbaseList, Consumer<KbaseEntity> reindexer) {
        return () -> kbaseList.forEach(reindexer);
    }

    private Map<String, Object> checkUpgradeAndReindex(IndexSpec spec) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(spec.documentClass());
        String indexName = spec.documentClass().getAnnotation(org.springframework.data.elasticsearch.annotations.Document.class).indexName();
        boolean existed = indexOps.exists();
        Map<String, Object> mapping = existed ? indexOps.getMapping() : Map.of();
        List<String> reasons = mappingMismatchReasons(mapping, spec);
        boolean needsUpgrade = !existed || !reasons.isEmpty();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", spec.name());
        result.put("indexName", indexName);
        result.put("existed", existed);
        result.put("needsUpgrade", needsUpgrade);
        result.put("reasons", reasons);

        if (!needsUpgrade) {
            result.put("upgraded", false);
            return result;
        }

        log.info("Elasticsearch index needs IK mapping upgrade: index={}, reasons={}", indexName, reasons);

        if (existed) {
            indexOps.delete();
        }

        boolean created = indexOps.create();
        boolean mapped = indexOps.putMapping();
        if (!(created && mapped)) {
            throw new IllegalStateException("Failed to recreate Elasticsearch index: " + indexName);
        }

        spec.reindex().run();
        result.put("upgraded", true);
        result.put("created", created);
        result.put("mapped", mapped);
        return result;
    }

    private List<String> mappingMismatchReasons(Map<String, Object> mapping, IndexSpec spec) {
        List<String> reasons = new ArrayList<>();
        Map<String, Object> properties = getObjectMap(mapping, "properties");
        if (properties.isEmpty()) {
            reasons.add("mapping properties missing");
            return reasons;
        }

        for (String field : spec.textFields()) {
            Map<String, Object> fieldMapping = getObjectMap(properties, field);
            if (!"text".equals(fieldMapping.get("type"))) {
                reasons.add(field + ".type != text");
                continue;
            }
            if (!IK_INDEX_ANALYZER.equals(fieldMapping.get("analyzer"))) {
                reasons.add(field + ".analyzer != " + IK_INDEX_ANALYZER);
            }
            if (!IK_SEARCH_ANALYZER.equals(fieldMapping.get("search_analyzer"))) {
                reasons.add(field + ".search_analyzer != " + IK_SEARCH_ANALYZER);
            }
        }

        for (String field : spec.keywordFields()) {
            Map<String, Object> fieldMapping = getObjectMap(properties, field);
            if (!"keyword".equals(fieldMapping.get("type"))) {
                reasons.add(field + ".type != keyword");
            }
        }

        for (String field : spec.booleanFields()) {
            Map<String, Object> fieldMapping = getObjectMap(properties, field);
            if (!"boolean".equals(fieldMapping.get("type"))) {
                reasons.add(field + ".type != boolean");
            }
        }

        return reasons;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getObjectMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private record IndexSpec(
            String name,
            Class<?> documentClass,
            List<String> textFields,
            List<String> keywordFields,
            List<String> booleanFields,
            Runnable reindex) {

        private static IndexSpecBuilder builder() {
            return new IndexSpecBuilder();
        }
    }

    private static class IndexSpecBuilder {

        private String name;

        private Class<?> documentClass;

        private List<String> textFields = List.of();

        private List<String> keywordFields = List.of();

        private List<String> booleanFields = List.of();

        private Runnable reindex;

        private IndexSpecBuilder name(String name) {
            this.name = name;
            return this;
        }

        private IndexSpecBuilder documentClass(Class<?> documentClass) {
            this.documentClass = documentClass;
            return this;
        }

        private IndexSpecBuilder textFields(List<String> textFields) {
            this.textFields = textFields;
            return this;
        }

        private IndexSpecBuilder keywordFields(List<String> keywordFields) {
            this.keywordFields = keywordFields;
            return this;
        }

        private IndexSpecBuilder booleanFields(List<String> booleanFields) {
            this.booleanFields = booleanFields;
            return this;
        }

        private IndexSpecBuilder reindex(Runnable reindex) {
            this.reindex = reindex;
            return this;
        }

        private IndexSpec build() {
            return new IndexSpec(name, documentClass, textFields, keywordFields, booleanFields, reindex);
        }
    }
}
