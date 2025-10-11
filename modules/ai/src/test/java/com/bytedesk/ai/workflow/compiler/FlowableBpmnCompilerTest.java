package com.bytedesk.ai.workflow.compiler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.bytedesk.ai.workflow.WorkflowEntity;

public class FlowableBpmnCompilerTest {

    @Test
    void compile_minimal_workflow_should_generate_bpmn() {
        WorkflowEntity wf = WorkflowEntity.builder()
            .uid("wf_1")
            .nickname("Test WF")
            .build();

        WorkflowDefinitionAggregate agg = WorkflowDefinitionAggregate.builder()
            .workflow(wf)
            .nodes(Collections.emptyList())
            .edges(Collections.emptyList())
            .variables(Collections.emptyList())
            .build();

        WorkflowCompiler compiler = new FlowableBpmnCompiler();
        CompileResult result = compiler.compile(agg);

        assertTrue(result.isSuccess());
        assertNotNull(result.getBpmnXml());
        assertTrue(result.getBpmnXml().contains("<process"));
    }
}
