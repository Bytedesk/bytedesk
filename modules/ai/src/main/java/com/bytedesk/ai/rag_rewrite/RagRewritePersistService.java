/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强记录持久化服务（已废弃，异步逻辑迁移到 RagRewriteEventListener）。
 */
package com.bytedesk.ai.rag_rewrite;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RAG Query 增强记录持久化服务。
 *
 * <p><b>已废弃</b>：异步持久化逻辑已迁移到 {@link RagRewriteEventListener}。
 * 保留此类仅为向后兼容（如未来需要提供同步持久化入口）。新代码请通过发布
 * {@link com.bytedesk.ai.rag_rewrite.event.RagRewriteCreateEvent} 触发持久化。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class RagRewritePersistService {

    private final RagRewriteRepository ragRewriteRepository;

    /**
     * 同步持久化（保留为可选入口，正常流程走异步监听器）。
     */
    public RagRewriteEntity save(RagRewriteEntity record) {
        return ragRewriteRepository.save(record);
    }
}
