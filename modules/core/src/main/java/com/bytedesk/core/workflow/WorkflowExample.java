/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 14:53:56
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

import com.bytedesk.core.base.BaseNode;
import com.bytedesk.core.base.NodeMeta;
import com.bytedesk.core.workflow.edge.WorkflowEdge;
import com.bytedesk.core.workflow.node.WorkflowConditionNode;
import com.bytedesk.core.workflow.node.WorkflowEndNode;
import com.bytedesk.core.workflow.node.WorkflowLLMNode;
import com.bytedesk.core.workflow.node.WorkflowLoopNode;
import com.bytedesk.core.workflow.node.WorkflowStartNode;

/**
 * 工作流使用示例
 */
public class WorkflowExample {
    
    /**
     * 创建示例工作流文档
     */
    public static WorkflowDocument createExampleWorkflow() {
        List<BaseNode> nodes = new ArrayList<>();
        List<WorkflowEdge> edges = new ArrayList<>();
        
        // 创建开始节点
        WorkflowStartNode startNode = WorkflowStartNode.builder()
                .id("start_0")
                .type("start")
                .name("Start")
                .meta(NodeMeta.builder()
                        .position(NodeMeta.Position.builder()
                                .x(180.0)
                                .y(298.0)
                                .build())
                        .build())
                .data(BaseNode.NodeData.builder()
                        .title("Start")
                        .outputs(createOutputs("query", "string", "Hello Flow."))
                        .build())
                .build();
        nodes.add(startNode);
        
        // 创建条件节点
        WorkflowConditionNode conditionNode = WorkflowConditionNode.builder()
                .id("condition_0")
                .type("condition")
                .name("Condition")
                .meta(NodeMeta.builder()
                        .position(NodeMeta.Position.builder()
                                .x(640.0)
                                .y(279.5)
                                .build())
                        .build())
                .data(BaseNode.NodeData.builder()
                        .title("Condition")
                        .inputsValues(createConditionInputsValues())
                        .inputs(createConditionInputs())
                        .build())
                .build();
        nodes.add(conditionNode);
        
        // 创建LLM节点
        WorkflowLLMNode llmNode = WorkflowLLMNode.builder()
                .id("llm_0")
                .type("llm")
                .name("LLM_0")
                .meta(NodeMeta.builder()
                        .position(NodeMeta.Position.builder()
                                .x(1660.1942854301792)
                                .y(1.8635936030104148)
                                .build())
                        .build())
                .modelType("gpt-3.5-turbo")
                .temperature(0.5)
                .systemPrompt("You are an AI assistant.")
                .prompt("")
                .data(BaseNode.NodeData.builder()
                        .title("LLM_0")
                        .inputsValues(createLLMInputsValues())
                        .inputs(createLLMInputs())
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(llmNode);
        
        // 创建循环节点
        WorkflowLoopNode loopNode = WorkflowLoopNode.builder()
                .id("loop_H8M3U")
                .type("loop")
                .name("Loop_2")
                .meta(NodeMeta.builder()
                        .position(NodeMeta.Position.builder()
                                .x(1020.0)
                                .y(452.0)
                                .build())
                        .build())
                .loopTimes(2)
                .data(BaseNode.NodeData.builder()
                        .title("Loop_2")
                        .inputsValues(createLoopInputsValues())
                        .inputs(createLoopInputs())
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(loopNode);
        
        // 创建结束节点
        WorkflowEndNode endNode = WorkflowEndNode.builder()
                .id("end_0")
                .type("end")
                .name("End")
                .meta(NodeMeta.builder()
                        .position(NodeMeta.Position.builder()
                                .x(2220.0)
                                .y(298.0)
                                .build())
                        .build())
                .data(BaseNode.NodeData.builder()
                        .title("End")
                        .outputs(createOutputs("result", "string", null))
                        .build())
                .build();
        nodes.add(endNode);
        
        // 创建边
        WorkflowEdge edge1 = WorkflowEdge.builder()
                .sourceNodeID("start_0")
                .targetNodeID("condition_0")
                .build();
        edges.add(edge1);
        
        WorkflowEdge edge2 = WorkflowEdge.builder()
                .sourceNodeID("condition_0")
                .targetNodeID("llm_0")
                .sourcePortID("if_0")
                .build();
        edges.add(edge2);
        
        WorkflowEdge edge3 = WorkflowEdge.builder()
                .sourceNodeID("llm_0")
                .targetNodeID("end_0")
                .build();
        edges.add(edge3);
        
        return WorkflowDocument.builder()
                .nodes(nodes)
                .edges(edges)
                .build();
    }
    
    /**
     * 创建输出配置
     */
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
    
    /**
     * 创建条件节点输入值
     */
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
    
    /**
     * 创建条件节点输入配置
     */
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
    
    /**
     * 创建LLM节点输入值
     */
    private static Map<String, Object> createLLMInputsValues() {
        Map<String, Object> inputsValues = new HashMap<>();
        inputsValues.put("modelType", "gpt-3.5-turbo");
        inputsValues.put("temperature", 0.5);
        inputsValues.put("systemPrompt", "You are an AI assistant.");
        inputsValues.put("prompt", "");
        return inputsValues;
    }
    
    /**
     * 创建LLM节点输入配置
     */
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
    
    /**
     * 创建循环节点输入值
     */
    private static Map<String, Object> createLoopInputsValues() {
        Map<String, Object> inputsValues = new HashMap<>();
        inputsValues.put("loopTimes", 2);
        return inputsValues;
    }
    
    /**
     * 创建循环节点输入配置
     */
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
    
    /**
     * 主方法示例
     */
    public static void main(String[] args) {
        // 创建示例工作流
        WorkflowDocument document = createExampleWorkflow();
        
        // 验证工作流
        boolean isValid = WorkflowUtils.validateWorkflowDocument(document);
        System.out.println("Workflow is valid: " + isValid);
        
        // 转换为JSON
        String json = WorkflowUtils.toJson(document);
        System.out.println("Workflow JSON: " + json);
        
        // 从JSON解析
        WorkflowDocument parsedDocument = WorkflowUtils.parseWorkflowDocument(json);
        System.out.println("Parsed document nodes count: " + parsedDocument.getNodes().size());
        System.out.println("Parsed document edges count: " + parsedDocument.getEdges().size());
    }
    
} 