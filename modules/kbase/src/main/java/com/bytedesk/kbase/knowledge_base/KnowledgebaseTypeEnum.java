/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 10:36:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-28 16:03:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

public enum KnowledgebaseTypeEnum {
    ASSISTANT, // 文档助手，内部文档知识库问答
    HELPCENTER, // 帮助中心
    LLM, // 大模型
    KEYWORD, // 关键词
    FAQ, // 常见问题
    QUICKREPLY, // 快捷回复
    AUTOREPLY, // 自动回复
    BLOG, // 博客
    EMAIL, // 邮件
    TABOO; // 敏感词

    // 根据字符串查找对应的枚举常量
    public static KnowledgebaseTypeEnum fromValue(String value) {
        for (KnowledgebaseTypeEnum type : KnowledgebaseTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No KnowledgebaseTypeEnum constant with value: " + value);
    }
}
