/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强（阶段4）。在 KB 检索【前】改写/扩展用户 query，提升召回率。
 *   关键约束（规划 §0 v3.4）：RAG Advisor 不替代 KB 搜索，仅做 Query 增强。
 *   - ragRewriteEnabled=true  → 用 RewriteQueryTransformer 改写 query，改写后 query 传入 KB 检索
 *   - ragMultiQueryEnabled=true → 用 MultiQueryExpander 扩展为多个 query，分别 KB 检索后合并去重重排
 *   - 两者默认关闭（向后兼容）；任一开关开启时，改写失败安全降级回原 query
 */
package com.bytedesk.ai.springai.adviser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

import com.bytedesk.ai.kbase.KbaseSearchHelper;
import com.bytedesk.ai.rag_rewrite.RagRewriteService;
import com.bytedesk.ai.rag_rewrite.RagRewriteStatusEnum;
import com.bytedesk.ai.rag_rewrite.RagRewriteTypeEnum;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.SearchResultWithSources;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;

import lombok.extern.slf4j.Slf4j;

/**
 * RAG Query 增强助手（阶段4）。
 *
 * <p>在 KB 检索【前】对用户 query 做改写/扩展，提升 {@link KbaseSearchHelper} 的召回率。
 * <b>不替代 KB 搜索</b>（规划 §0 v3.4）：增强后的 query 仍交由 {@code KnowledgeBaseSearchHelper}
 * 执行现有的多数据源聚合 + kbUid 隔离 + 混合检索 + 自定义重排 + 来源引用流程。</p>
 *
 * <p>开关（均默认关闭，向后兼容）：</p>
 * <ul>
 *   <li>{@code robot.llm.ragRewriteEnabled=true} → 用 {@link RewriteQueryTransformer} 改写 query</li>
 *   <li>{@code robot.llm.ragMultiQueryEnabled=true} → 用 {@link MultiQueryExpander} 扩展为多个 query，
 *       分别检索后合并去重再重排</li>
 * </ul>
 *
 * <p>所有路径均安全降级：ChatModel 不可用、LLM 改写异常、或开关关闭时，回退到原 query 走现有 KB 检索。</p>
 *
 * <p>注意：Query 改写会额外发起一次 LLM 调用（延迟 + 成本），故默认关闭，按 robot 单独开启。</p>
 */
@Slf4j
@Component
public class RagQueryRewriteHelper {

    private final ChatModel chatModel;
    private final KbaseSearchHelper knowledgeBaseSearchHelper;
    private final RagRewriteService RagRewriteService;

    public RagQueryRewriteHelper(
            ObjectProvider<ChatModel> chatModelProvider,
            KbaseSearchHelper knowledgeBaseSearchHelper,
            RagRewriteService RagRewriteService) {
        this.chatModel = chatModelProvider.getIfAvailable();
        this.knowledgeBaseSearchHelper = knowledgeBaseSearchHelper;
        this.RagRewriteService = RagRewriteService;
    }

    /**
     * 启动时输出 Bean 装配诊断日志，便于在应用启动后立即确认 RAG 增强是否可用
     * （无需发送测试消息即可判断 ragQueryRewriteHelper 是否注入成功 + chatModel 是否可用）。
     */
    @PostConstruct
    public void init() {
        log.info("RagQueryRewriteHelper initialized: chatModel={} ({}), knowledgeBaseSearchHelper={}",
                chatModel != null ? "available" : "NULL",
                chatModel != null ? chatModel.getClass().getSimpleName() : "bean not injected",
                knowledgeBaseSearchHelper != null ? "available" : "NULL");
        if (chatModel == null) {
            log.warn("RagQueryRewriteHelper: ChatModel bean 未注入，RAG 改写/扩展将不可用（开关开启时也会降级为原 query）。"
                    + "请检查是否存在 @Primary ChatModel bean 或 spring.ai.model.chat 配置。");
        }
    }

