/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-23 22:47:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.ai.workflow.edge.WorkflowEdge;
import com.bytedesk.ai.workflow.node.WorkflowBaseNode;
import com.bytedesk.ai.workflow.node.WorkflowNodeMeta;

/**
 * 工作流工具类
 */
public class WorkflowUtils {
    
    /**
     * 从JSON字符串解析工作流文档
     */
    public static WorkflowSchema parseWorkflowSchema(String json) {
        return JSON.parseObject(json, WorkflowSchema.class);
    }
    
    /**
     * 从JSON字符串解析节点列表
     */
    public static List<WorkflowBaseNode> parseNodes(String json) {
        return JSON.parseObject(json, new TypeReference<List<WorkflowBaseNode>>() {});
    }
    
    /**
     * 从JSON字符串解析边列表
     */
    public static List<WorkflowEdge> parseEdges(String json) {
        return JSON.parseObject(json, new TypeReference<List<WorkflowEdge>>() {});
    }
    
    /**
     * 将工作流文档转换为JSON字符串
     */
    public static String toJson(WorkflowSchema schema) {
        return JSON.toJSONString(schema);
    }
    
    /**
     * 将节点列表转换为JSON字符串
     */
    public static String nodesToJson(List<WorkflowBaseNode> nodes) {
        return JSON.toJSONString(nodes);
    }
    
    /**
     * 将边列表转换为JSON字符串
     */
    public static String edgesToJson(List<WorkflowEdge> edges) {
        return JSON.toJSONString(edges);
    }
    
    /**
     * 验证工作流文档的完整性
     */
    public static boolean validateWorkflowDocument(WorkflowSchema document) {
        if (document == null || document.getNodes() == null || document.getEdges() == null) {
            return false;
        }
        
        // 检查是否有开始节点
        boolean hasStartNode = document.getNodes().stream()
                .anyMatch(node -> "start".equals(node.getType()));
        
        // 检查是否有结束节点
        boolean hasEndNode = document.getNodes().stream()
                .anyMatch(node -> "end".equals(node.getType()));
        
        return hasStartNode && hasEndNode;
    }
    
    /**
     * 获取节点的元数据信息
     */
    public static WorkflowNodeMeta getNodeMeta(WorkflowBaseNode node) {
        return node.getMeta();
    }
    
    /**
     * 设置节点的元数据信息
     */
    public static void setNodeMeta(WorkflowBaseNode node, WorkflowNodeMeta meta) {
        node.setMeta(meta);
    }
    
    /**
     * 从Map创建NodeMeta对象
     */
    public static WorkflowNodeMeta createNodeMetaFromMap(Map<String, Object> metaMap) {
        if (metaMap == null) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> positionMap = (Map<String, Object>) metaMap.get("position");
        if (positionMap == null) {
            return null;
        }
        
        Double x = positionMap.get("x") instanceof Number ? 
            ((Number) positionMap.get("x")).doubleValue() : null;
        Double y = positionMap.get("y") instanceof Number ? 
            ((Number) positionMap.get("y")).doubleValue() : null;
        
        WorkflowNodeMeta.Position position = WorkflowNodeMeta.Position.builder()
                .x(x)
                .y(y)
                .build();
        
        return WorkflowNodeMeta.builder()
                .position(position)
                .build();
    }
    
    /**
     * 将NodeMeta转换为Map
     */
    public static Map<String, Object> nodeMetaToMap(WorkflowNodeMeta meta) {
        if (meta == null || meta.getPosition() == null) {
            return null;
        }
        
        Map<String, Object> metaMap = new java.util.HashMap<>();
        Map<String, Object> positionMap = new java.util.HashMap<>();
        
        positionMap.put("x", meta.getPosition().getX());
        positionMap.put("y", meta.getPosition().getY());
        metaMap.put("position", positionMap);
        
        return metaMap;
    }
    
} 