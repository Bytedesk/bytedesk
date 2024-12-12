package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import java.util.Map;

@Data
public class TestVariant {
    private String id;
    private String name;
    private double percentage;  // 变体的权重百分比 (0-100)
    private String groupId;  // 关联的Group ID
    private Map<String, Object> settings;  // 变体特定设置
} 
