/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:42:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-18 12:09:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

// model无法枚举，只能通过字符串来指定
public enum RobotModelEnum {
    ZHIPUAI_GLM_3_TURBO,
    ZHIPUAI_GLM_4,
    ZHIPUAI_CogView,
    ZHIPUAI_GLM4V,
    OLLAMA,
    OPENAI;
    
    // 根据整型值查找对应的枚举常量
    public static RobotModelEnum fromValue(String value) {
        for (RobotModelEnum type : RobotModelEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No RobotModelEnum constant with value " + value);
    }
}
