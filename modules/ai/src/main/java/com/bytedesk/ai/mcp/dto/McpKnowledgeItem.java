package com.bytedesk.ai.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpKnowledgeItem {

    private String title;

    private String content;

    private String summary;

    private String sourceType;

    private String sourceUid;

    private String sourceName;

    private String fileName;

    private String fileUrl;

    private String searchChannel;

    private Double score;
}