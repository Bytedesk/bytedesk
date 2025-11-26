/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 21:50:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 08:55:22
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
    WELCOME, // 欢迎消息
    CONTINUE, // 继续会话
    SYSTEM, // 系统消息
    NOTICE, // 通知消息
    TEXT, // 文本消息
    IMAGE, // 图片消息
    FILE, // 文件消息
    DOCUMENT, // 文档消息
    AUDIO, // 音频消息
    VOICE, // 语音消息
    VIDEO, // 视频消息
    MUSIC, // 音乐消息
    LOCATION, // 位置消息
    LINK, // 链接消息
    GOODS, // 商品消息
    CARD, // 名片消息
    EVENT, // 事件：离线、上线等
    EXTRA, // 附加信息
    GUESS, // 猜你想问
    HOT, // 热门话题
    SHORTCUT, // 快捷路径
    ORDER, // 订单
    // 
    QUEUE, // 排队消息: 用户进入排队队列
    QUEUE_NOTICE, // 排队通知消息: 通知客服，用户进入排队队列，通知客服人员
    QUEUE_UPDATE, // 排队更新消息: 通知访客，用户排队位置或预计等待时间更新
    QUEUE_ACCEPT, // 排队接受消息：通知客服，用户已经被其他客服接入
    QUEUE_TIMEOUT, // 排队超时消息：用户排队超时未接入客服
    QUEUE_CANCEL, // 排队取消消息：用户取消排队或被移出排队队列
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
    ROBOT, // 机器人消息
    ROBOT_CANCEL, // 机器人回答取消
    ROBOT_UNANSWERED, // 机器人未回答
    ROBOT_ERROR, // 机器人错误消息
    // 
    ROBOT_STREAM, // 机器人流式响应（包含源引用）
    ROBOT_STREAM_START, // 机器人流式响应开始
    ROBOT_STREAM_END, // 机器人流式响应结束
    ROBOT_STREAM_CANCEL, // 机器人流式响应取消
    ROBOT_STREAM_UNANSWERED, // 机器人流式响应未回答
    ROBOT_STREAM_ERROR, // 机器人流式响应错误
    // 
    ROBOT_UP, // 点赞: 赞同机器人回答
    ROBOT_DOWN, // 点踩: 反对机器人回答
    // ROBOT_QUESTION, // 机器人问题：用户提问直接使用 TEXT/IMAGE/VIDEO 等消息类型
    // ROBOT_ANSWER, // 机器人答案：非流式回答，直接使用 ROBOT 消息类型
    // 
    ARTICLE, // 文章
    // 
    RATE_INVITE, // 邀请评价
    RATE, // 访客主动评价
    RATE_SUBMIT, // 访客提交评价
    RATE_CANCEL, // 访客取消评价
    // 
    AUTO_CLOSED, // 自动关闭
    AGENT_CLOSED, // 客服关闭
    // 
    TRANSFER, // 会话转接
    TRANSFER_REJECT, // 转接拒绝
    TRANSFER_ACCEPT, // 转接接受
    TRANSFER_TIMEOUT, // 转接超时
    TRANSFER_CANCEL, // 转接取消    
    // 
    INVITE, // 会话邀请
    INVITE_REJECT, // 邀请拒绝
    INVITE_ACCEPT, // 邀请接受
    INVITE_TIMEOUT, // 邀请超时
    INVITE_CANCEL, // 邀请取消
    INVITE_EXIT, // 邀请退出
    INVITE_REMOVE, // 邀请删除
    // 
    INVITE_VISITOR,
    INVITE_VISITOR_REJECT,
    INVITE_VISITOR_ACCEPT,
    INVITE_VISITOR_TIMEOUT,
    INVITE_VISITOR_CANCEL,
    // 
    INVITE_GROUP,
    INVITE_GROUP_REJECT,
    INVITE_GROUP_ACCEPT,
    INVITE_GROUP_TIMEOUT,
    INVITE_GROUP_CANCEL,
    // 
    INVITE_KBASE,
    INVITE_KBASE_REJECT,
    INVITE_KBASE_ACCEPT,
    INVITE_KBASE_TIMEOUT,
    INVITE_KBASE_CANCEL,
    // 
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
    // 
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