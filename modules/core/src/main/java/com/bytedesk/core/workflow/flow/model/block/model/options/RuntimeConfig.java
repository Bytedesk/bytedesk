package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;

@Data
public class RuntimeConfig {
    private Integer timeout;  // 超时时间(ms)
    private Integer memory;   // 内存限制(MB)
} 
