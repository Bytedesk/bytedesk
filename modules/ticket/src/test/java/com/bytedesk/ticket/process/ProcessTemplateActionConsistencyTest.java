package com.bytedesk.ticket.process;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * 模板单一来源回归：后端默认流程模板（initProcess 使用）与 createFromTemplate
 * 加载的是同一份 ticket-process.json，此处校验模板内 processTicket（处理中）节点的
 * 动作配置与默认外部工单流程期望一致——COMPLETE=已解决 / HOLD=挂起 / CLOSE=关单。
 * 防止再次出现「默认流程显示已解决/挂起，TicketBuilder 创建流程显示提交处理结果/暂存」的标签漂移。
 */
class ProcessTemplateActionConsistencyTest {

    @Test
    void processingNodeActionsMatchDefaultProcessExpectation() throws Exception {
        JSONObject flowgram;
        try (InputStream in = getClass().getResourceAsStream("/processes/ticket-process.json")) {
            flowgram = JSONObject.parseObject(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }

        JSONObject processingNode = findNode(flowgram.getJSONArray("nodes"), "processTicket");
        assertThat(processingNode).as("模板缺少 processTicket 节点").isNotNull();
        assertThat(processingNode.getJSONObject("data").getString("ticketStage")).isEqualTo("PROCESSING");

        JSONArray actions = processingNode.getJSONObject("data").getJSONArray("availableActions");
        assertThat(actions).isNotNull().isNotEmpty();

        JSONObject complete = findAction(actions, "COMPLETE");
        JSONObject hold = findAction(actions, "HOLD");
        JSONObject close = findAction(actions, "CLOSE");
        assertThat(complete).as("处理中节点缺少 COMPLETE 动作").isNotNull();
        assertThat(hold).as("处理中节点缺少 HOLD 动作").isNotNull();
        assertThat(close).as("处理中节点缺少 CLOSE 动作").isNotNull();

        // 与桌面端 ChatTicketActions 展示的默认外部工单流程按钮保持一致
        assertThat(complete.getString("label")).isEqualTo("已解决");
        assertThat(hold.getString("label")).isEqualTo("挂起");
        assertThat(close.getString("label")).isEqualTo("关单");
        assertThat(complete.getString("type")).isEqualTo("complete");
        assertThat(hold.getString("type")).isEqualTo("hold");
        assertThat(close.getString("type")).isEqualTo("close");
    }

    @Test
    void waitClaimAndCustomerVerifyNodesKeepExpectedActions() throws Exception {
        JSONObject flowgram;
        try (InputStream in = getClass().getResourceAsStream("/processes/ticket-process.json")) {
            flowgram = JSONObject.parseObject(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
        JSONArray nodes = flowgram.getJSONArray("nodes");

        JSONObject waitClaim = findNode(nodes, "waitClaim");
        assertThat(waitClaim).isNotNull();
        JSONArray claimActions = waitClaim.getJSONObject("data").getJSONArray("availableActions");
        assertThat(findAction(claimActions, "CLAIM")).isNotNull();
        assertThat(findAction(claimActions, "ASSIGN")).isNotNull();

        JSONObject customerVerify = findNode(nodes, "customerVerify");
        assertThat(customerVerify).isNotNull();
        JSONArray verifyActions = customerVerify.getJSONObject("data").getJSONArray("availableActions");
        assertThat(findAction(verifyActions, "COMPLETE_VERIFIED")).isNotNull();
        assertThat(findAction(verifyActions, "COMPLETE_REJECTED")).isNotNull();
    }

    private JSONObject findNode(JSONArray nodes, String nodeId) {
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }

    private JSONObject findAction(JSONArray actions, String key) {
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if (key.equals(action.getString("key"))) {
                return action;
            }
        }
        return null;
    }
}
