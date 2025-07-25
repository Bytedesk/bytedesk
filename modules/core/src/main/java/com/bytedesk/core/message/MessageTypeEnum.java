/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-26 08:02:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
    QUEUE,
    NOTICE,
    TEXT, // 文本消息
    IMAGE, // 图片消息
    FILE, // 文件消息
    DOCUMENT, // 文档消息
    AUDIO, // 音频消息
    VOICE, // 语音消息
    VIDEO, // 视频消息
    MUSIC,
    LOCATION,
    LINK,
    GOODS, // 商品
    CARD,
    EVENT, // 事件：离线、上线等
    EXTRA, // 附加信息
    GUESS, // 猜你想问
    HOT, // 热门话题
    SHORTCUT, // 快捷路径
    ORDER, // 订单
    // 
    POLL, // 投票
    POLL_SUBMIT, // 投票提交
    // 
    FORM, // 表单：询前表单
    FORM_SUBMIT, // 表单提交
    // 选择消息
    CHOICE, // 客服发送选项消息，让访客选择
    CHOICE_SUBMIT, // 选择提交
    // 确认消息
    CONFIRM, // 客服发送确认消息，让访客确认：是、否
    CONFIRM_SUBMIT, // 确认提交
    // 
    LEAVE_MSG, // 留言
    LEAVE_MSG_SUBMIT, // 留言提交
    LEAVE_MSG_REPLIED, // 留言回复
    //
    CUSTOMER_SUBMIT, // 客户留资提交
    SYSTEM_ALARM, // 系统报警
    // 
    TICKET, // 工单消息
    TICKET_SUBMIT, // 工单提交
    // 
    TYPING, // 正在输入
    PROCESSING, // 正在处理，等待大模型回复中
    // 
    STICKER, // 贴纸
    EMAIL, // 邮件
    // 
    BUTTON, // 按钮消息
    BUTTON_SUBMIT, // 按钮提交
    // 
    PREVIEW, // 消息预知
    RECALL, // 撤回
    DELIVERED, // 回执: 已送达
    READ, // 回执: 已读
    QUOTATION, // 引用消息
    KICKOFF, // kickoff other clients
    SHAKE, // shake window
    FAQ, // 常见问题
    FAQ_QUESTION, // 常见问题问题
    FAQ_ANSWER, // 常见问题答案
    //
    ROBOT, // 机器人
    ROBOT_QUESTION, // 机器人问题
    ROBOT_ANSWER, // 机器人答案
    STREAM, // 流式消息TEXT，大模型回复
    STREAM_START, // 流式消息开始
    STREAM_END, // 流式消息结束
    // ROBOT_UP, // 机器人答案评价:UP
    // ROBOT_DOWN, // 机器人答案评价:DOWN
    ARTICLE, // 文章
    // 
    RATE, // 访客主动评价
    RATE_INVITE, // 客服邀请评价
    RATE_SUBMIT, // 访客提交评价
    RATE_CANCEL, // 访客取消评价
    // 
    AUTO_CLOSED, // 自动关闭
    AGENT_CLOSED, // 客服关闭
    //     
    // 
    TRANSFER,
    TRANSFER_REJECT,
    TRANSFER_ACCEPT,
    TRANSFER_TIMEOUT,
    TRANSFER_CANCEL,
    // 
    INVITE,
    INVITE_REJECT,
    INVITE_ACCEPT,
    INVITE_TIMEOUT,
    INVITE_CANCEL,
    INVITE_VISITOR,
    INVITE_VISITOR_REJECT,
    INVITE_VISITOR_ACCEPT,
    INVITE_VISITOR_TIMEOUT,
    INVITE_VISITOR_CANCEL,
    INVITE_GROUP,
    INVITE_GROUP_REJECT,
    INVITE_GROUP_ACCEPT,
    INVITE_GROUP_TIMEOUT,
    INVITE_GROUP_CANCEL,
    INVITE_KBASE,
    INVITE_KBASE_REJECT,
    INVITE_KBASE_ACCEPT,
    INVITE_KBASE_TIMEOUT,
    INVITE_KBASE_CANCEL,
    INVITE_ORGANIZATION,
    INVITE_ORGANIZATION_REJECT,
    INVITE_ORGANIZATION_ACCEPT,
    INVITE_ORGANIZATION_TIMEOUT,
    INVITE_ORGANIZATION_CANCEL,
    // 
    WEBRTC_AUDIO_INVITE,
    WEBRTC_VIDEO_INVITE,
    WEBRTC_AUDIO_INVITE_REJECT,
    WEBRTC_VIDEO_INVITE_REJECT,
    WEBRTC_AUDIO_INVITE_ACCEPT,
    WEBRTC_VIDEO_INVITE_ACCEPT,
    WEBRTC_AUDIO_INVITE_CANCEL,
    WEBRTC_VIDEO_INVITE_CANCEL,
    GROUP_CREATE,
    GROUP_INVITE,
    GROUP_DISMISS,
    NOTIFICATION_AGENT_REPLY_TIMEOUT, // 客服回复超时提醒
    NOTIFICATION_RATE_SUBMITTED, // 访客评价提交提醒
    ERROR,
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
                || MessageTypeEnum.CARD == type
                || MessageTypeEnum.QUOTATION == type;
    }

}