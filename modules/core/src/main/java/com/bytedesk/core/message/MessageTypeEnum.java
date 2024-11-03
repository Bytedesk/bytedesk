/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-02 10:25:13
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

// \(".*?"\)
public enum MessageTypeEnum {
    WELCOME,
    CONTINUE,
    SYSTEM,
    NOTICE,
    TEXT,
    IMAGE,
    FILE,
    AUDIO,
    VIDEO,
    MUSIC,
    LOCATION,
    LINK,
    GOODS, // 商品
    CARD,
    EVENT, // 事件：离线、上线等
    GUESS, // 猜你想问
    HOT, // 热门话题
    SHORTCUT, // 快捷路径
    ORDER, // 订单
    POLL, // 投票
    POLL_SUBMIT, // 投票提交
    FORM, // 表单：询前表单
    FORM_SUBMIT, // 表单提交
    LEAVE_MSG, // 留言
    LEAVE_MSG_SUBMIT, // 留言提交
    TICKET, // 客服工单
    TICKET_SUBMIT, // 工单提交
    TYPING, // 正在输入
    PROCESSING, // 正在处理，等待大模型回复中
    STREAM, // 流式消息TEXT，大模型回复
    PREVIEW, // 消息预知
    RECALL, // 撤回
    DELIVERED, // 回执: 已送达
    READ, // 回执: 已读
    QUOTATION, // 引用消息
    KICKOFF, // kickoff other clients
    SHAKE, // shake window
    FAQ, // 常见问题FAQ
    FAQ_UP, // 常见问题答案评价:UP
    FAQ_DOWN, // 常见问题答案评价:DOWN
    ROBOT, // 机器人
    ROBOT_UP, // 机器人答案评价:UP
    ROBOT_DOWN, // 机器人答案评价:DOWN
    RATE, // 访客主动评价
    RATE_INVITE, // 客服邀请评价
    RATE_SUBMIT, // 访客提交评价
    RATE_CANCEL, // 访客取消评价
    AUTO_CLOSED, // 自动关闭
    AGENT_CLOSED, // 客服关闭
    TRANSFER,
    TRANSFER_REJECT,
    TRANSFER_ACCEPT,
    INVITE,
    INVITE_REJECT,
    INVITE_ACCEPT,
    ARTICLE,
    WEBRTC_AUDIO_INVITE,
    WEBRTC_VIDEO_INVITE,
    GROUP_CREATE,
    GROUP_INVITE,
    GROUP_DISMISS,
    ;

    // 根据字符串查找对应的枚举常量
    public static MessageTypeEnum fromValue(String value) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        // throw new IllegalArgumentException("No MessageTypeEnum constant with value: " + value);
        return TEXT;
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