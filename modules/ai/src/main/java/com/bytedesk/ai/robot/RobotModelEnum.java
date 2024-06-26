/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 10:42:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-06 13:12:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

public enum RobotModelEnum {
    ZHIPUAI_GLM_3_TURBO("glm-3-turbo"),
    ZHIPUAI_GLM_4("GLM-4"),
    ZHIPUAI_CogView("cogview"),
    ZHIPUAI_GLM4V("glm-4v"),
    OLLAMA("ollama"),
    OPENAI("openai");

    // private final String name;
    private final String value;

    RobotModelEnum(String value) {
        // this.name = name;
        this.value = value;
    }

    // public String getName() {
    //     return name;
    // }

    // 获取枚举常量的整型值
    public String getValue() {
        return value;
    }

    // 根据整型值查找对应的枚举常量
    public static RobotModelEnum fromValue(String value) {
        for (RobotModelEnum type : RobotModelEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No RobotModelEnum constant with value " + value);
    }

    // public static RobotModelEnum fromString(String typeStr) {
    //     // 使用try-catch处理可能的异常
    //     try {
    //         return RobotModelEnum.valueOf(typeStr);
    //     } catch (IllegalArgumentException e) {
    //         // 处理错误，例如记录日志或抛出更具体的异常
    //         throw new IllegalArgumentException("Invalid robot type: " + typeStr, e);
    //     }
    // }
}
