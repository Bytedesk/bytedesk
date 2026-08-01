package com.bytedesk.core.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowTemplateOptionResponse {

    private String value;

    private String label;

    private String description;

    private String schema;
}