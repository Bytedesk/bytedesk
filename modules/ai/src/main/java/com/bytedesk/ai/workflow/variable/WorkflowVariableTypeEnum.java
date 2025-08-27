/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:18:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 10:18:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.variable;

/**
 * 工作流变量类型枚举
 */
public enum WorkflowVariableTypeEnum {
    
    /**
     * 字符串类型
     */
    STRING,
    
    /**
     * 数字类型
     */
    NUMBER,
    
    /**
     * 布尔类型
     */
    BOOLEAN,
    
    /**
     * 日期类型
     */
    DATE,
    
    /**
     * 对象类型（JSON格式）
     */
    OBJECT,
    
    /**
     * 数组类型（JSON格式）
     */
    ARRAY,
    
    /**
     * 文件引用类型
     */
    FILE
}
