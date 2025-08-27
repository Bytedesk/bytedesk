/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:28:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 10:28:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.variable;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 工作流变量请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowVariableRequest extends BaseRequest {

    /**
     * 工作流UID
     */
    private String workflowUid;
    
    /**
     * 节点UID
     */
    private String nodeUid;
    
    /**
     * 变量名称
     */
    private String name;
    
    /**
     * 变量值（JSON格式）
     */
    private String value;
    
    /**
     * 变量类型
     */
    // private String type;
    
    /**
     * 变量作用域
     */
    private String scope;
    
    /**
     * 变量描述
     */
    private String description;
}