    /**
     * 判断是否启用了任意 RAG Query 增强（改写或扩展）。
     */
    public boolean isRagEnabled(RobotProtobuf robot) {
        RobotLlm llm = robot == null ? null : robot.getLlm();
        if (llm == null) {
            return false;
        }
        return Boolean.TRUE.equals(llm.getRagRewriteEnabled())
                || Boolean.TRUE.equals(llm.getRagMultiQueryEnabled());
    }

    /**
     * 改写 query（仅 ragRewriteEnabled）。不改写时原样返回。
     *
     * <p>供调用方在仅需改写后的 query 字符串时使用（例如传入 {@code searchKnowledgeBase(query, robot)}）。</p>
     *
     * @param query 原始用户 query
     * @param robot 机器人配置
     * @return 改写后的 query；开关关闭/ChatModel 不可用/异常时返回原 query
     */
    public String rewriteQuery(String query, RobotProtobuf robot) {
        return rewriteQuery(query, robot, null, null);
    }

    /**
     * 改写 query（带上下文，用于持久化记录）。不改写时原样返回。
     *
     * @param query       原始用户 query
     * @param robot       机器人配置
     * @param threadTopic 会话主题（可为空，用于记录）
     * @param messageUid  触发消息 UID（可为空，用于记录）
     * @return 改写后的 query；开关关闭/ChatModel 不可用/异常时返回原 query
     */
    public String rewriteQuery(String query, RobotProtobuf robot, String threadTopic, String messageUid) {
        if (!shouldRewrite(robot)) {
            return query;
        }
        long start = System.currentTimeMillis();
        try {
            Query transformed = buildRewriteTransformer().transform(new Query(query));
            String rewritten = transformed != null ? transformed.text() : null;
            long latency = System.currentTimeMillis() - start;
            if (StringUtils.hasText(rewritten) && !rewritten.equals(query)) {
                log.info("RAG rewrite query: '{}' -> '{}'", query, rewritten);
                RagRewriteService.record(robot, threadTopic, messageUid,
                        query, RagRewriteTypeEnum.REWRITE, RagRewriteStatusEnum.SUCCESS,
                        rewritten, null, rewritten, null,
                        latency, false, null);
                return rewritten;
            }
            // 改写结果为空或与原 query 相同，记录为 SKIPPED
            RagRewriteService.record(robot, threadTopic, messageUid,
                    query, RagRewriteTypeEnum.REWRITE, RagRewriteStatusEnum.SKIPPED,
                    rewritten, null, query, null,
                    latency, false, "rewrite result empty or unchanged");
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            log.warn("RAG rewrite query failed, fallback to original: {}", e.getMessage());
            RagRewriteService.record(robot, threadTopic, messageUid,
                    query, RagRewriteTypeEnum.REWRITE, RagRewriteStatusEnum.FAILED,
                    null, null, query, null,
                    latency, true, e.getMessage());
        }
        return query;
    }

    /**
     * 执行带 RAG Query 增强的 KB 检索（带来源引用）。
     *
     * <p>分支逻辑：</p>
     * <ol>
     *   <li>{@code ragMultiQueryEnabled=true}：扩展为多个 query，分别检索后按 sourceUid 合并去重，
     *       再交由 {@link KbaseSearchHelper#rerankMergeTopK} 统一重排 TopK</li>
     *   <li>{@code ragRewriteEnabled=true}：改写 query 后单次检索</li>
     *   <li>均关闭：直接走 {@code searchKnowledgeBaseWithSources(query, robot)}（行为零变化）</li>
     * </ol>
     *
     * @param query 原始用户 query
     * @param robot 机器人配置
     * @return KB 检索结果（含来源引用）
     */
    public SearchResultWithSources searchWithRagEnhancement(String query, RobotProtobuf robot) {
        return searchWithRagEnhancement(query, robot, null, null);
    }

