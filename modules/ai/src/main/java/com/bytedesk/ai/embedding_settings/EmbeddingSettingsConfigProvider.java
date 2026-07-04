/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-04 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-04 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.ai.embedding_settings;

import java.util.Optional;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.vector.EmbeddingConfigProvider;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 DB EmbeddingSettings 的 VectorStore 提供者。
 * 在 modules/ai 中实现，通过 EmbeddingConfigProvider 接口供 modules/kbase 使用。
 */
@Slf4j
@Component
public class EmbeddingSettingsConfigProvider implements EmbeddingConfigProvider {

    private final ObjectProvider<EmbeddingSettingsKbaseVectorStoreResolver> resolverProvider;

    public EmbeddingSettingsConfigProvider(ObjectProvider<EmbeddingSettingsKbaseVectorStoreResolver> resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @PostConstruct
    void init() {
        log.info("EmbeddingSettingsConfigProvider initialized, resolver available={}", resolverProvider.getIfAvailable() != null);
    }

    @Override
    public Optional<VectorStore> getDefaultVectorStore() {
        EmbeddingSettingsKbaseVectorStoreResolver resolver = resolverProvider.getIfAvailable();
        if (resolver == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(resolver.resolveDefaultWithDbSettings());
        } catch (Exception e) {
            log.warn("Failed to resolve VectorStore from DB settings: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
