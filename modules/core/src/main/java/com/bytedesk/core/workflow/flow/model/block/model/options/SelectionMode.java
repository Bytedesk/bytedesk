package com.bytedesk.core.workflow.flow.model.block.model.options;

public enum SelectionMode {
    RANDOM,      // 随机选择
    SEQUENTIAL,  // 顺序选择
    WEIGHTED;    // 按权重选择
    
    public static SelectionMode fromString(String mode) {
        try {
            return valueOf(mode.toUpperCase());
        } catch (Exception e) {
            return RANDOM;
        }
    }
} 
