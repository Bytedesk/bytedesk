/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-30 22:19:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-28 06:14:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

public enum UploadTypeEnum {
    ASSISTANT, // 文档助手，内部文档知识库问答
    HELPDOC, // 帮助文档
    LLM, // 大模型
    KEYWORD, // 关键词
    FAQ, // 常见问题
    QUICKREPLY, // 快捷回复
    AUTOREPLY, // 自动回复
    BLOG, // 博客
    EMAIL, // 邮件
    TABOO, // 敏感词
    MEMBER, // 导入成员
    // IMPORT, // 导入Excel
    CHAT, // 聊天对话
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
