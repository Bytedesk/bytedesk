/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 18:54:21
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
    WELCOME("welcome"),
    CONTINUE("continue"),
    SYSTEM("system"),
    TEXT("text"),
    IMAGE("image"),
    FILE("file"),
    AUDIO("audio"),
    VIDEO("video"),
    MUSIC("music"),
    LOCATION("location"),
    GOODS("goods"), // 商品
    CARD("card"),
    EVENT("event"),
    GUESS("guess"), // 猜你想问
    SKILL("skill"), // 技能
    ORDER("order"), // 订单
    POLL("poll"), // 投票
    FORM("form"), // 表单：询前表单
    LEAVE_MSG("leave_msg"), // 留言
    TICKET("ticket"), // 客服工单
    TYPING("typing"), // 正在输入
    PREVIEW("preview"), // 消息预知
    RECALL("recall"), // 撤回
    DELIVERED("delivered"), // 回执: 已送达
    READ("read"), // 回执: 已读
    QUOTATION("quotation"), // qoute message
    KICKOFF("kickoff"), // kickoff other clients
    SHAKE("shake"), // shake window
    ROBOT_QA("robot_qa"), // 机器人QA
    QUICKBUTTON_QA("quickbutton_qa"),
    QUICKBUTTON_URL("quickbutton_url"),
    FAQ_QA("faq_qa"),
    RATE_INVITE("rate_invite"),
    RATE_INITIATIVE("rate_initiative"),
    TRANSFER("transfer"),
    TRANSFER_REJECT("transfer_reject"),
    TRANSFER_ACCEPT("transfer_accept"),
    INVITE("invite"),
    INVITE_REJECT("invite_reject"),
    INVITE_ACCEPT("invite_accept"),
    ARTICLE("article"),
    WEBRTC_AUDIO_INVITE("webrtc_audio_invite"),
    WEBRTC_VIDEO_INVITE("webrtc_video_invite"),
    GROUP_CREATE("group_create"),
    GROUP_INVITE("group_invite"),
    GROUP_DISMISS("group_dismiss"),
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

    public static boolean isRecept(MessageTypeEnum type) {
        return MessageTypeEnum.READ == type
                || MessageTypeEnum.DELIVERED == type;
    }

    public static boolean shouldCache(MessageTypeEnum type) {
        return MessageTypeEnum.TEXT == type
                || MessageTypeEnum.IMAGE == type
                || MessageTypeEnum.FILE == type
                || MessageTypeEnum.AUDIO == type
                || MessageTypeEnum.VIDEO == type
                || MessageTypeEnum.MUSIC == type
                || MessageTypeEnum.LOCATION == type
                || MessageTypeEnum.CARD == type;
    }

}