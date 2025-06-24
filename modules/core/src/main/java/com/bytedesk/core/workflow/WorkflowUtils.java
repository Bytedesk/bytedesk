/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:02:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bytedesk.core.base.BaseNode;
import com.bytedesk.core.workflow.edge.WorkflowEdge;

/**
 * 工作流工具类
 */
public class WorkflowUtils {
    
    /**
     * 从JSON字符串解析工作流文档
     */
    public static WorkflowDocument parseWorkflowDocument(String json) {
        return JSON.parseObject(json, WorkflowDocument.class);
    }
    
    /**
     * 从JSON字符串解析节点列表
     */
    public static List<BaseNode> parseNodes(String json) {
        return JSON.parseObject(json, new TypeReference<List<BaseNode>>() {});
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
    public static String toJson(WorkflowDocument document) {
        return JSON.toJSONString(document);
    }
    
    /**
     * 将节点列表转换为JSON字符串
     */
    public static String nodesToJson(List<BaseNode> nodes) {
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
    public static boolean validateWorkflowDocument(WorkflowDocument document) {
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
    public static Map<String, Object> getNodeMeta(BaseNode node) {
        if (node.getMeta() != null) {
            return JSON.parseObject(node.getMeta(), new TypeReference<Map<String, Object>>() {});
        }
        return null;
    }
    
    /**
     * 设置节点的元数据信息
     */
    public static void setNodeMeta(BaseNode node, Map<String, Object> meta) {
        if (meta != null) {
            node.setMeta(JSON.toJSONString(meta));
        }
    }
    
} 