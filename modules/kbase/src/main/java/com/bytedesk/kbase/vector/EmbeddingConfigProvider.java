/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-04 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-07-04 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 */
package com.bytedesk.kbase.vector;

import java.util.Optional;

import org.springframework.ai.vectorstore.VectorStore;

/**
 * 由 modules/ai 实现，供 modules/kbase 使用。
 * 返回基于 DB 配置的 VectorStore（使用正确的 API Key）。
 */
public interface EmbeddingConfigProvider {

    Optional<VectorStore> getDefaultVectorStore();
}
