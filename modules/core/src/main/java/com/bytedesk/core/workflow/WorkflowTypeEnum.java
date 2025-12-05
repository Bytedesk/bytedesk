/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 09:55:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

/**
 * 工作流类型枚举
 * 根据业内实践分类
 * 
 * 各分类的典型应用场景
 *   CHATBOT: 电商客服机器人、智能助手、FAQ自动回复
 *   TASK: 订单处理等后台运行任务
 */
public enum WorkflowTypeEnum {

    /**
     * 聊天机器人，启动对话窗口
     * 处理自动回复、智能问答、意图识别等AI对话流程
     */
    CHATBOT,

    /**
     * 任务工作流，后台运行
     * 适用于各种通用业务流程
     */
    TASK,
    
}
