/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 15:11:56
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
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String type;

    private String description;

    private String status;

    private List<BaseEdge> inEdges;
    
    private List<BaseEdge> outEdges;

    private NodeMeta meta;

    // 新增字段，对应JSON中的data部分
    private NodeData data;
    
    // 新增字段，对应JSON中的blocks部分（用于group和loop节点）
    private List<BaseNode> blocks;
    
    // 新增字段，对应JSON中的edges部分（用于group和loop节点）
    private List<BaseEdge> edges;

    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 节点数据类
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class NodeData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String title;
        
        private Map<String, Object> inputsValues;
        
        private Map<String, Object> inputs;
        
        private Map<String, Object> outputs;
        
        // 用于comment节点的特殊字段
        private Map<String, Object> size;
        private String note;
        
        // 用于group节点的特殊字段
        private String color;
        
        public String toJson() {
            return JSON.toJSONString(this);
        }
    }

}
