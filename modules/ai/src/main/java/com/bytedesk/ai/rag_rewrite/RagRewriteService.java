/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强记录服务。
 *   构建 {@link RagRewriteEntity} 并通过事件异步持久化。
 *   供 {@link com.bytedesk.ai.springai.adviser.RagQueryRewriteHelper} 在改写/扩展前后调用。
 *
 *   设计要点：
 *   - 非阻塞：仅发布事件，实际 DB 写入由 {@link RagRewriteEventListener} 异步完成
 *   - 容错：记录构建/发布失败不影响 RAG 主流程（catch 后仅 warn）
 *   - 上下文丰富：记录原始 query、改写/扩展结果、KB 检索命中数与摘要、耗时、降级标记
 */
package com.bytedesk.ai.rag_rewrite;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.rag_rewrite.event.RagRewriteCreateEvent;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.service.SearchResultWithSources;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagRewriteService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UidUtils uidUtils;
    private final ObjectMapper objectMapper;

    /**
     * 记录并异步持久化一次 RAG Query 增强执行。
     *
     * @param robot          机器人配置（用于 orgUid / robotUid）
     * @param threadTopic    会话主题（可为空）
     * @param messageUid     触发消息 UID（可为空）
     * @param originalQuery  原始用户 query
     * @param rewriteType    增强类型
     * @param status         执行状态
     * @param rewrittenQuery 改写后的 query（可为空）
     * @param expandedQueries 多查询扩展结果（可为空）
     * @param effectiveQuery 最终用于 KB 检索的 query
     * @param searchResult   KB 检索结果（用于提取命中数与摘要）
     * @param latencyMs      增强执行耗时（毫秒）
     * @param fallbackUsed   是否发生了降级
     * @param errorMessage   错误信息（可为空）
     */
    public void record(RobotProtobuf robot,
                       String threadTopic,
                       String messageUid,
                       String originalQuery,
                       RagRewriteTypeEnum rewriteType,
                       RagRewriteStatusEnum status,
                       String rewrittenQuery,
                       List<String> expandedQueries,
                       String effectiveQuery,
                       SearchResultWithSources searchResult,
                       Long latencyMs,
                       Boolean fallbackUsed,
                       String errorMessage) {
        try {
            RagRewriteEntity record = RagRewriteEntity.builder()
                    .uid(uidUtils.getUid())
                    .robotUid(robot != null ? robot.getUid() : null)
                    .orgUid(robot != null ? robot.getOrgUid() : null)
                    .threadTopic(threadTopic)
                    .messageUid(messageUid)
                    .originalQuery(originalQuery)
                    .rewriteType(rewriteType.name())
                    .status(status.name())
                    .rewrittenQuery(rewrittenQuery)
                    .expandedQueries(toJson(expandedQueries))
                    .effectiveQuery(effectiveQuery)
                    .resultCount(extractResultCount(searchResult))
                    .resultSummary(toResultSummaryJson(searchResult))
                    .latencyMs(latencyMs)
                    .fallbackUsed(fallbackUsed)
                    .errorMessage(errorMessage)
                    .build();

            applicationEventPublisher.publishEvent(new RagRewriteCreateEvent(record));
        } catch (Exception e) {
            // 记录失败不影响 RAG 主流程
            log.warn("Failed to build/publish RAG rewrite record: {}", e.getMessage());
        }
    }

    private String toJson(List<String> expandedQueries) {
        if (expandedQueries == null || expandedQueries.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(expandedQueries);
        } catch (Exception e) {
            log.warn("Failed to serialize expanded queries: {}", e.getMessage());
            return null;
        }
    }

    private Integer extractResultCount(SearchResultWithSources searchResult) {
        if (searchResult == null || searchResult.getSearchResults() == null) {
            return 0;
        }
        return searchResult.getSearchResults().size();
    }

    /**
     * 将 KB 检索结果转为摘要 JSON（sourceUid / sourceName / score），便于分析召回效果。
     * 仅记录摘要，避免存储完整内容占用过多空间。
     */
    private String toResultSummaryJson(SearchResultWithSources searchResult) {
        if (searchResult == null) {
            return null;
        }
        List<FaqProtobuf> faqs = searchResult.getSearchResults();
        List<RobotContent.SourceReference> sources = searchResult.getSourceReferences();
        if ((faqs == null || faqs.isEmpty()) && (sources == null || sources.isEmpty())) {
            return null;
        }
        try {
            ArrayNode summaryBuilder = objectMapper.createArrayNode();
            if (sources != null) {
                for (RobotContent.SourceReference src : sources) {
                    if (src == null) {
                        continue;
                    }
                    ObjectNode node = summaryBuilder.addObject();
                    node.put("sourceUid", src.getSourceUid());
                    node.put("sourceName", src.getSourceName());
                    node.put("sourceType", src.getSourceType() != null ? src.getSourceType().name() : null);
                    node.put("score", src.getScore());
                    node.put("searchChannel", src.getSearchChannel());
                }
            }
            return objectMapper.writeValueAsString(summaryBuilder);
        } catch (Exception e) {
            log.warn("Failed to serialize result summary: {}", e.getMessage());
            return null;
        }
    }
}
