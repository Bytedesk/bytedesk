package com.bytedesk.core.workflow.flow.model.block.model.options;

public enum ChatRole {
    SYSTEM,
    USER,
    ASSISTANT,
    FUNCTION;
    
    public static ChatRole fromString(String role) {
        try {
            return valueOf(role.toUpperCase());
        } catch (Exception e) {
            return USER;
        }
    }
} 
