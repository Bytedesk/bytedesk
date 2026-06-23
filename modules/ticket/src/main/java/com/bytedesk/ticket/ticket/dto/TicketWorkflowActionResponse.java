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
public class TicketWorkflowActionResponse {

    private String key;

    private String label;

    private String type;

    private String taskId;

    private String taskDefinitionKey;

    private String targetActivityId;

    private Boolean danger;

    private List<TicketWorkflowActionFieldResponse> fields;
}