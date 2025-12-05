/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-03 10:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-03 10:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bytedesk.core.workflow.edge.WorkflowEdge;
import com.bytedesk.core.workflow.node.WorkflowBaseNode;
import com.bytedesk.core.workflow.node.WorkflowConditionNode;
import com.bytedesk.core.workflow.node.WorkflowEndNode;
import com.bytedesk.core.workflow.node.WorkflowLLMNode;
import com.bytedesk.core.workflow.node.WorkflowLoopNode;
import com.bytedesk.core.workflow.node.WorkflowNodeMeta;
import com.bytedesk.core.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.core.workflow.node.WorkflowStartNode;

/**
 * Workflow initialization data holder.
 * 提供用于后端初始化和示例演示的默认工作流结构。
 */
public final class WorkflowInitData {

    /** 默认流程 UID 后缀（需要与组织 UID 组合保证唯一性） */
    public static final String DEFAULT_WORKFLOW_UID_SUFFIX = "df_workflow_builder";

    /** 默认流程名称 */
    public static final String DEFAULT_WORKFLOW_NAME = "默认智能流程";

    /** 默认流程描述 */
    public static final String DEFAULT_WORKFLOW_DESCRIPTION = "FlowBuilder 示例流程";

    /** 默认开始节点 ID */
    public static final String DEFAULT_START_NODE_ID = "start_0";

    private WorkflowInitData() {
    }

