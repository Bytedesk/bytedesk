/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-22 16:01:55
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 边数据基类
 * 参考 WorkflowEdgeJSON 接口定义
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class WorkflowEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    // private String type;

    // private String description;
    
    // 对应JSON中的sourceNodeID和targetNodeID
    private String sourceNodeID;
    
    private String targetNodeID;
    
    // 对应JSON中的sourcePortID和targetPortID
    private String sourcePortID;
    
    private String targetPortID;
    
    // 对应JSON中的data部分
    private EdgeData data;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static WorkflowEdge fromJson(String json) {
        return JSON.parseObject(json, WorkflowEdge.class);
    }
    
    /**
     * 边数据类
     */
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class EdgeData implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        // 可以存储任意边相关的数据
        private Map<String, Object> properties;
        
        public String toJson() {
            return JSON.toJSONString(this);
        }
    }
    
}
