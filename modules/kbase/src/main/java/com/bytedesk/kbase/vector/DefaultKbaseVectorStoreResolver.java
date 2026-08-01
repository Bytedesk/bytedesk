/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-06-12 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-06-12 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.vector;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.bytedesk.kbase.kbase.KbaseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultKbaseVectorStoreResolver implements KbaseVectorStoreResolver {

    private final ObjectProvider<ElasticsearchVectorStore> vectorStoreProvider;

    private final ObjectProvider<EmbeddingConfigProvider> embeddingConfigProvider;

    @Override
    public VectorStore resolveByKbase(KbaseEntity kbase) {
        return resolveEffectiveStore();
    }

    @Override
    public VectorStore resolveByKbUid(String kbUid) {
        return resolveEffectiveStore();
    }

    @Override
    public VectorStore resolveDefault() {
        return resolveEffectiveStore();
    }

    /**
     * 优先使用 DB EmbeddingSettings 配置的 VectorStore（通过 EmbeddingConfigProvider），
     * fallback 到 Spring 托管的 elasticsearchVectorStore。
     */
    private VectorStore resolveEffectiveStore() {
        VectorStore dbStore = embeddingConfigProvider
                .stream()
                .findFirst()
                .flatMap(EmbeddingConfigProvider::getDefaultVectorStore)
                .orElse(null);
        if (dbStore != null) {
            log.info("DefaultKbaseVectorStoreResolver: using DB-configured VectorStore");
            return dbStore;
        }
        log.info("DefaultKbaseVectorStoreResolver: falling back to Spring-managed VectorStore (no DB config available)");
        return resolveRequiredStore();
    }

    private VectorStore resolveRequiredStore() {
        ElasticsearchVectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            throw new IllegalStateException("ElasticsearchVectorStore is not available");
        }
        return vectorStore;
    }
}