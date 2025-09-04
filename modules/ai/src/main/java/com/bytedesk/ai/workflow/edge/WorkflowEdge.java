/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 18:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.edge;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 工作流边数据类
 * 用于定义工作流中节点之间的连接关系
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 边的唯一标识符
     */
    private String id;

    /**
     * 边的名称
     */
    private String name;

    /**
     * 边的类型（可选）
     */
    private String type;

    /**
     * 边的描述（可选）
     */
    private String description;
    
    /**
     * 源节点ID
     */
    private String sourceNodeID;
    
    /**
     * 目标节点ID
     */
    private String targetNodeID;
    
    /**
     * 源端口ID（可选，用于多端口节点）
     */
    private String sourcePortID;
    
    /**
     * 目标端口ID（可选，用于多端口节点）
     */
    private String targetPortID;

    /**
     * 边的权重或优先级（可选）
     */
    @Builder.Default
    private Integer weight = 0;

    /**
     * 条件表达式（用于条件边）
     */
    private String conditionExpression;

    /**
     * 边的标签（可选）
     */
    private String label;
    
    /**
     * 边的数据部分
     */
    private EdgeData data;

    /**
     * 边的样式配置
     */
    private EdgeStyle style;

    /**
     * 将对象转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * 从JSON字符串创建对象
     */
    public static WorkflowEdge fromJson(String json) {
        return JSON.parseObject(json, WorkflowEdge.class);
    }

    /**
     * 检查是否连接指定的两个节点
     */
    public boolean connectsNodes(String sourceNodeId, String targetNodeId) {
        return this.sourceNodeID != null && this.sourceNodeID.equals(sourceNodeId) &&
               this.targetNodeID != null && this.targetNodeID.equals(targetNodeId);
    }

    /**
     * 检查是否连接指定的节点（作为源或目标）
     */
    public boolean connectsNode(String nodeId) {
        return (this.sourceNodeID != null && this.sourceNodeID.equals(nodeId)) ||
               (this.targetNodeID != null && this.targetNodeID.equals(nodeId));
    }

    /**
     * 获取连接的另一个节点ID
     */
    public String getOtherNodeId(String nodeId) {
        if (this.sourceNodeID != null && this.sourceNodeID.equals(nodeId)) {
            return this.targetNodeID;
        } else if (this.targetNodeID != null && this.targetNodeID.equals(nodeId)) {
            return this.sourceNodeID;
        }
        return null;
    }
    
    /**
     * 边数据类
     * 用于存储边的自定义数据
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EdgeData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 可以存储任意边相关的数据
         */
        private Map<String, Object> properties;

        /**
         * 自定义属性
         */
        private Map<String, String> attributes;

        /**
         * 边的配置数据
         */
        private Map<String, Object> config;
        
        /**
         * 将对象转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
    }

    /**
     * 边样式类
     * 用于定义边的视觉样式
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EdgeStyle implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 边的颜色
         */
        @Builder.Default
        private String color = "#666666";

        /**
         * 边的线宽
         */
        @Builder.Default
        private Integer strokeWidth = 2;

        /**
         * 边的线型（solid, dashed, dotted）
         */
        @Builder.Default
        private String strokeType = "solid";

        /**
         * 是否显示箭头
         */
        @Builder.Default
        private Boolean showArrow = true;

        /**
         * 箭头大小
         */
        @Builder.Default
        private Integer arrowSize = 8;

        /**
         * 边的透明度 (0.0 - 1.0)
         */
        @Builder.Default
        private Double opacity = 1.0;

        /**
         * 是否动画显示
         */
        @Builder.Default
        private Boolean animated = false;

        /**
         * 将对象转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
    }
    
}
