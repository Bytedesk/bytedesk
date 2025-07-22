/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-22 15:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 15:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.workflow.edge.WorkflowEdge;
import com.bytedesk.core.workflow.node.WorkflowBaseNode;
import com.bytedesk.core.workflow.node.WorkflowNodeMeta;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseNode 测试类
 */
public class BaseNodeTest {

    @Test
    public void testBaseNodeCreation() {
        // 创建节点元数据
        WorkflowNodeMeta meta = WorkflowNodeMeta.startNode(100.0, 200.0);
        
        // 创建节点数据
        WorkflowBaseNode.NodeData data = WorkflowBaseNode.NodeData.of("测试节点");
        
        // 创建基础节点（这里使用匿名内部类，实际使用中应该创建具体的实现类）
        WorkflowBaseNode node = new WorkflowBaseNode() {
            // 匿名实现
        };
        node.setId("node-1");
        node.setName("测试节点");
        node.setType("action");
        node.setMeta(meta);
        node.setData(data);
        
        // 验证节点属性
        assertEquals("node-1", node.getId());
        assertEquals("测试节点", node.getName());
        assertEquals("action", node.getType());
        assertNotNull(node.getMeta());
        assertNotNull(node.getData());
        
        // 验证JSON序列化
        String json = node.toJson();
        assertNotNull(json);
        assertTrue(json.contains("node-1"));
        assertTrue(json.contains("测试节点"));
    }
    
    @Test
    public void testNodeDataCreation() {
        // 测试基础节点数据
        WorkflowBaseNode.NodeData data = WorkflowBaseNode.NodeData.of("基础节点");
        assertEquals("基础节点", data.getTitle());
        
        // 测试注释节点数据
        WorkflowBaseNode.NodeData commentData = WorkflowBaseNode.NodeData.comment("这是一个注释", 200, 100);
        assertEquals("这是一个注释", commentData.getNote());
        assertEquals(200, commentData.getWidth());
        assertEquals(100, commentData.getHeight());
        
        // 测试组节点数据
        WorkflowBaseNode.NodeData groupData = WorkflowBaseNode.NodeData.group("组节点", "#ff0000");
        assertEquals("组节点", groupData.getTitle());
        assertEquals("#ff0000", groupData.getColor());
        
        // 测试扩展属性
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", 123);
        data.setProperties(properties);
        
        assertEquals("value1", data.getProperties().get("key1"));
        assertEquals(123, data.getProperties().get("key2"));
    }
    
    @Test
    public void testNodeMetaCreation() {
        // 测试开始节点
        WorkflowNodeMeta startMeta = WorkflowNodeMeta.startNode(100.0, 200.0);
        assertTrue(startMeta.getIsStart());
        assertEquals(100.0, startMeta.getPosition().getX());
        assertEquals(200.0, startMeta.getPosition().getY());
        
        // 测试容器节点
        WorkflowNodeMeta containerMeta = WorkflowNodeMeta.containerNode(300.0, 400.0);
        assertTrue(containerMeta.getIsContainer());
        assertEquals(300.0, containerMeta.getPosition().getX());
        assertEquals(400.0, containerMeta.getPosition().getY());
        
        // 测试完整配置
        WorkflowNodeMeta fullMeta = WorkflowNodeMeta.builder()
                .position(WorkflowNodeMeta.Position.of(500.0, 600.0))
                .size(WorkflowNodeMeta.Size.of(200.0, 150.0))
                .padding(WorkflowNodeMeta.Padding.of(10))
                .draggable(true)
                .selectable(true)
                .deleteDisable(false)
                .copyDisable(false)
                .build();
        
        assertEquals(500.0, fullMeta.getPosition().getX());
        assertEquals(600.0, fullMeta.getPosition().getY());
        assertEquals(200.0, fullMeta.getSize().getWidth());
        assertEquals(150.0, fullMeta.getSize().getHeight());
        assertEquals(10, fullMeta.getPadding().getTop());
        assertTrue(fullMeta.getDraggable());
        assertTrue(fullMeta.getSelectable());
        assertFalse(fullMeta.getDeleteDisable());
        assertFalse(fullMeta.getCopyDisable());
    }
    
