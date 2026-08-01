package com.bytedesk.ticket.process;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessValidationResponse {

    private Boolean valid;

    private String processUid;

    private String type;

    private List<ProcessValidationIssueResponse> errors;

    private List<ProcessValidationIssueResponse> warnings;
}
