/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 10:36:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 18:04:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

public enum KbaseTypeEnum {
    ASSISTANT, // 文档助手，内部文档知识库问答
    HELPCENTER, // 帮助中心
    NOTEBASE, // 内部知识库
    LLM, // 大模型
    KEYWORD, // 关键词
    // FAQ, // 常见问题，合并到LLM
    QUICKREPLY, // 快捷回复
    AUTOREPLY, // 自动回复
    BLOG, // 博客
    EMAIL, // 邮件
    TABOO; // 敏感词

    // 根据字符串查找对应的枚举常量
    public static KbaseTypeEnum fromValue(String value) {
        for (KbaseTypeEnum type : KbaseTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No KbaseTypeEnum constant with value: " + value);
    }
}
