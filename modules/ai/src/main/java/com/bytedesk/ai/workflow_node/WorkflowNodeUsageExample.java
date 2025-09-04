/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-04 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.ai.workflow.node.WorkflowBaseNode;
import com.bytedesk.ai.workflow.node.WorkflowLLMNode;
import com.bytedesk.ai.workflow.node.WorkflowNodeMeta;
import com.bytedesk.ai.workflow.node.WorkflowNodeStatusEnum;
import com.bytedesk.ai.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.ai.workflow.node.WorkflowStartNode;
import com.bytedesk.ai.workflow.node.WorkflowEndNode;

import lombok.extern.slf4j.Slf4j;

/**
 * 工作流节点使用示例
 * 演示如何在WorkflowNodeEntity和node文件夹中的类之间进行格式转换
 */
@Slf4j
@Component
public class WorkflowNodeUsageExample {

    @Autowired
    private WorkflowNodeService workflowNodeService;

    /**
     * 示例1：创建一个简单的工作流节点并保存到数据库
     */
    public void createSimpleWorkflowExample(WorkflowEntity workflow) {
        log.info("=== 创建简单工作流节点示例 ===");

        // 1. 创建开始节点
        WorkflowStartNode startNode = WorkflowStartNode.builder()
                .id("start_001")
                .name("开始节点")
                .description("工作流的开始节点")
                .type(WorkflowNodeTypeEnum.START.getValue())
                .meta(WorkflowNodeMeta.startNode(100.0, 100.0))
                .data(WorkflowBaseNode.NodeData.of("开始"))
                .build();

        // 2. 创建LLM处理节点
        WorkflowLLMNode llmNode = WorkflowLLMNode.builder()
                .id("llm_001")
                .name("AI处理节点")
                .description("使用LLM进行文本处理")
                .type(WorkflowNodeTypeEnum.LLM.getValue())
                .meta(WorkflowNodeMeta.of(300.0, 200.0))
                .data(WorkflowBaseNode.NodeData.of("AI处理"))
                .modelType("gpt-3.5-turbo")
                .temperature(0.7)
                .systemPrompt("你是一个专业的AI助手")
                .prompt("请处理用户输入的文本")
                .build();

        // 3. 创建结束节点
        WorkflowEndNode endNode = WorkflowEndNode.builder()
                .id("end_001")
                .name("结束节点")
                .description("工作流的结束节点")
                .type(WorkflowNodeTypeEnum.END.getValue())
                .meta(WorkflowNodeMeta.of(500.0, 300.0))
                .data(WorkflowBaseNode.NodeData.of("结束"))
                .build();

        // 4. 保存节点到数据库
        List<WorkflowBaseNode> nodes = Arrays.asList(startNode, llmNode, endNode);
        List<WorkflowNodeEntity> savedEntities = workflowNodeService.createNodes(workflow, nodes);

        log.info("成功创建了 {} 个节点", savedEntities.size());
        savedEntities.forEach(entity -> 
            log.info("节点ID: {}, 类型: {}, 名称: {}", 
                entity.getUid(), entity.getType(), entity.getName()));
    }

    /**
     * 示例2：从数据库读取节点并转换为WorkflowBaseNode
     */
    public void loadAndConvertNodesExample(WorkflowEntity workflow) {
        log.info("=== 从数据库加载并转换节点示例 ===");

        // 1. 从数据库加载所有节点
        List<WorkflowNodeEntity> entities = workflowNodeService.findByWorkflow(workflow);
        
        log.info("从数据库加载了 {} 个节点", entities.size());

        // 2. 转换为WorkflowBaseNode对象
        List<WorkflowBaseNode> nodes = workflowNodeService.toWorkflowNodes(entities);

        // 3. 处理不同类型的节点
        for (WorkflowBaseNode node : nodes) {
            log.info("处理节点: {} ({})", node.getName(), node.getType());
            
            switch (WorkflowNodeTypeEnum.fromValue(node.getType())) {
                case START:
                    WorkflowStartNode startNode = (WorkflowStartNode) node;
                    log.info("  开始节点位置: {}", startNode.getMeta().getPosition());
                    break;
                    
                case LLM:
                    WorkflowLLMNode llmNode = (WorkflowLLMNode) node;
                    log.info("  LLM节点模型: {}, 温度: {}", 
                        llmNode.getModelType(), llmNode.getTemperature());
                    break;
                    
                case END:
                    WorkflowEndNode endNode = (WorkflowEndNode) node;
                    log.info("  结束节点位置: {}", endNode.getMeta().getPosition());
                    break;
                    
                default:
                    log.info("  其他类型节点");
                    break;
            }
        }
    }

