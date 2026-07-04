package com.bytedesk.ai.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpTicketCreateRequest {

    private String orgUid;

    private String reporterUid;

    private String reporterNickname;

    private String title;

    private String description;

    private String priority;

    private String type;

    private String workgroupUid;

    private String categoryUid;

    private String contactName;

    private String phone;

    private String email;
}