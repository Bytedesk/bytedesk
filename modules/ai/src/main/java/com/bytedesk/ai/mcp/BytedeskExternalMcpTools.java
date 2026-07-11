package com.bytedesk.ai.mcp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.mcp.dto.McpKnowledgeItem;
import com.bytedesk.ai.mcp.dto.McpKnowledgeSearchRequest;
import com.bytedesk.ai.mcp.dto.McpKnowledgeSearchResponse;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.ai.service.KnowledgeBaseSearchHelper;
import com.bytedesk.ai.service.SearchResultWithSources;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BytedeskExternalMcpTools {

    private static final int DEFAULT_TOP_K = 5;
    private static final int MAX_TOP_K = 10;
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int SUMMARY_LENGTH = 200;

    private final ObjectMapper objectMapper;
    private final KnowledgeBaseSearchHelper knowledgeBaseSearchHelper;
    private final RobotRestService robotRestService;

    @Tool(description = "Search Bytedesk knowledge base with semantic or mixed retrieval. Input is McpKnowledgeSearchRequest json; returns structured answer candidates with sources.")
    public Object bytedeskKnowledgeSearch(@ToolParam(description = "McpKnowledgeSearchRequest json") String requestJson) {
        long startedAt = System.currentTimeMillis();
        McpKnowledgeSearchRequest request = parse(requestJson, McpKnowledgeSearchRequest.class, "knowledge search request");
        McpKnowledgeSearchResponse response = searchKnowledge(request);
        log.info("[MCP-AUDIT] tool={} orgUid={} kbUid={} queryLength={} total={} durationMs={}",
                "bytedeskKnowledgeSearch", request.getOrgUid(), response.getKbUid(), request.getQuery().length(),
                response.getTotal(), System.currentTimeMillis() - startedAt);
        return response;
    }

        public McpKnowledgeSearchResponse searchKnowledge(McpKnowledgeSearchRequest request) {
        validateKnowledgeRequest(request);

        RobotProtobuf robot = resolveRobot(request);
        List<String> preferredLanguages = buildPreferredLanguages(request);
        SearchResultWithSources results = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(
            request.getQuery(),
            robot,
            request.getSourceType(),
            preferredLanguages);

        List<McpKnowledgeItem> items = mapKnowledgeItems(results);
        return McpKnowledgeSearchResponse.builder()
            .query(request.getQuery())
            .orgUid(request.getOrgUid())
            .kbUid(robot.getKbUid())
            .searchType(robot.getLlm() != null ? robot.getLlm().getSearchType() : null)
            .userLanguage(request.getUserLanguage())
            .preferredLanguages(preferredLanguages)
            .total(items.size())
            .items(items)
            .build();
        }

    private <T> T parse(String requestJson, Class<T> type, String label) {
        try {
            return objectMapper.readValue(requestJson, type);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid " + label + " json: " + exception.getMessage(), exception);
        }
    }

    private void validateKnowledgeRequest(McpKnowledgeSearchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("knowledge search request is required");
        }
        if (!StringUtils.hasText(request.getQuery())) {
            throw new IllegalArgumentException("query is required");
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }
        if (!StringUtils.hasText(request.getKbUid()) && !StringUtils.hasText(request.getRobotUid())) {
            throw new IllegalArgumentException("kbUid or robotUid is required");
        }
    }

    private RobotProtobuf resolveRobot(McpKnowledgeSearchRequest request) {
        RobotProtobuf robot = null;
        if (StringUtils.hasText(request.getRobotUid())) {
            Optional<RobotEntity> robotOptional = robotRestService.findByUid(request.getRobotUid());
            if (robotOptional.isPresent()) {
                robot = RobotProtobuf.fromEntity(robotOptional.get());
            }
        }
        if (robot == null && StringUtils.hasText(request.getKbUid())) {
            robot = RobotProtobuf.builder()
                    .uid("mcp_kb_search")
                    .orgUid(request.getOrgUid())
                    .kbEnabled(true)
                    .kbSourceEnabled(true)
                    .kbUid(request.getKbUid())
                    .llm(RobotLlm.builder()
                            .scoreThreshold(null)
                            .topP(null)
                            .build())
                    .build();
        }
        if (robot == null) {
            throw new IllegalArgumentException("robotUid or kbUid is required");
        }
        robot.setKbEnabled(true);
        robot.setOrgUid(request.getOrgUid());
        if (StringUtils.hasText(request.getKbUid())) {
            robot.setKbUid(request.getKbUid());
        }
        if (robot.getLlm() == null) {
            robot.setLlm(RobotLlm.builder().build());
        }
        RobotLlm llm = robot.getLlm();
        if (StringUtils.hasText(request.getSearchType())) {
            try {
                llm.setSearchType(RobotSearchTypeEnum.valueOf(request.getSearchType().trim().toUpperCase()).name());
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("invalid searchType: " + request.getSearchType(), exception);
            }
        }
        int topK = request.getTopK() == null ? DEFAULT_TOP_K : Math.min(Math.max(request.getTopK(), 1), MAX_TOP_K);
        llm.setTopK(topK);
        if (request.getScoreThreshold() != null) {
            llm.setScoreThreshold(request.getScoreThreshold());
        }
        if (request.getTopP() != null) {
            llm.setTopP(request.getTopP());
        }
        if (!StringUtils.hasText(llm.getSearchType())) {
            llm.setSearchType(RobotSearchTypeEnum.MIXED.name());
        }
        robot.setLlm(llm);
        return robot;
    }

    private List<McpKnowledgeItem> mapKnowledgeItems(SearchResultWithSources results) {
        List<McpKnowledgeItem> items = new ArrayList<>();
        List<FaqProtobuf> faqs = results.getSearchResults();
        List<RobotContent.SourceReference> sources = results.getSourceReferences();
        for (int index = 0; index < faqs.size(); index++) {
            FaqProtobuf faq = faqs.get(index);
            RobotContent.SourceReference source = index < sources.size() ? sources.get(index) : null;
            String content = truncate(faq.getAnswer(), MAX_CONTENT_LENGTH);
            items.add(McpKnowledgeItem.builder()
                    .title(faq.getQuestion())
                    .content(content)
                    .summary(truncate(content, SUMMARY_LENGTH))
                    .sourceType(source != null && source.getSourceType() != null ? source.getSourceType().name() : faq.getType())
                    .sourceUid(source != null ? source.getSourceUid() : (StringUtils.hasText(faq.getSourceUid()) ? faq.getSourceUid() : faq.getUid()))
                    .sourceName(source != null ? source.getSourceName() : faq.getQuestion())
                    .fileName(source != null ? source.getFileName() : null)
                    .fileUrl(source != null ? source.getFileUrl() : null)
                    .searchChannel(source != null ? source.getSearchChannel() : null)
                    .language(faq.getLanguage())
                    .sourceLanguage(faq.getSourceLanguage())
                    .translated(faq.getTranslated())
                    .score(source != null ? source.getScore() : null)
                    .build());
        }
        return items;
    }

    private List<String> buildPreferredLanguages(McpKnowledgeSearchRequest request) {
        Set<String> languages = new LinkedHashSet<>();
        addLanguage(languages, request.getUserLanguage());
        if (request.getPreferredLanguages() != null) {
            request.getPreferredLanguages().forEach(language -> addLanguage(languages, language));
        }
        addLanguage(languages, request.getSourceLanguage());
        if (request.getFallbackLanguages() != null) {
            request.getFallbackLanguages().forEach(language -> addLanguage(languages, language));
        }
        return new ArrayList<>(languages);
    }

    private void addLanguage(Set<String> languages, String language) {
        if (StringUtils.hasText(language)) {
            languages.add(language.trim().toUpperCase());
        }
    }

    private String truncate(String text, int maxLength) {
        if (!StringUtils.hasText(text) || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }
}