package com.bytedesk.ai.rag_rewrite;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.rag_rewrite.event.RagRewriteCreateEvent;
import com.bytedesk.ai.rag_rewrite.event.RagRewriteDeleteEvent;
import com.bytedesk.ai.rag_rewrite.event.RagRewriteUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RAG Query 增强记录事件监听器。
 * 监听 {@link RagRewriteCreateEvent} 等事件，将记录异步持久化到数据库，
 * 不阻塞主请求链路（KB 检索 + LLM 问答）。
 *
 * <p>注意：使用 {@code @Async} 切换到虚拟线程池（{@code AsyncExecutorConfig}）执行，
 * 异常仅记录日志，不影响主流程。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RagRewriteEventListener {

    private final RagRewriteRepository ragRewriteRepository;

    /**
     * 异步持久化 RAG 增强记录（创建事件）。
     * 由 {@link RagRewriteService#record} 发布 {@link RagRewriteCreateEvent} 触发。
     */
    @Async
    @EventListener
    public void onRagRewriteCreateEvent(RagRewriteCreateEvent event) {
        try {
            RagRewriteEntity record = event.getRagRewrite();
            if (record == null) {
                return;
            }
            ragRewriteRepository.save(record);
            log.info("RAG rewrite record persisted: uid={}, robotUid={}, type={}, status={}, resultCount={}, latency={}ms",
                    record.getUid(),
                    record.getRobotUid(),
                    record.getRewriteType(),
                    record.getStatus(),
                    record.getResultCount(),
                    record.getLatencyMs());
        } catch (Exception e) {
            // 持久化失败不影响主流程，仅记录日志
            log.error("Failed to persist RAG rewrite record", e);
        }
    }

    /**
     * 更新事件（当前记录为只读，预留）。
     */
    @EventListener
    public void onRagRewriteUpdateEvent(RagRewriteUpdateEvent event) {
        log.debug("RAG rewrite update event received: uid={}",
                event.getRagRewrite() != null ? event.getRagRewrite().getUid() : "null");
    }

    /**
     * 删除事件。
     */
    @EventListener
    public void onRagRewriteDeleteEvent(RagRewriteDeleteEvent event) {
        log.debug("RAG rewrite delete event received: uid={}",
                event.getRagRewrite() != null ? event.getRagRewrite().getUid() : "null");
    }
}
