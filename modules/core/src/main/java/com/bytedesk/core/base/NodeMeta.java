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

import com.alibaba.fastjson2.JSON;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 节点元数据类
 * 用于存储节点的位置信息等元数据
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class NodeMeta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 节点位置信息
     */
    private Position position;
    
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
} 