/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-30 22:19:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 12:17:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

public enum UploadTypeEnum {
    ASSISTANT, // 文档助手，内部文档知识库问答
    HELPCENTER, // 帮助文档
    LLM, // 大模型
    LLM_TEXT, // 大模型文件
    LLM_FILE, // 大模型文件
    KEYWORD, // 关键词
    FAQ, // 常见问题
    QUICKREPLY, // 快捷回复
    AUTOREPLY_FIXED, // 自动回复固定
    AUTOREPLY_KEYWORD, // 自动回复关键词
    PROMPT, // 提示词
    BLOG, // 博客
    EMAIL, // 邮件
    TABOO, // 敏感词
    MEMBER, // 导入成员
    CHAT, // 聊天对话
    TICKET, // 工单
    BPMN, // 流程图
    CUSTOMER, // 客户
    WORKFLOW, // 工作流
    ATTACHMENT; // 附件

    // 根据字符串查找对应的枚举常量
    public static UploadTypeEnum fromValue(String value) {
        for (UploadTypeEnum type : UploadTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No UploadTypeEnum constant with value: " + value);
    }
}
