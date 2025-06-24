/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-24 15:53:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:22:14
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
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseNode;
import com.bytedesk.core.workflow.edge.WorkflowEdge;

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
public class WorkflowDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点列表
     */
    private List<BaseNode> nodes;
    
    /**
     * 边列表
     */
    private List<WorkflowEdge> edges;
    
    /**
     * 从JSON字符串创建WorkflowDocument
     */
    public static WorkflowDocument fromJson(String json) {
        return JSON.parseObject(json, WorkflowDocument.class);
    }
    
    /**
     * 转换为JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
} 