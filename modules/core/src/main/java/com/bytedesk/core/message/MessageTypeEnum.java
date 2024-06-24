/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-22 12:02:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

public enum MessageTypeEnum {
    SYSTEM("system"), 
    TEXT("text"), 
    IMAGE("image"),
    FILE("file"),
    AUDIO("audio"), 
    VIDEO("video"), 
    THREAD("thread"),
    TYPING("typing"), 
    RECALL("recall"), 
    RECEIPT("receipt"), 
    ROBOT_QA("robot_qa"), 
    QUICKBUTTON_QA("quickbutton_qa"), 
    QUICKBUTTON_URL("quickbutton_url"),
    FAQ_QA("faq_qa"),
    RATE_INVITE("rate_invite"), // 被邀请评价
    RATE_INITIATIVE("rate_initiative"), // 主动评价
    ; 

    private final String value;

    MessageTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据字符串查找对应的枚举常量
    public static MessageTypeEnum fromValue(String value) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No MessageTypeEnum constant with value: " + value);
    }
}