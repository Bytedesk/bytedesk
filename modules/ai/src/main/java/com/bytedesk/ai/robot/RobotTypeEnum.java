/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 11:10:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-24 14:11:39
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

public enum RobotTypeEnum {
    SERVICE, // 客服机器人
    MARKETING, // 营销机器人
    KNOWLEDGEBASE, // 知识库机器人
    QA; // 问答机器人

    // 根据整型值查找对应的枚举常量
    public static RobotTypeEnum fromValue(String value) {
        for (RobotTypeEnum type : RobotTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

    public static RobotTypeEnum fromString(String typeStr) {
        // 使用try-catch处理可能的异常
        try {
            return RobotTypeEnum.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            // 处理错误，例如记录日志或抛出更具体的异常
            throw new IllegalArgumentException("Invalid robot type: " + typeStr, e);
        }
    }

}
