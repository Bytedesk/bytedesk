package com.bytedesk.ai.rag_rewrite;

/**
 * RAG Query 增强类型枚举（对应 RobotLlm.ragRewriteEnabled / ragMultiQueryEnabled 两个开关）。
 */
public enum RagRewriteTypeEnum {
    /** 单 query 改写（RewriteQueryTransformer） */
    REWRITE,
    /** 多查询扩展（MultiQueryExpander） */
    MULTI_QUERY,
    /** 未启用任何 RAG 增强（兜底记录） */
    NONE
}
