/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02 10:15:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 10:15:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.variable;

/**
 * 工作流变量作用域枚举
 */
public enum WorkflowVariableScopeEnum {
    
    /**
     * 全局变量
     * 在整个工作流生命周期中可见，可以跨节点、跨执行访问
     */
    GLOBAL,
    
    /**
     * 局部变量
     * 仅在当前节点可见，节点执行完成后变量不可访问
     */
    LOCAL,
    
    /**
     * 会话变量
     * 在整个会话过程中可见，跨工作流实例可用
     */
    SESSION
}
