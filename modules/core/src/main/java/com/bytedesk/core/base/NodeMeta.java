/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-25 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 11:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 节点元数据类
 * 参考 FlowNodeMeta 和 WorkflowNodeMeta 接口定义
 * 用于存储节点的位置信息、渲染配置等元数据
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class NodeMeta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // === 基础位置信息 ===
    /**
     * 节点位置信息
     */
    private Position position;
    
    /**
     * 子画布位置
     */
    private Position canvasPosition;
    
    // === 节点状态控制 ===
    /**
     * 是否为开始节点
     */
    private Boolean isStart;
    
    /**
     * 是否可添加
     */
    private Boolean addable;
    
    /**
     * 是否可展开
     */
    private Boolean expandable;
    
    /**
     * 是否可拖拽
     */
    private Boolean draggable;
    
    /**
     * 是否可选择
     */
    private Boolean selectable;
    
    /**
     * 是否禁用删除
     */
    private Boolean deleteDisable;
    
    /**
     * 是否禁用复制
     */
    private Boolean copyDisable;
    
    /**
     * 是否禁用添加
     */
    private Boolean addDisable;
    
    /**
     * 是否隐藏
     */
    private Boolean hidden;
    
    /**
     * 是否禁用输入点
     */
    private Boolean inputDisable;
    
    /**
     * 是否禁用输出点
     */
    private Boolean outputDisable;
    
    // === 尺寸和布局 ===
    /**
     * 节点尺寸
     */
    private Size size;
    
    /**
     * 是否禁用自动调整大小
     */
    private Boolean autoResizeDisable;
    
    /**
     * 默认是否展开
     */
    private Boolean defaultExpanded;
    
    /**
     * 默认是否折叠
     */
    private Boolean defaultCollapsed;
    
    /**
     * 间距
     */
    private Integer spacing;
    
    /**
     * 内边距
     */
    private Padding padding;
    
    /**
     * 行内块前置间距
     */
    private Integer inlineSpacingPre;
    
    /**
     * 行内块后置间距
     */
    private Integer inlineSpacingAfter;
    
    /**
     * 最小行内块间距
     */
    private Integer minInlineBlockSpacing;
    
    // === 渲染相关 ===
    /**
     * 渲染键
     */
    private String renderKey;
    
    /**
     * 是否为行内块
     */
    private Boolean isInlineBlocks;
    
    /**
     * 是否为节点结束
     */
    private Boolean isNodeEnd;
    
    /**
     * 是否为容器节点
     */
    private Boolean isContainer;
    
    /**
     * 使用动态点位
     */
    private Boolean useDynamicPort;
    
    /**
     * 默认点位
     */
    private Map<String, Object> defaultPorts;
    
    /**
     * 子画布配置
     */
    private Map<String, Object> subCanvas;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> extra;
    
    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 位置信息类
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Position implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * X坐标
         */
        private Double x;
        
        /**
         * Y坐标
         */
        private Double y;
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
        
        /**
         * 创建位置实例的便捷方法
         */
        public static Position of(Double x, Double y) {
            return Position.builder()
                    .x(x)
                    .y(y)
                    .build();
        }
    }
    
    /**
     * 尺寸信息类
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Size implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 宽度
         */
        private Double width;
        
        /**
         * 高度
         */
        private Double height;
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
        
        /**
         * 创建尺寸实例的便捷方法
         */
        public static Size of(Double width, Double height) {
            return Size.builder()
                    .width(width)
                    .height(height)
                    .build();
        }
    }
    
    /**
     * 内边距信息类
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Padding implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 上边距
         */
        private Integer top;
        
        /**
         * 右边距
         */
        private Integer right;
        
        /**
         * 下边距
         */
        private Integer bottom;
        
        /**
         * 左边距
         */
        private Integer left;
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
        
        /**
         * 创建内边距实例的便捷方法
         */
        public static Padding of(Integer top, Integer right, Integer bottom, Integer left) {
            return Padding.builder()
                    .top(top)
                    .right(right)
                    .bottom(bottom)
                    .left(left)
                    .build();
        }
        
        /**
         * 创建统一内边距的便捷方法
         */
        public static Padding of(Integer padding) {
            return Padding.builder()
                    .top(padding)
                    .right(padding)
                    .bottom(padding)
                    .left(padding)
                    .build();
        }
    }
    
    /**
     * 创建NodeMeta实例的便捷方法
     */
    public static NodeMeta of(Double x, Double y) {
        return NodeMeta.builder()
                .position(Position.of(x, y))
                .build();
    }
    
    /**
     * 创建NodeMeta实例的便捷方法
     */
    public static NodeMeta of(Position position) {
        return NodeMeta.builder()
                .position(position)
                .build();
    }
    
    /**
     * 创建开始节点的便捷方法
     */
    public static NodeMeta startNode(Double x, Double y) {
        return NodeMeta.builder()
                .position(Position.of(x, y))
                .isStart(true)
                .build();
    }
    
    /**
     * 创建容器节点的便捷方法
     */
    public static NodeMeta containerNode(Double x, Double y) {
        return NodeMeta.builder()
                .position(Position.of(x, y))
                .isContainer(true)
                .build();
    }
} 