package com.bytedesk.core.workflow.compiler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 编译结果：包含 BPMN/DMN XML 以及提示信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompileResult {
    private boolean success;
    private String bpmnXml; // 主要流程定义
    private String dmnXml;  // 可选：单表决策或聚合打包
    private String warnings; // 编译警告
    private String errors;   // 编译错误详情
}
