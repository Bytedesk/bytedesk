/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-17 14:44:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 14:46:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

// 模型类型: chat、embedding、vision、code、text2image、image2text、audio2text、text2audio、text2video、video2text
public enum LlmModelTypeEnum {
    CHAT("chat"),
    EMBEDDING("embedding"),
    VISION("vision"),
    CODE("code"),
    REASONING("reasoning"),
    TEXT2IMAGE("text2image"),
    IMAGE2TEXT("image2text"),
    AUDIO2TEXT("audio2text"),
    TEXT2AUDIO("text2audio"),
    TEXT2VIDEO("text2video"),
    VIDEO2TEXT("video2text");

    private String type;

   LlmModelTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static LlmModelTypeEnum fromType(String type) {
        for (LlmModelTypeEnum modelType : LlmModelTypeEnum.values()) {
            if (modelType.getType().equalsIgnoreCase(type)) {
                return modelType;
            }
        }
        throw new IllegalArgumentException("Unknown model type: " + type);
    }
    
}
