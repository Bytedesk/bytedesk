/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-25 09:58:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 06:42:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.workflow.node.WorkflowNodeTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRestService workflowRestService;

    // execute workflow
    public WorkflowResponse execute(WorkflowRequest request) {
        log.debug("Executing workflow with UID: {}", request.getUid());
        
        // 1. 获取工作流实体
        Optional<WorkflowEntity> optional = workflowRestService.findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("工作流不存在: " + request.getUid());
        }
        
        WorkflowEntity workflow = optional.get();
        log.debug("Workflow found: {}, type: {}", workflow.getNickname(), workflow.getType());
        
        // 2. 解析工作流内容
        String content = workflow.getSchema();
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("工作流内容为空");
        }
        
        // 3. 确定开始节点
        String currentNodeId = workflow.getCurrentNodeId();
        if (currentNodeId == null || currentNodeId.isEmpty()) {
            // 如果未指定当前节点，则查找开始节点
            currentNodeId = findStartNodeId(content);
            workflow.setCurrentNodeId(currentNodeId);
            workflowRestService.save(workflow);
        }
        
        // 4. 执行工作流逻辑
        try {
            executeWorkflowFromNode(workflow, currentNodeId, request);
            
            // 5. 返回更新后的工作流响应
            return workflowRestService.convertToResponse(workflow);
        } catch (Exception e) {
            log.error("工作流执行失败: {}", e.getMessage(), e);
            throw new RuntimeException("工作流执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查找工作流中的开始节点ID
     * 
     * @param content 工作流JSON内容
     * @return 开始节点的UID
     */
    private String findStartNodeId(String content) {
        try {
            // 解析工作流JSON，查找类型为"start"的节点
            JSONObject jsonObject = JSON.parseObject(content);
            JSONArray nodes = jsonObject.getJSONArray("nodes");
            if (nodes != null) {
                for (int i = 0; i < nodes.size(); i++) {
                    JSONObject node = nodes.getJSONObject(i);
                    if ("start".equals(node.getString("type"))) {
                        return node.getString("id");
                    }
                }
            }
            throw new RuntimeException("工作流中未找到开始节点");
        } catch (Exception e) {
            log.error("查找开始节点失败: {}", e.getMessage(), e);
            throw new RuntimeException("查找开始节点失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从指定节点开始执行工作流
     * 
     * @param workflow 工作流实体
     * @param nodeId 当前节点UID
     * @param request 请求参数
     */
    private WorkflowEntity executeWorkflowFromNode(WorkflowEntity workflow, String nodeId, WorkflowRequest request) {
        try {
            log.debug("Executing from node: {}", nodeId);
            
            // 1. 从工作流内容中解析当前节点
            JSONObject jsonObject = JSON.parseObject(workflow.getSchema());
            JSONArray nodes = jsonObject.getJSONArray("nodes");
            JSONObject currentNode = findNodeById(nodes, nodeId);
            
            if (currentNode == null) {
                throw new RuntimeException("未找到节点: " + nodeId);
            }
            
            // 2. 获取节点类型并执行相应逻辑
            String nodeType = currentNode.getString("type");
            WorkflowNodeTypeEnum type = WorkflowNodeTypeEnum.fromValue(nodeType);
            
            // 3. 根据节点类型执行不同逻辑
            switch (type) {
                case START:
                    log.debug("执行开始节点: {}", nodeId);
                    String nextNodeUid = findNextNode(jsonObject, nodeId);
                    if (nextNodeUid != null) {
                        // 更新当前节点并继续执行
                        workflow.setCurrentNodeId(nextNodeUid);
                        workflow = workflowRestService.save(workflow);
                        workflow = executeWorkflowFromNode(workflow, nextNodeUid, request);
                    }
                    break;
                    
                case END:
                    log.debug("执行结束节点: {}", nodeId);
                    // 工作流执行完毕，无需进一步操作
                    break;
                    
                case CONDITION:
                    log.debug("执行条件节点: {}", nodeId);
                    // 处理条件节点逻辑
                    String conditionResult = evaluateCondition(currentNode, request);
                    workflow.setCurrentNodeId(conditionResult);
                    workflow = workflowRestService.save(workflow);
                    workflow = executeWorkflowFromNode(workflow, conditionResult, request);
                    break;
                    
                case LOOP:
                    log.debug("执行循环节点: {}", nodeId);
                    // 处理循环节点逻辑
                    executeLoopNode(workflow, currentNode, request);
                    break;
                    
                case LLM:
                    log.debug("执行LLM节点: {}", nodeId);
                    // 处理LLM节点逻辑
                    executeLLMNode(workflow, currentNode, request);
                    String llmNextNodeUid = findNextNode(jsonObject, nodeId);
                    if (llmNextNodeUid != null) {
                        workflow.setCurrentNodeId(llmNextNodeUid);
                        workflow = workflowRestService.save(workflow);
                        workflow = executeWorkflowFromNode(workflow, llmNextNodeUid, request);
                    }
                    break;
                    
                case TEXT:
                    log.debug("执行文本节点: {}", nodeId);
                    // 处理文本节点逻辑
                    executeTextNode(workflow, currentNode, request);
                    String textNextNodeUid = findNextNode(jsonObject, nodeId);
                    if (textNextNodeUid != null) {
                        workflow.setCurrentNodeId(textNextNodeUid);
                        workflow = workflowRestService.save(workflow);
                        workflow = executeWorkflowFromNode(workflow, textNextNodeUid, request);
                    }
                    break;
                    
                case COMMENT:
                    log.debug("执行注释节点: {}", nodeId);
                    // 注释节点无需执行特定逻辑，直接进入下一个节点
                    String commentNextNodeUid = findNextNode(jsonObject, nodeId);
                    if (commentNextNodeUid != null) {
                        workflow.setCurrentNodeId(commentNextNodeUid);
                        workflow = workflowRestService.save(workflow);
                        workflow = executeWorkflowFromNode(workflow, commentNextNodeUid, request);
                    }
                    break;
                    
                case GROUP:
                    log.debug("执行分组节点: {}", nodeId);
                    // 处理分组节点逻辑
                    executeGroupNode(workflow, currentNode, request);
                    break;
                    
                default:
                    throw new IllegalArgumentException("不支持的节点类型: " + nodeType);
            }
            
        } catch (Exception e) {
            log.error("执行节点失败: {}", e.getMessage(), e);
            throw new RuntimeException("执行节点失败: " + e.getMessage(), e);
        }
        return workflow;
    }
    
    /**
     * 在节点数组中查找指定ID的节点
     */
    private JSONObject findNodeById(JSONArray nodes, String nodeId) {
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * 查找当前节点的下一个节点ID
     */
    private String findNextNode(JSONObject workflow, String currentNodeId) {
        JSONArray edges = workflow.getJSONArray("edges");
        if (edges != null) {
            for (int i = 0; i < edges.size(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                String sourceNodeId = edge.getString("sourceNodeId");
                if (currentNodeId.equals(sourceNodeId)) {
                    return edge.getString("targetNodeId");
                }
            }
        }
        return null;
    }
    
    /**
     * 评估条件节点并返回下一个节点ID
     */
    private String evaluateCondition(JSONObject conditionNode, WorkflowRequest request) {
        // 这里是条件节点逻辑的简化实现，实际应用中需要根据条件表达式进行评估
        // 可以从conditionNode中获取条件表达式，然后根据request参数进行评估
        log.debug("评估条件: {}", conditionNode.getString("name"));
        
        // 从节点数据中获取条件表达式
        JSONObject data = conditionNode.getJSONObject("data");
        if (data != null && data.containsKey("outputs")) {
            JSONObject outputs = data.getJSONObject("outputs");
            // 这里应该有条件评估逻辑，目前简化为取第一个输出端口对应的边
            // 实际实现中应该根据条件表达式评估结果选择输出端口
            if (outputs != null && !outputs.isEmpty()) {
                String portId = outputs.keySet().iterator().next();
                // 返回与此输出端口相连的边的目标节点ID
                return portId; // 这里简化处理，实际应该返回与此端口相连的边的目标节点
            }
        }
        
        // 默认返回null，表示工作流结束
        return null;
    }
    
    /**
     * 执行循环节点
     */
    private void executeLoopNode(WorkflowEntity workflow, JSONObject loopNode, WorkflowRequest request) {
        // 循环节点的逻辑实现
        log.debug("执行循环节点: {}", loopNode.getString("name"));
        
        // 从节点数据中获取循环条件和循环体
        JSONObject data = loopNode.getJSONObject("data");
        if (data != null) {
            // 这里应该有循环条件的评估逻辑
            // 实际实现中需要执行循环体中的每个节点
            
            // 简化实现: 直接执行一次循环体中的所有节点
            JSONArray blocks = loopNode.getJSONArray("blocks");
            if (blocks != null && !blocks.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    JSONObject blockNode = blocks.getJSONObject(i);
                    String blockNodeId = blockNode.getString("id");
                    // 可以在这里递归执行循环体中的每个节点
                    // 简化处理，不实际执行
                    log.debug("循环体节点: {}", blockNodeId);
                }
            }
        }
    }
    
    /**
     * 执行LLM节点
     */
    private WorkflowEntity executeLLMNode(WorkflowEntity workflow, JSONObject llmNode, WorkflowRequest request) {
        // LLM节点的逻辑实现
        log.debug("执行LLM节点: {}", llmNode.getString("name"));
        
        // 从节点数据中获取LLM参数
        JSONObject data = llmNode.getJSONObject("data");
        if (data != null && data.containsKey("inputs")) {
            JSONObject inputs = data.getJSONObject("inputs");
            if (inputs != null) {
                // 这里应该有LLM调用逻辑，根据输入参数调用LLM服务
                // 实际实现中需要调用相应的API或服务
                
                log.debug("LLM输入参数: {}", inputs.toString());
                // 模拟LLM响应
                String llmResponse = "这是LLM的响应";
                log.debug("LLM响应: {}", llmResponse);
                
                // 可以将LLM响应保存到工作流上下文中
                // 简化处理，不保存实际响应
            }
        }
        return workflow;
    }
    
    /**
     * 执行文本节点
     */
    private WorkflowEntity executeTextNode(WorkflowEntity workflow, JSONObject textNode, WorkflowRequest request) {
        // 文本节点的逻辑实现
        log.debug("执行文本节点: {}", textNode.getString("name"));
        
        // 从节点数据中获取文本内容
        JSONObject data = textNode.getJSONObject("data");
        if (data != null && data.containsKey("inputs")) {
            JSONObject inputs = data.getJSONObject("inputs");
            if (inputs != null && inputs.containsKey("text")) {
                String text = inputs.getString("text");
                log.debug("文本内容: {}", text);
                
                // 可以对文本内容进行处理，如模板替换、变量解析等
                // 简化处理，不做实际处理
            }
        }
        return workflow;
    }
    
    /**
     * 执行分组节点
     */
    private WorkflowEntity executeGroupNode(WorkflowEntity workflow, JSONObject groupNode, WorkflowRequest request) {
        // 分组节点的逻辑实现
        log.debug("执行分组节点: {}", groupNode.getString("name"));
        
        // 获取分组中的所有节点和边
        JSONArray blocks = groupNode.getJSONArray("blocks");
        JSONArray edges = groupNode.getJSONArray("edges");
        
        if (blocks != null && !blocks.isEmpty()) {
            // 找到分组中的入口节点（没有输入边的节点）
            String entryNodeId = findEntryNode(blocks, edges);
            if (entryNodeId != null) {
                // 可以从入口节点开始执行分组内的子流程
                log.debug("分组入口节点: {}", entryNodeId);
                
                // 简化处理，不实际执行分组内的子流程
            }
        }
        return workflow;
    }
    
    /**
     * 查找分组中的入口节点（没有输入边的节点）
     */
    private String findEntryNode(JSONArray blocks, JSONArray edges) {
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        
        // 获取所有有输入边的节点ID
        java.util.Set<String> nodesWithInputs = new java.util.HashSet<>();
        if (edges != null) {
            for (int i = 0; i < edges.size(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                nodesWithInputs.add(edge.getString("targetNodeId"));
            }
        }
        
        // 查找没有输入边的节点
        for (int i = 0; i < blocks.size(); i++) {
            JSONObject node = blocks.getJSONObject(i);
            String nodeId = node.getString("id");
            if (!nodesWithInputs.contains(nodeId)) {
                return nodeId;
            }
        }
        
        // 如果所有节点都有输入边，则返回第一个节点
        return blocks.getJSONObject(0).getString("id");
    }

    
}
