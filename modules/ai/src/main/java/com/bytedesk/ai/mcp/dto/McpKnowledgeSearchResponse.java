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
public class McpKnowledgeSearchResponse {

    private String query;

    private String orgUid;

    private String kbUid;

    private String searchType;

    private String userLanguage;

    private List<String> preferredLanguages;

    private Integer total;

    private List<McpKnowledgeItem> items;
}