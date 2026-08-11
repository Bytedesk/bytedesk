package com.bytedesk.ai.rag_rewrite;

/**
 * RAG Query 增强执行状态。
 */
public enum RagRewriteStatusEnum {
    /** 执行成功 */
    SUCCESS,
    /** 执行失败（已降级回原 query） */
    FAILED,
    /** 跳过（开关关闭或 ChatModel 不可用） */
    SKIPPED
}