    /**
     * 构建默认示例工作流 Schema。
     */
    public static WorkflowSchema buildDefaultWorkflow() {
        List<WorkflowBaseNode> nodes = new ArrayList<>();
        List<WorkflowEdge> edges = new ArrayList<>();

        WorkflowStartNode startNode = WorkflowStartNode.builder()
                .id(DEFAULT_START_NODE_ID)
                .type(WorkflowNodeTypeEnum.START.getValue())
                .name("Start")
                .meta(WorkflowNodeMeta.builder()
                        .position(WorkflowNodeMeta.Position.builder()
                                .x(100.0)
                                .y(200.0)
                                .build())
                        .build())
                .data(WorkflowBaseNode.NodeData.builder()
                        .title("Start")
                        .outputs(createOutputs("query", "string", "Hello Flow."))
                        .build())
                .build();
        nodes.add(startNode);

        WorkflowConditionNode conditionNode = WorkflowConditionNode.builder()
                .id("condition_0")
                .type(WorkflowNodeTypeEnum.CONDITION.getValue())
                .name("Condition")
                .meta(WorkflowNodeMeta.builder()
                        .position(WorkflowNodeMeta.Position.builder()
                                .x(320.0)
                                .y(200.0)
                                .build())
                        .build())
                .data(WorkflowBaseNode.NodeData.builder()
                        .title("Condition")
                        .inputsValues(createConditionInputsValues())
                        .inputs(createConditionInputs())
                        .build())
                .build();
        nodes.add(conditionNode);

        WorkflowLLMNode llmNode = WorkflowLLMNode.builder()
                .id("llm_0")
                .type(WorkflowNodeTypeEnum.LLM.getValue())
                .name("LLM_0")
                .meta(WorkflowNodeMeta.builder()
                        .position(WorkflowNodeMeta.Position.builder()
                                .x(560.0)
                                .y(100.0)
                                .build())
                        .build())
                .modelType("gpt-3.5-turbo")
                .temperature(0.5)
                .systemPrompt("You are an AI assistant.")
                .prompt("")
                .data(WorkflowBaseNode.NodeData.builder()
                        .title("LLM_0")
                        .inputsValues(createLLMInputsValues())
                        .inputs(createLLMInputs())
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(llmNode);

        WorkflowLoopNode loopNode = WorkflowLoopNode.builder()
                .id("loop_H8M3U")
                .type(WorkflowNodeTypeEnum.LOOP.getValue())
                .name("Loop_2")
                .meta(WorkflowNodeMeta.builder()
                        .position(WorkflowNodeMeta.Position.builder()
                                .x(560.0)
                                .y(300.0)
                                .build())
                        .build())
                .loopTimes(2)
                .data(WorkflowBaseNode.NodeData.builder()
                        .title("Loop_2")
                        .inputsValues(createLoopInputsValues())
                        .inputs(createLoopInputs())
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(loopNode);

        WorkflowEndNode endNode = WorkflowEndNode.builder()
                .id("end_0")
                .type(WorkflowNodeTypeEnum.END.getValue())
                .name("End")
                .meta(WorkflowNodeMeta.builder()
                        .position(WorkflowNodeMeta.Position.builder()
                                .x(800.0)
                                .y(200.0)
                                .build())
                        .build())
                .data(WorkflowBaseNode.NodeData.builder()
                        .title("End")
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(endNode);

        WorkflowEdge edge1 = WorkflowEdge.builder()
                .id("edge_start_condition_0")
                .sourceNodeID(DEFAULT_START_NODE_ID)
                .targetNodeID("condition_0")
                .build();
        edges.add(edge1);

        WorkflowEdge edge2 = WorkflowEdge.builder()
                .id("edge_condition_llm_0")
                .sourceNodeID("condition_0")
                .targetNodeID("llm_0")
                .sourcePortID("if_0")
                .build();
        edges.add(edge2);

        WorkflowEdge edge3 = WorkflowEdge.builder()
                .id("edge_llm_end_0")
                .sourceNodeID("llm_0")
                .targetNodeID("end_0")
                .build();
        edges.add(edge3);

        // condition_0 的 else 分支连接到 loop_H8M3U
        WorkflowEdge edge4 = WorkflowEdge.builder()
                .id("edge_condition_loop")
                .sourceNodeID("condition_0")
                .targetNodeID("loop_H8M3U")
                .sourcePortID("if_f0rOAt")
                .build();
        edges.add(edge4);

        // loop_H8M3U 循环完成后连接到 end_0
        WorkflowEdge edge5 = WorkflowEdge.builder()
                .id("edge_loop_end_0")
                .sourceNodeID("loop_H8M3U")
                .targetNodeID("end_0")
                .build();
        edges.add(edge5);

        return WorkflowSchema.builder()
                .nodes(nodes)
                .edges(edges)
                .build();
    }

    private static Map<String, Object> createOutputs(String propertyName, String type, String defaultValue) {
        Map<String, Object> outputs = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> property = new HashMap<>();

        property.put("type", type);
        if (defaultValue != null) {
            property.put("default", defaultValue);
        }

        properties.put(propertyName, property);
        outputs.put("type", "object");
        outputs.put("properties", properties);

        return outputs;
    }

    private static Map<String, Object> createConditionInputsValues() {
        Map<String, Object> inputsValues = new HashMap<>();
        List<Map<String, Object>> conditions = new ArrayList<>();

        Map<String, Object> condition1 = new HashMap<>();
        condition1.put("key", "if_0");
        Map<String, Object> value1 = new HashMap<>();
        value1.put("type", "expression");
        value1.put("content", "");
        condition1.put("value", value1);
        conditions.add(condition1);

        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("key", "if_f0rOAt");
        Map<String, Object> value2 = new HashMap<>();
        value2.put("type", "expression");
        value2.put("content", "");
        condition2.put("value", value2);
        conditions.add(condition2);

        inputsValues.put("conditions", conditions);
        return inputsValues;
    }

    private static Map<String, Object> createConditionInputs() {
        Map<String, Object> inputs = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> conditionsProperty = new HashMap<>();

        conditionsProperty.put("type", "array");
        Map<String, Object> items = new HashMap<>();
        items.put("type", "object");
        Map<String, Object> itemProperties = new HashMap<>();

        Map<String, Object> keyProperty = new HashMap<>();
        keyProperty.put("type", "string");
        itemProperties.put("key", keyProperty);

        Map<String, Object> valueProperty = new HashMap<>();
        valueProperty.put("type", "string");
        itemProperties.put("value", valueProperty);

        items.put("properties", itemProperties);
        conditionsProperty.put("items", items);

        properties.put("conditions", conditionsProperty);
        inputs.put("type", "object");
        inputs.put("properties", properties);

        return inputs;
    }

    private static Map<String, Object> createLLMInputsValues() {
        Map<String, Object> inputsValues = new HashMap<>();
        inputsValues.put("modelType", "gpt-3.5-turbo");
        inputsValues.put("temperature", 0.5);
        inputsValues.put("systemPrompt", "You are an AI assistant.");
        inputsValues.put("prompt", "");
        return inputsValues;
    }

    private static Map<String, Object> createLLMInputs() {
        Map<String, Object> inputs = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        required.add("modelType");
        required.add("temperature");
        required.add("prompt");

        Map<String, Object> modelTypeProperty = new HashMap<>();
        modelTypeProperty.put("type", "string");
        properties.put("modelType", modelTypeProperty);

        Map<String, Object> temperatureProperty = new HashMap<>();
        temperatureProperty.put("type", "number");
        properties.put("temperature", temperatureProperty);

        Map<String, Object> systemPromptProperty = new HashMap<>();
        systemPromptProperty.put("type", "string");
        properties.put("systemPrompt", systemPromptProperty);

        Map<String, Object> promptProperty = new HashMap<>();
        promptProperty.put("type", "string");
        properties.put("prompt", promptProperty);

        inputs.put("type", "object");
        inputs.put("required", required);
        inputs.put("properties", properties);

        return inputs;
    }

    private static Map<String, Object> createLoopInputsValues() {
        Map<String, Object> inputsValues = new HashMap<>();
        inputsValues.put("loopTimes", 2);
        return inputsValues;
    }

    private static Map<String, Object> createLoopInputs() {
        Map<String, Object> inputs = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        required.add("loopTimes");

        Map<String, Object> loopTimesProperty = new HashMap<>();
        loopTimesProperty.put("type", "number");
        properties.put("loopTimes", loopTimesProperty);

        inputs.put("type", "object");
        inputs.put("required", required);
        inputs.put("properties", properties);

        return inputs;
    }
}
