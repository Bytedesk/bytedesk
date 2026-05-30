package com.bytedesk.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.workflow.node.WorkflowChoiceNode;
import com.bytedesk.core.workflow.node.WorkflowEndNode;
import com.bytedesk.core.workflow.node.WorkflowFormNode;
import com.bytedesk.core.workflow.node.WorkflowStartNode;
import com.bytedesk.core.workflow.node.WorkflowTextNode;

class WorkflowSchemaTest {

    @Test
    void fromJsonShouldDeserializeDefaultLeadCollectionNodesToConcreteTypes() {
        WorkflowSchema schema = WorkflowSchema.fromJson(WorkflowInitData.buildDefaultLeadCollectionWorkflowSchemaJson());

        assertThat(schema.getNodes()).hasSize(8);
        assertThat(schema.getEdges()).hasSize(10);
        assertThat(schema.getNodes().get(0)).isInstanceOf(WorkflowStartNode.class);
        assertThat(schema.getNodes().get(1)).isInstanceOf(WorkflowTextNode.class);
        assertThat(schema.getNodes().get(2)).isInstanceOf(WorkflowChoiceNode.class);
        assertThat(schema.getNodes().get(3)).isInstanceOf(WorkflowChoiceNode.class);
        assertThat(schema.getNodes().get(4)).isInstanceOf(WorkflowTextNode.class);
        assertThat(schema.getNodes().get(5)).isInstanceOf(WorkflowFormNode.class);
        assertThat(schema.getNodes().get(6)).isInstanceOf(WorkflowTextNode.class);
        assertThat(schema.getNodes().get(7)).isInstanceOf(WorkflowEndNode.class);
    }
}