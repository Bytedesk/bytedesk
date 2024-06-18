/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 15:13:30
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
    TEXT("text"), // 0.文本
    IMAGE("image"), // 1.图片
    FILE("file"), // 2.文件
    VOICE("voice"), // 3.语音
    VIDEO("video"), // 4.视频
    THREAD("thread"), // 5.新会话
    TYPING("typing"), // 6.正在输入
    RECALL("recall"), // 7.撤回
    RECEIPT("receipt"), // 8.消息回执
    ROBOT_QA("robot_qa"), // 9.机器人问答
    QUICKBUTTON_QA("quickbutton_qa"), // 10.快捷按钮问答
    QUICKBUTTON_URL("quickbutton_url"), // 11.快捷按钮URL
    FAQ_QA("faq_qa"); // 12.常见问题问答

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