    /**
     * 示例3：执行工作流节点状态管理
     */
    public void executeWorkflowExample(WorkflowEntity workflow) {
        log.info("=== 工作流执行状态管理示例 ===");

        // 1. 获取开始节点
        var startNodeOpt = workflowNodeService.findStartNode(workflow);
        if (startNodeOpt.isEmpty()) {
            log.warn("未找到开始节点");
            return;
        }

        WorkflowNodeEntity startNode = startNodeOpt.get();
        
        // 2. 开始执行节点
        log.info("开始执行节点: {}", startNode.getName());
        workflowNodeService.updateNodeStatus(
            startNode.getUid(), 
            WorkflowNodeStatusEnum.PROCESSING, 
            null, 
            null
        );

        // 3. 模拟节点执行完成
        try {
            Thread.sleep(1000); // 模拟处理时间
            
            // 执行成功
            workflowNodeService.updateNodeStatus(
                startNode.getUid(), 
                WorkflowNodeStatusEnum.SUCCESS, 
                "{\"output\": \"开始节点执行成功\"}", 
                null
            );
            log.info("节点执行成功");
            
        } catch (InterruptedException e) {
            // 执行失败
            workflowNodeService.updateNodeStatus(
                startNode.getUid(), 
                WorkflowNodeStatusEnum.FAIL, 
                null, 
                "节点执行被中断"
            );
            log.error("节点执行失败", e);
        }
    }

    /**
     * 示例4：导出和导入工作流
     */
    public void exportImportWorkflowExample(WorkflowEntity sourceWorkflow, WorkflowEntity targetWorkflow) {
        log.info("=== 工作流导出导入示例 ===");

        // 1. 导出源工作流的节点数据
        List<WorkflowBaseNode> exportedNodes = workflowNodeService.exportWorkflowNodes(sourceWorkflow);
        log.info("导出了 {} 个节点", exportedNodes.size());

        // 2. 修改节点ID以避免冲突
        exportedNodes.forEach(node -> {
            String newId = "imported_" + node.getId();
            node.setId(newId);
        });

        // 3. 导入到目标工作流
        List<WorkflowNodeEntity> importedEntities = workflowNodeService.importWorkflowNodes(targetWorkflow, exportedNodes);
        log.info("成功导入了 {} 个节点", importedEntities.size());
    }

    /**
     * 示例5：节点类型统计和查询
     */
    public void nodeStatisticsExample(WorkflowEntity workflow) {
        log.info("=== 节点统计和查询示例 ===");

        // 1. 按类型查询节点
        List<WorkflowNodeEntity> llmNodes = workflowNodeService.findByWorkflowAndType(workflow, WorkflowNodeTypeEnum.LLM);
        log.info("LLM节点数量: {}", llmNodes.size());

        // 2. 查询执行中的节点
        List<WorkflowNodeEntity> processingNodes = workflowNodeService.findProcessingNodes(workflow);
        log.info("正在执行的节点数量: {}", processingNodes.size());

        // 3. 查询启用的节点
        List<WorkflowNodeEntity> enabledNodes = workflowNodeService.findEnabledNodesByWorkflow(workflow);
        log.info("启用的节点数量: {}", enabledNodes.size());

        // 4. 检查节点类型
        for (WorkflowNodeEntity entity : enabledNodes) {
            log.info("节点 {} - 是否为控制节点: {}, 是否为处理节点: {}, 是否为容器节点: {}", 
                entity.getName(),
                entity.isControlNode(),
                entity.isProcessingNode(),
                entity.isContainerNode());
        }
    }

    /**
     * 示例6：复杂节点数据处理
     */
    public void complexNodeDataExample() {
        log.info("=== 复杂节点数据处理示例 ===");

        // 1. 创建带有复杂数据的LLM节点
        WorkflowBaseNode.NodeData complexData = WorkflowBaseNode.NodeData.builder()
                .title("复杂AI处理节点")
                .content("处理用户查询并生成回复")
                .build();

        // 设置输入输出配置
        complexData.setInputsValues(java.util.Map.of(
            "userQuery", "${workflow.input.query}",
            "context", "${previous.output.context}"
        ));

        complexData.setOutputs(java.util.Map.of(
            "aiResponse", "string",
            "confidence", "number",
            "metadata", "object"
        ));

        // 2. 创建复杂的节点元数据
        WorkflowNodeMeta complexMeta = WorkflowNodeMeta.builder()
                .position(WorkflowNodeMeta.Position.of(400.0, 300.0))
                .size(WorkflowNodeMeta.Size.of(200.0, 120.0))
                .draggable(true)
                .selectable(true)
                .addable(false)
                .deleteDisable(false)
                .build();

        // 3. 创建节点
        WorkflowLLMNode complexNode = WorkflowLLMNode.builder()
                .id("complex_llm_001")
                .name("复杂AI节点")
                .type(WorkflowNodeTypeEnum.LLM.getValue())
                .data(complexData)
                .meta(complexMeta)
                .modelType("gpt-4")
                .temperature(0.3)
                .systemPrompt("你是一个专业的客服助手，能够理解用户意图并提供准确的回复")
                .prompt("基于用户查询: {{userQuery}} 和上下文: {{context}}，生成专业的回复")
                .build();

        log.info("创建了复杂节点: {}", complexNode.toJson());
    }
}
