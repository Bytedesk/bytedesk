/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-11 15:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-11 15:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

/**
 * 源引用类型枚举
 * 用于标识AI回答中引用内容的来源类型
 */
public enum SourceTypeEnum {
    
    FAQ("faq", "常见问题"),
    TEXT("text", "文本内容"),
    CHUNK("chunk", "文档片段"),
    WEBPAGE("webpage", "网页内容"),
    FILE("file", "文件内容"),
    DOCUMENT("document", "文档"),
    ARTICLE("article", "文章"),
    IMAGE("image", "图片"),
    VIDEO("video", "视频"),
    AUDIO("audio", "音频");
    
    private final String code;
    private final String description;
    
    SourceTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举值
     * @param code 代码
     * @return 对应的枚举值，如果没有找到则返回null
     */
    public static SourceTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (SourceTypeEnum type : SourceTypeEnum.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 检查代码是否有效
     * @param code 代码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