    @Test
    public void testBaseEdgeCreation() {
        // 创建边数据
        Map<String, Object> edgeProperties = new HashMap<>();
        edgeProperties.put("condition", "success");
        edgeProperties.put("priority", 1);
        
        WorkflowEdge.EdgeData edgeData = WorkflowEdge.EdgeData.builder()
                .properties(edgeProperties)
                .build();
        
        // 创建基础边（这里使用匿名内部类，实际使用中应该创建具体的实现类）
        WorkflowEdge edge = new WorkflowEdge() {
            // 匿名实现
        };
        edge.setId("edge-1");
        edge.setSourceNodeID("node-1");
        edge.setTargetNodeID("node-2");
        edge.setSourcePortID("output-1");
        edge.setTargetPortID("input-1");
        edge.setData(edgeData);
        
        // 验证边属性
        assertEquals("edge-1", edge.getId());
        assertEquals("node-1", edge.getSourceNodeID());
        assertEquals("node-2", edge.getTargetNodeID());
        assertEquals("output-1", edge.getSourcePortID());
        assertEquals("input-1", edge.getTargetPortID());
        assertNotNull(edge.getData());
        assertEquals("success", edge.getData().getProperties().get("condition"));
        assertEquals(1, edge.getData().getProperties().get("priority"));
        
        // 验证JSON序列化
        String json = edge.toJson();
        assertNotNull(json);
        assertTrue(json.contains("edge-1"));
        assertTrue(json.contains("node-1"));
        assertTrue(json.contains("node-2"));
    }
    
    @Test
    public void testComplexWorkflowStructure() {
        // 创建复杂的工作流结构
        
        // 1. 创建开始节点
        WorkflowNodeMeta startMeta = WorkflowNodeMeta.startNode(100.0, 100.0);
        WorkflowBaseNode.NodeData startData = WorkflowBaseNode.NodeData.of("开始");
        
        WorkflowBaseNode startNode = new WorkflowBaseNode() {};
        startNode.setId("start");
        startNode.setName("开始节点");
        startNode.setType("start");
        startNode.setMeta(startMeta);
        startNode.setData(startData);
        
        // 2. 创建动作节点
        WorkflowNodeMeta actionMeta = WorkflowNodeMeta.builder()
                .position(WorkflowNodeMeta.Position.of(300.0, 100.0))
                .size(WorkflowNodeMeta.Size.of(150.0, 80.0))
                .build();
        
        Map<String, Object> actionInputs = new HashMap<>();
        actionInputs.put("message", "Hello World");
        
        WorkflowBaseNode.NodeData actionData = WorkflowBaseNode.NodeData.builder()
                .title("发送消息")
                .inputs(actionInputs)
                .build();
        
        WorkflowBaseNode actionNode = new WorkflowBaseNode() {};
        actionNode.setId("action-1");
        actionNode.setName("发送消息");
        actionNode.setType("action");
        actionNode.setMeta(actionMeta);
        actionNode.setData(actionData);
        
        // 3. 创建边
        WorkflowEdge edge = new WorkflowEdge() {};
        edge.setId("edge-1");
        edge.setSourceNodeID("start");
        edge.setTargetNodeID("action-1");
        edge.setSourcePortID("output");
        edge.setTargetPortID("input");
        
        // 4. 创建组节点（容器）
        WorkflowNodeMeta groupMeta = WorkflowNodeMeta.containerNode(200.0, 200.0);
        WorkflowBaseNode.NodeData groupData = WorkflowBaseNode.NodeData.group("消息处理组", "#e6f3ff");
        
        WorkflowBaseNode groupNode = new WorkflowBaseNode() {};
        groupNode.setId("group-1");
        groupNode.setName("消息处理组");
        groupNode.setType("group");
        groupNode.setMeta(groupMeta);
        groupNode.setData(groupData);
        groupNode.setBlocks(java.util.Arrays.asList(startNode, actionNode));
        groupNode.setEdges(java.util.Arrays.asList(edge));
        
        // 验证组节点结构
        assertEquals(2, groupNode.getBlocks().size());
        assertEquals(1, groupNode.getEdges().size());
        assertEquals("start", groupNode.getBlocks().get(0).getId());
        assertEquals("action-1", groupNode.getBlocks().get(1).getId());
        assertEquals("edge-1", groupNode.getEdges().get(0).getId());
        
        // 验证JSON序列化
        String groupJson = groupNode.toJson();
        assertNotNull(groupJson);
        assertTrue(groupJson.contains("group-1"));
        assertTrue(groupJson.contains("消息处理组"));
        assertTrue(groupJson.contains("start"));
        assertTrue(groupJson.contains("action-1"));
        assertTrue(groupJson.contains("edge-1"));
    }
} 