/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:33:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 17:30:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

public enum MessageStatusEnum {
    SENDING, // 发送中
    TIMEOUT, // 超时
    BLOCKED, // in black list
    STRANGER, // not friend
    ERROR, // 发送错误
    SUCCESS, // 发送成功
    RECALL, // 撤回
    DELIVERED, // 送达
    READ, // 已读
    DESTROYED, // destroyed after read
    UNPROCESSED, // leave message unprocessed
    PROCESSED, // leave message processed
    LEAVE_MSG_SUBMIT, // 提交留言
    LEAVE_MSG_REPLIED, // 留言已回复
    RATE_SUBMIT, // 提交会话评价
    RATE_CANCEL, // 取消会话评价
    RATE_UP, // 评价消息up
    RATE_DOWN, // 评价消息down
    RATE_FEEDBACK, // 评价消息feedback
    TRANSFER_PENDING, // 转接待处理
    TRANSFER_ACCEPTED, // 接受转接
    TRANSFER_REJECTED, // 拒绝转接
    TRANSFER_TIMEOUT, // 转接超时
    TRANSFER_CANCELED, // 取消转接
    INVITE_PENDING, // 邀请处理
    INVITE_ACCEPTED, // 接受邀请
    INVITE_REJECTED, // 拒绝邀请
    INVITE_TIMEOUT, // 邀请超时
    INVITE_CANCELED, // 取消邀请
    INVITE_EXITED, // 邀请退出
    INVITE_VISITOR_PENDING, // 邀请访客处理
    INVITE_VISITOR_ACCEPTED, // 接受访客邀请
    INVITE_VISITOR_REJECTED, // 拒绝访客邀请
    INVITE_VISITOR_TIMEOUT, // 访客邀请超时
    INVITE_VISITOR_CANCELED, // 取消访客邀请
    INVITE_GROUP_ACCEPTED, // 接受群邀请
    INVITE_GROUP_REJECTED, // 拒绝群邀请
    INVITE_GROUP_TIMEOUT, // 群邀请超时
    INVITE_GROUP_CANCELED, // 取消群邀请
    INVITE_KBASE_PENDING, // 邀请知识库处理
    INVITE_KBASE_ACCEPTED, // 接受知识库邀请
    INVITE_KBASE_REJECTED, // 拒绝知识库邀请
    INVITE_KBASE_TIMEOUT, // 知识库邀请超时
    INVITE_KBASE_CANCELED, // 取消知识库邀请
    INVITE_ORGANIZATION_PENDING, // 邀请组织处理
    INVITE_ORGANIZATION_ACCEPTED, // 接受组织邀请
    INVITE_ORGANIZATION_REJECTED, // 拒绝组织邀请
    INVITE_ORGANIZATION_TIMEOUT, // 组织邀请超时
    INVITE_ORGANIZATION_CANCELED, // 取消组织邀请
    ;

    // 根据字符串查找对应的枚举常量
    public static MessageStatusEnum fromValue(String value) {
        for (MessageStatusEnum type : MessageStatusEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No MessageStatusEnum constant with value: " + value);
    }
}
