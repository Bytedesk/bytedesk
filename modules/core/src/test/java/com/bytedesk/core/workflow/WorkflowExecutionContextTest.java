package com.bytedesk.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

class WorkflowExecutionContextTest {

    @Test
    void findNextNodeIdShouldSupportUppercaseEdgeKeys() {
        JSONArray edges = JSONArray.of(
                JSONObject.parseObject("""
                        {
                          "id": "edge-1",
                          "sourceNodeID": "start-1",
                          "targetNodeID": "bot-1",
                          "sourcePortID": "defaultOutput",
                          "targetPortID": "defaultInput"
                        }
                        """));

        WorkflowExecutionContext context = WorkflowExecutionContext.builder()
                .edges(edges)
                .build();

        assertThat(context.findNextNodeId("start-1")).isEqualTo("bot-1");
        assertThat(context.findNextNodeId("start-1", "defaultOutput")).isEqualTo("bot-1");
        assertThat(context.findTargetNodeIdByEdgeId("edge-1")).isEqualTo("bot-1");
    }

    @Test
    void findNextNodeIdShouldAlsoSupportCamelCaseEdgeKeys() {
        JSONArray edges = JSONArray.of(
                JSONObject.parseObject("""
                        {
                          "id": "edge-2",
                          "sourceNodeId": "keyboard-1",
                          "targetNodeId": "transfer-1",
                          "sourcePortId": "keyboard-option-1",
                          "targetPortId": "defaultInput"
                        }
                        """));

        WorkflowExecutionContext context = WorkflowExecutionContext.builder()
                .edges(edges)
                .build();

        assertThat(context.findNextNodeId("keyboard-1", "keyboard-option-1")).isEqualTo("transfer-1");
        assertThat(context.findTargetNodeIdByEdgeId("edge-2")).isEqualTo("transfer-1");
    }
}