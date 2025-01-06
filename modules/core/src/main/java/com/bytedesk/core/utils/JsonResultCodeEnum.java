/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 22:03:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-06 14:15:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

public enum JsonResultCodeEnum {
    SUCCESS("success",200),
    AGENT_OFFLINE("agent offline", 201),
    AGENT_UNAVAILABLE("agent unavailable", 202),
    ROUTE_TO_ROBOT("route to robot",203), // route to robot
    ROBOT_ANSWER_START("robot answer start",204),
    ROBOT_ANSWER_CONTINUE("robot answer continue",205),
    ROBOT_ANSWER_END("robot answer end",206),
    ROBOT_DISABLED("robot disabled",207); 

    private final String name;
    private final int value;

    JsonResultCodeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

}
