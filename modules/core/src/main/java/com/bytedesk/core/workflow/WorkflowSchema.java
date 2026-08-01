/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-23 22:45:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.workflow.edge.WorkflowEdge;
import com.bytedesk.core.workflow.node.WorkflowBaseNode;
import com.bytedesk.core.workflow.node.WorkflowNodeFactory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 工作流文档类
 * 对应JSON中的FlowDocumentJSON结构
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class WorkflowSchema implements Serializable {
    

    private static final long serialVersionUID = 1L;

    /**
     * 节点列表
     */
    private List<WorkflowBaseNode> nodes;
    
    /**
     * 边列表
     */
    private List<WorkflowEdge> edges;
    
    /**
     * 从JSON字符串创建WorkflowDocument
     */
    public static WorkflowSchema fromJson(String json) {
        JSONObject root = JSON.parseObject(json);
        WorkflowSchema schema = new WorkflowSchema();
        schema.setNodes(parseNodes(root.getJSONArray("nodes")));
        schema.setEdges(parseEdges(root.getJSONArray("edges")));
        return schema;
    }

    private static List<WorkflowBaseNode> parseNodes(JSONArray nodeArray) {
        if (nodeArray == null) {
            return new ArrayList<>();
        }

        List<WorkflowBaseNode> nodes = new ArrayList<>(nodeArray.size());
        for (int i = 0; i < nodeArray.size(); i++) {
            JSONObject nodeObject = nodeArray.getJSONObject(i);
            WorkflowBaseNode node = WorkflowNodeFactory.parseNode(nodeObject);
            if (node != null && nodeObject != null) {
                node.setBlocks(parseNodes(nodeObject.getJSONArray("blocks")));
                node.setEdges(parseEdges(nodeObject.getJSONArray("edges")));
            }
            nodes.add(node);
        }
        return nodes;
    }

    private static List<WorkflowEdge> parseEdges(JSONArray edgeArray) {
        if (edgeArray == null) {
            return new ArrayList<>();
        }

        return edgeArray.toJavaList(WorkflowEdge.class);
    }
    
    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
} 