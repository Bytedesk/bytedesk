/*
 * @Author: bytedesk.com
 * @Description: Workflow DSL -> BPMN/DMN 编译器接口
 */
package com.bytedesk.core.workflow.compiler;

/**
 * 约定：
 * - 输入：来自前端 React Flow/自定义 DSL 的聚合模型（WorkflowDefinitionAggregate）
 * - 输出：可部署至 Flowable 的 BPMN/DMN XML（CompileResult）
 * - 不直接依赖 Flowable 运行时类，保证模块解耦
 */
public interface WorkflowCompiler {

    /**
     * 将自定义工作流定义编译为 BPMN/DMN XML
     *
     * 成功标准：
     * - result.success() = true
     * - 至少包含一份 bpmnXml（非空）
     */
    CompileResult compile(WorkflowDefinitionAggregate aggregate);
}
