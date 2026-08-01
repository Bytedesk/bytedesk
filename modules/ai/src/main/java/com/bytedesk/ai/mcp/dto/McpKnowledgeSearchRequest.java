package com.bytedesk.ai.mcp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpKnowledgeSearchRequest {

    private String query;

    private String orgUid;

    private String kbUid;

    private String robotUid;

    private Integer topK;

    private String searchType;

    private String sourceType;

    private String userLanguage;

    private String sourceLanguage;

    private List<String> preferredLanguages;

    private List<String> fallbackLanguages;

    private Boolean translateQueryEnabled;

    private Double scoreThreshold;

    private Double topP;
}