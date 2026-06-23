package com.bytedesk.ticket.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessValidationIssueResponse {

    private String level;

    private String code;

    private String message;

    private String nodeId;
}
