package com.bytedesk.core.workflow.flow.model;

import lombok.Data;

@Data
public class Variable {
    private String id;
    private String name;
    private boolean isSessionVariable;
} 