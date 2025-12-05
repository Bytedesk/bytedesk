package com.bytedesk.core.workflow.compiler;

import java.util.List;

import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.workflow_edge.WorkflowEdgeEntity;
import com.bytedesk.core.workflow_node.WorkflowNodeEntity;
import com.bytedesk.core.workflow_variable.WorkflowVariableEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 编译输入聚合：单个工作流的完整定义快照
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDefinitionAggregate {
    private WorkflowEntity workflow;
    private List<WorkflowNodeEntity> nodes;
    private List<WorkflowEdgeEntity> edges;
    private List<WorkflowVariableEntity> variables;
}
