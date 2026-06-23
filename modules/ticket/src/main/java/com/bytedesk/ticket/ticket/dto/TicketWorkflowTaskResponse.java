package com.bytedesk.ticket.ticket.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketWorkflowTaskResponse {

    private String ticketUid;

    private String processEntityUid;

    private String processInstanceId;

    private String taskId;

    private String taskName;

    private String taskDefinitionKey;

    private String assignee;

    private String nodeType;

    private String nodeTitle;

    private Boolean actionable;

    private List<TicketWorkflowActionResponse> actions;
}