package com.bytedesk.core.workflow.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;

class WorkflowNodeFactoryTest {

    @Test
    void createNodeShouldSupportTransferAndBotTypes() {
        assertThat(WorkflowNodeFactory.createNode("transfer")).isInstanceOf(WorkflowTransferNode.class);
        assertThat(WorkflowNodeFactory.createNode("bot")).isInstanceOf(WorkflowBotNode.class);
    }

    @Test
    void parseNodeShouldSupportTransferAndBotTypes() {
        JSONObject transferNode = JSONObject.parseObject("""
                {
                  "id": "transfer-1",
                  "type": "transfer",
                  "data": {
                    "content": "正在为您转接人工坐席，请稍候。",
                    "transferDestination": "5003",
                    "transferContext": "default"
                  }
                }
                """);
        JSONObject botNode = JSONObject.parseObject("""
                {
                  "id": "bot-1",
                  "type": "bot",
                  "data": {
                    "content": "正在为您接入语音机器人，请稍候。",
                    "transferDestination": "9201",
                    "transferContext": "default"
                  }
                }
                """);

        assertThat(WorkflowNodeFactory.parseNode(transferNode)).isInstanceOf(WorkflowTransferNode.class);
        assertThat(WorkflowNodeFactory.parseNode(botNode)).isInstanceOf(WorkflowBotNode.class);
    }
}