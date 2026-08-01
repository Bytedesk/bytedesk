/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-12 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-12 14:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.embedding_settings;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRepository;
import com.bytedesk.kbase.article.ArticleRequest;
import com.bytedesk.kbase.article.vector.ArticleVectorService;
import com.bytedesk.kbase.llm_chunk.ChunkRequest;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;
import com.bytedesk.kbase.llm_faq.FaqRequest;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;
import com.bytedesk.kbase.llm_text.TextRequest;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;
import com.bytedesk.kbase.llm_webpage.WebpageRequest;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingSettingsReindexService {

    private final ObjectProvider<RestClient> restClientProvider;

    private final KbaseRepository kbaseRepository;

    private final ObjectProvider<FaqVectorService> faqVectorServiceProvider;

    private final ObjectProvider<TextVectorService> textVectorServiceProvider;

    private final ObjectProvider<ChunkVectorService> chunkVectorServiceProvider;

    private final ObjectProvider<ArticleVectorService> articleVectorServiceProvider;

    private final ObjectProvider<WebpageVectorService> webpageVectorServiceProvider;

    @Async("applicationTaskExecutor")
    public void rebuildForEffectiveSettingsChange(EmbeddingSettingsSnapshot previousSnapshot, EmbeddingSettingsSnapshot currentSnapshot) {
        if (currentSnapshot == null) {
            return;
        }

        boolean providerChanged = !Objects.equals(previousSnapshot == null ? null : previousSnapshot.provider(), currentSnapshot.provider());
        boolean modelChanged = !Objects.equals(previousSnapshot == null ? null : previousSnapshot.model(), currentSnapshot.model());
        boolean dimensionsChanged = !Objects.equals(previousSnapshot == null ? null : previousSnapshot.dimensions(), currentSnapshot.dimensions())
                || !Objects.equals(previousSnapshot == null ? null : previousSnapshot.vectorStoreDimensions(), currentSnapshot.vectorStoreDimensions());
        boolean indexNameChanged = !Objects.equals(previousSnapshot == null ? null : previousSnapshot.vectorStoreIndexName(), currentSnapshot.vectorStoreIndexName());
        boolean vectorStoreTypeChanged = !Objects.equals(previousSnapshot == null ? null : previousSnapshot.vectorStoreType(), currentSnapshot.vectorStoreType());

        if (!providerChanged && !modelChanged && !dimensionsChanged && !indexNameChanged && !vectorStoreTypeChanged) {
            return;
        }

        FaqVectorService faqVectorService = faqVectorServiceProvider.getIfAvailable();
        TextVectorService textVectorService = textVectorServiceProvider.getIfAvailable();
        ChunkVectorService chunkVectorService = chunkVectorServiceProvider.getIfAvailable();
        ArticleVectorService articleVectorService = articleVectorServiceProvider.getIfAvailable();
        WebpageVectorService webpageVectorService = webpageVectorServiceProvider.getIfAvailable();
        if (faqVectorService == null && textVectorService == null && chunkVectorService == null
            && articleVectorService == null && webpageVectorService == null) {
            log.warn("Skip vector rebuild because vector services are not available");
            return;
        }

        log.info("EmbeddingSettings effective config changed, start vector rebuild: providerChanged={}, modelChanged={}, dimensionsChanged={}, indexNameChanged={}, vectorStoreTypeChanged={}",
                providerChanged, modelChanged, dimensionsChanged, indexNameChanged, vectorStoreTypeChanged);

        if (dimensionsChanged || indexNameChanged || vectorStoreTypeChanged) {
            deleteSharedVectorIndex(previousSnapshot, currentSnapshot);
        }

        List<KbaseEntity> kbases = kbaseRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(kbase -> !kbase.isDeleted())
                .filter(kbase -> StringUtils.hasText(kbase.getUid()))
                .toList();

        for (KbaseEntity kbase : kbases) {
            String kbUid = kbase.getUid();
            if (faqVectorService != null) {
                try {
                    faqVectorService.updateAllVectorIndex(FaqRequest.builder().kbUid(kbUid).build());
                } catch (Exception e) {
                    log.error("Failed to rebuild FAQ vectors after EmbeddingSettings change, kbUid={}", kbUid, e);
                }
            }

            if (textVectorService != null) {
                try {
                    textVectorService.updateAllVectorIndex(TextRequest.builder().kbUid(kbUid).build());
                } catch (Exception e) {
                    log.error("Failed to rebuild Text vectors after EmbeddingSettings change, kbUid={}", kbUid, e);
                }
            }

            if (chunkVectorService != null) {
                try {
                    chunkVectorService.updateAllVectorIndex(ChunkRequest.builder().kbUid(kbUid).build());
                } catch (Exception e) {
                    log.error("Failed to rebuild Chunk vectors after EmbeddingSettings change, kbUid={}", kbUid, e);
                }
            }

            if (articleVectorService != null) {
                try {
                    articleVectorService.updateAllVectorIndex(ArticleRequest.builder().kbUid(kbUid).build());
                } catch (Exception e) {
                    log.error("Failed to rebuild Article vectors after EmbeddingSettings change, kbUid={}", kbUid, e);
                }
            }

            if (webpageVectorService != null) {
                try {
                    webpageVectorService.updateAllVectorIndex(WebpageRequest.builder().kbUid(kbUid).build());
                } catch (Exception e) {
                    log.error("Failed to rebuild Webpage vectors after EmbeddingSettings change, kbUid={}", kbUid, e);
                }
            }
        }
    }

    private void deleteSharedVectorIndex(EmbeddingSettingsSnapshot previousSnapshot, EmbeddingSettingsSnapshot currentSnapshot) {
        String currentStoreType = currentSnapshot.vectorStoreType();
        if (StringUtils.hasText(currentStoreType) && !"elasticsearch".equalsIgnoreCase(currentStoreType)) {
            log.warn("Skip deleting vector index because current vector store type is unsupported: {}", currentStoreType);
            return;
        }

        if (previousSnapshot != null && StringUtils.hasText(previousSnapshot.vectorStoreType())
                && !"elasticsearch".equalsIgnoreCase(previousSnapshot.vectorStoreType())) {
            log.warn("Skip deleting previous vector index because previous vector store type is unsupported: {}", previousSnapshot.vectorStoreType());
            return;
        }

        String oldIndexName = previousSnapshot == null ? null : previousSnapshot.vectorStoreIndexName();
        String currentIndexName = currentSnapshot.vectorStoreIndexName();

        if (StringUtils.hasText(oldIndexName)) {
            deleteIndexQuietly(oldIndexName);
        }
        if (StringUtils.hasText(currentIndexName) && !Objects.equals(currentIndexName, oldIndexName)) {
            deleteIndexQuietly(currentIndexName);
        }
    }

    private void deleteIndexQuietly(String indexName) {
        RestClient restClient = restClientProvider.getIfAvailable();
        if (restClient == null) {
            log.warn("Skip deleting vector index because RestClient is not available: {}", indexName);
            return;
        }
        try {
            Request request = new Request("DELETE", "/" + indexName);
            restClient.performRequest(request);
            log.info("Deleted vector index: {}", indexName);
        } catch (ResponseException ex) {
            int statusCode = ex.getResponse().getStatusLine().getStatusCode();
            if (statusCode == 404) {
                log.info("Vector index does not exist, skip delete: {}", indexName);
                return;
            }
            log.error("Failed to delete vector index: {}", indexName, ex);
        } catch (IOException ex) {
            log.error("Failed to delete vector index: {}", indexName, ex);
        }
    }
}