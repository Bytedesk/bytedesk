/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-17 14:44:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-18 10:39:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

// 模型类型:
public enum LlmModelTypeEnum {
    TEXT("text"), // 文本对型
    EMBEDDING("embedding"), // 向量嵌入模型
    RERANK("rerank"), // 排序模型
    VISION("vision"), // 视觉模型
    CODE("code"),  // 代码模型
    REASONING("reasoning"), // 推理模型
    TEXT2IMAGE("text2image"), // 文本生成图像模型
    IMAGE2TEXT("image2text"), // 图像理解文本模型
    AUDIO2TEXT("audio2text"), // 语音转文本模型
    TEXT2AUDIO("text2audio"), // 文本转语音模型
    TEXT2VIDEO("text2video"), // 文本生成视频模型
    VIDEO2TEXT("video2text"); // 视频理解模型

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