    /**
     * 执行带 RAG Query 增强的 KB 检索（带上下文，用于持久化记录）。
     */
    public SearchResultWithSources searchWithRagEnhancement(String query, RobotProtobuf robot,
            String threadTopic, String messageUid) {
        RobotLlm llm = robot == null ? null : robot.getLlm();

        // 诊断日志：记录运行时机器人 RAG 开关状态（便于排查"开关已开但未生效"问题）
        boolean ragRewrite = llm != null && Boolean.TRUE.equals(llm.getRagRewriteEnabled());
        boolean ragMultiQuery = llm != null && Boolean.TRUE.equals(llm.getRagMultiQueryEnabled());
        log.info("RAG check: robotUid={}, query='{}', ragRewriteEnabled={}, ragMultiQueryEnabled={}, chatModel={}",
                robot != null ? robot.getUid() : "null",
                query,
                ragRewrite,
                ragMultiQuery,
                chatModel != null ? chatModel.getClass().getSimpleName() : "null");

        // 快速路径：未启用任何 RAG 增强 → 直接走现有 KB 检索（行为零变化）
        if (llm == null || !isRagEnabled(robot)) {
            log.info("RAG skipped (switches off or llm null), using original query for KB search");
            return knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot);
        }

        // ChatModel 不可用时安全降级（社区版可能无可用 ChatModel）
        if (chatModel == null) {
            log.warn("RAG skipped: ChatModel unavailable (bean not injected), fallback to original query");
            return knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(query, robot);
        }

        long ragStart = System.currentTimeMillis();

        // 分支1：多查询扩展
        if (Boolean.TRUE.equals(llm.getRagMultiQueryEnabled())) {
            try {
                List<String> expandedQueries = expandQueries(query);
                if (expandedQueries.size() > 1) {
                    log.info("RAG multi-query expand: '{}' -> {} queries", query, expandedQueries.size());
                    SearchResultWithSources result = searchAndMerge(expandedQueries, robot);
                    long latency = System.currentTimeMillis() - ragStart;
                    RagRewriteService.record(robot, threadTopic, messageUid,
                            query, RagRewriteTypeEnum.MULTI_QUERY, RagRewriteStatusEnum.SUCCESS,
                            null, expandedQueries, query, result,
                            latency, false, null);
                    return result;
                }
            } catch (Exception e) {
                log.warn("RAG multi-query expand failed, fallback to single rewrite/original: {}", e.getMessage());
                long latency = System.currentTimeMillis() - ragStart;
                // 降级到 rewrite/original 路径，记录 multi-query 失败
                RagRewriteService.record(robot, threadTopic, messageUid,
                        query, RagRewriteTypeEnum.MULTI_QUERY, RagRewriteStatusEnum.FAILED,
                        null, null, query, null,
                        latency, true, e.getMessage());
            }
        }

