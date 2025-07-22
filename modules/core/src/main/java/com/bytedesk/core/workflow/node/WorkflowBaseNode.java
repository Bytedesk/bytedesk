/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 23:49:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.node;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.edge.WorkflowEdge;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 节点数据基类
 * 参考 WorkflowNodeJSON 接口定义
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class WorkflowBaseNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点唯一标识
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 节点状态
     */
    private String status;

    /**
     * 入边列表
     */
    private List<WorkflowEdge> inEdges;
    
    /**
     * 出边列表
     */
    private List<WorkflowEdge> outEdges;

    /**
     * 节点元数据（UI相关配置）
     */
    private WorkflowNodeMeta meta;

    /**
     * 节点数据（表单数据）
     */
    private NodeData data;
    
    /**
     * 子节点列表（用于group和loop节点）
     */
    private List<WorkflowBaseNode> blocks;
    
    /**
     * 子节点间连线（用于group和loop节点）
     */
    private List<WorkflowEdge> edges;

    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 节点数据类
     * 用于存储节点的表单数据、输入输出配置等
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class NodeData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 节点标题
         */
        private String title;

        /**
         * 节点内容
         */
        private String content;
        
        /**
         * 输入值映射
         */
        private Map<String, Object> inputsValues;
        
        /**
         * 输入配置
         */
        private Map<String, Object> inputs;
        
        /**
         * 输出配置
         */
        private Map<String, Object> outputs;
        
        /**
         * 扩展属性
         */
        private Map<String, Object> properties;
        
        // === 特殊节点类型字段 ===
        
        /**
         * 用于comment节点的尺寸信息
         */
        private Map<String, Object> size;
        
        /**
         * 用于comment节点的注释内容
         */
        private String note;
        
        /**
         * 用于group节点的颜色
         */
        private String color;
        
        /**
         * 用于group节点的背景色
         */
        private String backgroundColor;
        
        /**
         * 用于group节点的边框色
         */
        private String borderColor;
        
        /**
         * 用于group节点的边框宽度
         */
        private Integer borderWidth;
        
        /**
         * 用于group节点的圆角半径
         */
        private Integer borderRadius;
        
        /**
         * 用于group节点的透明度
         */
        private Double opacity;
        
        /**
         * 用于group节点的阴影
         */
        private String shadow;
        
        /**
         * 用于group节点的字体大小
         */
        private Integer fontSize;
        
        /**
         * 用于group节点的字体颜色
         */
        private String fontColor;
        
        /**
         * 用于group节点的字体粗细
         */
        private String fontWeight;
        
        /**
         * 用于group节点的字体样式
         */
        private String fontStyle;
        
        /**
         * 用于group节点的文本对齐方式
         */
        private String textAlign;
        
        /**
         * 用于group节点的垂直对齐方式
         */
        private String verticalAlign;
        
        /**
         * 用于group节点的内边距
         */
        private String padding;
        
        /**
         * 用于group节点的外边距
         */
        private String margin;
        
        /**
         * 用于group节点的宽度
         */
        private Integer width;
        
        /**
         * 用于group节点的高度
         */
        private Integer height;
        
        /**
         * 用于group节点的最小宽度
         */
        private Integer minWidth;
        
        /**
         * 用于group节点的最小高度
         */
        private Integer minHeight;
        
        /**
         * 用于group节点的最大宽度
         */
        private Integer maxWidth;
        
        /**
         * 用于group节点的最大高度
         */
        private Integer maxHeight;
        
        /**
         * 用于group节点的显示方式
         */
        private String display;
        
        /**
         * 用于group节点的定位方式
         */
        private String position;
        
        /**
         * 用于group节点的Z轴层级
         */
        private Integer zIndex;
        
        /**
         * 用于group节点的溢出处理
         */
        private String overflow;
        
        /**
         * 用于group节点的光标样式
         */
        private String cursor;
        
        /**
         * 用于group节点的用户选择
         */
        private String userSelect;
        
        /**
         * 用于group节点的指针事件
         */
        private String pointerEvents;
        
        /**
         * 用于group节点的变换
         */
        private String transform;
        
        /**
         * 用于group节点的过渡效果
         */
        private String transition;
        
        /**
         * 用于group节点的动画
         */
        private String animation;
        
        /**
         * 用于group节点的过滤器
         */
        private String filter;
        
        /**
         * 用于group节点的混合模式
         */
        private String mixBlendMode;
        
        /**
         * 用于group节点的背景图片
         */
        private String backgroundImage;
        
        /**
         * 用于group节点的背景重复
         */
        private String backgroundRepeat;
        
        /**
         * 用于group节点的背景位置
         */
        private String backgroundPosition;
        
        /**
         * 用于group节点的背景尺寸
         */
        private String backgroundSize;
        
        /**
         * 用于group节点的背景附件
         */
        private String backgroundAttachment;
        
        /**
         * 用于group节点的背景裁剪
         */
        private String backgroundClip;
        
        /**
         * 用于group节点的背景原点
         */
        private String backgroundOrigin;
        
        /**
         * 用于group节点的背景混合模式
         */
        private String backgroundBlendMode;
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            return JSON.toJSONString(this);
        }
        
        /**
         * 创建基础节点数据的便捷方法
         */
        public static NodeData of(String title) {
            return NodeData.builder()
                    .title(title)
                    .build();
        }
        
        /**
         * 创建注释节点数据的便捷方法
         */
        public static NodeData comment(String note, Integer width, Integer height) {
            return NodeData.builder()
                    .note(note)
                    .width(width)
                    .height(height)
                    .build();
        }
        
        /**
         * 创建组节点数据的便捷方法
         */
        public static NodeData group(String title, String color) {
            return NodeData.builder()
                    .title(title)
                    .color(color)
                    .build();
        }
    }

}
