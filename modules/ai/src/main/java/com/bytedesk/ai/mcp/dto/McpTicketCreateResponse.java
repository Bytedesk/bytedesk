package com.bytedesk.ai.mcp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpTicketCreateResponse {

    private String uid;

    private String ticketNumber;

    private String title;

    private String status;

    private String priority;

    private String type;

    private String orgUid;

    private String createdAt;

    private String reporterUid;

    private String reporterNickname;

    private String workgroupUid;
}