        // 分支2：单 query 改写（multi-query 降级或仅开启 rewrite 时走到这里）
        String effectiveQuery = Boolean.TRUE.equals(llm.getRagRewriteEnabled())
                ? rewriteQuery(query, robot, threadTopic, messageUid)
                : query;
        SearchResultWithSources result = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(effectiveQuery, robot);
        return result;
    }

    /**
     * 执行带 RAG Query 增强的 KB 检索（仅 Faq 列表，便于兼容返回 {@code List<FaqProtobuf>} 的调用点）。
     *
     * <p>内部委托 {@link #searchWithRagEnhancement(String, RobotProtobuf)} 后做聚合/TopK，
     * 与 {@link KbaseSearchHelper#searchKnowledgeBase(String, RobotProtobuf)} 行为对齐。</p>
     */
    public List<FaqProtobuf> searchKnowledgeBaseWithRag(String query, RobotProtobuf robot) {
        return searchKnowledgeBaseWithRag(query, robot, null, null);
    }

    /**
     * 执行带 RAG Query 增强的 KB 检索（仅 Faq 列表，含上下文用于持久化记录）。
     *
     * <p>内部委托 {@link #searchWithRagEnhancement(String, RobotProtobuf, String, String)} 后做聚合/TopK，
     * 与 {@link KbaseSearchHelper#searchKnowledgeBase(String, RobotProtobuf)} 行为对齐。</p>
     */
    public List<FaqProtobuf> searchKnowledgeBaseWithRag(String query, RobotProtobuf robot,
            String threadTopic, String messageUid) {
        SearchResultWithSources raw = searchWithRagEnhancement(query, robot, threadTopic, messageUid);
        SearchResultWithSources aggregated = knowledgeBaseSearchHelper.rerankMergeTopK(raw, robot);
        return aggregated.getSearchResults();
    }

    // ===== 内部方法 =====

    private boolean shouldRewrite(RobotProtobuf robot) {
        RobotLlm llm = robot == null ? null : robot.getLlm();
        return llm != null && Boolean.TRUE.equals(llm.getRagRewriteEnabled()) && chatModel != null;
    }

    private QueryTransformer buildRewriteTransformer() {
        // 与 RagTestController 一致的构建方式：ChatClient.builder(chatModel).build().mutate()
        return RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(chatModel).build().mutate())
                .build();
    }

    private List<String> expandQueries(String query) {
        // 默认扩展为 3 个变体，并保留原 query（includeOriginal 默认 true）
        MultiQueryExpander expander = MultiQueryExpander.builder()
                .chatClientBuilder(ChatClient.builder(chatModel).build().mutate())
                .numberOfQueries(3)
                .build();
        List<Query> expanded = expander.expand(new Query(query));
        // 去重保序，避免重复检索同一 query
        Map<String, Boolean> dedup = new LinkedHashMap<>();
        if (expanded != null) {
            for (Query q : expanded) {
                if (q != null && StringUtils.hasText(q.text())) {
                    dedup.putIfAbsent(q.text(), Boolean.TRUE);
                }
            }
        }
        // 兜底：扩展结果为空时回退原 query
        if (dedup.isEmpty()) {
            dedup.put(query, Boolean.TRUE);
        }
        return new ArrayList<>(dedup.keySet());
    }

    /**
     * 对多个扩展 query 分别执行 KB 检索，合并结果后按 sourceUid 去重，
     * 再统一走 rerankMergeTopK 重排 TopK（复用现有重排逻辑，保证与单 query 路径一致）。
     */
    private SearchResultWithSources searchAndMerge(List<String> queries, RobotProtobuf robot) {
        List<FaqProtobuf> mergedFaqs = new ArrayList<>();
        List<RobotContent.SourceReference> mergedSources = new ArrayList<>();
        // 按 sourceUid 去重（同一文档被多个 query 命中时只保留一份，分数由后续 rerank 重算）
        Map<String, FaqProtobuf> faqByUid = new LinkedHashMap<>();
        Map<String, RobotContent.SourceReference> sourceByUid = new LinkedHashMap<>();

        for (String q : queries) {
            try {
                SearchResultWithSources result = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(q, robot);
                if (result == null) {
                    continue;
                }
                if (result.getSearchResults() != null) {
                    for (FaqProtobuf faq : result.getSearchResults()) {
                        if (faq == null) {
                            continue;
                        }
                        String uid = StringUtils.hasText(faq.getUid()) ? faq.getUid() : faq.getQuestion();
                        if (StringUtils.hasText(uid)) {
                            faqByUid.putIfAbsent(uid, faq);
                        }
                    }
                }
                if (result.getSourceReferences() != null) {
                    for (RobotContent.SourceReference src : result.getSourceReferences()) {
                        if (src == null || !StringUtils.hasText(src.getSourceUid())) {
                            continue;
                        }
                        sourceByUid.putIfAbsent(src.getSourceUid(), src);
                    }
                }
            } catch (Exception e) {
                log.warn("RAG multi-query sub-search failed for '{}': {}", q, e.getMessage());
            }
        }

        mergedFaqs.addAll(faqByUid.values());
        mergedSources.addAll(sourceByUid.values());

        // 合并后统一重排 TopK（与单 query 路径一致，保证输出质量与数量可控）
        SearchResultWithSources merged = new SearchResultWithSources(mergedFaqs, mergedSources);
        return knowledgeBaseSearchHelper.rerankMergeTopK(merged, robot);
    }
}
