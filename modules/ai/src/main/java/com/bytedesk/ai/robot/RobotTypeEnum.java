/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 11:10:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-09 09:54:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

public enum RobotTypeEnum {
    SERVICE, // 客服机器人
    MARKETING, // 营销机器人，售前营销获客机器人
    LLM, // 问答机器人, 直接调用大模型
    KB, // 知识库机器人, 跟某一个知识库对话
    KBDOC, // 知识库机器人, 跟某一个文档对话
;

    // 根据整型值查找对应的枚举常量
    public static RobotTypeEnum fromValue(String value) {
        for (RobotTypeEnum type : RobotTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No RobotTypeEnum constant with value " + value);
    }

}
