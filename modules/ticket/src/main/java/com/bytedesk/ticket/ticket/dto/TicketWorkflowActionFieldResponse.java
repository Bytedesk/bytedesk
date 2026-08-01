package com.bytedesk.ticket.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketWorkflowActionFieldResponse {

    private String name;

    private String label;

    private String component;

    private Boolean required;

    private String placeholder;
}
