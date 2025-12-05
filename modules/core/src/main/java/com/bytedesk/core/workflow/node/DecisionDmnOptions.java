package com.bytedesk.core.workflow.node;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DMN 决策节点配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDmnOptions implements Serializable {
    private static final long serialVersionUID = 1L;

    private String decisionKey;     // DMN 决策 key
    private String decisionVersion; // 可选：特定版本
    private String hitPolicy;       // First/Any/Collect 等
    private Map<String, String> inputMapping;  // 变量入参映射
    private Map<String, String> outputMapping; // 变量出参映射
}
